def runBuild(repo_dir){
  
  def img               = null
  def fields_list       = null
  def short_report      = null
  def docker_repository = "admssa/diag"
  def local_registry    = "docker-host:65534"
  def tag               = env.TAG_NAME
  def msg_title         = "<${env.BUILD_URL}|${env.JOB_NAME}>"
  def slack_channel     = "#jenkins-automation"    
  def slack             = load "jenkinslib/slack.groovy"
  def io_operations     = load "jenkinslib/io_operations.groovy"  
        
  try {   
    def build_directory   = io_operations.getDir(tag, repo_dir)  
    slack.sendToSlack('STARTED', slack_channel, "Starting the job", msg_title)
    if (build_directory != null) {
        stage('Build & push locally') {  
            img = docker.build("${docker_repository}:${tag}", "-f ./${build_directory}/Dockerfile ./${build_directory}")
            docker.withRegistry("http://${local_registry}"){ 
              img.push()
            }
        }
        stage('Scan for vulnerabilities') {
            def anchore_script  = load "jenkinslib/anchore.groovy"
            def iamge_name      = "${local_registry}/${docker_repository}:${tag}"
            def engine_url      = "http://docker-host:8228/v1"
            def anchore_timeout = '3600'
            if ('jupyter' in tag) {
                anchore_timeout = '10800'
            }
            writeFile file: 'anchore_images', text: iamge_name
            anchore bailOnFail: false, autoSubscribeTagUpdates: false, engineCredentialsId: 'anchore_admin', engineurl: engine_url, engineRetries: anchore_timeout, forceAnalyze: true, name: 'anchore_images'
            echo "Preparing reports before getting status of the check"
            withCredentials([usernamePassword(credentialsId: 'anchore_admin', usernameVariable: 'ANCHORE_CLI_USER', passwordVariable: 'ANCHORE_CLI_PASS')]) {
                short_report = anchore_script.generatePlainReport(iamge_name, engine_url) 
            }
            println short_report         
            if (short_report == null || short_report.status != 'pass'){
                currentBuild.result = 'FAILURE'
                error("Anchore: Image didn't pass the check")
            }
        }

        stage('Push to the dockerhub'){ 
            docker.withRegistry('', 'admssa_dockerhub') { 
                img.push()
                img.push("${build_directory}-latest")
            }
        }
        stage('Removing from the local registry'){
            println "Removing image manifest from the local registry"
            def registry = load "jenkinslib/registry.groovy"
            if( registry.deleteByTag(local_registry, docker_repository, tag) == false ){
                currentBuild.result = 'UNSTABLE'
            }
        }
        stage('Remove images') {
            sh "docker rmi ${docker_repository}:${tag} || true"
            sh "docker rmi ${docker_repository}:${build_directory}-latest || true"
            sh "docker rmi ${local_registry}/${docker_repository}:${tag} || true"
            sh "docker rmi ${local_registry}/${docker_repository}:${build_directory}-latest || true"
        }
    }
    }
    catch (e) {
        echo "Pipeline failed: ${e}"
        currentBuild.result = 'FAILURE'
        slack.sendSlackError(slack_channel, "Exception ${e} while running build: ${env.BUILD_URL}console", msg_title)
    }
    finally {
        sh 'docker rmi -f $(docker images -f "dangling=true" -q)  || true'
        def currentResult = currentBuild.result ?: 'SUCCESS'
        slack.sendToSlack(currentResult, slack_channel, "For details see Ancore <${env.BUILD_URL}anchore-results/|report> or full job <${env.BUILD_URL}console/|output.>", msg_title, short_report)
    }
}

return this
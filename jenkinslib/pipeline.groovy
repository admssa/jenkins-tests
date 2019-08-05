def runBuild(repo_dir){
  try {   
        def msg_title         = "Datalabs images build"
        def img               = null
        def docker_repository = "admssa/diag"
        def local_registry    = "docker-host:65534"
        def tag               = env.TAG_NAME
        def io_operations     = load "jenkinslib/io_operations.groovy"
        def slack             = load "jenkinslib/slack.gtoovy"
        def slack_channel     = "#jenkins-automation"
        def build_directory   = io_operations.getDir(tag, repo_dir)

    slack.sendToSlack('STARTED', "${env.JOB_NAME}", msg_title)

    if (build_directory != null) {
        stage('Build & push locally') {  
            img = docker.build("${docker_repository}:${tag}", "-f ./${build_directory}/Dockerfile ./${build_directory}")
            docker.withRegistry("http://${local_registry}"){ 
              img.push()
            }
        }
        stage('Scan for vulnerabilities') {
            def iamge_name      = "${local_registry}/${docker_repository}:${tag}"
            def anchore_timeout = '7200'

            writeFile file: 'anchore_images', text: iamge_name
            anchore autoSubscribeTagUpdates: false, engineCredentialsId: 'anchore_admin', engineurl: 'http://docker-host:8228/v1', engineRetries: anchore_timeout, forceAnalyze: true, name: 'anchore_images'
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
    }

    slack.sendToSlack(currentBuild.result, slack_channel, "${env.JOB_NAME}.\nJobURL:${env.BUILD_URL}.", msg_title)
    
    

}

return this
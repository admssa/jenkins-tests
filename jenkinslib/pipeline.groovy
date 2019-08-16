def runBuild(repo_dir, docker_registry, multibuild_opts, dockerhub_creds){
    def tag               = env.TAG_NAME
    def local_registry    = "docker-host:65534"
    def msg_title         = "<${env.BUILD_URL}|${env.JOB_NAME}>"
    def slack_channel     = "#jenkins-automation"
    def engine_url        = "http://docker-host:8228/v1"    
    def slack             = load "jenkinslib/slack.groovy"
    def images            = []
    def reports           = []        

    def skip_check = false
    def skip_push  = false
    def tag_msg    = gitTagMessage(tag)
    if (tag_msg != null) {
        skip_check = tag_msg.contains('nocheck')
        skip_push  = tag_msg.contains('nopush')   
    }
    try {
        slack.sendToSlack('STARTED', slack_channel, "Starting the job", msg_title)
        stage('Build images') {
            for (image in multibuild_opts) {
                images.add(docker.build(image.name, image.options))
            }
        } 
        if (!skip_check){
            stage('Push to the local registry'){
                docker.withRegistry("http://${local_registry}"){ 
                    for (img in images) {
                        img.push()
                    }
                }                
            } 
            stage('Scan for vulnerabilities') {  
                def anchore_timeout = '7200'
                def string_images   = ''     
                for (img in images){
                    def image_name = String.format("%s/%s", local_registry, img.imageName())
                    string_images = string_images.concat(image_name.concat('\n'))
                }
                writeFile file: 'anchore_images', text: string_images.trim()
                anchore bailOnFail: false, 
                        autoSubscribeTagUpdates: false, 
                        engineCredentialsId: 'anchore_admin', 
                        engineurl: engine_url, 
                        engineRetries: anchore_timeout, 
                        forceAnalyze: true, 
                        name: 'anchore_images'                
            }
            stage('Prepare short reports'){
                def anchore_script  = load "jenkinslib/anchore.groovy"
                withCredentials([usernamePassword(credentialsId: 'anchore_admin', 
                                                  usernameVariable: 'ANCHORE_CLI_USER', 
                                                  passwordVariable: 'ANCHORE_CLI_PASS')]) {
                    for (img in images){
                        def image_name = String.format("%s/%s", local_registry, img.imageName())
                        def short_report = anchore_script.generatePlainReport(image_name, engine_url)
                        reports.add(short_report)
                        if (short_report == null || short_report.anchore_check != 'pass'){
                            currentBuild.result = 'UNSTABLE'
                        }                          
                    }                    
                }      
            }
            stage('Removing from the local registry'){
                def registry = load "jenkinslib/registry.groovy"
                for (img in images) {
                    def splited_name = img.imageName().split(':')
                    def img_removed = registry.deleteByTag(local_registry, splited_name[0], splited_name[1])
                    if (!img_removed){
                        currentBuild.result = 'UNSTABLE'
                    }
                }                            
            }    
        }
        if (!skip_push){
            stage('Push to the dockerhub'){
                docker.withRegistry('', dockerhub_creds) {            
                    for (img in images) {
                        img.push()
                    }
                }
            }   
        }
        stage('Removing images'){
            for (img in images){
                def image_name = img.imageName()
                sh "docker rmi ${image_name} || true"
                sh "docker rmi ${local_registry}/${image_name} || true"
            }
        }                           
    }
    catch (e) {
        echo "Pipeline failed: ${e}"
        currentBuild.result = 'FAILURE'
        slack.sendSlackError(slack_channel, "Exception ${e} while running build: ${env.BUILD_URL}console", msg_title, null)    
    }    
    finally {
        sh 'docker rmi $(docker images -f "dangling=true" -q)  || true'
        def currentResult = currentBuild.result ?: 'SUCCESS'
        def message = "For details see Anchore <${env.BUILD_URL}anchore-results/|report> or full job <${env.BUILD_URL}console|output.>"        
        slack.sendToSlack(currentResult, slack_channel, message, msg_title, reports)
    }
}


def gitTagMessage(tag) {
    msg = sh(script: "git tag -n10000 -l ${tag}", returnStdout: true)?.trim()
    if (msg) {
        return msg.substring(tag.size()+1, msg.size()).trim()
    }
    return null
}

return this
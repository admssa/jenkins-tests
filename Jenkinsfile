@Library('jenkinslib')_

node {
    checkout scm
    def docker_registry   = "admssa/diag"
    def dockerhub_creds   = "admssa_dockerhub"
    def slack_channel     = "#automation_hooks"
    def multibuild_opts   = []
    def code_version      = sh(script: "cat ./CODE_VERSION", returnStdout: true)?.trim()


    if (env.TAG_NAME.contains('tag0')) {
        multibuild_opts = [
            [name: "${docker_registry}:${env.TAG_NAME}",
             options: "-f tag0/Dockerfile tag0/." ],
            [name: "${docker_registry}:${env.TAG_NAME}-missed",
             options: "-f tag0-missed/Dockerfile tag0-missed/." ],
            [name: "${docker_registry}:${env.TAG_NAME}-test",
             options: "-f tag0-test/Dockerfile tag0-test/." ],
        ] 

    }
    else if(env.TAG_NAME.contains('tag1')) {
        multibuild_opts = [ 
            [name: "${docker_registry}:${env.TAG_NAME}",
             options: "-f tag1/Dockerfile tag1/." ] ]
    }
    else if(env.TAG_NAME.contains('tag-elastic')){
        multibuild_opts = [ 
            [name: "${docker_registry}:${env.TAG_NAME}",
             options: "-f tag-elastic/Dockerfile tag-elastic/." ] ]
    }
    if (multibuild_opts.size() > 0){
        test.megatest()
        pipeline.runBuild(docker_registry, multibuild_opts, dockerhub_creds, slack_channel)  
    }
    else {
        println "Nothing to do here..."
    }
    


} 
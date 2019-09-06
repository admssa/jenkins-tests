@Library('jenkinslib')_

node {
    checkout scm
    def docker_registry   = "admssa/diag"
    def dockerhub_creds   = "admssa_dockerhub"
    def slack_channel     = "#automation_hooks"
    def multibuild_opts   = []
    def build_directory   = io_operations.getDir(env.TAG_NAME, pwd())  

    if (build_directory != null) {
        def options = "-f ./${build_directory}/Dockerfile ./${build_directory}"
        if (build_directory.contains("jupyter-")) {
            options = options + " --target ${build_directory}"
        }        
        multibuild_opts =[[name: "${docker_registry}:${env.TAG_NAME}", options: options]]
    }

    if (multibuild_opts.size() > 0){
        pipeline_general.runBuild(docker_registry, multibuild_opts, dockerhub_creds, slack_channel)  
    }
    else {
        println "Nothing to do here..."
    }
}
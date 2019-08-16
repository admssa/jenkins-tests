node {
    checkout scm
    def pipeline          = load "jenkinslib/pipeline.groovy"
    def docker_registry   = "admssa/diag"
    def dockerhub_creds   = "admssa_dockerhub"
    def multibuild_opts   = []
    def code_version      = sh(script: "cat ${repo_dir}/CODE_VERSION", returnStdout: true)?.trim()


    if (env.TAG_NAME.consist('tag0')) {
        multibuild_opts = [
            [name: "${docker_registry}:${env.TAG_NAME}",
             options: "-f tag0/Doclerfile ./tag1/." ],
            [name: "${docker_registry}:${env.TAG_NAME}-missed",
             options: "-f tag0-missed/Dockerfile tag0-midded/." ],
            [name: "${docker_registry}:${env.TAG_NAME}-test",
             options: "-f tag0-test/Dockerfile tag0-test/." ],
        ] 

    }
    else if(env.TAG_NAME.consist('tag1')) {
        multibuild_opts = [ 
            [name: "${docker_registry}:${env.TAG_NAME}",
             options: "-f tag1/Doclerfile ./tag1/." ] ]
    }
    if (multibuild_opts > 0){
        pipeline.runBuild(pwd(), docker_registry, multibuild_opts, dockerhub_creds)  
    }
    else {
        println "Nothing to do here..."
    }
    


} 
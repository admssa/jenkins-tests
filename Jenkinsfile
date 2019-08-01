node {
    try {
        stage('Git checkout current tag'){
            checkout scm
        }
        def img = null
        def docker_repo = "admssa/diag"
        def local_registry = "docker-host:65534"
        def tag = env.TAG_NAME
        def io_op = load "jenkinslib/io_operations.groovy"
        def build_dir = io_op.getDir(tag, pwd())

        stage('Build & push locally') {  
            if (build_dir != null) {
                img = docker.build("${docker_repo}:${tag}", "-f ./${build_dir}/Dockerfile ./${build_dir}")
                docker.withRegistry("http://${local_registry}"){ 
                    img.push()
                }
            }
        }
        stage('Scan for vulnerabilities') {
            def iamge_name = "${local_registry}/${docker_repo}:${tag}"
            writeFile file: 'anchore_images', text: iamge_name
            anchore autoSubscribeTagUpdates: false, engineCredentialsId: 'anchore_admin', engineurl: 'http://docker-host:8228/v1', forceAnalyze: true, name: 'anchore_images'
        }
        stage('Push to the dockerhub'){ 
            if (build_dir != null) {
                docker.withRegistry('', 'admssa_dockerhub') { 
                    img.push()
                    img.push("${build_dir}-latest")
                }
             }
        }
        stage('Remove images') {
            sh "docker rmi -f ${docker_repo}:${tag} || true"
            sh "docker rmi -f ${docker_repo}:${build_dir}-latest || true"
        }

    }
    catch (e) {
        echo "Pipeline failed: ${e}"
        currentBuild.result = 'FAILURE'
    }
    finally {
        sh 'docker rmi -f $(docker images -f "dangling=true" -q)  || true'
    }

} 
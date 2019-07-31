node {
    try {
        def img = null
        def io_op = load "jenkinslib/io_operations.groovy"
        def docker_repo = "admssa/diag"
        def tag = env.TAG_NAME

        stage('Git checkout current tag'){
            checkout scm
        }

        def build_dir = io_op.getDir(tag, pwd())

        docker.withRegistry('', 'admssa_dockerhub') {
            stage('Build & push') {
                if (build_dir != null) {
                    img = docker.build("${docker_repo}:${tag}", "-f ./${build_dir}/Dockerfile ./${build_dir}")
                    img.push()
                }
            }

            stage('Scan for vulnerabilities') {
                def iamge_name = "${docker_repo}:${tag}"
                writeFile file: 'anchore_images', text: iamge_name
                anchore autoSubscribeTagUpdates: false, engineCredentialsId: 'anchore_admin', engineurl: 'http://docker-host:8228/v1', forceAnalyze: true, name: 'anchore_images'
            }

            stage('Push latest tag'){
                echo sh(script: 'env|sort', returnStdout: true)
                if (build_dir != null) {
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
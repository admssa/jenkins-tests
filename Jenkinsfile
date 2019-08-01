node {
    try {
        stage('Git checkout current tag'){
            checkout scm
        }
        def img               = null
        def docker_repository = "admssa/diag"
        def local_registry    = "docker-host:65534"
        def tag               = env.TAG_NAME
        def io_operations     = load "jenkinslib/io_operations.groovy"
        def build_directory   = io_operations.getDir(tag, pwd())

        stage('Build & push locally') {  
            if (build_directory != null) {
                img = docker.build("${docker_repository}:${tag}", "-f ./${build_directory}/Dockerfile ./${build_directory}")
                docker.withRegistry("http://${local_registry}"){ 
                    img.push()
                }
            }
        }
        stage('Scan for vulnerabilities') {
            def iamge_name = "${local_registry}/${docker_repository}:${tag}"
            def anchore_timeout = 300
            if ('notebook' in tag) {
                anchore_timeout = 3600
            }
            writeFile file: 'anchore_images', text: iamge_name
            anchore engineRetries: anchore_timeout, autoSubscribeTagUpdates: false, engineCredentialsId: 'anchore_admin', engineurl: 'http://docker-host:8228/v1', forceAnalyze: true, name: 'anchore_images'
        }
        stage('Push to the dockerhub'){ 
            if (build_directory != null) {
                docker.withRegistry('', 'admssa_dockerhub') { 
                    img.push()
                    img.push("${build_directory}-latest")
                }
             }
        }
        stage('Remove images') {
            sh "docker rmi -f ${docker_repository}:${tag} || true"
            sh "docker rmi -f ${docker_repository}:${build_directory}-latest || true"
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
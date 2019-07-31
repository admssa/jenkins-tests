node {
    stage('Git checkout current tag'){
        checkout scm
    }

    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    def docker_repo = "admssa/diag"
    def io_op = load "jenkinslib/io_operations.groovy"
    def build_dir = io_op.getdir(tag, pwd())
    def img = null

 docker.withRegistry('', 'admssa_dockerhub') {
    stage('Build & push') {
          println build_dir
          if (build_dir != null) {
            img = docker.build("${docker_repo}:${tag}", "-f ./${build_dir}/Dockerfile ./${build_dir}")  
            img.push()
            println img            
          }
        }
    }

    stage('Push latest tag'){
        if (build_dir != null) {
            img.push("${build_dir}-latest")
        }
    }

    stage('Remove images') {
        sh "docker rmi -f ${docker_repo}:${tag} || true"
        sh "docker rmi -f ${docker_repo}:${build_dir}-latest || true"
    }

} 
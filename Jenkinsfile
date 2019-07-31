node {
    stage('Git checkout current tag'){
        checkout scm
    }

    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    def docker_repo = "admssa/diag"
    def io_op = load "jenkinslib/io_operations.groovy"
    def current_dir = pwd()
    def build_dir = io_op.getdir(tag, current_dir)

 docker.withRegistry('https://registry.example.com', 'credentials-id') {
    stage('Build') {
          println build_dir
          if (build_dir != null) {
            def img = docker.build("${docker_repo}:${tag}", "-f Dockerfile ./${build_dir}")  
            ehco img            
          }
        }
    }
}

} 
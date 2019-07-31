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
    
    stage('Build') {
          println build_dir
        script {
          if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            echo build_output
          }
        }
    }

} 
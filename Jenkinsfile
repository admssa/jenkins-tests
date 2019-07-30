node {
    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    stage('Git checkout current tag'){
        checkout scm
    }

    stage('Build') {
          def io_op = load "jenkinslib/io_operations.groovy"
          def current_dir = pwd()
          def build_dir = io_op.getdir(tag, current_dir)
          println current_dir
          println build_dir
        
          if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            println build_output
          }
    }

} 
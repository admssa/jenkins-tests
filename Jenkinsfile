#!groovy
node {
    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    stage('Git checkout current tag'){
        checkout scm
    }

    stage('Build') {
          def io_op = load "jenkinslib/io_operations.groovy"
          def build_dir = io_op.getdir(tag)
          println build_dir
          def lsla = script sh: "ls -la", returnStdout: true
          println lsla
          if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            println build_output
          }
    }


}
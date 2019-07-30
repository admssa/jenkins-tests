#!groovy
node {

    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    def io_op = load "jenkinslib/io_operations.groovy"
    def build_dir = io_op.getdir(tag)

    stage('Git checkout current tag'){
        checkout scm
    }


    stage('Build') {
          println build_dir
          if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            println build_output
          }
    }


}
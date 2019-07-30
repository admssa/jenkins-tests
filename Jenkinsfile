#!groovy
node {
    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    stage('Git checkout current tag'){
        checkout scm
    }

    stage('Build') {

          build_dir = getdir(tag)
          println build_dir
          def lsla = script sh: "ls -la", returnStdout: true
          println lsla
          if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            println build_output
          }
    }

    def listdirs(dir) {
        def currentDir = new File(dir)
        def dirs = []
        currentDir.eachFile FileType.DIRECTORIES, {
            dirs << it.name
        }
        return dirs
    }

  
    def getdir(tag){
        dirs = listdirs("../")  
        double_tag = tag.tokenize("-")[0] + "-" + tag.tokenize("-")[1]
        single_tag = tag.tokenize("-")[0]
        if (double_tag in dirs) {
            println "Got double"
            return double_tag
        }
        else if(single_tag in dirs) {
            println "Got single"
            return single_tag
        }
        else {
            return null
        }
    }


}
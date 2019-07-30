#!groovy
import groovy.io.*
node {
    def tag = env.TAG_NAME
    def tag_date = env.TAG_DATE
    stage('Git checkout current tag'){
        checkout scm
    }

    stage('Build') {

        def current_dir =new File(".")
        def dirs = []
        current_dir.eachFile FileType.DIRECTORIES, {
            dirs << it.name
        }
        def build_dir
        double_tag = tag.tokenize("-")[0] + "-" + tag.tokenize("-")[1]
        single_tag = tag.tokenize("-")[0]
        if (double_tag in dirs) {
            println "Got double"
            build_dir = double_tag
        }
        else if(single_tag in dirs) {
            println "Got single"
            build_dir = single_tag
        }
        else {
             build_dir = null
        }

        println build_dir
        def lsla = script sh: "ls -la", returnStdout: true
        println lsla
        if (build_dir) {
            def build_output = script sh: "docker build -t -f ./${build_dir} ${docker_repo}:${tag}", returnStdout: true
            println build_output
        }
    }

}
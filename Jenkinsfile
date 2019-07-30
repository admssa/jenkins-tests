#!groovy
node {
    stage('Git checkout'){
        checkout scm
        echo sh(returnStdout: true, script: 'env')

    }

    stage('Build') {
                echo 'Building only'
                // def build = sh script: "cd ./tag0 && docker build -t admssa/diag:${tag}", returnStdout: true
                // echo build
    }
}
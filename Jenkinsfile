node {

    stage('Git tag'){
        echo tag
    }

    stage('Build') {
                echo 'Building only'
                def build = sh script: "cd tag0 && docker build -t admssa/diag:${tag}", returnStdout: true
                echo build
    }
}
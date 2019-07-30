node {

    stage('Git tag'){
        def test_tag = sh(returnStdout: true, script: "git tag --sort version:refname | tail -1").trim()
        echo test_tag
    }

    stage('Build') {
             steps {
                echo 'Building only'
                def build = sh script: "cd tag0 && docker build -t ${tag}", returnStdout: true
                echo build
        }
    }
}
node {
    stage('Git Checkout') {
        echo "repository host: ${repositoryHost}" // github.com
        echo "repository path: ${repositoryPath}" // <user>/<repository>.git
        echo "repository jenkins credentials id: ${credentialsId}"  // jenkins credentials for the jenkins git account who have commit access
        echo "repository branch: ${branch}" // master or another branch
        echo "repository commiter username: ${repositoryCommiterUsername}" // Jenkins account email 
        echo "repository commiter name: ${repositoryCommiterEmail}" // Jenkins
        
        repositoryUrl = "${repositoryHost}${repositoryAccessSeparator}${repositoryPath}"
        repositoryUrlFull = "${repositoryAccess}${repositoryUrl}"
        echo "repository url: ${repositoryUrl}" // github.com/<user>/<repository>.git
        echo "repository url full: ${repositoryUrlFull}" // https://github.com/<user>/<repository>.git
    }
    stage('Git tag'){
        def test_tag = sh(returnStdout: true, script: "git tag --sort version:refname | tail -1").trim()
        echo test_tag
    }

}
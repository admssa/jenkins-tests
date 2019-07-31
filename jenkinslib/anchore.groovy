#!groovy

def runTest(image_name, admin_password, engine_url){
    def testExitCode = 1
    stage('Add image into the engine'){
        sh "anchore-cli --u admin --p ${admin_password} --url ${engine_url} image add ${image_name}"
    }
    stage('Check if image scaned') {

    }
}
node {
    
    checkout scm
    def pipeline         = load "jenkinslib/pipeline.groovy"
    def slack         = load "jenkinslib/test.groovy"
    // pipeline.runBuild(pwd())
    //slack.testSlack()
    echo env.TAG_MESSAGE

    echo sh(returnStdout: true, script: 'env')

    msg = sh(script: "git tag -n10000 -l ${env.TAG_NAME}", returnStdout: true)?.trim()
    println msg.substring(env.TAG_NAME.size()+1, msg.size())

} 
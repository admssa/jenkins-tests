node {
    
    checkout scm
    def pipeline         = load "jenkinslib/pipeline.groovy"
    def slack         = load "jenkinslib/test.groovy"
    // pipeline.runBuild(pwd())
    //slack.testSlack()

    echo sh(returnStdout: true, script: 'env')

    msg = sh(script: "git tag -n10000 -l ${env.TAG}", returnStdout: true)?.trim()
    println msg

} 
node {
    
    checkout scm
    def pipeline         = load "jenkinslib/pipeline.groovy"
    def slack         = load "jenkinslib/test.groovy"
    pipeline.runBuild(pwd())
    //slack.testSlack()

} 
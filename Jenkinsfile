node {
    
    checkout scm
    def pipeline         = load "jenkinslib/pipeline.groovy"
    def slack            = load "jenkinslib/test.groovy"
    // def tag              = env.TAG_NAME
    // def tag_with_msg     = sh(script: "git tag -n10000 -l ${env.TAG_NAME}", returnStdout: true)?.trim()
    // def tag_msg          = tag_with_msg.substring(tag.size()+1, msg.size()).trim()

    pipeline.runBuild(pwd())
    //slack.testSlack()

    echo rag_msg

    


} 
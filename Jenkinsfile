node {
    checkout scm
    def msg_title         = "Datalabs images build"
    def slack_channel     = "#jenkins-automation"    
    def slack             = load "jenkinslib/slack.groovy"
    def pipeline          = load "jenkinslib/pipeline.groovy"


    slack.sendToSlack('STARTED', slack_channel, "Starting build job: ${env.JOB_NAME}", msg_title)
    
    def image = pipeline.runBuild(pwd(),slack, slack_channel, msg_title)

    slack.sendToSlack(currentBuild.result, slack_channel, image, msg_title)
    
} 
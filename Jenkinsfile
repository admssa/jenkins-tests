node {
    checkout scm
    def msg_title         = "Datalabs images build"
    def slack             = load "jenkinslib/slack.groovy"
    def slack_channel     = "#jenkins-automation"
    def pipeline          = load "jenkinslib/pipeline.groovy"


    slack.sendToSlack('STARTED', slack_channel, "Starting build job: ${env.JOB_NAME}", msg_title)
    
    pipeline.runBuild(pwd(),slack, slack_channel, msg_title)

    post {
        slack.sendToSlack(currentBuild.result, "No additional output", msg_title)
    }
} 
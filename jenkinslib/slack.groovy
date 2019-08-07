#!groovy
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

def sendSlackFields(slack_channel, msg, title, color, field_list) {
    JSONObject attachment = new JSONObject();
    attachment.put('text', msg.toString());
    attachment.put('title', title.toString());
    attachment.put('color', color.toString());
    attachment.put('mrkdwn_in', ['fields']);
    if (field_list != null && field_list.size() > 0) {
      attachment.put('fields', field_list);
    }
    JSONArray attachments = new JSONArray();
    attachments.add(attachment);
    slackSend color: color,
        channel: slack_channel,
        attachments: attachments.toString()
}

def sendSlackNotification(slack_channel, msg, title, color, fields=null) {
    sendSlackFields(slack_channel, msg, title, color, fields)
}

def sendSlackError(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'danger')
}

def sendSlackWarning(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'warning')
}

def sendSlackSuccess(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'good')
}

def sendSlackNetral(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, '#439FE0')
}

def sendToSlack(buildResult, slack_channel, msg, title, fields, short_report=null){
    def fields = null
    if (short_report != null){
        fields = []
    for ( e in short_report ) {
        fields.add(new JsonSlurper().parseText("""{"title": "${e.key}", "value": "${e.value}", "short":"false"}"""))
    }
    }

    default_message = "Job: ${env.JOB_NAME} was %s\n%s\nJobURL: ${env.BUILD_URL}"
    def full_message = null
    if (buildResult == 'SUCCESS'){
        full_message = String.format(default_message, "finished successfully", msg, fields)
        sendSlackSuccess(slack_channel, full_message, title)
    } else if (buildResult == 'UNSTABLE'){
        full_message = String.format(default_message, "unstable", msg, fields)
        sendSlackWarning(slack_channel, full_message, title)
    } else if (buildResult == 'FAILURE'){
        full_message = String.format(default_message, "failed", msg, fields)
        sendSlackError(slack_channel, full_message, title, fields)
    } 
    else{
        sendSlackNetral(slack_channel, msg, title, fields)
    }
}

return this
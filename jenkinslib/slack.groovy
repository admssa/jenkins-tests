#!groovy
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import groovy.json.JsonSlurperClassic;

def sendSlackFields(slack_channel, msg, title, color, field_list) {
    JSONObject attachment = new JSONObject();
    attachment.put('text', msg.toString());
    attachment.put('title', title.toString());
    attachment.put('color', color.toString());
    attachment.put('mrkdwn_in', ['fields']);
    if (field_list != null && field_list.size() > 0) {
      attachment.put('fields', field_list);
      println(field_list)
    }
    JSONArray attachments = new JSONArray();
    attachments.add(attachment);
    slackSend color: color,
        channel: slack_channel,
        attachments: attachments.toString()
}

def sendSlackNotification(slack_channel, msg, title, color, fields) {
    sendSlackFields(slack_channel, msg, title, color, fields)
}

def sendSlackError(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'danger', fields)
}

def sendSlackWarning(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'warning', fields)
}

def sendSlackSuccess(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'good', fields)
}

def sendSlackNetral(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, '#439FE0', fields)
}

def sendToSlack(buildResult, slack_channel, msg, title, short_report=null){
    def fields = null
    if (short_report != null){
        fields = []
        for ( e in short_report ) {
            fields.add(new JsonSlurperClassic().parseText("""{"title": "${e.key}", "value": "${e.value}", "short":"false"}"""))
     }
    }

    default_message = "Job: ${env.JOB_NAME} was %s\n%s\nJobURL: ${env.BUILD_URL}"
    def full_message = null
    if (buildResult == 'SUCCESS'){
        full_message = String.format(default_message, "finished successfully", msg)
        sendSlackSuccess(slack_channel, full_message, title,  fields)
    } else if (buildResult == 'UNSTABLE'){
        full_message = String.format(default_message, "unstable", msg)
        sendSlackWarning(slack_channel, full_message, title,  fields)
    } else if (buildResult == 'FAILURE'){
        full_message = String.format(default_message, "failed", msg)
        sendSlackError(slack_channel, full_message, title, fields)
    } 
    else{
        sendSlackNetral(slack_channel, msg, title, fields)
    }
}

return this
#!groovy
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

def sendSlackFields(slack_channel, msg, title, color, field_list) {
    JSONObject attachment = new JSONObject();
    attachment.put('text', msg.toString());
    attachment.put('title', title.toString());
    attachment.put('color', color.toString());
    attachment.put('footer', 'UTC: ' + getDateNow());
    attachment.put('mrkdwn_in', ['fields'])
    if (field_list != null && field_list.size() > 0) {
      attachment.put('fields', field_list);
    }
    JSONArray attachments = new JSONArray();
    attachments.add(attachment);
    slackSend color: color,
        channel: slack_channel,
        attachments: attachments.toString()
}

def sendSlackNotification(slack_channel, msg, title, color) {
    sendSlackFields(slack_channel, msg, title, color, null)
}

def sendSlackError(slack_channel, msg, title) {
    sendSlackNotification(slack_channel, msg, title, 'danger')
}

def sendSlackWarning(slack_channel, msg, title) {
    sendSlackNotification(slack_channel, msg, title, 'warning')
}

def sendSlackSuccess(slack_channel, msg, title) {
    sendSlackNotification(slack_channel, msg, title, 'good')
}

def sendSlackNetral(slack_channel, msg, title) {
    sendSlackNotification(slack_channel, msg, title, '#439FE0')
}

def sendToSlack(buildResult, slack_channel, msg, title){
    if (buildResult == 'SUCCESS'){
        sendSlackSuccess(slack_channel, msg, title)
    }
    else if (buildResult == 'UNSTABLE'){
        sendSlackWarning(slack_channel, msg, title)
    }
    else if (buildResult == 'FAILURE'){
        sendSlackError(slack_channel, msg, title)
    }
    else{
        sendSlackNetral(slack_channel, msg, title)
    }
}
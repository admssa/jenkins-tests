#!groovy
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import groovy.json.JsonSlurperClassic;

def sendSlackString(slack_channel, msg, title, color, reports){

    JSONObject attachment = new JSONObject()
    attachment.put('text', msg.toString())
    attachment.put('title', title.toString())
    attachment.put('color', color.toString())
    JSONArray attachments = new JSONArray()
    attachments.add(attachment);
    if (reports != null && reports.size() > 0){
        for (report in reports) {
            attachments.add(jsonToAttachment(report, color))
        }
    }
        
    slackSend color: color,
    channel: slack_channel,
    attachments: attachments.toString()
}

def jsonToAttachment(short_report, color){
    def msg = new String() 
    for (object in short_report){
        def line = new String() 
        if (object.value != null) {
            if (object.value instanceof net.sf.json.JSONObject){
                if (object.value.size() == 0){
                    line = "None"
                }
                else{
                    object.value.each {
                        line = line + it.key + ": " + it.value + "  "
                    }
                }

            }
            else {
                line = object.value.toString()
            }
            msg = msg + "\n*" + object.key.toString() + ":* _" + line + "_"
        }
    }
    JSONObject attachment = new JSONObject()
    attachment.put('text', msg.toString())
    attachment.put('color', color)
    
    return attachment
}

def sendSlackNotification(slack_channel, msg, title, color, reports) {
    sendSlackString(slack_channel, msg, title, color, reports)
}

def sendSlackError(slack_channel, msg, title, reports) {
    sendSlackNotification(slack_channel, msg, title, 'danger', reports)
}

def sendSlackWarning(slack_channel, msg, title, reports) {
    sendSlackNotification(slack_channel, msg, title, 'warning', reports)
}

def sendSlackSuccess(slack_channel, msg, title, reports) {
    sendSlackNotification(slack_channel, msg, title, 'good', reports)
}

def sendSlackNetral(slack_channel, msg, title, reports) {
    sendSlackNotification(slack_channel, msg, title, '#439FE0', reports)
}

def sendToSlack(buildResult, slack_channel, msg, title, reports=null){
    default_message = "Job was %s\n%s"
    def full_message = null
    if (buildResult == 'SUCCESS'){
        full_message = String.format(default_message, "finished successfully", msg)
        sendSlackSuccess(slack_channel, full_message, title,  reports)
    } else if (buildResult == 'UNSTABLE'){
        full_message = String.format(default_message, "unstable", msg)
        sendSlackWarning(slack_channel, full_message, title,  reports)
    } else if (buildResult == 'FAILURE'){
        full_message = String.format(default_message, "failed", msg)
        sendSlackError(slack_channel, full_message, title, reports)
    } 
    else{
        sendSlackNetral(slack_channel, msg, title, reports)
    }
}

#!groovy
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import groovy.json.JsonSlurperClassic;

def sendSlackString(slack_channel, msg, title, color, short_report){
    if (short_report != null ){
      msg = msg + "```"  
      for (object in short_report){
          if (object.value instanceof java.util.HashMap && object.value.size() > 0){
               object.value = object.value.toString().replace("[", "").replace("]", "")
            }
            msg = msg + "\n*" + object.key.toString() + ":* " + object.value.toString()
        }
      msg = msg + "```"  
    }
    JSONObject attachment = new JSONObject()
    attachment.put('text', msg.toString())
    attachment.put('title', title.toString())
    attachment.put('color', color.toString())
    JSONArray attachments = new JSONArray()
    attachments.add(attachment);

        
    slackSend color: color,
    channel: slack_channel,
    attachments: attachments.toString()
}

def jsonToStringMsg(){

}

def sendSlackNotification(slack_channel, msg, title, color, report) {
    sendSlackString(slack_channel, msg, title, color, report)
}

def sendSlackError(slack_channel, msg, title, report) {
    sendSlackNotification(slack_channel, msg, title, 'danger', report)
}

def sendSlackWarning(slack_channel, msg, title, report) {
    sendSlackNotification(slack_channel, msg, title, 'warning', report)
}

def sendSlackSuccess(slack_channel, msg, title, fields) {
    sendSlackNotification(slack_channel, msg, title, 'good', fields)
}

def sendSlackNetral(slack_channel, msg, title, report) {
    sendSlackNotification(slack_channel, msg, title, '#439FE0', report)
}

def sendToSlack(buildResult, slack_channel, msg, title, short_report=null){
    default_message = "Job was %s\n%s"
    def full_message = null
    if (buildResult == 'SUCCESS'){
        full_message = String.format(default_message, "finished successfully", msg)
        sendSlackSuccess(slack_channel, full_message, title,  short_report)
    } else if (buildResult == 'UNSTABLE'){
        full_message = String.format(default_message, "unstable", msg)
        sendSlackWarning(slack_channel, full_message, title,  short_report)
    } else if (buildResult == 'FAILURE'){
        full_message = String.format(default_message, "failed", msg)
        sendSlackError(slack_channel, full_message, title, short_report)
    } 
    else{
        sendSlackNetral(slack_channel, msg, title, short_report)
    }
}

return this
import groovy.json.JsonSlurperClassic;
def testSlack(){ 
    def slack                 = load "jenkinslib/slack.groovy"
    def jsonString = '{"status":"fail","image":"admssa/diag:tag0-001.2","os":{"Negligible":"0(0)","Unknown":"0(0)","Low":"6(6)","Medium":"18(18)","High":"0(0)"},"non-os":{"Negligible":"0(0)","Unknown":"0(0)","Low":"0(0)","Medium":"4(0)","High":"2(0)"}}'
    def json = new JsonSlurperClassic().parseText(jsonString)
    slack.sendToSlack('SUCCESS', '#jenkins-automation', "For details see Anchore <${env.BUILD_URL}anchore-results/|report> or full job <${env.BUILD_URL}console|output.>", "<${env.BUILD_URL}|${env.JOB_NAME}>", json)
}
return this   

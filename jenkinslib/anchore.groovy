#!groovy
import net.sf.json.JSONObject;

def generatePlainReport(image_name, engine_url){
    JSONObject report = new JSONObject()
    def all_images = reqestGETJson("${engine_url}/images")
    def image_digest = null
    def response = null
    if (all_images != null && all_images instanceof java.util.ArrayList){
        for (img in all_images) {
            for (tag in img.image_detail.fulltag){
                if (tag.equals(image_name)) {
                    image_digest = img.imageDigest
                }
            }
        }
    }
    else{
        println "ERROR: Images list must be ArrayList of JSONs"
    } 
    def check_status = reqestGETJson("${engine_url}/images/${image_digest}/check?tag=${image_name}&detail=false")
    def anchore_status =  check_status[image_digest][image_name].status[0][0]
    report.put("anchore_check", anchore_status)
    def image_vulns = null    
    if (image_digest != null){
        image_vulns = reqestGETJson("${engine_url}/images/${image_digest}/vuln/all")
    }
    else {
        println "ERROR: Something went wrong, image digest is ${image_digest}"
    }
    if (image_vulns != null && org.apache.groovy.json.internal.LazyMap){
        TreeSet<String> severities = new TreeSet<String>()
        TreeSet<String> package_types = new TreeSet<String>()
        
        image_vulns.vulnerabilities.each {
             package_types.add(it.package_type)
             severities.add(it.severity)
             }
        for (type in package_types){
            JSONObject vulns_by_type = new JSONObject()
            for (severity in severities){
                HashSet<String> vulns = new HashSet<String>()
                HashSet<String> vulns_with_fixes = new HashSet<String>()
                vulns.addAll(( image_vulns.vulnerabilities.findAll{
                    it.severity == severity && 
                    it.package_type == type } ).package)
                vulns_with_fixes.addAll(( image_vulns.vulnerabilities.findAll{
                    it.severity == severity && 
                    it.package_type == type && 
                    it.fix != 'None'} ).package)
                if(vulns.size() > 0){
                    vulns_by_type.put(severity, vulns.size() + "(" + vulns_with_fixes.size() + ")")
                }    
            }
            report.put(type, vulns_by_type)
        }
        println(report)       
    }
    return report
}

def reqestGETJson(url){
    def auth_string = "${ANCHORE_CLI_USER}:${ANCHORE_CLI_PASS}".getBytes().encodeBase64().toString();
    def responce = null
    def http_client = new URL(url).openConnection()
    try {
        http_client.setRequestMethod('GET')
        http_client.setRequestProperty("Accept", "application/json")
        http_client.setRequestProperty("Authorization", "Basic ${auth_string}")
        http_client.setConnectTimeout(5000)
        http_client.connect()
        if (http_client.responseCode == 200 || http_client.responseCode == 202 ) {
            InputStream input_stream = http_client.getInputStream()
            responce = new groovy.json.JsonSlurper().parseText(input_stream.text)
        }
        else {
            println("HTTP response error; ${http_client.responseCode}")
        }
    }
    catch (Exception e) {
        println(e)
        throw e
    }
    finally {
        if (http_client != null) {
            http_client.disconnect();
        }
    }
    return responce
}

return this
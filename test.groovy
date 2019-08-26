#!groovy

@Grab(group='net.sf.json-lib', module='json-lib', version='2.4', classifier='jdk15')



import net.sf.json.JSONObject;
import groovy.json.JsonSlurperClassic;


generateByRequest("docker.io/johnsnowlabs/datalabs:jupyter-notebook-0.35.5-09-token-6", "http://192.168.1.3:8228/v1")

// def generatePlainReport(image, engine_url){
//     def cmd_get_vulns = "anchore-cli --json --url ${engine_url} image vuln ${image}"
//     def cmd_get_check = "anchore-cli --json --url ${engine_url} evaluate check ${image}"

//    //  JSONObject report  = new JSONObject()

//     def jsonSlurper = new JsonSlurperClassic()
//     def string_check = cmd_get_check.execute()
//     def json_check = jsonSlurper.parseText(string_check.text)
//     println json_check[image].status

// }


def generateByRequest(image_name, engine_url){
    def report = new JSONObject()
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
 
}


def reqestGETJson(url){
    def auth_string = "admin:test123".getBytes().encodeBase64().toString();
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

// def arl = ['lolo', 'test']
// def str = ''
// for (i in arl){
//    str = str.concat(i.concat("\n"))
// } 

// println str.trim()

// JSONObject test = new JSONObject()
// if (test.length() != 0){
//    print true
// }
// else {
//    print false
// }

// def jsonString = '{"anchore_check":"fail","image":"admssa/diag:tag0-001.2","os":{"Critical":"1(1)", "Negligible":"0(0)","Unknown":"0(0)","Low":"6(6)","Medium":"18(18)","High":"0(0)"},"non-os":{"Negligible":"0(0)","Unknown":"0(0)","Low":"0(0)","Medium":"4(0)","High":"2(0)"}}'

// def json = new JsonSlurperClassic().parseText(jsonString)

// def formated_message = new String()
// for (object in json){
//    // println object.value.getClass()
//    if (object.value instanceof java.util.HashMap && object.value.size() > 0){
//        object.value = object.value.toString().replaceAll("[^a-zA-Z0-9():]+", " ")
//       //  print object.value 
//    }
//    if (object.value.contains("Critical")){
//       print "olololo Critical"
//    }
//     formated_message = formated_message + "\n*" + object.key.toString() + "*\n" + object.value.toString()
// }

// def t1 = null

// if (t1.toString.contains("tesg")){
//    println "piu"
// }
// else {
//    println "wow"
// }

//println formated_message



// for ( e in json ) {
//     println "key = ${e.key}, value = ${e.value}"
// }

// def fieldList = []
// for ( e in json ) {
//    fieldList.add(new JsonSlurperClassic().parseText("""{"title": "${e.key}", "value": "${e.value}", "short":"false"}"""))
// }

// for (i in fieldList){
//     println i
// }

// def say(msg='Hello',name='world') {
//    println "$msg $name!"
// }
// say("test")

// if ("jopa".contains("a")) {
//    print 'true' 
// }
// else {
//    print 'false'
// }
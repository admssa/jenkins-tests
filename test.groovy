#!groovy

@Grab(group='net.sf.json-lib', module='json-lib', version='2.4', classifier='jdk15')



import net.sf.json.JSONObject;
import groovy.json.JsonSlurperClassic;
import groovy.json.JsonOutput;
import groovy.xml.MarkupBuilder;

contentHTMLreport("sha256:e29a5d862a9b2865a1c71f4cbc6e7a626630aae29325f7a65fb786ffc40e27bd","admssa/diag:tag0-test-01", "docker.io", "http://192.168.1.3:8228/v1")

// def generatePlainReport(image, engine_url){
//     def cmd_get_vulns = "anchore-cli --json --url ${engine_url} image vuln ${image}"
//     def cmd_get_check = "anchore-cli --json --url ${engine_url} evaluate check ${image}"

//    //  JSONObject report  = new JSONObject()

//     def jsonSlurper = new JsonSlurperClassic()
//     def string_check = cmd_get_check.execute()
//     def json_check = jsonSlurper.parseText(string_check.text)
//     println json_check[image].status

// }




def generateByRequest(image_digest, image_name, registry, engine_url){
    JSONObject report = new JSONObject()
    def fulltag = String.format("%s/%s", registry, image_name)
    def response = null
    def check_status = reqestGETJson("${engine_url}/images/${image_digest}/check?tag=${fulltag}&detail=false")
    def anchore_status =  check_status[image_digest][fulltag].status[0][0]
    report.put("anchore_check", anchore_status)
    report.put("image", image_name)
    def image_vulns = reqestGETJson("${engine_url}/images/${image_digest}/vuln/all")

    if (image_vulns != null){
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

def contentHTMLreport(image_digest, image_name, registry, engine_url){
    def html_files = ""
    def fulltag = "${registry}/${image_name}"
    def content = ['os','python',"java","npm","gem","files"]

    for (c in content) {
        def file_name = "${c}_${image_digest}.html"
        def writer = new StringWriter()
        def report = new MarkupBuilder(writer)
        def content_json = reqestGETJson("${engine_url}/images/${image_digest}/content/${c}")
        report.html {
            meta charset:"utf-8"
            meta name:"viewport", content:"width=device-width, initial-scale=1, shrink-to-fit=no"
            head {
                style {
                    mkp.yield """
                        h3 {
                        font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
                        }
                        #report {
                        font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
                        border-collapse: collapse;
                        }

                        #report td, #report th {
                        border: 1px solid #ddd;
                        padding: 8px;
                        }

                        #report tr:nth-child(even){background-color: #f2f2f2;}

                        #report tr:hover {background-color: #ddd;}

                        #report th {
                        padding-top: 12px;
                        padding-bottom: 12px;
                        text-align: left;
                        background-color: #4CAF50;
                        color: white;
                        }
                        """.stripIndent(10)
                        }
            }
            body {
                table(id:"report") {
                    h3 String.format("%s: %s", c.toUpperCase(), image_name)
                    theader {
                        for(item in content_json.content[0]){
                            th "${item.key}"
                        }
                    }
                    tbody {
                        for (items in content_json.content){
                            tr{
                                for (pkg in items) {
                                    td pkg.value
                                }
                            }
                        }
                    }
                }
            }
        } 
        html_files = html_files + "${file_name},"
        println writer
    }
    println html_files
    return html_files
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
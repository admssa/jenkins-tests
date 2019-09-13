#!groovy
import net.sf.json.JSONObject;
import groovy.xml.MarkupBuilder;

def generatePlainReport(image_digest, image_name, registry, engine_url){
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
    def content_types = ['os','python',"java","npm","gem"]
    for (c in content_types) {
        def file_name = "${c}_${image_digest}.html"

        def content_json = reqestGETJson("${engine_url}/images/${image_digest}/content/${c}")
        println content_json.content
        html_table = createHTML(content_json, image_name, c)
        writeFile file: file_name, text: html_table
        html_files = html_files + "${file_name},"   
    }
    return html_files
}

@NonCPS
def createHTML(content_json, image_name, c){
    def writer = new StringWriter()
    def report = new MarkupBuilder(writer)

    report.html {
        delegate.meta charset:"utf-8"
        delegate.meta name:"viewport", content:"width=device-width, initial-scale=1, shrink-to-fit=no"
        delegate.head {
            delegate.style {
                delegate.mkp.yield """
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
        delegate.body {
            delegate.table(id:"report") {
                delegate.h3 String.format("%s: %s", c.toUpperCase(), image_name)
                delegate.theader {
                    for(item in content_json.content[0]){
                        delegate.th "${item.key}"
                        println item.key
                    }
                }
                delegate.tbody {
                    for (items in content_json.content){
                        delegate.tr{
                            for (pkg in items) {
                                println pkg.value
                                delegate.td pkg.value
                            }
                        }
                    }
                }
            }
        }
    }
    return writer.toString()
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
#!groovy
import net.sf.json.JSONObject;

def generatePlainReport(image, engine_url){

    def vuln_types     = ["os", "non-os"]
    def vuln_levels    = ["Low", "Medium", "High", "Negligible"]
    //require IMAGE, VULN_TYPE, VULN_LEVEL
    def get_vulns      = """anchore-cli --url ${engine_url} image vuln %s %s | grep %s | awk '{print \$2}' | sort | uniq | wc -l"""
    def get_status_cmd = String.format("""anchore-cli  --url ${engine_url} evaluate check %s | grep Status | awk '{print \$2}'""", image)
    JSONObject report  = new JSONObject()

    def status = sh script: get_status_cmd, returnStdout: true
    report.put("status", status.trim())
    

    for (type in vuln_types){
        def vulns_by_type = new JSONObject()
        for (level in vuln_levels){
            def cmd = String.format(get_vulns, image, type, level)
            def number = sh script: cmd, returnStdout: true
            vulns_by_type.put(level,number.trim())
        }
        report.put(type,vulns_by_type)
    }

    
    return report
    
}
return this


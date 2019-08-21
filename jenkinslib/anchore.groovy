#!groovy
import net.sf.json.JSONObject;

def generatePlainReport(image, engine_url){

    def vuln_types     = ["os", "non-os"]
    def vuln_levels    = ["Unknown", "Negligible", "Low", "Medium", "High", "Critical" ]
    //require IMAGE, VULN_TYPE, VULN_LEVEL
    def get_vulns      = """anchore-cli --url ${engine_url} image vuln %s %s | grep %s | awk '{print \$2}' | sort | uniq | wc -l"""
    def get_fixes      = """anchore-cli --url ${engine_url} image vuln %s %s | grep -v None | grep %s | awk '{print \$2}' | sort | uniq | wc -l"""
    def cmd_get_status = String.format("""anchore-cli  --url ${engine_url} evaluate check %s | grep Status | awk '{print \$2}'""", image)
    JSONObject report  = new JSONObject()

    def status = sh script: cmd_get_status, returnStdout: true
    def reg = ~/^docker-host:65534\// 
    def img = image - reg
    report.put("anchore_check", status.trim())
    report.put("image", img)

    for (type in vuln_types){
        def vulns_by_type = new JSONObject()
        for (level in vuln_levels){
            def cmd_vulns = String.format(get_vulns, image, type, level)
            def cmd_fixes = String.format(get_fixes, image, type, level)
            def number = sh script: cmd_vulns, returnStdout: true
            def fixes = sh script: cmd_fixes, returnStdout: true
            if (number.trim() != '0'){
                vulns_by_type.put(level, number.trim() + "(" + fixes.trim() + ")")
            }
        }
        report.put(type,vulns_by_type)
    }   
    return report
}
return this
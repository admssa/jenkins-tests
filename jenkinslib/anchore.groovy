#!groovy
import net.sf.json.JSONObject;

def generatePlainReport(image, engine_url){

    vuln_types  = ["os", "non-os"]
    vuln_levels = ["Low", "Medium", "High", "Negligible"]
    //require IMAGE, VULN_TYPE, VULN_LEVEL
    get_vulns = """anchore-cli image vuln %s %s | grep %s | awk '{print \$2}' | sort | uniq | wc -l"""
    //require IMAGE
    get_stoppers = "anchore-cli evaluate check s% --detail | grep stop"
    JSONObject unic_vulns = new JSONObject()


    for (type in vuln_levels){
        def vulns_by_type = new JSONObject()
        for (level in vuln_levels){
            def cmd = String.format(get_vulns, image, type, level)
            def number = sh script: cmd, returnStdout: true
            vulns_by_type.put(level,number)
        }
        unic_vulns.put(type,vulns_by_type)
    }


    return unic_vulns
    
}
return this


#!groovy

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

def generatePlainReport(image, engine_url){
    url = String.format("%s/images/%s/content", engine_url, image)
    def resp = getRequest(url)
    return resp

}

def getRequest(url){
    def responce = null
    def http_client = null
    try {
        http_client =  new URL(url).openConnection()
        http_client.setRequestMethod('GET')
        http_client.setConnectTimeout(10)
        http_client.connect()
        resp_code = http_client.getResponseCode()
        if ( resp_code == '200' || resp_code == '202'){
            responce = http_client.getInputStream().getText()
        }
        else {
            println String.format("Error %s while requesting %s\n%s", resp_code, url, http_client.getInputStream().getText())
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
    println "Returning ${responce}"
    return responce  
}
return this
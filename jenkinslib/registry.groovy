def delete_by_tag(registry, repository, tag){
    def digest = get_digest(registry, repository, tag)
    def result = false
    def http_client = new URL(url).openConnection()
    if (digest != null ){
        def url = String.format("http://%s/v2/%s/manifests/%s",registry,repository,digest)
        try {
            http_client.setRequestMethod('DELETE')
            http_client.setConnectTimeout(5000)
            http_client.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json")
            http_client.connect()
            if (http_client.responseCode == 202) {
                result = true
            }
        }
        catch (Exception e) {
            println(e)
            throw e
        }
        finally{
            if (http_client != null) {
            http_client.disconnect();
            }
        }
    }
    return result
}

def get_digest(registry, repository, tag){
    def url = String.format("http://%s/v2/%s/manifests/%s",registry,repository,tag)
    def responce = null
    def http_client = new URL(url).openConnection()
    try {
        http_client.setRequestMethod('GET')
        http_client.setConnectTimeout(5000)
        http_client.connect()
        if (http_client.responseCode == 200) { 
            responce = http_client.getHeaderField("Docker-Content-Digest");
    }
    else {
        println("HTTP response error; ${http_client.responseCode}")
    }
    }
    catch(Exception e){
        println(e)
        throw e
    }
    finally{
        if (http_client != null) {
        http_client.disconnect();
        }
    }   
    return responce
}

return this
def delete_by_tag(registry, repository, tag){
    def digest = get_digest(registry, repository, tag)
    def result = false
    def http_client = new URL(url)
    if (digest != null ){
        def url = String.format("http://%s/v2/%s/manifests/%s",registry,repository,digest)
        try {
            http_client.openConnection()
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
            http_client.close()
        }
    }
    return result
}

def get_digest(registry, repository, tag){
    def url = String.format("http://%s/v2/%s/manifests/%s",registry,repository,tag)
    def responce = null
    def http_client = new URL(url)
    try {
        http_client.openConnection()
        http_client.setRequestMethod('GET')
        http_client.connect()
        http_client.setConnectTimeout(5000)
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
        http_client.close()
    }
    return responce
}

return this
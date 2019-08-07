#!groovy

import groovy.json.JsonSlurper;



def jsonString = '{"status":"pass","os":{"Low":"0","Medium":"0","High":"0","Negligible":"0"},"non-os":{"Low":"0","Medium":"0","High":"0","Negligible":"0"}}'

def jsonSlurper = new JsonSlurper()
def json = jsonSlurper.parseText(jsonString)

for ( e in json ) {
    println "key = ${e.key}, value = ${e.value}"
}

def fieldList = []
for ( e in json ) {
   fieldList.add(new JsonSlurper().parseText("""{"title": "${e.key}", "value": "${e.value}", "short":"false"}"""))
}

for (i in fieldList){
    println i
}

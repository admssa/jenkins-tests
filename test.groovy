#!groovy

import groovy.json.JsonSlurperClassic;



def jsonString = '{"status":"pass","os":{"Low":"0","Medium":"0","High":"0","Negligible":"0"},"non-os":{"Low":"0","Medium":"0","High":"0","Negligible":"0"}}'

def json = new JsonSlurperClassic().parseText(jsonString)


for ( e in json ) {
    println "key = ${e.key}, value = ${e.value}"
}

def fieldList = []
for ( e in json ) {
   fieldList.add(new JsonSlurperClassic().parseText("""{"title": "${e.key}", "value": "${e.value}", "short":"false"}"""))
}

for (i in fieldList){
    println i
}

def say(msg='Hello',name='world') {
   println "$msg $name!"
}
say("test")

if ("jopa".contains("a")) {
   print 'true' 
}
else {
   print 'false'
}
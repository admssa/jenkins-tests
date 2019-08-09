#!groovy

import groovy.json.JsonSlurperClassic;



def jsonString = '{"status":"pass","os":{"Low":"0(0)","Medium":"0(0)","High":"2(1)","Negligible":"0(0)"},"non-os":{"Low":"0","Medium":"0","High":"0","Negligible":"0"}}'

def json = new JsonSlurperClassic().parseText(jsonString)

def formated_message = new String()
for (object in json){
   // println object.value.getClass()
   if (object.value instanceof java.util.HashMap && object.value.size() > 0){
       object.value = object.value.toString().replace("[", "").replace("]", "")
      //  println object.value
   }
    formated_message = formated_message + "\n*" + object.key.toString() + "*\n" + object.value.toString()
}


println formated_message


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
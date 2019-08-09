#!groovy

import groovy.json.JsonSlurperClassic;

import net.sf.json.JSONObject;

JSONObject test = new JSONObject()
if (test.length() != 0){
   print true
}
else {
   print false
}

// def jsonString = '{"status":"fail","image":"admssa/diag:tag0-001.2","os":{"Negligible":"0(0)","Unknown":"0(0)","Low":"6(6)","Medium":"18(18)","High":"0(0)"},"non-os":{"Negligible":"0(0)","Unknown":"0(0)","Low":"0(0)","Medium":"4(0)","High":"2(0)"}}'

// def json = new JsonSlurperClassic().parseText(jsonString)

// def formated_message = new String()
// for (object in json){
//    // println object.value.getClass()
//    if (object.value instanceof java.util.HashMap && object.value.size() > 0){
//        object.value = object.value.toString().replaceAll("[^a-zA-Z0-9():]+", " ")
//        print object.value
//    }
//     formated_message = formated_message + "\n*" + object.key.toString() + "*\n" + object.value.toString()
// }


// println formated_message



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
#!groovy

import groovy.io.*

def listdirs(dir) {
	dlist = []
	new File(dir).eachDir {dlist << it.name }
	dlist.sort()

    return dlist.flatten()
}

def getdir(tag){
    dirs = listdirs(".")
    def tag_in_dirs = false    
    doble_tag = tag.tokenize("-")[0] + "-" + tag.tokenize("-")[1]
    single_tag = tag.tokenize("-")[0]
    if (doble_tag in dirs) {
        return doble_tag
    }
    else if(single_tag in dirs) {
        return single_tag
    }
    else {
        return null
    }
}

tag = "tag0-test-8.2-1"
def dir = getdir(tag)
println dir
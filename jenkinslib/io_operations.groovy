#!groovy

import groovy.io.*

def listdirs(dir) {
    def currentDir = new File('.')
    def dirs = []
    currentDir.eachFile FileType.DIRECTORIES, {
        dirs << it.name
    }
    return dirs
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
#!groovy

import groovy.io.*

@NonCPS
def listDirs(dir) {
    def currentDir = new File(dir)
    def dirs = []
    currentDir.eachFile FileType.DIRECTORIES, {
        dirs << it.name
}
return dirs
}

def getDir(tag, dir){
    dirs = listDirs(dir)
    def tag_in_dirs = false
    double_tag = tag.tokenize("-")[0] + "-" + tag.tokenize("-")[1]
    single_tag = tag.tokenize("-")[0]
    if (double_tag in dirs) {
        println "Got double"
        return double_tag
    }
    else if (single_tag in dirs) {
        println "Got single"
        return single_tag
    }
    else {
        return null
    }
}
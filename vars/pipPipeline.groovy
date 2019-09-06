def distributePipPackage(s3_path){

    def img         = null
    def docker_file = 'pipBuildDockerfile'
    def base_image  = 'python:3.6.9-slim-buster'
    def image_name  = "pip-builder:${env.TAG_NAME}"
    stage('Prepare Dockerfile'){      
        writeFile file: docker_file, text: "FROM ${base_image}\nADD ./ .\nRUN pip3 install awscli setuptools s3cmd"
    }
    stage('Build environment'){
        img = docker.build(image_name, "-f ${docker_file} ./")
    }
    stage('Built and push package'){
        withCredentials([[
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 's3pypi-pusher',
            accessKeyVariable: 'AWS_ACCESS_KEY_ID',
            secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {          
                img.inside('--entrypoint ""') {
                    withCredentials([string(credentialsId: 's3pypi-secret', variable: 'SECRET')]) {    
                        sh '''
                            python3 setup.py sdist
                            for i in \$(ls dist); do
                              s3cmd put dist/\$i s3://pypi.johnsnowlabs.com/${SECRET}/${s3_path}/
                            done
                            '''
                    }
                }
            }
    }
    stage('Removing local images') {
        sh "docker rmi ${image_name}"
    }
}

return this
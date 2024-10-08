package com.example

class Docker implements Serializable {

    def script

    Docker(script) {
        this.script = script
    }

    // function parameters are passed from within Jenkinsfile initially, to demonstrate dynamic method invokation.
    // Env Vars are exposed via passing 'this' from the original groovy scripts in /vars folder
    def buildImage(String imageName, String imageVersion) {
        script.echo "building the docker image: $imageName:$imageVersion ..."
        script.sh "docker build -t '${script.DOCKER_HUB_REPO_URL}':'${script.VERSION_NUM}' ."
    }

    def dockerLogin() {
        script.sh "echo '${script.PASS}' | docker login -u '${script.USER}' --password-stdin"
    }

    def dockerPush() {
        script.sh "docker push '${script.DOCKER_HUB_REPO_URL}':'${script.VERSION_NUM}'"
    }

}
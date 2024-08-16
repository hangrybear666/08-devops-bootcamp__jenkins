def buildApp() {
  echo 'building the application...'
  sh 'mvn clean package'
}

def buildImage() {
  echo "building the docker image..."
  sh "docker build -t ${DOCKER_HUB_REPO_URL}:${VERSION_NUM} ."
  sh 'echo $PASS | docker login -u $USER --password-stdin'
  sh "docker push ${DOCKER_HUB_REPO_URL}:${VERSION_NUM}"
}

def deployApp() {
  echo 'deploying docker image...'
}

return this
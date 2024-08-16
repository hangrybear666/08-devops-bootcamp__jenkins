def buildApp() {
  echo 'building the application...'
  sh 'mvn clean package'
}
def buildImage() {
  echo "building the docker image..."
  sh "docker build -t hangrybear/devops_bootcamp:${IMAGE_NAME} ."
  sh 'echo $PASS | docker login -u $USER --password-stdin'
  sh "docker push hangrybear/devops_bootcamp:${IMAGE_NAME}"
}
def deployApp() {
  echo "deploying app"
}

return this
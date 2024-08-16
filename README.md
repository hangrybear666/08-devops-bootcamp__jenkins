# Running Jenkins as docker container on remote VPS and executing multibranch declarative pipelines to test, version, publish and deploy web apps remotely.

Collection of Dockerfiles, Jenkinsfiles and shell scripts for automating java/node web app test, versioning, publishing and deployment via declarative Jenkins pipelines on remote VPS hosts.

The main packages are:
- asd


## Setup

1. Pull SCM

    Pull the repository locally by running
    ```
    git clone https://github.com/hangrybear666/08-devops-bootcamp__jenkins.git
    ```

2. Create Remote Linux VPS and configure

    Generate local ssh key and add to remote VPS's `authorized_keys` file.

3. Install additional dependencies on remote

    Some Linux distros ship without the `netstat` or `jq` commands we use. In that case run `apt install net-tools` or `dnf install net-tools` on fedora et cetera.

4. Create environment files 
        
    Add an `.env` file in your repository's `node-app/` directory and add the following key value-pairs:
    ```
    SERVICE_USER_PW=xxx
    ```

5. Add your remote VPS configuration parameters to `config/remote.properties`

    ```
    REMOTE_ADDRESS=167.99.128.206
    ROOT_USER="root"
    SERVICE_USER="docker-runner"
    ```

6. Install docker locally.

    Make sure to install docker and docker-compose (typically built-in) for local development. See https://docs.docker.com/engine/install/


7. Dont forget to open ports in your remote firewall.

    50000 and 8080 for Jenkins, 22 for ssh.

8. Create Docker Hub Credentials and Repo

    a. Sign up to Docker Hub https://hub.docker.com/ and create a personal access token at https://app.docker.com/settings/personal-access-tokens/ 

    b. Save the Docker Hub Credentials in your `.env` file 

    c. Then create a new private Repository and add the URL as well
    ```
    DOCKER_HUB_USER=xxx
    DOCKER_HUB_TOKEN=xxx
    DOCKER_HUB_REPO=hangrybear/devops_bootcamp
    ```

## Usage (Demo Projects)

0. Install docker and start jenkins in docker (dind) on remote VPS and install node and npm inside jenkins-dind container

    NOTE: The installation script is aimed at Debian 12. See https://docs.docker.com/engine/install/debian/ 
    ```
    cd scripts
    ./remote-install-docker.sh
    ./remote-install-java.sh
    ./remote-run-jenkins-in-docker.sh
    ./jenkins-container-install-node-npm.sh
    ```

    To setup and configure jenkins, you first need to extract the standard admin password from the docker container on your remote:
    ```
    ssh root@167.99.128.206
    docker ps
    # extract the hash from jenkins-dind container
    docker exec -it <hash> bash
    cat /var/jenkins_home/secrets/initialAdminPassword
    ```

    Add your new admin credentials to `.env` file or store in another secure location to not lose access to jenkins.

1. Run a declarative Jenkins Pipeline with embedded groovy script to build and push your java application to Docker Hub with Maven

    a. Change the default value of `DOCKER_HUB_REPO_URL`  in your `Jenkinsfile` file to your own or simply provide it as user input when building the pipeline with parameters.

    b. Add `docker-hub-repo` credential-id to jenkins with your username and password you can find in your `.env` file after having run setup step 8.
    
    c. Add your git credentials with the id `git-creds` and the username `x-token-auth` and fetch a personal access token from your git account.

    d. Add Maven under Manage Jenkins -> Tools -> Maven and name it `Maven`.

    e. Create a declarative pipeline under New Item -> Pipeline -> `java-app` and set it to get `java-app/Jenkinsfile` (!) from SCM under Definition and add your Git Credentials with the branch specifier `*/main`.

    asd

## Usage (Exercises)

1. asd

    asdf

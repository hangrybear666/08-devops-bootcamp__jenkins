# Running Jenkins as docker container on remote VPS and executing multibranch declarative pipelines to test, version, publish and deploy web apps remotely.

Collection of Dockerfiles, Jenkinsfiles and shell scripts for automating java/node web app test, versioning, publishing and deployment via declarative Jenkins pipelines on remote VPS hosts.

<u><b>The main projects are:</b></u>
1. Scripts for installing docker, java, node & npm on remote and start a jenkins container with docker capabilities (Docker in Docker / DinD)
2. A `Jenkinsfile` for a declarative and a multibranch jenkins pipeline to build and push a Docker Image of a java maven application automatically on Github pushes via webhook.
3. A `Jenkinsfile_sharedLibrary` containing dynamically parameterized groovy scripts and classes for standardizing and reusing common functionality between pipelines in a git hosted shared library.
4. A `Jenkinsfile_versioning` demonstrating automatic version incrementation, while commiting the new version to Github from the pipeline itself without triggering new builds via SCM webhook triggers.

## Setup

### 1. Pull SCM

    Pull the repository locally by running
    ```
    git clone https://github.com/hangrybear666/08-devops-bootcamp__jenkins.git
    ```

### 2. Create Remote Linux VPS and configure

    Generate local ssh key and add to remote VPS's `authorized_keys` file.

### 3. Install additional dependencies on remote

    Some Linux distros ship without the `netstat` or `jq` commands we use. In that case run `apt install net-tools` or `dnf install net-tools` on fedora et cetera.

### 4. Create environment files

    Add an `.env` file in your repository's root directory and add the following key value-pairs:
    ```
    SERVICE_USER_PW=xxx
    ```

### 5. Add your remote VPS configuration parameters to `config/remote.properties`

    ```
    REMOTE_ADDRESS=167.99.128.206
    ROOT_USER="root"
    SERVICE_USER="docker-runner"
    ```

### 6. Install docker locally.

    Make sure to install docker and docker-compose (typically built-in) for local development. See https://docs.docker.com/engine/install/


### 7. Dont forget to open ports in your remote firewall.

    50000 and 8080 for Jenkins, 22 for ssh.

### 8. Create Docker Hub Credentials and Repo

    a. Sign up to Docker Hub https://hub.docker.com/ and create a personal access token at https://app.docker.com/settings/personal-access-tokens/

    b. Save the Docker Hub Credentials in your `.env` file

    c. Then create a new private Repository and add the URL as well
    ```
    DOCKER_HUB_USER=xxx
    DOCKER_HUB_TOKEN=xxx
    DOCKER_HUB_REPO=hangrybear/devops_bootcamp
    ```

## Usage (Demo Projects)

<details closed>
<summary><b>0. Install docker and start jenkins in docker (dind) on remote VPS and install node and npm inside jenkins-dind container</b></summary>

*NOTE:* The installation script is aimed at Debian 12. See https://docs.docker.com/engine/install/debian/
```bash
cd scripts
./remote-install-docker.sh
./remote-install-java.sh
./remote-run-jenkins-in-docker.sh
./jenkins-container-install-node-npm.sh
```

To setup and configure jenkins, you first need to extract the standard admin password from the docker container on your remote:
```bash
ssh root@167.99.128.206
docker ps
# extract the hash from jenkins-dind container
docker exec -it <hash> bash
cat /var/jenkins_home/secrets/initialAdminPassword
```

Add your new admin credentials to `.env` file or store in another secure location to not lose access to jenkins.

</details>

-----

<details closed>
<summary><b>1. Run a declarative Jenkins Pipeline (and a multibranch pipeline) with embedded groovy script to build and push your java application to Docker Hub with Maven</b></summary>

##### a. Change the default value of `DOCKER_HUB_REPO_URL`  in your `Jenkinsfile` file to your own and push the changes.or simply provide it as user input when building the pipeline with parameters.

##### b. Add `docker-hub-repo` credential-id to jenkins with your username and password you can find in your `.env` file after having run setup step 8.

##### c. Add your git credentials with the id `git-creds` and the username `x-token-auth` and fetch a personal access token from your git account.

##### d. Add Maven under Manage Jenkins -> Tools -> Maven and name it `Maven`.

##### e. Create a declarative pipeline under New Item -> Pipeline -> `java-app` and set it to get `java-app/Jenkinsfile` (!) from SCM under Definition and add your Git Credentials with the branch specifier `*/main`.

##### f. Create a multibranch pipeline under New Item -> Multibranch Pipeline -> `java-app-multibranch` and set it to get `java-app/Jenkinsfile` (!) from SCM under Definition and add your Git Credentials with the branch specifier `*`.

##### g. build both Pipelines manually in Jenkins UI. NOTE: You might have to run the multibranch pipeline twice and/or make changes to the branches after initially starting it, as the `DOCKER_HUB_REPO_URL` parameter might not get initialized and exposed correctly before SCM has pulled once.

</details>

-----

<details closed>
<summary><b>2. Run a declarative pipeline with dynamically parameterized code hosted in a Jenkins shared library for reusability and avoiding code duplication between microservices or teams.</b></summary>

*Note:* Follow the setup steps from the prior step, specifically  b-d .

##### a. Change the default value of `DOCKER_HUB_REPO_URL` in your `Jenkinsfile_sharedLibrary` file to your own or simply provide it as user input when building the pipeline with parameters.

##### b. Change the library identifier and remote in your `Jenkinsfile_sharedLibrary` file to your own and push the changes.

##### c. Create a declarative pipeline under New Item -> Pipeline -> `java-app-sharedLibrary` and set it to get `java-app/Jenkinsfile_sharedLibrary` (!) from SCM under Definition and add your Git Credentials with the branch specifier `*/main`.

##### d. Build the pipeline

</details>

-----

<details closed>
<summary><b>3. To setup Github and to trigger both your declarative pipeline and your multibranch pipeline after code has been pushed automatically via webhook, follow these steps
</b></summary>

##### a. Navigate to Manage Jenkins -> System -> Add Github Server with name `Github`, check the "Manage Hooks" checkbox and and add the API_URL https://api.github.com with a Github API Token as Jenkins credentials and add it to your `.env` file to not lose access. NOTE: The github token must have only webhook permissions, the rest is optional.

##### b. In your pipeline/s check the "Github Projects" flag and set https://github.com/hangrybear666/08-devops-bootcamp__jenkins. Then add the `GitHub hook trigger for GITScm polling` Flag.

##### c. In your Github Repository add your jenkins repo url on push events as hook, navigate to Settings -> Webhooks -> http://165.227.155.148:8080/github-webhook/

</details>

-----

<details closed>
<summary><b>4. To have your pipeline automatically increment the artifact version, add a dynamic docker image tag and commit the version changes to scm without triggered an automatic build via push hook, follow these steps</b></summary>

##### a. Open `Jenkinsfile_versioning` and set your own repository url in `git remote set-url origin` in the commit version update stage.

##### b. Create a declarative pipeline under New Item -> Pipeline -> `java-app-versioning` and set it to get `java-app/Jenkinsfile_versioning` (!) from SCM under Definition and add your Git Credentials with the branch specifier `*/jenkins-jobs`.

##### c. Manage Jenkins -> Available Plugins -> Ignore Committer Strategy -> Install. Configure your pipelines to avoid builds after version commits from jenkins itself via Plugin by navigating to your multibranch pipeline settings and adding `jenkins@example.com` under Configuration -> Branch Sources -> Add -> Ignore Committer Strategy. NOTE: Make sure to check the `Allow builds when a changeset contains non-ignored author(s)` Flag!

##### d. For regular declarative pipeline we can use built-in functionality to ignore certain commits by author `jenkins` under Configuration -> Pipeline -> Additional Behaviors -> Add -> Polling ignores commits from certain users

##### e. Build the pipeline and check the `jenkins-jobs` branch in your github repository for recent version/pom.xml pushes from jenkins user.

</details>

-----
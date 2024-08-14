#!/bin/bash

# load key value pairs from config file
source ../config/remote.properties

# ssh into remote, then install NODE and NPM via NVM inside the running jenkins-dind docker container
ssh $ROOT_USER@$REMOTE_ADDRESS <<EOF

docker exec -u 0 jenkins-dind /bin/bash -c "
# download nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# make nvm immediately available in shell
export NVM_DIR="\$HOME/.nvm"
[ -s "\$NVM_DIR/nvm.sh" ] && \. "\$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "\$NVM_DIR/bash_completion" ] && \. "\$NVM_DIR/bash_completion"  # This loads nvm bash_completion

nvm install 22
node -v
npm -v
"
EOF

#!/bin/bash

openssl aes-256-cbc -K $encrypted_71566cf63c17_key -iv $encrypted_71566cf63c17_iv -in deploy_rsa.enc -out /tmp/deploy_rsa -d
eval "$(ssh-agent -s)"
chmod 0400 /tmp/deploy_rsa
ssh-add /tmp/deploy_rsa

scp -oStrictHostKeyChecking=no ../bookup-api/build/libs/bookup-api.jar $SSH_HOST:$SSH_HOME
scp -oStrictHostKeyChecking=no ../bookup-front/app.zip $SSH_HOST:$SSH_HOME

ssh -T -oStrictHostKeyChecking=no $SSH_HOST <<EOF
    cd $SSH_HOME
    ./run.sh
EOF
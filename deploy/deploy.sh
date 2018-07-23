#!/bin/bash

ssh -T -oStrictHostKeyChecking=no $SSH_HOST <<EOF
    cd $SSH_HOME
    nohup ./deploy.sh &
EOF
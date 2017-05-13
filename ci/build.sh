#!/bin/bash

mkdir /root/.aws
echo "[default]" > /root/.aws/credentials
echo "aws_access_key_id = ${AWS_ACCESS_KEY_ID}" >> /root/.aws/credentials
echo "aws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}" >> /root/.aws/credentials

source /root/.sdkman/bin/sdkman-init.sh
sbt compile
sbt scripted



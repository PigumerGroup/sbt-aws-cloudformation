FROM ubuntu
RUN  apt update && \
     apt upgrade -y && \
     apt install curl zip unzip openssl libssl-dev python python-pip gcc openjdk-8-jdk-headless -y && \
     sh -c 'curl -s "https://get.sdkman.io" | bash' && \
     sh -c 'echo "source /root/.sdkman/bin/sdkman-init.sh; sdk install sbt; sdk install scala" | bash'


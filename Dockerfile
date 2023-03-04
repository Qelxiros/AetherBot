FROM selenium/standalone-chrome:latest
USER 0

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 \
    --recv-keys E1DD270288B4E6030699E45FA1715D88E1DF1F24 && \
    su -c "echo 'deb http://ppa.launchpad.net/git-core/ppa/ubuntu trusty main' \
    > /etc/apt/sources.list.d/git.list" && \
    apt-get update && \
    apt-get -y install git

COPY . .
USER 1200

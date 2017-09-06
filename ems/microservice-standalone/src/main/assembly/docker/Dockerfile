FROM ubuntu:14.04

ARG HTTP_PROXY=${HTTP_PROXY}
ARG HTTPS_PROXY=${HTTPS_PROXY}

ENV http_proxy $HTTP_PROXY
ENV https_proxy $HTTPS_PROXY


RUN apt-get update && apt-get install -y software-properties-common
RUN sudo -E add-apt-repository ppa:openjdk-r/ppa && apt-get update && apt-get install -y openjdk-8-jdk 

ADD . /service
WORKDIR /service
RUN mkdir emsdriver

# get binary zip from nexus
RUN wget -q -O emsdiver-standalone.zip 'https://nexus.onap.org/service/local/artifact/maven/redirect?r=snapshots&g=org.onap.vfc.nfvo.driver.ems.ems&a=emsdriver-standalone&v=LATEST&e=zip' && \
    unzip emsdiver-standalone.zip -d emsdriver && \
    rm -rf emsdiver-standalone.zip


EXPOSE 8206

WORKDIR /service
ENTRYPOINT emsdriver/docker/docker-entrypoint.sh

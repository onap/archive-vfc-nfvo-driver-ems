FROM centos:7

RUN sed -i 's/enabled=1/enabled=0/' /etc/yum/pluginconf.d/fastestmirror.conf
RUN sed -i 's|#baseurl=http://mirror.centos.org/centos|baseurl=http://mirrors.ocf.berkeley.edu/centos|' /etc/yum.repos.d/*.repo
RUN yum update -y

RUN yum install -y wget unzip socat java-1.8.0-openjdk-headless
RUN sed -i 's|#networkaddress.cache.ttl=-1|networkaddress.cache.ttl=10|' /usr/lib/jvm/jre/lib/security/java.security
ENV JAVA_HOME /usr/lib/jvm/jre

ADD . /service
WORKDIR /service
RUN mkdir emsdriver

# get binary zip from nexus
RUN wget -q -O emsdiver-standalone.zip 'https://nexus.onap.org/service/local/artifact/maven/redirect?r=snapshots&g=org.onap.vfc.nfvo.driver.ems.ems&a=emsdriver-standalone&v=LATEST&e=zip' && \
    unzip emsdiver-standalone.zip -d emsdriver && \
    rm -rf emsdiver-standalone.zip

EXPOSE 8206

WORKDIR /service
RUN chmod +x emsdriver/*.sh
RUN chmod +x emsdriver/docker/*.sh
ENTRYPOINT emsdriver/docker/docker-entrypoint.sh

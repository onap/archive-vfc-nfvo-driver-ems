FROM centos:7
ENV JAVA_HOME /usr/lib/jvm/jre
ADD . /service
WORKDIR /service
RUN bash docker-env-config.sh

EXPOSE 8206
USER onap
WORKDIR /service
ENTRYPOINT emsdriver/docker/docker-entrypoint.sh

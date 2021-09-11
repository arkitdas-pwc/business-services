FROM egovio/alpine-maven-builder-jdk-8:gcp AS build
ARG WORK_DIR
WORKDIR /app

#FROM jboss/wildfly
RUN mkdir /opt/jboss/
COPY wildfly.zip /opt/jboss/
RUN cd /opt/jboss/ && unzip wildfly.zip && rm wildfly.zip

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

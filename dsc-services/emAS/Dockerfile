FROM egovio/alpine-maven-builder-jdk-8:gcp AS build
ARG WORK_DIR
WORKDIR /app

# Create runtime image
FROM egovio/wildfly:1-helm-fin-e6312078

COPY *.war /opt/jboss/wildfly/standalone/deployments/
COPY ds.zip /opt/jboss/wildfly/standalone/
RUN cd /opt/jboss/wildfly/standalone/ && unzip ds.zip && rm ds.zip

USER jboss

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]


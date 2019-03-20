FROM maven:3-jdk-8-alpine AS BUILD

RUN apk add --no-cache git

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN mvn --batch-mode --errors --fail-fast \
  --define maven.javadoc.skip=true \
  --define skipTests=true install

FROM jetty:jre8-alpine

ENV JAVA_OPTIONS="-Djava.awt.headless=true -Djava.io.tmpdir=/tmp"

RUN mkdir -p /etc/sos \
 && ln -s /etc/sos /var/lib/jetty/webapps/ROOT/config

VOLUME /tmp
VOLUME /etc/sos

COPY --from=BUILD /usr/src/app/webapp/target/52n-sos-webapp /var/lib/jetty/webapps/ROOT

HEALTHCHECK --interval=5s \
            --timeout=5s \
            --retries=3 \
            CMD wget localhost:8080/ -q -O - > /dev/null 2>&1


LABEL org.label-schema.name "52°North SOS"
LABEL org.label-schema.description "52°North Sensor Observation Service"
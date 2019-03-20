FROM maven:3-jdk-8-alpine AS BUILD

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN mvn --batch-mode --errors --fail-fast install

FROM jetty:jre8-alpine

ENV JAVA_OPTIONS="-Djava.awt.headless=true -Djava.io.tmpdir=/tmp"

VOLUME /tmp

COPY --from=BUILD /usr/src/app/webapp/target/52n-sos-webapp /var/lib/jetty/webapps/ROOT
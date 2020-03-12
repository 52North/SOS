FROM maven:3-jdk-8-alpine AS BUILD

RUN apk add --no-cache git

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN mvn --batch-mode --errors --fail-fast \
  --define maven.javadoc.skip=true \
  --define skipTests=true install

FROM jetty:jre8-alpine

USER root
RUN  set -ex \
 && apk add --no-cache jq \
 && wget -q -P /usr/local/bin https://raw.githubusercontent.com/52North/arctic-sea/master/etc/faroe-entrypoint.sh \
 && chmod +x /usr/local/bin/faroe-entrypoint.sh
USER jetty
ENV FAROE_CONFIGURATION /etc/sos/configuration.json

COPY --from=BUILD /usr/src/app/webapp/target/52n-sos-webapp /var/lib/jetty/webapps/ROOT
COPY ./docker/logback.xml /var/lib/jetty/webapps/ROOT/WEB-INF/classes/
COPY ./docker/helgoland.json /var/lib/jetty/webapps/ROOT/static/client/helgoland/assets/settings.json
COPY ./docker/default-config /etc/sos

USER root
RUN mkdir -p /var/lib/jetty/webapps/ROOT/WEB-INF/tmp \
 && chown -R jetty:jetty /var/lib/jetty/webapps/ROOT /etc/sos
USER jetty
RUN ln -s /etc/sos /var/lib/jetty/webapps/ROOT/WEB-INF/config

VOLUME /var/lib/jetty/webapps/ROOT/WEB-INF/tmp
VOLUME /etc/sos

ENV FAROE_CONFIGURATION /etc/sos/configuration.json


HEALTHCHECK --interval=5s --timeout=20s --retries=3 \
  CMD wget http://localhost:8080/ -q -O - > /dev/null 2>&1

LABEL maintainer="Carsten Hollmann <c.hollmann@52north.org>" \
      org.opencontainers.image.title="52°North SOS" \
      org.opencontainers.image.description="52°North Sensor Observation Service" \
      org.opencontainers.image.licenses="GPLv2" \
      org.opencontainers.image.url="https://52north.org/software/software-projects/sos/" \
      org.opencontainers.image.vendor="52°North GmbH" \
      org.opencontainers.image.source="https://github.com/52north/SOS.git" \
      org.opencontainers.image.version="5.0.0" \
      org.opencontainers.image.authors="Carsten Hollmann <c.hollmann@52north.org>, Christian Autermann <c.autermann@52north.org>"

ENTRYPOINT [ "/usr/local/bin/faroe-entrypoint.sh", "/docker-entrypoint.sh" ]
CMD [ "java", "-jar", "/usr/local/jetty/start.jar" ]

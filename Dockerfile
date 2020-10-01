FROM adoptopenjdk/openjdk8:alpine-slim AS BCRYPT_BUILD

WORKDIR /usr/src/app
COPY docker/bcrypt/BCrypt.java /usr/src/app
RUN javac -source 1.8 -target 1.8 BCrypt.java \
 && rm BCrypt.java

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
 && apk add --no-cache jq gettext \
 && wget -q -P /usr/local/bin https://raw.githubusercontent.com/52North/arctic-sea/master/etc/faroe-entrypoint.sh \
 && chmod +x /usr/local/bin/faroe-entrypoint.sh
USER jetty:jetty

ENV FAROE_CONFIGURATION /etc/sos/configuration.json
ENV WEBAPP ${JETTY_BASE}/webapps/ROOT
ENV HELGOLAND ${WEBAPP}/static/client/helgoland
ENV HELGOLAND_CONFIG ${HELGOLAND}/assets/settings.json

COPY --chown=jetty:jetty --from=BUILD /usr/src/app/webapp/target/52n-sos-webapp ${WEBAPP}
COPY --chown=jetty:jetty ./docker/logback.xml    ${WEBAPP}/WEB-INF/classes/
COPY --chown=jetty:jetty ./docker/jetty-web.xml  ${WEBAPP}/WEB-INF/jetty-web.xml.template
COPY --chown=jetty:jetty ./docker/helgoland.json ${HELGOLAND_CONFIG}
COPY --chown=jetty:jetty ./docker/default-config /etc/sos

USER root
COPY --from=BCRYPT_BUILD /usr/src/app /usr/lib/java-brcypt
COPY docker/bcrypt/bcrypt.sh /usr/local/bin/bcrypt
COPY docker/sos-entrypoint.sh /usr/local/bin


RUN mkdir -p ${WEBAPP}/WEB-INF/tmp \
 && ln -s /etc/sos ${WEBAPP}/WEB-INF/config \
 && chown -R jetty:jetty ${WEBAPP}/WEB-INF/tmp \
                         ${WEBAPP}/WEB-INF/config \
                         /etc/sos \
 && chmod +x /usr/local/bin/sos-entrypoint.sh \
             /usr/local/bin/bcrypt
USER jetty:jetty

VOLUME ${WEBAPP}/WEB-INF/tmp
VOLUME /etc/sos

HEALTHCHECK --start-period=60s --interval=30s \
            --timeout=20s --retries=3 \
  CMD wget http://localhost:8080${SOS_CONTEXT_PATH} -q -O - > /dev/null 2>&1

LABEL maintainer="Carsten Hollmann <c.hollmann@52north.org>" \
      org.opencontainers.image.title="52°North SOS" \
      org.opencontainers.image.description="52°North Sensor Observation Service" \
      org.opencontainers.image.licenses="GPLv2" \
      org.opencontainers.image.url="https://52north.org/software/software-projects/sos/" \
      org.opencontainers.image.vendor="52°North GmbH" \
      org.opencontainers.image.source="https://github.com/52north/SOS.git" \
      org.opencontainers.image.version="5.3.1" \
      org.opencontainers.image.authors="Carsten Hollmann <c.hollmann@52north.org>, Christian Autermann <c.autermann@52north.org>"

ENTRYPOINT [ \
  "/usr/local/bin/sos-entrypoint.sh", \
  "/usr/local/bin/faroe-entrypoint.sh", \
  "/docker-entrypoint.sh" ]

ENV SOS_ADMIN_USERNAME=admin \
    SOS_ADMIN_PASSWORD=password \
    SOS_DATASOURCE_TYPE=h2 \
    SOS_DATASOURCE_USERNAME=postgres \
    SOS_DATASOURCE_PASSWORD=postgres \
    SOS_DATASOURCE_DATABASE=sos \
    SOS_DATASOURCE_HOST=db \
    SOS_DATASOURCE_PORT=5432 \
    SOS_CONTEXT_PATH=/

CMD [ "java", "-jar", "/usr/local/jetty/start.jar" ]

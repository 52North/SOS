#!/bin/sh

set -e

set_admin_user() {
  local username="$1"
  local password="$2"
  local temp="$(mktemp)"
  password=$(echo "${password}" | bcrypt --rounds 10)
  jq --arg username "${username}" --arg password "${password}" \
    '.users = {"\($username)": $password}' \
    "${FAROE_CONFIGURATION}" > "${temp}"
  mv -f "${temp}" "${FAROE_CONFIGURATION}"

}

if [ -n "${SOS_ADMIN_USERNAME}" -a -n "${SOS_ADMIN_PASSWORD}" ]; then
  set_admin_user "${SOS_ADMIN_USERNAME}" "${SOS_ADMIN_PASSWORD}"
fi


if [ "${SOS_DATASOURCE_TYPE}" = "postgres" ]; then
  : ${SOS_DATASOURCE_USERNAME:=postgres}
  : ${SOS_DATASOURCE_PASSWORD:=postgres}
  : ${SOS_DATASOURCE_DATABASE:=sos}
  : ${SOS_DATASOURCE_HOST:=db}
  : ${SOS_DATASOURCE_PORT:=5432}
  
  rm -f /etc/sos/datasource.properties
  cat > /etc/sos/datasource.properties <<EOF
HIBERNATE_DIRECTORY=/hbm/transactional/core;/hbm/transactional/dataset
hibernate.c3p0.acquire_increment=1
hibernate.c3p0.contextClassLoaderSource=library
hibernate.c3p0.idle_test_period=30
hibernate.c3p0.max_size=30
hibernate.c3p0.max_statements=0
hibernate.c3p0.min_size=10
hibernate.c3p0.preferredTestQuery=SELECT 1
hibernate.c3p0.privilegeSpawnedThreads=true
hibernate.c3p0.timeout=0
hibernate.connection.autoReconnect=true
hibernate.connection.autoReconnectForPools=true
hibernate.connection.driver_class=org.postgresql.Driver
hibernate.connection.username=${SOS_DATASOURCE_USERNAME}
hibernate.connection.password=${SOS_DATASOURCE_PASSWORD}
hibernate.connection.provider_class=org.hibernate.c3p0.internal.C3P0ConnectionProvider
hibernate.connection.testOnBorrow=true
hibernate.connection.url=jdbc:postgresql://${SOS_DATASOURCE_HOST}:${SOS_DATASOURCE_PORT}/${SOS_DATASOURCE_DATABASE}
hibernate.datasource.timeStringFormat=
hibernate.datasource.timeStringZ=false
hibernate.datasource.timezone=+00\:00
hibernate.default_schema=public
hibernate.dialect=org.n52.hibernate.spatial.dialect.postgis.TimestampWithTimeZonePostgisPG95Dialect
hibernate.jdbc.batch_size=20
org.n52.iceland.ds.Datasource=org.n52.sos.ds.datasource.PostgresDatasource
PROVIDED_JDBC=false
sos.database.concept=TRANSACTIONAL
sos.feature.concept=DEFAULT_FEATURE_CONCEPT
EOF
fi

exec "$@"
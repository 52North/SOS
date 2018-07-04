# 52°North SOS OSGeo-Live Contribution preparation

1. **Create SOS package**

      1. Checkout the branch:

         ```git checkout distribution/osgeolive```

      1. Merge with the latest release (replace `x.y.z` with the latest version):

         ```git merge x.y.z --no-ff```

      1. Fix conflicts.

      1. Build with maven: ```mvn clean install```.

1. **Example Data Update**

    1. Identify software versions of OSGeoLive via:

         1. tomcat version:
            ```
            user@osgeolive:~$ /usr/share/tomcat8/bin/version.sh
            ```
         1. postgreSQL and PostGIS:
            ```
            user@osgeolive:~$ sudo -u postgres psql
            postgres=# \c 52nSOS
            52nSOS0# SELECT version(), PostGIS_version();
            ```
         1. Update `SOS_REPO/webapp-osgeolive/docker/docker-compose.yml` to match
            the output of `version.sh`:
            ```
            db:
              image: mdillon/postgis:10-alpine
            [...]
            sos:
              image: tomcat:8.5-jre8-alpine
            ```

    1. Insert the sensor using ```insert-sensor.xml``` using *POX* binding
       (*Client* &rarr; `SOS` &rarr; `2.0.0` &rarr; `POX` &rarr; `InsertSensor`
       &rarr; `[POX] InsertSensor (SOS 2.0.0)`). Copy paste the request from
       the **updated** file. Replace height, coordinates and all other date
       that is not up to date.

      1. Update json requests (in `src/main/resources/`) to current year:

         *Update the search patterns, but ensure to use a month with **31** days*:

         ```$ find . -name "*.json" -exec sed -i s/YYYY-MM/thisYear-ExampleMonth/g {} \;```

      1. Update json requests to current location:

         *Update the search patterns to match the latest used location!*

         ```
         $ find . -name "*.json" -exec sed -i s/YY.YYYY,/new.Location,/g {} \;
         $ find . -name "*.json" -exec sed -i s/XX.XXXX/new.Location/g {} \;
         ```

      1. Insert all data using the ```data-request_*.json``` files with the *JSON* binding via *Client* &rarr; `SOS` &rarr; `2.0.0` &rarr; `JSON` &rarr; `InsertObservation` &rarr; `[JSON] InsertObservation (SOS 2.0.0)`.

      1. Set the i18n settings for

         * feature of interest and

         * observable properties

         via the [I18N settings](http://localhost:8080/52nSOS/admin/i18n).

      1. Export the current database into file ```52nSOS.sql``` in ```webapp-osgeo-live/src/main/assembly``` folder using the following settings in e.g. PGAdmin III:

         * File: ```52nSOS.sql```
         * Format: ```plain```
         * Encoding: ```UTF8```
         * Rolename: ```user```

      1. **Optional**: Update the following files from the deployed 52N-SOS instance:

         * In the case of 52N-SOS configuration changes:

           ```configuration.db``` into ```webapp-osgeo-live/src/main/webapp```

         * In the case of datasource configuration changes:

           ```datasource.properties``` into ```webapp-osgeo-live/src/main/webapp/WEB-INF```

         * In the case of jsClient configuration changes:

           ```settings.json``` into ```webapp-osgeo-live/src/main/webapp/static/client/jsClient```

1. **Final Package**

    Run ```mvn clean install``` for the last time and copy the resulting ```52n-sos-osgeo-live-X.Y.tar.gz``` to the 52N webdav folder for OSGeo-Live ```https://52north.org/files/sensorweb/osgeo-live/```.

1. **Update Installer**:

    Update the properties

      * `SOS_TAR_NAME` to match the name of the new uploaded file.

      * `SOS_VERSION` to match the version of the current SOS deployed on OSGeoLive.

1. **Update Documentation**

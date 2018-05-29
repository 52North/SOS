# 52°North SOS OSGeo-Live Contribution preparation

1. **Create SOS package**

  1. Check, if [feature/sos-js](https://github.com/EHJ-52n/SOS/tree/feature/sos-js) could be merged if the ```resultModel:=om:Measurement``` [issue is fixed](https://github.com/52North/SOS/pull/429).

  1. Checkout the branch:

    ```git checkout distribution/osgeolive```

  1. Merge with the latest release (replace ```x.y.z``` with the latest version):

    ```git merge x.y.z --no-ff```

  1. Fix conflicts.

  1. Ensure that in the root ```pom.xml```:

    * ```conf.sos.name``` is set to ```52nSOS``` and

    * ```conf.osgeo.live.version``` in set to the latest OSGeo-Live version e.g. ```12.0```.

  1. Build with maven: ```mvn clean install```.

1. **Example Data Update**

  1. Deploy and configure ```52nSOS##x.y.z.war``` in any tomcat, having a postgresql server with postgis enabled db running. The database name MUST be ```52nSOS``` with owner ```user``` using password ```user``` (requirements from OSGeo-Live). **[i]** This might require a reset because the build version is *pre-configured* via *Admin* &rarr; *Reset*.

    1. Admin user MUST be ```user``` with password ```user```.

    1. Activate in the [operations configuration](http://localhost:8080/52nSOS/admin/operations) (*Admin* &rarr; *Settings* &rarr; *Encodings*) *InsertSensor* and *InsertObservation*.

    1. Disable in the [encoding configuration](http://localhost:8080/52nSOS/admin/encodings) (*Admin* &rarr; *Settings* &rarr; *Operations*) each entry with ```http://dd.eionet.europa.eu/schemaset/id2011850eu-1.0```.

  1. Insert the sensor using ```insert-sensor.xml``` using *POX* binding (*Client* &rarr; `SOS` &rarr; `2.0.0` &rarr; `POX` &rarr; `InsertSensor` &rarr; `[POX] InsertSensor (SOS 2.0.0)`). Copy paste the request from the **updated** file. Replace height, coordinates and all other date that is not up to date.

  1. Update json requests to current year:

    *Update the search patterns, but ensure to use a month with **31** days!*

    ```$ find . -name "*.json" -exec sed -i s/2017-03/yyyy-mm/g {} \;```

  1. Update json requests to current location:

    *Update the search patterns to match the latest used location!*

    ```
    $ find . -name "*.json" -exec sed -i s/51.9348,/yy.yyyy,/g {} \;
    $ find . -name "*.json" -exec sed -i s/7.6524,/xx.xxxx,/g {} \;
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

  Run ```mvn clean install``` for the last time and copy the resulting ```52n-sos-osgeo-live-X.Y.tar.gz``` to the 52N webdav folder for OSGeo-Live ````https://52north.org/files/sensorweb/osgeo-live/```.

1. **Update Installer**

  Update the property:

  - `SOS_TAR_NAME` to match the name of the new uploaded file.
  - `SOS_VERSION` to match the version of the current SOS deployed on OSGeoLive.

1. **Update Documentation**

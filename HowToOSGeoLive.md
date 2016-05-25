# 52°North SOS OSGeo-Live Contribution preparation

1. **Create SOS package**

  1. Check, if [feature/sos-js](https://github.com/EHJ-52n/SOS/tree/feature/sos-js) could be merged if the ```resultModel:=om:Measurement``` issue is fixed.

  1. Checkout the branch:

    ```git checkout distribution/osgeolive```

  1. Merge with the latest release (replace ```x.y.z``` with the latest version):

    ```git merge x.y.z --no-ff```

  1. Fix conflicts.

  1. Ensure that ```conf.sos.name``` in the root ```pom.xml``` is set to ```52nSOS```.

  1. Build with maven: ```mvn clean install```.

  1. Ensure that

    * *REST API*, and
    * *jsClient*

    are contained in the bundle in ```webapp-bundle/target```.

  1. Copy ```webapp-bundle/target/52nSOS##x.y.z.war``` to your OSGeo-Live package folder.

1. **Example Data Update**

  1. Deploy and configure ```52nSOS##x.y.z.war``` in any tomcat, having a postgresql server with postgis enabled db running. The database name MUST be ```52nSOS``` with owner ```user``` using password ```user``` (requirements from OSGeo-Live).

    1. Admin user MUST be ```user``` with password ```user```.

    1. Activate in the [operations configuration](http://localhost:8080/52nSOS/admin/operations) *InsertSensor* and *InsertObservation*.

    1. Disable in the [encoding configuration](http://localhost:8080/52nSOS/admin/encodings) each entry with ```http://dd.eionet.europa.eu/schemaset/id2011850eu-1.0```.

  1. Insert the sensor using ```insert-sensor.xml``` using *POX* binding.

  1. Update json requests to current year:

     *Update the search patterns, but ensure to use a month with 31 days!*

    ```$ find . -name "*.json" -exec sed -i s/2016-07/yyyy-mm/g {} \;```

  1. Insert all data using the ```data-request_*.json``` files with the *JSON* binding.

  1. Set the i18n settings for feature of interest and observable properties via the [I18N settings](http://localhost:8080/52nSOS/admin/i18n).

  1. Export the current database into file ```52nSOS.sql``` in your OSGeo-Live package folder using the following settings in e.g. PGAdmin III:

    * File: ```52nSOS.sql```
    * Format: ```plain```
    * Encoding: ```UTF8```
    * Rolename: ```user```

1. **Final Package**

  1. Add database configuration

  1. Add service configuration

1. **Update Installer**

1. **Update Documentation**

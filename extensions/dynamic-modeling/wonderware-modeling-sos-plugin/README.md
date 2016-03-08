# 52°North SOS - Wonderware Modeling Extension
README file of the Wonderware Modeling Extension for the 52°North Sensor Observation Service (SOS)

This is hosted in the repository of the [52°North Sensor Observation Service (SOS)][1].

This 52°North SOS extension integrates [SCADA - (Supervisory Control and Data Acquisition)][6] models 
of [Wonderware platforms][7] in the 52°North Sensor Observation Service (SOS) adding their entities 
as "virtual" SOS objects (Offerings, Procedures, FOIs, Observations and so on). 

The plugin queries the data registered in the SCADA database to inject them to output WEB responses. 
The data (Sensors, and historic-live records) are dynamically inserted using a mechanism of virtual 
injection of data in order to avoid to save them in the main database managed by 
the 52°North SOS service.

All SCADA entities configured, with their respective historic-live records, will create on-the-fly
a set of available -Offering/Procedure/FeatureOfInterest/Observation/Timeseries- objects.

The plugin is configured using a settings file hosted in `WEB-INF\dynamic-models` folder. 
Each entry in this file defines the properties of a sensor type (with its database connection and its related geographical store).

We can define an entry for an SCADA/Wonderware object type similar to:

    <model class="org.n52.sos.extensions.wonderware.SimpleFeatureModel">
      <name>SCADA_flowmeters</name>
      <description>My flowmeters.</description>
      <databaseDriverClass>com.microsoft.sqlserver.jdbc.SQLServerDriver</databaseDriverClass>
      <databaseConnectionUrl>jdbc:sqlserver://xxx.xxx.xxx.xxx:1433;DatabaseName=MyDatabase;user=myuser;Password=mypwd
      </databaseConnectionUrl>
      <featureStoreUrl>wonderware/Caudalimetros_SCADA.shp</featureStoreUrl>
      <featureKey>SCADA</featureKey>
      <attributes>
        <attribute>
          <attributeName>flow</attributeName>
          <fieldId>SCADA</fieldId>
          <dateFrom>2011-06-01 00:00:00</dateFrom>
          <dateTo>now</dateTo>
          <stepTime>300000</stepTime>
          <RetrievalMode>Cyclic</RetrievalMode>
          <RetrievalAlignment>StartDateAligned</RetrievalAlignment>
        </attribute>
      </attributes>      
    </model>

  Where:

    * class: Java class used to manage a specific SCADA/Wonderware object.
    * name: Name of the object type (Unique).
    * description: Label or description.
    * databaseDriverClass: JDBC driver class to access to the database.
    * databaseConnectionUrl: Connection string to the database.
    * featureStoreUrl: Connection string to the GIS Feature Store. This store contains the related geometries of objects managed.
    * featureKey: Fieldname key of the features.
    * attributes: Settings set to configure the specific SCADA data retrieval mode.

  Now, only shapefiles are supported as feature sources.


## Branches

The 52°North SOS is a reference implementation of the [OGC Sensor Observation Service specification (version 2.0)][2]. 
It was implemented during the [OGC Web Services Testbed, Phase 9 (OWS-9)][3] and tested to be compliant to this 
specification within the [OGC CITE testing][4] in December of 2012.

This project follows the [Gitflow branching model](http://nvie.com/posts/a-successful-git-branching-model/). "master" reflects the latest stable release.
Ongoing development is done in branch [develop](../../tree/develop) and dedicated feature branches (feature-*).

## Code Compilation

This project is managed with Maven. Simply run `mvn clean install` to create a deployable .JAR file.


## Distributions

Here you can find some information that relates to the distributions of the 52°North SOS.

### Download

The latest release of 52°North SOS can be downloaded from this website:

    http://52north.org/downloads/sensor-web/sos

### Contents
  * `/src` :   The source files of this 52°North SOS extension
  * `README` : This file

### Installation

No printer friendly installation guide exist for this release. Instead, refer to the [wiki documentation][5].


## Contributing

You are interesting in contributing the 52°North SOS and you want to pull your changes to the 52N repository to make it available to all?

In that case we need your official permission and for this purpose we have a so called contributors license agreement (CLA) in place. 
With this agreement you grant us the rights to use and publish your code under an open source license.

A link to the contributors license agreement and further explanations are available here: 

    http://52north.org/about/licensing/cla-guidelines


## Work remaining

  * It would be possible to implement the notification of alerts about values out of configured limits.


--
[1]: http://52north.org/communities/sensorweb/sos/index.html
[2]: https://portal.opengeospatial.org/files/?artifact_id=47599
[3]: http://www.ogcnetwork.net/ows-9
[4]: http://cite.opengeospatial.org/test_engine
[5]: https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation
[6]: https://en.wikipedia.org/wiki/SCADA
[7]: http://software.schneider-electric.com/wonderware

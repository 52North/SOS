# OGC Web Service Statistics Module for 52°North web services

## About

This module allows to capture usage statistics for OGC web services into a document database for interactive visualisation and long-term statistics of service and domain specific meta-information.

Project description: https://wiki.52north.org/bin/view/Projects/GSoC2015Statistics4Ows

## Features
The statistics collection module collects information during the whole lifecycle of the user requests. So it means not only the user's initial request is measured but it's response and it's generated exceptions also. The following requests and some of their properties are measured (to see  the full list of measured properties see [this document]() )
- Core Extension
  - GetCapabilities, for requesting a self-description of the service.
  - GetObservation, for requesting the pure sensor data encoded in Observations & Measurements 2.0 (O&M)
  - DescribeSensor for requesting information about a certain sensor, encoded in a Sensor Model Language 1.0.1 (SensorML) instance document.
- Enhanced Extension
  - GetFeatureOfInterest, for requesting the GML 3.2.1 encoded representation of the feature that is the target of the observation.
  - GetObservaitonById, for requesting the pure sensor data for a specific observation identifier
- Transactional Extension
  - InsertSensor, for publishing new sensors.
  - UpdateSensorDescription, for updating the description of a sensor
  - DeleteSensor, for deleting a sensor
  - InsertObservation, for publishing observations for registered sensors.
- Result Handling Extension
  - InsertResultTemplate, for inserting a result template into a SOS server that describes the structure of the values of a InsertResult of GetResult request.
  - InsertResult, for uploading raw values accordingly to the structure and encoding defined in the InsertResultTemplate request
  - GetResultTemplate, for getting the result structure and encoding for specific parameter constellations
  - GetResult, for getting the raw data for specific parameter constellations

There is one utility request called `BatchRequest` which is not analyzed and measured by this module.

## Usage
Before you start the webapplication make sure that your Elasticsearch cluster is running and reachable from your server. To start the module go to your SOS deployment administrator's settings page on the Elasticsearch tab.

The Elasticsearch functionality must be enabled for this module to be actived. Before you start your SOS deployment make sure that your Elasticsearch server is running and reachable. You can enable the module by checking the check box on the settings page.
- **Connection mode**: Two connection mode is supported. Node and Transport client. You can read more about it on the official site what is the difference. In short if you can use the Node connection type it is recommended, but in case there is some firewall issues stick with the Transport Client mode.
- **Cluster name**: Elasticsearch servers are organized in cluster. Specify the name to connect to. For more informatino refer to the official guide here .
- **Address(es) of the cluster**: The Statistics module uses unicast messages to connect with the Elasticsearch cluster. You can specify here a comma separated list of addresses in form of <host>[:<port>] to connect to.
- **Index name**: The index name. If the index is already exists the module will store the statistics values in that index. This value and the next Type name must be used in conjuction to specify the exact path of your data in Elasticsearch.
- **Type name**: Inside your index under what type-name to store your data. If you type-name in your index is already exists the module will continue to store the collected data there if it is possible.
- **Unique id**: It is possible that many SOS deployments could store their data in one Elasticsearch index/type. To distingush between the deployments an unique id is generated for you, but you can specify your own id. Collosion is possible.
Enable preconfigured Kibana settings: If you would like to use our provided Kibana visualizations and dashboards you can enable the checkbox and specify the file location (json formatted text file) of the kibana settings. The settings will be loaded to the .kibana Elasticsearch index where the Kibana 4 application will read up the configurations.

**Important! The settings will take only effect if you restart the webapplication.**
![Elasticsearch settings](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/elasticsearch-settingspage.JPG)

- **Enable the geolocation transformation** To enable the GeoLite database functionality which converts the client IPv4 address to geo coordinates check the checkbox.
- **Downloading type**
  - **Auto download** - The geolite databases (city and country) will be downloaded and extracted to the directory you specify with the `Download directory` field. The web server must have access to this directory. If the directory not exists it will be created.
  - **Manual** - The geolite databases are downloaded by the administrator. In this case the `City location` and the `Country location` fields must be specified.
- **Download directory** - By auto downloading the files will be extrected to this folder.
- **Country location** - Absolute file path to the country geolite database
- **City location** - Absolute file path to the city geolite database
- **Geolite database type** - Country or City. It specifies which database the module will use.

**Important! The settings will take only effect if you restart the webapplication.**

![Geolite settings](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/geolite-settingspage.JPG)

#### Pitfalls 
In case the webapplication can't connect to your Elasticsearch cluster the statistics module is won't collect any information and unfortunately currently no method exists to notify the user on the admin interface. So after the webapplication starts up **check your logs** whether the Elasticsearch Statistics Client is successfully started. You can see the error message about the cause if there is any. Possible causes:
- org.elasticsearch.discovery.MasterNotDiscoveredException - can't connect to the Elasticsearch cluster. Make sure you gave the same cluster name in both your webapplication settings and in your Elasticsearch cluster settings. If a firewall is between your two systems try the Transport Client connection mode.
- org.elasticsearch.ElasticsearchException Database schema version inconsistency - The Elasticsearch schema in your index and type name is already exists but with a different schema which your current SOS webapplication supports. Change your type or index name and the SOS webapplication will create an empty index and type name for you. Data migration is currently not supported between different schemas.

## Setup

- Checkout this branch.
- Install Elasticsearch on your local machine in version 1.7.x from https://www.elastic.co/downloads/elasticsearch
- Install Kibana 4.x on your local machine from https://www.elastic.co/downloads/kibana
- Elasticsearch configuration
	- Open up the Elasticsearch config file under the ``config/elasticsearch.yml``. Uncomment and modify existing entry cluster-name, e.g. to ``ogc-statistics-cluster``, i.e. ``cluster.name: ogc-statistics-cluster`` if you want to change the default settings.
	- In production it is highly advised to disable multicast discovery so insert the line into the config file
	`discovery.zen.ping.multicast.enabled: false`
	- Copy the contents of the `scripts` folder from the ``statistics\misc\scripts``, containing some `*.groovy` script files, to every Elasticsearch node's `..\config\scripts` directory. If you miss this step a couple of visualizations in Kibana won't be available.
- Run ``mvn clean install -DskipTests=true`` from the SOS root project.
- Deploy the .war file on your Tomcat server.
- Go to your application's url and set up your SOS deployment, i.e. configure a database. For testing, use "H2/GeoDB (in memory)". 
  - In the SOS settings, open the page "Statistics".
    - Activate the checkbox "Enable statistics collection".
	- Configure the previously set cluster name, e.g. ``ogc-statistics-cluster``
	- Configure further statistics settings, see instructions on settings page.
- Run example queries with [SoapUI](http://soapui.org/) using the file ``SOS\statistics\statistics-core\src\test\resources\soapUI\SOSBulkRequests.xml``: File -> Import project, then 
- Start Elasticsearch with the appropriate executable in ``<Elasticsearch directory>/bin``, e.g. ``bin/elasticsearch.bat`` on Windows. On Linux machines refer to the [official guide here](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-service.html).
- Start Kibana with the appropriate executable in ``<Kibana directory>/bin``, e.g. ``bin\kibana.bat`` on Windows. The default port is 5601. On Linux machine run the ``bin\kibana.sh`` file or refer to the [official guide here](https://www.elastic.co/guide/en/kibana/current/setup.html).
- If you have not imported the preconfigured Kibana settings which come with the application you need to do the index setup manually. Set up the index in Kibana, the default index name is ``ogc-statistics-index``and the timestamp field is ``@timestamp``. See your *Statistics Settings* page on the SOS admin Settings page for further details.

![Kibana set up index](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/kibana-index-setup.JPG)

- Head to the [discover mode](https://www.elastic.co/guide/en/kibana/current/discover.html) and check if you see any raw data there.

![Discover mode](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/discover-mode.JPG)

- With the default Kibana settings setup, click on the link below to view the bar chart of the number of counts by request types this year.

![Bar chart](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/kibana-countoperations.JPG)

- This chart describes for how long the requests were running. Most of the execution time were fast less than 100ms but there were some outliers like 900ms.
![Execution times](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/exec-ms-interval.JPG)

## Parameter

Here you can find the supported parameter: [parameter documentation](PARAMETERS.md)

## Security

Here you can find information about securing the statistics support: [security documentation](SECURITY.md)
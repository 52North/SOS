#Developer guide

## Architectural overview
The main entry point for collection of service statistics is the class `org.n52.sos.statistics.sos.SosStatisticsLoggerListener`. It receives the events from the `ServiceEventBus`.
Currently the following events are processed in the event listener.
 - RequestEvent
 - ExceptionEvent
 - ResponseEvent
 - OutgoingResponseEvent
 - CountingOutputstreamEvent
 
 These events can be fired during any client's request call. The
 - RequestEvent
 - OutgoingResponseEvent
 are always fired and the `OutgoingResponseEvent` is always the last event which comes in order.
 
 Based on the Java Servlet architecture every client request is serviced by only one java thread. These events are grouped together based on the `java.lang.Thread#getId`. The events doesn't directly
 implement the `org.n52.iceland.event.ServiceEvent` interface but they are subclass of the `org.n52.iceland.event.events.AbstractFlowEvent` class which contains the thread id.
 
 You can see an UML sequence diagram of one client request spawns multiple events.
![Statistics Events Processing](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/Statistics_event_processing.jpg)

##Processing incoming events
In the `SosStatisticsLoggerListener` event listener, the events are distributed to different **resolver** classes (which implement `org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver` interface) based on the event's concrete class. For example when a `RequestEvent` event is received it will be routed to the `SosRequestEventResolver` resolver. 

Some of the events' payload don't contain any SOS specific information so they are implemented in a different not sos specific package structure. For example the
`org.n52.sos.statistics.impl.resolvers.OutgoingResponseEventResolver` which is responsible for processing the `OutgoingResponseEvent`s are Iceland specific classes.

Within the **XXXResolver** classes for further processing the payload different processing classes are implemented called `XXXHandler`.
The `RequestEvent` contains payload as `org.n52.iceland.request.AbstractServiceRequest` type instances. The concrete classes of this
abstract AbstractServiceRequest are application specific (e.g: SOS specific: DescribeSensorRequest,GetCapabilitiesRequest, etc... ).
For such every concrete class a handler is specified e.g.: GetCapabilitiesRequestHandler which reads the important metrics data.

The **XXXHandler** classes must implement the IServiceEventHandler which return type is a `Map<String,Object>` type which is a persistable format for Elasticsearch.

The Elasticsearch client API automatically converts the Java Map to a JSON string, including the nested map objects, e.g. `Map<String,Map<String,Object>>`.


## Extending the module with new parameters
There can be a time in the near future when new parameters (e.g: some RequestEvent parameter) needs to be stored in Elasticsearch. Most of the primitive parameters can be one-to-one mapped from Java to Elasticsearch types like `String -> String` or `Long to Long` but some of them needs to be transformed like `Geometry -> Geo_Shape`.
These conversions are located [n52.sos.statistics.sos.models](https://github.com/lestarcdog/SOS/tree/feature/statistics/groupevents/statistics/src/main/java/org/n52/sos/statistics/sos/models)

The default mappings for all parameters can be found `ServiceDataMapping class`. The SOS specific parameters are located `SosDataMapping`. The funcionality of the parameters are implemented in the `org.n52.sos.statistics.api.parameters` package.

The parameters are hierarchical e.g: `OmObservation` has the `OmObservationConstellation` object and it has many primitive objects. So the primitive types which don't have any children corresponds to the class [SingleEsParameter](https://github.com/lestarcdog/SOS/blob/feature/statistics/groupevents/statistics/src/main/java/org/n52/sos/statistics/api/parameters/SingleEsParameter.java). The hierarchical ones for the [ObjectEsParameter](https://github.com/lestarcdog/SOS/blob/feature/statistics/groupevents/statistics/src/main/java/org/n52/sos/statistics/api/parameters/ObjectEsParameter.java)

The Elasticsearch mappings (which in the end will be transformed into JSON strings) can be given in many ways, this module uses the `java.util.Map<String,Object>` way. The [ElasticsearchTypeRegistry](https://github.com/lestarcdog/SOS/blob/feature/statistics/groupevents/statistics/src/main/java/org/n52/sos/statistics/api/parameters/ElasticsearchTypeRegistry.java) has the Elasticsearch types definition in that format. These types are rarely modified.

There is a "helper class" [ObjectEsParameterFactory](https://github.com/lestarcdog/SOS/blob/feature/statistics/groupevents/statistics/src/main/java/org/n52/sos/statistics/api/parameters/ObjectEsParameterFactory.java) which contains the composite `ObjectEsParameter`s. It is extensible with new methods and definitions.

To have an automatic parameter generation functionality the `Description` class has some parameters which gives indication about the parameter origin (in which Event was it transported), the operation (GetCapabilities) and a description message. The `PARAMETER.MD` file generator is at the separate module called statistics-generator. In the `package phase` the generation is called and the `PARAMETERS.MD` file is created in the `statistics` parent module.

To add a new parameter the following structure is needed. The description can be null the other fields are mandatory.

```java
public static final AbstractEsParameter GC_FORMATS_FIELD = 
new SingleEsParameter("getcapabilities-formats", 
                      new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Accept formats"),
                      ElasticsearchTypeRegistry.stringField);
```

### Elasticsearch Schema creation and Connection
If you want to integrate the iceland project based statistics module in a new project it is advised to create the Elasticsearch schema mapping beforehand you insert any data.
The data mapping creation and consistency check happens automatically on every application start. The information stored in one Elasticsearch index and in two Elasticsearch types:
- mt : for metadata automatically created client can't modify
- <user specified> : stores the application data

the metadata type contains the schema version, connected deployments unique id and additional timestamp information about creation and modification. If the webapplication has a different Elasticsearch schema then in the `mt` type it won't insert any data and the user must resolve manually the migration (probably by creating a new Elasticsearch index.)
It is the developer responsibility to increment the schema version on breaking schema changes in the subclass by returning `DefaultElasticsearchSchemas#getSchemaVersion` a greater number then before.
Breaking changes:
- Deleting a paremeter
- Changing an existing parameter's type

The setting up the Elasticsearch connections and schema creation happens in the `org.n52.sos.statistics.impl.ElasticsearchAdminHandler` class. 

There are properties which are default to all schema those settings are stored in the `org.n52.sos.statistics.impl.schemabuilders.DefaultElasticsearchSchemas` abstract class. The developer should subclass this class and call the `DefaultElasticsearchSchemas#processSchemaClass` method in his/her subclass. The input parameter is a list of `Class`s which contains `static public final AbstractEsParameter` class variables. (For more information how to setup a data mapping schema file see the chapter "Extending the module with new parameters")

###Kibana exporter/importer
The statistics module ships with preconfigured Kibana dashboards and visualizations. Kibana after the 4.x.x version stores it's application data in Elasticsearch defaults to the index name ".kibana"
The Kibana importer job to parse the configuration file which were created beforehand and saved under the file `/src/main/resources/kibana/kibana_config.json`. It has a custom json schema format because we need more information what the factory Kibana importer exporter provide. The importer activates on the startup if the user enabled the function on the settings page. The importer takes consideration the different Elasticsearch types names between the export development environment and import production environment and it processes the config file before. 

The Kibana exporter is intended for in-house use only! It is a java runnable jar and exports the `kibana_config.json` file.

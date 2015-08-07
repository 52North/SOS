#Developer guide

## Architectural overview
The main entry point for collection of service statistics is the class `org.n52.sos.statistics.sos.SosStatisticsServiceEventListener`. It receives the events from the `ServiceEventBus`.
Currently the following events are processed in the statistics event listener.

 - ``RequestEvent``
 - ``ExceptionEvent``
 - ``ResponseEvent``
 - ``OutgoingResponseEvent``
 - ``CountingOutputstreamEvent``
 
These events can be fired _in that order_ during one client's request call. The ``RequestEvent`` and ``OutgoingResponseEvent`` are always fired, and the latter is always the last event.
 
Based on the Java Servlet architecture every client request is serviced by only one Java thread. These events are grouped together based on the `java.lang.Thread#getId`. The events do not directly implement the `org.n52.iceland.event.ServiceEvent` interface but they are subclass of the `org.n52.iceland.event.events.AbstractFlowEvent` class, which contains the thread id.
 
You can see an UML sequence diagram of one client request spawns multiple events.
![Statistics Events Processing](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/Statistics_event_processing.jpg)


##Processing incoming events
In the `org.n52.sos.statistics.sos.SosStatisticsServiceEventListener` event listener, the events are distributed to different **resolver classes** (which implement `org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver` interface) based on the event's concrete class. For example when a `RequestEvent` event is received it will be routed to the `SosRequestEventResolver` resolver. The resolver map the contents of a service event to a data structure that can be loaded to the statistics database and persists the .

Some of the events' payloads do not contain any SOS specific information so they are implemented in an independent package structure. For example the
`org.n52.sos.statistics.impl.resolvers.OutgoingResponseEventResolver`, which is responsible for processing the `OutgoingResponseEvent`s, are Iceland-specific classes.

Within the **XxxResolver** classes, different processing classes for each possible payload are implemented as `XXXHandler`s. The `RequestEvent` contains payload as instances of `org.n52.iceland.request.AbstractServiceRequest`. The concrete classes of this abstract AbstractServiceRequest are application-specific (e.g. for the SOS these are DescribeSensorRequest, GetCapabilitiesRequest, etc.). For every concrete class a handler is specified, e.g. ``GetCapabilitiesRequestHandler``, which reads the relevant metrics data.

The **XXXHandler** classes must implement the IServiceEventHandler interface which return type is a `Map<String,Object>` type which is a persistable format for Elasticsearch.

The Elasticsearch client API automatically converts the Java Map to a JSON string, including the nested map objects, e.g. `Map<String,Map<String,Object>>`.

##Adding new statistics Resolvers and Handlers
- The **resolvers** processing a `ServiceEvent` subclass and then it routes the event's payload to the appropriate handler. The Resolvers must implement the `IServiceEventResolver<T extends ServiceEvent>` interface. 
- The **handlers** processing the payload (or in some cases the event class itself). The Handlers must implement the `IServiceEventHandler<T>`.

Finding your appropriate **handler** in your Java **resolver** class have a helping utility class called `EventHandlerFinder#findHandler`.
use it like this:
```java
//"handlers" is a `Map<String,IServiceEventHandler<T>>` and are previously injected via SpringFramework
//"payload" is the payload of the event (or in some cases the event class itself)
IServiceEventHandler<AbstractServiceResponse> handler = EventHandlerFinder.findHandler(payload, handlers);
```

####Registering the handler for an existing resolver
If you would like to process an event's payload in a new way you can register a handler to an event resolver the following way.

- Open up the appropriate event configuration xml file `src/main/resources/imports/`.
- Instantiate your handler as Spring bean `prototype`.
- Add your handler to the resolvers handler property as a map entry
	- **Key :** The key must be exactly match the simple name of the payload's class (the generic type of your `IServiceEventHandler<T>`)
	e.g.: processing the `org.n52.sos.request.DescribeSensorRequest` data in a handler would look like this as a map entry
	```xml
	<!-- describeSensorRequestHandler is the handler class previously initiated as a prototype bean -->
	<entry key="DescribeSensorRequest" value-ref="describeSensorRequestHandler" />
	```
	**There is one special key `default`** if no handlers found it will be a backup handler for processing the data. If no handlers found for the data NPE is thrown and the processing is stopped.
	- **Value :** your newly created handler.


####Registering the new resolver
This is only necessary when you want to process a new event type which extends the `ServiceEvent` class.

- First register the new event class that will be returned in the `SosStatisticsServiceEventListener#getTypes`
- Add a new spring configuration xml file under the `src/main/resources/imports/` folder with the name of your event (or any name you want).
- Initiate your resolver bean as a `prototype` Spring bean
- Add your handlers as described in the previous chapter.
- Go to the `configured-context.xml` under `src/main/resources` and import your newly created xml file e.g:
```xml
	<import resource="imports/countingoutputstrem-event.xml"/>
``` 
- Create a new method which returns your new resolver bean in the `SosStatisticsResolverFactory` class. e.g.:
```java
    public SosResponseEventResolver getMyNewEventResolver() {
        return ctx.getBean(MyNewEventResolver.class);
    }
```
- Finally add a new if-else branch in `SosStatisticsServiceEventListener#addEventToResolver` class where you test for your new event class. e.g
```java
 } else if (event instanceof MyNewEvent) {
            MyNewEventResolver responseEventResolver = resolverFactory.getMyNewEventResolverResolver();
            myNewEventResolver.setEvent((MyNewEvent) event);
            evtResolver = myNewEventResolver;
}
```


## Extending the module with new parameters
New parameters (e.g: some RequestEvent parameter or service extension) can be stored in Elasticsearch as follows:

- If there is a new event class, add a new resolver implementation.
- If there is a new payload for an event, e.g. a new service request, add a new handler to the respective resolver.
- If there is a new field in a concrete request, extend the existing handler or implement a new handler, replacing the existing one.

Within the handlers, you have to "translate" from the service request to an Elasticsearch-compatible data structure. Most of the primitive parameters can be mapped directly from Java classes to Elasticsearch types like `String -> String` or `Long -> Long` but some of them needs to be converted, such as `Geometry -> Geo_Shape`. These conversions are located in ``n52.sos.statistics.sos.models``.

The default mappings for all parameters can be found `org.n52.sos.statistics.api.mappings.ServiceEventDataMapping`. The SOS specific parameters are located in `org.n52.sos.statistics.sos.SosDataMapping`. The different variants of parameters are implemented in the `org.n52.sos.statistics.api.parameters` package.

The parameters are hierarchical. `OmObservation` contains the `OmObservationConstellation` object which has many primitive objects. The primitive types, which don't have any children, correspond to the class ``org.n52.sos.statistics.api.parametersSingleEsParameter``. The hierarchical ones use the ``org.n52.sos.statistics.api.parameters.ObjectEsParameter``.

The Elasticsearch mappings (which in the end will be transformed into JSON strings) can be given in many ways. The current implementation uses a `java.util.Map<String,Object>`. The ``org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry`` provides the Elasticsearch types definition in that format. These types are rarely modified.

There is a helper class ``org.n52.sos.statistics.api.parameters.ObjectEsParameterFactory`` which contains construction methods for the composite `ObjectEsParameter`s. It is extensible with new methods and definitions.

To have an automatic parameter generation functionality, the `org.n52.sos.statistics.api.parameters.Description` class has some parameters which gives indication about the parameter origin (i.e. in which event it was provided), the operation (e.g. GetCapabilities) and a description message. The `PARAMETERS.MD` file generator is in a separate Maven module called ``statistics-generator``, which is activated during the ``mvn package`` phase and creates an up-to-date human readable list of the parameter names in the `statistics` parent module.

To add a new parameter the following structure is needed. The description can be null, the other fields are mandatory.

```java
public static final AbstractEsParameter GC_FORMATS_FIELD = 
new SingleEsParameter("getcapabilities-formats", 
                      new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Accept formats"),
                      ElasticsearchTypeRegistry.stringField);
```

## Elasticsearch connection and schema creation

The setting up of Elasticsearch connections and schema creation is encapsulated in the class `org.n52.sos.statistics.impl.ElasticsearchAdminHandler`.

If you want to integrate the iceland project based statistics module in a new project, it is advised to create the Elasticsearch schema mapping before you insert any data.

The data mapping creation and consistency check happens automatically on every application start. The information stored in one Elasticsearch index and in two Elasticsearch types:
- ``mt`` : for metadata that the automatically created client can't modify
- ``"user specified"`` : stores the application data, the "usage statistics"

The metadata type contains the statistics module _schema version_, the _unique id of connected service instances_ (i.e. deployed services that send their statistics to this Elasticsearch index), and an additional _timestamp_ information about creation and modification. If the webapplication has a different schema version form the one that `mt` type announces, then it _will not insert any data_ and the user must resolve any migration manually, probably by creating a new Elasticsearch index.

### Adding a new schema

It is the developer responsibility to increment the schema version on breaking schema changes by returning a larger number than `DefaultElasticsearchSchemas#getSchemaVersion()*, or any other schemas that might exist.

Breaking changes:
- Deleting a parameter
- Changing an existing parameter's type

There are properties which are default to all schemata. These settings are stored in the `org.n52.sos.statistics.impl.schemabuilders.DefaultElasticsearchSchemas` abstract class. The developer should subclass this class and call the `DefaultElasticsearchSchemas#processSchemaClass` method in her subclass. The input parameter is a list of `Class`s which contains `static public final AbstractEsParameter` class variables. For more information how to set up a data mapping schema file see the chapter "Extending the module with new parameters".

## Kibana exporter/importer
The statistics module ships with preconfigured Kibana dashboards and visualizations. Kibana (>= 4.x) stores its application data in Elasticsearch by default to the index named ".kibana".

The project contains a Kibana importer job to parse the configuration file, which were created beforehand and saved under the file `/src/main/resources/kibana/kibana_config.json`, and store the predefined dashboards and visualizations in the index to make them directly available to users.

The file has a custom JSON schema format, because we need more information than what the factory Kibana importer exporter provides. The importer activates on startup if the user enabled the function on the settings page. The importer considers the different Elasticsearch types names between the export development environment and import production environment and it processes the config file before. 

**The standalone Kibana exporter is intended for in-house use only!** It is a Java runnable jar, must be executed manually, and creates the `kibana_config.json` file. The input arguments for the main class is the development Elasticsearch cluster address and the cluster name
```bash
java -jar kibana-exporter.jar localhost:9300 ogc-statistics-cluster
```

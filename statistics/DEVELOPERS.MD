#Developer guide

## Architectural overview
The main entry point for collection of service statistics is the class `org.n52.sos.statistics.sos.SosStatisticsLoggerListener`.
 It receives the events from the `ServiceEventBus`.
 
 You can see an UML sequence diagram of the client calling a `DescribeSensorRequest`
![DescribeSensorRequest](https://wiki.52north.org/pub/Projects/GSoC2015Statistics4Ows/DescribeSensorRequest.jpg)
 
 Currently the following events are processed in the event listener.
 - RequestEvent
 - ExceptionEvent
 - ResponseEvent
 - OutgoingResponseEvent
 - CountingOutputstreamEvent

In the event listener, the events are distributed to different **resolver** classes (which implement `org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver`) based on the event's concrete class. For example when a `RequestEvent` event is received it will be routed to the `SosRequestEventResolver` resolver. 

Some of the events doesn't contain any SOS specific information so they are implemented in a different package **TODO after refactoring where**. For example the
`OutgoingResponseEventResolver` which is responsible for processing the `OutgoingResponseEvent`s.

Within the **resolver** for further processing the payload different processing classes are implemented called `XXXHandler`.
The `RequestEvent` contains payload as `org.n52.iceland.request.AbstractServiceRequest` type instances. The concrete classes of this
abstract AbstractServiceRequest are SOS specific in this setting (e.g: DescribeSensorRequest,GetCapabilitiesRequest, etc... ). For such every concrete class a handler is specified e.g.: GetCapabilitiesRequestHandler which reads the important metrics data.

The **XXXHandler** classes must implement the IServiceEventHandler which return type is a `Map<String,Object>` type which is a persistable format for Elasticsearch.

In the handlers, the data is extracted from the provided objects and a `Map<String,Object>` is returned.

This `Map<String,Object>` is persisted to the statistics database, which is based on Elasticsearch.

The Elasticsearch client API automatically converts the map to a JSON string, including nested map objects, e.g. `Map<String,Map<String,Object>>`.


#### Extending the module with new parameters
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


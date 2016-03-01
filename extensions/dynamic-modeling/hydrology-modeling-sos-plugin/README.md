# 52°North SOS - Hydrology Modeling Extension
README file of the Hydrology Modeling Extension for the 52°North Sensor Observation Service (SOS)

This is hosted in the repository of the [52°North Sensor Observation Service (SOS)][1].

This 52°North SOS extension integrates Hydrologic models in the 52°North Sensor Observation Service (SOS)
adding their entities as "virtual" SOS objects (Offerings, Procedures, FOIs, Observations and so on). 
Now, only [EPANET network models][6] are supported but other types can be added (e.g. [SWMM][8], [HEC-RAS][9]).

The plugin solves the models on-the-fly and gets their simulation results to inject them to output WEB responses.
The hydraulical results are dynamically inserted using a mechanism of virtual injection of data in order to 
avoid to save them in the main database managed by the 52°North SOS service.

All entities registered in each hydraulical model, and their respective simulation results, will create on-the-fly
a set of available -Offering/Procedure/FeatureOfInterest/Observation/Timeseries- objects.

The plugin is configured using a settings file hosted in `WEB-INF\dynamic-models` folder. 
Each entry in this file defines the properties of a specific model (mainly the location of the network file).

We can define an entry for an [EPANET network model][6] similar to:

    <model class="org.n52.sos.extensions.hydrology.epanet.EpanetModel">
      <name>Net1</name>
      <description>EPANET Example Network 1 - inptools. A simple example.</description>
      <objectFilter>*:*.*</objectFilter>
      <relatedFeatureExpression></relatedFeatureExpression>
      <fileName>hydrology/Net1.inp</fileName>
      <srid>4326</srid>
      <networkSolver class="org.n52.sos.extensions.hydrology.epanet.BaseformEpanetSolver" />
    </model>

  Where:

    * class: Java class used to manage a model.
    * name: Name of the model (Unique).
    * description: Label or description of the model.
    * objectFilter: Optional filter to hide network objects in the "GetCapabilites" response to avoid huge responses.
      We can define filters with this pattern: 
      epanet_object_type[;epanet_object_type]:object_id[;object_id].property_mame[;property_mame].
    * fileName: Path of the INP file.
    * srid: ReferenceSystem ID of the coordinates of the objects managed.
    * relatedFeatureExpression: text or url to insert in definition of the "virtual" offering to create (Optional).
    * networkSolver: Java class used to solve the hydraulical network.

  As previously it said, only [EPANET network models][6] are supported. Now, the plugin provides a solver for EPANET models 
  using the [Baseform-Epanet-Java-Library][7].


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
  * `/src` :                      The source files of this 52°North SOS extension
  * `README` :                    This file
  * `..\hydrology-core-library` : Core library to manage Hydraulic models

### Installation

No printer friendly installation guide exist for this release. Instead, refer to the [wiki documentation][5].


## Contributing

You are interesting in contributing the 52°North SOS and you want to pull your changes to the 52N repository to make it available to all?

In that case we need your official permission and for this purpose we have a so called contributors license agreement (CLA) in place. 
With this agreement you grant us the rights to use and publish your code under an open source license.

A link to the contributors license agreement and further explanations are available here: 

    http://52north.org/about/licensing/cla-guidelines


## Work remaining

  * Other hydraulic model types can be added (e.g. [SWMM][8] or [HEC-RAS][9] models), new java classes are needed to manage them.
  * Simulation of models with on-the-fly modifications would be possible implementing a mechanism (e.g. WPS requests) to notify the changes to the solver.


--
[1]: http://52north.org/communities/sensorweb/sos/index.html
[2]: https://portal.opengeospatial.org/files/?artifact_id=47599
[3]: http://www.ogcnetwork.net/ows-9
[4]: http://cite.opengeospatial.org/test_engine
[5]: https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation
[6]: http://www.epa.gov/water-research/epanet
[7]: https://github.com/Baseform/Baseform-Epanet-Java-Library
[8]: http://www.epa.gov/water-research/storm-water-management-model-swmm
[9]: http://www.hec.usace.army.mil/software/hec-ras

# 52°North SOS [![OpenHUB](https://www.openhub.net/p/SensorObservationService/widgets/project_thin_badge.gif)](https://www.openhub.net/p/SensorObservationService)
README file for the 52°North Sensor Observation Service (SOS) version 4.3

This is the repository of the [52°North Sensor Observation Service (SOS)][1].

The 52°North SOS is a reference implementation of the
[OGC Sensor Observation Service specification (version 2.0)][2]. It was
implemented during the [OGC Web Services Testbed,  Phase 9 (OWS-9)][3] and
tested  to be compliant to this specification within the [OGC CITE testing][4]
in December of 2012.

## Build Status
* Master: [![Master Build Status](https://travis-ci.org/52North/SOS.png?branch=master)](https://travis-ci.org/52North/SOS)
* Develop: [![Develop Build Status](https://travis-ci.org/52North/SOS.png?branch=develop)](https://travis-ci.org/52North/SOS)

## Branches

This project follows the  [Gitflow branching model](http://nvie.com/posts/a-successful-git-branching-model/). "master" reflects the latest stable release.
Ongoing development is done in branch [develop](../../tree/develop) and dedicated feature branches (feature-*).

## Code Compilation

This project is managed with Maven3. Simply run `mvn clean install`
to create a deployable .WAR file.

## Distributions

Here you can find some information that relates to the distributions of the 52°North SOS.

### Download

The latest release of 52°North SOS can be downloaded from this website:

    http://52north.org/downloads/sensor-web/sos

### Contents
  * `/src` :                 The source files of 52°North SOS modules
  * `/bin` :                 Executable binary of 52°North SOS webapp module
  * `LICENSE` :              The license of 52°North SOS
  * `NOTICE` :               Third Party libraries and their licenses
  * `README` :               This file
  * `RELEASE-NOTES` :        The release notes of the 52°North SOS

No printer friendly documentation exist for this release. Instead, refer to the [wiki documentation][5].

### Installation

No printer friendly installation guide exist for this release. Instead, refer to the [wiki documentation][5].

## Contributing

You are interesting in contributing the 52°North SOS and you want to pull your changes to the 52N repository to make it available to all?

In that case we need your official permission and for this purpose we have a so called contributors license agreement (CLA) in place. With this agreement you grant us the rights to use and publish your code under an open source license.

A link to the contributors license agreement and further explanations are available here: 

    http://52north.org/about/licensing/cla-guidelines


## Support and Contact

You can get support in the community mailing list and forums:

    http://52north.org/resources/mailing-lists-and-forums/

If you encounter any issues with the software or if you would like to see
certain functionality added, let us know at:

 - Carsten Hollmann (c.hollmann@52north.org)
 - Christian Autermann (c.autermann@52north.org)
 - Eike Hinderk Jürrens [@EHJ-52n](e.h.juerrens@52north.org)

The Sensor Web Community

52°North Inititative for Geospatial Open Source Software GmbH, Germany

--
[1]: http://52north.org/communities/sensorweb/sos/index.html
[2]: https://portal.opengeospatial.org/files/?artifact_id=47599
[3]: http://www.ogcnetwork.net/ows-9
[4]: http://cite.opengeospatial.org/test_engine
[5]: https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation

# 52°North Sensor Observation Service (SOS)
| Master | Develop | OpenHUB | LGTM Alerts | LGTM code quality | dependabot |
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
| [![Master Build Status](https://travis-ci.org/52North/SOS.png?branch=master)](https://travis-ci.org/52North/SOS) | [![Develop Build Status](https://travis-ci.org/52North/SOS.png?branch=develop)](https://travis-ci.org/52North/SOS) | [![OpenHUB](https://www.openhub.net/p/SensorObservationService/widgets/project_thin_badge.gif)](https://www.openhub.net/p/SensorObservationService) | [![Total alerts](https://img.shields.io/lgtm/alerts/g/52North/SOS.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/52North/SOS/alerts/) | [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/52North/SOS.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/52North/SOS/context:java) | [![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=52North/SOS)](https://dependabot.com) |

## Description

### Standardized, web-based upload and download of sensor data and sensor metadata

**The 52°North Sensor Observation Service (SOS) provides an interoperable web-based interface for inserting and querying sensor data and sensor descriptions. It aggregates observations from live in-situ sensors as well as historical data sets (time series data).**

The 52°North SOS is a reference implementation of the
[OGC Sensor Observation Service specification (version 2.0)](https://portal.opengeospatial.org/files/?artifact_id=47599), an interoperable interface for publishing and querying sensor data and metadata. 
It was implemented during the [OGC Web Services Testbed,  Phase 9 (OWS-9)](http://www.ogcnetwork.net/ows-9) 
and tested to be compliant to this specification within the [OGC CITE testing](http://cite.opengeospatial.org/test_engine) in December of 2012.

The 52°North SOS enables the user to:

 - insert and retrieve georeferenced observation data
 - access georeferenced measurement data in a standardized format (ISO/OGC Observation and Measurement - O&M 2.0, OGC WaterML 2.0)
 - insert and retrieve sensor descriptions (encoded according to the OGC SensorML standard - SML 1.0.1, SML 2.0)
 - publish measurement data (near real-time, as well as archived data) 

An extension accommodates additional INSPIRE Directive requirements, thus ensuring interoperable exchange of any kind of observation data across political, administrative and organizational boundaries. Client applications, such as [Helgoland](http://www-neu.52north.org/software/software-projects/helgoland/) enable analysis and visualization of the measurement data provided via the SOS server.
 
### Features

  - [INSPIRE Download Service](http://inspire.ec.europa.eu/id/document/tg/download-sos) for measurement data
  - [Guidelines for the use of Observations & Measurements and Sensor Web Enablement-related standards in INSPIRE (D2.9)](http://inspire.ec.europa.eu/id/document/tg/d2.9-o%26m-swe)
  - [OGC Hydro Profile] (http://docs.opengeospatial.org/bp/14-004r1/14-004r1.html), including GetDataAvailability operation
  - [SensorML 1.0.1 and 2.0](http://www.opengeospatial.org/standards/sensorml)
  - [WaterML 2.0](http://www.opengeospatial.org/standards/waterml)
  - [AQD e-Reporting flows E](https://www.eionet.europa.eu/aqportal/requirements/dataflows)
  - Multiple DB support (by using the [Hibernate ORM framework](http://hibernate.org/orm/))
  - Bundle including [Sensor Web Server Helgoland](https://github.com/52North/sensorweb-server-helgoland) and [helgoland](https://github.com/52North/helgoland/)
  - DeleteObservation operation, to delete observation by identifier (not part of the SOS 2.0 specification)
  - [Efficient XML Interchange (EXI) 1.0 format](http://52north.org/communities/sensorweb/sos/index.html#www.w3.orgTRexi)
  - Support for [GoundWaterML 2 GeologyLog observation](http://www.opengeospatial.org/standards/gwml2)
  
### Future work

As of version 5.x, the 52N SOS's database model and DAO layer will be merged with the [52N Sensor Web Server Helgoland](https://github.com/52North/sensorweb-server-helgoland) to provide an easy-to-install and easy-to-configure service. This service will provide an OGC SOS 2.0 interface and 52N Sensor WEB REST-API to easily access the observed data via a restful interface.

The database models of both services will be harmonized. A simple basic database model will provide the main features of the SOS and the Sensor WEB REST-API and will be easily extendable.

The Data Access Objects (DAO) layer will also be harmonized to provide a single point for accessing and manipulating the data in the database. This will reduce the maintenance and the SOS and the REST interface will easily support new database features.

In the [Sensor Web Server database model](https://github.com/52North/sensorweb-server-db-model) the harmonized database model and the DAO layer will be provided as a separate project for use in the future 52N SOS and 52N Sensor WEB REST-API implementations. Additionally the [52N Sensor Web Server Helgoland Adapters](https://github.com/52North/sensorweb-server-helgoland-adapters) will also use this project to persist the metadata of the harvested SOS services. 

## Quick Start

 Getting started - the [installation guide](https://wiki.52north.org/SensorWeb/SensorObservationServiceVDocumentation#Installation) helps you install and configure the 52°North SOS.
 
### Docker

Docker images are automatically deployed to [Docker Hub](https://hub.docker.com/r/52north/sos).

The configuration is located in `/etc/sos`. The directory is per default a volume and when no other directory is mounted (see also the [Docker documentation](https://docs.docker.com/storage/) for pre-populated volumes) a default configuration using H2 is used. This default installation uses the admin credentials `admin:password`. If you want to install the SOS, you have to mount a empty directory instead of the default volume.

The cache file is located in `/var/lib/jetty/webapps/ROOT/tmp`. The directory is per default a volume.

A custom [Helgoland](https://github.com/52North/helgoland) `settings.json` can be mounted to `/var/lib/jetty/webapps/ROOT/static/client/helgoland/settings.json`. This is needed, e.g when the external URL of the SOS differs from `http://localhost:8080/`.

#### Examples

Default:
```sh
docker run -p 8080:8080 52north/sos:latest
```

With a local configuration folder:
```sh
docker run -p 8080:8080 -v ./config:/etc/sos 52north/sos:latest
```

Remote debugging enabled:
```sh
docker run -p 8080:8080 -p 8000:8000 -e 'JAVA_OPTIONS=-Xdebug -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n' 52north/sos:latest
```

## User Guide

 An Open Geospatial Consortium (OGC) SOS [tutorial](http://www.ogcnetwork.net/SOS_2_0/tutorial) is currently not available, see http://www.ogcnetwork.net.

## Demo

 Access a basic form-based test client and the administrative backend at our [demo site](http://sensorweb.demo.52north.org/sensorwebtestbed/).

## License

 The 52°North SOS is published under the [GNU General Public License, Version 2 (GPLv2)](http://www.gnu.org/licenses/gpl-2.0.html)

## Changelog

 The latest changes, additions, bugfixes, etc. can be found in the [RELEASE-NOTES](https://github.com/52North/SOS/blob/website-markdowns/RELEASE-NOTES)

## References

 - [IRCEL-CELINE](http://www.irceline.be/) (Belgium): *Current and archived air quality data for all of Belgium*
 - [Wupperverband](https://www.wupperverband.de/) (Germnay): *Regional water board providing a multitude of hydrological measurment data with the help of SOS standards*
 - [Swedish EPA](http://www.swedishepa.se/)/[IVL](http://www.ivl.se/)/[SMHI](http://www.smhi.se/) (Schweden): *Current and archived air quality data for all of Sweden, as well as delivery this data to the European Protection Agency*
 - [RIVM](http://www.rivm.nl/) (Netherlands): *Current and archived air quality data for all of the Netherlands*
 - [Lithuanian EPA](http://gamta.lt/cms/index?lang=en) (Lithuania): *Current and archived air quality data for all of Lithuania*
 - [European Environment Agency (EEA)](http://www.eea.europa.eu/): *Use of SOS interface to collect data from the member countries, as well as to publish the collective data*
 - [PEGELONLINE](https://www.pegelonline.wsv.de/) (Germany): *Interoperable publication of te federal waterways' hydrological measurment data.*

## Credits

### Contributors

The development the 52°North Sensor Observation Service implementations was contributed by

| Name | Organisation |
| ------------- | :-------------: |
| [Carsten Hollmann](http://52north.org/about/52-north-team/25-carsten-hollmann) | [52&deg;North](http://52north.org) |
| [Eike Hinderk J&uuml;rrens](http://52north.org/about/52-north-team/14-eike-hinderk-juerrens) | [52&deg;North](http://52north.org) |
| [Christian Autermann](http://52north.org/about/52-north-team/30-autermann-christian) | [52&deg;North](http://52north.org) |
| [Christoph Stasch](http://52north.org/about/52-north-team/31-stasch-christoph) | [52&deg;North](http://52north.org) |
| Shane StClair | [Axiom Data Science](http://www.axiomdatascience.com) |
| Victor Gonz&aacute;lez | [geomati.co](http://geomati.co/en) |
| Oscar Fonts | [geomati.co](http://geomati.co/en) |
| Carlos Giraldo | [Instituto Tecnol&oacute;gico de Galicia (ITG)](http://www.itg.es/) |
| Alexander Kmoch | [Z_GIS](http://www.zgis.at),  [Universit&auml;t Salzburg</a> (Austria and Germany)](http://www.uni-salzburg.at) |
| Carl Schroedl | Center for Integrated Data Analytics ([CIDA](http://cida.usgs.gov)), [USGS](http://www.usgs.gov) |
| Jordan Walker | Center for Integrated Data Analytics ([CIDA](http://cida.usgs.gov)), [USGS](http://www.usgs.gov) |

### Contributing organizations

The development the 52°North Sensor Observation Service implementations was contributed by several organizations

<p align="center"><a target="_blank" href="http://52north.org"><img alt="52N" align="middle" width="286" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/52n-logo-220x80.png" /></a></p>

| | | | |
| :-------------: | :-------------: | :-------------: | :-------------: |
| <a target="_blank" href="http://www.uni-muenster.de/Geoinformatics/en/index.html"><img alt="IfGI"  align="middle" width="200" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/logo_ifgi.png"/></a> | <a target="_blank" href="http://www.axiomdatascience.com"><img alt="Axiom Data Science"  align="middle" width="85" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/axiom.png"/></a> | <a target="_blank" href="http://geomati.co"><img alt="geomati.co"  align="middle" width="85" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/logo_geomatico_256.png"/></a> | <a target="_blank" href="http://www.itg.es/"><img alt="ITG"  align="middle" width="104" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/Logo_ITG_vectorizado.png"/></a> |
| <a target="_blank" href="http://www.zgis.at"><img alt="ZIGS"  align="middle" width="128" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/UniSalzburgZGIS_1.jpg"/></a> | <a href="http://52north.org/about/licensing/cla-guidelines">Your logo?!<br/>Get involved!</a> | <img alt="Placeholder" align="middle" width="85" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/placeholder.png"/> | <img alt="Placeholder" align="middle" width="85" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/contribution/placeholder.png"/> | | |


### Funding organizations/projects

The development the 52°North Sensor Observation Service implementations was supported by several organizations and projects. Among other we would like to thank the following organisations and project

| Project/Logo | Description |
| :-------------: | :------------- |
| <a target="_blank" href="https://cos4cloud-eosc.eu/"><img alt="Cos4Cloud - Co-designed citizen observatories for the EOS-Cloud" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/cos4cloud.png" /></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">European Union’s Horizon 2020</a> research project <a target="_blank" href="https://cos4cloud-eosc.eu/">Cos4Cloud</a> (co-funded by the European Commission under the grant agreement n&deg;863463) |
| <a target="_blank" href="https://bmbf.de/"><img alt="BMBF" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/bmbf_logo_en.png"/></a><a target="_blank" href="http://tamis.kn.e-technik.tu-dortmund.de/"><img alt="TaMIS - Das Talsperren-Mess-Informations-System" align="middle"  src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/TaMIS_Logo_small.png"/></a> |  The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://www.bmbf.de/"> German Federal Ministry of Education and Research</a> research project <a target="_blank" href="http://tamis.kn.e-technik.tu-dortmund.de/">TaMIS</a> (co-funded by the German Federal Ministry of Education and Research, programme Geotechnologien, under grant agreement no. 03G0854[A-D]) |
| <a target="_blank" href="https://www.jerico-ri.eu/"><img alt="JERICO-S3 - Science - Services- Sustainability" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/jerico_s3.png" /></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">European Union’s Horizon 2020</a> research project <a target="_blank" href="https://www.jerico-ri.eu/">JERICO-S3</a> (co-funded by the European Commission under the grant agreement n&deg;871153) |
| <a target="_blank" href="http://www.nexosproject.eu/"><img alt="NeXOS - Next generation, Cost-effective, Compact, Multifunctional Web Enabled Ocean Sensor Systems Empowering Marine, Maritime and Fisheries Management" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_nexos.png" /></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a target="_blank" href="http://www.nexosproject.eu/">NeXOS</a> (co-funded by the European Commission under the grant agreement n&deg;614102) |
| <a target="_blank" href="https://bmbf.de/"><img alt="BMBF" align="middle"  src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/bmbf_logo_en.png"/></a><a target="_blank" href="https://colabis.de/"><img alt="COLABIS - Collaborative Early Warning Information Systems for Urban Infrastructures" align="middle"  src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/colabis.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://www.bmbf.de/"> German Federal Ministry of Education and Research</a> research project <a target="_blank" href="https://colabis.de/">COLABIS</a> (co-funded by the German Federal Ministry of Education and Research, programme Geotechnologien, under grant agreement no. 03G0852A) |
| <a target="_blank" href="https://www.bmvi.de/"><img alt="BMVI" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/bmvi-logo-en.png"/></a><a target="_blank" href="https://www.bmvi.de/DE/Themen/Digitales/mFund/Ueberblick/ueberblick.html"><img alt="mFund" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/mFund.jpg"/></a><a target="_blank" href="http://wacodis.fbg-hsbo.de/"><img alt="WaCoDis - Water management Copernicus services for the determination of substance inputs into waters and dams within the framework of environmental monitoring" align="middle" width="126" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/wacodis-logo.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://www.bmvi.de/"> German Federal Ministry of of Transport and Digital Infrastructure</a> research project <a target="_blank" href="http://wacodis.fbg-hsbo.de/">WaCoDis</a> (co-funded by the German Federal Ministry of Transport and Digital Infrastructure, programme mFund) |
| <a target="_blank" href="https://bmbf.de/"><img alt="BMBF" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/bmbf_logo_neu_eng.png"/></a><a target="_blank" href="https://www.fona.de/"><img alt="FONA" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/fona.png"/></a><a target="_blank" href="https://colabis.de/"><img alt="Multidisciplinary data acquisition as the key for a globally applicable water resource management (MuDak-WRM)" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/mudak_wrm_logo.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://www.bmbf.de/"> German Federal Ministry of Education and Research</a> research project <a target="_blank" href="http://www.mudak-wrm.kit.edu/">MuDak-WRM</a> (co-funded by the German Federal Ministry of Education and Research, programme FONA) |
| <a target="_blank" href="https://www.seadatanet.org/About-us/SeaDataCloud/"><img alt="SeaDataCloud" align="middle" width="156" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/LOGO_SDC_Layer_opengraphimage.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">Horizon 2020</a> research project <a target="_blank" href="https://www.seadatanet.org/About-us/SeaDataCloud/">SeaDataCloud</a> (co-funded by the European Commission under the grant agreement n&deg;730960) |
| <a target="_blank" href="http://www.odip.org"><img alt="ODIP II - Ocean Data Interoperability Platform" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/odip-logo.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">Horizon 2020</a> research project <a target="_blank" href="http://www.odip.org/">ODIP II</a> (co-funded by the European Commission under the grant agreement n&deg;654310) |
| <a target="_blank" href="http://inspire.ec.europa.eu"><img alt="INSPIRE" align="middle" width="60" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/inspire-logo.jpg" /></a> <a target="_blank" href="http://ec.europa.eu/isa/"><img alt="ISA" align="middle" width="60" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/ISALogo.png" /></a> <a target="_blank" href="http://ec.europa.eu/isa/actions/01-trusted-information-exchange/1-17action_en.htm"><img alt="ARE3NA" align="middle" width="60" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/ARe3NA.png"/></a> | The enhancements to make the 52&deg;North SOS an <a target="_blank" href="http://inspire.ec.europa.eu/">INSPIRE</a> compliant Download Service were funded by the <a target="_blank" href="http://ec.europa.eu/dgs/jrc/">JRC</a> under the <a target="_blank" href="http://ec.europa.eu/isa/">ISA</a> Programme's Action 1.17: A Reusable INSPIRE Reference Platform (<a target="_blank" href="http://ec.europa.eu/isa/actions/01-trusted-information-exchange/1-17action_en.htm">ARE3NA</a>). |
| <a target="_blank" href="http://www.ioos.noaa.gov"><img alt="IOOS - Integrated Ocean Observing System" align="middle" width="156" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_ioos.png"/></a> | The <a target="_blank" href="http://www.ioos.noaa.gov">IOOS</a> project with the mission: <br/>Lead the integration of ocean, coastal, and Great Lakes observing capabilities, in collaboration with Federal and non-Federal partners, to maximize access to data and generation of information products, inform decision making, and promote economic, environmental, and social benefits to our Nation and the world. |
| <a target="_blank" href="http://www.brgm.fr/"><img alt="BRGM - Bureau de Recherches GÃ©ologiques et MiniÃ¨res" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/173px-Logo_BRGM.svg.png"/></a> | <a href="http://www.brgm.fr/" title="BRGM">BRGM</a>, the French geological survey, is France's reference public institution for Earth Science applications in the management of surface and subsurface resources and risks. |
| <a target="_blank" href="http://www.wupperverband.de"><img alt="Wupperverband" align="middle" width="196" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_wv.jpg"/></a> | The <a target="_blank" href="http://www.wupperverband.de/">Wupperverband</a> for water, humans and the environment (Germany) |
| <a target="_blank" href="http://www.irceline.be/en"><img alt="Belgian Interregional Environment Agency (IRCEL - CELINE)" align="middle" width="130" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_irceline_no_text.png"/></a> | The <a href="http://www.irceline.be/en" target="_blank" title="Belgian Interregional Environment Agency (IRCEL - CELINE)">Belgian Interregional Environment Agency (IRCEL - CELINE)</a> is active in the domain of air quality (modelling, forecasts, informing the public on the state of their air quality, e-reporting to the EU under the air quality directives, participating in scientific research on air quality, etc.). IRCEL &mdash; CELINE is a permanent cooperation between three regional environment agencies: <a href="http://www.awac.be/" title="Agence wallonne de l&#39Air et du Climat (AWAC)">Agence wallonne de l'Air et du Climat (AWAC)</a>, <a href="http://www.ibgebim.be/" title="Bruxelles Environnement - Leefmilieu Brussel">Bruxelles Environnement - Leefmilieu Brussel</a> and <a href="http://www.vmm.be/" title="Vlaamse Milieumaatschappij (VMM)">Vlaamse Milieumaatschappij (VMM)</a>. |
| <a target="_blank" href="http://www.ivl.se/english"><img alt="IVL Swedish Environmental Research Institute" align="middle" width="196" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/ivl_eng_rgb_70mm.png"/></a> | The <a target="_blank" href="http://www.ivl.se/english">IVL Swedish Environmental Research Institute</a> is an independent, non-profit research institute, owned by a foundation jointly established by the Swedish Government and Swedish industry. |
| <a target="_blank" href="https://www.itzbund.de"><img alt="ITZBund" align="middle" width="120" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_itzbund.png"/></a>| The <a target="_blank" href="https://www.itzbund.de">ITZBund</a> Information Technology Services Centre of the Federal Government (Germany) |
| <a target="_blank" href="http://www.dlr.de"><img alt="German Aerospace Centre" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/DLR-logo.jpg"/></a> | The <a target="_blank" href="http://www.dlr.de">German Aerospace Centre</a> (Deutsches Zentrum fuer Luft- und Raumfahrt, DLR) and part of their <a target="_blank" href="http://www.dlr.de/eoc/en/desktopdefault.aspx/tabid-5400/10196_read-21914/">Environmental and Crisis Information System</a> (Umwelt- und Kriseninformationssystem, UKis) |
| <a target="_blank" href="http://www.smart-project.info"><img alt="SMART Aquifer Characterisation Programme (SAC)" align="middle"  width="96" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/Logo_SMART_v2_rescale.png"/></a> | The <a target="_blank" href="http://www.smart-project.info">SMART</a> Project, funded by the <a target="_blank" href="http://www.msi.govt.nz/">Ministry of Business, Innovation and Employment</a> (07/2011 &ndash; 06/2017): <br/> Experts in the Smart Project will develop, apply, and validate pioneering new techniques for understanding New Zealand&lsquo;s groundwater resources. Satellite and airborne remote sensing techniques and Sensor Observation Services including seismic signals from earthquakes are used for rapid and costeffective characterisation and mapping of New Zealand&lsquo;s aquifer systems. Together with a stakeholder network the research team will use new methods to overcome the current time- and resourceconsuming challenges of in-time data acquisition. Special spatial skills in hydrogeology, geology, satellite remote sensing, geophysics, seismology, uncertainty mathematics and spatial information technology will be developed to assist with improvement of New Zealand&lsquo;s freshwater management. |
| | The OGC Web Services, <a target="_blank" href="http://www.opengeospatial.org/projects/initiatives/ows-9">Phase 9 (OWS-9)</a> Testbed |
| | The OGC Web Services, <a target="_blank" href="http://www.opengeospatial.org/projects/initiatives/ows-10">Phase 10 (OWS-10)</a> Testbed |
| | <a target="_blank" href="http://www.rijkswaterstaat.nl/en/">Rijkswaterstaat</a> - Dutch Ministry of Infrastructure and the Environment (The Netherlands) |
| <a target="_blank" href="https://cordis.europa.eu/project/id/244100"><img alt="EO2HEAVEN - Earth Observation and ENVironmental Modeling for the Mitigation of HEAlth Risks" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_eo2heaven_200px.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a target="_blank" href="https://cordis.europa.eu/project/id/244100">EO2HEAVEN</a> (co-funded by the European Commission under the grant agreement n&deg;244100) |
| <a target="_blank" href="https://cordis.europa.eu/project/id/265178"><img alt="GeoViQua - QUAlity aware VIsualization for the Global Earth Observation System of Systems" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_geoviqua.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a href="https://cordis.europa.eu/project/id/265178" title="GeoViQua">GeoViQua</a> (co-funded by the European Commission under the grant agreement n&deg;265178) |
| <a target="_blank" href="http://www.geowow.eu/"><img alt="GEOWOW - GEOSS interoperability for Weather, Ocean and Water" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_geowow.png"/></a> | The development of this version of the 52&deg;North SOS was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a href="http://www.geowow.eu/" title="GEOWOW">GEOWOW</a> (co-funded by the European Commission under the grant agreement n&deg;282915) |

## Contact

 - Carsten Hollmann (c.hollmann@52north.org)
 - Christian Autermann (c.autermann@52north.org)
 - Eike Hinderk Jürrens [(EHJ-52n)](https://github.com/EHJ-52n/)
 
## Download

The binaries of the 52N SOS releases are provided are provided with the releases on GitHub:

    https://github.com/52North/SOS/releases
    
### Contents
  * `/src` :             The source files of 52°North SOS modules
  * `/bin` :             Executable binary of 52°North SOS webapp module
  * `LICENSE` :         The license of 52°North SOS
  * `NOTICE` :          Third Party libraries and their licenses
  * `README` :          This file
  * `RELEASE-NOTES` : The release notes of the 52°North SOS

No printer friendly documentation exist for this release. Instead, refer to the [wiki documentation](https://wiki.52north.org/SensorWeb/SensorObservationServiceVDocumentation).

## Support

You can get support via the community mailing list:

    https://list.52north.org/mailman/listinfo/sensorweb/

## License

The 52N SOS is licensed under the [GNU General Public License v2 (GPLv2)](http://www.gnu.org/licenses/gpl-2.0.html).

The 3rd party libraries used and their licenses are listed in the [NOITICE file](https://github.com/52North/SOS/blob/develop/NOTICE)

## Contribute

Are you are interested in contributing to the 52°North SOS and you want to pull your changes to the 52N repository to make it available to all?

In that case we need your official permission. For this purpose we have a so-called contributors license agreement (CLA) in place. With this agreement you grant us the rights to use and publish your code under an open source license.

A link to the contributors license agreement and further explanations are available here: 

    https://52north.org/software/licensing/guidelines/
    
## Branches

This project follows the  [Gitflow branching model](http://nvie.com/posts/a-successful-git-branching-model/). "master" reflects the latest stable release.
Ongoing development is done in branch [develop](../../tree/develop) and dedicated feature branches (feature-*).

## Code Compilation

This project is managed with Maven3. Simply run `mvn clean install`
to create a deployable .WAR file.


52°North Inititative for Geospatial Open Source Software GmbH, Germany

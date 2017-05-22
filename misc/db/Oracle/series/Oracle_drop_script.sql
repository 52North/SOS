--
-- Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
-- Software GmbH
--
-- This program is free software; you can redistribute it and/or modify it
-- under the terms of the GNU General Public License version 2 as published
-- by the Free Software Foundation.
--
-- If the program is linked with libraries which are licensed under one of
-- the following licenses, the combination of the program with the linked
-- library is not considered a "derivative work" of the program:
--
--     - Apache License, version 2.0
--     - Apache Software License, version 1.0
--     - GNU Lesser General Public License, version 3
--     - Mozilla Public License, versions 1.0, 1.1 and 2.0
--     - Common Development and Distribution License (CDDL), version 1.0
--
-- Therefore the distribution of the program linked with libraries licensed
-- under the aforementioned licenses, is permitted by the copyright holders
-- if the distribution is compliant with both the GNU General Public
-- License version 2 and the aforementioned licenses.
--
-- This program is distributed in the hope that it will be useful, but
-- WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
-- Public License for more details.
--

drop table oracle."procedure" cascade constraints;
drop table oracle.address cascade constraints;
drop table oracle.addressdeliveryPoint cascade constraints;
drop table oracle.addressemail cascade constraints;
drop table oracle.blobvalue cascade constraints;
drop table oracle.booleanfeatparamvalue cascade constraints;
drop table oracle.booleanparametervalue cascade constraints;
drop table oracle.booleanseriesparamvalue cascade constraints;
drop table oracle.booleanvalue cascade constraints;
drop table oracle.categoryfeatparamvalue cascade constraints;
drop table oracle.categoryparametervalue cascade constraints;
drop table oracle.categoryseriesparamvalue cascade constraints;
drop table oracle.categoryvalue cascade constraints;
drop table oracle.codespace cascade constraints;
drop table oracle.complexvalue cascade constraints;
drop table oracle.compositeobservation cascade constraints;
drop table oracle.compositephenomenon cascade constraints;
drop table oracle.contact cascade constraints;
drop table oracle.coordinatesystemaxis cascade constraints;
drop table oracle.countfeatparamvalue cascade constraints;
drop table oracle.countparametervalue cascade constraints;
drop table oracle.countseriesparamvalue cascade constraints;
drop table oracle.countvalue cascade constraints;
drop table oracle.domainofvalidity cascade constraints;
drop table oracle.exextent cascade constraints;
drop table oracle.exextentverticalext cascade constraints;
drop table oracle.featureofinterest cascade constraints;
drop table oracle.featureofinteresttype cascade constraints;
drop table oracle.featureparameter cascade constraints;
drop table oracle.featurerelation cascade constraints;
drop table oracle.geometryvalue cascade constraints;
drop table oracle.i18nfeatureofinterest cascade constraints;
drop table oracle.i18nobservableproperty cascade constraints;
drop table oracle.i18noffering cascade constraints;
drop table oracle.i18nprocedure cascade constraints;
drop table oracle.numericfeatparamvalue cascade constraints;
drop table oracle.numericparametervalue cascade constraints;
drop table oracle.numericseriesparamvalue cascade constraints;
drop table oracle.numericvalue cascade constraints;
drop table oracle.observableproperty cascade constraints;
drop table oracle.observation cascade constraints;
drop table oracle.observationconstellation cascade constraints;
drop table oracle.observationhasoffering cascade constraints;
drop table oracle.observationtype cascade constraints;
drop table oracle.offering cascade constraints;
drop table oracle.offeringallowedfeaturetype cascade constraints;
drop table oracle.offeringallowedobservationtype cascade constraints;
drop table oracle.offeringhasrelatedfeature cascade constraints;
drop table oracle.offeringrelation cascade constraints;
drop table oracle.onlineresource cascade constraints;
drop table oracle.parameter cascade constraints;
drop table oracle.phonefacsimile cascade constraints;
drop table oracle.phonevoice cascade constraints;
drop table oracle.proceduredescriptionformat cascade constraints;
drop table oracle.profileobservation cascade constraints;
drop table oracle.profilevalue cascade constraints;
drop table oracle.relatedfeature cascade constraints;
drop table oracle.relatedfeaturehasrole cascade constraints;
drop table oracle.relatedfeaturerole cascade constraints;
drop table oracle.relatedobservation cascade constraints;
drop table oracle.relatedseries cascade constraints;
drop table oracle.responsibleparty cascade constraints;
drop table oracle.resulttemplate cascade constraints;
drop table oracle.role cascade constraints;
drop table oracle.sensorsystem cascade constraints;
drop table oracle.series cascade constraints;
drop table oracle.seriesmetadata cascade constraints;
drop table oracle.seriesparameter cascade constraints;
drop table oracle.specimen cascade constraints;
drop table oracle.swedataarrayvalue cascade constraints;
drop table oracle.telephone cascade constraints;
drop table oracle.textfeatparamvalue cascade constraints;
drop table oracle.textparametervalue cascade constraints;
drop table oracle.textseriesparamvalue cascade constraints;
drop table oracle.textvalue cascade constraints;
drop table oracle.unit cascade constraints;
drop table oracle.validproceduretime cascade constraints;
drop table oracle.verticalcrs cascade constraints;
drop table oracle.verticalcrscope cascade constraints;
drop table oracle.verticalcrsdomofval cascade constraints;
drop table oracle.verticalcs cascade constraints;
drop table oracle.verticalcscoodsysaxis cascade constraints;
drop table oracle.verticaldatum cascade constraints;
drop table oracle.verticaldatumscope cascade constraints;
drop table oracle.verticalexextent cascade constraints;
drop table oracle.wmlmonitoringpoint cascade constraints;
drop table oracle.wmlmprelatedparty cascade constraints;
drop table oracle.wmlmpverticaldatum cascade constraints;
drop table oracle.xmlfeatparamvalue cascade constraints;
drop table oracle.xmlparametervalue cascade constraints;
drop table oracle.xmlseriesparamvalue cascade constraints;
drop sequence oracle.addressId_seq;
drop sequence oracle.codespaceId_seq;
drop sequence oracle.contactId_seq;
drop sequence oracle.coordSysAxisId_seq;
drop sequence oracle.domOfValidityId_seq;
drop sequence oracle.exExtentId_seq;
drop sequence oracle.featureOfInterestId_seq;
drop sequence oracle.featureOfInterestTypeId_seq;
drop sequence oracle.i18nObsPropId_seq;
drop sequence oracle.i18nOfferingId_seq;
drop sequence oracle.i18nProcedureId_seq;
drop sequence oracle.i18nfeatureOfInterestId_seq;
drop sequence oracle.metadataId_seq;
drop sequence oracle.observablePropertyId_seq;
drop sequence oracle.observationConstellationId_seq;
drop sequence oracle.observationId_seq;
drop sequence oracle.observationTypeId_seq;
drop sequence oracle.offeringId_seq;
drop sequence oracle.onlineResourceId_seq;
drop sequence oracle.parameterId_seq;
drop sequence oracle.procDescFormatId_seq;
drop sequence oracle.procedureId_seq;
drop sequence oracle.relatedFeatureId_seq;
drop sequence oracle.relatedFeatureRoleId_seq;
drop sequence oracle.relatedObservationId_seq;
drop sequence oracle.responsiblePartyId_seq;
drop sequence oracle.resultTemplateId_seq;
drop sequence oracle.roleId_seq;
drop sequence oracle.seriesId_seq;
drop sequence oracle.seriesRelationId_seq;
drop sequence oracle.telephoneId_seq;
drop sequence oracle.unitId_seq;
drop sequence oracle.validProcedureTimeId_seq;
drop sequence oracle.verticalCRSId_seq;
drop sequence oracle.verticalCSId_seq;
drop sequence oracle.verticalDatumId_seq;
drop sequence oracle.verticalExExtentId_seq;
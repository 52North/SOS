--
-- Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

drop table "procedure" cascade constraints;
drop table blobValue cascade constraints;
drop table booleanValue cascade constraints;
drop table categoryValue cascade constraints;
drop table codespace cascade constraints;
drop table compositePhenomenon cascade constraints;
drop table countValue cascade constraints;
drop table featureOfInterest cascade constraints;
drop table featureOfInterestType cascade constraints;
drop table featureRelation cascade constraints;
drop table geometryValue cascade constraints;
drop table i18nfeatureOfInterest cascade constraints;
drop table i18nobservableProperty cascade constraints;
drop table i18noffering cascade constraints;
drop table i18nprocedure cascade constraints;
drop table numericValue cascade constraints;
drop table observableProperty cascade constraints;
drop table observation cascade constraints;
drop table observationConstellation cascade constraints;
drop table observationHasOffering cascade constraints;
drop table observationType cascade constraints;
drop table offering cascade constraints;
drop table offeringAllowedFeatureType cascade constraints;
drop table offeringAllowedObservationType cascade constraints;
drop table offeringHasRelatedFeature cascade constraints;
drop table parameter cascade constraints;
drop table procedureDescriptionFormat cascade constraints;
drop table relatedFeature cascade constraints;
drop table relatedFeatureHasRole cascade constraints;
drop table relatedFeatureRole cascade constraints;
drop table resultTemplate cascade constraints;
drop table sensorSystem cascade constraints;
drop table series cascade constraints;
drop table sweDataArrayValue cascade constraints;
drop table textValue cascade constraints;
drop table unit cascade constraints;
drop table validProcedureTime cascade constraints;
drop sequence codespaceId_seq;
drop sequence featureOfInterestId_seq;
drop sequence featureOfInterestTypeId_seq;
drop sequence i18nObsPropId_seq;
drop sequence i18nOfferingId_seq;
drop sequence i18nProcedureId_seq;
drop sequence i18nfeatureOfInterestId_seq;
drop sequence observablePropertyId_seq;
drop sequence observationConstellationId_seq;
drop sequence observationId_seq;
drop sequence observationTypeId_seq;
drop sequence offeringId_seq;
drop sequence parameterId_seq;
drop sequence procDescFormatId_seq;
drop sequence procedureId_seq;
drop sequence relatedFeatureId_seq;
drop sequence relatedFeatureRoleId_seq;
drop sequence resultTemplateId_seq;
drop sequence seriesId_seq;
drop sequence unitId_seq;
drop sequence validProcedureTimeId_seq;
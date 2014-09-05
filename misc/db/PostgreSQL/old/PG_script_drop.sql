--
-- Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

drop table if exists "procedure" cascade;
drop table if exists blobValue cascade;
drop table if exists booleanValue cascade;
drop table if exists categoryValue cascade;
drop table if exists codespace cascade;
drop table if exists compositePhenomenon cascade;
drop table if exists countValue cascade;
drop table if exists featureOfInterest cascade;
drop table if exists featureOfInterestType cascade;
drop table if exists featureRelation cascade;
drop table if exists geometryValue cascade;
drop table if exists numericValue cascade;
drop table if exists observableProperty cascade;
drop table if exists observation cascade;
drop table if exists observationConstellation cascade;
drop table if exists observationHasOffering cascade;
drop table if exists observationType cascade;
drop table if exists offering cascade;
drop table if exists offeringAllowedFeatureType cascade;
drop table if exists offeringAllowedObservationType cascade;
drop table if exists offeringHasRelatedFeature cascade;
drop table if exists parameter cascade;
drop table if exists procedureDescriptionFormat cascade;
drop table if exists relatedFeature cascade;
drop table if exists relatedFeatureHasRole cascade;
drop table if exists relatedFeatureRole cascade;
drop table if exists resultTemplate cascade;
drop table if exists sensorSystem cascade;
drop table if exists sweDataArrayValue cascade;
drop table if exists textValue cascade;
drop table if exists unit cascade;
drop table if exists validProcedureTime cascade;
drop sequence codespaceId_seq;
drop sequence featureOfInterestId_seq;
drop sequence featureOfInterestTypeId_seq;
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
drop sequence unitId_seq;
drop sequence validProcedureTimeId_seq;
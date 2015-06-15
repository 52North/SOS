--
-- Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

alter table blobValue drop constraint observationBlobValueFk;
alter table booleanValue drop constraint observationBooleanValueFk;
alter table categoryValue drop constraint observationCategoryValueFk;
alter table compositePhenomenon drop constraint observablePropertyChildFk;
alter table compositePhenomenon drop constraint observablePropertyParentFk;
alter table countValue drop constraint observationCountValueFk;
alter table featureOfInterest drop constraint featureFeatureTypeFk;
alter table featureOfInterest drop constraint featureCodespaceFk;
alter table featureRelation drop constraint featureOfInterestChildFk;
alter table featureRelation drop constraint featureOfInterestParentFk;
alter table geometryValue drop constraint observationGeometryValueFk;
alter table numericValue drop constraint observationNumericValueFk;
alter table observation drop constraint observationFeatureFk;
alter table observation drop constraint observationObPropFk;
alter table observation drop constraint observationProcedureFk;
alter table observation drop constraint observationCodespaceFk;
alter table observation drop constraint observationUnitFk;
alter table observationConstellation drop constraint obsConstObsPropFk;
alter table observationConstellation drop constraint obsnConstProcedureFk;
alter table observationConstellation drop constraint obsConstObservationIypeFk;
alter table observationConstellation drop constraint obsConstOfferingFk;
alter table observationHasOffering drop constraint observationOfferingFk;
alter table offeringAllowedFeatureType drop constraint offeringFeatureTypeFk;
alter table offeringAllowedObservationType drop constraint offeringObservationTypeFk;
alter table offeringHasRelatedFeature drop constraint relatedFeatureOfferingFk;
alter table offeringHasRelatedFeature drop constraint offeringRelatedFeatureFk;
alter table parameter drop constraint parameterObservationFk;
alter table procedure drop constraint procProcDescFormatFk;
alter table relatedFeature drop constraint relatedFeatureFeatureFk;
alter table relatedFeatureHasRole drop constraint relatedFeatRelatedFeatRoleFk;
alter table relatedFeatureHasRole drop constraint FK5643E7654A79987;
alter table resultTemplate drop constraint resultTemplateOfferingIdx;
alter table resultTemplate drop constraint resultTemplateObsPropFk;
alter table resultTemplate drop constraint resultTemplateProcedureFk;
alter table resultTemplate drop constraint resultTemplateFeatureIdx;
alter table sensorSystem drop constraint procedureChildFk;
alter table sensorSystem drop constraint procedureParenfFk;
alter table spatialFilteringProfile drop constraint sfpObservationFK;
alter table sweDataArrayValue drop constraint observationSweDataArrayValueFk;
alter table textValue drop constraint observationTextValueFk;
alter table validProcedureTime drop constraint validProcedureTimeProcedureFk;
alter table validProcedureTime drop constraint validProcProcDescFormatFk;
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
drop table if exists procedure cascade;
drop table if exists procedureDescriptionFormat cascade;
drop table if exists relatedFeature cascade;
drop table if exists relatedFeatureHasRole cascade;
drop table if exists relatedFeatureRole cascade;
drop table if exists resultTemplate cascade;
drop table if exists sensorSystem cascade;
drop table if exists spatialFilteringProfile cascade;
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
drop sequence spatialFilteringProfileId_seq;
drop sequence unitId_seq;
drop sequence validProcedureTimeId_seq;
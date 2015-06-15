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

alter table blobValue drop foreign key observationBlobValueFk;
alter table booleanValue drop foreign key observationBooleanValueFk;
alter table categoryValue drop foreign key observationCategoryValueFk;
alter table compositePhenomenon drop foreign key observablePropertyChildFk;
alter table compositePhenomenon drop foreign key observablePropertyParentFk;
alter table countValue drop foreign key observationCountValueFk;
alter table featureOfInterest drop foreign key featureFeatureTypeFk;
alter table featureOfInterest drop foreign key featureCodespaceFk;
alter table featureRelation drop foreign key featureOfInterestChildFk;
alter table featureRelation drop foreign key featureOfInterestParentFk;
alter table geometryValue drop foreign key observationGeometryValueFk;
alter table numericValue drop foreign key observationNumericValueFk;
alter table observation drop foreign key observationSeriesFk;
alter table observation drop foreign key observationCodespaceFk;
alter table observation drop foreign key observationUnitFk;
alter table observationConstellation drop foreign key obsConstObsPropFk;
alter table observationConstellation drop foreign key obsnConstProcedureFk;
alter table observationConstellation drop foreign key obsConstObservationIypeFk;
alter table observationConstellation drop foreign key obsConstOfferingFk;
alter table observationHasOffering drop foreign key observationOfferingFk;
alter table offeringAllowedFeatureType drop foreign key offeringFeatureTypeFk;
alter table offeringAllowedObservationType drop foreign key offeringObservationTypeFk;
alter table offeringHasRelatedFeature drop foreign key relatedFeatureOfferingFk;
alter table offeringHasRelatedFeature drop foreign key offeringRelatedFeatureFk;
alter table parameter drop foreign key parameterObservationFk;
alter table `procedure` drop foreign key procProcDescFormatFk;
alter table relatedFeature drop foreign key relatedFeatureFeatureFk;
alter table relatedFeatureHasRole drop foreign key relatedFeatRelatedFeatRoleFk;
alter table resultTemplate drop foreign key resultTemplateOfferingIdx;
alter table resultTemplate drop foreign key resultTemplateObsPropFk;
alter table resultTemplate drop foreign key resultTemplateProcedureFk;
alter table resultTemplate drop foreign key resultTemplateFeatureIdx;
alter table sensorSystem drop foreign key procedureChildFk;
alter table sensorSystem drop foreign key procedureParenfFk;
alter table series drop foreign key seriesFeatureFk;
alter table series drop foreign key seriesObPropFk;
alter table series drop foreign key seriesProcedureFk;
alter table spatialFilteringProfile drop foreign key sfpObservationFK;
alter table sweDataArrayValue drop foreign key observationSweDataArrayValueFk;
alter table textValue drop foreign key observationTextValueFk;
alter table validProcedureTime drop foreign key validProcedureTimeProcedureFk;
alter table validProcedureTime drop foreign key validProcProcDescFormatFk;
drop table if exists blobValue;
drop table if exists booleanValue;
drop table if exists categoryValue;
drop table if exists codespace;
drop table if exists compositePhenomenon;
drop table if exists countValue;
drop table if exists featureOfInterest;
drop table if exists featureOfInterestType;
drop table if exists featureRelation;
drop table if exists geometryValue;
drop table if exists numericValue;
drop table if exists observableProperty;
drop table if exists sweDataArrayValue;
drop table if exists textValue;
drop table if exists observationHasOffering;
drop table if exists parameter;
drop table if exists observation;
drop table if exists series;
drop table if exists observationConstellation;
drop table if exists observationType;
drop table if exists offeringAllowedFeatureType;
drop table if exists offeringAllowedObservationType;
drop table if exists offeringHasRelatedFeature;
drop table if exists offering;
drop table if exists `procedure`;
drop table if exists procedureDescriptionFormat;
drop table if exists relatedFeatureHasRole;
drop table if exists relatedFeature;
drop table if exists relatedFeatureRole;
drop table if exists resultTemplate;
drop table if exists sensorSystem;
drop table if exists spatialFilteringProfile;
drop table if exists unit;
drop table if exists validProcedureTime;
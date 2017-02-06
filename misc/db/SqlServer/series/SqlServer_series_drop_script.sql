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

-- Database name to apply changes to
use sos
drop table dbo.blobValue;
drop table dbo.booleanValue;
drop table dbo.categoryValue;
drop table dbo.countValue;
drop table dbo.geometryValue;
drop table dbo.numericValue;
drop table dbo.sweDataArrayValue;
drop table dbo.textValue;
drop table dbo.compositePhenomenon;
drop table dbo.parameter;
drop table dbo.observationHasOffering;
drop table dbo.offeringAllowedFeatureType;
drop table dbo.offeringAllowedObservationType;
drop table dbo.offeringHasRelatedFeature;
drop table dbo.relatedFeatureHasRole;
drop table dbo.relatedFeatureRole;
drop table dbo.relatedFeature;
drop table dbo.sensorSystem;
drop table dbo.validProcedureTime;
drop table dbo.resultTemplate;
drop table dbo.featureRelation;
drop table dbo.observationConstellation;
drop table dbo.observation;
drop table dbo.series;
drop table dbo.unit;
drop table dbo.observationType;
drop table dbo.featureOfInterest;
drop table dbo.observableProperty;
drop table dbo.offering;
drop table dbo.[procedure];
drop table dbo.featureOfInterestType;
drop table dbo.procedureDescriptionFormat;
drop table dbo.codespace;
drop table dbo.i18nfeatureOfInterest;
drop table dbo.i18nobservableProperty;
drop table dbo.i18noffering;
drop table dbo.i18nprocedure;

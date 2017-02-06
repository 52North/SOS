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

SET foreign_key_checks = 0;
drop table if exists sos.blobValue;
drop table if exists sos.booleanValue;
drop table if exists sos.categoryValue;
drop table if exists sos.codespace;
drop table if exists sos.compositePhenomenon;
drop table if exists sos.countValue;
drop table if exists sos.featureOfInterest;
drop table if exists sos.featureOfInterestType;
drop table if exists sos.i18nfeatureOfInterest;
drop table if exists sos.i18nobservableProperty;
drop table if exists sos.i18noffering;
drop table if exists sos.i18nprocedure;
drop table if exists sos.featureRelation;
drop table if exists sos.geometryValue;
drop table if exists sos.numericValue;
drop table if exists sos.observableProperty;
drop table if exists sos.sweDataArrayValue;
drop table if exists sos.textValue;
drop table if exists sos.observationHasOffering;
drop table if exists sos.parameter;
drop table if exists sos.observation;
drop table if exists sos.series;
drop table if exists sos.observationConstellation;
drop table if exists sos.observationType;
drop table if exists sos.offeringAllowedFeatureType;
drop table if exists sos.offeringAllowedObservationType;
drop table if exists sos.offeringHasRelatedFeature;
drop table if exists sos.offering;
drop table if exists sos.`procedure`;
drop table if exists sos.procedureDescriptionFormat;
drop table if exists sos.relatedFeatureHasRole;
drop table if exists sos.relatedFeature;
drop table if exists sos.relatedFeatureRole;
drop table if exists sos.resultTemplate;
drop table if exists sos.sensorSystem;
drop table if exists sos.unit;
drop table if exists sos.validProcedureTime;
SET foreign_key_checks = 1;
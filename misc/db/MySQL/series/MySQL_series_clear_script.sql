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

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sos-netcdf.blobValue;
TRUNCATE TABLE sos-netcdf.booleanValue;
TRUNCATE TABLE sos-netcdf.categoryValue;
TRUNCATE TABLE sos-netcdf.compositePhenomenon;
TRUNCATE TABLE sos-netcdf.compositeObservation;
TRUNCATE TABLE sos-netcdf.countValue;
TRUNCATE TABLE sos-netcdf.featureRelation;
TRUNCATE TABLE sos-netcdf.geometryValue;
TRUNCATE TABLE sos-netcdf.numericValue;
TRUNCATE TABLE sos-netcdf.complexValue;
TRUNCATE TABLE sos-netcdf.observationConstellation;
TRUNCATE TABLE sos-netcdf.observationHasOffering;
TRUNCATE TABLE sos-netcdf.offeringAllowedFeatureType;
TRUNCATE TABLE sos-netcdf.offeringAllowedObservationType;
TRUNCATE TABLE sos-netcdf.offeringHasRelatedFeature;
TRUNCATE TABLE sos-netcdf.relatedFeatureHasRole;
TRUNCATE TABLE sos-netcdf.relatedFeatureRole;
TRUNCATE TABLE sos-netcdf.resultTemplate;
TRUNCATE TABLE sos-netcdf.offering;
TRUNCATE TABLE sos-netcdf.sensorSystem;
TRUNCATE TABLE sos-netcdf.parameter;
TRUNCATE TABLE sos-netcdf.observation;
TRUNCATE TABLE sos-netcdf.observableProperty;
TRUNCATE TABLE sos-netcdf.sweDataArrayValue;
TRUNCATE TABLE sos-netcdf.textValue;
TRUNCATE TABLE sos-netcdf.unit;
TRUNCATE TABLE sos-netcdf.series;
TRUNCATE TABLE sos-netcdf.validProcedureTime;
TRUNCATE TABLE sos-netcdf.`procedure`;
TRUNCATE TABLE sos-netcdf.featureOfInterest;
TRUNCATE TABLE sos-netcdf.relatedFeature;
TRUNCATE TABLE sos-netcdf.codespace;
TRUNCATE TABLE sos-netcdf.procedureDescriptionFormat;
TRUNCATE TABLE sos-netcdf.featureOfInterestType;
TRUNCATE TABLE sos-netcdf.observationType;
TRUNCATE TABLE sos-netcdf.i18nfeatureOfInterest;
TRUNCATE TABLE sos-netcdf.i18nobservableProperty;
TRUNCATE TABLE sos-netcdf.i18noffering;
TRUNCATE TABLE sos-netcdf.i18nprocedure;
SET FOREIGN_KEY_CHECKS = 1;
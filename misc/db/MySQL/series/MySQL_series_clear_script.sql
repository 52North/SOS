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

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE blobValue;
TRUNCATE TABLE booleanValue;
TRUNCATE TABLE categoryValue;
TRUNCATE TABLE compositePhenomenon;
TRUNCATE TABLE countValue;
TRUNCATE TABLE featureRelation;
TRUNCATE TABLE geometryValue;
TRUNCATE TABLE numericValue;
TRUNCATE TABLE observationConstellation;
TRUNCATE TABLE observationHasOffering;
TRUNCATE TABLE offeringAllowedFeatureType;
TRUNCATE TABLE offeringAllowedObservationType;
TRUNCATE TABLE offeringHasRelatedFeature;
TRUNCATE TABLE relatedFeatureHasRole;
TRUNCATE TABLE relatedFeatureRole;
TRUNCATE TABLE resultTemplate;
TRUNCATE TABLE offering;
TRUNCATE TABLE sensorSystem;
TRUNCATE TABLE spatialfilteringprofile;
TRUNCATE TABLE parameter;
TRUNCATE TABLE observation;
TRUNCATE TABLE observableProperty;
TRUNCATE TABLE swedataarrayvalue;
TRUNCATE TABLE textValue;
TRUNCATE TABLE unit;
TRUNCATE TABLE series;
TRUNCATE TABLE validProcedureTime;
TRUNCATE TABLE `procedure`;
TRUNCATE TABLE featureOfInterest;
TRUNCATE TABLE relatedFeature;
TRUNCATE TABLE codespace;
TRUNCATE TABLE procedureDescriptionFormat;
TRUNCATE TABLE featureOfInterestType;
TRUNCATE TABLE observationType;
SET FOREIGN_KEY_CHECKS = 1;
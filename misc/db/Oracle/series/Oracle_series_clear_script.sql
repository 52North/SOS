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

TRUNCATE TABLE blobValue REUSE STORAGE;
TRUNCATE TABLE booleanValue REUSE STORAGE;
TRUNCATE TABLE categoryValue REUSE STORAGE;
TRUNCATE TABLE codespace REUSE STORAGE;
TRUNCATE TABLE compositePhenomenon REUSE STORAGE;
TRUNCATE TABLE countValue REUSE STORAGE;
TRUNCATE TABLE featureRelation REUSE STORAGE;
TRUNCATE TABLE geometryValue REUSE STORAGE;
TRUNCATE TABLE numericValue REUSE STORAGE;
TRUNCATE TABLE observableProperty REUSE STORAGE;
TRUNCATE TABLE observation REUSE STORAGE;
TRUNCATE TABLE observationConstellation REUSE STORAGE;
TRUNCATE TABLE observationHasOffering REUSE STORAGE;
TRUNCATE TABLE observationType REUSE STORAGE;
TRUNCATE TABLE offering REUSE STORAGE;
TRUNCATE TABLE offeringAllowedFeatureType REUSE STORAGE;
TRUNCATE TABLE offeringAllowedObservationType REUSE STORAGE;
TRUNCATE TABLE offeringHasRelatedFeature REUSE STORAGE;
TRUNCATE TABLE parameter REUSE STORAGE;
TRUNCATE TABLE "procedure" REUSE STORAGE;
TRUNCATE TABLE procedureDescriptionFormat REUSE STORAGE;
TRUNCATE TABLE relatedFeature REUSE STORAGE;
TRUNCATE TABLE relatedFeatureHasRole REUSE STORAGE;
TRUNCATE TABLE relatedFeatureRole REUSE STORAGE;
TRUNCATE TABLE resultTemplate REUSE STORAGE;
TRUNCATE TABLE sensorSystem REUSE STORAGE;
TRUNCATE TABLE series REUSE STORAGE;
TRUNCATE TABLE swedataarrayvalue REUSE STORAGE;
TRUNCATE TABLE textValue REUSE STORAGE;
TRUNCATE TABLE unit REUSE STORAGE;
TRUNCATE TABLE validProcedureTime REUSE STORAGE;
TRUNCATE TABLE featureOfInterest REUSE STORAGE;
TRUNCATE TABLE featureOfInterestType REUSE STORAGE;
TRUNCATE TABLE i18nfeatureOfInterest REUSE STORAGE;
TRUNCATE TABLE i18nobservableProperty REUSE STORAGE;
TRUNCATE TABLE i18noffering REUSE STORAGE;
TRUNCATE TABLE i18nprocedure REUSE STORAGE;

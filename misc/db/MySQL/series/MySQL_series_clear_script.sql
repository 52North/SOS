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

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sos.blobValue;
TRUNCATE TABLE sos.booleanValue;
TRUNCATE TABLE sos.categoryValue;
TRUNCATE TABLE sos.compositePhenomenon;
TRUNCATE TABLE sos.countValue;
TRUNCATE TABLE sos.featureRelation;
TRUNCATE TABLE sos.geometryValue;
TRUNCATE TABLE sos.numericValue;
TRUNCATE TABLE sos.observationConstellation;
TRUNCATE TABLE sos.observationHasOffering;
TRUNCATE TABLE sos.offeringAllowedFeatureType;
TRUNCATE TABLE sos.offeringAllowedObservationType;
TRUNCATE TABLE sos.offeringHasRelatedFeature;
TRUNCATE TABLE sos.relatedFeatureHasRole;
TRUNCATE TABLE sos.relatedFeatureRole;
TRUNCATE TABLE sos.resultTemplate;
TRUNCATE TABLE sos.offering;
TRUNCATE TABLE sos.sensorSystem;
TRUNCATE TABLE sos.parameter;
TRUNCATE TABLE sos.observation;
TRUNCATE TABLE sos.observableProperty;
TRUNCATE TABLE sos.swedataarrayvalue;
TRUNCATE TABLE sos.textValue;
TRUNCATE TABLE sos.unit;
TRUNCATE TABLE sos.series;
TRUNCATE TABLE sos.validProcedureTime;
TRUNCATE TABLE sos.`procedure`;
TRUNCATE TABLE sos.featureOfInterest;
TRUNCATE TABLE sos.relatedFeature;
TRUNCATE TABLE sos.codespace;
TRUNCATE TABLE sos.procedureDescriptionFormat;
TRUNCATE TABLE sos.featureOfInterestType;
TRUNCATE TABLE sos.observationType;
TRUNCATE TABLE sos.i18nfeatureOfInterest;
TRUNCATE TABLE sos.i18nobservableProperty;
TRUNCATE TABLE sos.i18noffering;
TRUNCATE TABLE sos.i18nprocedure;
SET FOREIGN_KEY_CHECKS = 1;
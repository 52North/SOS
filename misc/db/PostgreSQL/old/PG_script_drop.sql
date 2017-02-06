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

drop table if exists public."procedure" cascade;
drop table if exists public.blobValue cascade;
drop table if exists public.booleanValue cascade;
drop table if exists public.categoryValue cascade;
drop table if exists public.codespace cascade;
drop table if exists public.compositePhenomenon cascade;
drop table if exists public.countValue cascade;
drop table if exists public.featureOfInterest cascade;
drop table if exists public.featureOfInterestType cascade;
drop table if exists public.featureRelation cascade;
drop table if exists public.geometryValue cascade;
drop table if exists public.i18nfeatureOfInterest cascade;
drop table if exists public.i18nobservableProperty cascade;
drop table if exists public.i18noffering cascade;
drop table if exists public.i18nprocedure cascade;
drop table if exists public.numericValue cascade;
drop table if exists public.observableProperty cascade;
drop table if exists public.observation cascade;
drop table if exists public.observationConstellation cascade;
drop table if exists public.observationHasOffering cascade;
drop table if exists public.observationType cascade;
drop table if exists public.offering cascade;
drop table if exists public.offeringAllowedFeatureType cascade;
drop table if exists public.offeringAllowedObservationType cascade;
drop table if exists public.offeringHasRelatedFeature cascade;
drop table if exists public.parameter cascade;
drop table if exists public.procedureDescriptionFormat cascade;
drop table if exists public.relatedFeature cascade;
drop table if exists public.relatedFeatureHasRole cascade;
drop table if exists public.relatedFeatureRole cascade;
drop table if exists public.resultTemplate cascade;
drop table if exists public.sensorSystem cascade;
drop table if exists public.sweDataArrayValue cascade;
drop table if exists public.textValue cascade;
drop table if exists public.unit cascade;
drop table if exists public.validProcedureTime cascade;
drop sequence public.codespaceId_seq;
drop sequence public.featureOfInterestId_seq;
drop sequence public.featureOfInterestTypeId_seq;
drop sequence public.i18nObsPropId_seq;
drop sequence public.i18nOfferingId_seq;
drop sequence public.i18nProcedureId_seq;
drop sequence public.i18nfeatureOfInterestId_seq;
drop sequence public.observablePropertyId_seq;
drop sequence public.observationConstellationId_seq;
drop sequence public.observationId_seq;
drop sequence public.observationTypeId_seq;
drop sequence public.offeringId_seq;
drop sequence public.parameterId_seq;
drop sequence public.procDescFormatId_seq;
drop sequence public.procedureId_seq;
drop sequence public.relatedFeatureId_seq;
drop sequence public.relatedFeatureRoleId_seq;
drop sequence public.resultTemplateId_seq;
drop sequence public.unitId_seq;
drop sequence public.validProcedureTimeId_seq;

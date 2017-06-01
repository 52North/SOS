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

drop table if exists public."procedure" cascade;
drop table if exists public.address cascade;
drop table if exists public.addressdeliveryPoint cascade;
drop table if exists public.addressemail cascade;
drop table if exists public.blobvalue cascade;
drop table if exists public.booleanfeatparamvalue cascade;
drop table if exists public.booleanparametervalue cascade;
drop table if exists public.booleanseriesparamvalue cascade;
drop table if exists public.booleanvalue cascade;
drop table if exists public.categoryfeatparamvalue cascade;
drop table if exists public.categoryparametervalue cascade;
drop table if exists public.categoryseriesparamvalue cascade;
drop table if exists public.categoryvalue cascade;
drop table if exists public.codespace cascade;
drop table if exists public.complexvalue cascade;
drop table if exists public.compositeobservation cascade;
drop table if exists public.compositephenomenon cascade;
drop table if exists public.contact cascade;
drop table if exists public.coordinatesystemaxis cascade;
drop table if exists public.countfeatparamvalue cascade;
drop table if exists public.countparametervalue cascade;
drop table if exists public.countseriesparamvalue cascade;
drop table if exists public.countvalue cascade;
drop table if exists public.domainofvalidity cascade;
drop table if exists public.exextent cascade;
drop table if exists public.exextentverticalext cascade;
drop table if exists public.featureofinterest cascade;
drop table if exists public.featureofinteresttype cascade;
drop table if exists public.featureparameter cascade;
drop table if exists public.featurerelation cascade;
drop table if exists public.geometryvalue cascade;
drop table if exists public.i18nfeatureofinterest cascade;
drop table if exists public.i18nobservableproperty cascade;
drop table if exists public.i18noffering cascade;
drop table if exists public.i18nprocedure cascade;
drop table if exists public.numericfeatparamvalue cascade;
drop table if exists public.numericparametervalue cascade;
drop table if exists public.numericseriesparamvalue cascade;
drop table if exists public.numericvalue cascade;
drop table if exists public.observableproperty cascade;
drop table if exists public.observation cascade;
drop table if exists public.observationconstellation cascade;
drop table if exists public.observationhasoffering cascade;
drop table if exists public.observationtype cascade;
drop table if exists public.offering cascade;
drop table if exists public.offeringallowedfeaturetype cascade;
drop table if exists public.offeringallowedobservationtype cascade;
drop table if exists public.offeringhasrelatedfeature cascade;
drop table if exists public.offeringrelation cascade;
drop table if exists public.onlineresource cascade;
drop table if exists public.parameter cascade;
drop table if exists public.phonefacsimile cascade;
drop table if exists public.phonevoice cascade;
drop table if exists public.proceduredescriptionformat cascade;
drop table if exists public.profileobservation cascade;
drop table if exists public.profilevalue cascade;
drop table if exists public.relatedfeature cascade;
drop table if exists public.relatedfeaturehasrole cascade;
drop table if exists public.relatedfeaturerole cascade;
drop table if exists public.relatedobservation cascade;
drop table if exists public.relatedseries cascade;
drop table if exists public.responsibleparty cascade;
drop table if exists public.resulttemplate cascade;
drop table if exists public.role cascade;
drop table if exists public.sensorsystem cascade;
drop table if exists public.series cascade;
drop table if exists public.seriesmetadata cascade;
drop table if exists public.seriesparameter cascade;
drop table if exists public.specimen cascade;
drop table if exists public.swedataarrayvalue cascade;
drop table if exists public.telephone cascade;
drop table if exists public.textfeatparamvalue cascade;
drop table if exists public.textparametervalue cascade;
drop table if exists public.textseriesparamvalue cascade;
drop table if exists public.textvalue cascade;
drop table if exists public.unit cascade;
drop table if exists public.validproceduretime cascade;
drop table if exists public.verticalcrs cascade;
drop table if exists public.verticalcrscope cascade;
drop table if exists public.verticalcrsdomofval cascade;
drop table if exists public.verticalcs cascade;
drop table if exists public.verticalcscoodsysaxis cascade;
drop table if exists public.verticaldatum cascade;
drop table if exists public.verticaldatumscope cascade;
drop table if exists public.verticalexextent cascade;
drop table if exists public.wmlmonitoringpoint cascade;
drop table if exists public.wmlmprelatedparty cascade;
drop table if exists public.wmlmpverticaldatum cascade;
drop table if exists public.xmlfeatparamvalue cascade;
drop table if exists public.xmlparametervalue cascade;
drop table if exists public.xmlseriesparamvalue cascade;
drop sequence public.addressId_seq;
drop sequence public.codespaceId_seq;
drop sequence public.contactId_seq;
drop sequence public.coordSysAxisId_seq;
drop sequence public.domOfValidityId_seq;
drop sequence public.exExtentId_seq;
drop sequence public.featureOfInterestId_seq;
drop sequence public.featureOfInterestTypeId_seq;
drop sequence public.i18nObsPropId_seq;
drop sequence public.i18nOfferingId_seq;
drop sequence public.i18nProcedureId_seq;
drop sequence public.i18nfeatureOfInterestId_seq;
drop sequence public.metadataId_seq;
drop sequence public.observablePropertyId_seq;
drop sequence public.observationConstellationId_seq;
drop sequence public.observationId_seq;
drop sequence public.observationTypeId_seq;
drop sequence public.offeringId_seq;
drop sequence public.onlineResourceId_seq;
drop sequence public.parameterId_seq;
drop sequence public.procDescFormatId_seq;
drop sequence public.procedureId_seq;
drop sequence public.relatedFeatureId_seq;
drop sequence public.relatedFeatureRoleId_seq;
drop sequence public.relatedObservationId_seq;
drop sequence public.responsiblePartyId_seq;
drop sequence public.resultTemplateId_seq;
drop sequence public.roleId_seq;
drop sequence public.seriesId_seq;
drop sequence public.seriesRelationId_seq;
drop sequence public.telephoneId_seq;
drop sequence public.unitId_seq;
drop sequence public.validProcedureTimeId_seq;
drop sequence public.verticalCRSId_seq;
drop sequence public.verticalCSId_seq;
drop sequence public.verticalDatumId_seq;
drop sequence public.verticalExExtentId_seq;
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

SET foreign_key_checks = 0;
drop table if exists sos.`procedure`;
drop table if exists sos.address;
drop table if exists sos.addressdeliveryPoint;
drop table if exists sos.addressemail;
drop table if exists sos.blobvalue;
drop table if exists sos.booleanfeatparamvalue;
drop table if exists sos.booleanparametervalue;
drop table if exists sos.booleanseriesparamvalue;
drop table if exists sos.booleanvalue;
drop table if exists sos.categoryfeatparamvalue;
drop table if exists sos.categoryparametervalue;
drop table if exists sos.categoryseriesparamvalue;
drop table if exists sos.categoryvalue;
drop table if exists sos.codespace;
drop table if exists sos.complexvalue;
drop table if exists sos.compositeobservation;
drop table if exists sos.compositephenomenon;
drop table if exists sos.contact;
drop table if exists sos.coordinatesystemaxis;
drop table if exists sos.countfeatparamvalue;
drop table if exists sos.countparametervalue;
drop table if exists sos.countseriesparamvalue;
drop table if exists sos.countvalue;
drop table if exists sos.domainofvalidity;
drop table if exists sos.exextent;
drop table if exists sos.exextentverticalext;
drop table if exists sos.featureofinterest;
drop table if exists sos.featureofinteresttype;
drop table if exists sos.featureparameter;
drop table if exists sos.featurerelation;
drop table if exists sos.geometryvalue;
drop table if exists sos.i18nfeatureofinterest;
drop table if exists sos.i18nobservableproperty;
drop table if exists sos.i18noffering;
drop table if exists sos.i18nprocedure;
drop table if exists sos.numericfeatparamvalue;
drop table if exists sos.numericparametervalue;
drop table if exists sos.numericseriesparamvalue;
drop table if exists sos.numericvalue;
drop table if exists sos.observableproperty;
drop table if exists sos.observation;
drop table if exists sos.observationconstellation;
drop table if exists sos.observationhasoffering;
drop table if exists sos.observationtype;
drop table if exists sos.offering;
drop table if exists sos.offeringallowedfeaturetype;
drop table if exists sos.offeringallowedobservationtype;
drop table if exists sos.offeringhasrelatedfeature;
drop table if exists sos.offeringrelation;
drop table if exists sos.onlineresource;
drop table if exists sos.parameter;
drop table if exists sos.phonefacsimile;
drop table if exists sos.phonevoice;
drop table if exists sos.proceduredescriptionformat;
drop table if exists sos.profileobservation;
drop table if exists sos.profilevalue;
drop table if exists sos.relatedfeature;
drop table if exists sos.relatedfeaturehasrole;
drop table if exists sos.relatedfeaturerole;
drop table if exists sos.relatedobservation;
drop table if exists sos.relatedseries;
drop table if exists sos.responsibleparty;
drop table if exists sos.resulttemplate;
drop table if exists sos.role;
drop table if exists sos.sensorsystem;
drop table if exists sos.series;
drop table if exists sos.seriesmetadata;
drop table if exists sos.seriesparameter;
drop table if exists sos.specimen;
drop table if exists sos.swedataarrayvalue;
drop table if exists sos.telephone;
drop table if exists sos.textfeatparamvalue;
drop table if exists sos.textparametervalue;
drop table if exists sos.textseriesparamvalue;
drop table if exists sos.textvalue;
drop table if exists sos.unit;
drop table if exists sos.validproceduretime;
drop table if exists sos.verticalcrs;
drop table if exists sos.verticalcrscope;
drop table if exists sos.verticalcrsdomofval;
drop table if exists sos.verticalcs;
drop table if exists sos.verticalcscoodsysaxis;
drop table if exists sos.verticaldatum;
drop table if exists sos.verticaldatumscope;
drop table if exists sos.verticalexextent;
drop table if exists sos.wmlmonitoringpoint;
drop table if exists sos.wmlmprelatedparty;
drop table if exists sos.wmlmpverticaldatum;
drop table if exists sos.xmlfeatparamvalue;
drop table if exists sos.xmlparametervalue;
drop table if exists sos.xmlseriesparamvalue;
SET foreign_key_checks = 1;

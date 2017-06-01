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

-- Database name to apply changes to
use sos
drop table dbo.[procedure];
drop table dbo.address;
drop table dbo.addressdeliveryPoint;
drop table dbo.addressemail;
drop table dbo.blobvalue;
drop table dbo.booleanfeatparamvalue;
drop table dbo.booleanparametervalue;
drop table dbo.booleanseriesparamvalue;
drop table dbo.booleanvalue;
drop table dbo.categoryfeatparamvalue;
drop table dbo.categoryparametervalue;
drop table dbo.categoryseriesparamvalue;
drop table dbo.categoryvalue;
drop table dbo.codespace;
drop table dbo.complexvalue;
drop table dbo.compositeobservation;
drop table dbo.compositephenomenon;
drop table dbo.contact;
drop table dbo.coordinatesystemaxis;
drop table dbo.countfeatparamvalue;
drop table dbo.countparametervalue;
drop table dbo.countseriesparamvalue;
drop table dbo.countvalue;
drop table dbo.domainofvalidity;
drop table dbo.exextent;
drop table dbo.exextentverticalext;
drop table dbo.featureofinterest;
drop table dbo.featureofinteresttype;
drop table dbo.featureparameter;
drop table dbo.featurerelation;
drop table dbo.geometryvalue;
drop table dbo.i18nfeatureofinterest;
drop table dbo.i18nobservableproperty;
drop table dbo.i18noffering;
drop table dbo.i18nprocedure;
drop table dbo.numericfeatparamvalue;
drop table dbo.numericparametervalue;
drop table dbo.numericseriesparamvalue;
drop table dbo.numericvalue;
drop table dbo.observableproperty;
drop table dbo.observation;
drop table dbo.observationconstellation;
drop table dbo.observationhasoffering;
drop table dbo.observationtype;
drop table dbo.offering;
drop table dbo.offeringallowedfeaturetype;
drop table dbo.offeringallowedobservationtype;
drop table dbo.offeringhasrelatedfeature;
drop table dbo.offeringrelation;
drop table dbo.onlineresource;
drop table dbo.parameter;
drop table dbo.phonefacsimile;
drop table dbo.phonevoice;
drop table dbo.proceduredescriptionformat;
drop table dbo.profileobservation;
drop table dbo.profilevalue;
drop table dbo.relatedfeature;
drop table dbo.relatedfeaturehasrole;
drop table dbo.relatedfeaturerole;
drop table dbo.relatedobservation;
drop table dbo.relatedseries;
drop table dbo.responsibleparty;
drop table dbo.resulttemplate;
drop table dbo.role;
drop table dbo.sensorsystem;
drop table dbo.series;
drop table dbo.seriesmetadata;
drop table dbo.seriesparameter;
drop table dbo.specimen;
drop table dbo.swedataarrayvalue;
drop table dbo.telephone;
drop table dbo.textfeatparamvalue;
drop table dbo.textparametervalue;
drop table dbo.textseriesparamvalue;
drop table dbo.textvalue;
drop table dbo.unit;
drop table dbo.validproceduretime;
drop table dbo.verticalcrs;
drop table dbo.verticalcrscope;
drop table dbo.verticalcrsdomofval;
drop table dbo.verticalcs;
drop table dbo.verticalcscoodsysaxis;
drop table dbo.verticaldatum;
drop table dbo.verticaldatumscope;
drop table dbo.verticalexextent;
drop table dbo.wmlmonitoringpoint;
drop table dbo.wmlmprelatedparty;
drop table dbo.wmlmpverticaldatum;
drop table dbo.xmlfeatparamvalue;
drop table dbo.xmlparametervalue;
drop table dbo.xmlseriesparamvalue;
--
-- Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
alter table sos.`procedure` drop foreign key procProcDescFormatFk;
alter table sos.`procedure` drop foreign key procCodespaceIdentifierFk;
alter table sos.`procedure` drop foreign key procCodespaceNameFk;
alter table sos.`procedure` drop foreign key typeOfFk;
alter table sos.addressdeliveryPoint drop foreign key adddelpoiFk;
alter table sos.addressemail drop foreign key addemailFk;
alter table sos.blobvalue drop foreign key observationBlobValueFk;
alter table sos.booleanfeatparamvalue drop foreign key featParamBooleanValueFk;
alter table sos.booleanparametervalue drop foreign key parameterBooleanValueFk;
alter table sos.booleanseriesparamvalue drop foreign key seriesParamBooleanValueFk;
alter table sos.booleanvalue drop foreign key observationBooleanValueFk;
alter table sos.categoryfeatparamvalue drop foreign key featParamCategoryValueFk;
alter table sos.categoryfeatparamvalue drop foreign key catfeatparamvalueUnitFk;
alter table sos.categoryparametervalue drop foreign key parameterCategoryValueFk;
alter table sos.categoryparametervalue drop foreign key catParamValueUnitFk;
alter table sos.categoryseriesparamvalue drop foreign key seriesParamCategoryValueFk;
alter table sos.categoryseriesparamvalue drop foreign key seriesCatParamValueUnitFk;
alter table sos.categoryvalue drop foreign key observationCategoryValueFk;
alter table sos.complexvalue drop foreign key observationComplexValueFk;
alter table sos.compositeobservation drop foreign key observationChildFk;
alter table sos.compositeobservation drop foreign key observationParentFK;
alter table sos.compositephenomenon drop foreign key observablePropertyChildFk;
alter table sos.compositephenomenon drop foreign key observablePropertyParentFk;
alter table sos.contact drop foreign key contactphoneFk;
alter table sos.contact drop foreign key contactaddressFk;
alter table sos.contact drop foreign key contactonlineresFk;
alter table sos.coordinatesystemaxis drop foreign key csaCodespaceIdentifierFk;
alter table sos.coordinatesystemaxis drop foreign key csaCodespaceNameFk;
alter table sos.coordinatesystemaxis drop foreign key scsacodeaaFk;
alter table sos.coordinatesystemaxis drop foreign key scsacodeadFk;
alter table sos.coordinatesystemaxis drop foreign key scsacodermFk;
alter table sos.coordinatesystemaxis drop foreign key csaUnitFk;
alter table sos.countfeatparamvalue drop foreign key featParamCountValueFk;
alter table sos.countparametervalue drop foreign key parameterCountValueFk;
alter table sos.countseriesparamvalue drop foreign key seriesParamCountValueFk;
alter table sos.countvalue drop foreign key observationCountValueFk;
alter table sos.domainofvalidity drop foreign key codCodespaceIdentifierFk;
alter table sos.domainofvalidity drop foreign key codCodespaceNameFk;
alter table sos.domainofvalidity drop foreign key dovexeFk;
alter table sos.exextentverticalext drop foreign key FK_qppk0ymn6peh5v1571aajqa8g;
alter table sos.exextentverticalext drop foreign key exeexveFk;
alter table sos.featureofinterest drop foreign key featureFeatureTypeFk;
alter table sos.featureofinterest drop foreign key featureCodespaceIdentifierFk;
alter table sos.featureofinterest drop foreign key featureCodespaceNameFk;
alter table sos.featureparameter drop foreign key FK_4ps6yv41rwnbu3q0let2v7772;
alter table sos.featurerelation drop foreign key featureOfInterestChildFk;
alter table sos.featurerelation drop foreign key featureOfInterestParentFk;
alter table sos.geometryvalue drop foreign key observationGeometryValueFk;
alter table sos.i18nfeatureofinterest drop foreign key i18nFeatureFeatureFk;
alter table sos.i18nobservableproperty drop foreign key i18nObsPropObsPropFk;
alter table sos.i18noffering drop foreign key i18nOfferingOfferingFk;
alter table sos.i18nprocedure drop foreign key i18nProcedureProcedureFk;
alter table sos.numericfeatparamvalue drop foreign key featParamNumericValueFk;
alter table sos.numericfeatparamvalue drop foreign key quanfeatparamvalueUnitFk;
alter table sos.numericparametervalue drop foreign key parameterNumericValueFk;
alter table sos.numericparametervalue drop foreign key quanParamValueUnitFk;
alter table sos.numericseriesparamvalue drop foreign key seriesParamNumericValueFk;
alter table sos.numericseriesparamvalue drop foreign key seriesQuanParamValueUnitFk;
alter table sos.numericvalue drop foreign key observationNumericValueFk;
alter table sos.observableproperty drop foreign key obsPropCodespaceIdentifierFk;
alter table sos.observableproperty drop foreign key obsPropCodespaceNameFk;
alter table sos.observation drop foreign key observationSeriesFk;
alter table sos.observation drop foreign key obsCodespaceIdentifierFk;
alter table sos.observation drop foreign key obsCodespaceNameFk;
alter table sos.observation drop foreign key observationUnitFk;
alter table sos.observationconstellation drop foreign key obsConstObsPropFk;
alter table sos.observationconstellation drop foreign key obsnConstProcedureFk;
alter table sos.observationconstellation drop foreign key obsConstObservationIypeFk;
alter table sos.observationconstellation drop foreign key obsConstOfferingFk;
alter table sos.observationhasoffering drop foreign key observationOfferingFk;
alter table sos.observationhasoffering drop foreign key FK_s19siow5aetbwb8ppww4kb96n;
alter table sos.offering drop foreign key offCodespaceIdentifierFk;
alter table sos.offering drop foreign key offCodespaceNameFk;
alter table sos.offeringallowedfeaturetype drop foreign key offeringFeatureTypeFk;
alter table sos.offeringallowedfeaturetype drop foreign key FK_cu8nfsf9q5vsn070o2d3u6chg;
alter table sos.offeringallowedobservationtype drop foreign key offeringObservationTypeFk;
alter table sos.offeringallowedobservationtype drop foreign key FK_jehw0637hllvta9ao1tqdhrtm;
alter table sos.offeringhasrelatedfeature drop foreign key relatedFeatureOfferingFk;
alter table sos.offeringhasrelatedfeature drop foreign key offeringRelatedFeatureFk;
alter table sos.offeringrelation drop foreign key offeringChildFk;
alter table sos.offeringrelation drop foreign key offeringParenfFk;
alter table sos.parameter drop foreign key FK_3v5iovcndi9w0hgh827hcvivw;
alter table sos.phonefacsimile drop foreign key phonefacsimilefk;
alter table sos.phonevoice drop foreign key phonevoicefk;
alter table sos.profileobservation drop foreign key profileObsChildFk;
alter table sos.profileobservation drop foreign key profileObsParentFK;
alter table sos.profilevalue drop foreign key observationProfileValueFk;
alter table sos.profilevalue drop foreign key profileUnitFk;
alter table sos.referencevalue drop foreign key observationRefValueFk;
alter table sos.relatedfeature drop foreign key relatedFeatureFeatureFk;
alter table sos.relatedfeaturehasrole drop foreign key relatedFeatRelatedFeatRoleFk;
alter table sos.relatedfeaturehasrole drop foreign key FK_5fd921q6mnbkc57mgm5g4uyyn;
alter table sos.relatedobservation drop foreign key FK_g0f0mpuxn3co65uwud4pwxh4q;
alter table sos.relatedobservation drop foreign key FK_m4nuof4x6w253biuu1r6ttnqc;
alter table sos.relatedseries drop foreign key relatedSeriesFk;
alter table sos.responsibleparty drop foreign key rpcontactFk;
alter table sos.responsibleparty drop foreign key rproleFk;
alter table sos.resulttemplate drop foreign key resultTemplateOfferingIdx;
alter table sos.resulttemplate drop foreign key resultTemplateObsPropFk;
alter table sos.resulttemplate drop foreign key resultTemplateProcedureFk;
alter table sos.resulttemplate drop foreign key resultTemplateFeatureIdx;
alter table sos.sensorsystem drop foreign key procedureChildFk;
alter table sos.sensorsystem drop foreign key procedureParenfFk;
alter table sos.series drop foreign key seriesFeatureFk;
alter table sos.series drop foreign key seriesObPropFk;
alter table sos.series drop foreign key seriesProcedureFk;
alter table sos.series drop foreign key seriesOfferingFk;
alter table sos.series drop foreign key seriesUnitFk;
alter table sos.series drop foreign key seriesCodespaceIdentifierFk;
alter table sos.series drop foreign key seriesCodespaceNameFk;
alter table sos.seriesreference drop foreign key seriesrefreffk;
alter table sos.seriesreference drop foreign key seriesrefseriesfk;
alter table sos.specimen drop foreign key specimenFeatureFk;
alter table sos.specimen drop foreign key observationUnitFk;
alter table sos.swedataarrayvalue drop foreign key observationSweDataArrayValueFk;
alter table sos.textfeatparamvalue drop foreign key featParamTextValueFk;
alter table sos.textparametervalue drop foreign key parameterTextValueFk;
alter table sos.textseriesparamvalue drop foreign key seriesParamTextValueFk;
alter table sos.textvalue drop foreign key observationTextValueFk;
alter table sos.validproceduretime drop foreign key validProcedureTimeProcedureFk;
alter table sos.validproceduretime drop foreign key validProcProcDescFormatFk;
alter table sos.verticalcrs drop foreign key vcrsCodespaceIdentifierFk;
alter table sos.verticalcrs drop foreign key vcrsCodespaceNameFk;
alter table sos.verticalcrs drop foreign key vcrsvcsfk;
alter table sos.verticalcrs drop foreign key vcrsvdfk;
alter table sos.verticalcrscope drop foreign key vcrsscopefk;
alter table sos.verticalcrsdomofval drop foreign key vcrsdovfk;
alter table sos.verticalcrsdomofval drop foreign key vcrsdovidfk;
alter table sos.verticalcs drop foreign key vcsCodespaceIdentifierFk;
alter table sos.verticalcs drop foreign key vcsCodespaceNameFk;
alter table sos.verticalcscoodsysaxis drop foreign key vcscsafk;
alter table sos.verticalcscoodsysaxis drop foreign key vcscsaidfk;
alter table sos.verticaldatum drop foreign key vdCodespaceIdentifierFk;
alter table sos.verticaldatum drop foreign key vdCodespaceNameFk;
alter table sos.verticaldatum drop foreign key vddovFk;
alter table sos.verticaldatum drop foreign key vdCodespaceADefFk;
alter table sos.verticaldatumscope drop foreign key vdscopefk;
alter table sos.verticalexextent drop foreign key vevcrsFk;
alter table sos.wmlmonitoringpoint drop foreign key wmlmpFeatureFk;
alter table sos.wmlmprelatedparty drop foreign key wmlmprpFk;
alter table sos.wmlmprelatedparty drop foreign key FK_nyt2o5m81p35lqk7rctyook4o;
alter table sos.wmlmpverticaldatum drop foreign key FK_r4s9vt1g0o5o4lfi37qrh2yad;
alter table sos.wmlmpverticaldatum drop foreign key wmlmpvdFk;
alter table sos.xmlfeatparamvalue drop foreign key featParamXmlValueFk;
alter table sos.xmlparametervalue drop foreign key parameterXmlValueFk;
alter table sos.xmlseriesparamvalue drop foreign key seriesParamXmlValueFk;
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
drop table if exists sos.referencevalue;
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
drop table if exists sos.seriesreference;
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

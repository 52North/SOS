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

-- Database name to apply changes to
use sos
create table sdo.[procedure] (procedureId bigint identity not null, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), disabled char(1) default 'F' not null check (disabled in ('T','F')), descriptionFile varchar(MAX), referenceFlag char(1) default 'F' check (referenceFlag in ('T','F')), typeOf bigint, isType char(1) default 'F' check (isType in ('T','F')), isAggregation char(1) default 'T' check (isAggregation in ('T','F')), primary key (procedureId));
create table sdo.blobValue (observationId bigint not null, value varbinary(MAX), primary key (observationId));
create table sdo.booleanParameterValue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table sdo.booleanValue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F')));
create table sdo.categoryParameterValue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
create table sdo.categoryValue (observationId bigint not null, value varchar(255), primary key (observationId));
create table sdo.codespace (codespaceId bigint identity not null, codespace varchar(255) not null, primary key (codespaceId));
create table sdo.complexValue (observationId bigint not null, primary key (observationId));
create table sdo.compositeObservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
create table sdo.compositePhenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table sdo.countParameterValue (parameterId bigint not null, value int, primary key (parameterId));
create table sdo.countValue (observationId bigint not null, value int, primary key (observationId));
create table sdo.featureOfInterest (featureOfInterestId bigint identity not null, featureOfInterestTypeId bigint not null, identifier varchar(255), codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), geom GEOMETRY, descriptionXml varchar(MAX), url varchar(255), primary key (featureOfInterestId));
create table sdo.featureOfInterestType (featureOfInterestTypeId bigint identity not null, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId));
create table sdo.featureRelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId));
create table sdo.geometryValue (observationId bigint not null, value GEOMETRY, primary key (observationId));
create table sdo.i18nfeatureOfInterest (id bigint identity not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table sdo.i18nobservableProperty (id bigint identity not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table sdo.i18noffering (id bigint identity not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table sdo.i18nprocedure (id bigint identity not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id));
create table sdo.numericParameterValue (parameterId bigint not null, value double precision, unitId bigint, primary key (parameterId));
create table sdo.numericValue (observationId bigint not null, value double precision, primary key (observationId));
create table sdo.observableProperty (observablePropertyId bigint identity not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), disabled char(1) default 'F' not null check (disabled in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), primary key (observablePropertyId));
create table sdo.observation (observationId bigint identity not null, seriesId bigint not null, phenomenonTimeStart datetime2 not null, phenomenonTimeEnd datetime2 not null, resultTime datetime2 not null, identifier varchar(255), codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), child char(1) default 'F' not null check (child in ('T','F')), parent char(1) default 'F' not null check (parent in ('T','F')), validTimeStart datetime2 default NULL, validTimeEnd datetime2 default NULL, samplingGeometry GEOMETRY, unitId bigint, primary key (observationId));
create table sdo.observationConstellation (observationConstellationId bigint identity not null, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), primary key (observationConstellationId));
create table sdo.observationHasOffering (observationId bigint not null, offeringId bigint not null, primary key (observationId, offeringId));
create table sdo.observationType (observationTypeId bigint identity not null, observationType varchar(255) not null, primary key (observationTypeId));
create table sdo.offering (offeringId bigint identity not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), disabled char(1) default 'F' not null check (disabled in ('T','F')), primary key (offeringId));
create table sdo.offeringAllowedFeatureType (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId));
create table sdo.offeringAllowedObservationType (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId));
create table sdo.offeringHasRelatedFeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId));
create table sdo.parameter (parameterId bigint identity not null, observationId bigint not null, name varchar(255) not null, primary key (parameterId));
create table sdo.procedureDescriptionFormat (procedureDescriptionFormatId bigint identity not null, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId));
create table sdo.relatedFeature (relatedFeatureId bigint identity not null, featureOfInterestId bigint not null, primary key (relatedFeatureId));
create table sdo.relatedFeatureHasRole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table sdo.relatedFeatureRole (relatedFeatureRoleId bigint identity not null, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId));
create table sdo.resultTemplate (resultTemplateId bigint identity not null, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, featureOfInterestId bigint not null, identifier varchar(255) not null, resultStructure varchar(MAX) not null, resultEncoding varchar(MAX) not null, primary key (resultTemplateId));
create table sdo.sensorSystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId));
create table sdo.series (seriesId bigint identity not null, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), published char(1) default 'T' not null check (published in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), firstTimeStamp datetime2, lastTimeStamp datetime2, firstNumericValue double precision, lastNumericValue double precision, unitId bigint, primary key (seriesId));
create table sdo.sweDataArrayValue (observationId bigint not null, value varchar(MAX), primary key (observationId));
create table sdo.textParameterValue (parameterId bigint not null, value varchar(255), primary key (parameterId));
create table sdo.textValue (observationId bigint not null, value varchar(MAX), primary key (observationId));
create table sdo.unit (unitId bigint identity not null, unit varchar(255) not null, primary key (unitId));
create table sdo.validProcedureTime (validProcedureTimeId bigint identity not null, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime2 not null, endTime datetime2, descriptionXml varchar(MAX) not null, primary key (validProcedureTimeId));
alter table sdo.[procedure] add constraint procIdentifierUK unique (identifier);
create index booleanParamIdx on sdo.booleanParameterValue (value);
create index categoryParamIdx on sdo.categoryParameterValue (value);
alter table sdo.codespace add constraint codespaceUK unique (codespace);
create index countParamIdx on sdo.countParameterValue (value);
alter table sdo.featureOfInterest add constraint foiIdentifierUK unique (identifier);
alter table sdo.featureOfInterest add constraint featureUrl unique (url);
create spatial index featureGeomIdx on sdo.featureOfInterest (geom);
alter table sdo.featureOfInterestType add constraint featureTypeUK unique (featureOfInterestType);
alter table sdo.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on sdo.i18nfeatureOfInterest (objectId);
alter table sdo.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on sdo.i18nobservableProperty (objectId);
alter table sdo.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on sdo.i18noffering (objectId);
alter table sdo.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on sdo.i18nprocedure (objectId);
create index quantityParamIdx on sdo.numericParameterValue (value);
alter table sdo.observableProperty add constraint obsPropIdentifierUK unique (identifier);
alter table sdo.observation add constraint obsIdentifierUK unique (identifier);
create index obsSeriesIdx on sdo.observation (seriesId);
create index obsPhenTimeStartIdx on sdo.observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on sdo.observation (phenomenonTimeEnd);
create index obsResultTimeIdx on sdo.observation (resultTime);
create spatial index samplingGeomIdx on sdo.observation (samplingGeometry);
alter table sdo.observationConstellation add constraint obsnConstellationIdentity unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on sdo.observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on sdo.observationConstellation (procedureId);
create index obsConstOfferingIdx on sdo.observationConstellation (offeringId);
create index obshasoffobservationidx on sdo.observationHasOffering (observationId);
create index obshasoffofferingidx on sdo.observationHasOffering (offeringId);
alter table sdo.observationType add constraint observationTypeUK unique (observationType);
alter table sdo.offering add constraint offIdentifierUK unique (identifier);
create index paramNameIdx on sdo.parameter (name);
alter table sdo.procedureDescriptionFormat add constraint procDescFormatUK unique (procedureDescriptionFormat);
alter table sdo.relatedFeatureRole add constraint relFeatRoleUK unique (relatedFeatureRole);
create index resultTempOfferingIdx on sdo.resultTemplate (offeringId);
create index resultTempeObsPropIdx on sdo.resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on sdo.resultTemplate (procedureId);
create index resultTempIdentifierIdx on sdo.resultTemplate (identifier);
alter table sdo.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId);
create index seriesFeatureIdx on sdo.series (featureOfInterestId);
create index seriesObsPropIdx on sdo.series (observablePropertyId);
create index seriesProcedureIdx on sdo.series (procedureId);
create index textParamIdx on sdo.textParameterValue (value);
alter table sdo.unit add constraint unitUK unique (unit);
create index validProcedureTimeStartTimeIdx on sdo.validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on sdo.validProcedureTime (endTime);
alter table sdo.[procedure] add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references sdo.procedureDescriptionFormat;
alter table sdo.[procedure] add constraint procCodespaceIdentifierFk foreign key (codespace) references sdo.codespace;
alter table sdo.[procedure] add constraint procCodespaceNameFk foreign key (codespaceName) references sdo.codespace;
alter table sdo.[procedure] add constraint typeOfFk foreign key (typeOf) references sdo.[procedure];
alter table sdo.blobValue add constraint observationBlobValueFk foreign key (observationId) references sdo.observation;
alter table sdo.booleanParameterValue add constraint parameterBooleanValueFk foreign key (parameterId) references sdo.parameter;
alter table sdo.booleanValue add constraint observationBooleanValueFk foreign key (observationId) references sdo.observation;
alter table sdo.categoryParameterValue add constraint parameterCategoryValueFk foreign key (parameterId) references sdo.parameter;
alter table sdo.categoryParameterValue add constraint catParamValueUnitFk foreign key (unitId) references sdo.unit;
alter table sdo.categoryValue add constraint observationCategoryValueFk foreign key (observationId) references sdo.observation;
alter table sdo.complexValue add constraint observationComplexValueFk foreign key (observationId) references sdo.observation;
alter table sdo.compositeObservation add constraint observationChildFk foreign key (childObservationId) references sdo.observation;
alter table sdo.compositeObservation add constraint observationParentFK foreign key (observationId) references sdo.complexValue;
alter table sdo.compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references sdo.observableProperty;
alter table sdo.compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references sdo.observableProperty;
alter table sdo.countParameterValue add constraint parameterCountValueFk foreign key (parameterId) references sdo.parameter;
alter table sdo.countValue add constraint observationCountValueFk foreign key (observationId) references sdo.observation;
alter table sdo.featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references sdo.featureOfInterestType;
alter table sdo.featureOfInterest add constraint featureCodespaceIdentifierFk foreign key (codespace) references sdo.codespace;
alter table sdo.featureOfInterest add constraint featureCodespaceNameFk foreign key (codespaceName) references sdo.codespace;
alter table sdo.featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references sdo.featureOfInterest;
alter table sdo.featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references sdo.featureOfInterest;
alter table sdo.geometryValue add constraint observationGeometryValueFk foreign key (observationId) references sdo.observation;
alter table sdo.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references sdo.featureOfInterest;
alter table sdo.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references sdo.observableProperty;
alter table sdo.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references sdo.offering;
alter table sdo.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references sdo.[procedure];
alter table sdo.numericParameterValue add constraint parameterNumericValueFk foreign key (parameterId) references sdo.parameter;
alter table sdo.numericParameterValue add constraint quanParamValueUnitFk foreign key (unitId) references sdo.unit;
alter table sdo.numericValue add constraint observationNumericValueFk foreign key (observationId) references sdo.observation;
alter table sdo.observableProperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references sdo.codespace;
alter table sdo.observableProperty add constraint obsPropCodespaceNameFk foreign key (codespaceName) references sdo.codespace;
alter table sdo.observation add constraint observationSeriesFk foreign key (seriesId) references sdo.series;
alter table sdo.observation add constraint obsCodespaceIdentifierFk foreign key (codespace) references sdo.codespace;
alter table sdo.observation add constraint obsCodespaceNameFk foreign key (codespaceName) references sdo.codespace;
alter table sdo.observation add constraint observationUnitFk foreign key (unitId) references sdo.unit;
alter table sdo.observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references sdo.observableProperty;
alter table sdo.observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references sdo.[procedure];
alter table sdo.observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references sdo.observationType;
alter table sdo.observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references sdo.offering;
alter table sdo.observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references sdo.offering;
alter table sdo.observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references sdo.observation;
alter table sdo.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references sdo.codespace;
alter table sdo.offering add constraint offCodespaceNameFk foreign key (codespaceName) references sdo.codespace;
alter table sdo.offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references sdo.featureOfInterestType;
alter table sdo.offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references sdo.offering;
alter table sdo.offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references sdo.observationType;
alter table sdo.offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references sdo.offering;
alter table sdo.offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references sdo.offering;
alter table sdo.offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references sdo.relatedFeature;
alter table sdo.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references sdo.observation;
alter table sdo.relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references sdo.featureOfInterest;
alter table sdo.relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references sdo.relatedFeatureRole;
alter table sdo.relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references sdo.relatedFeature;
alter table sdo.resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references sdo.offering;
alter table sdo.resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references sdo.observableProperty;
alter table sdo.resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references sdo.[procedure];
alter table sdo.resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references sdo.featureOfInterest;
alter table sdo.sensorSystem add constraint procedureChildFk foreign key (childSensorId) references sdo.[procedure];
alter table sdo.sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references sdo.[procedure];
alter table sdo.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references sdo.featureOfInterest;
alter table sdo.series add constraint seriesObPropFk foreign key (observablePropertyId) references sdo.observableProperty;
alter table sdo.series add constraint seriesProcedureFk foreign key (procedureId) references sdo.[procedure];
alter table sdo.series add constraint seriesUnitFk foreign key (unitId) references sdo.unit;
alter table sdo.sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references sdo.observation;
alter table sdo.textParameterValue add constraint parameterTextValueFk foreign key (parameterId) references sdo.parameter;
alter table sdo.textValue add constraint observationTextValueFk foreign key (observationId) references sdo.observation;
alter table sdo.validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references sdo.[procedure];
alter table sdo.validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references sdo.procedureDescriptionFormat;
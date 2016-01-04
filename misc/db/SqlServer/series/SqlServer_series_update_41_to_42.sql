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

-- Can also be required in existing SOS 4.2.0-SNAPSHOT versions

--update numeric columns to double
ALTER TABLE dbo.numericvalue ALTER COLUMN value double precision;
ALTER TABLE dbo.series ALTER COLUMN firstNumericValue double precision;
ALTER TABLE dbo.series ALTER COLUMN lastNumericValue double precision;

-- add published flag to series table
ALTER TABLE dbo.series ADD published char(1) default 'T' check (published in ('T','F'));
UPDATE dbo.series SET dbo.series.published = 'T';
ALTER TABLE dbo.series ALTER COLUMN published char(1) NOT NULL;

-- Required to update from SOS 4.1 to 4.2

-- update observation table
GO
EXEC sp_rename 'dbo.observation.codespaceId' , 'codespace', 'COLUMN';
GO
ALTER TABLE dbo.observation ADD name varchar(255);
ALTER TABLE dbo.observation ADD codespacename bigint;
ALTER TABLE dbo.observation add constraint obsCodespaceNameFk foreign key (codespacename) references dbo.codespace;

-- update offering table
ALTER TABLE dbo.offering ADD codespace bigint;
ALTER TABLE dbo.offering ADD codespacename bigint;
ALTER TABLE dbo.offering ADD description varchar(255);
ALTER TABLE dbo.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
ALTER TABLE dbo.offering add constraint offCodespaceNameFk foreign key (codespacename) references dbo.codespace;

-- update procedure table
ALTER TABLE dbo.[procedure] ADD codespace bigint;
ALTER TABLE dbo.[procedure] ADD name varchar(255);
ALTER TABLE dbo.[procedure] ADD codespacename bigint;
ALTER TABLE dbo.[procedure] ADD description varchar(255);
ALTER TABLE dbo.[procedure] add constraint procCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
ALTER TABLE dbo.[procedure] add constraint procCodespaceNameFk foreign key (codespacename) references dbo.codespace;

-- update observaleProperty table
ALTER TABLE dbo.observableproperty ADD codespace bigint;
ALTER TABLE dbo.observableproperty ADD name varchar(255);
ALTER TABLE dbo.observableproperty ADD codespacename bigint;
ALTER TABLE dbo.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
ALTER TABLE dbo.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references dbo.codespace;

-- update featureOfInterest table
GO
EXEC sp_rename 'dbo.featureofinterest.codespaceId' , 'codespace', 'COLUMN';
GO
ALTER TABLE dbo.featureofinterest ALTER COLUMN name varchar(255);
ALTER TABLE dbo.featureofinterest ADD codespacename bigint;
ALTER TABLE dbo.featureofinterest ADD  description varchar(255);
ALTER TABLE dbo.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references dbo.codespace;

-- create multiligualism tables (optional, required for multiligualism support)

-- i18n featureOfInterest
create table dbo.i18nfeatureOfInterest (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
ALTER TABLE dbo.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on dbo.i18nfeatureOfInterest (objectId);
ALTER TABLE dbo.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references dbo.featureOfInterest;
create sequence dbo.i18nfeatureOfInterestId_seq;

-- i18n observableProperty
create table dbo.i18nobservableProperty (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
ALTER TABLE dbo.i18nobservableProperty add constraint i18nobsPropIdentity  unique (objectId, locale);
create index i18nObsPropIdx on dbo.i18nobservableProperty (objectId);
ALTER TABLE dbo.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references observableProperty;
create sequence dbo.i18nObsPropId_seq;

-- i18n offering
create table dbo.i18noffering (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
alter table dbo.i18noffering add constraint i18nOfferingIdentity  unique (objectId, locale);
create index i18nOfferingIdx on dbo.i18noffering (objectId);
alter table dbo.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references dbo.offering;
create sequence .dbo.i18nOfferingId_seq;

-- i18n procedure
create table dbo.i18nprocedure (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id));
alter table dbo.i18nprocedure add constraint i18nProcedureIdentity  unique (objectId, locale);
create index i18nProcedureIdx on dbo.i18nprocedure (objectId);
alter table dbo.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references dbo.[procedure];
create sequence dbo.i18nProcedureId_seq;
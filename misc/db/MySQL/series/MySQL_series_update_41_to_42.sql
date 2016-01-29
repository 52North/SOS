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

-- Can also be required in existing SOS 4.2.0-SNAPSHOT versions

-- update numeric columns to double
ALTER TABLE sos.numericValue MODIFY value DOUBLE PRECISION;
ALTER TABLE sos.series MODIFY firstNumericValue DOUBLE PRECISION;
ALTER TABLE sos.series MODIFY lastNumericValue DOUBLE PRECISION;

-- add published flag to series table
ALTER TABLE sos.series ADD COLUMN published char(1) default 'T' not null;

-- Required to update from SOS 4.1 to 4.2

-- update observation table
ALTER TABLE sos.observation CHANGE codespaceid codespace bigint;
ALTER TABLE sos.observation ADD COLUMN name varchar(255);
ALTER TABLE sos.observation ADD COLUMN codespacename bigint;
ALTER TABLE sos.observation add constraint obsCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);

-- update offering table
ALTER TABLE sos.offering ADD COLUMN codespace bigint;
ALTER TABLE sos.offering ADD COLUMN codespacename bigint;
ALTER TABLE sos.offering ADD COLUMN description varchar(255);
ALTER TABLE sos.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
ALTER TABLE sos.offering add constraint offCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);

-- update procedure table
ALTER TABLE sos.`procedure` ADD COLUMN codespace bigint;
ALTER TABLE sos.`procedure` ADD COLUMN name varchar(255);
ALTER TABLE sos.`procedure` ADD COLUMN codespacename bigint;
ALTER TABLE sos.`procedure` ADD COLUMN description varchar(255);
ALTER TABLE sos.`procedure` add constraint procCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
ALTER TABLE sos.`procedure` add constraint procCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);

-- update observaleProperty table
ALTER TABLE sos.observableproperty ADD COLUMN codespace bigint;
ALTER TABLE sos.observableproperty ADD COLUMN name varchar(255);
ALTER TABLE sos.observableproperty ADD COLUMN codespacename bigint;
ALTER TABLE sos.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
ALTER TABLE sos.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);

-- update featureOfInterest table
ALTER TABLE sos.featureofinterest CHANGE codespaceid codespace bigint;
ALTER TABLE sos.featureofinterest MODIFY name TYPE varchar(255);
ALTER TABLE sos.featureofinterest ADD COLUMN codespacename bigint;
ALTER TABLE sos.featureofinterest ADD COLUMN description varchar(255);
ALTER TABLE sos.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);

-- create multiligualism tables (optional, required for multiligualism support)

-- i18n featureOfInterest
create table sos.i18nfeatureOfInterest (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
ALTER TABLE sos.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on sos.i18nfeatureOfInterest (objectId);
ALTER TABLE sos.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references sos.featureOfInterest (featureOfInterestId);

-- i18n observableProperty
create table sos.i18nobservableProperty (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
ALTER TABLE sos.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on sos.i18nobservableProperty (objectId);
ALTER TABLE sos.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references sos.observableProperty (observablePropertyId);

-- i18n offering
create table sos.i18noffering (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
alter table sos.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on sos.i18noffering (objectId);
alter table sos.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references sos.offering (offeringId);

-- i18n procedure
create table sos.i18nprocedure (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id)) ENGINE=InnoDB;
alter table sos.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on sos.i18nprocedure (objectId);
alter table sos.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references sos.`procedure` (procedureId);

SET FOREIGN_KEY_CHECKS = 1;
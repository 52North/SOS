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

--update numeric columns to double
ALTER TABLE NUMERICVALUE MODIFY VALUE DOUBLE PRECISION;

-- update OBSERVATION table
ALTER TABLE OBSERVATION RENAME COLUMN CODESPACEID TO CODESPACE;
ALTER TABLE OBSERVATION ADD COLUMN NAME varchar(255);
ALTER TABLE OBSERVATION ADD COLUMN CODESPACENAME number(19,0);
ALTER TABLE OBSERVATION ADD CONSTRAINT obsCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OBSERVATION ADD CONSTRAINT obsCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;

-- update OFFERING table
ALTER TABLE OFFERING ADD COLUMN CODESPACE number(19,0);
ALTER TABLE OFFERING ADD COLUMN CODESPACENAME number(19,0);
ALTER TABLE OFFERING ADD COLUMN DESCRIPTION varchar(255);
ALTER TABLE OFFERING ADD CONSTRAINT offCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OFFERING ADD CONSTRAINT offCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;

-- update procedure table
ALTER TABLE "procedure" ADD COLUMN CODESPACE number(19,0);
ALTER TABLE "procedure" ADD COLUMN NAME varchar(255);
ALTER TABLE "procedure" ADD COLUMN CODESPACENAME number(19,0);
ALTER TABLE "procedure" ADD COLUMN DESCRIPTION varchar(255);
ALTER TABLE "procedure" ADD CONSTRAINT procCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE "procedure" ADD CONSTRAINT procCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;

-- update observaleProperty table
ALTER TABLE OBSERVABLEPROPERTY ADD COLUMN CODESPACE number(19,0);
ALTER TABLE OBSERVABLEPROPERTY ADD COLUMN NAME varchar(255);
ALTER TABLE OBSERVABLEPROPERTY ADD COLUMN CODESPACENAME number(19,0);
ALTER TABLE OBSERVABLEPROPERTY ADD CONSTRAINT obsPropCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OBSERVABLEPROPERTY ADD CONSTRAINT obsPropCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;

-- update featureOfInterest table
ALTER TABLE FEATUREOFINTEREST RENAME COLUMN CODESPACEID TO CODESPACE;
ALTER TABLE FEATUREOFINTEREST ALTER COLUMN NAME TYPE varchar(255);
ALTER TABLE FEATUREOFINTEREST ADD COLUMN CODESPACENAME number(19,0);
ALTER TABLE FEATUREOFINTEREST ADD COLUMN DESCRIPTION varchar(255);
ALTER TABLE FEATUREOFINTEREST ADD CONSTRAINT featureCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;

-- create multiligualism tables (optional, required for multiligualism support)

-- i18n featureOfInterest
CREATE TABLE i18nfeatureOfInterest (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar(255) not null, NAME varchar(255), DESCRIPTION varchar(255), primary key (ID));
ALTER TABLE i18nfeatureOfInterest ADD CONSTRAINT i18nFeatureIdentity unique (OBJECTID, LOCALE);
CREATE index i18nFeatureIdx ON i18nfeatureOfInterest (OBJECTID);
ALTER TABLE i18nfeatureOfInterest ADD CONSTRAINT i18nFeatureFeatureFk FOREIGN KEY (OBJECTID) REFERENCES featureOfInterest;
CREATE SEQUENCE i18nfeatureOfInterestId_seq;

-- i18n observableProperty
CREATE TABLE i18nobservableProperty (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar(255) not null, NAME varchar(255), DESCRIPTION varchar(255), primary key (ID));
ALTER TABLE i18nobservableProperty ADD CONSTRAINT i18nobsPropIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nObsPropIdx ON i18nobservableProperty (OBJECTID);
ALTER TABLE i18nobservableProperty ADD CONSTRAINT i18nObsPropObsPropFk FOREIGN KEY (OBJECTID) REFERENCES observableProperty;
CREATE SEQUENCE i18nObsPropId_seq;

-- i18n OFFERING
CREATE TABLE i18nOFFERING (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar(255) not null, NAME varchar(255), DESCRIPTION varchar(255), primary key (ID));
ALTER TABLE i18nOFFERING ADD CONSTRAINT i18nOfferingIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nOfferingIdx ON i18nOFFERING (OBJECTID);
ALTER TABLE i18nOFFERING ADD CONSTRAINT i18nOfferingOfferingFk FOREIGN KEY (OBJECTID) REFERENCES OFFERING;
CREATE SEQUENCE i18nOfferingId_seq;

-- i18n procedure
CREATE TABLE i18nprocedure (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar(255) not null, NAME varchar(255), DESCRIPTION varchar(255), SHOTNAME varchar(255), LONGNAME varchar(255), primary key (ID));
ALTER TABLE i18nprocedure ADD CONSTRAINT i18nProcedureIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nProcedureIdx ON i18nprocedure (OBJECTID);
ALTER TABLE i18nprocedure ADD CONSTRAINT i18nProcedureProcedureFk FOREIGN KEY (OBJECTID) REFERENCES "procedure";
CREATE SEQUENCE i18nProcedureId_seq;
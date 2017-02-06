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

-- ###############################################################################################
-- # !!! Before you execute this scricpt we recommend to create a dump from the current database #
-- ###############################################################################################

-- create series table and sequence add constraints to series
CREATE sequence seriesId_seq;
create table series (seriesId number(19,0) not null, featureOfInterestId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, deleted char(1 char) default 'F' not null check (deleted in ('T','F')), firstTimeStamp timestamp, lastTimeStamp timestamp, firstNumericValue DOUBLE PRECISION, lastNumericValue DOUBLE PRECISION, unitId number(19,0), primary key (seriesId))
ALTER TABLE series add constraint seriesFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
ALTER TABLE series add constraint seriesObPropFk foreign key (observablePropertyId) references observableProperty;
ALTER TABLE series add constraint seriesProcedureFk foreign key (procedureId) references procedure;
alter table series add constraint seriesUnitFk foreign key (unitId) references unit;
alter table series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId);

-- add series column to observation table
ALTER TABLE observation ADD seriesId number(19,0);
ALTER TABLE observation add constraint observationSeriesFk foreign key (seriesId) references series;

-- create series
INSERT INTO series (SELECT seriesId_seq.nextval, o.featureofinterestid, o.observablepropertyid, o.procedureid, 'F' FROM (SELECT DISTINCT featureofinterestid, observablepropertyid, procedureid FROM observation) o);

-- update observations, set series
UPDATE observation o SET seriesid = (SELECT s.seriesid FROM series s WHERE o.featureofinterestid = s.featureofinterestid AND o.observablepropertyid = s.observablepropertyid AND o.procedureid = s.procedureid);

-- set series column to not null and add unique constraint
ALTER TABLE observation MODIFY (seriesId NOT NULL);
ALTER TABLE observation ADD unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);

-- drop old constraints and drop old columns
ALTER TABLE observation DROP COLUMN featureofinterestid CASCADE CONSTRAINTS ;
ALTER TABLE observation DROP COLUMN observablepropertyid CASCADE CONSTRAINTS ;
ALTER TABLE observation DROP COLUMN procedureid CASCADE CONSTRAINTS ;

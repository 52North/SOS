--
-- Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
create table series (seriesId bigint not null auto_increment, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, deleted char(1) default 'F' not null, primary key (seriesId), unique (featureOfInterestId, observablePropertyId, procedureId)) ENGINE=InnoDB;
create index seriesFeatureIdx on series (featureOfInterestId);
create index seriesObsPropIdx on series (observablePropertyId);
create index seriesProcedureIdx on series (procedureId);
alter table series add index seriesFeatureFk (featureOfInterestId), add constraint seriesFeatureFk foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table series add index seriesObPropFk (observablePropertyId), add constraint seriesObPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table series add index seriesProcedureFk (procedureId), add constraint seriesProcedureFk foreign key (procedureId) references `procedure` (procedureId);

-- add series column to observation table
ALTER TABLE observation ADD COLUMN seriesId int8;
create index obsSeriesIdx on observation (seriesId);
alter table observation add index observationSeriesFk (seriesId), add constraint observationSeriesFk foreign key (seriesId) references series (seriesId);

-- create series
INSERT INTO series (featureofinterestid, observablepropertyid, procedureid, deleted) SELECT o.featureofinterestid as f, o.observablepropertyid, o.procedureid, 'F' FROM (SELECT Auto_increment AS next FROM information_schema.tables WHERE table_name='series') i, (SELECT DISTINCT featureofinterestid, observablepropertyid, procedureid FROM observation) o;

-- update observations, set series
SET SQL_SAFE_UPDATES=0;
UPDATE observation o, series s SET o.seriesid = s.seriesid WHERE o.featureofinterestid = s.featureofinterestid AND o.observablepropertyid = s.observablepropertyid AND o.procedureid = s.procedureid;
SET SQL_SAFE_UPDATES=1;

-- set series column to not null and add unique constraint
ALTER TABLE observation MODIFY seriesId int8 not null;
ALTER TABLE observation ADD unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);

-- drop old constraints and drop old columnsobservation
alter table observation drop foreign key observationFeatureFk;
alter table observation drop foreign key observationObPropFk;
alter table observation drop foreign key observationProcedureFk;
ALTER TABLE observation DROP INDEX featureofinterestid;
ALTER TABLE observation DROP COLUMN featureofinterestid;
ALTER TABLE observation DROP COLUMN observablepropertyid;
ALTER TABLE observation DROP COLUMN procedureid;
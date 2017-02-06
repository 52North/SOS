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
-- # !!! Before you execute this script we recommend to create a dump from the current database  #
-- ###############################################################################################

-- create series table and sequence add constraints to series
CREATE sequence public.seriesId_seq;
create table public.series (seriesId int8 not null, featureOfInterestId int8 not null, observablePropertyId int8 not null, procedureId int8 not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), firstTimeStamp timestamp, lastTimeStamp timestamp, firstNumericValue double precision, lastNumericValue double precision, unitId int8, primary key (seriesId));
ALTER TABLE public.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId);
CREATE INDEX seriesFeatureIdx on public.series (featureOfInterestId);
CREATE INDEX seriesObsPropIdx on public.series (observablePropertyId);
CREATE INDEX seriesProcedureIdx on public.series (procedureId);
ALTER TABLE public.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
ALTER TABLE public.series add constraint seriesObPropFk foreign key (observablePropertyId) references observableProperty;
ALTER TABLE public.series add constraint seriesProcedureFk foreign key (procedureId) references "procedure";
ALTER TABLE public.series add constraint seriesUnitFk foreign key (unitId) references unit;

-- add series column to observation table
ALTER TABLE public.observation ADD COLUMN seriesId int8;
ALTER TABLE public.observation add constraint observationSeriesFk foreign key (seriesId) references series;

-- create series
INSERT INTO public.series (SELECT nextval('seriesId_seq'), o.featureofinterestid, o.observablepropertyid, o.procedureid, 'F' FROM (SELECT DISTINCT featureofinterestid, observablepropertyid, procedureid FROM observation) o);

-- update observations, set series
UPDATE public.observation o SET seriesid = s.seriesid FROM public.series s WHERE o.featureofinterestid = s.featureofinterestid AND o.observablepropertyid = s.observablepropertyid AND o.procedureid = s.procedureid;

-- set series column to not null and add unique constraint
ALTER TABLE public.observation ALTER COLUMN seriesId SET NOT NULL;
ALTER TABLE public.observation add constraint observationIdentity unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);

-- drop old constraints and drop old columns
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS observation_featureofinterestid_observablepropertyid_proced_key;
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS observationfeaturefk;
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS observationobpropfk;
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS observationprocedurefk;
ALTER TABLE public.observation DROP COLUMN IF EXISTS featureofinterestid;
ALTER TABLE public.observation DROP COLUMN IF EXISTS observablepropertyid;
ALTER TABLE public.observation DROP COLUMN IF EXISTS procedureid;

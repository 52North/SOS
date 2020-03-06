--
-- Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

-- This update script fills the offeringid values in the series table.
-- !!! This script only works in the case that a each observation belongs to only one offering!!!
-- If the offeringid column is still filled with values, this statement should be omitted
UPDATE dbo.series ser SET offeringid = q.offeringid FROM (SELECT DISTINCT s.seriesid, off.offeringid FROM dbo.series s inner join dbo.observation o on s.seriesid = o.seriesid inner join dbo.observationhasoffering off on o.observationid = off.observationid) q WHERE q.seriesid = ser.seriesid;

-- Update offeringid column to NOT NULL
alter table dbo.series alter column offeringId set not null;

-- Set seriestype from value tables
update dbo.series set seriestype = 'quantity' where seriesid in (select distinct o.seriesid from dbo.observation o inner join dbo.numericvalue v on o.observationid = v.observationid);
update dbo.series set seriestype = 'count' where seriesid in (select distinct o.seriesid from dbo.observation o inner join dbo.countvalue v on o.observationid = v.observationid);
update dbo.series set seriestype = 'text' where seriesid in (select distinct o.seriesid from dbo.observation o inner join dbo.textvalue v on o.observationid = v.observationid);
update dbo.series set seriestype = 'category' where seriesid in (select distinct o.seriesid from dbo.observation o inner join dbo.categoryvalue v on o.observationid = v.observationid);
update dbo.series set seriestype = 'boolean' where seriesid in (select distinct o.seriesid from dbo.observation o inner join dbo.booleanvalue v on o.observationid = v.observationid);
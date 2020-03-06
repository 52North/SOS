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
SET SQL_SAFE_UPDATES=0;
UPDATE sos.series ser, (SELECT DISTINCT o.seriesid, off.offeringid FROM sos.observation o inner join sos.observationhasoffering off on o.observationid = off.observationid) q SET ser.offeringid = q.offeringid  WHERE q.seriesid = ser.seriesid;
SET SQL_SAFE_UPDATES=1;

-- Update offeringid column to NOT NULL
alter table sos.series alter column offeringId set not null;


-- Set seriestype from value tables
SET SQL_SAFE_UPDATES=0;
update sos.series set seriestype = 'quantity' where seriesid in (select distinct o.seriesid from sos.observation o inner join sos.numericvalue v on o.observationid = v.observationid);
update sos.series set seriestype = 'count' where seriesid in (select distinct o.seriesid from sos.observation o inner join sos.countvalue v on o.observationid = v.observationid);
update sos.series set seriestype = 'text' where seriesid in (select distinct o.seriesid from sos.observation o inner join sos.textvalue v on o.observationid = v.observationid);
update sos.series set seriestype = 'category' where seriesid in (select distinct o.seriesid from sos.observation o inner join sos.categoryvalue v on o.observationid = v.observationid);
update sos.series set seriestype = 'boolean' where seriesid in (select distinct o.seriesid from sos.observation o inner join sos.booleanvalue v on o.observationid = v.observationid);
SET SQL_SAFE_UPDATES=1;
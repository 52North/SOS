--
-- Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

-- alter table
alter table series add column offeringid int8;

-- drop and add constraint
alter table series drop constraint seriesIdentity;
alter table observation drop constraint obsIdentifierUK;
alter table series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId, offeringId);

-- create index
create index seriesOfferingIdx on series (offeringId);

-- create foreign keys
alter table series add constraint seriesOfferingFk foreign key (offeringId) references offering;


-- update series table (!!! Works only if each observation relates to one and the same offering!!!)
UPDATE series ser SET offeringid = q.offeringid FROM (SELECT DISTINCT s.seriesid, off.offeringid FROM series s 
inner join observation o on s.seriesid = o.seriesid 
inner join observationhasoffering off on o.observationid = off.observationid) q WHERE q.seriesid = ser.seriesid;
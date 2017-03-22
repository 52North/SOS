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
alter table sos.series add column offeringid int8;

-- drop and add constraint
alter table sos.series drop index seriesIdentity;
alter table sos.observation drop index obsIdentifierUK;
alter table sos.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId, offeringId);

-- create index
create index seriesOfferingIdx on sos.series (offeringId);

-- create foreign keys
alter table sos.series add constraint seriesOfferingFk foreign key (offeringId) references sos.offering;

-- update series table (!!! Works only if each observation relates to one and the same offering!!!)
SET SQL_SAFE_UPDATES=0;
UPDATE sos.series ser, (SELECT DISTINCT o.seriesid, off.offeringid FROM sos.observation o inner join sos.observationhasoffering off on o.observationid = off.observationid) q SET ser.offeringid = q.offeringid  WHERE q.seriesid = ser.seriesid;
SET SQL_SAFE_UPDATES=1;
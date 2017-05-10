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

ALTER TABLE procedure ADD istype char(1 char) default 'F' check (istype in ('T','F'));
ALTER TABLE procedure ADD isaggregation char(1 char) default 'F' check (isaggregation in ('T','F'));

ALTER TABLE procedure ADD typeof number(19,0);

alter table procedure add constraint typeoffk foreign key (procedureid) references procedure;

-- complex observation
ALTER TABLE observableproperty ADD hiddenchild char(1 char) default 'F' check (hiddenchild in ('T','F'));

ALTER TABLE observation ADD child char(1 char) default 'F' check (child in ('T','F'));
ALTER TABLE observation ADD parent char(1 char) default 'F' check (parent in ('T','F'));

create table complexValue (observationId number(19,0) not null, primary key (observationId));
alter table complexValue add constraint observationComplexValueFk foreign key (observationId) references observation;

create table compositeObservation (observationId number(19,0) not null, childObservationId number(19,0) not null, primary key (observationId, childObservationId));
alter table compositeObservation add constraint observationChildFk foreign key (childObservationId) references observation;
alter table compositeObservation add constraint observationParentFK foreign key (observationId) references complexValue;

-- spatial index
create index featureGeomIdx on featureOfInterest (geom)  INDEXTYPE IS MDSYS.SPATIAL_INDEX;
create index samplingGeomIdx on observation (samplingGeometry)  INDEXTYPE IS MDSYS.SPATIAL_INDEX;
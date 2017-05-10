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

ALTER TABLE dbo.[procedure] ADD istype char(1) default 'T' check (istype in ('T','F'));
ALTER TABLE dbo.[procedure] ADD isaggregation char(1) default 'T' check (isaggregation in ('T','F'));

ALTER TABLE dbo.[procedure] ADD typeof bigint;

ALTER TABLE dbo.[procedure] add constraint typeoffk foreign key (typeof) references dbo.[procedure];

-- complex observation
ALTER TABLE dbo.observableproperty ADD COLUMN hiddenchild char(1) default 'F' check (hiddenchild in ('T','F'));

ALTER TABLE dbo.observation ADD COLUMN child char(1) default 'F' check (child in ('T','F'));
ALTER TABLE dbo.observation ADD COLUMN parent char(1) default 'F' check (parent in ('T','F'));

create table dbo.complexValue (observationId bigint not null, primary key (observationId));
alter table dbo.complexValue add constraint observationComplexValueFk foreign key (observationId) references dbo.observation;

create table dbo.compositeObservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
alter table dbo.compositeObservation add constraint observationChildFk foreign key (childObservationId) references dbo.observation;
alter table dbo.compositeObservation add constraint observationParentFK foreign key (observationId) references dbo.complexValue;

-- spatial index
create spatial index featureGeomIdx on sdo.featureOfInterest (geom);
create spatial index samplingGeomIdx on sdo.observation (samplingGeometry);
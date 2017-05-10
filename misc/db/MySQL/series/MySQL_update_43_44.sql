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

ALTER TABLE sos.`procedure` ADD COLUMN istype char(1) default 'F';
ALTER TABLE sos.`procedure` ADD COLUMN isaggregation char(1) default 'F';


ALTER TABLE sos.`procedure` ADD COLUMN typeof bigint;

alter table sos.`procedure` add constraint typeoffk foreign key (typeof) references sos.`procedure` (procedureid);

-- complex observation
ALTER TABLE sos.observableproperty ADD COLUMN hiddenchild char(1) default 'F';

ALTER TABLE sos.observation ADD COLUMN child char(1) default 'F';
ALTER TABLE sos.observation ADD COLUMN parent char(1) default 'F';

create table sos.complexValue (observationId bigint not null comment 'Foreign Key (FK) to the related observation from the observation table. Contains "observation".observationid', primary key (observationId)) comment='Value table for complex observation' ENGINE=InnoDB;
alter table sos.complexValue add constraint observationComplexValueFk foreign key (observationId) references sos.observation (observationId);

create table sos.compositeObservation (observationId bigint not null comment 'Foreign Key (FK) to the related parent complex observation. Contains "observation".observationid', childObservationId bigint not null comment 'Foreign Key (FK) to the related child complex observation. Contains "observation".observationid', primary key (observationId, childObservationId)) comment='Relation table for complex parent/child observations' ENGINE=InnoDB;
alter table sos.compositeObservation add constraint observationChildFk foreign key (childObservationId) references sos.observation (observationId);
alter table sos.compositeObservation add constraint observationParentFK foreign key (observationId) references sos.complexValue (observationId);
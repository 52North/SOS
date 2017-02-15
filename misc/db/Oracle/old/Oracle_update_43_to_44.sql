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

-- add tables for complex observation
create table complexValue (observationId number(19,0) not null, primary key (observationId));
comment on table complexValue is 'Value table for complex observation';
comment on column complexValue.observationId is 'Foreign Key (FK) to the related observation from the observation table. Contains "observation".observationid';
create table compositeObservation (observationId number(19,0) not null, childObservationId number(19,0) not null, primary key (observationId, childObservationId));
comment on table compositeObservation is 'Relation table for complex parent/child observations';
comment on column compositeObservation.observationId is 'Foreign Key (FK) to the related parent complex observation. Contains "observation".observationid';
comment on column compositeObservation.childObservationId is 'Foreign Key (FK) to the related child complex observation. Contains "observation".observationid';
alter table complexValue add constraint observationComplexValueFk foreign key (observationId) references observation;
alter table compositeObservation add constraint observationChildFk foreign key (childObservationId) references observation;
alter table compositeObservation add constraint observationParentFK foreign key (observationId) references complexValue;

-- add columns for complex observation to onbservation table 
ALTER TABLE observation ADD COLUMN child char(1 char) default 'F' not null check (child in ('T','F'));
ALTER TABLE observation ADD COLUMN parent char(1 char) default 'F' not null check (parent in ('T','F'));
comment on column observation.child is 'Flag to indicate that this observation is a child observation for complex observation';
comment on column observation.parent is 'Flag to indicate that this observation is a parent observation for complex observation';

-- spatial index
create index featureGeomIdx on featureOfInterest (geom)  INDEXTYPE IS MDSYS.SPATIAL_INDEX;
create index samplingGeomIdx on observation (samplingGeometry)  INDEXTYPE IS MDSYS.SPATIAL_INDEX;

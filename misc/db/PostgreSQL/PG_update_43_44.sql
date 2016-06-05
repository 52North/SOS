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

ALTER TABLE public."procedure" ADD COLUMN istype char(1) default 'F' check (istype in ('T','F'));
ALTER TABLE public."procedure" ADD COLUMN isaggregation char(1) default 'F' check (isaggregation in ('T','F'));

ALTER TABLE public."procedure" ADD COLUMN typeof int8;

alter table public."procedure" add constraint typeoffk foreign key (procedureid) references public."procedure";

-- complex observation
ALTER TABLE public.observableproperty ADD COLUMN hiddenchild char(1) default 'F' check (hiddenchild in ('T','F'));

ALTER TABLE public.observation ADD COLUMN child char(1) default 'F' check (child in ('T','F'));
ALTER TABLE public.observation ADD COLUMN parent char(1) default 'F' check (parent in ('T','F'));

create table public.complexValue (observationId int8 not null, primary key (observationId));
alter table public.complexValue add constraint observationComplexValueFk foreign key (observationId) references public.observation;

create table public.compositeObservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
alter table public.compositeObservation add constraint observationChildFk foreign key (childObservationId) references public.observation;
alter table public.compositeObservation add constraint observationParentFK foreign key (observationId) references public.complexValue;

-- spatial index
create index featureGeomIdx on public.featureOfInterest USING GIST (geom);
create index samplingGeomIdx on public.observation USING GIST (samplingGeometry);

-- XML based parameter values
CREATE TABLE public.xmlparametervalue (parameterid bigint PRIMARY KEY, value text);
ALTER TABLE public.xmlparametervalue ADD CONSTRAINT parameterxmlvaluefk FOREIGN KEY (parameterid) REFERENCES public.parameter (parameterid)
CREATE INDEX xmlparamidx ON public.xmlparametervalue USING btree (value);

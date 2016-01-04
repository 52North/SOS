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

-- add published flag to series table
ALTER TABLE public.series ADD published char(1) NOT NULL default 'T' check (published in ('T','F'));

-- add station table
create sequence public.stationId_seq;
create table public.station (stationId int8 not null, identifier varchar(255) not null, codespace int8, name varchar(255), codespacename int8, description varchar(255), primary key (stationId));
alter table public.station add constraint stationIdentifierUK unique (identifier);

-- add network table
create sequence public.networkId_seq;
create table public.network (networkId int8 not null, identifier varchar(255) not null, codespace int8, name varchar(255), codespacename int8, description varchar(255), primary key (networkId));
alter table public.network add constraint networkIdentifierUK unique (identifier);

-- add station and network columns to
alter table public.samplingPoint add column station int8;
alter table public.samplingPoint add constraint sampPointStationFk foreign key (station) references public.station;
alter table public.samplingPoint add column network int8;
alter table public.samplingPoint add constraint sampPointNetworkFk foreign key (network) references public.network;

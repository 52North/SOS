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

-- create table
create table public.seriesHasOffering (seriesId int8 not null, offeringId int8 not null, primary key (seriesId, offeringId));

-- create indices
create index serieshasoffseriesidx on public.seriesHasOffering (seriesId);
create index serieshasoffofferingidx on public.seriesHasOffering (offeringId);

-- create foreign keys
alter table public.seriesHasOffering add constraint seriesOfferingFk foreign key (offeringId) references public.offering;
alter table public.seriesHasOffering add constraint FK_ehsn5rny4c7pg5mfk5b7pjcoc foreign key (seriesId) references public.series;

-- update table with values
INSERT INTO public.serieshasoffering (SELECT DISTINCT o.seriesid, oho.offeringid FROM public.observation o JOIN public.observationhasoffering oho ON o.observationid = oho.observationid);

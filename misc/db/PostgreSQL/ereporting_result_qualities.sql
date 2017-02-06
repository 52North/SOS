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

ALTER TABLE public.observation ADD COLUMN primaryobservation varchar(255);
-- hour or var, ...
UPDATE public.observation SET primaryobservation = 'hour';
ALTER TABLE public.observation ALTER COLUMN primaryobservation SET NOT NULL;

ALTER TABLE public.observation ADD COLUMN uncertaintyEstimation FLOAT8;
ALTER TABLE public.observation ADD COLUMN dataCaptureFlag character(1);
ALTER TABLE public.observation ADD COLUMN dataCaptureg FLOAT8;
ALTER TABLE public.observation ADD COLUMN timeCoverageFlag character(1);
ALTER TABLE public.observation ADD CONSTRAINT "observation_timecoverage_flag_check" CHECK (timeCoverageFlag = ANY(ARRAY['T'::bpchar, 'F'::bpchar]));
ALTER TABLE public.observation ADD CONSTRAINT "observation_datacapture_flag_check" CHECK (dataCaptureFlag = ANY(ARRAY['T'::bpchar, 'F'::bpchar]));


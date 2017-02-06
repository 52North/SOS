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

-- Script to remove old unique constraints with generated names.
-- Execute this script after running the installtion process with selected "Update schema" to update the old database model.
-- It is not required if you create the tables during the installation process.

ALTER TABLE public.codespace DROP CONSTRAINT IF EXISTS codespace_codespace_key;
ALTER TABLE public.codespace DROP CONSTRAINT IF EXISTS uk_gix2s5yqji3xx3sq2w208fev4;
ALTER TABLE public.featureofinterest DROP CONSTRAINT IF EXISTS featureofinterest_identifier_key;
ALTER TABLE public.featureofinterest DROP CONSTRAINT IF EXISTS uk_eluouf83mg2fwgysklm9wesg7;
ALTER TABLE public.featureofinterest DROP CONSTRAINT IF EXISTS featureofinterest_url_key;
ALTER TABLE public.featureofinterest DROP CONSTRAINT IF EXISTS uk_4m8qhs19lgomrf90kv12wqydo;
ALTER TABLE public.featureofinteresttype DROP CONSTRAINT IF EXISTS featureofinteresttype_featureofinteresttype_key;
ALTER TABLE public.featureofinteresttype DROP CONSTRAINT IF EXISTS uk_1kbcqyg1snanybgjkgi3af77c;
ALTER TABLE public.observableproperty DROP CONSTRAINT IF EXISTS observableproperty_identifier_key;
ALTER TABLE public.observableproperty DROP CONSTRAINT IF EXISTS uk_sm8q1lm6dedcpshhxgog4v5yc;
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS identifier;
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS uk_mi06n33vm64vg2yk2ix0xjuny;
-- series concept
ALTER TABLE vobservation DROP CONSTRAINT IF EXISTS observation_seriesid_phenomenontimestart_phenomenontimeend__key;
-- old concept
ALTER TABLE public.observation DROP CONSTRAINT IF EXISTS observation_featureofinterestid_observablepropertyid_proced_key;
ALTER TABLE observationtype DROP CONSTRAINT IF EXISTS observationtype_observationtype_key;
ALTER TABLE public.observationtype DROP CONSTRAINT IF EXISTS uk_phym2p6x132lj4fw0jg9hhmri;
ALTER TABLE voffering DROP CONSTRAINT IF EXISTS offering_identifier_key;
ALTER TABLE public.offering DROP CONSTRAINT IF EXISTS uk_k5dd7ybqa4d61mgkoae4t4mq4;
ALTER TABLE public."procedure" DROP CONSTRAINT IF EXISTS procedure_identifier_key;
ALTER TABLE public."procedure" DROP CONSTRAINT IF EXISTS uk_lmepjgjt540e1nkoma1vhv1cq;
ALTER TABLE public.relatedfeaturerole DROP CONSTRAINT IF EXISTS relatedfeaturerole_relatedfeaturerole_key;
ALTER TABLE public.relatedfeaturerole DROP CONSTRAINT IF EXISTS uk_tmebuotikqlxnnbcbshy7t6od;
ALTER TABLE public.unit DROP CONSTRAINT IF EXISTS unit_unit_key;
ALTER TABLE public.unit DROP CONSTRAINT IF EXISTS uk_bg019o25br570qfwc0nx9bhx3;

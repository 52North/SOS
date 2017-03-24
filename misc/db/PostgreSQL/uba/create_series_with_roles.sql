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

﻿CREATE MATERIALIZED VIEW public.series_with_mergerole AS 
 SELECT s1.seriesid,
        CASE
            WHEN NOT (s1.seriesid IN ( SELECT s0.seriesid
               FROM mergable_series s0)) OR (s1.seriesid IN ( SELECT min(s0.seriesid) AS min
               FROM mergable_series s0
                 JOIN series s1_1 ON s0.samplingpointid = s1_1.samplingpointid
              GROUP BY s0.samplingpointid)) THEN 'master'::text
            ELSE 'slave'::text
        END AS merge_role,
    s1.featureofinterestid,
    s1.observablepropertyid,
    s1.procedureid,
    s1.procedureid as offeringid,
    s1.deleted,
    s1.published,
    s1.firsttimestamp,
    s1.lasttimestamp,
    s1.firstnumericvalue,
    s1.lastnumericvalue,
    s1.unitid,
    s1.samplingpointid,
    st.stationid
   FROM series s1
     JOIN samplingpoint sp ON sp.samplingpointid = s1.samplingpointid
     JOIN station st ON st.stationid = sp.station
WITH DATA;
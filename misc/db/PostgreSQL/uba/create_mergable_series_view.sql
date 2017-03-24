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

CREATE MATERIALIZED VIEW public.mergable_series AS 
 SELECT s2.seriesid,
    s2.featureofinterestid,
    s2.observablepropertyid,
    s2.procedureid,
    s2.deleted,
    s2.published,
    s2.firsttimestamp,
    s2.lasttimestamp,
    s2.firstnumericvalue,
    s2.lastnumericvalue,
    s2.unitid,
    s2.samplingpointid
   FROM series s2
  WHERE (s2.samplingpointid IN ( SELECT s1.samplingpointid
           FROM ( SELECT count(s0.seriesid) AS mergable_count,
                    s0.samplingpointid
                   FROM series s0
                     JOIN samplingpoint sp ON sp.samplingpointid = s0.samplingpointid
                     JOIN station st ON st.stationid = sp.station
                  GROUP BY s0.samplingpointid) s1
          WHERE s1.mergable_count <> 1))
WITH DATA;
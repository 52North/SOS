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
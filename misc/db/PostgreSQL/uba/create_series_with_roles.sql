CREATE MATERIALIZED VIEW public.series_with_mergerole AS 
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
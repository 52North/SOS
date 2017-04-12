

-- update/creation script for UBA views within 52°North SOS v4.3.11-UBA-1.0.3


DROP MATERIALIZED VIEW IF EXISTS foi_station CASCADE;
    
CREATE MATERIALIZED VIEW public.foi_station AS
SELECT DISTINCT st.stationid,
  st.identifier,
  st.codespace,
  st.name,
  st.codespacename,
  st.description,
  foi.geom
FROM series s,
  featureofinterest foi,
  samplingpoint sp,
  station st
WHERE s.featureofinterestid = foi.featureofinterestid
AND s.samplingpointid = sp.samplingpointid
AND sp.station = st.stationid
WITH DATA;

    
DROP MATERIALIZED VIEW IF EXISTS mergable_series CASCADE;
    
CREATE MATERIALIZED VIEW public.mergable_series AS
select counts.samplingpointid from (
  select samplingpointid,count(seriesid) num from series group by samplingpointid
) counts
where counts.num <> 1
WITH DATA;

    
DROP MATERIALIZED VIEW IF EXISTS series_with_mergerole CASCADE;
    
CREATE MATERIALIZED VIEW public.series_with_mergerole AS
select 
case
  when s.seriesid in (
    select min(seriesid) from series group by samplingpointid
  ) THEN 'master'::text
  ELSE 'slave'::text
  END as merge_role,
s.*,
sp.station stationid
from series s, samplingpoint sp
where s.samplingpointid = sp.samplingpointid
WITH DATA;

    
DROP MATERIALIZED VIEW IF EXISTS station_with_mergerole CASCADE;
    
CREATE MATERIALIZED VIEW public.station_with_mergerole AS
SELECT DISTINCT st1.stationid,
  m.masterref,
  CASE
    WHEN st1.stationid = m.masterref THEN 'master'::text
    ELSE 'slave'::text
  END AS merge_role,
  st1.identifier,
  st1.codespace,
  st1.name,
  st1.codespacename,
  st1.description,
  st1.geom
FROM foi_station st1
JOIN (
  SELECT max(st2.stationid) AS masterref, st2.geom
  FROM foi_station st2
  GROUP BY st2.geom
) m ON st1.geom = m.geom
WITH DATA;

    
ALTER TABLE  public.foi_station
    OWNER TO sos_user;
ALTER TABLE  public.mergable_series
    OWNER TO sos_user;
ALTER TABLE  public.series_with_mergerole
    OWNER TO sos_user;
ALTER TABLE  public.station_with_mergerole
    OWNER TO sos_user;

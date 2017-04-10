CREATE MATERIALIZED VIEW public.foi_with_mergerole AS 
 SELECT DISTINCT foi1.featureofinterestid,
        CASE
            WHEN NOT (foi1.featureofinterestid IN ( SELECT foi0.featureofinterestid
               FROM mergable_foi foi0)) OR (foi1.featureofinterestid IN ( SELECT max(foi0.featureofinterestid) AS max
               FROM mergable_foi foi0
                 JOIN featureofinterest foi1_1 ON foi0.geom = foi1_1.geom
              GROUP BY foi0.geom)) THEN 'master'::text
            ELSE 'slave'::text
        END AS merge_role,
    foi1.hibernatediscriminator,
    foi1.featureofinteresttypeid,
    foi1.identifier,
    foi1.codespace,
    foi1.name,
    foi1.codespacename,
    foi1.description,
    foi1.geom,
    foi1.descriptionxml,
    foi1.url
   FROM featureofinterest foi1
WITH DATA;
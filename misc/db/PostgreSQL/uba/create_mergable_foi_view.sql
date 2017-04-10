CREATE MATERIALIZED VIEW public.mergable_foi AS 
 SELECT DISTINCT foi.featureofinterestid,
    foi.hibernatediscriminator,
    foi.featureofinteresttypeid,
    foi.identifier,
    foi.codespace,
    foi.name,
    foi.codespacename,
    foi.description,
    foi.geom,
    foi.descriptionxml,
    foi.url
   FROM featureofinterest foi
  WHERE (foi.geom IN ( SELECT g0.geom
           FROM ( SELECT featureofinterest.geom,
                    count(*) AS num
                   FROM featureofinterest
                  GROUP BY featureofinterest.geom) g0
          WHERE g0.num <> 1))
WITH DATA;
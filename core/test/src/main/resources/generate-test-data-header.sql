--
-- Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

CREATE OR REPLACE FUNCTION get_observation_type(text) RETURNS bigint AS
$$
    SELECT observation_type_id FROM observation_type
    WHERE observation_type = 'http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_'::text || $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_procedure(text) RETURNS bigint AS
$$
    SELECT procedure_id FROM procedure WHERE identifier = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_feature_of_interest_type(text) RETURNS bigint AS
$$
    SELECT feature_of_interest_type_id
    FROM feature_of_interest_type
    WHERE feature_of_interest_type = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_spatial_sampling_feature_type(text) RETURNS bigint AS
$$
    SELECT get_feature_of_interest_type('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_Sampling'::text || $1);
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_feature_of_interest(text) RETURNS bigint AS
$$
    SELECT feature_of_interest_id FROM feature_of_interest WHERE identifier = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_sensor_ml_description_format() RETURNS bigint AS
$$
    SELECT procedure_description_format_id FROM procedure_description_format
    WHERE procedure_description_format = 'http://www.opengis.net/sensorML/1.0.1'::text;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_offering(text) RETURNS bigint AS
$$
    SELECT offering_id FROM offering WHERE identifier = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_observable_property(text) RETURNS bigint AS
$$
    SELECT observable_property_id FROM observable_property WHERE identifier = $1;
$$
LANGUAGE 'sql';

---- INSERTION FUNCTIONS
CREATE OR REPLACE FUNCTION insert_category_value(text) RETURNS bigint AS
$$
    INSERT INTO category_value(value) SELECT $1 WHERE $1 NOT IN (SELECT value FROM category_value);
    SELECT category_value_id FROM category_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_count_value(int) RETURNS bigint AS
$$
    INSERT INTO count_value(value) SELECT $1 WHERE $1 NOT IN (SELECT value FROM count_value);
    SELECT count_value_id FROM count_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_numeric_value(numeric) RETURNS bigint AS
$$
    INSERT INTO numeric_value(value) SELECT $1 WHERE $1 NOT IN (SELECT value FROM numeric_value);
    SELECT numeric_value_id FROM numeric_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_text_value(text) RETURNS bigint AS
$$
    INSERT INTO text_value(value) SELECT $1 WHERE $1 NOT IN (SELECT value FROM text_value);
    SELECT text_value_id FROM text_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_geometry_value(geometry) RETURNS bigint AS
$$
    INSERT INTO geometry_value(value) SELECT $1 WHERE $1 NOT IN (SELECT value FROM geometry_value);
    SELECT geometry_value_id FROM geometry_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_observation_type(text) RETURNS bigint AS
$$
    INSERT INTO observation_type(observation_type) SELECT $1 WHERE $1 NOT IN (SELECT observation_type FROM observation_type);
    SELECT observation_type_id FROM observation_type WHERE observation_type = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_feature_of_interest_type(text) RETURNS bigint AS
$$
    INSERT INTO feature_of_interest_type(feature_of_interest_type) SELECT $1 WHERE $1 NOT IN (SELECT feature_of_interest_type FROM feature_of_interest_type);
    SELECT feature_of_interest_type_id FROM feature_of_interest_type WHERE feature_of_interest_type = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_offering(text) RETURNS bigint AS
$$
    INSERT INTO offering(identifier, name) SELECT $1, $1 WHERE $1 NOT IN (SELECT identifier FROM offering);
    SELECT offering_id FROM offering WHERE identifier = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_unit(text) RETURNS bigint AS
$$
    INSERT INTO unit(unit) SELECT $1 WHERE $1 NOT IN (SELECT unit FROM unit);
    SELECT unit_id FROM unit WHERE unit = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_procedure_description_format(text) RETURNS bigint AS
$$
    INSERT INTO procedure_description_format(procedure_description_format) SELECT $1 WHERE $1 NOT IN (SELECT procedure_description_format FROM procedure_description_format);
    SELECT procedure_description_format_id FROM procedure_description_format WHERE procedure_description_format = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_feature_of_interest(text, numeric, numeric) RETURNS bigint AS
$$
    INSERT INTO feature_of_interest(feature_of_interest_type_id, identifier, name, geom, description_xml)
    SELECT get_spatial_sampling_feature_type('Point'), $1, $1, ST_GeomFromText('POINT(' || $2 || $3 || ')', 4326),
'<sams:SF_SpatialSamplingFeature
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:sams="http://www.opengis.net/samplingSpatial/2.0"
    xmlns:sf="http://www.opengis.net/sampling/2.0"
    xmlns:gml="http://www.opengis.net/gml/3.2" gml:id="ssf_'::text || $1 || '">
    <gml:identifier codeSpace="">'::text || $1 || '</gml:identifier>
    <sf:type xlink:href="http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint"/>
    <sf:sampledFeature xlink:href="http://www.opengis.net/def/nil/OGC/0/unknown"/>
    <sams:shape>
        <gml:Point gml:id="p_ssf_'::text || $1 || '">
            <gml:pos srsName="http://www.opengis.net/def/crs/EPSG/0/4326">'::text|| $3 || ' '::text || $2 || '</gml:pos>
        </gml:Point>
    </sams:shape>
</sams:SF_SpatialSamplingFeature>'::text
    WHERE $1 NOT IN (SELECT identifier FROM feature_of_interest);
    SELECT feature_of_interest_id FROM feature_of_interest WHERE identifier = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_observable_property(text) RETURNS bigint AS
$$
    INSERT INTO observable_property(identifier, description) SELECT $1, $1
    WHERE $1 NOT IN (SELECT identifier FROM observable_property WHERE identifier = $1);
    SELECT observable_property_id FROM observable_property WHERE identifier = $1
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION create_sensor_description(text, text, numeric, numeric, numeric) RETURNS text AS
$$
    SELECT
'<sml:SensorML version="1.0.1"
  xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
  xmlns:gml="http://www.opengis.net/gml"
  xmlns:swe="http://www.opengis.net/swe/1.0.1"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <sml:member>
    <sml:System >
      <sml:identification>
        <sml:IdentifierList>
          <sml:identifier name="uniqueID">
            <sml:Term definition="urn:ogc:def:identifier:OGC:uniqueID">
              <sml:value>'::text || $1 || '</sml:value>
            </sml:Term>
          </sml:identifier>
        </sml:IdentifierList>
      </sml:identification>
      <sml:position name="sensorPosition">
        <swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">
          <swe:location>
            <swe:Vector gml:id="STATION_LOCATION">
              <swe:coordinate name="easting">
                <swe:Quantity axisID="x">
                  <swe:uom code="degree"/>
                  <swe:value>'::text || $3 || '</swe:value>
                </swe:Quantity>
              </swe:coordinate>
              <swe:coordinate name="northing">
                <swe:Quantity axisID="y">
                  <swe:uom code="degree"/>
                  <swe:value>'::text || $4 || '</swe:value>
                </swe:Quantity>
              </swe:coordinate>
              <swe:coordinate name="altitude">
                <swe:Quantity axisID="z">
                  <swe:uom code="m"/>
                  <swe:value>'::text || $5 || '</swe:value>
                </swe:Quantity>
              </swe:coordinate>
            </swe:Vector>
          </swe:location>
        </swe:Position>
      </sml:position>
      <sml:inputs>
        <sml:InputList>
          <sml:input name="">
            <swe:ObservableProperty definition="'::text || $2 || '"/>
          </sml:input>
        </sml:InputList>
      </sml:inputs>
      <sml:outputs>
        <sml:OutputList>
          <sml:output name="">
            <swe:Quantity  definition="'::text || $2 || '">
              <swe:uom code="NOT_DEFINED"/>
            </swe:Quantity>
          </sml:output>
        </sml:OutputList>
      </sml:outputs>
    </sml:System>
  </sml:member>
</sml:SensorML>'::text;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_procedure(text,timestamp,text,numeric,numeric,numeric,bigint,bigint) RETURNS bigint AS
$$
    INSERT INTO procedure(identifier, procedure_description_format_id, deleted) SELECT
        $1, get_sensor_ml_description_format(), false WHERE $1 NOT IN (
            SELECT identifier FROM procedure WHERE identifier = $1);
    INSERT INTO valid_procedure_time(procedure_id, start_time, description_xml)
        SELECT get_procedure($1), $2, create_sensor_description($1, $3, $4, $5, $6)
        WHERE get_procedure($1) NOT IN (
            SELECT procedure_id FROM valid_procedure_time WHERE procedure_id = get_procedure($1));
    INSERT INTO procedure_has_observation_type(procedure_id, observation_type_id)
        SELECT get_procedure($1), $7
        WHERE $7 NOT IN (SELECT observation_type_id FROM procedure_has_observation_type
                 WHERE procedure_id = get_procedure($1) AND observation_type_id = $7);
    INSERT INTO procedure_has_feature_of_interest_type(procedure_id, feature_of_interest_type_id)
        SELECT get_procedure($1), $8
        WHERE $8 NOT IN (SELECT feature_of_interest_type_id FROM procedure_has_feature_of_interest_type
                 WHERE procedure_id = get_procedure($1) AND feature_of_interest_type_id = $8);
    SELECT get_procedure($1);
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_procedure(text,timestamp,text,numeric,numeric,numeric,text,text) RETURNS bigint AS
$$
    SELECT insert_procedure($1, $2, $3, $4, $5, $6, get_observation_type($7), get_spatial_sampling_feature_type($8));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_observation_constellation(bigint,bigint,bigint,bigint) RETURNS bigint AS
$$
    INSERT INTO offering_has_allowed_observation_type(offering_id, observation_type_id)
    SELECT $3, $1 WHERE $3 NOT IN (
        SELECT offering_id FROM offering_has_allowed_observation_type WHERE offering_id = $3);
    INSERT INTO observation_constellation(observation_type_id, procedure_id, offering_id, observable_property_id)
    SELECT $1,$2,$3,$4 WHERE $1 NOT IN (SELECT observation_type_id
        FROM observation_constellation  WHERE observation_type_id = $1
          AND procedure_id = $2 AND offering_id = $3 AND observable_property_id = $4);
    SELECT observation_constellation_id FROM observation_constellation
        WHERE observation_type_id = $1  AND procedure_id = $2 AND offering_id = $3 AND observable_property_id = $4;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_observation_constellation(text,text,text,text) RETURNS bigint AS
$$
    SELECT insert_observation_constellation(get_observation_type($1),
        get_procedure($2), get_offering($3), get_observable_property($4));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_observation_constellation(bigint,bigint,bigint,bigint) RETURNS bigint AS
$$
    SELECT observation_constellation_id FROM observation_constellation
        WHERE observation_type_id = $1  AND procedure_id = $2 AND offering_id = $3 AND observable_property_id = $4;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_observation_constellation(text,text,text,text) RETURNS bigint AS
$$
    SELECT get_observation_constellation(get_observation_type($1),
        get_procedure($2), get_offering($3), get_observable_property($4));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_unit(text) RETURNS bigint AS
$$
    SELECT unit_id FROM unit WHERE unit = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION get_boolean_value(boolean) RETURNS bigint AS
$$
    SELECT boolean_value_id FROM boolean_value WHERE value = $1;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_numeric_observation(bigint, numeric) RETURNS VOID AS
$$
    INSERT INTO observation_has_numeric_value(observation_id, numeric_value_id)
        SELECT $1, insert_numeric_value($2) WHERE $1 NOT IN (
            SELECT observation_id FROM observation_has_numeric_value
            WHERE observation_id = $1 AND numeric_value_id = insert_numeric_value($2));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_observation(bigint, text, text, timestamp) RETURNS bigint AS
$$
    INSERT INTO observation(observation_constellation_id, feature_of_interest_id, unit_id, phenomenon_time_start, phenomenon_time_end, result_time)
    SELECT $1, get_feature_of_interest($2), get_unit($3), $4, $4, $4 WHERE $1 NOT IN (
        SELECT observation_constellation_id FROM observation
        WHERE observation_constellation_id = $1 AND feature_of_interest_id = get_feature_of_interest($2)
                AND unit_id = get_unit($3) AND phenomenon_time_start = $4 AND phenomenon_time_end = $4 AND result_time = $4);

    SELECT observation_id FROM observation
    WHERE feature_of_interest_id = get_feature_of_interest($2)
        AND observation_constellation_id = $1 AND unit_id = get_unit($3)
        AND phenomenon_time_start = $4;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_boolean_observation(bigint, boolean) RETURNS VOID AS
$$
    INSERT INTO observation_has_boolean_value(observation_id, boolean_value_id) SELECT $1, get_boolean_value($2)
    WHERE $1 NOT IN (SELECT observation_id FROM observation_has_boolean_value
            WHERE observation_id = $1 AND boolean_value_id = get_boolean_value($2));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_count_observation(bigint, int) RETURNS VOID AS
$$
    INSERT INTO observation_has_count_value(observation_id, count_value_id) SELECT $1, insert_count_value($2)
    WHERE $1 NOT IN (SELECT observation_id FROM observation_has_count_value
            WHERE observation_id = $1 AND count_value_id = insert_count_value($2));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_text_observation(bigint, text) RETURNS VOID AS
$$
    INSERT INTO observation_has_text_value(observation_id, text_value_id) SELECT $1, insert_text_value($2)
    WHERE $1 NOT IN (SELECT observation_id FROM observation_has_text_value
            WHERE observation_id = $1 AND text_value_id = insert_text_value($2));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_category_observation(bigint, text) RETURNS VOID AS
$$
    INSERT INTO observation_has_category_value(observation_id, category_value_id) SELECT $1, insert_category_value($2)
    WHERE $1 NOT IN (SELECT observation_id FROM observation_has_category_value
        WHERE observation_id = $1 AND category_value_id = insert_category_value($2));
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_result_template(bigint,bigint,text,text,text) RETURNS bigint AS
$$
    INSERT INTO result_template(observation_constellation_id, feature_of_interest_id, identifier, result_structure, result_encoding)
    SELECT  $1, $2, $3, $4, $5 WHERE $3 NOT IN (
        SELECT identifier FROM result_template
        WHERE observation_constellation_id = $1
            AND feature_of_interest_id = $2
            AND identifier = $3
            AND result_structure = $4
            AND result_encoding = $5);
    SELECT result_template_id FROM result_template WHERE identifier = $3;
$$
LANGUAGE 'sql';

CREATE OR REPLACE FUNCTION insert_result_template(text,text,text,text,text,text) RETURNS bigint AS
$$
    SELECT insert_result_template(get_observation_constellation($1, $2, $3, $4), get_feature_of_interest($5),
        $2 || '/template/1'::text,
        '<swe:DataRecord xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:xlink="http://www.w3.org/1999/xlink">
            <swe:field name="phenomenonTime">
                <swe:Time definition="http://www.opengis.net/def/property/OGC/0/PhenomenonTime">
                    <swe:uom xlink:href="http://www.opengis.net/def/uom/ISO-8601/0/Gregorian"/>
                </swe:Time>
            </swe:field>
            <swe:field name="'::text || $4 || '">
                <swe:Quantity definition="'::text || $4 || '">
                    <swe:uom code="'::text || $6 || '"/>
                </swe:Quantity>
            </swe:field>
        </swe:DataRecord>'::text,
        '<swe:TextEncoding xmlns:swe="http://www.opengis.net/swe/2.0" tokenSeparator="#" blockSeparator="@"/>'::text);
$$
LANGUAGE 'sql';

--
-- NOTE: in table observation: the column identifier can be null but is in the unique constraint....
--

---- OBSERVATION_TYPE
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation');
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement');
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation');
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation');
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation');
SELECT insert_observation_type('http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation');

---- FEATURE_OF_INTEREST_TYPE
SELECT insert_feature_of_interest_type('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve');
SELECT insert_feature_of_interest_type('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSurface');
SELECT insert_feature_of_interest_type('http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint');
SELECT insert_feature_of_interest_type('http://www.opengis.net/def/nil/OGC/0/unknown');

---- PROCEDURE_DESCRIPTION_FORMAT
SELECT insert_procedure_description_format('http://www.opengis.net/sensorML/1.0.1');

---- INSERT VALUES
INSERT INTO boolean_value(value) SELECT true  WHERE true  NOT IN (SELECT value FROM boolean_value);
INSERT INTO boolean_value(value) SELECT false WHERE false NOT IN (SELECT value FROM boolean_value);

---- UNIT
SELECT insert_unit('test_unit_1');

---- OBSERVABLE_PROPERTY
SELECT insert_observable_property('test_observable_property_1');

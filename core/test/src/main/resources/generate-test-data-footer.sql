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

DROP FUNCTION create_sensor_description(text, text, numeric, numeric, numeric);
DROP FUNCTION get_boolean_value(boolean);
DROP FUNCTION get_feature_of_interest(text);
DROP FUNCTION get_feature_of_interest_type(text);
DROP FUNCTION get_observable_property(text);
DROP FUNCTION get_observation_constellation(bigint,bigint,bigint,bigint);
DROP FUNCTION get_observation_constellation(text,text,text,text);
DROP FUNCTION get_observation_type(text);
DROP FUNCTION get_offering(text);
DROP FUNCTION get_procedure(text);
DROP FUNCTION get_sensor_ml_description_format();
DROP FUNCTION get_spatial_sampling_feature_type(text);
DROP FUNCTION get_unit(text);
DROP FUNCTION insert_boolean_observation(bigint, boolean);
DROP FUNCTION insert_category_observation(bigint, text);
DROP FUNCTION insert_category_value(text);
DROP FUNCTION insert_count_observation(bigint, int);
DROP FUNCTION insert_count_value(int);
DROP FUNCTION insert_feature_of_interest(text, numeric, numeric);
DROP FUNCTION insert_feature_of_interest_type(text);
DROP FUNCTION insert_geometry_value(geometry);
DROP FUNCTION insert_numeric_observation(bigint, numeric);
DROP FUNCTION insert_numeric_value(numeric);
DROP FUNCTION insert_observable_property(text);
DROP FUNCTION insert_observation(bigint,text, text,timestamp);
DROP FUNCTION insert_observation_constellation(bigint,bigint,bigint,bigint);
DROP FUNCTION insert_observation_constellation(text,text,text,text);
DROP FUNCTION insert_observation_type(text);
DROP FUNCTION insert_offering(text);
DROP FUNCTION insert_procedure_description_format(text);
DROP FUNCTION insert_procedure(text,timestamp,text,numeric,numeric,numeric,bigint,bigint);
DROP FUNCTION insert_procedure(text,timestamp,text,numeric,numeric,numeric,text,text);
DROP FUNCTION insert_text_observation(bigint, text);
DROP FUNCTION insert_text_value(text);
DROP FUNCTION insert_unit(text);
DROP FUNCTION insert_result_template(text,text,text,text,text,text);
DROP FUNCTION insert_result_template(bigint,bigint,text,text,text);
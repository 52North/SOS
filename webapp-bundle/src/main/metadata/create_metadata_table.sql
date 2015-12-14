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

﻿CREATE TABLE series_metadata 
(
	metadata_id bigint NOT NULL,
	series_id bigint NOT NULL,
	field_name character varying(255) NOT NULL,
	field_type character varying(10) DEFAULT 'string',
	field_value text,
	last_update timestamp,
	CONSTRAINT seriespk PRIMARY KEY (metadata_id),
	CONSTRAINT seriesfk FOREIGN KEY (series_id)
		REFERENCES series (seriesid) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT metadataidentity UNIQUE (series_id, field_name),
	CONSTRAINT chk_type CHECK (field_type IN ('string','boolean','integer','double','text','json'))
)
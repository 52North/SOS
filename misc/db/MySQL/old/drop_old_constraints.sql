--
-- Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

-- Script to remove old unique constraints with generated names.
-- Execute this script after running the installtion process with selected "Update schema" to update the old database model.
-- It is not required if you create the tables during the installation process.

ALTER TABLE sos.codespace DROP INDEX codespace;
ALTER TABLE sos.featureofinterest DROP INDEX identifier;
ALTER TABLE sos.featureofinterest DROP INDEX url;
ALTER TABLE sos.featureofinteresttype DROP INDEX featureofinteresttype;
ALTER TABLE sos.observableproperty DROP INDEX identifier;
ALTER TABLE sos.observation DROP INDEX identifier;
-- old concept
ALTER TABLE sos.observation DROP INDEX featureofinterestid;
ALTER TABLE sos.observationtype DROP INDEX observationtype;
ALTER TABLE sos.offering DROP INDEX identifier;
ALTER TABLE sos.`procedure` DROP INDEX identifier;
ALTER TABLE sos.relatedfeaturerole DROP INDEX relatedfeaturerole;
ALTER TABLE sos.unit DROP INDEX unit;

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

-- ------------------------------------------------------------------------------
-- SELECT data FROM Wonderware HISTORY-table
-- http://www.wonderwarene.com/support/Docs/TechTip_1004_WonderwareHistorian%26DifferentRetrievalMethods.pdf
-- Test:
-- TAG_NAME_VALUE	 = 'MTAAR1QA01.VP'
-- START_DATA_VALUE  = '20140101 10:35:20'
-- END_DATA_VALUE	 = '20140101 10:35:30'
-- RETRIEVAL_VALUE	 = 'Cyclic'
-- CYCLE_COUNT_VALUE = 100
-- 
-- ------------------------------------------------------------------------------

SELECT 
	TableType = 'history', 
	temp.TagName, 
	Description, 
	DateTime = convert(nvarchar, DateTime, 21), 
	Value, 
--	MinEU = ISNULL(cast(AnalogTag.MinEU as VarChar(20)),'N/A'), 
--	MaxEU = ISNULL(cast(AnalogTag.MaxEU as VarChar(20)),'N/A'),
	Unit  = ISNULL(cast(EngineeringUnit.Unit as VarChar(20)),'N/A')
FROM (
	  SELECT * FROM History 
	  WHERE 
		History.TagName IN (TAG_NAME_VALUE) 
		AND wwRetrievalMode = RETRIEVAL_VALUE
		AND wwCycleCount = CYCLE_COUNT_VALUE
		AND DateTime >= START_DATA_VALUE 
		AND DateTime <= END_DATA_VALUE 
	 ) temp 

LEFT JOIN Tag ON Tag.TagName = temp.TagName 
LEFT JOIN AnalogTag ON AnalogTag.TagName = temp.TagName 
LEFT JOIN EngineeringUnit ON AnalogTag.EUKey = EngineeringUnit.EUKey 
WHERE 
	temp.StartDateTime >= START_DATA_VALUE
	AND
	VALUE IS NOT NULL;

-- ------------------------------------------------------------------------------

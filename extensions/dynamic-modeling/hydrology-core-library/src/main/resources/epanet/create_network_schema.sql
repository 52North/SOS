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
-- Table structure for node
-- ------------------------------------------------------------------------------

CREATE TABLE epanet_node
(
  object_id varchar(16) PRIMARY KEY NOT NULL,   
  enet_type varchar(10), 
  elevation REAL, 
  basedemand REAL,
  initquality REAL,  
  x REAL, 
  y REAL
);

-- CREATE INDEX epanet_node_x_index ON epanet_node (x);
-- CREATE INDEX epanet_node_y_index ON epanet_node (y);

-- ------------------------------------------------------------------------------
-- Table structure for arc
-- ------------------------------------------------------------------------------

CREATE TABLE epanet_arc
(
  object_id varchar(16) PRIMARY KEY NOT NULL, 
  enet_type varchar(10), 
  node_id_1 varchar(16), 
  node_id_2 varchar(16),   
  diameter REAL, 
  roughness REAL,
  status INT,
  x1 REAL, 
  y1 REAL, 
  x2 REAL, 
  y2 REAL
);

-- CREATE INDEX epanet_arc_x1_index ON epanet_arc (x1);
-- CREATE INDEX epanet_arc_y1_index ON epanet_arc (y1);
-- CREATE INDEX epanet_arc_x2_index ON epanet_arc (x2);
-- CREATE INDEX epanet_arc_y2_index ON epanet_arc (y2);

-- ------------------------------------------------------------------------------
-- Table structure for report node
-- ------------------------------------------------------------------------------

CREATE TABLE epanet_report_node
(
  object_id varchar(16) NOT NULL, 
  step_time varchar(8), 
  demand REAL, 
  head REAL, 
  pressure REAL, 
  quality REAL 
);

CREATE INDEX epanet_report_node_object_id_index ON epanet_report_node (object_id);

-- ------------------------------------------------------------------------------
-- Table structure for report arc
-- ------------------------------------------------------------------------------

CREATE TABLE epanet_report_arc 
(
  object_id varchar(16) NOT NULL, 
  step_time varchar(8), 
  flow REAL, 
  velocity REAL, 
  unitheadloss REAL, 
  frictionfactor REAL, 
  quality REAL, 
  status INT
);

CREATE INDEX epanet_report_arc_object_id_index ON epanet_report_arc (object_id);

-- ------------------------------------------------------------------------------
-- Table structure for units
-- ------------------------------------------------------------------------------

CREATE TABLE epanet_units
(
  object_id varchar(16) PRIMARY KEY NOT NULL,   
  unit_name varchar(10),
  precision INT
);

-- ------------------------------------------------------------------------------

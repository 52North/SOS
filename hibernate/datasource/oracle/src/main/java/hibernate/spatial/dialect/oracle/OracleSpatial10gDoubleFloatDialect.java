/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package hibernate.spatial.dialect.oracle;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.oracle.OracleSpatial10gDialect;
import org.n52.sos.ds.datasource.SpatialIndexDialect;

public class OracleSpatial10gDoubleFloatDialect extends OracleSpatial10gDialect implements SpatialIndexDialect {

    private static final long serialVersionUID = -1294060043623083068L;

    public OracleSpatial10gDoubleFloatDialect() {
        super();
        registerColumnType(Types.DOUBLE, "float");
    }
    

    public String buildSqlCreateSpatialIndexString(Index index, String defaultCatalog, String defaultSchema) {

        // https://docs.oracle.com/cd/A97630_01/appdev.920/a96630/sdo_objindex.htm#i78196
        // CREATE INDEX cola_spatial_idx ON cola_markets(shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX;
        
//        String name = index.getName();
//        Table table = index.getTable();
//        Iterator<Column> columns = index.getColumnIterator();
//        java.util.Map<Column, String> columnOrderMap = new HashMap<Column, String>();
//        
//        
//        StringBuilder buf = new StringBuilder( "create" )
//                        .append( " index " )
//                        .append( this.qualifyIndexName() ?
//                                        name :
//                                        StringHelper.unqualify( name ) )
//                        .append( " on " )
//                        .append( table.getQualifiedName( this, defaultCatalog, defaultSchema ) )
//                        .append( " (" );
//        while (columns.hasNext()) {
//            Column column = columns.next();
//            buf.append(column.getQuotedName(this));
//            if (columnOrderMap.containsKey(column)) {
//                buf.append(" ").append(columnOrderMap.get(column));
//            }
//            if (columns.hasNext())
//                buf.append(", ");
//        }
//        buf.append(")  INDEXTYPE IS MDSYS.SPATIAL_INDEX");
//        return buf.toString();
    	return "";
    }
}

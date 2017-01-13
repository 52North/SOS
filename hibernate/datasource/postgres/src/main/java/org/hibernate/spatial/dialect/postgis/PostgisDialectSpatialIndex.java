/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.hibernate.spatial.dialect.postgis;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.n52.sos.ds.datasource.SpatialIndexDialect;

public class PostgisDialectSpatialIndex extends PostgisDialect implements SpatialIndexDialect {

    private static final long serialVersionUID = 1L;
    
    public PostgisDialectSpatialIndex() {
        super();
    }

    public String buildSqlCreateSpatialIndexString(Index index, String defaultCatalog, String defaultSchema) {
        // http://postgis.net/docs/manual-2.0/using_postgis_dbmanagement.html#idp60795872
        // CREATE INDEX [indexname] ON [tablename] USING GIST ( [geometryfield] ); 
        
        String name = index.getName();
        Table table = index.getTable();
        Iterator<Column> columns = index.getColumnIterator();
        java.util.Map<Column, String> columnOrderMap = new HashMap<Column, String>();
        
        
        StringBuilder buf = new StringBuilder( "create" )
                        .append( " index " )
                        .append( this.qualifyIndexName() ?
                                        name :
                                        StringHelper.unqualify( name ) )
                        .append( " on " )
                        .append( table.getQualifiedName( this, defaultCatalog, defaultSchema ) )
                        .append(" USING GIST")
                        .append( " (" );
        while (columns.hasNext()) {
            Column column = columns.next();
            buf.append(column.getQuotedName(this));
            if (columnOrderMap.containsKey(column)) {
                buf.append(" ").append(columnOrderMap.get(column));
            }
            if (columns.hasNext())
                buf.append(", ");
        }
        buf.append(")");
        return buf.toString();
    }

}

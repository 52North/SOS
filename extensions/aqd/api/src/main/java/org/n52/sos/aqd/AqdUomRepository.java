/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.aqd;

public class AqdUomRepository {
    
    public static Uom getAqdUom(String v) {
        try {
            return UomConcentration.from(v);
        } catch (Exception e) {
            
        }
        
        return null;
    }
    
   public interface Uom {
        
        String baseURI = "http://dd.eionet.europa.eu/vocabulary/uom/";
        
        public String getId();
        
        public String getNotation();
        
        public String getConceptURI();
        
    }
    
    public enum UomConcentration implements Uom {
        MilligramsCubicMetre("mg.m-3", "mg/m3"),
        NanogramsSquareMetreDay("ng.m-2.day-1", "ng/m2/day"),
        NanogramsCubicMetre("ng.m-3", "ng/m3"),
        PircogramsCubicMetre("pg.m-3", "pg/m3"),
        MicrogramsSquareMetreDay("ug.m-2.day-1", "ug/m2/day"),
        MicrogramsCubicMetre("ug.m-3", "ug/m3"),
        MicrogramsCubicMetreDay("ug.m-3.day", "ug/m3·day"),
        MicrogramsCubicMetreHour("ug.m-3.h", "ug/m3·h");
        
        private final String concentrationBaseURI = baseURI + "concentration/";
        
        private final String conceptURI;
        
        private final String id;
        
        private final String notation;
        
        UomConcentration(String id, String notation) {
            this.id = id;
            this.notation = notation;
            this.conceptURI = concentrationBaseURI + id;
        }

        public String getId() {
            return id;
        }
        @Override
        public String getNotation() {
            return notation;
        }
        @Override
        public String getConceptURI() {
            return conceptURI;
        }

        public static UomConcentration from(String v) {
            for (UomConcentration c : UomConcentration.values()) {
                if (c.getNotation().equals(v) || c.getId().equals(v) || c.getConceptURI().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

}

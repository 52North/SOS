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
package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.base.Strings;

/**
 * Internal representation of the ISO GMD ExExtent.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class EXExtent extends AbstractObject {
    
    private String description;
//    private List<Object> exGeographicalExtent = new ArrayList<>();
//    private List<Object> exTemporalExtent = new ArrayList<>();
    private List<Referenceable<EXVerticalExtent>> exVerticalExtent = new ArrayList<>();
    
    public String getDescription() {
        return description;
    }
    
    public EXExtent setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public boolean hasDescription() {
        return !Strings.isNullOrEmpty(getDescription());
    }
    
//    public List<Object> getExGeographicalExtent() {
//        return exGeographicalExtent;
//    }
//    
//    public EXExtent setExGeographicalExtent(List<Object> exGeographicalExtent) {
//        this.exGeographicalExtent.clear();
//        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exGeographicalExtent)) {
//            this.exGeographicalExtent.addAll(exGeographicalExtent);
//        }
//        return this;
//    }
//    
//    public EXExtent addExGeographicalExtent(List<Object> exGeographicalExtent) {
//        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exGeographicalExtent)) {
//            this.exGeographicalExtent.addAll(exGeographicalExtent);
//        }
//        return this;
//    }
//    
//    public EXExtent addExGeographicalExtent(Object exGeographicalExtent) {
//        if (exGeographicalExtent != null) {
//            this.exGeographicalExtent.add(exGeographicalExtent);
//        }
//        return this;
//    }
//    
//    public boolean hasGeographicalExtent() {
//        return getExGeographicalExtent() != null && !getExGeographicalExtent().isEmpty();
//    }
//    
//    public List<Object> getExTemporalExtent() {
//        return exTemporalExtent;
//    }
//    
//    public EXExtent setExTemporalExtent(List<Object> exTemporalExtent) {
//        this.exTemporalExtent.clear();
//        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exTemporalExtent)) {
//            this.exTemporalExtent.addAll(exTemporalExtent);
//        }
//        return this;
//    }
//    
//    public EXExtent addExTemporalExtent(List<Object> exTemporalExtent) {
//        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exTemporalExtent)) {
//            this.exTemporalExtent.addAll(exTemporalExtent);
//        }
//        return this;
//    }
//    
//    public EXExtent addExTemporalExtent(Object exTemporalExtent) {
//        if (exTemporalExtent != null) {
//            this.exTemporalExtent.add(exTemporalExtent);
//        }
//        return this;
//    }
    
    public List<Referenceable<EXVerticalExtent>> getExVerticalExtent() {
        return exVerticalExtent;
    }
    
    public EXExtent setVerticalExtent(List<Referenceable<EXVerticalExtent>> exVerticalExtent) {
        this.exVerticalExtent.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exVerticalExtent)) {
            this.exVerticalExtent.addAll(exVerticalExtent);
        }
        return this;
    }
    
    public EXExtent addVerticalExtent(List<Referenceable<EXVerticalExtent>> exVerticalExtent) {
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(exVerticalExtent)) {
            this.exVerticalExtent.addAll(exVerticalExtent);
        }
        return this;
    }
    
    public EXExtent addVerticalExtent(Referenceable<EXVerticalExtent> exVerticalExtent) {
        if (exVerticalExtent != null) {
            this.exVerticalExtent.add(exVerticalExtent);
        }
        return this;
    }
    
    public EXExtent addVerticalExtent(EXVerticalExtent exVerticalExtent) {
        if (exVerticalExtent != null) {
            this.exVerticalExtent.add(Referenceable.of(exVerticalExtent));
        }
        return this;
    }
    
    public boolean hasVerticalExtent() {
        return getExVerticalExtent() != null && !getExVerticalExtent().isEmpty();
    }

}

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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.Map;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Maps;

/**
 * Simple class for returning observation constellation info, designed to be fast and to 
 * load all necessary info for all observation constellations in a single query
 * (useful during cache loading) 
 */
public class ObservationConstellationInfo {
    private String offering;
    private String procedure;
    private String observableProperty;
    private String observationType;
    private boolean hiddenChild = false;

    public String getOffering() {
        return offering;
    }
    public void setOffering(String offering) {
        this.offering = offering;
    }

    public String getProcedure() {
        return procedure;
    }
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getObservableProperty() {
        return observableProperty;
    }
    public void setObservableProperty(String observableProperty) {
        this.observableProperty = observableProperty;
    }

    public String getObservationType() {
        return observationType;
    }
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public boolean isHiddenChild() {
        return hiddenChild;
    }
    public void setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
    }
    
    public static Map<String,Collection<ObservationConstellationInfo>> mapByOffering(
            Collection<ObservationConstellationInfo> ocis) {
        Map<String,Collection<ObservationConstellationInfo>> map = Maps.newHashMap();
        for (ObservationConstellationInfo oci : ocis) {
            CollectionHelper.addToCollectionMap(oci.getOffering(), oci, map);
        }
        return map;        
    }
    
    public static Map<String,Collection<ObservationConstellationInfo>> mapByProcedure(
            Collection<ObservationConstellationInfo> ocis) {
        Map<String,Collection<ObservationConstellationInfo>> map = Maps.newHashMap();
        for (ObservationConstellationInfo oci : ocis) {
            CollectionHelper.addToCollectionMap(oci.getProcedure(), oci, map);
        }
        return map;        
    }

    public static Map<String,Collection<ObservationConstellationInfo>> mapByObservableProperty(
            Collection<ObservationConstellationInfo> ocis) {
        Map<String,Collection<ObservationConstellationInfo>> map = Maps.newHashMap();
        for (ObservationConstellationInfo oci : ocis) {
            CollectionHelper.addToCollectionMap(oci.getObservableProperty(), oci, map);
        }
        return map;        
    }
}

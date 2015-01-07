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
package org.n52.sos.ds.hibernate.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.util.CacheHelper;

/**
 * @since 4.0.0
 * 
 */
public class DatasourceCacheUpdateHelper {

    private DatasourceCacheUpdateHelper() {

    }

    public static Set<String> getAllOfferingIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> offerings = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            offerings.add(CacheHelper.addPrefixOrGetOfferingIdentifier(oc.getOffering().getIdentifier()));
        }
        return offerings;
    }

    public static Set<String> getAllOfferingIdentifiersFromObservationConstellationInfos(
            Collection<ObservationConstellationInfo> observationConstellationInfos) {
        Set<String> offerings = new HashSet<String>(observationConstellationInfos.size());
        for (ObservationConstellationInfo oci : observationConstellationInfos) {
            offerings.add(CacheHelper.addPrefixOrGetOfferingIdentifier(oci.getOffering()));
        }
        return offerings;
    }

    public static Set<String> getAllProcedureIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> procedures = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            procedures.add(CacheHelper.addPrefixOrGetProcedureIdentifier(oc.getProcedure().getIdentifier()));
        }
        return procedures;
    }

    public static Set<String> getAllProcedureIdentifiersFromObservationConstellationInfos(
            Collection<ObservationConstellationInfo> observationConstellationInfos) {
        return getAllProcedureIdentifiersFromObservationConstellationInfos(observationConstellationInfos, null);
    }

    public static Set<String> getAllProcedureIdentifiersFromObservationConstellationInfos(
            Collection<ObservationConstellationInfo> observationConstellationInfos, ProcedureFlag procedureFlag) {
        Set<String> procedures = new HashSet<String>(observationConstellationInfos.size());
        for (ObservationConstellationInfo oci : observationConstellationInfos) {
            boolean addProcedure = false;
            if (procedureFlag == null) {
                //add all procedures
                addProcedure = true;
            } else {
                if (procedureFlag.equals(ProcedureFlag.PARENT) && !oci.isHiddenChild()) {
                    addProcedure = true;
                } else if (procedureFlag.equals(ProcedureFlag.HIDDEN_CHILD) && oci.isHiddenChild()) {
                    addProcedure = true;                    
                }
            }
            if (addProcedure) {
                procedures.add(CacheHelper.addPrefixOrGetProcedureIdentifier(oci.getProcedure()));
            }
        }
        return procedures;
    }

    public static Set<String> getAllObservablePropertyIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> observableProperties = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            observableProperties.add(CacheHelper.addPrefixOrGetObservablePropertyIdentifier(oc.getObservableProperty()
                    .getIdentifier()));
        }
        return observableProperties;
    }

    public static Set<String> getAllObservablePropertyIdentifiersFromObservationConstellationInfos(
            Collection<ObservationConstellationInfo> observationConstellationInfos) {
        Set<String> observableProperties = new HashSet<String>(observationConstellationInfos.size());
        for (ObservationConstellationInfo oci : observationConstellationInfos) {
            observableProperties.add(CacheHelper.addPrefixOrGetObservablePropertyIdentifier(oci.getObservableProperty()));
        }
        return observableProperties;
    }    
}

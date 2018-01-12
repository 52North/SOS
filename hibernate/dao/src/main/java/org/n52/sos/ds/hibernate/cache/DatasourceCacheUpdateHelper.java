/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;

/**
 * @since 4.0.0
 *
 */
public class DatasourceCacheUpdateHelper {

    private DatasourceCacheUpdateHelper() {
    }

    public static Set<String> getAllOfferingIdentifiersFrom(Collection<ObservationConstellation> oc) {
        return oc.stream().map(ObservationConstellation::getOffering).map(Offering::getIdentifier).collect(toSet());
    }

    public static Set<String> getAllOfferingIdentifiersFromObservationConstellationInfos(Collection<ObservationConstellationInfo> oci) {
        return oci.stream().map(ObservationConstellationInfo::getOffering).collect(toSet());
    }

    public static Set<String> getAllProcedureIdentifiersFrom(Collection<ObservationConstellation> oc) {
        return oc.stream().map(ObservationConstellation::getProcedure).map(Procedure::getIdentifier).collect(toSet());
    }

    public static Set<String> getAllProcedureIdentifiersFromObservationConstellationInfos(Collection<ObservationConstellationInfo> oci) {
        return getAllProcedureIdentifiersFromObservationConstellationInfos(oci, null);
    }

    public static Set<String> getAllProcedureIdentifiersFromObservationConstellationInfos(Collection<ObservationConstellationInfo> oci, ProcedureFlag flag) {
        Predicate<ObservationConstellationInfo> pred = x -> flag == null || (flag.equals(ProcedureFlag.PARENT) && !x.isHiddenChild()) || (flag.equals(ProcedureFlag.HIDDEN_CHILD) && x.isHiddenChild());
        return oci.stream().filter(pred).map(ObservationConstellationInfo::getProcedure).collect(toSet());
    }

    public static Set<String> getAllObservablePropertyIdentifiersFrom(Collection<ObservationConstellation> oc) {
        return oc.stream().map(ObservationConstellation::getObservableProperty).map(ObservableProperty::getIdentifier).collect(toSet());
    }

    public static Set<String> getAllObservablePropertyIdentifiersFromObservationConstellationInfos(Collection<ObservationConstellationInfo> oci) {
        return oci.stream().map(ObservationConstellationInfo::getObservableProperty).collect(toSet());
    }
}

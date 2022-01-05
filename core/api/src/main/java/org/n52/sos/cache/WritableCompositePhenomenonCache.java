/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.cache;

import java.util.Collection;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface WritableCompositePhenomenonCache extends
        CompositePhenomenonCache {
    void addCompositePhenomenon(String compositePhenomenon);

    void addCompositePhenomenon(Collection<String> compositePhenomenon);

    default void setCompositePhenomenon(Collection<String> compositePhenomenon) {
        clearCompositePhenomenon();
        addCompositePhenomenon(compositePhenomenon);
    }

    void clearCompositePhenomenon();

    void addCompositePhenomenonForProcedure(String procedure, String compositePhenomenon);

    void addCompositePhenomenonForProcedure(String procedure, Collection<String> compositePhenomenon);

    default void setCompositePhenomenonForProcedure(String procedure,
                                                    Collection<String> compositePhenomenon) {
        clearCompositePhenomenonForProcedure(procedure);
        addCompositePhenomenonForProcedure(procedure, compositePhenomenon);
    }

    void clearCompositePhenomenonForProcedure(String procedure);

    void clearCompositePhenomenonForProcedures();

    void addCompositePhenomenonForOffering(String offering, String compositePhenomenon);

    void addCompositePhenomenonForOffering(String offering, Collection<String> compositePhenomenon);

    default void setCompositePhenomenonForOffering(String offering, Collection<String> compositePhenomenon) {
        clearCompositePhenomenonForOffering(offering);
        addCompositePhenomenonForProcedure(offering, compositePhenomenon);
    }

    void clearCompositePhenomenonForOffering(String offering);

    void clearCompositePhenomenonForOfferings();

    void addObservablePropertyForCompositePhenomenon(String compositePhenomenon, String observableProperty);

    void addCompositePhenomenonForObservableProperty(String observableProperty, String compositePhenomenon);

    void addObservablePropertiesForCompositePhenomenon(String compositePhenomenon,
                                                       Collection<String> observableProperty);

    default void setObservablePropertiesForCompositePhenomenon(String compositePhenomenon,
                                                               Collection<String> observableProperty) {
        clearObservablePropertiesForCompositePhenomenon(compositePhenomenon);
        addObservablePropertiesForCompositePhenomenon(compositePhenomenon, observableProperty);
    }

    void clearObservablePropertiesForCompositePhenomenon(String compositePhenomenon);

    void clearObservablePropertiesForCompositePhenomenon();

    void clearCompositePhenomenonsForObservableProperty();

    void clearCompositePhenomenonsForObservableProperty(String observableProperty);

}

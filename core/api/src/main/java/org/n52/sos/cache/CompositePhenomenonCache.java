/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import java.util.Set;

/**
 *
 * @author Christian Autermann
 */
public interface CompositePhenomenonCache {
    /**
     * @return the composite phenomenon
     */
    Set<String> getCompositePhenomenons();

    /**
     * Checks if the supplied observable property exists and if it is a
     * composite phenomenon.
     *
     * @param observableProperty the observable property to check
     *
     * @return whether or not the observable property is a composite phenomenon
     */
    boolean isCompositePhenomenon(String observableProperty);

    /**
     * Get the composite phenomenon associated with the specified procedure.
     *
     * @param procedure the procedure
     *
     * @return the composite phenomenon
     */
    Set<String> getCompositePhenomenonsForProcedure(String procedure);

    /**
     * Checks if the specified observable property exists, is a composite
     * phenomenon and is associated with the specified procedure.
     *
     * @param procedure          the procedure
     * @param observableProperty the observable property
     *
     * @return whether or not the observable property is a composite phenomenon
     *         of the procedure
     */
    boolean isCompositePhenomenonForProcedure(String procedure,
                                              String observableProperty);

    /**
     * Get the composite phenomenon associated with the specified procedure.
     *
     * @param offering the offering
     *
     * @return the composite phenomenon
     */
    Set<String> getCompositePhenomenonsForOffering(String offering);

    /**
     * Checks if the specified observable property exists, is a composite
     * phenomenon and is associated with the specified offering.
     *
     * @param offering           the offering
     * @param observableProperty the observable property
     *
     * @return whether or not the observable property is a composite phenomenon
     *         of the offering
     */
    boolean isCompositePhenomenonForOffering(String offering,
                                             String observableProperty);

    /**
     * Get the observable properties associated with the specified composite
     * phenomenon.
     *
     * @param compositePhenomenon the composite phenomenon
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForCompositePhenomenon(
            String compositePhenomenon);

    /**
     * Checks if the specified observable property is a component of the
     * specified composite phenomenon.
     *
     * @param compositePhenomenon the composite phenomenon
     * @param observableProperty  the observable property
     *
     * @return whether or not the observable property is a component of the
     *         composite phenomenon
     */
    boolean isObservablePropertyOfCompositePhenomenon(String compositePhenomenon,
                                                      String observableProperty);

    /**
     * Gets the composite phenomenon (or parent) for the specified observable
     * property.
     *
     * @param observableProperty the observable property
     *
     * @return the composite phenomenon or {@code null}
     */
    Set<String> getCompositePhenomenonForObservableProperty(String observableProperty);

    /**
     * Checks if the specified observable property is a component of a composite
     * phenomenon.
     *
     * @param observableProperty the observable property
     *
     * @return whether or not the observable property is a component of a
     *         composite phenomenon
     */
    boolean isCompositePhenomenonComponent(String observableProperty);
}

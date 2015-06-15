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
package org.n52.sos.inspire;

import java.util.Collection;
import java.util.Set;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Service internal representation of INSPIRE supported CRS
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireSupportedCRS implements InspireObject {

    /* element DefaultCRS 1..1 */
    private String defaultCRS;

    /* element otherCRS 0..* */
    private Set<String> otherCRS = Sets.newHashSet();

    /**
     * constructor
     * 
     * @param defaultCRS
     *            the mandatory default CRS
     */
    public InspireSupportedCRS(String defaultCRS) {
        setDefaultCRS(defaultCRS);
    }

    /**
     * Get the default CRS
     * 
     * @return the defaultCRS
     */
    public String getDefaultCRS() {
        return defaultCRS;
    }

    /**
     * Set the default CRS
     * 
     * @param defaultCRS
     *            the defaultCRS to set
     */
    private void setDefaultCRS(String defaultCRS) {
        this.defaultCRS = defaultCRS;
    }

    /**
     * Get the other CRSs
     * 
     * @return the other CRS
     */
    public Set<String> getOtherCRS() {
        return otherCRS;
    }

    /**
     * Set the other CRSs, clears the existing collection
     * 
     * @param otherCRS
     *            the otherCRS to set
     * @return this
     */
    public InspireSupportedCRS setOtherCRS(Collection<String> otherCRS) {
        getOtherCRS().clear();
        if (CollectionHelper.isNotEmpty(otherCRS)) {
            getOtherCRS().addAll(otherCRS);
        }
        return this;
    }

    /**
     * Add the other CRS
     * 
     * @param otherCRS
     *            the other CRS to add
     * @return this
     */
    public InspireSupportedCRS addOtherCRS(String otherCRS) {
        getOtherCRS().add(otherCRS);
        return this;
    }

    /**
     * Check if other CRSs are set
     * 
     * @return <code>true</code>, if other CRSs are set
     */
    public boolean isSetSupportedCRSs() {
        return CollectionHelper.isNotEmpty(getOtherCRS());
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n defaultCRS=%s,%n otherCRS=%s%n]", this.getClass().getSimpleName(),
                getDefaultCRS(), CollectionHelper.collectionToString(getOtherCRS()));
    }

}

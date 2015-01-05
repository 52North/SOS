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
package org.n52.sos.ogc.sos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.ows.OwsCapabilities;
import org.n52.sos.util.CollectionHelper;

/**
 * Class which represents the Capabilities.
 * 
 * @since 4.0.0
 * 
 */
public class SosCapabilities extends OwsCapabilities {

    /**
     * Metadata for all supported filter
     */
    private FilterCapabilities filterCapabilities;

    /**
     * All ObservationOfferings provided by this SOS.
     */
    private SortedSet<SosObservationOffering> contents = new TreeSet<SosObservationOffering>();

    /**
     * extensions
     */
    private List<CapabilitiesExtension> extensions = new LinkedList<CapabilitiesExtension>();

    public SosCapabilities(String version) {
        super(SosConstants.SOS, version);
    }

    /**
     * Get filter capabilities
     * 
     * @return filter capabilities
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Set filter capabilities
     * 
     * @param filterCapabilities
     *            filter capabilities
     */
    public void setFilterCapabilities(FilterCapabilities filterCapabilities) {
        this.filterCapabilities = filterCapabilities;
    }

    public boolean isSetFilterCapabilities() {
        return getFilterCapabilities() != null;
    }

    /**
     * Get contents data
     * 
     * @return contents data
     */
    public SortedSet<SosObservationOffering> getContents() {
        return Collections.unmodifiableSortedSet(contents);
    }

    /**
     * Set contents data
     * 
     * @param contents
     *            contents data
     */
    public void setContents(Collection<SosObservationOffering> contents) {
        this.contents =
                contents == null ? new TreeSet<SosObservationOffering>() : new TreeSet<SosObservationOffering>(
                        contents);
    }

    public boolean isSetContents() {
        return contents != null && !contents.isEmpty();
    }

    /**
     * Set extension data
     * 
     * @param extensions
     *            extension data
     */
    public void setExensions(Collection<CapabilitiesExtension> extensions) {
        this.extensions =
                extensions == null ? new LinkedList<CapabilitiesExtension>() : new ArrayList<CapabilitiesExtension>(
                        extensions);
    }

    /**
     * Get extension data
     * 
     * @return extension data
     */
    public List<CapabilitiesExtension> getExtensions() {
        return Collections.unmodifiableList(this.extensions);
    }

    public boolean isSetExtensions() {
        return CollectionHelper.isNotEmpty(getExtensions());
    }

}

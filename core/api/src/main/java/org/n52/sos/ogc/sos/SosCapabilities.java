/*
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
package org.n52.sos.ogc.sos;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.filter.FilterCapabilities;
import org.n52.shetland.ogc.ows.OwsCapabilities;
import org.n52.shetland.ogc.ows.OwsCapabilitiesExtension;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.util.CollectionHelper;

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
    private Optional<FilterCapabilities> filterCapabilities;

    /**
     * All ObservationOfferings provided by this SOS.
     */
    private Optional<SortedSet<SosObservationOffering>> contents;

    public SosCapabilities(SosCapabilities owsCapabilities) {
        super(owsCapabilities);
        this.filterCapabilities = owsCapabilities.getFilterCapabilities();
        this.contents = owsCapabilities.getContents();
    }

    public SosCapabilities(OwsCapabilities owsCapabilities) {
        this(owsCapabilities, null, null);
    }

    public SosCapabilities(OwsCapabilities owsCapabilities, FilterCapabilities filterCapabilities, Collection<SosObservationOffering> contents) {
        super(owsCapabilities);
        this.filterCapabilities = Optional.ofNullable(filterCapabilities);
        this.contents = Optional.ofNullable(contents).map(CollectionHelper::newSortedSet);
    }

    public SosCapabilities(String service, String version, String updateSequence,
                           OwsServiceIdentification serviceIdentification, OwsServiceProvider serviceProvider,
                           OwsOperationsMetadata operationsMetadata, Set<String> languages,
                           FilterCapabilities filterCapabilities,
                           Collection<SosObservationOffering> contents,
                           Collection<OwsCapabilitiesExtension> extensions) {
        super(SosConstants.SOS, version, updateSequence, serviceIdentification, serviceProvider, operationsMetadata, languages, extensions);
        this.filterCapabilities = Optional.ofNullable(filterCapabilities);
        this.contents = Optional.ofNullable(contents).map(CollectionHelper::newSortedSet);
    }

    /**
     * Get filter capabilities
     *
     * @return filter capabilities
     */
    public Optional<FilterCapabilities> getFilterCapabilities() {
        return filterCapabilities;
    }

    public void setFilterCapabilities(FilterCapabilities filterCapabilities) {
        this.filterCapabilities = Optional.ofNullable(filterCapabilities);
    }

    /**
     * Get contents data
     *
     * @return contents data
     */
    public Optional<SortedSet<SosObservationOffering>> getContents() {
        return this.contents.map(Collections::unmodifiableSortedSet);
    }

    public void setContents(Collection<SosObservationOffering> contents) {
        this.contents = Optional.ofNullable(contents).map(CollectionHelper::newSortedSet);
    }
}

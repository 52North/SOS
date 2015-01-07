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

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.n52.sos.ogc.ows.MergableExtension;
import org.n52.sos.util.CollectionHelper;

/**
 * @since 4.0.0
 * 
 */
public class SosInsertionCapabilities implements CapabilitiesExtension, MergableExtension<SosInsertionCapabilities> {
    private static final String SECTION_NAME = Sos2Constants.CapabilitiesSections.InsertionCapabilities.name();

    private SortedSet<String> featureOfInterestTypes = new TreeSet<String>();

    private SortedSet<String> observationTypes = new TreeSet<String>();

    private SortedSet<String> procedureDescriptionFormats = new TreeSet<String>();

    private SortedSet<String> supportedEncodings = new TreeSet<String>();

    public SortedSet<String> getFeatureOfInterestTypes() {
        return Collections.unmodifiableSortedSet(featureOfInterestTypes);
    }

    public SortedSet<String> getObservationTypes() {
        return Collections.unmodifiableSortedSet(observationTypes);
    }

    public SortedSet<String> getProcedureDescriptionFormats() {
        return Collections.unmodifiableSortedSet(procedureDescriptionFormats);
    }

    public SortedSet<String> getSupportedEncodings() {
        return Collections.unmodifiableSortedSet(supportedEncodings);
    }

    public void addFeatureOfInterestTypes(Collection<String> featureOfInterestTypes) {
        this.featureOfInterestTypes.addAll(featureOfInterestTypes);
    }

    public void addObservationTypes(Collection<String> observationTypes) {
        this.observationTypes.addAll(observationTypes);
    }

    public void addProcedureDescriptionFormats(Collection<String> procedureDescriptionFormats) {
        this.procedureDescriptionFormats.addAll(procedureDescriptionFormats);
    }

    public void addSupportedEncodings(Collection<String> supportedEncodings) {
        this.supportedEncodings.addAll(supportedEncodings);
    }

    public void addFeatureOfInterestType(String featureOfInterestType) {
        this.featureOfInterestTypes.add(featureOfInterestType);
    }

    public void addObservationType(String observationType) {
        this.observationTypes.add(observationType);
    }

    public void addProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormats.add(procedureDescriptionFormat);
    }

    public void addSupportedEncoding(String supportedEncoding) {
        this.supportedEncodings.add(supportedEncoding);
    }

    public boolean isSetFeatureOfInterestTypes() {
        return CollectionHelper.isNotEmpty(featureOfInterestTypes);
    }

    public boolean isSetObservationTypes() {
        return CollectionHelper.isNotEmpty(observationTypes);
    }

    public boolean isSetProcedureDescriptionFormats() {
        return CollectionHelper.isNotEmpty(procedureDescriptionFormats);
    }

    public boolean isSetSupportedEncodings() {
        return CollectionHelper.isNotEmpty(supportedEncodings);
    }

    @Override
    public String getSectionName() {
        return SECTION_NAME;
    }

    @Override
    public void merge(SosInsertionCapabilities insertionCapabilities) {
        addFeatureOfInterestTypes(insertionCapabilities.getFeatureOfInterestTypes());
        addObservationTypes(insertionCapabilities.getObservationTypes());
        addProcedureDescriptionFormats(insertionCapabilities.getProcedureDescriptionFormats());
        addSupportedEncodings(insertionCapabilities.getSupportedEncodings());
    }
}

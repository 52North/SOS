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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class KeywordEnrichment extends SensorMLEnrichment {
    @Override
    protected void enrich(AbstractSensorML description) throws OwsExceptionReport {
        List<String> keywords = createKeywordsList(description);
        if (CollectionHelper.isNotEmpty(keywords)) {
            description.setKeywords(keywords);
        }
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithDiscoveryInformation();
    }

    private List<String> createKeywordsList(AbstractSensorML description) {
        Set<String> keywords = Sets.newHashSet();
        addExisting(description, keywords);
        addObservableProperties(keywords);
        addIdentifier(keywords);
        addIntendedApplication(keywords);
        addProcedureType(keywords);
        addOfferings(keywords);
        addLongName(description, keywords);
        addShortName(description, keywords);
        addFeatures(description, keywords);
        return Lists.newArrayList(keywords);
    }

    private void addLongName(AbstractSensorML description,Set<String> keywords) {
        Optional<SmlIdentifier> longName = description
                .findIdentification(longNamePredicate());
        if (longName.isPresent()) {
            keywords.add(longName.get().getValue());
        }
    }

    private void addShortName(AbstractSensorML description,Set<String> keywords) {
        Optional<SmlIdentifier> shortName = description
                .findIdentification(shortNamePredicate());
        if (shortName.isPresent()) {
            keywords.add(shortName.get().getValue());
        }
    }

    private void addFeatures(AbstractSensorML description,Set<String> keywords) {
        if (procedureSettings().isEnrichWithFeatures() &&
            description.isSetFeaturesOfInterest()) {
            keywords.addAll(description.getFeaturesOfInterest());
        }
    }

    private void addOfferings(Set<String> keywords) {
        if (procedureSettings().isEnrichWithOfferings()) {
            keywords.addAll(getCache().getOfferingsForProcedure(getIdentifier()));
        }
    }

    private void addProcedureType(Set<String> keywords) {
        if (procedureSettings().isGenerateClassification() &&
            !procedureSettings().getClassifierProcedureTypeValue().isEmpty()) {
            keywords.add(procedureSettings().getClassifierProcedureTypeValue());
        }
    }

    private void addIntendedApplication(Set<String> keywords) {
        if (procedureSettings().isGenerateClassification() &&
            !procedureSettings().getClassifierIntendedApplicationValue().isEmpty()) {
            keywords.add(procedureSettings().getClassifierIntendedApplicationValue());
        }
    }

    private void addObservableProperties(Set<String> keywords) {
        keywords.addAll(getCache()
                .getObservablePropertiesForProcedure(getIdentifier()));
    }

    private void addExisting(AbstractSensorML description,Set<String> keywords) {
        if (description.isSetKeywords()) {
            keywords.addAll(description.getKeywords());
        }
    }

    private void addIdentifier(Set<String> keywords) {
        keywords.add(getIdentifier());
    }
}

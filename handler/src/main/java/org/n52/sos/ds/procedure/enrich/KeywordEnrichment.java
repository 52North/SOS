/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure.enrich;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class KeywordEnrichment extends SensorMLEnrichment {

    public KeywordEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

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

    private void addLongName(AbstractSensorML description, Set<String> keywords) {
        Optional<SmlIdentifier> longName = description
                .findIdentification(longNamePredicate());
        if (longName.isPresent()) {
            keywords.add(longName.get().getValue());
        }
    }

    private void addShortName(AbstractSensorML description, Set<String> keywords) {
        Optional<SmlIdentifier> shortName = description
                .findIdentification(shortNamePredicate());
        if (shortName.isPresent()) {
            keywords.add(shortName.get().getValue());
        }
    }

    private void addFeatures(AbstractSensorML description, Set<String> keywords) {
        if (procedureSettings().isEnrichWithFeatures() &&
                getDescription().isSetFeaturesOfInterest()) {
            keywords.addAll(getDescription().getFeaturesOfInterest());
        }
    }

    private void addOfferings(Set<String> keywords) {
        if (procedureSettings().isEnrichWithOfferings()) {
            for (String offering : getCache()
                    .getOfferingsForProcedure(getIdentifier())) {
                if (getCache().getPublishedOfferings().contains(offering)) {
                    keywords.add(offering);
                }
            }
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
        for (String obsProp : getCache()
                .getObservablePropertiesForProcedure(getIdentifier())) {
            if (getCache().getPublishedObservableProperties().contains(obsProp)) {
                keywords.add(obsProp);
            }
        }
    }

    private void addExisting(AbstractSensorML description, Set<String> keywords) {
        if (description.isSetKeywords()) {
            keywords.addAll(description.getKeywords());
        }
    }

    private void addIdentifier(Set<String> keywords) {
        keywords.add(getIdentifier());
    }
}

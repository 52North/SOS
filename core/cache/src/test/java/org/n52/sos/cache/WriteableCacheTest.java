/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import org.junit.Before;
import org.junit.Test;

public class WriteableCacheTest {
    private static final String FEATURE = "feature";
    private static final String FEATUREA_OTHER = "featureOther";
    private static final String FEATURE_NAME = "featureName";
    private static final String FEATURE_NAME_OTHER = "featureNameOther";
    private static final String PROCEDURE = "procedure";
    private static final String PROCEDURE_OTHER = "procedureOther";
    private static final String PROCEDURE_NAME = "procedureName";
    private static final String PROCEDURE_NAME_OTHER = "procedureNameOther";
    private static final String OBSERVED_PROPERTY = "observedProperty";
    private static final String OBSERVED_PROPERTY_OTHER = "observedPropertyOther";
    private static final String OBSERVED_PROPERTY_NAME = "observedPropertyName";
    private static final String OBSERVED_PROPERTY_NAME_OTHER = "observedPropertyNameOther";
    private static final String OFFERING = "offering";
    private static final String OFFERING_OTHER = "offeringOther";
    private static final String OFFERING_NAME = "offeringName";
    private static final String OFFERING_NAME_OTHER = "offeringNameOther";
    private InMemoryCacheImpl cache;

    @Before
    public void init() {
        cache = new InMemoryCacheImpl();
        cache.addFeatureOfInterestIdentifierHumanReadableName(FEATURE, FEATURE_NAME);
        cache.addProcedureIdentifierHumanReadableName(PROCEDURE, PROCEDURE_NAME);
        cache.addObservablePropertyIdentifierHumanReadableName(OBSERVED_PROPERTY, OBSERVED_PROPERTY_NAME);
        cache.addOfferingIdentifierHumanReadableName(OFFERING, OFFERING_NAME);
    }

    @Test
    public void test_same_identifier_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(FEATURE, FEATURE_NAME);
    }

    @Test
    public void test_same_identifier_other_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(FEATURE, FEATURE_NAME_OTHER);
    }

    @Test
    public void test_other_identifier_same_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(FEATUREA_OTHER, FEATURE_NAME);
    }

    @Test
    public void test_other_identifier_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(FEATUREA_OTHER, FEATURE_NAME_OTHER);
    }

    @Test
    public void test_same_identifier_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(PROCEDURE, PROCEDURE_NAME);
    }

    @Test
    public void test_same_identifier_other_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(PROCEDURE, PROCEDURE_NAME_OTHER);
    }

    @Test
    public void test_other_identifier_same_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(PROCEDURE_OTHER, PROCEDURE_NAME);
    }

    @Test
    public void test_other_identifier_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(PROCEDURE_OTHER, PROCEDURE_NAME_OTHER);
    }

    @Test
    public void test_same_identifier_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(OBSERVED_PROPERTY, OBSERVED_PROPERTY_NAME);
    }

    @Test
    public void test_same_identifier_other_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(OBSERVED_PROPERTY, OBSERVED_PROPERTY_NAME_OTHER);
    }

    @Test
    public void test_other_identifier_same_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(OBSERVED_PROPERTY_OTHER, OBSERVED_PROPERTY_NAME);
    }

    @Test
    public void test_other_identifier_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(OBSERVED_PROPERTY_OTHER, OBSERVED_PROPERTY_NAME_OTHER);
    }

    @Test
    public void test_same_identifier_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(OFFERING, OFFERING_NAME);
    }

    @Test
    public void test_same_identifier_other_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(OFFERING, OFFERING_NAME_OTHER);
    }

    @Test
    public void test_other_identifier_same_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(OFFERING_OTHER, OFFERING_NAME);
    }

    @Test
    public void test_other_identifier_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(OFFERING_OTHER, OFFERING_NAME_OTHER);
    }

}

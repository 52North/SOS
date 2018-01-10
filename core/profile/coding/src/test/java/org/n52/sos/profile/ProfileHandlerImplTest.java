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
package org.n52.sos.profile;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.n52.sos.service.profile.Profile;

public class ProfileHandlerImplTest {

    private ProfileHandlerImpl phi;

    @Before
    public void setup() {
        this.phi = new ProfileHandlerImpl();
        this.phi.init();
    }

    @Test
    public void is_aktive_profile_SOS_20_PROFILE() {
        assertThat(phi.getActiveProfile().getIdentifier(), is("SOS_20_PROFILE"));
    }

    @Test
    public void is_two_profles_available() {
        assertThat(phi.getAvailableProfiles().size(), is(2));
    }

    @Test
    public void check_SOS_20_PROFILE() {
        Profile profile = phi.getAvailableProfiles().get("SOS_20_PROFILE");
        assertNotNull(profile);
        assertThat(profile.isActiveProfile(), is(true));
        assertThat(profile.getObservationResponseFormat(), is("http://www.opengis.net/om/2.0"));
        assertThat(profile.isEncodeFeatureOfInterestInObservations(), is(false));
        assertThat(profile.isShowMetadataOfEmptyObservations(), is(false));
        assertThat(profile.isListFeatureOfInterestsInOfferings(), is(true));
        assertThat(profile.isEncodeChildProcedureDescriptions(), is(false));
        assertThat(profile.isShowFullOperationsMetadata(), is(true));
        assertThat(profile.isShowFullOperationsMetadataForObservations(), is(true));
        assertThat(profile.isAllowSubsettingForSOS20OM20(), is(false));
        assertThat(profile.isMergeValues(), is(false));
        assertThat(profile.getResponseNoDataPlaceholder(), is("noData"));
        assertThat(profile.isSetNoDataPlaceholder(), is(true));
        assertThat(profile.getNoDataPlaceholder().size(), is(1));
        assertThat(profile.getNoDataPlaceholder().iterator().next(), is("noData"));
        assertThat(profile.isReturnLatestValueIfTemporalFilterIsMissingInGetObservation(), is(false));
    }

    @Test
    public void check_hydrology() {
        Profile profile = phi.getAvailableProfiles().get("hydrology");
        assertNotNull(profile);
        assertThat(profile.isActiveProfile(), is(false));
        assertThat(profile.getObservationResponseFormat(), is("http://www.opengis.net/waterml/2.0"));
        assertThat(profile.isEncodeFeatureOfInterestInObservations(), is(false));
        assertThat(profile.getEncodingNamespaceForFeatureOfInterest(), is("http://www.opengis.net/waterml/2.0"));
        assertThat(profile.isShowMetadataOfEmptyObservations(), is(true));
        assertThat(profile.isListFeatureOfInterestsInOfferings(), is(true));
        assertThat(profile.isEncodeChildProcedureDescriptions(), is(false));
        assertThat(profile.isShowFullOperationsMetadata(), is(true));
        assertThat(profile.isShowFullOperationsMetadataForObservations(), is(true));
        assertThat(profile.isAllowSubsettingForSOS20OM20(), is(true));
        assertThat(profile.isMergeValues(), is(true));
        assertThat(profile.getResponseNoDataPlaceholder(), is("noData"));
        assertThat(profile.isSetNoDataPlaceholder(), is(true));
        assertThat(profile.getNoDataPlaceholder().size(), is(1));
        assertThat(profile.getNoDataPlaceholder().iterator().next(), is("noData"));
        assertThat(profile.isReturnLatestValueIfTemporalFilterIsMissingInGetObservation(), is(true));
        assertThat(profile.isEncodeProcedureInObservation(), is(true));
        assertThat(profile.isEncodeProcedureInObservation("http://www.opengis.net/waterml/2.0/observationProcess"), is(true));
        assertThat(profile.getDefaultObservationTypesForEncoding().isEmpty(), is(false));
        assertThat(profile.getDefaultObservationTypesForEncoding().containsKey("http://www.opengis.net/waterml/2.0"), is(true));
        assertThat(profile.getDefaultObservationTypesForEncoding().get("http://www.opengis.net/waterml/2.0"), is("http://www.opengis.net/def/observationType/waterml/2.0/MeasurementTimeseriesTVPObservation"));
    }
}

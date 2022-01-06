/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.faroe.json.JsonConfiguration;
import org.n52.iceland.service.ServletConfigLocationProvider;
import org.n52.janmayen.ConfigLocationProvider;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.sos.profile.ProfileHandlerImplTest.TargetTestConfigLocationProvider;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.Profiles;

public class ProfileHandlerImplTest implements Profiles {

    private static final String PROFILE_SOS = "SOS_20_PROFILE";

    private static final String PROFILE_HYDRO = "hydrology";

    private static final String NO_DATA = "noData";

    private ProfileHandlerImpl phi;

    private JsonConfiguration jsonConfiguration;

    @Before
    public void setup() {
        this.phi = new ProfileHandlerImpl();
        jsonConfiguration = new JsonConfiguration();
        jsonConfiguration.setConfigLocationProvider(new TargetTestConfigLocationProvider());
        jsonConfiguration.init();
        this.phi.setConfiguration(jsonConfiguration);
        this.phi.init();
    }

    @After
    public void teardown() {
        jsonConfiguration.delete();
    }


    @Test
    public void is_aktive_profile_SOS_20_PROFILE() {
        MatcherAssert.assertThat(phi.getActiveProfile().getIdentifier(), Is.is(PROFILE_SOS));
    }

    @Test
    public void is_three_profles_available() {
        MatcherAssert.assertThat(phi.getAvailableProfiles().size(), Is.is(3));
    }

    @Test
    public void check_DEFAULT_PROFILE() {
        testSosProfile(createSosProfile());
    }

    @Test
    public void check_SOS_20_PROFILE() {
        testSosProfile(phi.getAvailableProfiles().get(PROFILE_SOS));
    }

    private void testSosProfile(Profile profile) {
        Assert.assertNotNull(profile);
        MatcherAssert.assertThat(profile.isActiveProfile(), Is.is(true));
        MatcherAssert.assertThat(profile.getObservationResponseFormat(), Is.is("http://www.opengis.net/om/2.0"));
        MatcherAssert.assertThat(profile.isEncodeFeatureOfInterestInObservations(), Is.is(false));
        MatcherAssert.assertThat(profile.isShowMetadataOfEmptyObservations(), Is.is(false));
        MatcherAssert.assertThat(profile.isListFeatureOfInterestsInOfferings(), Is.is(true));
        MatcherAssert.assertThat(profile.isEncodeChildProcedureDescriptions(), Is.is(false));
        MatcherAssert.assertThat(profile.isShowFullOperationsMetadata(), Is.is(true));
        MatcherAssert.assertThat(profile.isShowFullOperationsMetadataForObservations(), Is.is(true));
        MatcherAssert.assertThat(profile.isAllowSubsettingForSOS20OM20(), Is.is(false));
        MatcherAssert.assertThat(profile.isMergeValues(), Is.is(false));
        MatcherAssert.assertThat(profile.getResponseNoDataPlaceholder(), Is.is(NO_DATA));
        MatcherAssert.assertThat(profile.isSetNoDataPlaceholder(), Is.is(true));
        MatcherAssert.assertThat(profile.getNoDataPlaceholder().size(), Is.is(1));
        MatcherAssert.assertThat(profile.getNoDataPlaceholder().iterator().next(), Is.is(NO_DATA));
        MatcherAssert.assertThat(profile.isReturnLatestValueIfTemporalFilterIsMissingInGetObservation(), Is.is(false));
    }

    @Test
    public void check_hydrology() {
        Profile profile = phi.getAvailableProfiles().get(PROFILE_HYDRO);
        Assert.assertNotNull(profile);
        MatcherAssert.assertThat(profile.isActiveProfile(), Is.is(false));
        MatcherAssert.assertThat(profile.getObservationResponseFormat(), Is.is(WaterMLConstants.NS_WML_20));
        MatcherAssert.assertThat(profile.isEncodeFeatureOfInterestInObservations(), Is.is(false));
        MatcherAssert.assertThat(profile.getEncodingNamespaceForFeatureOfInterest(), Is.is(WaterMLConstants.NS_WML_20));
        MatcherAssert.assertThat(profile.isShowMetadataOfEmptyObservations(), Is.is(true));
        MatcherAssert.assertThat(profile.isListFeatureOfInterestsInOfferings(), Is.is(true));
        MatcherAssert.assertThat(profile.isEncodeChildProcedureDescriptions(), Is.is(false));
        MatcherAssert.assertThat(profile.isShowFullOperationsMetadata(), Is.is(true));
        MatcherAssert.assertThat(profile.isShowFullOperationsMetadataForObservations(), Is.is(true));
        MatcherAssert.assertThat(profile.isAllowSubsettingForSOS20OM20(), Is.is(true));
        MatcherAssert.assertThat(profile.isMergeValues(), Is.is(true));
        MatcherAssert.assertThat(profile.getResponseNoDataPlaceholder(), Is.is(NO_DATA));
        MatcherAssert.assertThat(profile.isSetNoDataPlaceholder(), Is.is(true));
        MatcherAssert.assertThat(profile.getNoDataPlaceholder().size(), Is.is(1));
        MatcherAssert.assertThat(profile.getNoDataPlaceholder().iterator().next(), Is.is(NO_DATA));
        MatcherAssert.assertThat(profile.isReturnLatestValueIfTemporalFilterIsMissingInGetObservation(), Is.is(true));
        MatcherAssert.assertThat(profile.isEncodeProcedureInObservation(), Is.is(true));
        MatcherAssert.assertThat(
                profile.isEncodeProcedureInObservation("http://www.opengis.net/waterml/2.0/observationProcess"),
                Is.is(true));
        MatcherAssert.assertThat(profile.getObservationTypesForEncoding().isEmpty(), Is.is(false));
        MatcherAssert.assertThat(profile.getObservationTypesForEncoding().containsKey(WaterMLConstants.NS_WML_20),
                Is.is(true));
        MatcherAssert.assertThat(profile.getObservationTypesForEncoding().get(WaterMLConstants.NS_WML_20),
                Is.is("http://www.opengis.net/def/observationType/waterml/2.0/MeasurementTimeseriesTVPObservation"));
    }

    @Test
    public void check_persist() {
        phi.activateProfile(PROFILE_HYDRO);
        phi.persist();
        jsonConfiguration.writeNow();
        phi.reloadProfiles();
        MatcherAssert.assertThat(phi.getAvailableProfiles().get(PROFILE_HYDRO).isActiveProfile(), Is.is(true));
        MatcherAssert.assertThat(phi.getAvailableProfiles().get(PROFILE_SOS).isActiveProfile(), Is.is(false));
        MatcherAssert.assertThat(phi.getActiveProfile().getIdentifier(), Is.is(PROFILE_HYDRO));

        phi.activateProfile(PROFILE_SOS);
        phi.persist();
        jsonConfiguration.writeNow();
        phi.reloadProfiles();
        MatcherAssert.assertThat(phi.getAvailableProfiles().get(PROFILE_HYDRO).isActiveProfile(), Is.is(false));
        MatcherAssert.assertThat(phi.getAvailableProfiles().get(PROFILE_SOS).isActiveProfile(), Is.is(true));
        MatcherAssert.assertThat(phi.getActiveProfile().getIdentifier(), Is.is(PROFILE_SOS));

    }

    public class TargetTestConfigLocationProvider implements ConfigLocationProvider {

        @Override
        public String get() {
            return "target/";
        }

    }
}

/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service;

import static java.lang.Boolean.FALSE;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ogc.OGCConstants;

import com.google.common.collect.ImmutableSet;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MiscSettings implements SettingDefinitionProvider {
    public static final String TOKEN_SEPARATOR = "misc.tokenSeparator";

    public static final String TUPLE_SEPARATOR = "misc.tupleSeparator";
    
    public static final String DECIMAL_SEPARATOR = "misc.decimalSeparator";

    public static final String CHARACTER_ENCODING = "misc.characterEncoding";

    public static final String SRS_NAME_PREFIX_SOS_V1 = "misc.srsNamePrefixSosV1";

    public static final String SRS_NAME_PREFIX_SOS_V2 = "misc.srsNamePrefixSosV2";

    public static final String DEFAULT_OFFERING_PREFIX = "misc.defaultOfferingPrefix";

    public static final String DEFAULT_PROCEDURE_PREFIX = "misc.defaultProcedurePrefix";

    public static final String HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING = "misc.httpResponseCodeUseInKvpAndPoxBinding";

    public static final String RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES =
            "misc.relatedSamplingFeatureRoleForChildFeatures";

    public static final String HYDRO_MAX_NUMBER_OF_RETURNED_VALUES = "profile.hydrology.maxReturnedValue";

    public static final String HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES = "profile.hydrology.maxReturnedTimeSeries";

    public static final String RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST = "profile.hydrology.overallExtrema";

    public static final String CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES = "misc.convertComplexProfileToSingleProfiles";
    
    public static final String MERGE_FOR_EPRTR = "misc.merge.eprtr";

    public static final SettingDefinitionGroup GROUP = 
    		new SettingDefinitionGroup()
		    		.setTitle("Miscellaneous")
		            .setOrder(ORDER_3);

    public static final StringSettingDefinition TOKEN_SEPERATOR_DEFINITION = 
    		new StringSettingDefinition()
		            .setGroup(GROUP)
		            .setOrder(ORDER_0)
		            .setKey(TOKEN_SEPARATOR)
		            .setDefaultValue(",")
		            .setTitle("Token separator")
		            .setDescription("Token separator in result element (a character). <strong>MUST</strong> not be "
		                    + "part of the data.");

    public static final StringSettingDefinition TUPLE_SEPERATOR_DEFINITION = 
    		new StringSettingDefinition()
		            .setGroup(GROUP)
		            .setOrder(ORDER_1)
		            .setKey(TUPLE_SEPARATOR)
		            .setDefaultValue("@@")
		            .setTitle("Tuple separator")
		            .setDescription("Tuple separator in result element (a character). <strong>MUST</strong> not be "
                            + "part of the data.");
    
    public static final StringSettingDefinition DECIMAL_SEPERATOR_DEFINITION = 
    		new StringSettingDefinition()
				    .setGroup(GROUP)
				    .setOrder(ORDER_2)
				    .setKey(DECIMAL_SEPARATOR)
				    .setDefaultValue(".")
				    .setTitle("Decimal separator")
				    .setDescription("Decimal separator in result element (a character). When your data contains values"
				            + " like <tt>2,5</tt> enter <tt>,</tt>.");

    public static final StringSettingDefinition SRS_NAME_PREFIX_SOS_V1_DEFINITION = 
    		new StringSettingDefinition()
		            .setGroup(GROUP)
		            .setOrder(ORDER_4).setKey(SRS_NAME_PREFIX_SOS_V1)
		            .setDefaultValue(OGCConstants.URN_DEF_CRS_EPSG)
		            .setTitle("SOSv1 SRS Prefix")
		            .setDescription("Prefix for the SRS name in SOS v1.0.0.");

    public static final StringSettingDefinition SRS_NAME_PREFIX_SOS_V2_DEFINITION = 
    		new StringSettingDefinition()
		            .setGroup(GROUP).setOrder(ORDER_5)
		            .setKey(SRS_NAME_PREFIX_SOS_V2)
		            .setDefaultValue(OGCConstants.URL_DEF_CRS_EPSG)
		            .setTitle("SOSv2 SRS Prefix")
		            .setDescription("Prefix for the SRS name in SOS v2.0.0.");

    public static final StringSettingDefinition CHARACTER_ENCODING_DEFINITION = 
    		new StringSettingDefinition()
		            .setGroup(GROUP).setOrder(ORDER_6)
		            .setKey(CHARACTER_ENCODING).setDefaultValue("UTF-8")
		            .setTitle("Character Encoding")
		            .setDescription("The character encoding used for responses.");

    public static final StringSettingDefinition DEFAULT_OFFERING_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(MiscSettings.GROUP)
                    .setOrder(ORDER_7)
                    .setKey(DEFAULT_OFFERING_PREFIX)
                    .setDefaultValue("http://www.example.org/offering/")
                    .setTitle("Default Offering Prefix")
                    .setDescription(
                            "The default prefix for generated offerings (if not defined in Register-/InsertSensor requests).");

    public static final StringSettingDefinition DEFAULT_PROCEDURE_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(MiscSettings.GROUP)
                    .setOrder(ORDER_8)
                    .setKey(DEFAULT_PROCEDURE_PREFIX)
                    .setDefaultValue("http://www.example.org/procedure/")
                    .setTitle("Default Procedure Prefix")
                    .setDescription(
                            "The default prefix for generated procedures (if not defined in Register-/InsertSensor requests).");

    public static final BooleanSettingDefinition HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_12)
                    .setKey(HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING)
                    .setDefaultValue(FALSE)
                    .setTitle("Use HTTP Status Codes in KVP and POX Binding?")
                    .setDescription(
                            "Should the response returned by KVP and POX binding use the exception specific HTTP status code or always <tt>HTTP 200 - OK</tt>.");

    public static final StringSettingDefinition RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setKey(RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES)
                    .setDefaultValue("isSampledAt")
                    .setTitle("Role for childs of related features")
                    .setDescription(
                            "The value for the role of an child feature. It is used when a related feature is already sampled at the child feature.")
                    .setOrder(ORDER_13);

    // TODO move to Profile settings if implemented
    public static final IntegerSettingDefinition HYDRO_MAX_NUMBER_OF_RETURNED_VALUES_DEFINITION =
            new IntegerSettingDefinition()
                    .setGroup(GROUP)
                    .setKey(HYDRO_MAX_NUMBER_OF_RETURNED_VALUES)
                    .setDefaultValue(0)
                    .setTitle("Maximum number of returned observation values")
                    .setDescription("Set the maximum number of returned observation values for the Hydrology-Profile. Set to <code>0</code> (zero) for unlimited number of observations.")
                    .setOrder(ORDER_14);

    // TODO move to Profile settings if implemented
    public static final IntegerSettingDefinition HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES_DEFINITION =
            new IntegerSettingDefinition()
                    .setGroup(GROUP)
                    .setKey(HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES)
                    .setDefaultValue(0)
                    .setTitle("Maximum number of returned time series")
                    .setDescription("Set the maximum number of returned time series for the Hydrology-Profile. Set to <code>0</code> (zero) for unlimited number of observations.")
                    .setOrder(ORDER_15);

    // TODO move to Profile settings if implemented
    public static final BooleanSettingDefinition RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_16)
                    .setKey(RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST)
                    .setDefaultValue(false)
                    .setTitle("Should the SOS return overall extrema for time series?")
                    .setDescription(
                            "The service will return <strong>overall extrema</strong> for first/latest observation "
                            + "queries when this option is <strong>activated</strong>. When disabled, only for "
                            + "each time series");
    
    public static final BooleanSettingDefinition CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_17)
                    .setKey(CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES)
                    .setDefaultValue(true)
                    .setTitle("Should the SOS convert complex profiles to single profiles?")
                    .setDescription(
                            "Should the SOS convert complex profiles to single profiles?");
    
    public static final BooleanSettingDefinition MERGE_FOR_EPRTR_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_18)
                    .setKey(MERGE_FOR_EPRTR)
                    .setDefaultValue(false)
                    .setTitle("Should the SOS merge observations for E-PRTR?")
                    .setDescription(
                            "Should the SOS merge observations for E-PRTR?");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(
            TOKEN_SEPERATOR_DEFINITION, TUPLE_SEPERATOR_DEFINITION,DECIMAL_SEPERATOR_DEFINITION,
            SRS_NAME_PREFIX_SOS_V1_DEFINITION, SRS_NAME_PREFIX_SOS_V2_DEFINITION, DEFAULT_OFFERING_PREFIX_DEFINITION,
            DEFAULT_PROCEDURE_PREFIX_DEFINITION, CHARACTER_ENCODING_DEFINITION,
            HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING_DEFINITION, HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES_DEFINITION,
            HYDRO_MAX_NUMBER_OF_RETURNED_VALUES_DEFINITION, RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST_DEFINITION,
            CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES_DEFINITION, MERGE_FOR_EPRTR_DEFINITION
            /*, RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES_DEFINITION */);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}

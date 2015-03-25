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
package org.n52.sos.service;

import static org.n52.sos.service.MiscSettings.CHARACTER_ENCODING;
import static org.n52.sos.service.MiscSettings.DEFAULT_FEATURE_PREFIX;
import static org.n52.sos.service.MiscSettings.DEFAULT_OBSERVABLEPROPERTY_PREFIX;
import static org.n52.sos.service.MiscSettings.DEFAULT_OFFERING_PREFIX;
import static org.n52.sos.service.MiscSettings.DEFAULT_PROCEDURE_PREFIX;
import static org.n52.sos.service.MiscSettings.HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING;
import static org.n52.sos.service.MiscSettings.SRS_NAME_PREFIX_SOS_V1;
import static org.n52.sos.service.MiscSettings.SRS_NAME_PREFIX_SOS_V2;
import static org.n52.sos.service.ServiceSettings.ADD_OUTPUTS_TO_SENSOR_ML;
import static org.n52.sos.service.ServiceSettings.ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR;
import static org.n52.sos.service.ServiceSettings.SENSOR_DIRECTORY;
import static org.n52.sos.service.ServiceSettings.SERVICE_URL;
import static org.n52.sos.service.ServiceSettings.STRICT_SPATIAL_FILTERING_PROFILE;
import static org.n52.sos.service.ServiceSettings.USE_DEFAULT_PREFIXES;
import static org.n52.sos.service.ServiceSettings.VALIDATE_RESPONSE;

import java.net.URI;
import java.util.Locale;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.i18n.I18NSettings;
import org.n52.sos.util.Validation;
import org.n52.sos.util.XmlOptionsHelper;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
@Configurable
public class ServiceConfiguration {
    private static ServiceConfiguration instance;

    /**
     * character encoding for responses.
     */
    private String characterEncoding;

    private String defaultOfferingPrefix;

    private String defaultProcedurePrefix;

    private String defaultObservablePropertyPrefix;

    private String defaultFeaturePrefix;

    private boolean useDefaultPrefixes;

    private boolean encodeFullChildrenInDescribeSensor;

    private boolean addOutputsToSensorML;

    private boolean strictSpatialFilteringProfile;

    private boolean validateResponse;

    private boolean useHttpStatusCodesInKvpAndPoxBinding;

    /**
     * @return Returns a singleton instance of the ServiceConfiguration.
     */
    public static synchronized ServiceConfiguration getInstance() {
        if (instance == null) {
            instance = new ServiceConfiguration();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    /**
     * private constructor for singleton
     */
    private ServiceConfiguration() {
    }

    /**
     * URL of this service.
     */
    private String serviceURL;

    /**
     * directory of sensor descriptions in SensorML format.
     */
    private String sensorDirectory;

    /**
     * Prefix URN for the spatial reference system.
     */
    private String srsNamePrefix;

    /**
     * prefix URN for the spatial reference system.
     */
    private String srsNamePrefixSosV2;

    /**
     * boolean indicates, whether SOS supports quality information in
     * observations.
     */
    private final boolean supportsQuality = true;

    /**
     * token separator for result element.
     */
    private String tokenSeparator;

    /**
     * tuple separator for result element.
     */
    private String tupleSeparator;
    
    /**
     * decimal separator for result element.
     */
    private String decimalSeparator;

    private boolean deregisterJdbcDriver;

    private Locale defaultLanguage;

    private boolean showAllLanguageValues;

    private int maxNumberOfReturnedTimeSeries = Integer.MAX_VALUE;

    private int maxNumberOfReturnedValues = Integer.MAX_VALUE;

    private boolean overallExtrema = true;

    private boolean streamingEncoding = true;

    /**
     * Returns the default token seperator for results.
     * <p/>
     *
     * @return the tokenSeperator.
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    @Setting(MiscSettings.TOKEN_SEPARATOR)
    public void setTokenSeparator(final String separator) throws ConfigurationException {
        Validation.notNullOrEmpty("Token separator", separator);
        tokenSeparator = separator;
    }

    public String getTupleSeparator() {
        return tupleSeparator;
    }

    @Setting(MiscSettings.TUPLE_SEPARATOR)
    public void setTupleSeparator(final String separator) throws ConfigurationException {
        Validation.notNullOrEmpty("Tuple separator", separator);
        tupleSeparator = separator;
    }
    

    public String getDecimalSeparator() {
        return decimalSeparator;
    }
    
    @Setting(MiscSettings.DECIMAL_SEPARATOR)
    public void setDecimalSeparator(final String separator) throws ConfigurationException {
        Validation.notNullOrEmpty("Decimal separator", separator);
        decimalSeparator = separator;
    }

    @Setting(CHARACTER_ENCODING)
    public void setCharacterEncoding(final String encoding) throws ConfigurationException {
        Validation.notNullOrEmpty("Character Encoding", encoding);
        characterEncoding = encoding;
        XmlOptionsHelper.getInstance().setCharacterEncoding(characterEncoding);
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public String getDefaultOfferingPrefix() {
        return defaultOfferingPrefix;
    }

    @Setting(DEFAULT_OFFERING_PREFIX)
    public void setDefaultOfferingPrefix(final String prefix) {
        defaultOfferingPrefix = prefix;
    }

    public String getDefaultProcedurePrefix() {
        return defaultProcedurePrefix;
    }

    @Setting(DEFAULT_OBSERVABLEPROPERTY_PREFIX)
    public void setDefaultObservablePropertyPrefix(final String prefix) {
        defaultObservablePropertyPrefix = prefix;
    }

    public String getDefaultObservablePropertyPrefix() {
        return defaultObservablePropertyPrefix;
    }

    @Setting(DEFAULT_PROCEDURE_PREFIX)
    public void setDefaultProcedurePrefix(final String prefix) {
        defaultProcedurePrefix = prefix;
    }

    public String getDefaultFeaturePrefix() {
        return defaultFeaturePrefix;
    }

    @Setting(DEFAULT_FEATURE_PREFIX)
    public void setDefaultFeaturePrefix(final String prefix) {
        defaultFeaturePrefix = prefix;
    }

    public boolean isUseDefaultPrefixes() {
        return useDefaultPrefixes;
    }

    @Setting(USE_DEFAULT_PREFIXES)
    public void setUseDefaultPrefixes(final boolean prefix) {
        useDefaultPrefixes = prefix;
    }

    public boolean isEncodeFullChildrenInDescribeSensor() {
        return encodeFullChildrenInDescribeSensor;
    }

    @Setting(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
    public void setEncodeFullChildrenInDescribeSensor(final boolean encodeFullChildrenInDescribeSensor) {
        this.encodeFullChildrenInDescribeSensor = encodeFullChildrenInDescribeSensor;
    }

    public boolean isAddOutputsToSensorML() {
        return addOutputsToSensorML;
    }

    @Setting(ADD_OUTPUTS_TO_SENSOR_ML)
    public void setAddOutputsToSensorML(final boolean addOutputsToSensorML) {
        this.addOutputsToSensorML = addOutputsToSensorML;
    }

    public boolean isStrictSpatialFilteringProfile() {
        return strictSpatialFilteringProfile;
    }

    @Setting(STRICT_SPATIAL_FILTERING_PROFILE)
    public void setStrictSpatialFilteringProfile(final boolean strictSpatialFilteringProfile) {
        this.strictSpatialFilteringProfile = strictSpatialFilteringProfile;
    }

    public boolean isValidateResponse() {
        return validateResponse;
    }

    @Setting(VALIDATE_RESPONSE)
    public void setValidateResponse(final boolean validateResponse) {
        this.validateResponse = validateResponse;
    }

    /**
     * @return the supportsQuality
     */
    // HibernateObservationUtilities
    public boolean isSupportsQuality() {
        return supportsQuality;
    }

    // @Setting(SUPPORTS_QUALITY)
    // public void setSupportsQuality(final boolean supportsQuality) {
    // this.supportsQuality = supportsQuality;
    // }

    public boolean isUseHttpStatusCodesInKvpAndPoxBinding() {
        return useHttpStatusCodesInKvpAndPoxBinding;
    }

    @Setting(HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING)
    public void setUseHttpStatusCodesInKvpAndPoxBinding(final boolean useHttpStatusCodesInKvpAndPoxBinding) {
        Validation.notNull(HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING, useHttpStatusCodesInKvpAndPoxBinding);
        this.useHttpStatusCodesInKvpAndPoxBinding = useHttpStatusCodesInKvpAndPoxBinding;
    }

    /**
     * @return Returns the sensor description directory
     */
    // HibernateProcedureUtilities
    public String getSensorDir() {
        return sensorDirectory;
    }

    @Setting(SENSOR_DIRECTORY)
    public void setSensorDirectory(final String sensorDirectory) {
        this.sensorDirectory = sensorDirectory;
    }

    /**
     * Get service URL.
     *
     * @return the service URL
     */
    public String getServiceURL() {
        return serviceURL;
    }

    @Setting(SERVICE_URL)
    public void setServiceURL(final URI serviceURL) throws ConfigurationException {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        this.serviceURL = url;
    }

    /**
     * @return prefix URN for the spatial reference system
     */
    /*
     * SosHelper AbstractKvpDecoder GmlEncoderv311 ITRequestEncoder
     */
    public String getSrsNamePrefix() {
        return srsNamePrefix;
    }

    @Setting(SRS_NAME_PREFIX_SOS_V1)
    public void setSrsNamePrefixForSosV1(String prefix) {
        if (!prefix.endsWith(":") && !prefix.isEmpty() && prefix.startsWith("urn")) {
            prefix += ":";
        }
        srsNamePrefix = prefix;
    }

    /**
     * @return prefix URN for the spatial reference system
     */
    /*
     * SosHelper GmlEncoderv321 AbstractKvpDecoder SosEncoderv100
     */
    public String getSrsNamePrefixSosV2() {
        return srsNamePrefixSosV2;
    }

    @Setting(SRS_NAME_PREFIX_SOS_V2)
    public void setSrsNamePrefixForSosV2(String prefix) {
        if (!prefix.endsWith("/") && !prefix.isEmpty() && prefix.startsWith("http")) {
            prefix += "/";
        }
        srsNamePrefixSosV2 = prefix;
    }

    @Setting(ServiceSettings.DEREGISTER_JDBC_DRIVER)
    public void setDeregisterJdbcDriver(final boolean deregisterJdbcDriver) {
        this.deregisterJdbcDriver = deregisterJdbcDriver;
    }

    public boolean isDeregisterJdbcDriver() {
        return deregisterJdbcDriver;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(final String defaultLanguage) {
        Validation.notNullOrEmpty("Default language as three character string", defaultLanguage);
        this.defaultLanguage = new Locale(defaultLanguage);
    }

    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguageValues(final boolean showAllLanguageValues) {
        this.showAllLanguageValues = showAllLanguageValues;
    }

    public boolean isShowAllLanguageValues() {
        return showAllLanguageValues;
    }

    public boolean isSetDefaultLanguage() {
        // TODO Auto-generated method stub
        return false;
    }

    @Setting(MiscSettings.HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES)
    public void setMaxNumberOfReturnedTimeSeries(Integer value) {
        this.maxNumberOfReturnedTimeSeries = value;
    }

    public int getMaxNumberOfReturnedTimeSeries() {
        return maxNumberOfReturnedTimeSeries;
    }

    @Setting(MiscSettings.HYDRO_MAX_NUMBER_OF_RETURNED_VALUES)
    public void setMaxNumberOfReturnedValues(Integer value) {
        this.maxNumberOfReturnedValues = value;
    }

    public int getMaxNumberOfReturnedValues() {
        return maxNumberOfReturnedValues;
    }

    @Setting(MiscSettings.RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST)
    public void setOverallExtrema(boolean overallExtrema) {
        this.overallExtrema  = overallExtrema;
    }

    public boolean isOverallExtrema() {
        return overallExtrema;
    }

    @Setting(StreamingSettings.FORCE_STREAMING_ENCODING)
    public void setForceStreamingEncoding(boolean streamingEncoding) {
        this.streamingEncoding  = streamingEncoding;
    }

    /**
     * @return
     */
    public boolean isForceStreamingEncoding() {
        return streamingEncoding;
    }


    /*
     * Now, we return the list of returned features and not a complex encoded
     * relatedFeature => this setting is not needed at all See
     * AbstractGetFeatureOfInterestDAO:100-195 Don't forget to activate in
     * MiscSettings the relatedFeature setting
     *
     * @Setting(MiscSettings.RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES)
     * public void setRelatedSamplingFeatureRoleForChildFeatures(final String
     * relatedSamplingFeatureRoleForChildFeatures) { Validation.notNullOrEmpty(
     * MiscSettings.RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES,
     * relatedSamplingFeatureRoleForChildFeatures);
     * this.relatedSamplingFeatureRoleForChildFeatures =
     * relatedSamplingFeatureRoleForChildFeatures; }
     *
     * public String getRelatedSamplingFeatureRoleForChildFeatures() { return
     * relatedSamplingFeatureRoleForChildFeatures; }
     */
}

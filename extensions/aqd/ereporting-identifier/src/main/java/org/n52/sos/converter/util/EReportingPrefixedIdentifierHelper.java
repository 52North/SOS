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
package org.n52.sos.converter.util;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.settings.EReportingSetting;
import org.n52.sos.util.StringHelper;

@Configurable
public class EReportingPrefixedIdentifierHelper {

    private static EReportingPrefixedIdentifierHelper instance;

    private String namespacePrefix;

    private String offeringPrefix;

    private String procedurePrefix;

    private String observablePropertyPrefix;

    private String featureOfInterestPrefix;

    private String samplingPointPrefix;

    private String stationPrefix;

    private String networkPrefix;

    /**
     * @return Returns a singleton instance of the
     *         EReportingPrefixedIdentifierHelper.
     */
    public static synchronized EReportingPrefixedIdentifierHelper getInstance() {
        if (instance == null) {
            instance = new EReportingPrefixedIdentifierHelper();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    private EReportingPrefixedIdentifierHelper() {

    }

    /**
     * @return the namespacePrefix
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * @param namespacePrefix
     *            the namespacePrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_NAMESPACE)
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    public boolean isSetNamespacePrefix() {
        return StringHelper.isNotEmpty(getNamespacePrefix());
    }

    /**
     * @return the offeringPrefix
     */
    public String getOfferingPrefix() {
        return offeringPrefix;
    }

    /**
     * @param offeringPrefix
     *            the offeringPrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_OFFERING_PREFIX_KEY)
    public void setOfferingPrefix(String offeringPrefix) {
        this.offeringPrefix = offeringPrefix;
    }

    public boolean isSetOfferingPrefix() {
        return StringHelper.isNotEmpty(getOfferingPrefix());
    }

    /**
     * @return the procedurePrefix
     */
    public String getProcedurePrefix() {
        return procedurePrefix;
    }

    /**
     * @param procedurePrefix
     *            the procedurePrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_PROCEDURE_PREFIX_KEY)
    public void setProcedurePrefix(String procedurePrefix) {
        this.procedurePrefix = procedurePrefix;
    }

    public boolean isSetProcedurePrefix() {
        return StringHelper.isNotEmpty(getProcedurePrefix());
    }

    /**
     * @return the observablePropertyPrefix
     */
    public String getObservablePropertyPrefix() {
        return observablePropertyPrefix;
    }

    // /**
    // * @param observablePropertyPrefix the observablePropertyPrefix to set
    // */
    // @Setting(EReportingSetting.EREPORTING_OBSERVABLE_PROPERTY_PREFIX_KEY)
    // public void setObservablePropertyPrefix(String observablePropertyPrefix)
    // {
    // this.observablePropertyPrefix = observablePropertyPrefix;
    // }

    public boolean isSetObservablePropertyPrefix() {
        return StringHelper.isNotEmpty(getObservablePropertyPrefix());
    }

    /**
     * @return the featureOfInterestPrefix
     */
    public String getFeatureOfInterestPrefix() {
        return featureOfInterestPrefix;
    }

    /**
     * @param featureOfInterestPrefix
     *            the featureOfInterestPrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_FEATURE_OF_INTEREST_PREFIX_KEY)
    public void setFeatureOfInterestPrefix(String featureOfInterestPrefix) {
        this.featureOfInterestPrefix = featureOfInterestPrefix;
    }

    public boolean isSetFeatureOfInterestPrefix() {
        return StringHelper.isNotEmpty(getFeatureOfInterestPrefix());
    }

    /**
     * @return the samplingPointPrefix
     */
    public String getSamplingPointPrefix() {
        return samplingPointPrefix;
    }

    /**
     * @param samplingPointPrefix
     *            the samplingPointPrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_SAMPLING_POINT_PREFIX_KEY)
    public void setSamplingPointPrefix(String samplingPointPrefix) {
        this.samplingPointPrefix = samplingPointPrefix;
    }

    public boolean isSetSamplingPointPrefix() {
        return StringHelper.isNotEmpty(getSamplingPointPrefix());
    }

    /**
     * @return the stationPrefix
     */
    public String getStationPrefix() {
        return stationPrefix;
    }

    /**
     * @param stationPrefix
     *            the stationPrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_STATION_PREFIX_KEY)
    public void setStationPrefix(String stationPrefix) {
        this.stationPrefix = stationPrefix;
    }

    public boolean isSetStationPrefix() {
        return StringHelper.isNotEmpty(getStationPrefix());
    }

    /**
     * @return the networkPrefix
     */
    public String getNetworkPrefix() {
        return networkPrefix;
    }

    /**
     * @param networkPrefix
     *            the networkPrefix to set
     */
    @Setting(EReportingSetting.EREPORTING_NETWORK_PREFIX_KEY)
    public void setNetworkPrefix(String networkPrefix) {
        this.networkPrefix = networkPrefix;
    }

    public boolean isSetNetworkPrefix() {
        return StringHelper.isNotEmpty(getNetworkPrefix());
    }

    public boolean isSetAnyPrefix() {
        return isSetNamespacePrefix() || isSetFeatureOfInterestPrefix() || isSetObservablePropertyPrefix()
                || isSetOfferingPrefix() || isSetProcedurePrefix() || isSetSamplingPointPrefix()
                || isSetStationPrefix() || isSetNetworkPrefix();
    }

}

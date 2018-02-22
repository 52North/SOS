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
package org.n52.sos.converter.util;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.util.EReportingSetting;

import com.google.common.base.Strings;

@Configurable
public class EReportingPrefixedIdentifierHelper implements Constructable {

    @Deprecated
    private static EReportingPrefixedIdentifierHelper instance;

    private String namespacePrefix;

    private String offeringPrefix;

    private String procedurePrefix;

    private String observablePropertyPrefix;

    private String featureOfInterestPrefix;

    private String samplingPointPrefix;

    private String stationPrefix;

    private String networkPrefix;

    @Override
    public void init() {
        EReportingPrefixedIdentifierHelper.instance = this;
    }

    /**
     * @return Returns a singleton instance of the
     *         EReportingPrefixedIdentifierHelper.
     */
    @Deprecated
    public static EReportingPrefixedIdentifierHelper getInstance() {
        return instance;
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
        return !Strings.isNullOrEmpty(getNamespacePrefix());
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
        return !Strings.isNullOrEmpty(getOfferingPrefix());
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
        return !Strings.isNullOrEmpty(getProcedurePrefix());
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
        return !Strings.isNullOrEmpty(getObservablePropertyPrefix());
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
        return !Strings.isNullOrEmpty(getFeatureOfInterestPrefix());
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
        return !Strings.isNullOrEmpty(getSamplingPointPrefix());
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
        return !Strings.isNullOrEmpty(getStationPrefix());
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
        return !Strings.isNullOrEmpty(getNetworkPrefix());
    }

    public boolean isSetAnyPrefix() {
        return isSetNamespacePrefix() || isSetFeatureOfInterestPrefix() || isSetObservablePropertyPrefix()
                || isSetOfferingPrefix() || isSetProcedurePrefix() || isSetSamplingPointPrefix()
                || isSetStationPrefix() || isSetNetworkPrefix();
    }

}

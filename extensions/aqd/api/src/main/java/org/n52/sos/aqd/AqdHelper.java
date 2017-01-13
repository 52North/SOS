/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.aqd;

import java.util.Set;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.settings.EReportingSetting;

import com.google.common.base.Strings;

@Configurable
public class AqdHelper {

    private String namespace;
    private String observationPrefix;
    private Set<Integer> validityFlags;
    private Set<Integer> verificationFlags;

    /**
     * @return the validityFlags
     */
    public Set<Integer> getValidityFlags() {
        return validityFlags;
    }

    /**
     * @param validityFlags the validityFlags to set
     */
    @Setting(EReportingSetting.EREPORTING_VALIDITY_FLAGS)
    public void setValidityFlags(String validityFlags) {
        this.validityFlags = JavaHelper.getIntegerSetFromString(validityFlags);
    }

    public String getEReportingNamespace() {
        return namespace;
    }

    @Setting(EReportingSetting.EREPORTING_NAMESPACE)
    public void setEReportingNamespace(String namespace) throws ConfigurationError {
        this.namespace = namespace;
    }

    public boolean isSetEReportingNamespace() {
        return !Strings.isNullOrEmpty(getEReportingNamespace());
    }

    @Setting(EReportingSetting.EREPORTING_OBSERVATION_PREFIX)
    public void setEReportingObservationPrefix(String observationPrefix) throws ConfigurationError {
        this.observationPrefix = observationPrefix;
    }

    public String getEReportingObservationPrefix() {
        return observationPrefix;
    }

    public boolean isSetEReportingObservationPrefix() {
        return !Strings.isNullOrEmpty(getEReportingObservationPrefix());
    }

    public boolean isSetValidityFlags() {
        return CollectionHelper.isNotEmpty(getValidityFlags());
    }

    /**
     * @return the verificationFlags
     */
    public Set<Integer> getVerificationFlags() {
        return verificationFlags;
    }

    /**
     * @param verificationFlags the verificationFlags to set
     */
    @Setting(EReportingSetting.EREPORTING_VERIFICATION_FLAGS)
    public void setVerificationFlags(String verificationFlags) {
        this.verificationFlags = JavaHelper.getIntegerSetFromString(verificationFlags);
    }

    public boolean isSetVerificationFlags() {
        return CollectionHelper.isNotEmpty(getVerificationFlags());
    }

    public void processObservation(OmObservation observation, TimePeriod timePeriod, TimeInstant resultTime,
                                   FeatureCollection featureCollection, AbstractEReportingHeader eReportingHeader,
                                   int counter) {
        if (observation.isSetPhenomenonTime()) {
            // generate gml:id
            observation.setGmlId(getObservationId(counter));
            // add xlink:href to eReportingHeader.content
            eReportingHeader.addContent((AbstractFeature) new OmObservation()
                    .setIdentifier(new CodeWithAuthority(getObservationXlink(observation.getGmlId()))));
            timePeriod.extendToContain(observation.getPhenomenonTime());
            observation.setResultTime(resultTime);
            featureCollection.addMember(observation);
        }
    }

    public String getObservationXlink(String gmlId) {
        StringBuilder id = new StringBuilder();
        if (isSetEReportingNamespace()) {
            id.append(getEReportingNamespace());
            if (!getEReportingNamespace().endsWith("/")) {
                id.append("/");
            }
        } else {
            id.append("#");
        }
        id.append(gmlId);
        return id.toString();

    }

    public String getObservationId(int counter) {
        return (isSetEReportingObservationPrefix() ? getEReportingObservationPrefix() : "o_")
                .concat(Integer.toString(counter));
    }
}

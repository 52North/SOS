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
package org.n52.sos.inspire.capabilities;

import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireObject;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesResponseLanguage;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesSupportedLanguage;
import org.n52.sos.ogc.ows.OwsExtendedCapabilities;
import org.n52.sos.ogc.sos.SosConstants;

/**
 * Abstract service internal representation of INSPIRE  ExtendedCapabilities.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public abstract class InspireExtendedCapabilities implements OwsExtendedCapabilities,
        InspireExtendedCapabilitiesSupportedLanguage, InspireExtendedCapabilitiesResponseLanguage, InspireObject {

    private String inspireId;

    /* SupportedLanguages 1..1 */
    private InspireSupportedLanguages supportedLanguages;

    /* ResponseLanguage 1..1 */
    private InspireLanguageISO6392B responseLanguage;

    /**
     * Constructor
     * 
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     */
    public InspireExtendedCapabilities(InspireSupportedLanguages supportedLanguages,
            InspireLanguageISO6392B responseLanguage) {
        setSupportedLanguages(supportedLanguages);
        setResponseLanguage(responseLanguage);
    }

    @Override
    public String getService() {
        return SosConstants.SOS;
    }

    @Override
    public String getNamespace() {
        return InspireConstants.NS_INSPIRE_COMMON;
    }

    @Override
    public InspireSupportedLanguages getSupportedLanguages() {
        return supportedLanguages;
    }

    @Override
    public InspireExtendedCapabilities setSupportedLanguages(InspireSupportedLanguages supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
        return this;
    }

    @Override
    public boolean isSetSupportedLanguages() {
        return getSupportedLanguages() != null;
    }

    @Override
    public InspireLanguageISO6392B getResponseLanguage() {
        return responseLanguage;
    }

    @Override
    public InspireExtendedCapabilities setResponseLanguage(InspireLanguageISO6392B responseLanguage) {
        this.responseLanguage = responseLanguage;
        return this;
    }

    @Override
    public boolean isSetResponseLanguage() {
        return getResponseLanguage() != null;
    }

    /**
     * Set the INSPIRE id
     * 
     * @param inspireId
     *            INSPIRE id to set
     * @return this
     */
    public InspireExtendedCapabilities setInspireId(String inspireId) {
        this.inspireId = inspireId;
        return this;
    }

    /**
     * Get the INSPIRE id
     * 
     * @return the INSPIRE id
     */
    public String getInspireId() {
        return inspireId;
    }

}

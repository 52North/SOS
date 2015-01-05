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
import org.n52.sos.util.StringHelper;

@Configurable
public class PrefixedIdentifierHelper {
    
    private static PrefixedIdentifierHelper instance;
    
    private String globalPrefix;
    
    private String offeringPrefix;
    
    private String procedurePrefix;
    
    private String observablePropertyPrefix;
    
    private String featureOfInterestPrefix;
    
    /**
     * @return Returns a singleton instance of the PrefixedIdentifierHelper.
     */
    public static synchronized PrefixedIdentifierHelper getInstance() {
        if (instance == null) {
            instance = new PrefixedIdentifierHelper();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    private PrefixedIdentifierHelper() {

    }

    /**
     * @return the globalPrefix
     */
    public String getGlobalPrefix() {
        return globalPrefix;
    }

    /**
     * @param globalPrefix the globalPrefix to set
     */
    @Setting(PrefixedIdentifierSetting.GLOBAL_PREFIX_KEY)
    public void setGlobalPrefix(String globalPrefix) {
        this.globalPrefix = globalPrefix;
    }
    
    public boolean isSetGlobalPrefix() {
        return StringHelper.isNotEmpty(getGlobalPrefix());
    }

    /**
     * @return the offeringPrefix
     */
    public String getOfferingPrefix() {
        return offeringPrefix;
    }

    /**
     * @param offeringPrefix the offeringPrefix to set
     */
    @Setting(PrefixedIdentifierSetting.OFFERING_PREFIX_KEY)
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
     * @param procedurePrefix the procedurePrefix to set
     */
    @Setting(PrefixedIdentifierSetting.PROCEDURE_PREFIX_KEY)
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

    /**
     * @param observablePropertyPrefix the observablePropertyPrefix to set
     */
    @Setting(PrefixedIdentifierSetting.OBSERVABLE_PROPERTY_PREFIX_KEY)
    public void setObservablePropertyPrefix(String observablePropertyPrefix) {
        this.observablePropertyPrefix = observablePropertyPrefix;
    }

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
     * @param featureOfInterestPrefix the featureOfInterestPrefix to set
     */
    @Setting(PrefixedIdentifierSetting.FEATURE_OF_INTEREST_PREFIX_KEY)
    public void setFeatureOfInterestPrefix(String featureOfInterestPrefix) {
        this.featureOfInterestPrefix = featureOfInterestPrefix;
    }
    
    public boolean isSetFeatureOfInterestPrefix() {
        return StringHelper.isNotEmpty(getFeatureOfInterestPrefix());
    }

    public boolean isSetAnyPrefix() {
        return isSetGlobalPrefix() && isSetFeatureOfInterestPrefix() && isSetObservablePropertyPrefix() && isSetOfferingPrefix() && isSetProcedurePrefix();
    }

}

/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.om.quality;

/**
 * class which represents a simple quantitative data quality element
 * 
 * @since 4.0.0
 */
public class SosQuality {

    /** name of the result value */
    private String resultName;

    /** unit of the result value */
    private String resultUnit;

    /** value of the quality result */
    private String resultValue;

    /** type of the quality object */
    private QualityType qualityType;

    /**
     * constructor
     * 
     * @param resultName
     *            Result name
     * @param resultUnit
     *            Result unit
     * @param resultValue
     *            Result value
     * @param qualityType
     *            Quality type
     */
    public SosQuality(String resultName, String resultUnit, String resultValue, QualityType qualityType) {
        this.resultName = resultName;
        this.resultUnit = resultUnit;
        this.resultValue = resultValue;
        this.qualityType = qualityType;
    }

    /**
     * Get value
     * 
     * @return the resultValue
     */
    public String getResultValue() {
        return resultValue;
    }

    /**
     * Set value
     * 
     * @param resultValue
     *            the resultValue to set
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    /**
     * Get name
     * 
     * @return the resultName
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * Set name
     * 
     * @param resultName
     *            the resultName to set
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     * Get unit
     * 
     * @return the resultValueUnit
     */
    public String getResultUnit() {
        return resultUnit;
    }

    /**
     * Set unit
     * 
     * @param resultValueUnit
     *            the resultValueUnit to set
     */
    public void setResultUnit(String resultValueUnit) {
        this.resultUnit = resultValueUnit;
    }

    /**
     * Get quality type
     * 
     * @return the qualityType
     */
    public QualityType getQualityType() {
        return qualityType;
    }

    /**
     * Set quality type
     * 
     * @param qualityType
     *            the qualityType to set
     */
    public void setQualityType(QualityType qualityType) {
        this.qualityType = qualityType;
    }

    /**
     * quality type
     * 
     * @since 4.0.0
     */
    public enum QualityType {
        quantity, category, text
    }

}

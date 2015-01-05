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
package org.n52.sos.ds.hibernate.entities.ereporting;

import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.HasPrimaryObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.EReportingQualityData;
import org.n52.sos.util.StringHelper;

public class EReportingQuality implements HasPrimaryObservation, EReportingQualityData {
    
    public static final String YEAR = "year";
    
    public static final String SERIES = "series";
    
    public static final String PRIMARY_OBSERVATION = "primaryObservation";
    
    private long id;
    
    private int year;
    
    private long series;
    
    private String primaryObservation;
    
    private Boolean timeCoverageFlag;

    private Boolean dataCaptureFlag;
    
    private Double uncertaintyEstimation; 
    
    

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the series
     */
    public long getSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(long series) {
        this.series = series;
    }

    @Override
    public String getPrimaryObservation() {
        return primaryObservation;
    }
    
    @Override
    public EReportingQuality setPrimaryObservation(String primaryObservation) {
        this.primaryObservation = primaryObservation;
        return this;
    }

    @Override
    public boolean isSetPrimaryObservation() {
        return StringHelper.isNotEmpty(getPrimaryObservation());
    }

    @Override
    public Boolean getDataCaptureFlag() {
        return this.dataCaptureFlag;
    }

    @Override
    public void setDataCaptureFlag(Boolean dataCaptureFlag) {
        this.dataCaptureFlag = dataCaptureFlag;
    }

    @Override
    public boolean isSetDataCaptureFlag() {
        return this.dataCaptureFlag != null;
    }
    
    @Override
    public Boolean getTimeCoverageFlag() {
        return this.timeCoverageFlag;
    }

    @Override
    public void setTimeCoverageFlag(Boolean timeCoverageFlag) {
        this.timeCoverageFlag = timeCoverageFlag;
    }

    @Override
    public boolean isSetTimeCoverageFlag() {
        return this.timeCoverageFlag != null;
    }

    @Override
    public Double getUncertaintyEstimation() {
        return this.uncertaintyEstimation;
    }

    @Override
    public void setUncertaintyEstimation(Double uncertaintyEstimation) {
        this.uncertaintyEstimation = uncertaintyEstimation;
    }

    @Override
    public boolean isSetUncertaintyEstimation() {
        return this.uncertaintyEstimation != null;
    }

}

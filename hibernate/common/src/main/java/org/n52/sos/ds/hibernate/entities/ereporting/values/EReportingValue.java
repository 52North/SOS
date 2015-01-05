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
package org.n52.sos.ds.hibernate.entities.ereporting.values;

import org.hibernate.Session;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingQualityDAO;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingQuality;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.EReportingValues;
import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.HasEReportingSeries;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.ds.hibernate.util.observation.EReportingHelper;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.StringHelper;

public abstract class EReportingValue extends SeriesValue implements EReportingValues {

    private static final long serialVersionUID = 996063222630981539L;

    private Integer validation = EReportingValues.DEFAULT_VALIDATION;

    private Integer verification = EReportingValues.DEFAULT_VERIFICATION;

    private String primaryObservation = EReportingValues.DEFAULT_PRIMARY_OBSERVATION;

    private Boolean timeCoverageFlag;

    private Boolean dataCaptureFlag;

    private Double dataCapture;

    private Double uncertaintyEstimation;

    public EReportingSeries getEReportingSeries() {
        if (hasEReportingSeries()) {
            return (EReportingSeries) getSeries();
        }
        return null;
    }

    @Override
    public HasEReportingSeries setEReportingSeries(EReportingSeries series) {
        setSeries(series);
        return this;
    }

    @Override
    public boolean hasEReportingSeries() {
        return getSeries() != null && getSeries() instanceof EReportingSeries;
    }

    @Override
    public Integer getVerification() {
        return verification;
    }

    @Override
    public EReportingValue setVerification(Integer verification) {
        this.verification = verification;
        return this;
    }

    @Override
    public boolean isSetVerification() {
        return getVerification() != null;
    }

    @Override
    public Integer getValidation() {
        return validation;
    }

    @Override
    public EReportingValue setValidation(Integer validation) {
        this.validation = validation;
        return this;
    }

    @Override
    public boolean isSetValidation() {
        return getValidation() != null;
    }

    @Override
    public String getPrimaryObservation() {
        return primaryObservation;
    }

    @Override
    public EReportingValue setPrimaryObservation(String primaryObservation) {
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
    public Double getDataCapture() {
        return this.dataCapture;
    }

    @Override
    public EReportingValue setDataCapture(Double dataCapture) {
        this.dataCapture = dataCapture;
        return this;
    }

    @Override
    public boolean isSetDataCapture() {
        return this.dataCapture != null;
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

    @Override
    public OmObservation mergeValueToObservation(OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat)) {
            if (!observation.isSetValue()) {
                addValuesToObservation(observation, responseFormat);
            } else {
                checkTime(observation);
                EReportingHelper.mergeValues((SweDataArray) observation.getValue().getValue().getValue(),
                        EReportingHelper.createSweDataArray(observation, this));
            }
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observation.getObservationConstellation()
                    .getObservationType())) {
                observation.getObservationConstellation().setObservationType(
                        OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }

        } else {
            super.mergeValueToObservation(observation, responseFormat);
        }
        return observation;
    }

    private void checkTime(OmObservation observation) {
        if (observation.isSetValue()) {
            Time obsPhenTime = observation.getValue().getPhenomenonTime();
            Time valuePhenTime = createPhenomenonTime();
            if (obsPhenTime != null) {
                TimePeriod timePeriod = null;
                if (obsPhenTime instanceof TimePeriod) {
                    timePeriod = (TimePeriod) obsPhenTime;
                } else {
                    timePeriod = new TimePeriod();
                    timePeriod.extendToContain(obsPhenTime);
                }
                timePeriod.extendToContain(valuePhenTime);
                observation.getValue().setPhenomenonTime(timePeriod);
            } else {
                observation.getValue().setPhenomenonTime(valuePhenTime);
            }
        }
        TimeInstant rt = createResutlTime(getResultTime());
        if (observation.getResultTime().getValue().isBefore(rt.getValue())) {
            observation.setResultTime(rt);
        }
        if (isSetValidTime()) {
            TimePeriod vt = createValidTime(getValidTimeStart(), getValidTimeEnd());
            if (observation.isSetValidTime()) {
                observation.getValidTime().extendToContain(vt);
            } else {
                observation.setValidTime(vt);
            }
        }
    }

    @Override
    protected void addValueSpecificDataToObservation(OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        // nothing to do
    }

    @Override
    public void addValueSpecificDataToObservation(OmObservation observation, Session session, SwesExtensions extensions)
            throws OwsExceptionReport {
        if (AqdHelper.getInstance().hasFlowExtension(extensions)) {
            ReportObligationType flow = AqdHelper.getInstance().getFlow(extensions);
            if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                int year = DateTimeHelper.makeDateTime(getPhenomenonTimeStart()).getYear();
                EReportingQuality eReportingQuality =
                        new EReportingQualityDAO().getEReportingQuality(getSeries().getSeriesId(), year,
                                getPrimaryObservation(), session);
                if (eReportingQuality != null) {
                    observation.setResultQuality(EReportingHelper.getGmdDomainConsistency(eReportingQuality, true));
                } else {
                    observation.setResultQuality(EReportingHelper.getGmdDomainConsistency(new EReportingQuality(), true));
                }
            }
        }
    }

    @Override
    protected void addObservationValueToObservation(OmObservation observation, Value<?> value, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat)) {
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observation.getObservationConstellation()
                    .getObservationType())) {
                observation.getObservationConstellation().setObservationType(
                        OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }
            observation.setValue(EReportingHelper.createSweDataArrayValue(observation, this));
        } else {
            super.addObservationValueToObservation(observation, value, responseFormat);
        }

    }

    private boolean checkResponseFormat(String responseFormat) {
        return AqdConstants.NS_AQD.equals(responseFormat);
    }

    @Override
    public String getDiscriminator() {
        return getPrimaryObservation();
    }

}

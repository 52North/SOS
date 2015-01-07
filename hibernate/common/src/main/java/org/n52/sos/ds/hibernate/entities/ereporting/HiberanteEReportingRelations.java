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

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.ds.hibernate.entities.series.HibernateSeriesRelations;

public interface HiberanteEReportingRelations extends HibernateSeriesRelations {

    interface HasEReportingSamplingPoint {

        String SAMPLING_POINT = "samplingPoint";

        EReportingSamplingPoint getSamplingPoint();

        HasEReportingSamplingPoint setSamplingPoint(EReportingSamplingPoint samplingPoint);

        boolean hasSamplingPoint();
    }

    interface HasInspireId {

        String INSPIRE_ID = "inspireId";

        String getInspireId();

        HasInspireId setInspireId(String inspireId);

        boolean isSetInspireId();

    }

    interface HasEReportingSeries extends HasSeries {

        EReportingSeries getEReportingSeries();

        HasEReportingSeries setEReportingSeries(EReportingSeries series);

        boolean hasEReportingSeries();
    }

    interface HasValidation {

        Integer DEFAULT_VALIDATION = -1;

        String VALIDATION = "validation";

        Integer getValidation();

        HasValidation setValidation(Integer validation);

        boolean isSetValidation();
    }

    interface HasVerification {

        Integer DEFAULT_VERIFICATION = 3;

        String VERIFICATION = "verification";

        Integer getVerification();

        HasVerification setVerification(Integer verification);

        boolean isSetVerification();
    }

    interface HasPrimaryObservation {

        String DEFAULT_PRIMARY_OBSERVATION = AqdConstants.VAR;

        String PRIMARY_OBSERVATION = "primaryObservation";

        String getPrimaryObservation();

        HasPrimaryObservation setPrimaryObservation(String primaryObservation);

        boolean isSetPrimaryObservation();
    }

    interface HasTimeCoverageFlag {
        String TIME_COVERAGE_FLAG = "timeCoverageFlag";

        Boolean getTimeCoverageFlag();

        void setTimeCoverageFlag(Boolean timeCoverageFlag);

        boolean isSetTimeCoverageFlag();
    }

    interface HasDataCaptureFlag {
        String DATA_CAPTURE_FLAG = "dataCaptureFlag";

        Boolean getDataCaptureFlag();

        void setDataCaptureFlag(Boolean dataCaptureFlag);

        boolean isSetDataCaptureFlag();
    }

    interface HasDataCapture {

        String DATA_CAPTURE = "dataCapture";

        Double getDataCapture();

        HasDataCapture setDataCapture(Double dataCapture);

        boolean isSetDataCapture();
    }

    interface HasUncertaintyEstimation {
        String UNCERTAINTY_ESTIMATION = "uncertaintyEstimation";

        Double getUncertaintyEstimation();

        void setUncertaintyEstimation(Double uncertaintyEstimation);

        boolean isSetUncertaintyEstimation();
    }

    interface EReportingQualityData extends HasTimeCoverageFlag, HasDataCaptureFlag, HasUncertaintyEstimation {

    }

    interface EReportingValues extends EReportingValuesTime, HasDataCapture, EReportingQualityData, HasUnit,
            GetStringValue {

        String getPrimaryObservation();

    }

    interface EReportingValuesTime extends HasEReportingSeries, HasValidation, HasVerification, HasPrimaryObservation {

        String getPrimaryObservation();

    }

    interface HasAssessmentType {

        String ASSESSMENTTYPE = "assessmentType";

        /**
         * @return the assessmentType
         */
        EReportingAssessmentType getAssessmentType();

        /**
         * @param assessmentType
         *            the assessmentType to set
         */
        HasAssessmentType setAssessmentType(EReportingAssessmentType assessmentType);

        boolean isSetAssessmentType();

    }

    /**
     * Interface for AQD EReporting Station elements
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.3.0
     *
     */
    interface HasStation {
        String STATION = "station";

        /**
         * @return the station
         */
        EReportingStation getStation();

        /**
         * @param station
         *            the station to set
         * @return this
         */
        HasStation setStation(EReportingStation station);

        /**
         * @return <code>true</code>, if station is not null
         */
        boolean isSetStation();
    }

    /**
     * Interface for AQD EReporting Network elements
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.3.0
     *
     */
    interface HasNetwork {
        String NETWORK = "network";

        /**
         * @return the network
         */
        EReportingNetwork getNetwork();

        /**
         * @param network
         *            the network to set
         * @return this
         */
        HasNetwork setNetwork(EReportingNetwork network);

        /**
         * @return <code>true</code>, if network is not null
         */
        boolean isSetNetwork();
    }
}

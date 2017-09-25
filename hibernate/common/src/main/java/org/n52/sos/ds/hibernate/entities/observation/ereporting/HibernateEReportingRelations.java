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
package org.n52.sos.ds.hibernate.entities.observation.ereporting;

import org.n52.shetland.aqd.AqdConstants;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.GetStringValue;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingAssessmentType;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingNetwork;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingStation;
import org.n52.sos.ds.hibernate.entities.observation.series.HibernateSeriesRelations;

import com.google.common.base.Strings;

public interface HibernateEReportingRelations {

    interface HasEReportingSamplingPoint {
        String SAMPLING_POINT = "samplingPoint";

        EReportingSamplingPoint getSamplingPoint();

        void setSamplingPoint(EReportingSamplingPoint samplingPoint);

        default boolean hasSamplingPoint() {
            return getSamplingPoint() != null;
        }
    }

    interface HasInspireId {
        String INSPIRE_ID = "inspireId";

        String getInspireId();

        void setInspireId(String inspireId);

        default boolean isSetInspireId() {
            return getInspireId() != null;
        }
    }

    interface HasEReportingSeries extends HibernateSeriesRelations.HasSeries {
        EReportingSeries getEReportingSeries();

        void setEReportingSeries(EReportingSeries series);

        default boolean hasEReportingSeries() {
            return getSeries() instanceof EReportingSeries;
        }
    }

    interface HasValidation {

        Integer DEFAULT_VALIDATION = -1;

        String VALIDATION = "validation";

        Integer getValidation();

        void setValidation(Integer validation);

        default boolean isSetValidation() {
            return getValidation() != null;
        }
    }

    interface HasVerification {

        Integer DEFAULT_VERIFICATION = 3;
        String VERIFICATION = "verification";

        Integer getVerification();

        void setVerification(Integer verification);

        default boolean isSetVerification() {
            return getVerification() != null;
        }
    }

    interface HasPrimaryObservation {

        String DEFAULT_PRIMARY_OBSERVATION = AqdConstants.VAR;

        String PRIMARY_OBSERVATION = "primaryObservation";

        String getPrimaryObservation();

        void setPrimaryObservation(String primaryObservation);

        default boolean isSetPrimaryObservation() {
            return !Strings.isNullOrEmpty(getPrimaryObservation());
        }
    }

    interface HasTimeCoverageFlag {
        String TIME_COVERAGE_FLAG = "timeCoverageFlag";

        Boolean getTimeCoverageFlag();

        void setTimeCoverageFlag(Boolean timeCoverageFlag);

        default boolean isSetTimeCoverageFlag() {
            return getTimeCoverageFlag() != null;
        }
    }

    interface HasDataCaptureFlag {
        String DATA_CAPTURE_FLAG = "dataCaptureFlag";

        Boolean getDataCaptureFlag();

        void setDataCaptureFlag(Boolean dataCaptureFlag);

        default boolean isSetDataCaptureFlag() {
            return getDataCaptureFlag() != null;
        }
    }

    interface HasDataCapture {

        String DATA_CAPTURE = "dataCapture";

        Double getDataCapture();

        void setDataCapture(Double dataCapture);

        default boolean isSetDataCapture() {
            return getDataCapture() != null;
        }
    }

    interface HasUncertaintyEstimation {
        String UNCERTAINTY_ESTIMATION = "uncertaintyEstimation";

        Double getUncertaintyEstimation();

        void setUncertaintyEstimation(Double uncertaintyEstimation);

        default boolean isSetUncertaintyEstimation() {
            return getUncertaintyEstimation() != null;
        }
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
         * @param assessmentType the assessmentType to set
         */
        void setAssessmentType(EReportingAssessmentType assessmentType);

        default boolean isSetAssessmentType() {
            return getAssessmentType() != null && !getAssessmentType().isSetAssessmentType();
        }
    }

    /**
     * Interface for AQD EReporting Station elements
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
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
         * @param station the station to set
         */
        void setStation(EReportingStation station);

        /**
         * @return <code>true</code>, if station is not null
         */
        default boolean isSetStation() {
            return getStation() != null;
        }
    }

    /**
     * Interface for AQD EReporting Network elements
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
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
         * @param network the network to set
         */
        void setNetwork(EReportingNetwork network);

        /**
         * @return <code>true</code>, if network is not null
         */
        default boolean isSetNetwork() {
            return getNetwork() != null;
        }
    }
}

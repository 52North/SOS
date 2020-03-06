/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * SOS InsertObservation request
 * 
 * @since 4.0.0
 */
public class InsertObservationRequest extends AbstractServiceRequest<InsertObservationResponse> {

    /**
     * Assigned sensor id
     */
    private String assignedSensorId;

    private List<String> offerings;

    /**
     * SOS observation collection with observations to insert
     */
    private List<OmObservation> observations;

    private ReferenceChecker referenceChecker = new ReferenceChecker();

    public InsertObservationRequest() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.InsertObservation.name();
    }

    /**
     * Get assigned sensor id
     * 
     * @return assigned sensor id
     */
    public String getAssignedSensorId() {
        return assignedSensorId;
    }

    /**
     * Set assigned sensor id
     * 
     * @param assignedSensorId
     *            assigned sensor id
     */
    public void setAssignedSensorId(String assignedSensorId) {
        this.assignedSensorId = assignedSensorId;
    }

    public boolean isSetAssignedSensorId() {
        return StringHelper.isNotEmpty(getAssignedSensorId());
    }

    /**
     * Get observations to insert
     * 
     * @return observations to insert
     */
    public List<OmObservation> getObservations() {
        return observations;
    }

    /**
     * Set observations to insert
     * 
     * @param observation
     *            observations to insert
     * @throws OwsExceptionReport
     */
    public InsertObservationRequest setObservation(List<OmObservation> observation) throws OwsExceptionReport {
        observations = referenceChecker.checkObservationsForReferences(observation);
        return this;
    }

    /**
     * Add observation to insert
     * 
     * @param observation
     *            observation to add
     * @throws OwsExceptionReport
     */
    public void addObservation(OmObservation observation) throws OwsExceptionReport {
        if (observation != null) {
            if (observations == null) {
                observations = new LinkedList<OmObservation>();
            }
            observations.add(referenceChecker.checkObservationForReferences(observation));
        }
    }

    public boolean isSetObservation() {
        return CollectionHelper.isNotEmpty(getObservations());
    }

    public InsertObservationRequest setOfferings(List<String> offerings) {
        this.offerings = offerings;
        return this;
    }

    public List<String> getOfferings() {
        return offerings;
    }

    public boolean isSetOfferings() {
        return CollectionHelper.isNotEmpty(getOfferings());
    }

    @Override
    public InsertObservationResponse getResponse() throws OwsExceptionReport {
        return (InsertObservationResponse) new InsertObservationResponse().set(this);
    }

    public boolean isSetExtensionSplitDataArrayIntoObservations() {
        return isSetExtensions() && getExtensions()
                .isBooleanExtensionSet(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
    }

    /**
     * Checks if an observation contains referenced elements. Checked elements
     * are phenomenonTime, resultTime and featureOfInterest.
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.3.7
     *
     */
    private class ReferenceChecker {
        final Map<String, Time> phenomenonTimes = new HashMap<String, Time>();

        final Map<String, TimeInstant> resultTimes = new HashMap<String, TimeInstant>();

        final Map<String, AbstractFeature> features = new HashMap<String, AbstractFeature>();

        /**
         * Check observations for references
         * 
         * @param observations
         *            {@link OmObservation}s to check
         * @return Checked observations
         * @throws OwsExceptionReport
         *             If an error occurs
         */
        public List<OmObservation> checkObservationsForReferences(final List<OmObservation> observations)
                throws OwsExceptionReport {
            if (CollectionHelper.isNotEmpty(observations)) {
                for (OmObservation observation : observations) {
                    checkObservationForReferences(observation);
                }
            }
            return observations;
        }

        /**
         * Check observation for references
         * 
         * @param observation
         *            {@link OmObservation} to check
         * @return Checked observation
         * @throws OwsExceptionReport
         *             If an error occurs
         */
        public OmObservation checkObservationForReferences(OmObservation observation) throws OwsExceptionReport {
            if (observation != null) {
                checkAndAddPhenomenonTime(observation.getPhenomenonTime(), phenomenonTimes);
                checkAndAddResultTime(observation.getResultTime(), resultTimes);
                checkAndAddFeatures(observation.getObservationConstellation().getFeatureOfInterest(), features);
                checkReferencedElements(observation, phenomenonTimes, resultTimes, features);
            }
            return observation;
        }

        private void checkAndAddPhenomenonTime(final Time phenomenonTime, final Map<String, Time> phenomenonTimes) {
            if (phenomenonTime != null && !phenomenonTime.isReferenced()) {
                phenomenonTimes.put(phenomenonTime.getGmlId(), phenomenonTime);
            }
        }

        private void checkAndAddResultTime(final TimeInstant resultTime, final Map<String, TimeInstant> resultTimes) {
            if (resultTime != null && !resultTime.isReferenced()) {
                resultTimes.put(resultTime.getGmlId(), resultTime);
            }
        }

        private void checkAndAddFeatures(final AbstractFeature featureOfInterest,
                final Map<String, AbstractFeature> features) {
            if (featureOfInterest != null && !featureOfInterest.isReferenced()) {
                features.put(featureOfInterest.getGmlId(), featureOfInterest);
            }
        }

        private void checkReferencedElements(final OmObservation observation, final Map<String, Time> phenomenonTimes,
                final Map<String, TimeInstant> resultTimes, final Map<String, AbstractFeature> features)
                throws OwsExceptionReport {
            // phenomenonTime
            final Time phenomenonTime = observation.getPhenomenonTime();
            if (phenomenonTime != null && phenomenonTime.isReferenced()) {
                observation.getValue().setPhenomenonTime(phenomenonTimes.get(phenomenonTime.getGmlId()));
            }
            // resultTime
            final TimeInstant resultTime = observation.getResultTime();
            if (resultTime != null && resultTime.isReferenced()) {
                if (resultTimes.containsKey(resultTime.getGmlId())) {
                    observation.setResultTime(resultTimes.get(resultTime.getGmlId()));
                } else if (phenomenonTimes.containsKey(resultTime.getGmlId())) {
                    final Time iTime = phenomenonTimes.get(resultTime.getGmlId());
                    if (iTime instanceof TimeInstant) {
                        observation.setResultTime((TimeInstant) iTime);
                    } else if (iTime instanceof TimePeriod) {
                        final TimePeriod timePeriod = (TimePeriod) iTime;
                        observation.setResultTime(new TimeInstant(timePeriod.getEnd()));
                    } else {
                        throw new InvalidParameterValueException().at("observation.resultTime")
                                .withMessage("The time value type is not supported");
                    }

                }
            }
            // featureOfInterest
            final AbstractFeature featureOfInterest = observation.getObservationConstellation().getFeatureOfInterest();
            if (featureOfInterest != null && featureOfInterest.isReferenced()) {
                observation.getObservationConstellation()
                        .setFeatureOfInterest(features.get(featureOfInterest.getGmlId()));
            }
        }
    }
}

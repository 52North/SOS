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
package org.n52.sos.request;

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
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
     */
    public void setObservation(List<OmObservation> observation) {
        this.observations = observation;
    }

    public void addObservation(OmObservation observation) {
        if (observations == null) {
            observations = new LinkedList<OmObservation>();
        }
        observations.add(observation);
    }

    public boolean isSetObservation() {
        return CollectionHelper.isNotEmpty(getObservations());
    }

    public void setOfferings(List<String> offerings) {
        this.offerings = offerings;
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
}

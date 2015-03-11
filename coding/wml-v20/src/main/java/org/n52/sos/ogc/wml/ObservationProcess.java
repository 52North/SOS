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
package org.n52.sos.ogc.wml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;

/**
 * @since 4.0.0
 * 
 */
public class ObservationProcess extends SosProcedureDescription {

    private static final long serialVersionUID = -2211664623972369575L;
    
    /*
     * Multiplicity: 1 A defintion of the type of process used in the
     * observation. This may be a Sensor, ManualMethod, Algorithm or Simulation
     * (including models).
     */
    private ReferenceType processType;

    /*
     * Multiplicity: 0..1 A reference to the original source of the data. For
     * example, if this is a post-processed time series (and processType is
     * algorithm), this link would specify the original process that generated
     * the data, e.g. the sensor. This allows the origin of the data to be
     * maintained regardless of the processing that has occured to it.
     */
    private ReferenceType originatingProcess;

    /*
     * Multiplicity: 0..1 If the process involves temporal aggregation of a
     * result set, the time duration over which data has been aggregated should
     * be expressed here. E.g. hourly, daily aggregates.
     */
    private String aggregationDuration;

    /*
     * Multiplicity: 0..1 Reference to an external process definition
     */
    private ReferenceType processReference;

    /*
     * Multiplicity: 0..1 Specifies the datum that is used as the zero point for
     * level measurements. This can be process-specific as opposed the gauge at
     * the actual monitoring point.
     */
    private ReferenceType verticalDatum;

    /*
     * Multiplicity: 0..* A list of the inputs used in the process. This may be
     * a list of references to the data sets used (e.g. model input series) or a
     * input array to an algorithm.
     */
    private final List<ReferenceType> inputs = new ArrayList<ReferenceType>(0);

    /*
     * Multiplicity: 0..* Comments specific to the process from the operator or
     * system performing the process.
     */
    private List<String> comments = new ArrayList<String>(0);

    /*
     * Multiplicity: 0..* A defintion of the type of process used in the
     * observation. This may be a Sensor, ManualMethod, Algorithm or Simulation
     * (including models).
     */
    private final List<NamedValue<?>> parameters = new ArrayList<NamedValue<?>>(0);

    private Set<SosOffering> offeringIdentifiers;

    @Override
    public Set<SosOffering> getOfferings() {
        return offeringIdentifiers;
    }

    public ObservationProcess setOfferingIdentifiers(final Set<SosOffering> offeringIdentifiers) {
        this.offeringIdentifiers = offeringIdentifiers;
        return this;
    }

    @Override
    public boolean isSetOfferings() {
        return offeringIdentifiers != null && !offeringIdentifiers.isEmpty();
    }

    @Override
    public ObservationProcess setIdentifier(final String procedureIdentifier) {
        super.setIdentifier(procedureIdentifier);
        return this;
    }

    public ReferenceType getProcessType() {
        return processType;
    }

    public ObservationProcess setProcessType(final ReferenceType processType) {
        this.processType = processType;
        return this;
    }

    public ReferenceType getOriginatingProcess() {
        return originatingProcess;
    }

    public ObservationProcess setOriginatingProcess(final ReferenceType originatingProcess) {
        this.originatingProcess = originatingProcess;
        return this;
    }

    public String getAggregationDuration() {
        return aggregationDuration;
    }

    public ObservationProcess setAggregationDuration(final String aggregationDuration) {
        this.aggregationDuration = aggregationDuration;
        return this;
    }

    public ReferenceType getProcessReference() {
        return processReference;
    }

    public ObservationProcess setProcessReference(final ReferenceType processReference) {
        this.processReference = processReference;
        return this;
    }

    public ReferenceType getVerticalDatum() {
        return verticalDatum;
    }

    public ObservationProcess setVerticalDatum(final ReferenceType verticalDatum) {
        this.verticalDatum = verticalDatum;
        return this;
    }

    public List<ReferenceType> getInputs() {
        return inputs;
    }

    public ObservationProcess setInputs(final List<ReferenceType> input) {
        inputs.addAll(input);
        return this;
    }

    public ObservationProcess addInputs(final ReferenceType input) {
        inputs.add(input);
        return this;
    }

    public List<String> getComments() {
        return comments;
    }

    public ObservationProcess setComments(final List<String> comments) {
        this.comments = comments;
        return this;
    }

    public ObservationProcess addComment(final String comment) {
        comments.add(comment);
        return this;
    }

    public List<NamedValue<?>> getParameters() {
        return parameters;
    }

    public ObservationProcess setParameters(final List<NamedValue<?>> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public ObservationProcess addParameter(final NamedValue<?> parameter) {
        parameters.add(parameter);
        return this;
    }

    public boolean isSetProcessType() {
        return processType != null;
    }

    public boolean isSetOriginatingProcess() {
        return originatingProcess != null && originatingProcess.hasValues();
    }

    public boolean isSetProcessReference() {
        return processReference != null && processReference.hasValues();
    }

    public boolean isSetAggregationDuration() {
        return aggregationDuration != null && !aggregationDuration.isEmpty();
    }

    public boolean isSetVerticalDatum() {
        return verticalDatum != null && verticalDatum.hasValues();
    }

    public boolean isSetInputs() {
        return inputs != null && !inputs.isEmpty();
    }

    public boolean isSetComments() {
        return comments != null && !comments.isEmpty();
    }

    public boolean isSetParameters() {
        return parameters != null && !parameters.isEmpty();
    }

}

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
package org.n52.sos.ogc.om;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.Constants;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 */
public class OmObservationConstellation implements Serializable, Cloneable {
    private static final long serialVersionUID = 8758412729768944974L;

    /** Identifier of the procedure by which the observation is made */
    private SosProcedureDescription procedure;

    /** Identifier of the observableProperty to which the observation accords to */
    private AbstractPhenomenon observableProperty;

    /** Identifiers of the offerings to which this observation belongs */
    private Set<String> offerings;

    /** Identifier of the featureOfInterest to which this observation belongs */
    private AbstractFeature featureOfInterest;

    /** type of the observation */
    private String observationType;

    // private SosResultTemplate sosResultTemplate;

    /**
     * default constructor
     */
    public OmObservationConstellation() {
        super();
    }

    /**
     * constructor
     * 
     * @param procedure
     *            Procedure by which the observation is made
     * @param observableProperty
     *            observableProperty to which the observation accords to
     * @param featureOfInterest
     *            featureOfInterest to which this observation belongs
     */
    public OmObservationConstellation(SosProcedureDescription procedure, AbstractPhenomenon observableProperty,
            AbstractFeature featureOfInterest) {
        super();
        this.procedure = procedure;
        this.observableProperty = observableProperty;
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * constructor
     * 
     * @param procedure
     *            Procedure by which the observation is made
     * @param observableProperty
     *            observableProperty to which the observation accords to
     * @param offerings
     *            offering to which this observation belongs
     * @param featureOfInterest
     *            featureOfInterest to which this observation belongs
     * @param observationType
     *            Observation type
     */
    public OmObservationConstellation(SosProcedureDescription procedure, AbstractPhenomenon observableProperty,
            Set<String> offerings, AbstractFeature featureOfInterest, String observationType) {
        super();
        this.procedure = procedure;
        this.observableProperty = observableProperty;
        this.offerings = offerings;
        this.featureOfInterest = featureOfInterest;
        this.observationType = observationType;
    }

    /**
     * Get the procedure
     * 
     * @return the procedure
     */
    public SosProcedureDescription getProcedure() {
        return procedure;
    }

    /**
     * Set the procedure
     * 
     * @param procedure
     *            the procedure to set
     * @return this
     */
    public OmObservationConstellation setProcedure(SosProcedureDescription procedure) {
        this.procedure = procedure;
        return this;
    }

    /**
     * Get observableProperty
     * 
     * @return the observableProperty
     */
    public AbstractPhenomenon getObservableProperty() {
        return observableProperty;
    }

    /**
     * Set observableProperty
     * 
     * @param observableProperty
     *            the observableProperty to set
     * @return this
     */
    public OmObservationConstellation setObservableProperty(AbstractPhenomenon observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    /**
     * Get offering
     * 
     * @return the offering
     */
    public Set<String> getOfferings() {
        return offerings;
    }

    /**
     * Set offering
     * 
     * @param offerings
     *            the offering to set
     * @return this
     */
    public OmObservationConstellation setOfferings(Set<String> offerings) {
        this.offerings = offerings;
        return this;
    }

    public OmObservationConstellation setOfferings(List<String> offerings) {
        if (this.offerings == null) {
            this.offerings = new HashSet<String>(0);
        }
        this.offerings.addAll(offerings);
        return this;
    }

    public OmObservationConstellation addOffering(String offering) {
        if (offerings == null) {
            offerings = new HashSet<String>(0);
        }
        offerings.add(offering);
        return this;
    }

    /**
     * Get featureOfInterest
     * 
     * @return the featureOfInterest
     */
    public AbstractFeature getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * Set featureOfInterest
     * 
     * @param featureOfInterest
     *            the featureOfInterest to set
     * @return this
     */
    public OmObservationConstellation setFeatureOfInterest(AbstractFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    /**
     * Get observation type
     * 
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * Set observation type
     * 
     * @param observationType
     *            the observationType to set
     * @return this
     */
    public OmObservationConstellation setObservationType(String observationType) {
        this.observationType = observationType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OmObservationConstellation) {
            return hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.procedure, Constants.HASH_CODE_19,this.observableProperty,
                                this.offerings, Constants.HASH_CODE_43,this.featureOfInterest);
    }

    /**
     * Check if constellations are equal excluding observableProperty
     * 
     * @param toCheckObsConst
     *            Observation constellation to chek
     * @return true if equals
     */
    public boolean equalsExcludingObsProp(OmObservationConstellation toCheckObsConst) {
        return (procedure.equals(toCheckObsConst.getProcedure())
                && featureOfInterest.equals(toCheckObsConst.getFeatureOfInterest())
                && observationType.equals(toCheckObsConst.getObservationType()) && checkObservationTypeForMerging());

    }

    private boolean checkObservationTypeForMerging() {
        return (!observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT)
                && !observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION) && !observationType
                    .equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION));
    }

    public boolean isSetObservationType() {
        return observationType != null && !observationType.isEmpty();
    }

    public boolean isSetOfferings() {
        return offerings != null && !offerings.isEmpty();
    }

    @Override
    public OmObservationConstellation clone() throws CloneNotSupportedException {
        OmObservationConstellation clone = new OmObservationConstellation();
        clone.setFeatureOfInterest(this.getFeatureOfInterest());
        clone.setObservableProperty(this.getObservableProperty());
        clone.setObservationType(this.getObservationType());
        clone.setOfferings(new HashSet<String>(this.getOfferings()));
        clone.setProcedure(this.getProcedure());
        return clone;
    }

    public boolean isEmpty() {
        return !isSetOfferings() && !isSetProcedure() && !isSetObservableProperty() && isSetFeatureOfInterest();
    }

    private boolean isSetFeatureOfInterest() {
        return getFeatureOfInterest() != null && getFeatureOfInterest().isSetIdentifier();
    }

    private boolean isSetObservableProperty() {
        return getObservableProperty() != null && getObservableProperty().isSetIdentifier();
    }

    private boolean isSetProcedure() {
        return getProcedure() != null && getProcedure().isSetIdentifier();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("OmObservationConstellation [");
        builder.append("procedure=").append(getProcedure().getIdentifierCodeWithAuthority());
        builder.append(", observableProperty=").append(getObservableProperty().getIdentifierCodeWithAuthority());
        builder.append(", featureOfInterest=").append(getFeatureOfInterest().getIdentifierCodeWithAuthority());
        if (isSetOfferings()) {
            builder.append(", offerings=[");
            boolean first = true;
            for (String offering : getOfferings()) {
                if (!first) {
                    builder.append(", ");
                }
                first = false;
                builder.append(offering);
            }
            builder.append("]");
        }
        if (isSetObservationType()) {
            builder.append(", observationType=").append(getObservationType());
        }
        builder.append("]");
        return builder.toString();
    }
}

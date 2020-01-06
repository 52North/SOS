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
package org.n52.sos.ogc.om;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.Metadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.Constants;
import org.n52.sos.w3c.Nillable;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 */
public class OmObservationConstellation extends AbstractFeature implements Serializable, Cloneable {
    private static final long serialVersionUID = 8758412729768944974L;

    /** Identifier of the procedure by which the observation is made */
    private Nillable<SosProcedureDescription> procedure = Nillable.<SosProcedureDescription>nil();

    /**
     * Identifier of the observableProperty to which the observation accords to
     */
    private AbstractPhenomenon observableProperty;

    /** Identifiers of the offerings to which this observation belongs */
    private Set<String> offerings;

    /** Identifier of the featureOfInterest to which this observation belongs */
    private Nillable<AbstractFeature> featureOfInterest = Nillable.<AbstractFeature>nil();

    /** type of the observation */
    private String observationType;

    // private SosResultTemplate sosResultTemplate;

    private DefaultPointMetadata defaultPointMetadata;

    private Metadata metadata;

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
        this.procedure = Nillable.of(procedure);
        this.observableProperty = observableProperty;
        this.featureOfInterest = Nillable.of(featureOfInterest);
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
     * @param offerings
     *            offering to which this observation belongs
     */
    public OmObservationConstellation(SosProcedureDescription procedure, AbstractPhenomenon observableProperty,
            AbstractFeature featureOfInterest, Set<String> offerings) {
       this(procedure, observableProperty, featureOfInterest);
       this.offerings = offerings;
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
        this(procedure, observableProperty, featureOfInterest, offerings);
        this.observationType = observationType;
    }

    /**
     * Get the procedure
     *
     * @return the procedure
     */
    public SosProcedureDescription getProcedure() {
        if (procedure.isPresent()){
            return procedure.get();
        }
        return null;
    }
    
    /**
     * Get the procedure
     *
     * @return the procedure
     */
    public Nillable<SosProcedureDescription> getNillableProcedure() {
        return procedure;
    }
    
    public String getProcedureIdentifier() {
        if (getProcedure() != null) {
            return getProcedure().getIdentifier();
        }
        return null;
    }

    /**
     * Set the procedure
     *
     * @param procedure
     *            the procedure to set
     * @return this
     */
    public OmObservationConstellation setProcedure(SosProcedureDescription procedure) {
        if (featureOfInterest == null) {
            return setProcedure(Nillable.<SosProcedureDescription>nil());
        }
        return setProcedure(Nillable.of(procedure));
    }
    
    
    /**
     * Set the procedure
     *
     * @param procedure
     *            the procedure to set
     * @return this
     */
    public OmObservationConstellation setProcedure(Nillable<SosProcedureDescription> procedure) {
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
    
    public String getObservablePropertyIdentifier() {
        return getObservableProperty().getIdentifier();
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
            this.offerings = new HashSet<>(offerings.size());
        }
        this.offerings.addAll(offerings);
        return this;
    }

    public OmObservationConstellation addOffering(String offering) {
        if (offerings == null) {
            offerings = new HashSet<>(1);
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
        if (featureOfInterest.isPresent()) {
            return featureOfInterest.get();
        }
        return null;
    }
    
   /**
    * Get featureOfInterest
    *
    * @return the featureOfInterest
    */
   public Nillable<AbstractFeature> getNillableFeatureOfInterest() {
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
        if (featureOfInterest == null) {
            return setFeatureOfInterest(Nillable.<AbstractFeature>nil());
        }
        return setFeatureOfInterest(Nillable.of(featureOfInterest));
    }
    
    /**
     * Set featureOfInterest
     *
     * @param featureOfInterest
     *            the featureOfInterest to set
     * @return this
     */
    public OmObservationConstellation setFeatureOfInterest(Nillable<AbstractFeature> featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }
    
    public String getFeatureOfInterestIdentifier() {
        if (getFeatureOfInterest() != null) {
            return getFeatureOfInterest().getIdentifier();
        }
        return null;
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
        return Objects.hashCode(this.procedure, Constants.HASH_CODE_19, this.observableProperty, this.offerings,
                Constants.HASH_CODE_43, this.featureOfInterest);
    }

    /**
     * Check if constellations are equal excluding observableProperty
     *
     * @param toCheckObsConst
     *            Observation constellation to chek
     * @return true if equals
     */
    @Deprecated
    public boolean equalsExcludingObsProp(OmObservationConstellation toCheckObsConst) {
        return (procedure.equals(toCheckObsConst.getProcedure())
                && featureOfInterest.equals(toCheckObsConst.getFeatureOfInterest())
                && observationType.equals(toCheckObsConst.getObservationType()) && checkObservationTypeForMerging());

    }

    /**
     * TODO change if currently not supported types could be merged.
     * 
     * @return <code>true</code>, if the observation can be merged
     */
    @Deprecated
    public boolean checkObservationTypeForMerging() {
        return (isSetObservationType() && !OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observationType)
                && !OmConstants.OBS_TYPE_COMPLEX_OBSERVATION.equals(observationType)
                && !OmConstants.OBS_TYPE_OBSERVATION.equals(observationType)
                && !OmConstants.OBS_TYPE_UNKNOWN.equals(observationType));
    }

    public boolean isSetObservationType() {
        return observationType != null && !observationType.isEmpty();
    }

    public boolean isSetOfferings() {
        return offerings != null && !offerings.isEmpty();
    }
    
    public boolean isSetProcedure() {
        return getNillableProcedure() != null && getNillableProcedure().isPresent();
    }
    
    public boolean isSetFeatureOfInterest() {
        return getNillableFeatureOfInterest() != null && getNillableFeatureOfInterest().isPresent();
    }

    @Override
    public OmObservationConstellation clone() throws CloneNotSupportedException {
        OmObservationConstellation clone = new OmObservationConstellation();
        clone.setFeatureOfInterest(this.getFeatureOfInterest());
        clone.setObservableProperty(this.getObservableProperty());
        clone.setObservationType(this.getObservationType());
        clone.setOfferings(new HashSet<String>(this.getOfferings()));
        clone.setProcedure(this.getProcedure());
        clone.setIdentifier(this.getIdentifier());
        clone.setName(this.getName());
        clone.setDescription(this.getDescription());
        return clone;
    }

    public boolean isEmpty() {
        return !isSetOfferings() && !hasProcedure() && !hasObservableProperty() && hasFeatureOfInterest();
    }

    private boolean hasFeatureOfInterest() {
        return getFeatureOfInterest() != null && getFeatureOfInterest().isSetIdentifier();
    }

    private boolean hasObservableProperty() {
        return getObservableProperty() != null && getObservableProperty().isSetIdentifier();
    }

    private boolean hasProcedure() {
        return getProcedure() != null && getProcedure().isSetIdentifier();
    }

    public boolean isSetDefaultPointMetadata() {
        return defaultPointMetadata != null;
    }

    public DefaultPointMetadata getDefaultPointMetadata() {
        return defaultPointMetadata;
    }

    public void setDefaultPointMetadata(DefaultPointMetadata defaultPointMetadata) {
        this.defaultPointMetadata = defaultPointMetadata;
    }

    public boolean isSetMetadata() {
        return metadata != null;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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

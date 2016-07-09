/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.ValuedParameterVisitor;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class ObservationOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationOmObservationCreator.class);

    private final Collection<? extends Observation<?>> observations;

    private final AbstractObservationRequest request;

    private final Map<String, AbstractFeature> features = Maps.newHashMap();

    private final Map<String, AbstractPhenomenon> observedProperties = Maps.newHashMap();

    private final Map<String, SosProcedureDescription> procedures = Maps.newHashMap();

    
    private final Map<Integer, OmObservationConstellation> observationConstellations = Maps.newHashMap();

    private List<OmObservation> observationCollection;


    public ObservationOmObservationCreator(Collection<? extends Observation<?>> observations,
            AbstractObservationRequest request, Locale language, Session session) {
    	super(request, session);
        this.request = request;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
    }

    public ObservationOmObservationCreator(Collection<? extends Observation<?>> observations, AbstractObservationRequest request,
            Session session) {
    	super(request, session);
        this.request = request;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
    }

	private Collection<? extends Observation<?>> getObservations() {
        return observations;
    }

    private String getResultModel() {
        return request.getResultModel();
    }

    private SosProcedureDescription getProcedure(String procedureId) {
        return procedures.get(procedureId);
    }

    private AbstractPhenomenon getObservedProperty(String phenomenonId) {
        return observedProperties.get(phenomenonId);
    }

    private AbstractFeature getFeature(String featureId) {
        return features.get(featureId);
    }

    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        if (getObservations() == null) {
            return Collections.emptyList();
        } else if (this.observationCollection == null) {
            this.observationCollection = Lists.newLinkedList();
            // now iterate over resultset and create Measurement for each row
            for (Observation<?> hObservation : getObservations()) {
//                // check remaining heap size and throw exception if minimum is
//                // reached
//                SosHelper.checkFreeMemory();
//
//                String procedureId = createProcedure(hObservation);
//                String featureId = createFeatureOfInterest(hObservation);
//                String phenomenonId = createPhenomenon(hObservation);
//                // TODO: add offering ids to response if needed later.
//                // String offeringID =
//                // hoc.getOffering().getIdentifier();
//                // String mimeType = SosConstants.PARAMETER_NOT_SET;

                observationCollection.add(createObservation(hObservation));
            }
        }
        return this.observationCollection;
    }

    protected OmObservation createObservation(Observation<?> hObservation) throws OwsExceptionReport, ConverterException {
        LOGGER.trace("Creating Observation...");
        SosHelper.checkFreeMemory();
        String procedureId = createProcedure(hObservation);
        String featureId = createFeatureOfInterest(hObservation);
        String phenomenonId = createPhenomenon(hObservation);
        final Value<?> value = hObservation.accept(new ObservationValueCreator());
        OmObservation sosObservation = null;
        if (value != null) {
            // TODO delete, set in ObservationValueCreator
//            if (hObservation.getUnit() != null) {
//                value.setUnit(hObservation.getUnit().getUnit());
//            }
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst =
                    createObservationConstellation(hObservation, procedureId, phenomenonId, featureId);
            sosObservation = createNewObservation(obsConst, hObservation, value);
            // add SpatialFilteringProfile
            if (hObservation.hasSamplingGeometry()) {
                sosObservation.addParameter(createSpatialFilteringProfileParameter(hObservation.getSamplingGeometry()));
            } else if (hObservation.isSetLongLat()) {
                sosObservation.addParameter(createSpatialFilteringProfileParameter(new HibernateGeometryCreator().createGeometry(hObservation)));
            }
            addParameter(sosObservation, hObservation);
            checkForAdditionalObservationCreator(hObservation, sosObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
        }
        getSession().evict(hObservation);
        LOGGER.trace("Creating Observation done.");
        return sosObservation;
    }

    private void addParameter(OmObservation observation, Observation<?> hObservation) throws OwsExceptionReport {
        if (hObservation.hasParameters()) {
            for (Parameter<?> parameter : hObservation.getParameters()) {
                observation.addParameter(parameter.accept(new ValuedParameterVisitor()));
            }
        }
    }

    private void checkOrSetObservablePropertyUnit(AbstractPhenomenon phen, String unit) {
        if (phen instanceof OmObservableProperty) {
            final OmObservableProperty obsProp = (OmObservableProperty) phen;
            if (obsProp.getUnit() == null && unit != null) {
                obsProp.setUnit(unit);
            }
        }
    }

    /**
     * Get observation value from all value tables for an Observation object
     *
     * @param hObservation
     *            Observation object
     *
     * @return Observation value
     *
     * @throws OwsExceptionReport
     * @throws CodedException
     * 
     * 
     * User {@link Observation#accept(org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor)}
     */
    @Deprecated
    private Value<?> getValueFromObservation(Observation<?> hObservation)
            throws OwsExceptionReport {
        Value<?> value = hObservation.accept(new ObservationValueCreator());
//        if (value != null && hObservation.isSetUnit()) {
//            value.setUnit(hObservation.getUnit().getUnit());
//        }
        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createNewObservation(OmObservationConstellation oc, Observation<?> ho, Value<?> value) {
        final OmObservation o = new OmObservation();
        o.setObservationID(Long.toString(ho.getObservationId()));
        if (ho.isSetIdentifier() && !ho.getIdentifier().startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            final CodeWithAuthority identifier = new CodeWithAuthority(ho.getIdentifier());
            if (ho.isSetCodespace()) {
                identifier.setCodeSpace(ho.getCodespace().getCodespace());
            }
            o.setIdentifier(identifier);
        }
        if (ho.isSetDescription()) {
            o.setDescription(ho.getDescription());
        }
        o.setNoDataValue(getActiveProfile().getResponseNoDataPlaceholder());
        o.setTokenSeparator(getTokenSeparator());
        o.setTupleSeparator(getTupleSeparator());
        o.setDecimalSeparator(getDecimalSeparator());
        o.setObservationConstellation(oc);
        o.setResultTime(new TimeInstant(new DateTime(ho.getResultTime(), DateTimeZone.UTC)));

        if (ho.getValidTimeStart() != null || ho.getValidTimeEnd() != null) {
            o.setValidTime(new TimePeriod(new DateTime(ho.getValidTimeStart(), DateTimeZone.UTC),
                                          new DateTime(ho.getValidTimeEnd(), DateTimeZone.UTC)));
        }

        o.setValue(new SingleObservationValue(getPhenomenonTime(ho), value));
        return o;
    }

    private Time getPhenomenonTime(final Observation<?> hObservation) {
        // create time element
        final DateTime phenStartTime = new DateTime(hObservation.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (hObservation.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(hObservation.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        Time phenomenonTime;
        if (phenStartTime.equals(phenEndTime)) {
            phenomenonTime = new TimeInstant(phenStartTime);
        } else {
            phenomenonTime = new TimePeriod(phenStartTime, phenEndTime);
        }
        return phenomenonTime;
    }

    private String createPhenomenon(final Observation<?> hObservation) {
        LOGGER.trace("Creating Phenomenon...");
        final String phenID = hObservation.getObservableProperty().getIdentifier();
        if (!observedProperties.containsKey(phenID)) {
        	 OmObservableProperty omObservableProperty = createObservableProperty(hObservation.getObservableProperty());
//            final String description = hObservation.getObservableProperty().getDescription();
//            OmObservableProperty omObservableProperty = new OmObservableProperty(phenID, description, null, null);
//            if (hObservation.getObservableProperty().isSetName()) {
//            	omObservableProperty.setHumanReadableIdentifier(hObservation.getObservableProperty().getName());
//            	omObservableProperty.setName(new CodeType(hObservation.getObservableProperty().getName()));
//            }
            observedProperties.put(phenID, omObservableProperty);
        }
        LOGGER.trace("Creating Phenomenon done.");
        return phenID;
    }

    private String createProcedure(final Observation<?> hObservation) throws OwsExceptionReport,
            ConverterException {
        // TODO sfp full description
        LOGGER.trace("Creating Procedure...");
        final String procedureId = hObservation.getProcedure().getIdentifier();
        if (!procedures.containsKey(procedureId)) {
            final SosProcedureDescription procedure = createProcedure(procedureId);
            procedures.put(procedureId, procedure);
        }
        LOGGER.trace("Creating Procedure done.");
        return procedureId;
    }

    private String createFeatureOfInterest(final Observation<?> hObservation) throws OwsExceptionReport {
        LOGGER.trace("Creating Feature...");
        final String foiID = hObservation.getFeatureOfInterest().getIdentifier();
        if (!features.containsKey(foiID)) {
            final AbstractFeature featureByID = createFeatureOfInterest(foiID);
            features.put(foiID, featureByID);
        }
        LOGGER.trace("Creating Feature done.");
        return foiID;
    }

    private OmObservationConstellation createObservationConstellation(Observation<?> hObservation,
            String procedureId, String phenomenonId, String featureId) {
        OmObservationConstellation obsConst =
                new OmObservationConstellation(getProcedure(procedureId), getObservedProperty(phenomenonId),
                        getFeature(featureId));
        if (observationConstellations.containsKey(obsConst.hashCode())) {
            return observationConstellations.get(obsConst.hashCode());
        } else {
            int hashCode = obsConst.hashCode();
            /* sfp the offerings to find the templates */
            if (obsConst.getOfferings() == null) {
                final Set<String> offerings =
                        Sets.newHashSet(getCache().getOfferingsForObservableProperty(
                                obsConst.getObservableProperty().getIdentifier()));
                offerings.retainAll(getCache().getOfferingsForProcedure(obsConst.getProcedure().getIdentifier()));
                obsConst.setOfferings(offerings);
            }
            if (StringHelper.isNotEmpty(getResultModel())) {
                obsConst.setObservationType(getResultModel());
            }
            final ObservationConstellationDAO dao = new ObservationConstellationDAO();
            final ObservationConstellation hoc =
                    dao.getFirstObservationConstellationForOfferings(hObservation.getProcedure(),
                            hObservation.getObservableProperty(), hObservation.getOfferings(), getSession());
            if (hoc != null && hoc.getObservationType() != null) {
                obsConst.setObservationType(hoc.getObservationType().getObservationType());
            }
            observationConstellations.put(hashCode, obsConst);
            return obsConst;
        }
    }


}

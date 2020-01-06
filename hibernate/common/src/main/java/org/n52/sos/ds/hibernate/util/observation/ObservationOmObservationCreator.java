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
import org.n52.sos.ds.hibernate.dao.observation.series.parameter.SeriesParameterDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.LegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.observation.ParameterAdder;
import org.n52.sos.ds.hibernate.entities.parameter.series.SeriesParameterAdder;
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
import org.n52.sos.util.GeometryHandler;
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
    private final Map<Long, List<Parameter>> seriesParameter = Maps.newHashMap();
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
        } 
        else if (this.observationCollection == null) {
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

//        getObservations().parallelStream().forEach(o -> {
//            try {
//                observationCollection.add(createObservation(o));
//            } catch (OwsExceptionReport e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        });
        return this.observationCollection;
    }

    protected OmObservation createObservation(Observation<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Observation...");
        SosHelper.checkFreeMemory();
        String procedureId = createProcedure(hObservation);
        String featureId = createFeatureOfInterest(hObservation);
        String phenomenonId = createPhenomenon(hObservation);
        Set<String> offerings = createOfferingSet(hObservation, procedureId, phenomenonId);
        final Value<?> value = hObservation.accept(new ObservationValueCreator());
        OmObservation sosObservation = null;
        if (value != null) {
            if (hObservation.getUnit() != null) {
                value.setUnit(hObservation.getUnit().getUnit());
            } else if (hObservation instanceof SeriesObservation && ((SeriesObservation)hObservation).getSeries().isSetUnit()) {
                value.setUnit(queryUnit(((SeriesObservation)hObservation).getSeries()));
            }
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst =
                    createObservationConstellation(hObservation, procedureId, phenomenonId, featureId, offerings);
            sosObservation = createNewObservation(obsConst, hObservation, value);
            // add SpatialFilteringProfile
            if (hObservation.hasSamplingGeometry()) {
                sosObservation.addSpatialFilteringProfileParameter(GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(hObservation.getSamplingGeometry()));
            } else if (hObservation.isSetLongLat()) {
                sosObservation.addSpatialFilteringProfileParameter(GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(new HibernateGeometryCreator().createGeometry(hObservation)));
            }
            addRelatedObservations(sosObservation, hObservation);
            addParameter(sosObservation, hObservation);
            checkForAdditionalObservationCreator(hObservation, sosObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
        }
        getSession().evict(hObservation);
        LOGGER.trace("Creating Observation done in {} ms.", System.currentTimeMillis() - start);
        return sosObservation;
    }

    private void addRelatedObservations(OmObservation sosObservation, Observation<?> hObservation) throws CodedException {
        new RelatedObservationAdder(sosObservation, hObservation).add();
    }

    private void addParameter(OmObservation observation, Observation<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Parameter...");
        if (hObservation instanceof AbstractSeriesObservation) {
            Series series = ((AbstractSeriesObservation) hObservation).getSeries();
            if (!seriesParameter.containsKey(series.getSeriesId())) {
                seriesParameter.put(series.getSeriesId(), new SeriesParameterDAO().getSeriesParameter(series, getSession()));
            }
            if (!seriesParameter.get(series.getSeriesId()).isEmpty()) {
                new SeriesParameterAdder(observation, seriesParameter.get(series.getSeriesId())).add();
            }
        }
        new ParameterAdder(observation, hObservation).add();
        LOGGER.trace("Creating Parameter done in {} ms.", System.currentTimeMillis() - start);
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
        o.setObservationConstellation(oc);
        addDefaultValuesToObservation(o);
        o.setResultTime(new TimeInstant(new DateTime(ho.getResultTime(), DateTimeZone.UTC)));

        if (ho.getValidTimeStart() != null || ho.getValidTimeEnd() != null) {
            o.setValidTime(new TimePeriod(new DateTime(ho.getValidTimeStart(), DateTimeZone.UTC),
                                          new DateTime(ho.getValidTimeEnd(), DateTimeZone.UTC)));
        }

        o.setValue(new SingleObservationValue(getPhenomenonTime(ho), value));
        return o;
    }

    private Time getPhenomenonTime(final Observation<?> hObservation) {
        return new PhenomenonTimeCreator(hObservation).create();
    }

    private String createPhenomenon(final Observation<?> hObservation) {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Phenomenon...");
        final String phenID = hObservation.getObservableProperty().getIdentifier();
        if (!observedProperties.containsKey(phenID)) {
            OmObservableProperty omObservableProperty = createObservableProperty(hObservation.getObservableProperty());
            observedProperties.put(phenID, omObservableProperty);
        }
        LOGGER.trace("Creating Phenomenon done in {} ms.", System.currentTimeMillis() - start);
        return phenID;
    }

    private String createProcedure(final Observation<?> hObservation) throws OwsExceptionReport {
        // TODO sfp full description
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Procedure...");
        final String procedureId = hObservation.getProcedure().getIdentifier();
        if (!procedures.containsKey(procedureId)) {
            final SosProcedureDescription procedure = createProcedure(hObservation.getProcedure());
            procedures.put(procedureId, procedure);
        }
        LOGGER.trace("Creating Procedure done in {} ms.", System.currentTimeMillis() - start);
        return procedureId;
    }

    private String createFeatureOfInterest(final Observation<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Feature...");
        final String foiID = hObservation.getFeatureOfInterest().getIdentifier();
        if (!features.containsKey(foiID)) {
            final AbstractFeature featureByID = createFeatureOfInterest(hObservation.getFeatureOfInterest());
            features.put(foiID, featureByID);
        }
        LOGGER.trace("Creating Feature done in {} ms.", System.currentTimeMillis() - start);
        return foiID;
    }

    private Set<String> createOfferingSet(Observation<?> hObservation, String procedure, String observedProperty) {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Offerings...");
        Set<String> offerings = Sets.newHashSet();    
        if (hObservation instanceof AbstractSeriesObservation && ((AbstractSeriesObservation<?>) hObservation).getSeries().isSetOffering()) {
             offerings.add(((AbstractSeriesObservation<?>) hObservation).getSeries().getOffering().getIdentifier());
        } else if (hObservation instanceof LegacyObservation && ((LegacyObservation)hObservation).isSetOfferings()) {
            for (Offering offering : ((LegacyObservation)hObservation).getOfferings()) {
                offerings.add(offering.getIdentifier());
            }
        } else {
            offerings.addAll(getCache().getOfferingsForObservableProperty(
                            observedProperty));
            offerings.retainAll(getCache().getOfferingsForProcedure(procedure));
        }
        LOGGER.trace("Creating Offerings done in {} ms.", System.currentTimeMillis() - start);
        return offerings;
    }
    private OmObservationConstellation createObservationConstellation(Observation<?> hObservation,
            String procedureId, String phenomenonId, String featureId, Set<String> offerings) {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating ObservationConstellation...");
        OmObservationConstellation obsConstCheck = new OmObservationConstellation();
        obsConstCheck.setProcedure(getProcedure(procedureId));
        obsConstCheck.setObservableProperty(getObservedProperty(phenomenonId));
        obsConstCheck.setOfferings(offerings);
                       
        if (!observationConstellations.containsKey(obsConstCheck.hashCode())) {
            if (StringHelper.isNotEmpty(getResultModel())) {
                obsConstCheck.setObservationType(getResultModel());
            } else {
                final ObservationConstellationDAO dao = new ObservationConstellationDAO();
                final ObservationConstellation hoc =
                        dao.getFirstObservationConstellationForOfferings(hObservation.getProcedure(),
                                hObservation.getObservableProperty(), hObservation.getOffering(), getSession());
                if (hoc != null && hoc.getObservationType() != null) {
                    obsConstCheck.setObservationType(hoc.getObservationType().getObservationType());
                }
            }
            observationConstellations.put(obsConstCheck.hashCode(), obsConstCheck);
        } else {
            obsConstCheck = observationConstellations.get(obsConstCheck.hashCode());
        }
        OmObservationConstellation obsConst;
        try {
            obsConst = obsConstCheck.clone();
        } catch (CloneNotSupportedException e) {
            obsConst = obsConstCheck;
        }
        obsConst.setFeatureOfInterest(getFeature(featureId));
        if (hObservation instanceof SeriesObservation<?>) {
            Series series = ((SeriesObservation<?>) hObservation).getSeries();
            if (series.isSetIdentifier()) {
                addIdentifier(obsConst, series);
            }
            if (series.isSetName()) {
                addName(obsConst, series);
            }
            if (series.isSetDescription()) {
                obsConst.setDescription(series.getDescription());
            }
        }
        LOGGER.trace("Creating ObservationConstellation done in {} ms.", System.currentTimeMillis() - start);
        return obsConst;
    }
}

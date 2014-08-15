/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;


public class ObservationOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationOmObservationCreator.class);

    private final Collection<? extends Observation<?>> observations;

    private final AbstractObservationRequest request;

    private final Map<String, AbstractFeature> features = Maps.newHashMap();

    private final Map<String, AbstractPhenomenon> observedProperties = Maps.newHashMap();

    private final Map<String, SosProcedureDescription> procedures = Maps.newHashMap();

    private final Set<OmObservationConstellation> observationConstellations = Sets.newHashSet();

    private List<OmObservation> observationCollection;


    public ObservationOmObservationCreator(Collection<? extends Observation<?>> observations,
            AbstractObservationRequest request, Locale language, Session session) {
    	super(checkVersion(request), session);
        this.request = request;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
    }

    public ObservationOmObservationCreator(Collection<? extends Observation<?>> observations, AbstractObservationRequest request,
            Session session) {
    	super(checkVersion(request), session);
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

    private String getResponseFormat() {
        if (request.isSetResponseFormat()) {
            return request.getResponseFormat();
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getObservationResponseFormat();
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
        final Value<?> value = getValueFromObservation(hObservation);
        OmObservation sosObservation = null;
        if (value != null) {
            if (hObservation.getUnit() != null) {
                value.setUnit(hObservation.getUnit().getUnit());
            }
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst =
                    createObservationConstellation(hObservation, procedureId, phenomenonId, featureId);
            sosObservation = createNewObservation(obsConst, hObservation, value);
            // add SpatialFilteringProfile
            if (hObservation.hasSamplingGeometry()) {
                sosObservation.addParameter(createSpatialFilteringProfileParameter(hObservation.getSamplingGeometry()));
            }
            checkFoAdditionalObservationCreator(hObservation, sosObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
        }
        getSession().evict(hObservation);
        LOGGER.trace("Creating Observation done.");
        return sosObservation;
    }

    private void checkFoAdditionalObservationCreator(Observation<?> hObservation, OmObservation sosObservation) {
        AdditionalObservationCreatorKey key = new AdditionalObservationCreatorKey(getResponseFormat(), hObservation.getClass());
        if (AdditionalObservationCreatorRepository.getInstance().hasAdditionalObservationCreatorFor(key)) {
            AdditionalObservationCreator creator = AdditionalObservationCreatorRepository.getInstance().get(key);
            creator.create(sosObservation, hObservation);
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
     */
    private Value<?> getValueFromObservation(Observation<?> hObservation)
            throws OwsExceptionReport {
        return hObservation.accept(new ObservationValueCreator());
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
        o.setObservationConstellation(oc);
        o.setResultTime(new TimeInstant(new DateTime(ho.getResultTime(), DateTimeZone.UTC)));
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

        /* sfp the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            final Set<String> offerings = Sets.newHashSet(getCache().getOfferingsForObservableProperty(obsConst.getObservableProperty().getIdentifier()));
            offerings.retainAll(getCache().getOfferingsForProcedure(obsConst.getProcedure().getIdentifier()));
//            final Set<String> offerings =
//                    Sets.newHashSet(getCache().getOfferingsForObservableProperty(
//                            obsConst.getObservableProperty().getIdentifier()));
//            offerings.retainAll(getCache().getOfferingsForProcedure(obsConst.getProcedure().getIdentifier()));
            obsConst.setOfferings(offerings);
        }
        if (!observationConstellations.contains(obsConst)) {
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
            observationConstellations.add(obsConst);
        }
        return obsConst;
    }


}

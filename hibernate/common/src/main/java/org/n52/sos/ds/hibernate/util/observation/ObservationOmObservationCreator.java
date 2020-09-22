/*
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.iceland.convert.ConverterException;
import org.n52.janmayen.http.MediaTypes;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.DetectionLimitEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.sos.ds.hibernate.util.HibernateUnproxy;
import org.n52.sos.ds.observation.ObservationValueCreator;
import org.n52.sos.ds.observation.ParameterAdder;
import org.n52.sos.ds.observation.PhenomenonTimeCreator;
import org.n52.sos.ds.observation.RelatedObservationAdder;
import org.n52.sos.util.SosHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ObservationOmObservationCreator extends AbstractOmObservationCreator implements HibernateUnproxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationOmObservationCreator.class);

    private final Collection<? extends DataEntity<?>> observations;

    private final AbstractObservationRequest request;

    private final Map<String, AbstractFeature> features = Maps.newHashMap();

    private final Map<String, AbstractPhenomenon> observedProperties = Maps.newHashMap();

    private final Map<String, SosProcedureDescription<?>> procedures = Maps.newHashMap();

    private final Map<Integer, OmObservationConstellation> observationConstellations = Maps.newHashMap();

    private final Map<Long, Set<ParameterEntity<?>>> seriesParameter = Maps.newHashMap();

    private List<OmObservation> observationCollection;

    public ObservationOmObservationCreator(Collection<? extends DataEntity<?>> observations,
            AbstractObservationRequest request, Locale i18n, String pdf, HibernateOmObservationCreatorContext creatorContext,
            Session session) {
        super(request, i18n, pdf, creatorContext, session);
        this.request = request;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
    }

    private Collection<? extends DataEntity<?>> getObservations() {
        return observations;
    }

    private String getResultModel() {
        return request.getResultModel();
    }

    private AbstractFeature getProcedure(String procedureId) {
        return procedures.get(procedureId);
    }

    private AbstractPhenomenon getObservedProperty(String phenomenonId) {
        return observedProperties.get(phenomenonId);
    }

    private AbstractFeature getFeature(String featureId) {
        return features.get(featureId);
    }

    @Override
    public ObservationStream create() throws OwsExceptionReport, ConverterException {
        if (getObservations() == null) {
            return ObservationStream.empty();
        } else if (this.observationCollection == null) {
            this.observationCollection = Lists.newLinkedList();
            // now iterate over resultset and create Measurement for each row
            for (DataEntity<?> hObservation : getObservations()) {
                // // check remaining heap size and throw exception if minimum
                // is
                // // reached
                // SosHelper.checkFreeMemory();
                //
                // String procedureId = createProcedure(hObservation);
                // String featureId = createFeatureOfInterest(hObservation);
                // String phenomenonId = createPhenomenon(hObservation);
                // // TODO: add offering ids to response if needed later.
                // // String offeringID =
                // // hoc.getOffering().getIdentifier();
                // // String mimeType = SosConstants.PARAMETER_NOT_SET;

                observationCollection.add(createObservation(hObservation));
            }
        }
        return ObservationStream.of(this.observationCollection);
    }

    protected OmObservation createObservation(DataEntity<?> hObservation)
            throws OwsExceptionReport, ConverterException {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Observation...");
        SosHelper.checkFreeMemory();
        String procedureId = createProcedure(hObservation);
        String featureId = createFeatureOfInterest(hObservation);
        String phenomenonId = createPhenomenon(hObservation);
        Set<String> offerings = createOfferingSet(hObservation, procedureId, phenomenonId);
        final Value<?> value = new ObservationValueCreator(getCreatorContext().getDecoderRepository())
                .visit(unproxy(hObservation, getSession()));
        OmObservation sosObservation = null;
        if (value != null) {
            value.setUnit(queryUnit(hObservation.getDataset()));
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst =
                    createObservationConstellation(hObservation, procedureId, phenomenonId, featureId, offerings);
            sosObservation = createNewObservation(obsConst, hObservation, value);
            // add SpatialFilteringProfile
            if (hObservation.isSetGeometryEntity()) {
                sosObservation.addSpatialFilteringProfileParameter(getGeometryHandler()
                        .switchCoordinateAxisFromToDatasourceIfNeeded(hObservation.getGeometryEntity().getGeometry()));
            }
            addRelatedObservations(sosObservation, hObservation);
            addParameter(sosObservation, hObservation);
            checkForAdditionalObservationCreator(hObservation, sosObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
            if (!value.isSetValue() && hObservation.hasDetectionLimit()) {
                sosObservation
                        .addParameter(createDetectionLimit(hObservation.getDetectionLimit(), value.getUnitObject()));
            }
        }
        getSession().evict(hObservation);
        LOGGER.trace("Creating Observation done in {} ms.", System.currentTimeMillis() - start);
        return sosObservation;
    }

    private void addRelatedObservations(OmObservation sosObservation, DataEntity<?> hObservation)
            throws CodedException {
        new RelatedObservationAdder(sosObservation, hObservation, getCreatorContext().getServiceURL().toString(),
                getCreatorContext().getBindingRepository().isActive(MediaTypes.APPLICATION_KVP)).add();
    }

    private void addParameter(OmObservation observation, DataEntity<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Parameter...");
        DatasetEntity series = ((DataEntity<?>) hObservation).getDataset();
        if (!seriesParameter.containsKey(series.getId()) && series.hasParameters()) {
            seriesParameter.put(series.getId(), series.getParameters());
        }
        if (seriesParameter.get(series.getId()) != null && !seriesParameter.get(series.getId()).isEmpty()) {
            new DatasetParameterAdder(observation, seriesParameter.get(series.getId())).add();
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createNewObservation(OmObservationConstellation oc, DataEntity<?> ho, Value<?> value)
            throws OwsExceptionReport {
        final OmObservation o = new OmObservation();
        o.setObservationID(Long.toString(ho.getId()));
        if (ho.isSetIdentifier() && !ho.getIdentifier().startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            final CodeWithAuthority identifier = new CodeWithAuthority(ho.getIdentifier());
            if (ho.isSetIdentifierCodespace()) {
                identifier.setCodeSpace(ho.getIdentifierCodespace().getName());
            }
            o.setIdentifier(identifier);
        }
        addNameAndDescription(ho, o, getRequestedLanguage(), getI18N(), false);
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

    private Time getPhenomenonTime(final DataEntity<?> hObservation) {
        return new PhenomenonTimeCreator(hObservation).create();
    }

    private String createPhenomenon(final DataEntity<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Phenomenon...");
        final String phenID = hObservation.getDataset().getPhenomenon().getIdentifier();
        if (!observedProperties.containsKey(phenID)) {
            OmObservableProperty omObservableProperty =
                    createObservableProperty(hObservation.getDataset().getPhenomenon());
            observedProperties.put(phenID, omObservableProperty);
        }
        LOGGER.trace("Creating Phenomenon done in {} ms.", System.currentTimeMillis() - start);
        return phenID;
    }

    private String createProcedure(final DataEntity<?> hObservation) throws OwsExceptionReport, ConverterException {
        // TODO sfp full description
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Procedure...");
        final String procedureId = hObservation.getDataset().getProcedure().getIdentifier();
        if (!procedures.containsKey(procedureId)) {
            final SosProcedureDescription<?> procedure = createProcedure(hObservation.getDataset().getProcedure());
            procedures.put(procedureId, procedure);
        }
        LOGGER.trace("Creating Procedure done in {} ms.", System.currentTimeMillis() - start);
        return procedureId;
    }

    private String createFeatureOfInterest(final DataEntity<?> hObservation) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Feature...");
        final String foiID = hObservation.getDataset().getFeature().getIdentifier();
        if (!features.containsKey(foiID)) {
            final AbstractFeature featureByID = createFeatureOfInterest(hObservation.getDataset().getFeature());
            features.put(foiID, featureByID);
        }
        LOGGER.trace("Creating Feature done in {} ms.", System.currentTimeMillis() - start);
        return foiID;
    }

    private Set<String> createOfferingSet(DataEntity<?> hObservation, String procedure, String observedProperty) {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating Offerings...");
        Set<String> offerings = Sets.newHashSet();
        offerings.add(hObservation.getDataset().getOffering().getIdentifier());
        LOGGER.trace("Creating Offerings done in {} ms.", System.currentTimeMillis() - start);
        return offerings;
    }

    private NamedValue<?> createDetectionLimit(DetectionLimitEntity detectionLimit, UoM uoM) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        final ReferenceType referenceType =
                new ReferenceType(detectionLimit.getFlag() > 0 ? "exceed limit" : "below limit");
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(detectionLimit.getDetectionLimit(), uoM));
        return namedValue;
    }

    private OmObservationConstellation createObservationConstellation(DataEntity<?> hObservation, String procedureId,
            String phenomenonId, String featureId, Set<String> offerings) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        LOGGER.trace("Creating ObservationConstellation...");
        OmObservationConstellation obsConst = new OmObservationConstellation(getProcedure(procedureId),
                getObservedProperty(phenomenonId), getFeature(featureId), offerings);
        if (observationConstellations.containsKey(obsConst.hashCode())) {
            obsConst = observationConstellations.get(obsConst.hashCode());
        }
        int hashCode = obsConst.hashCode();
        if (!Strings.isNullOrEmpty(getResultModel())) {
            obsConst.setObservationType(getResultModel());
        }
        if (hObservation.getDataset().isSetOMObservationType()) {
            obsConst.setObservationType(hObservation.getDataset().getOmObservationType().getFormat());
        }
        observationConstellations.put(hashCode, obsConst);
        DatasetEntity series = hObservation.getDataset();
        if (series.isSetIdentifier()) {
            addIdentifier(obsConst, series);
        }
        obsConst.setObservationType(getResultModel());
        if (request.isSetRequestedLanguage()) {
            addNameAndDescription(series, obsConst,
                    getRequestedLanguage(), getI18N(), false);
            if (obsConst.isSetName()) {
                obsConst.setHumanReadableIdentifier(obsConst.getFirstName().getValue());
            }
        } else {
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

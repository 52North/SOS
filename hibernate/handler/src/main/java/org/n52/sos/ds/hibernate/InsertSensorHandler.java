/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.PhenomenonNameDescriptionProvider;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.FeatureWith.FeatureWithGeometry;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.shetland.ogc.sensorML.elements.SmlCapability;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swes.SwesFeatureRelationship;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractInsertSensorHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureHistoryDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationPersister;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.util.GeometryHandler;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

/**
 * Implementation of the abstract class AbstractInsertSensorHandler
 *
 * @since 4.0.0
 *
 */
public class InsertSensorHandler extends AbstractInsertSensorHandler implements Constructable {

    public static final Predicate<SmlCapabilities> REFERENCE_VALUES_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_REFERENCE_VALUES);

    private static final String REFERENCE_VALUE = "_referencevalue";

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    public InsertSensorHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public void init() {
        sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized InsertSensorResponse insertSensor(final InsertSensorRequest request)
            throws OwsExceptionReport {
        final InsertSensorResponse response = new InsertSensorResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        final String assignedProcedureID = request.getAssignedProcedureIdentifier();
        // we use only the first offering for the response because swes 2.0
        // specifies only one single element
        final SosOffering firstAssignedOffering = request.getFirstAssignedOffering();
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = session.beginTransaction();
            FormatDAO formatDAO = new FormatDAO();
            final FormatEntity procedureDescriptionFormat =
                    formatDAO.getOrInsertFormatEntity(request.getProcedureDescriptionFormat(), session);
            if (procedureDescriptionFormat != null) {
                final ProcedureEntity hProcedure = new ProcedureDAO(getDaoFactory()).getOrInsertProcedure(
                        assignedProcedureID, procedureDescriptionFormat, request.getProcedureDescription(),
                        request.isType(), session);
                // TODO: set correct validTime,
                new ProcedureHistoryDAO(getDaoFactory()).insert(hProcedure, procedureDescriptionFormat,
                        getSensorDescriptionFromProcedureDescription(request.getProcedureDescription()),
                        new DateTime(DateTimeZone.UTC), session);
                if (!request.isType()) {
                    final List<FormatEntity> observationTypes =
                            formatDAO.getOrInsertFormatEntitys(request.getMetadata().getObservationTypes(), session);
                    final List<FormatEntity> featureOfInterestTypes = formatDAO
                            .getOrInsertFormatEntitys(request.getMetadata().getFeatureOfInterestTypes(), session);
                    if (observationTypes != null && featureOfInterestTypes != null) {
                        final List<PhenomenonEntity> hObservableProperties = getOrInsertNewObservableProperties(
                                request.getObservableProperty(), request.getProcedureDescription(), session);
                        Map<String, UnitEntity> hUnits =
                                getOrInsertNewUnits(hObservableProperties, request.getProcedureDescription(), session);
                        final AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
                        final OfferingDAO offeringDAO = getDaoFactory().getOfferingDAO();
                        Set<String> allParentOfferings = getAllParentOfferings(hProcedure);
                        Set<String> parentOfferings = getParentOfferings(hProcedure);
                        for (final SosOffering assignedOffering : request.getAssignedOfferings()) {
                            if (!assignedOffering.isParentOffering()) {
                                final List<RelatedFeatureEntity> hRelatedFeatures =
                                        new LinkedList<RelatedFeatureEntity>();
                                if (request.getRelatedFeatures() != null && !request.getRelatedFeatures().isEmpty()) {
                                    final RelatedFeatureDAO relatedFeatureDAO = getDaoFactory().getRelatedFeatureDAO();
                                    for (final SwesFeatureRelationship relatedFeature : request.getRelatedFeatures()) {
                                        hRelatedFeatures.addAll(relatedFeatureDAO.getOrInsertRelatedFeature(
                                                relatedFeature.getFeature(), relatedFeature.getRole(), session));
                                    }
                                }
                                final OfferingEntity hOffering = offeringDAO.getAndUpdateOrInsert(
                                        new SosOffering(assignedOffering.getIdentifier(),
                                                assignedOffering.getOfferingName()),
                                        hRelatedFeatures, observationTypes, featureOfInterestTypes, session);

                                // add offering to parent offering if this procedure
                                // is a child/component
                                if (!parentOfferings.isEmpty() && !allParentOfferings.isEmpty()
                                        && hProcedure.hasParents()
                                        && !allParentOfferings.contains(assignedOffering.getIdentifier())
                                        && !parentOfferings.contains(assignedOffering.getIdentifier())) {
                                    offeringDAO.updateParentOfferings(parentOfferings, hOffering, session);
                                }

                                CategoryEntity hCategory = getCategory(request, session);
                                Optional<PlatformEntity> platform = getPlatform(request, session);
                                for (final PhenomenonEntity hObservableProperty : hObservableProperties) {
                                    ObservationContext ctx = new ObservationContext().setCategory(hCategory)
                                            .setOffering(hOffering).setPhenomenon(hObservableProperty)
                                            .setProcedure(hProcedure).setPublish(false)
                                            .setHiddenChild(!assignedOffering.isParentOffering());
                                    if (platform.isPresent()) {
                                        ctx.setPlatform(platform.get());
                                    }
                                    checkForMobileInsituFlags(ctx,
                                            request.getProcedureDescription().getProcedureDescription());
                                    if (hUnits.containsKey(hObservableProperty.getIdentifier())) {
                                        ctx.setUnit(hUnits.get(hObservableProperty.getIdentifier()));
                                    }
                                    if (request.getProcedureDescription().isSetFeaturesOfInterestMap()) {
                                        boolean inserted = false;
                                        for (AbstractFeature feature : request.getProcedureDescription()
                                                .getFeaturesOfInterestMap().values()) {
                                            if (feature instanceof FeatureWithGeometry
                                                    && ((FeatureWithGeometry) feature).isSetGeometry()) {
                                                ctx.setFeatureOfInterest(getDaoFactory().getFeatureOfInterestDAO()
                                                        .checkOrInsert(feature, session));
                                                inserted = true;
                                                seriesDAO.getOrInsert(ctx, session);
                                            }
                                        }
                                        if (!inserted) {
                                            seriesDAO.getOrInsert(ctx, session);
                                        }
                                    } else {
                                        seriesDAO.getOrInsert(ctx, session);
                                    }

                                    if (checkPreconditionsOfStaticReferenceValues(request)) {
                                        addStaticReferenceValues(request, session, procedureDescriptionFormat,
                                                hProcedure, observationTypes, featureOfInterestTypes, hRelatedFeatures,
                                                hOffering, hObservableProperty, seriesDAO);
                                    }
                                }
                            }
                        }
                        // TODO: parent and child procedures
                    } else {
                        throw new NoApplicableCodeException()
                                .withMessage("Error while inserting InsertSensor into database!");
                    }
                }
                response.setAssignedProcedure(assignedProcedureID);
                response.setAssignedOffering(firstAssignedOffering.getIdentifier());
            } else {
                throw new InvalidParameterValueException(Sos2Constants.InsertSensorParams.procedureDescriptionFormat,
                        request.getProcedureDescriptionFormat());
            }
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while inserting sensor data into database!");
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        return response;
    }

    private boolean checkPreconditionsOfStaticReferenceValues(final InsertSensorRequest request) {
        return request.getProcedureDescription().getProcedureDescription() instanceof AbstractProcessV20
                && ((AbstractProcessV20) request.getProcedureDescription().getProcedureDescription())
                        .isSetSmlFeatureOfInterest()
                && ((AbstractSensorML) request.getProcedureDescription().getProcedureDescription())
                        .findCapabilities(REFERENCE_VALUES_PREDICATE).isPresent()
                && !request.getProcedureDescription().getFeaturesOfInterestMap().isEmpty()
                && request.getProcedureDescription().getFeaturesOfInterestMap().size() == 1;
    }

    private void addStaticReferenceValues(InsertSensorRequest request, Session session,
            FormatEntity procedureDescriptionFormat, ProcedureEntity hProcedure, List<FormatEntity> observationTypes,
            List<FormatEntity> featureOfInterestTypes, List<RelatedFeatureEntity> hRelatedFeatures,
            OfferingEntity hOffering, PhenomenonEntity hObservableProperty, AbstractSeriesDAO seriesDAO)
            throws OwsExceptionReport {
        AbstractFeature sosFeatureOfInterest =
                request.getProcedureDescription().getFeaturesOfInterestMap().entrySet().iterator().next().getValue();
        AbstractFeatureEntity hFeature = getDaoFactory().getFeatureDAO().insertFeature(sosFeatureOfInterest, session);
        for (SmlCapability referenceValue : ((AbstractSensorML) request.getProcedureDescription()
                .getProcedureDescription()).findCapabilities(REFERENCE_VALUES_PREDICATE).get().getCapabilities()) {
            if (!(referenceValue.getAbstractDataComponent() instanceof SweQuantity)) {
                throw new NoApplicableCodeException().withMessage(
                        "ReferenceValue of Type '%s' is not supported -> Aborting InsertSensor Operation!",
                        referenceValue.getAbstractDataComponent().getDataComponentType());
            }
            SweQuantity referenceValueValue = (SweQuantity) referenceValue.getAbstractDataComponent();
            String identifier = hProcedure.getIdentifier() + REFERENCE_VALUE;
            SosProcedureDescription procedureReferenceSeries =
                    new SosProcedureDescriptionUnknownType(identifier, procedureDescriptionFormat.getFormat(), "");
            procedureReferenceSeries.setReference(true);
            procedureReferenceSeries.setName(new CodeType(referenceValue.getName()));
            ProcedureEntity hProcedureReferenceSeries = getDaoFactory().getProcedureDAO().getOrInsertProcedure(
                    identifier, procedureDescriptionFormat, procedureReferenceSeries, false, session);
            OfferingEntity hOfferingReferenceSeries = getDaoFactory().getOfferingDAO().getAndUpdateOrInsert(
                    new SosOffering(hOffering.getIdentifier() + REFERENCE_VALUE,
                            hOffering.getName() + REFERENCE_VALUE),
                    hRelatedFeatures, observationTypes, featureOfInterestTypes, session);
            TimeInstant time = new TimeInstant(new DateTime(0));
            SingleObservationValue<BigDecimal> sosValue = new SingleObservationValue<>(
                    new QuantityValue(referenceValueValue.getValue(), referenceValueValue.getUom()));

            OmObservation sosObservation = new OmObservation();
            sosValue.setPhenomenonTime(time);
            sosObservation.setResultTime(time);
            sosObservation.setValue(sosValue);
            sosObservation.setObservationConstellation(new OmObservationConstellation(procedureReferenceSeries,
                    new OmObservableProperty(hObservableProperty.getIdentifier()), sosFeatureOfInterest));

            DatasetEntity hObservationConstellationReferenceSeries = new DatasetEntity();
            hObservationConstellationReferenceSeries.setObservableProperty(hObservableProperty);
            hObservationConstellationReferenceSeries.setOffering(hOfferingReferenceSeries);
            hObservationConstellationReferenceSeries.setProcedure(hProcedureReferenceSeries);
            Map<String, CodespaceEntity> codespaceCache = CollectionHelper.synchronizedMap();
            Map<UoM, UnitEntity> unitCache = CollectionHelper.synchronizedMap();
            Map<String, FormatEntity> formatCache = CollectionHelper.synchronizedMap();
            ObservationPersister persister =
                    new ObservationPersister(getDaoFactory(), getDaoFactory().getObservationDAO(), sosObservation,
                            hObservationConstellationReferenceSeries, hFeature, codespaceCache, unitCache, formatCache,
                            Collections.singleton(hOfferingReferenceSeries), session);
            DataEntity<?> observation = sosValue.getValue().accept(persister);
            DatasetEntity hReferenceSeries = seriesDAO.getSeries(hProcedureReferenceSeries.getIdentifier(),
                    hObservableProperty.getIdentifier(), hOfferingReferenceSeries.getIdentifier(),
                    Collections.singleton(hFeature.getIdentifier()), session).get(0);
            hReferenceSeries.setPublished(false);
            session.update(hReferenceSeries);
            ObservationContext ctxReferenced = new ObservationContext();
            ctxReferenced.setPhenomenon(hObservableProperty);
            ctxReferenced.setFeatureOfInterest(hFeature);
            ctxReferenced.setProcedure(hProcedure);
            ctxReferenced.setOffering(hOffering);
            ctxReferenced.setPublish(false);
            DatasetEntity hSeries = seriesDAO.getOrInsertSeries(ctxReferenced, observation, session);
            if (hSeries.getValueType().equals(ValueType.quantity)) {
                hSeries.setReferenceValues(Lists.newArrayList(hReferenceSeries));
            }
            session.update(hSeries);
        }
    }

    /**
     * Create OmObservableProperty objects from observableProperty identifiers and get or insert them into the
     * database
     *
     * @param obsProps
     *            observableProperty identifiers
     * @param sosProcedureDescription
     *            procedure description
     * @param session
     *            Hibernate Session
     * @return ObservableProperty entities
     */
    private List<PhenomenonEntity> getOrInsertNewObservableProperties(final Collection<String> obsProps,
            SosProcedureDescription sosProcedureDescription, final Session session) {
        final List<OmObservableProperty> observableProperties = new ArrayList<>(obsProps.size());
        if (sosProcedureDescription.getProcedureDescription() instanceof PhenomenonNameDescriptionProvider) {
            PhenomenonNameDescriptionProvider process =
                    (PhenomenonNameDescriptionProvider) sosProcedureDescription.getProcedureDescription();
            for (final String observableProperty : obsProps) {
                OmObservableProperty omObservableProperty = new OmObservableProperty(observableProperty);
                if (process.isSetObservablePropertyName(observableProperty)) {
                    omObservableProperty.addName(process.getObservablePropertyName(observableProperty));
                }
                if (process.isSetObservablePropertyDescription(observableProperty)) {
                    omObservableProperty.setDescription(process.getObservablePropertyDescription(observableProperty));
                }
                observableProperties.add(omObservableProperty);
            }

        }
        return getDaoFactory().getObservablePropertyDAO().getOrInsertObservableProperty(observableProperties, session);
    }

    private Map<String, UnitEntity> getOrInsertNewUnits(List<PhenomenonEntity> hObservableProperties,
            SosProcedureDescription<?> procedureDescription, Session session) {
        Map<String, UnitEntity> map = new LinkedHashMap<>();
        if (procedureDescription.getProcedureDescription() instanceof PhenomenonNameDescriptionProvider) {
            PhenomenonNameDescriptionProvider process =
                    (PhenomenonNameDescriptionProvider) procedureDescription.getProcedureDescription();
            for (PhenomenonEntity phenomenonEntity : hObservableProperties) {
                UoM unit = process.getObservablePropertyUnit(phenomenonEntity.getIdentifier());
                if (unit != null) {
                    UnitEntity hUnit = getDaoFactory().getUnitDAO().getOrInsertUnit(unit, session);
                    if (hUnit != null) {
                        map.put(phenomenonEntity.getIdentifier(), hUnit);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Get SensorDescription String from procedure description
     *
     * @param procedureDescription
     *            Procedure description
     * @return SensorDescription String
     */
    private String getSensorDescriptionFromProcedureDescription(SosProcedureDescription<?> procedureDescription) {
        if (procedureDescription.getProcedureDescription() instanceof SensorML) {
            final SensorML sensorML = (SensorML) procedureDescription.getProcedureDescription();
            // if SensorML is not a wrapper
            if (!sensorML.isWrapper()) {
                return sensorML.getXml();
            } else if (sensorML.isWrapper() && sensorML.getMembers().size() == 1) {
                // if SensorML is a wrapper and member size is 1
                return sensorML.getMembers().get(0).getXml();
            } else {
                // TODO: get sensor description for procedure identifier
                return "";
            }
        } else if (procedureDescription.getProcedureDescription().isSetXml()) {
            return procedureDescription.getProcedureDescription().getXml();
        } else if (procedureDescription.isSetXml()) {
            return procedureDescription.getXml();
        }
        return "";
    }

    /**
     * Check whether the procedure description provides flags for mobile and insitu and add it to the
     * {@link ObservationContext}.
     *
     * @param ctx
     *            the {@link ObservationContext} to add flags
     * @param procedureDescription
     *            the procedure description to check for
     */
    private void checkForMobileInsituFlags(ObservationContext ctx, AbstractFeature procedureDescription) {
        if (procedureDescription instanceof AbstractSensorML) {
            AbstractSensorML sml = (AbstractSensorML) procedureDescription;
            if (sml.isSetMobile()) {
                ctx.setMobile(sml.getMobile());
            }
            if (sml.isSetInsitu()) {
                ctx.setInsitu(sml.getInsitu());
            }
        }
    }

    private CategoryEntity getCategory(InsertSensorRequest request, Session session) {
        if (request.hasExtension(OmConstants.PARAMETER_NAME_CATEGORY)) {
            Optional<Extension<?>> extension = request.getExtension(OmConstants.PARAMETER_NAME_CATEGORY);
            if (extension.isPresent() && extension.get().getValue() instanceof SweText) {
                return getDaoFactory().getCategoryDAO().getOrInsertCategory((SweText) extension.get().getValue(),
                        session);
            }
        }
        return getDaoFactory().getCategoryDAO().getOrInsertCategory(getDaoFactory().getDefaultCategory(), session);
    }

    private Optional<PlatformEntity> getPlatform(InsertSensorRequest request, Session session) {
        if (request.hasExtension(OmConstants.PARAMETER_NAME_PLATFORM)) {
            Optional<Extension<?>> extension = request.getExtension(OmConstants.PARAMETER_NAME_PLATFORM);
            if (extension.isPresent() && extension.get().getValue() instanceof SweText) {
                return Optional.of(getDaoFactory().getPlatformDAO()
                        .getOrInsertPlatform((SweText) extension.get().getValue(), session));
            }
        }
        return Optional.empty();
    }

    private Set<String> getAllParentOfferings(ProcedureEntity hProcedure) {
        Set<String> parentOfferings = new HashSet<>();
        if (hProcedure.hasParents()) {
            for (ProcedureEntity proc : hProcedure.getParents()) {
                parentOfferings.addAll(getCache().getOfferingsForProcedure(proc.getIdentifier()));
                parentOfferings.addAll(getParentOfferings(hProcedure));
            }
        }
        return parentOfferings;
    }

    private Set<String> getParentOfferings(ProcedureEntity hProcedure) {
        Set<String> parentOfferings = new HashSet<>();
        if (hProcedure.hasParents()) {
            for (ProcedureEntity proc : hProcedure.getParents()) {
                parentOfferings.addAll(getCache().getOfferingsForProcedure(proc.getIdentifier()));
            }
        }
        return parentOfferings;
    }

    private GeometryHandler getGeometryHandler() {
        return getDaoFactory().getGeometryHandler();
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    private synchronized DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

    @VisibleForTesting
    protected synchronized void initForTesting(DaoFactory daoFactory, ConnectionProvider connectionProvider) {
        this.daoFactory = daoFactory;
        this.connectionProvider = connectionProvider;
    }

}

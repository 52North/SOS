/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.series.db.beans.RelatedFeatureRoleEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.shetland.ogc.PhenomenonNameDescriptionProvider;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
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
import org.n52.shetland.ogc.swes.SwesFeatureRelationship;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractInsertSensorHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureDAO;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureRoleDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationPersister;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractInsertSensorHandler
 *
 * @since 4.0.0
 *
 */
public class InsertSensorDAO extends AbstractInsertSensorHandler {

    private HibernateSessionHolder sessionHolder;
    private DaoFactory daoFactory;

    public static final Predicate<SmlCapabilities> REFERENCE_VALUES_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_REFERENCE_VALUES);

    /**
     * constructor
     */
    public InsertSensorDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized InsertSensorResponse insertSensor(final InsertSensorRequest request) throws OwsExceptionReport {
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
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            final FormatEntity procedureDescriptionFormat =
                    new FormatDAO().getOrInsertFormatEntity(
                            request.getProcedureDescriptionFormat(), session);
            if (procedureDescriptionFormat != null)  {
                final ProcedureEntity hProcedure =
                        daoFactory.getProcedureDAO().getOrInsertProcedure(assignedProcedureID, procedureDescriptionFormat,
                                request.getProcedureDescription(), request.isType(), session);
                // TODO: set correct validTime,
                if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
                    daoFactory.getValidProcedureTimeDAO().insertValidProcedureTime(
                            hProcedure,
                            procedureDescriptionFormat,
                            getSensorDescriptionFromProcedureDescription(request.getProcedureDescription()), new DateTime(DateTimeZone.UTC), session);
                }
                if (!request.isType()) {
                    final List<FormatEntity> observationTypes = daoFactory.getObservationTypeDAO()
                            .getOrInsertFormatEntitys(request.getMetadata().getObservationTypes(), session);
                    final List<FormatEntity> featureOfInterestTypes = daoFactory.getFeatureTypeDAO()
                            .getOrInsertFormatEntitys(request.getMetadata().getFeatureOfInterestTypes(),
                                    session);
                    if (observationTypes != null && featureOfInterestTypes != null) {
                        final List<PhenomenonEntity> hObservableProperties =
                                getOrInsertNewObservableProperties(request.getObservableProperty(),
                                        request.getProcedureDescription(), session);
                        final AbstractSeriesDAO observationConstellationDAO =
                                daoFactory.getSeriesDAO();
                        final OfferingDAO offeringDAO = daoFactory.getOfferingDAO();
                        for (final SosOffering assignedOffering : request.getAssignedOfferings()) {
                            final List<RelatedFeatureEntity> hRelatedFeatures = new LinkedList<>();
                            if (request.getRelatedFeatures() != null && !request.getRelatedFeatures().isEmpty()) {
                                final RelatedFeatureDAO relatedFeatureDAO = new RelatedFeatureDAO(daoFactory);
                                final RelatedFeatureRoleDAO relatedFeatureRoleDAO = new RelatedFeatureRoleDAO();
                                for (final SwesFeatureRelationship relatedFeature : request.getRelatedFeatures()) {
                                    final List<RelatedFeatureRoleEntity> relatedFeatureRoles = relatedFeatureRoleDAO
                                            .getOrInsertRelatedFeatureRole(relatedFeature.getRole(), session);
                                    hRelatedFeatures.addAll(relatedFeatureDAO.getOrInsertRelatedFeature(
                                            relatedFeature.getFeature(), relatedFeatureRoles, session));
                                }
                            }
                            final OfferingEntity hOffering =
                                    offeringDAO.getAndUpdateOrInsert(assignedOffering, hRelatedFeatures,
                                            observationTypes, featureOfInterestTypes, session);
                            for (final PhenomenonEntity hObservableProperty : hObservableProperties) {
                                CategoryEntity hCategory = daoFactory.getObservablePropertyDAO().getOrInsertCategory(hObservableProperty, session);
                                 DatasetEntity hObservationConstellation = observationConstellationDAO
                                        .checkOrInsertSeries(hProcedure, hObservableProperty,
                                                hOffering, hCategory, assignedOffering.isParentOffering(), session);
                                if (checkPreconditionsOfStaticReferenceValues(request)) {
                                    addStaticReferenceValues(request, session, procedureDescriptionFormat, hProcedure,
                                            observationTypes, featureOfInterestTypes, hRelatedFeatures, hOffering,
                                            hObservableProperty);
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
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while inserting sensor data into database!");
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    private boolean checkPreconditionsOfStaticReferenceValues(final InsertSensorRequest request) {
        return request.getProcedureDescription().getProcedureDescription() instanceof AbstractProcessV20 &&
                ((AbstractProcessV20) request.getProcedureDescription().getProcedureDescription()).isSetSmlFeatureOfInterest() &&
                ((AbstractSensorML) request.getProcedureDescription().getProcedureDescription()).findCapabilities(REFERENCE_VALUES_PREDICATE)
                    .isPresent() &&
                    !request.getProcedureDescription().getFeaturesOfInterestMap().isEmpty() &&
                    request.getProcedureDescription().getFeaturesOfInterestMap().size() == 1;
    }

    private void addStaticReferenceValues(final InsertSensorRequest request, Session session,
            final ProcedureDescriptionFormat procedureDescriptionFormat, final Procedure hProcedure,
            final List<ObservationType> observationTypes, final List<FeatureOfInterestType> featureOfInterestTypes,
            final List<RelatedFeature> hRelatedFeatures, final Offering hOffering,
            final ObservableProperty hObservableProperty) throws OwsExceptionReport, CodedException {
        // We support only ONE feature of interest atm. -> check method
        AbstractFeature sosFeatureOfInterest = request.getProcedureDescription().getFeaturesOfInterestMap().entrySet()
                .iterator().next().getValue();
        AbstractFeatureOfInterest hFeature = new FeatureOfInterestDAO().checkOrInsertFeatureOfInterest(
                sosFeatureOfInterest, session);
        for (SmlCapability referenceValue : ((AbstractSensorML) request.getProcedureDescription())
                .findCapabilities(REFERENCE_VALUES_PREDICATE).get().getCapabilities()) {
            if (!(referenceValue.getAbstractDataComponent() instanceof SweQuantity)) {
                // FIXME clarify if abort or just log error message?
                throw new NoApplicableCodeException().withMessage(
                        "ReferenceValue of Type '%s' is not supported -> Aborting InsertSensor Operation!",
                        referenceValue.getAbstractDataComponent().getDataComponentType());
            }
            String identifier = hProcedure.getIdentifier() + "_referencevalue";
            SosProcedureDescription procedureReferenceSeries = new SosProcedureDescriptionUnknowType(identifier,
                    procedureDescriptionFormat.getProcedureDescriptionFormat(), "");
            procedureReferenceSeries.setReference(true);
            procedureReferenceSeries.setName(new CodeType(referenceValue.getName()));
            Procedure hProcedureReferenceSeries = new ProcedureDAO().getOrInsertProcedure(
                    identifier,
                    procedureDescriptionFormat,
                    procedureReferenceSeries,
                    false,
                    session);
            Offering hOfferingReferenceSeries = new OfferingDAO().getAndUpdateOrInsertNewOffering(
                    new SosOffering(
                            hOffering.getIdentifier() + "_referencevalue",
                            hOffering.getName() + "_referencevalue"),
                    hRelatedFeatures,
                    observationTypes,
                    featureOfInterestTypes,
                    session);
            TimeInstant time = new TimeInstant(new DateTime(0));
            SweQuantity referenceValueValue = (SweQuantity) referenceValue.getAbstractDataComponent();
            SingleObservationValue<Double> sosValue = new SingleObservationValue<>(new QuantityValue(
                    referenceValueValue.getValue(), referenceValueValue.getUom()));

            OmObservation sosObservation = new OmObservation();
            sosValue.setPhenomenonTime(time);
            sosObservation.setResultTime(time);
            sosObservation.setValue(sosValue);
            sosObservation.setObservationConstellation(new OmObservationConstellation(procedureReferenceSeries,
                    new OmObservableProperty(hObservableProperty.getIdentifier()),
                    sosFeatureOfInterest));

            ObservationConstellation hObservationConstellationReferenceSeries = new ObservationConstellation();
            hObservationConstellationReferenceSeries.setObservableProperty(hObservableProperty);
            hObservationConstellationReferenceSeries.setOffering(hOfferingReferenceSeries);
            hObservationConstellationReferenceSeries.setProcedure(hProcedureReferenceSeries);
            Map<String, Codespace> codespaceCache = CollectionHelper.synchronizedMap();
            Map<UoM, Unit> unitCache = CollectionHelper.synchronizedMap();
            ObservationPersister persister = new ObservationPersister(
                    new SeriesObservationDAO(),
                    sosObservation,
                    hObservationConstellationReferenceSeries,
                    hFeature,
                    codespaceCache,
                    unitCache,
                    Collections.singleton(hOfferingReferenceSeries),
                    session
                    );
            sosValue.getValue().accept(persister);
            SeriesDAO seriesDAO = new SeriesDAO();
            Series hReferenceSeries = seriesDAO.getSeries(hProcedureReferenceSeries.getIdentifier(),
                    hObservableProperty.getIdentifier(),
                    hOfferingReferenceSeries.getIdentifier(),
                    Collections.singleton(hFeature.getIdentifier()),
                    session).get(0);
            session.update(hReferenceSeries);
            ObservationContext ctx = new ObservationContext();
            ctx.setObservableProperty(hObservableProperty);
            ctx.setFeatureOfInterest(hFeature);
            ctx.setProcedure(hProcedure);
            ctx.setOffering(hOffering);
            ctx.setPublish(false);
            Series hSeries = seriesDAO.getOrInsertSeries(ctx, session);
            hSeries.setReferenceValues(Collections.singletonList(hReferenceSeries));
            session.update(hSeries);
        }
    }

    /**
     * Create OmObservableProperty objects from observableProperty identifiers
     * and get or insert them into the database
     *
     * @param obsProps
     *            observableProperty identifiers
     * @param sosProcedureDescription
     * @param session
     *            Hibernate Session
     * @return ObservableProperty entities
     */
    private List<PhenomenonEntity> getOrInsertNewObservableProperties(final Collection<String> obsProps,
            SosProcedureDescription sosProcedureDescription, final Session session) {
        final List<OmObservableProperty> observableProperties = new ArrayList<>(obsProps.size());
        if (sosProcedureDescription.getProcedureDescription() instanceof PhenomenonNameDescriptionProvider) {
            PhenomenonNameDescriptionProvider process = (PhenomenonNameDescriptionProvider) sosProcedureDescription.getProcedureDescription();
            for (final String observableProperty : obsProps) {
                OmObservableProperty omObservableProperty = new OmObservableProperty(observableProperty);
                if (process.isSetObservablePropertyDescription(observableProperty)) {
                    String name = process.getObservablePropertyDescription(observableProperty);
                    if (!Strings.isNullOrEmpty(name)) {
                        omObservableProperty.addName(name);
                    }
                }
                observableProperties.add(omObservableProperty);
            }
            
        }
        return daoFactory.getObservablePropertyDAO().getOrInsertObservableProperty(observableProperties, false, session);
    }

    /**
     * Get SensorDescription String from procedure description
     *
     * @param procedureDescription
     *            Procedure description
     * @return SensorDescription String
     */
    private String getSensorDescriptionFromProcedureDescription(SosProcedureDescription<?> procedureDescription ) {
        if (procedureDescription.getProcedureDescription() instanceof SensorML) {
            final SensorML sensorML = (SensorML) procedureDescription.getProcedureDescription();
            // if SensorML is not a wrapper
            if (!sensorML.isWrapper()) {
                return sensorML.getXml();
            }
            // if SensorML is a wrapper and member size is 1
            else if (sensorML.isWrapper() && sensorML.getMembers().size() == 1) {
                return sensorML.getMembers().get(0).getXml();
            } else {
                // TODO: get sensor description for procedure identifier
                return "";
            }
        } else if (procedureDescription.getProcedureDescription() instanceof AbstractFeature) {
            return procedureDescription.getProcedureDescription().getXml();
        } else if (procedureDescription.isSetXml()) {
            return procedureDescription.getXml();
        }
        return "";
    }
    
    private GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureEntity.class);
    }

}

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
package org.n52.sos.ds.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ds.AbstractInsertSensorDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.CategoryDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestTypeDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationTypeDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDescriptionFormatDAO;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureDAO;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureRoleDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationPersister;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.RelatedFeatureRole;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.sos.ogc.sensorML.elements.SmlCapability;
import org.n52.sos.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.sos.ogc.sos.CapabilitiesExtension;
import org.n52.sos.ogc.sos.CapabilitiesExtensionKey;
import org.n52.sos.ogc.sos.CapabilitiesExtensionProvider;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionCapabilities;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * Implementation of the abstract class AbstractInsertSensorDAO
 *
 * @since 4.0.0
 *
 */
public class InsertSensorDAO extends AbstractInsertSensorDAO implements CapabilitiesExtensionProvider {

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public static final Predicate<SmlCapabilities> REFERENCE_VALUES_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_REFERENCE_VALUES);

    /**
     * constructor
     */
    public InsertSensorDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public synchronized InsertSensorResponse insertSensor(final InsertSensorRequest request) throws OwsExceptionReport {
        checkForTransactionalEntity();
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
            final ProcedureDescriptionFormat procedureDescriptionFormat =
                    new ProcedureDescriptionFormatDAO().getOrInsertProcedureDescriptionFormat(
                            request.getProcedureDescriptionFormat(), session);
            if (procedureDescriptionFormat != null)  {
                final Procedure hProcedure =
                        new ProcedureDAO().getOrInsertProcedure(assignedProcedureID, procedureDescriptionFormat,
                                request.getProcedureDescription(), request.isType(), session);
                // TODO: set correct validTime,
                new ValidProcedureTimeDAO().insertValidProcedureTime(
                        hProcedure,
                        procedureDescriptionFormat,
                        getSensorDescriptionFromProcedureDescription(request.getProcedureDescription(),
                                assignedProcedureID), new DateTime(DateTimeZone.UTC), session);
                if (!request.isType()) {
                    final List<ObservationType> observationTypes = new ObservationTypeDAO()
                            .getOrInsertObservationTypes(request.getMetadata().getObservationTypes(), session);
                    final List<FeatureOfInterestType> featureOfInterestTypes = new FeatureOfInterestTypeDAO()
                            .getOrInsertFeatureOfInterestTypes(request.getMetadata().getFeatureOfInterestTypes(),
                                    session);
                    if (observationTypes != null && featureOfInterestTypes != null) {
                        final List<ObservableProperty> hObservableProperties =
                                getOrInsertNewObservableProperties(request.getObservableProperty(),
                                        request.getProcedureDescription(), session);
                        final ObservationConstellationDAO observationConstellationDAO =
                                new ObservationConstellationDAO();
                        final OfferingDAO offeringDAO = new OfferingDAO();
                        Set<String> allParentOfferings = getAllParentOfferings(hProcedure);
                        Set<String> parentOfferings = getParentOfferings(hProcedure);
                        for (final SosOffering assignedOffering : request.getAssignedOfferings()) {
                            final List<RelatedFeature> hRelatedFeatures = new LinkedList<>();
                            if (request.getRelatedFeatures() != null && !request.getRelatedFeatures().isEmpty()) {
                                final RelatedFeatureDAO relatedFeatureDAO = new RelatedFeatureDAO();
                                final RelatedFeatureRoleDAO relatedFeatureRoleDAO = new RelatedFeatureRoleDAO();
                                for (final SwesFeatureRelationship relatedFeature : request.getRelatedFeatures()) {
                                    final List<RelatedFeatureRole> relatedFeatureRoles = relatedFeatureRoleDAO
                                            .getOrInsertRelatedFeatureRole(relatedFeature.getRole(), session);
                                    hRelatedFeatures.addAll(relatedFeatureDAO.getOrInsertRelatedFeature(
                                            relatedFeature.getFeature(), relatedFeatureRoles, session));
                                }
                            }
                            
                            final Offering hOffering =
                                    offeringDAO.getAndUpdateOrInsertNewOffering(assignedOffering, hRelatedFeatures,
                                            observationTypes, featureOfInterestTypes, session);
                            
                            // add offering to parent offering if this procedure is a child/component
                            if (!parentOfferings.isEmpty() && !allParentOfferings.isEmpty() && hProcedure.hasParents()
                                    && !allParentOfferings.contains(assignedOffering.getIdentifier())
                                    && !parentOfferings.contains(assignedOffering.getIdentifier())) {
                                offeringDAO.updateParentOfferings(parentOfferings, hOffering, session);
                            }
                            
                            for (final ObservableProperty hObservableProperty : hObservableProperties) {
                                observationConstellationDAO.checkOrInsertObservationConstellation(hProcedure,
                                        hObservableProperty, hOffering, assignedOffering.isParentOffering(), session);
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
        return request.getProcedureDescription() instanceof AbstractProcessV20 &&
                ((AbstractProcessV20) request.getProcedureDescription()).isSetSmlFeatureOfInterest() &&
                ((AbstractSensorML) request.getProcedureDescription()).findCapabilities(REFERENCE_VALUES_PREDICATE)
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
                    true,
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
            SeriesObservationContext ctx = new SeriesObservationContext();
            ctx.setObservableProperty(hObservableProperty);
            ctx.setFeatureOfInterest(hFeature);
            ctx.setProcedure(hProcedure);
            ctx.setOffering(hOffering);
            // category
            if (HibernateHelper.isColumnSupported(Series.class, Series.CATEGORY)) {
                ctx.setCategory(new CategoryDAO().getOrInsertCategory(hObservableProperty, session));
            }
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
    private List<ObservableProperty> getOrInsertNewObservableProperties(final List<String> obsProps,
            SosProcedureDescription sosProcedureDescription, final Session session) {
        final List<OmObservableProperty> observableProperties = new ArrayList<>(obsProps.size());
        for (final String observableProperty : obsProps) {
            OmObservableProperty omObservableProperty = new OmObservableProperty(observableProperty);
            if (sosProcedureDescription.supportsObservablePropertyName()) {
                if (sosProcedureDescription.isSetObservablePropertyNameFor(observableProperty)) {
                    String name = sosProcedureDescription.getObservablePropertyNameFor(observableProperty);
                    if (!Strings.isNullOrEmpty(name)) {
                        omObservableProperty.addName(name);
                    }
                }
            }
            observableProperties.add(omObservableProperty);
        }
        return new ObservablePropertyDAO().getOrInsertObservableProperty(observableProperties, false, session);
    }

    /**
     * Get SensorDescription String from procedure description
     *
     * @param procedureDescription
     *            Procedure description
     * @param procedureIdentifier
     *            Procedure identifier
     * @return SensorDescription String
     */
    private String getSensorDescriptionFromProcedureDescription(final SosProcedureDescription procedureDescription,
            final String procedureIdentifier) {
        if (procedureDescription instanceof SensorML) {
            final SensorML sensorML = (SensorML) procedureDescription;
            // if SensorML is not a wrapper
            if (!sensorML.isWrapper()) {
                return sensorML.getSensorDescriptionXmlString();
            }
            // if SensorML is a wrapper and member size is 1
            else if (sensorML.isWrapper() && sensorML.getMembers().size() == 1) {
                return sensorML.getMembers().get(0).getSensorDescriptionXmlString();
            } else {
                // TODO: get sensor description for procedure identifier
                return "";
            }
        }
        // if procedureDescription not SensorML
        else {
            return procedureDescription.getSensorDescriptionXmlString();
        }
    }

    private void checkForTransactionalEntity() throws CodedException {
        if (!HibernateHelper.isEntitySupported(TProcedure.class)) {
            throw new NoApplicableCodeException().withMessage("The transactional database profile is not activated!");
        }
    }

    private Set<String> getAllParentOfferings(Procedure hProcedure) {
        Set<String> parentOfferings = new HashSet<>();
        if (hProcedure.hasParents()) {
            for (Procedure proc : hProcedure.getParents()) {
                parentOfferings.addAll(getCache().getOfferingsForProcedure(proc.getIdentifier()));
                parentOfferings.addAll(getParentOfferings(hProcedure));
            }
        }
        return parentOfferings;
    }
    
    private Set<String> getParentOfferings(Procedure hProcedure) {
        Set<String> parentOfferings = new HashSet<>();
        if (hProcedure.hasParents()) {
            for (Procedure proc : hProcedure.getParents()) {
                parentOfferings.addAll(getCache().getOfferingsForProcedure(proc.getIdentifier()));
            }
        }
        return parentOfferings;
    }

    @Override
    public CapabilitiesExtension getExtension() {
        final SosInsertionCapabilities insertionCapabilities = new SosInsertionCapabilities();
        insertionCapabilities.addFeatureOfInterestTypes(getCache().getFeatureOfInterestTypes());
        insertionCapabilities.addObservationTypes(getCache().getObservationTypes());
        insertionCapabilities.addProcedureDescriptionFormats(CodingRepository.getInstance()
                .getSupportedTransactionalProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        return insertionCapabilities;
    }

    @Override
    public CapabilitiesExtensionKey getCapabilitiesExtensionKey() {
        return new CapabilitiesExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
    }

    @Override
    public boolean hasRelatedOperation() {
        return true;
    }

    @Override
    public String getRelatedOperation() {
        return getOperationName();
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ValidProcedureTime.class);
    }

}

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
package org.n52.sos.ds.hibernate.util.observation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.http.MediaType;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.iso.gmd.CiOnlineResource;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.GenericMetaData;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public abstract class AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOmObservationCreator.class);
    protected static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES =
            "getUnitForObservablePropertyProcedureSeries";
    protected static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES =
            "getUnitForObservablePropertySeries";
    private final AbstractObservationRequest request;
    private final Session session;
    private final Locale i18n;
    private final String pdf;
    private final OmObservationCreatorContext creatorContext;

    public AbstractOmObservationCreator(
            AbstractObservationRequest request, Locale i18n, String pdf, OmObservationCreatorContext creatorContext,
            Session session) {
        this.creatorContext = creatorContext;
        this.request = request;
        this.session = session;
        this.i18n = i18n == null ? creatorContext.getDefaultLanguage() : i18n;
        this.pdf = pdf;
    }

    protected OmObservationCreatorContext getCreatorContext() {
        return creatorContext;
    }

    public DaoFactory getDaoFactory() {
        return getCreatorContext().getDaoFactory();
    }

    protected SosContentCache getCache() {
        return getCreatorContext().getCache();
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return getCreatorContext().getFeatureQueryHandler();
    }

    protected AdditionalObservationCreatorRepository getAdditionalObservationCreatorRepository() {
        return getCreatorContext().getAdditionalObservationCreatorRepository();
    }

    protected Profile getActiveProfile() {
        return getCreatorContext().getProfileHandler().getActiveProfile();
    }

    protected String getTokenSeparator() {
        return getCreatorContext().getTokenSeparator();
    }

    protected String getTupleSeparator() {
        return getCreatorContext().getTupleSeparator();
    }

    protected String getDecimalSeparator() {
        return getCreatorContext().getDecimalSeparator();
    }

    protected ConverterRepository getConverterRepository() {
        return getCreatorContext().getConverterRepository();
    }

    protected HibernateProcedureDescriptionGeneratorFactoryRepository getProcedureDescriptionGeneratorFactoryRepository() {
        return getCreatorContext().getProcedureDescriptionGeneratorFactoryRepository();
    }

    protected GeometryHandler getGeometryHandler() {
        return getCreatorContext().getGeometryHandler();
    }

    protected LocalizedProducer<OwsServiceProvider> getServiceProvider() {
        return getCreatorContext().getServiceProvider(getService());
    }

    protected String getNoDataValue() {
        return getActiveProfile().getResponseNoDataPlaceholder();
    }

    protected void addDefaultValuesToObservation(OmObservation o) {
        o.setNoDataValue(getActiveProfile().getResponseNoDataPlaceholder());
        o.setNoDataValue(getNoDataValue());
        o.setTokenSeparator(getTokenSeparator());
        o.setTupleSeparator(getTupleSeparator());
        o.setDecimalSeparator(getDecimalSeparator());
        addMetadata(o);
    }

    public abstract ObservationStream create()
            throws OwsExceptionReport, ConverterException;

    private void addMetadata(OmObservation o) {
        if (MetaDataConfigurations.getInstance().isShowCiOnlineReourceInObservations()) {
            CiOnlineResource ciOnlineResource = new CiOnlineResource(getServiceURL());
            ciOnlineResource.setProtocol("OGC:SOS-2.0.0");
            o.addMetaDataProperty(new GenericMetaData(ciOnlineResource));
        }
    }

    protected URI getServiceURL() {
        return getCreatorContext().getServiceURL();
    }

    public String getVersion() {
        return request.getVersion();
    }

    public String getService() {
        return request.getService();
    }

    public String getResponseFormat() {
        return request.isSetResponseFormat() ? request.getResponseFormat()
                : getActiveProfile().getObservationResponseFormat();
    }

    public List<MediaType> getAcceptType() {
        return request.getRequestContext().getAcceptType().get();
    }

    public boolean checkAcceptType() {
        return request.getRequestContext() != null && request.getRequestContext().getAcceptType().isPresent();
    }

    public Session getSession() {
        return session;
    }

    public Locale getI18N() {
        return i18n;
    }

    public I18NDAORepository getI18NDAORepository() {
        return getCreatorContext().getI18nr();
    }

    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(
                new GeometryValue(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

    protected OmObservableProperty createObservableProperty(PhenomenonEntity observableProperty)
            throws CodedException {
        String phenID = observableProperty.getIdentifier();
        String description = observableProperty.getDescription();
        OmObservableProperty omObservableProperty = new OmObservableProperty(phenID, description, null, null);
        if (observableProperty.isSetName()) {
            omObservableProperty.setHumanReadableIdentifier(observableProperty.getName());
            addName(omObservableProperty, observableProperty);
        }
        return omObservableProperty;
    }

    /**
     * Get procedure object from series
     *
     * @param identifier
     *
     * @return Procedure identifier
     * @throws ConverterException
     *             If an error occurs sensor description creation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected SosProcedureDescription<?> createProcedure(String identifier)
            throws ConverterException, OwsExceptionReport {
        return createProcedure(new ProcedureDAO(getDaoFactory()).getProcedureForIdentifier(identifier, getSession()));
    }

    /**
     * Get procedure object from series
     *
     * @param identifier
     *
     * @return Procedure object
     *
     * @throws ConverterException
     *             If an error occurs sensor description creation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected SosProcedureDescription<?> createProcedure(ProcedureEntity hProcedure)
            throws ConverterException, OwsExceptionReport {
        // Procedure hProcedure =
        // getDaoFactory().getProcedureDAO().getProcedureForIdentifier(identifier,
        // getSession());
        String pdf = !Strings.isNullOrEmpty(this.pdf) ? this.pdf
                : hProcedure.getFormat().getFormat();
        if (getActiveProfile().isEncodeProcedureInObservation()) {
            return getCreatorContext().getProcedureConverter().createSosProcedureDescription(hProcedure, pdf,
                    getVersion(), i18n, getSession());
        } else {
            SosProcedureDescriptionUnknownType sosProcedure =
                    new SosProcedureDescriptionUnknownType(hProcedure.getIdentifier(), pdf, null);
            if (hProcedure.isSetName()) {
                sosProcedure.setHumanReadableIdentifier(hProcedure.getName());
                addName(sosProcedure, hProcedure);
            }
            return sosProcedure;
        }
    }

    /**
     * @param abstractFeature
     * @param hAbstractFeature
     */
    protected void addIdentifier(AbstractFeature abstractFeature,
            DescribableEntity hAbstractFeature) {
        if (hAbstractFeature.isSetIdentifierCodespace()) {
            abstractFeature.setIdentifier(new CodeWithAuthority(hAbstractFeature.getIdentifier(),
                    hAbstractFeature.getIdentifierCodespace().getName()));
        }
        abstractFeature.setIdentifier(new CodeWithAuthority(hAbstractFeature.getIdentifier()));
    }

    /**
     * @param abstractFeature
     * @param hAbstractFeature
     *
     * @throws CodedException
     */
    protected void addName(AbstractFeature abstractFeature, DescribableEntity hAbstractFeature)
            throws CodedException {
        if (hAbstractFeature.isSetNameCodespace()) {
            try {
                abstractFeature.addName(hAbstractFeature.getName(),
                        new URI(hAbstractFeature.getNameCodespace().getName()));
            } catch (URISyntaxException e) {
                throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating URI from '{}'",
                        hAbstractFeature.getNameCodespace().getName());
            }
        }
        abstractFeature.addName(hAbstractFeature.getName());

    }

    /**
     * Get featureOfInterest object from series
     *
     * @param identifier
     *
     * @return FeatureOfInerest object
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected AbstractFeature createFeatureOfInterest(AbstractFeatureEntity foi)
            throws OwsExceptionReport {
        if (getActiveProfile().isEncodeFeatureOfInterestInObservations()) {
            FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject(getSession());
            queryObject.setFeatureObject(foi).addFeatureIdentifier(foi.getIdentifier()).setVersion(getVersion());
            final AbstractFeature feature = getFeatureQueryHandler().getFeatureByID(queryObject);
            if (getActiveProfile().getEncodingNamespaceForFeatureOfInterest() != null
                    && !feature.getDefaultElementEncoding()
                            .equals(getActiveProfile().getEncodingNamespaceForFeatureOfInterest())) {
                feature.setDefaultElementEncoding(getActiveProfile().getEncodingNamespaceForFeatureOfInterest());
            }
            return feature;
        } else {
            SamplingFeature samplingFeature = new SamplingFeature(new CodeWithAuthority(foi.getIdentifier()));
            if (foi.isSetIdentifierCodespace()) {
                samplingFeature.getIdentifierCodeWithAuthority().setCodeSpace(foi.getIdentifierCodespace().getName());
            }
            if (foi.isSetName()) {
                CodeType codeType = new CodeType(foi.getName());
                if (foi.isSetNameCodespace()) {
                    try {
                        codeType.setCodeSpace(new URI(foi.getNameCodespace().getName()));
                    } catch (URISyntaxException e) {
                        throw new NoApplicableCodeException().causedBy(e).withMessage(
                                "The codespace '{}' of the name is not an URI!",
                                foi.getNameCodespace().getName());
                    }
                }
                samplingFeature.setName(codeType);
            }
            return samplingFeature;
        }
    }

    /**
     * Get featureOfInterest object from series
     *
     * @param identifier
     * @return FeatureOfInerest object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected AbstractFeature createFeatureOfInterest(String featureOfInterest)
            throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject(getSession());
        queryObject.setFeatureObject(featureOfInterest).setVersion(getVersion());
        final AbstractFeature feature = getFeatureQueryHandler().getFeatureByID(queryObject);
        return feature;
    }

    protected void checkForAdditionalObservationCreator(DataEntity<?> hObservation, OmObservation sosObservation)
            throws CodedException {
        for (AdditionalObservationCreatorKey key : getAdditionalObservationCreatorKeys(hObservation)) {
            if (getAdditionalObservationCreatorRepository().hasAdditionalObservationCreatorFor(key)) {
                AdditionalObservationCreator creator = getAdditionalObservationCreatorRepository().get(key);
                creator.create(sosObservation, hObservation, getSession());
                break;
            }
        }
        if (checkAcceptType()) {
            for (AdditionalObservationCreatorKey key : getAdditionalObservationCreatorKeys(getAcceptType(),
                    hObservation)) {
                if (getAdditionalObservationCreatorRepository().hasAdditionalObservationCreatorFor(key)) {
                    AdditionalObservationCreator creator = getAdditionalObservationCreatorRepository().get(key);
                    creator.create(sosObservation, hObservation, getSession());
                    break;
                }
            }
        }
    }

    private List<AdditionalObservationCreatorKey> getAdditionalObservationCreatorKeys(DataEntity<?> hObservation) {
        List<AdditionalObservationCreatorKey> keys = new LinkedList<>();
        keys.add(new AdditionalObservationCreatorKey(getResponseFormat(), hObservation.getClass()));
        keys.add(new AdditionalObservationCreatorKey(getResponseFormat(), hObservation.getClass().getSuperclass()));
        keys.add(new AdditionalObservationCreatorKey(null, hObservation.getClass()));
        keys.add(new AdditionalObservationCreatorKey(null, hObservation.getClass().getSuperclass()));
        return keys;
    }

    private List<AdditionalObservationCreatorKey> getAdditionalObservationCreatorKeys(List<MediaType> acceptType,
            DataEntity<?> hObservation) {
        List<AdditionalObservationCreatorKey> keys = new LinkedList<>();
        for (MediaType mediaType : acceptType) {
            keys.add(new AdditionalObservationCreatorKey(mediaType.withoutParameters().toString(),
                    hObservation.getClass()));
            keys.add(new AdditionalObservationCreatorKey(mediaType.withoutParameters().toString(),
                    hObservation.getClass().getSuperclass()));
        }
        return keys;
    }

    public static String checkVersion(AbstractObservationRequest request) {
        return request != null ? request.getVersion() : null;
    }

    protected String queryUnit(DatasetEntity series) {
        if (series.hasUnit()) {
            return series.getUnit().getUnit();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES,
                getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, series.getObservableProperty().getIdentifier());
            namedQuery.setParameter(DatasetEntity.PROPERTY_PROCEDURE, series.getProcedure().getIdentifier());
            LOGGER.debug("QUERY queryUnit({}, {}) with NamedQuery '{}': {}",
                    series.getObservableProperty().getIdentifier(), series.getProcedure().getIdentifier(),
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES,
                getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, series.getObservableProperty().getIdentifier());
            LOGGER.debug("QUERY queryUnit({}) with NamedQuery '{}': {}",
                    series.getObservableProperty().getIdentifier(), SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES,
                    namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        }
        return null;
    }
}

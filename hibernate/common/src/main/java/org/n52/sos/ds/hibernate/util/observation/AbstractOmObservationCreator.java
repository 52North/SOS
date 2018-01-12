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
import java.util.Locale;

import javax.inject.Inject;

import org.hibernate.Session;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
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
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.CodingSettings;
import org.n52.svalbard.encode.EncoderRepository;

import com.google.common.base.Strings;
import org.locationtech.jts.geom.Geometry;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public abstract class AbstractOmObservationCreator {
    private final AbstractObservationRequest request;
    private final Session session;
    private final Locale i18n;
    private final LocalizedProducer<OwsServiceProvider> serviceProvider;
    private String tokenSeparator;
    private String tupleSeparator;
    private String decimalSeparator;
    private EncoderRepository encoderRepository;
    private String pdf;
    private DaoFactory daoFactory;

    public AbstractOmObservationCreator(AbstractObservationRequest request,
                                        Locale i18n,
                                        LocalizedProducer<OwsServiceProvider> serviceProvider,
                                        String pdf,
                                        DaoFactory daoFactory,
                                        Session session) {
        this.request = request;
        this.session = session;
        this.i18n = i18n == null ? ServiceConfiguration.getInstance().getDefaultLanguage() : i18n;
        this.serviceProvider = serviceProvider;
        this.pdf = pdf;
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Setting(CodingSettings.TOKEN_SEPARATOR)
    public void setTokenSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Token separator", separator);
        tokenSeparator = separator;
    }

    @Setting(CodingSettings.TUPLE_SEPARATOR)
    public void setTupleSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Tuple separator", separator);
        tupleSeparator = separator;
    }

    @Setting(CodingSettings.DECIMAL_SEPARATOR)
    public void setDecimalSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Decimal separator", separator);
        decimalSeparator = separator;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    protected SosContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return Configurator.getInstance().getFeatureQueryHandler();
    }

    protected AdditionalObservationCreatorRepository getAdditionalObservationCreatorRepository() {
        return AdditionalObservationCreatorRepository.getInstance();
    }

    protected Profile getActiveProfile() {
        return ProfileHandler.getInstance().getActiveProfile();
    }

    protected String getTokenSeparator() {
        return tokenSeparator;
    }

    protected String getTupleSeparator() {
        return tupleSeparator;
    }

    protected String getDecimalSeparator() {
        return decimalSeparator;
    }

    private ConverterRepository getConverterRepository() {
        return ConverterRepository.getInstance();
    }

    private HibernateProcedureDescriptionGeneratorFactoryRepository getProcedureDescriptionGeneratorFactoryRepository() {
        return HibernateProcedureDescriptionGeneratorFactoryRepository.getInstance();
    }

    private GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }

    protected String getNoDataValue() {
        return getActiveProfile().getResponseNoDataPlaceholder();
    }

    public abstract ObservationStream create() throws OwsExceptionReport,
                                                      ConverterException;

    public String getVersion() {
        return request.getVersion();
    }

    public String getResponseFormat() {
        return request.isSetResponseFormat()
                       ? request.getResponseFormat()
                       : getActiveProfile().getObservationResponseFormat();
    }

    public Session getSession() {
        return session;
    }

    public Locale getI18N() {
        return i18n;
    }

    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(new GeometryValue(getGeometryHandler()
                .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

    protected OmObservableProperty createObservableProperty(ObservableProperty observableProperty) throws CodedException {
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
     * @return Procedure object
     *
     * @throws ConverterException If an error occurs sensor description creation
     * @throws OwsExceptionReport If an error occurs
     */
    protected SosProcedureDescription<?> createProcedure(String identifier)
            throws ConverterException, OwsExceptionReport {
        Procedure hProcedure = getDaoFactory().getProcedureDAO().getProcedureForIdentifier(identifier, getSession());
        String pdf = !Strings.isNullOrEmpty(this.pdf) ? this.pdf
                             : hProcedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
        if (getActiveProfile().isEncodeProcedureInObservation()) {
            return new HibernateProcedureConverter(this.serviceProvider, getDaoFactory(), getConverterRepository(), getProcedureDescriptionGeneratorFactoryRepository())
                    .createSosProcedureDescription(hProcedure, pdf, getVersion(), getSession());
        } else {
            SosProcedureDescriptionUnknownType sosProcedure
                    = new SosProcedureDescriptionUnknownType(identifier, pdf, null);
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
     *
     * @throws CodedException
     */
    protected void addName(AbstractFeature abstractFeature, AbstractIdentifierNameDescriptionEntity hAbstractFeature)
            throws CodedException {
        if (hAbstractFeature.isSetCodespaceName()) {
            try {
                abstractFeature.addName(hAbstractFeature.getName(), new URI(hAbstractFeature.getCodespaceName()
                                        .getCodespace()));
            } catch (URISyntaxException e) {
                throw new NoApplicableCodeException().causedBy(e)
                        .withMessage("Error while creating URI from '{}'", hAbstractFeature.getCodespaceName()
                                     .getCodespace());
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
     * @throws OwsExceptionReport If an error occurs
     */
    protected AbstractFeature createFeatureOfInterest(FeatureOfInterest foi) throws OwsExceptionReport {
        if (getActiveProfile().isEncodeFeatureOfInterestInObservations()) {
            FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
            queryObject.addFeatureIdentifier(foi.getIdentifier()).setConnection(getSession()).setVersion(getVersion());
            final AbstractFeature feature = getFeatureQueryHandler().getFeatureByID(queryObject);
            if (getActiveProfile().getEncodingNamespaceForFeatureOfInterest() != null &&
                     !feature.getDefaultElementEncoding()
                        .equals(getActiveProfile().getEncodingNamespaceForFeatureOfInterest())) {
                feature.setDefaultElementEncoding(getActiveProfile().getEncodingNamespaceForFeatureOfInterest());
            }
            return feature;
        } else {
            SamplingFeature samplingFeature = new SamplingFeature(new CodeWithAuthority(foi.getIdentifier()));
            if (foi.isSetCodespace()) {
                samplingFeature.getIdentifierCodeWithAuthority().setCodeSpace(foi.getCodespace().getCodespace());
            }
            if (foi.isSetName()) {
                CodeType codeType = new CodeType(foi.getName());
                if (foi.isSetCodespaceName()) {
                    try {
                        codeType.setCodeSpace(new URI(foi.getCodespaceName().getCodespace()));
                    } catch (URISyntaxException e) {
                        throw new NoApplicableCodeException().causedBy(e)
                                .withMessage("The codespace '{}' of the name is not an URI!", foi.getCodespaceName()
                                             .getCodespace());
                    }
                }
                samplingFeature.setName(codeType);
            }
            return samplingFeature;
        }
    }

    protected void checkForAdditionalObservationCreator(Observation<?> hObservation, OmObservation sosObservation)
            throws CodedException {
        AdditionalObservationCreatorKey key = new AdditionalObservationCreatorKey(getResponseFormat(), hObservation
                                                                                  .getClass());

        if (getAdditionalObservationCreatorRepository().hasAdditionalObservationCreatorFor(key)) {
            AdditionalObservationCreator creator = getAdditionalObservationCreatorRepository().get(key);
            creator.create(sosObservation, hObservation);
        } else {
            AdditionalObservationCreatorKey key2 = new AdditionalObservationCreatorKey(null, hObservation.getClass());
            if (getAdditionalObservationCreatorRepository().hasAdditionalObservationCreatorFor(key2)) {
                AdditionalObservationCreator creator = getAdditionalObservationCreatorRepository().get(key2);
                creator.add(sosObservation, hObservation);
            }
        }
    }

    public static String checkVersion(AbstractObservationRequest request) {
        return request != null ? request.getVersion() : null;
    }
}

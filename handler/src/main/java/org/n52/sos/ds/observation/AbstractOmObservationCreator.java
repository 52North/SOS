/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.observation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.I18nNameDescriptionAdder;
import org.n52.sos.ds.feature.create.FeatureVisitorContext;
import org.n52.sos.ds.feature.create.FeatureVisitorImpl;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractOmObservationCreator implements I18nNameDescriptionAdder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOmObservationCreator.class);

    private final AbstractObservationRequest request;

    private final Session session;

    private final Locale i18n;

    private final String pdf;

    private final OmObservationCreatorContext creatorContext;

    public AbstractOmObservationCreator(AbstractObservationRequest request, Locale i18n, String pdf,
            OmObservationCreatorContext creatorContext, Session session) {
        this.creatorContext = creatorContext;
        this.request = request;
        this.i18n = i18n == null ? creatorContext.getDefaultLanguage() : i18n;
        this.pdf = pdf;
        this.session = session;
    }

    protected OmObservationCreatorContext getCreatorContext() {
        return creatorContext;
    }

    protected SosContentCache getCache() {
        return getCreatorContext().getCache();
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

    protected ProcedureDescriptionGeneratorFactoryRepository
                getProcedureDescriptionGeneratorFactoryRepository() {
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
        // addMetadata(o);
    }

    public abstract ObservationStream create() throws OwsExceptionReport, ConverterException;

    // private void addMetadata(OmObservation o) {
    // if (MetaDataConfigurations.getInstance().isShowCiOnlineReourceInObservations()) {
    // CiOnlineResource ciOnlineResource = new CiOnlineResource(getServiceURL());
    // ciOnlineResource.setProtocol("OGC:SOS-2.0.0");
    // o.addMetaDataProperty(new GenericMetaData(ciOnlineResource));
    // }
    // }

    protected String getServiceURL() {
        return getCreatorContext().getServiceURL();
    }

    public String getVersion() {
        return getRequest().getVersion();
    }

    public String getService() {
        return getRequest().getService();
    }

    public String getResponseFormat() {
        return getRequest().isSetResponseFormat() ? getRequest().getResponseFormat()
                : getActiveProfile().getObservationResponseFormat();
    }

    public List<MediaType> getAcceptType() {
        return getRequest().getRequestContext().getAcceptType().get();
    }

    public boolean checkAcceptType() {
        return getRequest().getRequestContext() != null && getRequest().getRequestContext().getAcceptType().isPresent();
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

    public AbstractObservationRequest getRequest() {
        return request;
    }

    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry)
            throws OwsExceptionReport {
        return new SpatialFilteringProfileCreator(getGeometryHandler()).create(samplingGeometry);
    }

    protected OmObservableProperty createObservableProperty(PhenomenonEntity observableProperty)
            throws OwsExceptionReport {
        String phenID = observableProperty.getIdentifier();
        String description = observableProperty.getDescription();
        OmObservableProperty omObservableProperty = new OmObservableProperty(phenID, description, null, null);

        if (getRequest().isSetRequestedLanguage()) {
            addNameAndDescription(observableProperty, omObservableProperty,
                    getRequestedLanguage(), getI18N(), false);
            if (omObservableProperty.isSetName()) {
                omObservableProperty.setHumanReadableIdentifier(omObservableProperty.getFirstName().getValue());
            }
        } else {
            if (observableProperty.isSetName()) {
                omObservableProperty.setHumanReadableIdentifier(observableProperty.getName());
                addName(omObservableProperty, observableProperty);
            }
        }
        return omObservableProperty;
    }

    /**
     * Get procedure object from series
     *
     * @param hProcedure
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
        String format = !Strings.isNullOrEmpty(this.pdf) ? this.pdf
                : hProcedure.getFormat() != null && hProcedure.getFormat().isSetFormat()
                        ? hProcedure.getFormat().getFormat()
                        : SensorML20Constants.NS_SML_20;
        if (getActiveProfile().isEncodeProcedureInObservation()) {
            return getCreatorContext().getProcedureConverter().createSosProcedureDescription(hProcedure, format,
                    getVersion(), i18n, getSession());
        } else {
            SosProcedureDescriptionUnknownType sosProcedure =
                    new SosProcedureDescriptionUnknownType(hProcedure.getIdentifier(), format, null);
            if (getRequest().isSetRequestedLanguage()) {
                addNameAndDescription(hProcedure, sosProcedure, getRequestedLanguage(), getI18N(), false);
                if (sosProcedure.isSetName()) {
                    sosProcedure.setHumanReadableIdentifier(sosProcedure.getFirstName().getValue());
                }
            } else {
                if (hProcedure.isSetName()) {
                    sosProcedure.setHumanReadableIdentifier(hProcedure.getName());
                    addName(sosProcedure, hProcedure);
                }
            }
            return sosProcedure;
        }
    }

    protected void addIdentifier(AbstractFeature abstractFeature, DescribableEntity hAbstractFeature) {
        if (hAbstractFeature.isSetIdentifierCodespace()) {
            abstractFeature.setIdentifier(new CodeWithAuthority(hAbstractFeature.getIdentifier(),
                    hAbstractFeature.getIdentifierCodespace().getName()));
        }
        abstractFeature.setIdentifier(new CodeWithAuthority(hAbstractFeature.getIdentifier()));
    }

    protected void addName(AbstractFeature abstractFeature, DescribableEntity hAbstractFeature) throws CodedException {
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
     * @param foi
     *
     * @return FeatureOfInerest object
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected AbstractFeature createFeatureOfInterest(AbstractFeatureEntity foi) throws OwsExceptionReport {
        final AbstractFeature feature = new FeatureVisitorImpl(getFeatureVisitorContext()).visit(foi);
        if (!getActiveProfile().isEncodeFeatureOfInterestInObservations()
                && feature instanceof AbstractSamplingFeature) {
            ((AbstractSamplingFeature) feature).setEncode(false);
        }
        return feature;
    }

    protected FeatureVisitorContext getFeatureVisitorContext() {
        FeatureVisitorContext context = new FeatureVisitorContext().setGeometryHandler(getGeometryHandler())
                .setDefaultLanguage(getI18N())
                .setI18NDAORepository(getI18NDAORepository())
                .setCache(getCache())
                .setActiveProfile(getActiveProfile());
        return context;
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

    public String checkVersion(AbstractObservationRequest request) {
        return getRequest() != null ? getRequest().getVersion() : null;
    }

    protected String queryUnit(DatasetEntity series) {
        if (series.isSetUnit()) {
            return series.getUnit().getUnit();
        }
        return null;
    }

    protected Locale getRequestedLanguage() {
        if (getRequest().isSetRequestedLanguage()) {
            return LocaleHelper.decode(getRequest().getRequestedLanguage());
        }
        return null;
    }

}

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

import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.iso.gmd.CiOnlineResource;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.GenericMetaData;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractOmObservationCreator {
    private final AbstractObservationRequest request;
    private final Session session;
    private final Locale i18n;

    public AbstractOmObservationCreator(AbstractObservationRequest request, Session session) {
        this(request, null, session);
    }

    public AbstractOmObservationCreator(AbstractObservationRequest request, Locale i18n, Session session) {
        this.request = request;
        this.session = session;
        this.i18n = i18n == null ?  ServiceConfiguration.getInstance().getDefaultLanguage() : i18n;
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return Configurator.getInstance().getFeatureQueryHandler();
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    protected String getTokenSeparator() {
        return ServiceConfiguration.getInstance().getTokenSeparator();
    }

    protected String getTupleSeparator() {
        return ServiceConfiguration.getInstance().getTupleSeparator();
    }
    
    protected String getDecimalSeparator() {
        return ServiceConfiguration.getInstance().getDecimalSeparator();
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

    private void addMetadata(OmObservation o) {
        if (MetaDataConfigurations.getInstance().isShowCiOnlineReourceInObservations()) {
            CiOnlineResource ciOnlineResource = new CiOnlineResource(ServiceConfiguration.getInstance().getServiceURL());
            ciOnlineResource.setProtocol("OGC:SOS-2.0.0");
            o.addMetaDataProperty(new GenericMetaData(ciOnlineResource));
        }
    }

    public abstract List<OmObservation> create() throws OwsExceptionReport,
                                                        ConverterException;

    public String getVersion() {
        return request.getVersion();
    }
    
    public String getResponseFormat() {
        if (request.isSetResponseFormat()) {
            return request.getResponseFormat();
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getObservationResponseFormat();
    }

    public Session getSession() {
        return session;
    }

    public Locale getI18N() {
        return i18n;
    }

    @Deprecated
    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry) throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(new GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }


    protected OmObservableProperty createObservableProperty(ObservableProperty observableProperty) {
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
     * @param identifier
     *
     * @return Procedure object
     * @throws ConverterException
     *             If an error occurs sensor description creation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected SosProcedureDescription createProcedure(String identifier) throws ConverterException, OwsExceptionReport {
        Procedure hProcedure = new ProcedureDAO().getProcedureForIdentifier(identifier, getSession());
        String pdf = hProcedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
        if (getActiveProfile().isEncodeProcedureInObservation()) {
            return new HibernateProcedureConverter().createSosProcedureDescription(hProcedure, pdf, getVersion(),
                    getSession());
        } else {
            SosProcedureDescriptionUnknowType sosProcedure =
                    new SosProcedureDescriptionUnknowType(identifier, pdf, null);
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
    protected void addName(AbstractFeature abstractFeature, AbstractIdentifierNameDescriptionEntity hAbstractFeature) {
        if (hAbstractFeature.isSetCodespaceName()) {
            abstractFeature.addName(hAbstractFeature.getName(), hAbstractFeature.getCodespaceName().getCodespace());
        }
        abstractFeature.addName(hAbstractFeature.getName());
        
    }
    
    /**
     * Get featureOfInterest object from series
     *
     * @param identifier
     * @return FeatureOfInerest object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected AbstractFeature createFeatureOfInterest(String identifier) throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
        queryObject.addFeatureIdentifier(identifier).setConnection(getSession()).setVersion(getVersion());
        final AbstractFeature feature =
                getFeatureQueryHandler().getFeatureByID(queryObject);
        return feature;
    }

    protected void checkForAdditionalObservationCreator(Observation<?> hObservation, OmObservation sosObservation) throws CodedException {
        for (AdditionalObservationCreatorKey key : getAdditionalObservationCreatorKeys(hObservation)) {
            if (AdditionalObservationCreatorRepository.getInstance().hasAdditionalObservationCreatorFor(key)) {
                AdditionalObservationCreator<?> creator = AdditionalObservationCreatorRepository.getInstance().get(key);
                creator.create(sosObservation, hObservation, getSession());
                break;
            } 
        }
    }
    
    private List<AdditionalObservationCreatorKey> getAdditionalObservationCreatorKeys(Observation<?> hObservation) {
        List<AdditionalObservationCreatorKey> keys = Lists.newArrayList();
        keys.add(new AdditionalObservationCreatorKey(getResponseFormat(), hObservation.getClass()));
        keys.add(new AdditionalObservationCreatorKey(getResponseFormat(), hObservation.getClass().getSuperclass()));
        keys.add(new AdditionalObservationCreatorKey(null, hObservation.getClass()));
        keys.add(new AdditionalObservationCreatorKey(null, hObservation.getClass().getSuperclass()));
        return keys;
    }

    public static String checkVersion(AbstractObservationRequest request) {
        if (request != null) {
            return request.getVersion();
        }
        return null;
    }
}

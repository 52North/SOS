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
package org.n52.sos.ds.hibernate.util.feature.create;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxyHelper;
import org.n52.sos.ds.hibernate.dao.inspire.EnvironmentalMonitoringFacilityDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.inspire.EnvironmentalMonitoringFacility;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;
import org.n52.svalbard.inspire.ef.ObservingCapability;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EnvironmentalMonitoringFacilityStrategy extends AbstractFeatureCreationStrategy {

    public EnvironmentalMonitoringFacilityStrategy(int storageEPSG, int storage3DEPSG) {
        super(storageEPSG, storage3DEPSG);
    }

    @Override
    public boolean apply(FeatureOfInterest feature) {
        return EnvironmentalMonitoringFacility.class
                .equals(HibernateProxyHelper.getClassWithoutInitializingProxy(feature));
    }

    @Override
    public AbstractFeature create(FeatureOfInterest f, Locale i18n, String version, Session s)
            throws OwsExceptionReport {
        if (f.isSetUrl()) {
            return new org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility(
                    new SimpleAttrs().setHref(f.getUrl()));
        }
        if (f instanceof EnvironmentalMonitoringFacility) {
            EnvironmentalMonitoringFacility emf = (EnvironmentalMonitoringFacility) f;
            EnvironmentalMonitoringFacilityDAO featureDAO = new EnvironmentalMonitoringFacilityDAO();
            final CodeWithAuthority identifier = featureDAO.getIdentifier(emf);
            if (!SosHelper.checkFeatureOfInterestIdentifierForSosV2(emf.getIdentifier(), version)) {
                identifier.setValue(null);
            }
            final org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility emFeature =
                    new org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility(new Identifier(identifier),
                            getMediaMonitored(emf.getMediaMonitored()));
            addNameAndDescription(i18n, emf, emFeature, featureDAO);
            emFeature.setGeometry(createGeometry(emf, s));
            // add measurementRegime, mobile, operationalActivityPeriod(Set)
            emFeature.setMeasurementRegime(new ReferenceType(emf.getMeasurementRegime()));
            emFeature.setMobile(emf.isMobile());

            // in table or from Data
            // emFeature.setOperationalActivityPeriod(operationalActivityPeriod);

            addObservingCapabilities(emFeature, f);
            addHasObservations(emFeature, f);

            // final Set<FeatureOfInterest> parentFeatures = emf.getParents();
            // if (parentFeatures != null && !parentFeatures.isEmpty()) {
            // final List<AbstractFeature> sampledFeatures = new
            // ArrayList<AbstractFeature>(parentFeatures.size());
            // for (final FeatureOfInterest parentFeature : parentFeatures) {
            // sampledFeatures.add(create(parentFeature, i18n, version, s));
            // }
            // emFeature.setSampledFeatures(sampledFeatures);
            // }
            return emFeature;
        }
        return null;
    }

    private Set<ReferenceType> getMediaMonitored(Set<String> mediaMonitored) {
        Set<ReferenceType> referenceTypes = Sets.newHashSetWithExpectedSize(mediaMonitored.size());
        for (String mm : mediaMonitored) {
            referenceTypes.add(new ReferenceType(mm));
        }
        return referenceTypes;
    }

    private void addObservingCapabilities(org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility emFeature,
            FeatureOfInterest feature) {
        emFeature.addObservingCapability(createObservingCapability(feature.getIdentifier()));
        if (feature.hasChilds()) {
            for (FeatureOfInterest child : feature.getChilds()) {
                emFeature.addObservingCapability(createObservingCapability(child.getIdentifier()));
            }
        }

    }

    private ObservingCapability createObservingCapability(String identifier) {
        return new ObservingCapability(addParameter(getGetDataAvailabilityUrl(), "featureOfInterest", identifier));
    }

    private void addHasObservations(org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility emFeature,
            FeatureOfInterest feature) {
        Map<String, Set<String>> featureOfferings = Maps.newHashMap();
        featureOfferings.put(feature.getIdentifier(),
                getCache().getOfferingsForFeatureOfInterest(feature.getIdentifier()));
        // check for child features
        if (feature.hasChilds()) {
            for (FeatureOfInterest child : feature.getChilds()) {
                Set<String> childOfferings = getCache().getOfferingsForFeatureOfInterest(child.getIdentifier());
                if (CollectionHelper.isNotEmpty(childOfferings)) {
                    featureOfferings.put(child.getIdentifier(), childOfferings);
                }
            }
        }
        createHasObservations(emFeature, featureOfferings);
    }

    private void createHasObservations(org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility emFeature,
            Map<String, Set<String>> featureOfferings) {
        if (featureOfferings != null) {
            for (Entry<String, Set<String>> entry : featureOfferings.entrySet()) {
                emFeature.addHasObservation(createObservation(entry.getKey(), entry.getValue()));
            }
        }
    }

    private OmObservation createObservation(String feature, Set<String> offerings) {
        String getObservationUrl = getGetObservationUrl();
        addParameter(getObservationUrl, SosConstants.GetObservationParams.featureOfInterest.name(), feature);
        addParameter(getObservationUrl, SosConstants.GetObservationParams.offering.name(), offerings);
        SimpleAttrs simpleAttrs = new SimpleAttrs().setHref(getObservationUrl);
        OmObservation omObservation = new OmObservation();
        omObservation.setSimpleAttrs(simpleAttrs);
        return omObservation;
    }

    private OmObservation createObservation(String featureOfInterest, String offering) {
        SimpleAttrs simpleAttrs = new SimpleAttrs().setHref(
                addParameter(getGetObservationUrl(), SosConstants.GetObservationParams.offering.name(), offering));
        OmObservation omObservation = new OmObservation();
        omObservation.setSimpleAttrs(simpleAttrs);
        return omObservation;
    }

    private String getGetDataAvailabilityUrl() {
        return new StringBuilder(getBaseGetUrl()).append(getRequest("GetDataAvailability")).toString();
    }

    private String getGetObservationUrl() {
        return new StringBuilder(getBaseGetUrl()).append(getRequest(SosConstants.Operations.GetObservation.name()))
                .toString();
    }

    private String getBaseGetUrl() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceConfiguration().getServiceURL());
        // ?
        url.append('?');
        // service
        url.append(OWSConstants.RequestParams.service.name()).append('=').append(SosConstants.SOS);
        // version
        url.append('&').append(OWSConstants.RequestParams.version.name()).append('=')
                .append(Sos2Constants.SERVICEVERSION);
        return url.toString();
    }

    private String getRequest(String requestName) {
        return new StringBuilder().append('&').append(RequestParams.request.name()).append('=').append(requestName)
                .toString();
    }

    private String addParameter(String url, String parameter, String value) {
        return new StringBuilder(url).append('&').append(parameter).append('=').append(value).toString();
    }

    private String addParameter(String url, String parameter, Set<String> offerings) {
        return new StringBuilder(url).append('&').append(parameter).append('=').append(Joiner.on(',').join(offerings))
                .toString();
    }

    private ServiceConfiguration getServiceConfiguration() {
        return ServiceConfiguration.getInstance();
    }
}

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
package org.n52.sos.ds.hibernate.create;

import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.i18n.HibernateI18NDAO;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.metadata.I18NFeatureMetadata;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public abstract class AbstractFeatureCreator<T extends FeatureOfInterest> implements FeatureCreator<T> {

    private int storageEPSG;
    private int storage3DEPSG;

    public AbstractFeatureCreator(int storageEPSG, int storage3DEPSG) {
        this.storageEPSG = storageEPSG;
        this.storage3DEPSG = storage3DEPSG;
        
    }
    
    protected void addNameAndDescription(Locale requestedLocale, FeatureOfInterest feature,
            AbstractFeature abstractFeature, FeatureOfInterestDAO featureDAO, Session session) throws OwsExceptionReport {
        HibernateI18NDAO<I18NFeatureMetadata> i18nDAO = (HibernateI18NDAO) I18NDAORepository.getInstance().getDAO(I18NFeatureMetadata.class);
        // set name as human readable identifier if set
        if (feature.isSetName()) {
            abstractFeature.setHumanReadableIdentifier(feature.getName());
        }
        if (i18nDAO == null) {
            // no i18n support
            abstractFeature.addName(featureDAO.getName(feature));
            abstractFeature.setDescription(featureDAO.getDescription(feature));
        } else {
            I18NFeatureMetadata i18n = i18nDAO.getMetadata(feature.getIdentifier(), session);
            if (requestedLocale != null) {
                // specific locale was requested
                Optional<LocalizedString> name = i18n.getName().getLocalizationOrDefault(requestedLocale);
                if (name.isPresent()) {
                    abstractFeature.addName(name.get().asCodeType());
                } else {
                    abstractFeature.addName(featureDAO.getName(feature));
                }
                Optional<LocalizedString> description =
                        i18n.getDescription().getLocalizationOrDefault(requestedLocale);
                if (description.isPresent()) {
                    abstractFeature.setDescription(description.get().getText());
                } else {
                    abstractFeature.setDescription(featureDAO.getDescription(feature));
                }
            } else {
                if (ServiceConfiguration.getInstance().isShowAllLanguageValues()) {
                    for (LocalizedString name : i18n.getName()) {
                        abstractFeature.addName(name.asCodeType());
                    }
                } else {
                    Optional<LocalizedString> name = i18n.getName().getDefaultLocalization();
                    if (name.isPresent()) {
                        abstractFeature.addName(name.get().asCodeType());
                    } else {
                        abstractFeature.addName(featureDAO.getName(feature));
                    }
                }
                // choose always the description in the default locale
                Optional<LocalizedString> description = i18n.getDescription().getDefaultLocalization();
                if (description.isPresent()) {
                    abstractFeature.setDescription(description.get().getText());
                }
            }
        }
    }
    
    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     * @return geometry
     * @throws OwsExceptionReport
     */
    protected Geometry createGeometryFrom(FeatureOfInterest feature, Session session) throws OwsExceptionReport {
        if (feature.isSetGeometry()) {
            return GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(feature.getGeom());
        } else if (feature.isSetLongLat()) {
            return getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                    new HibernateGeometryCreator(storageEPSG, storage3DEPSG).createGeometry(feature));
        } else {
            if (!feature.isSetUrl() && session != null) {
                if (createFeatureGeometryFromSamplingGeometries()) {
                    int srid = getGeometryHandler().getStorageEPSG();
                    if (DaoFactory.getInstance().getObservationDAO().getSamplingGeometriesCount(feature.getIdentifier(), session).longValue() < 100) {
                        List<Geometry> geometries = DaoFactory.getInstance().getObservationDAO().getSamplingGeometries(feature.getIdentifier(), session);
                        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(geometries)) {
                            List<Coordinate> coordinates = Lists.newLinkedList();
                            Geometry lastGeoemtry = null;
                            for (Geometry geometry : geometries) {
                                if (geometry != null && (lastGeoemtry == null || !geometry.equalsTopo(lastGeoemtry))) {
                                        coordinates.add(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry).getCoordinate());
                                    lastGeoemtry = geometry;
                                    if (geometry.getSRID() != srid) {
                                        srid = geometry.getSRID();
                                     }
                                }
                                if (geometry.getSRID() != srid) {
                                   srid = geometry.getSRID();
                                }
                                if (!geometry.equalsTopo(lastGeoemtry)) {
                                    coordinates.add(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry).getCoordinate());
                                    lastGeoemtry = geometry;
                                }
                            }
                            Geometry geom = null;
                            if (coordinates.size() == 1) {
                                geom = new GeometryFactory().createPoint(coordinates.iterator().next());
                            } else {
                                geom = new GeometryFactory().createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
                            }
                            geom.setSRID(srid);
                            return geom;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    protected GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }
    
    protected ContentCache getCache() {
        return getConfigurator().getCache();
    }
    
    protected Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    protected int getStorageEPSG() {
        return storageEPSG;
    }

    protected int getStorage3DEPSG() {
        return storage3DEPSG;
    }
    
    private boolean createFeatureGeometryFromSamplingGeometries() {
        return ServiceConfiguration.getInstance().isCreateFeatureGeometryFromSamplingGeometries() && !ServiceConfiguration.getInstance().isUpdateFeatureGeometry();
    }
    
}

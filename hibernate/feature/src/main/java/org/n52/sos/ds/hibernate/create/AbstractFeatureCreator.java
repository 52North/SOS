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
package org.n52.sos.ds.hibernate.create;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.metadata.I18NFeatureMetadata;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.util.JTSConverter;

import com.google.common.collect.Lists;


public abstract class AbstractFeatureCreator<T extends FeatureEntity>
        implements FeatureCreator<T> {

    public static final String CREATE_FOI_GEOM_FROM_SAMPLING_GEOMS =
            "service.createFeatureGeometryFromSamplingGeometries";
    private FeatureVisitorContext context;

    public AbstractFeatureCreator(FeatureVisitorContext context) {
        this.context = context;
    }

    public CodeWithAuthority getIdentifier(DescribableEntity entity) {
        CodeWithAuthority identifier = new CodeWithAuthority(entity.getIdentifier());
        if (entity.isSetIdentifierCodespace()) {
            identifier.setCodeSpace(entity.getIdentifierCodespace().getName());
        }
        return identifier;
    }

    protected void addNameAndDescription(Locale requestedLocale, FeatureEntity feature,
            AbstractFeature abstractFeature) throws OwsExceptionReport {
        I18NDAO<I18NFeatureMetadata> i18nDAO = getContext().getI18NDAORepository().getDAO(I18NFeatureMetadata.class);
        // set name as human readable identifier if set
        if (feature.isSetName()) {
            abstractFeature.setHumanReadableIdentifier(feature.getName());
        }
        if (i18nDAO == null) {
            // no i18n support
            abstractFeature.addName(getName(feature));
            abstractFeature.setDescription(getDescription(feature));
        } else {
            if (requestedLocale != null) {
                // specific locale was requested
                I18NFeatureMetadata i18n = i18nDAO.getMetadata(feature.getIdentifier(), requestedLocale);
                Optional<LocalizedString> name = i18n.getName().getLocalization(requestedLocale);
                if (name.isPresent()) {
                    abstractFeature.addName(new CodeType(name.get()));
                }
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(requestedLocale);
                if (description.isPresent()) {
                    abstractFeature.setDescription(description.get().getText());
                }
            } else {
                final I18NFeatureMetadata i18n;
                if (getContext().isShowAllLanguages()) {
                    // load all names
                    i18n = i18nDAO.getMetadata(feature.getIdentifier());
                } else {
                    // load only name in default locale
                    i18n = i18nDAO.getMetadata(feature.getIdentifier(), getContext().getDefaultLanguage());
                }
                for (LocalizedString name : i18n.getName()) {
                    // either all or default only
                    abstractFeature.addName(new CodeType(name));
                }
                // choose always the description in the default locale
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(getContext().getDefaultLanguage());
                if (description.isPresent()) {
                    abstractFeature.setDescription(description.get().getText());
                }
            }
        }
    }

    protected CodeType getName(DescribableEntity entity) throws OwsExceptionReport {
        if (entity.isSetName()) {
            CodeType name = new CodeType(entity.getName());
            if (entity.isSetNameCodespace()) {
                try {
                    name.setCodeSpace(new URI(entity.getNameCodespace().getName()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating URI from '{}'",
                            entity.getNameCodespace().getName());
                }
            }
            return name;
        }
        return null;
    }

    protected String getDescription(DescribableEntity entity) {
        if (entity.isSetDescription()) {
            return entity.getDescription();
        }
        return null;
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     * @return geometry
     * @throws OwsExceptionReport
     */
    protected Geometry createGeometryFrom(FeatureEntity feature) throws OwsExceptionReport {
        if (feature.isSetGeometry()) {
            return getContext().getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(JTSConverter.convert(feature.getGeometryEntity().getGeometry()));
        } else {
            if (!feature.isSetUrl() && getContext().getSession() != null) {
                if (getContext().createFeatureGeometryFromSamplingGeometries()) {
                    int srid = getContext().getGeometryHandler().getStorageEPSG();
                    if (getContext().getDaoFactory().getObservationDAO().getSamplingGeometriesCount(feature.getIdentifier(), getContext().getSession())
                            .longValue() < 100) {
                        List<Geometry> geometries =
                                getContext().getDaoFactory().getObservationDAO().getSamplingGeometries(feature.getIdentifier(), getContext().getSession());
                        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(geometries)) {
                            List<Coordinate> coordinates = Lists.newLinkedList();
                            Geometry lastGeoemtry = null;
                            for (Geometry geometry : geometries) {
                                if (geometry != null && (lastGeoemtry == null || !geometry.equalsTopo(lastGeoemtry))) {
                                    coordinates.add(getContext().getGeometryHandler()
                                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry).getCoordinate());
                                    lastGeoemtry = geometry;
                                    if (geometry.getSRID() != srid) {
                                        srid = geometry.getSRID();
                                    }
                                }
                                if (geometry.getSRID() != srid) {
                                    srid = geometry.getSRID();
                                }
                                if (!geometry.equalsTopo(lastGeoemtry)) {
                                    coordinates.add(getContext().getGeometryHandler()
                                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry).getCoordinate());
                                    lastGeoemtry = geometry;
                                }
                            }
                            Geometry geom = null;
                            if (coordinates.size() == 1) {
                                geom = new GeometryFactory().createPoint(coordinates.iterator().next());
                            } else {
                                geom = new GeometryFactory()
                                        .createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
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

    protected FeatureVisitorContext getContext() {
        return context;
    }
}

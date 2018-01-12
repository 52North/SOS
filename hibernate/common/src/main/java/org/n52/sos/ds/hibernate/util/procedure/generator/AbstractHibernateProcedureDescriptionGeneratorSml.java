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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.elements.SmlPosition;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.request.ProcedureRequestSettingProvider;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.CodingSettings;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Coordinate;

/**
 * Abstract generator class for SensorML procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.2.0
 *
 */
public abstract class AbstractHibernateProcedureDescriptionGeneratorSml
        extends AbstractHibernateProcedureDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateProcedureDescriptionGeneratorSml.class);
    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY = "getUnitForObservableProperty";
    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE = "getUnitForObservablePropertyProcedure";
    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING = "getUnitForObservablePropertyProcedureOffering";

    protected static final String POSITION_NAME = "sensorPosition";

    private final ProfileHandler profileHandler;
    private final EntitiyHelper entitiyHelper;
    private final GeometryHandler geometryHandler;

    private String srsNamePrefixUrl;
    private boolean addOutputsToSensorML;
    private String latLongUom;
    private String altitudeUom;

    public AbstractHibernateProcedureDescriptionGeneratorSml(ProfileHandler profileHandler,
                                                             EntitiyHelper entitiyHelper,
                                                             GeometryHandler geometryHandler,
                                                             DaoFactory daoFactory,
                                                             I18NDAORepository i18NDAORepository,
                                                             ContentCacheController cacheController) {
        super(daoFactory, i18NDAORepository, cacheController);
        this.profileHandler = profileHandler;
        this.entitiyHelper = entitiyHelper;
        this.geometryHandler = geometryHandler;
    }

    @Setting(ProcedureDescriptionSettings.ALTITUDE_UOM)
    public void setAltitudeUom(String altitudeUom) {
        this.altitudeUom = altitudeUom;
    }

    @Setting(ProcedureDescriptionSettings.LAT_LONG_UOM)
    public void setLatLongUom(String latLongUom) {
        this.latLongUom = latLongUom;
    }

    @Setting(ProcedureRequestSettingProvider.ADD_OUTPUTS_TO_SENSOR_ML)
    public void setAddOutputsToSensorML(boolean addOutputsToSensorML) {
        this.addOutputsToSensorML = addOutputsToSensorML;
    }

    @Setting(CodingSettings.SRS_NAME_PREFIX_URL)
    public void setSrsNamePrefixUrl(String srsNamePrefixUrl) {
        this.srsNamePrefixUrl = srsNamePrefixUrl;
    }

    /**
     * Set common values to procedure description
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param abstractProcess
     *            SensorML process
     * @param session
     *            the session
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected void setCommonValues(Procedure procedure, AbstractProcess abstractProcess, Session session)
            throws OwsExceptionReport {
        setCommonData(procedure, abstractProcess, session);

        String identifier = procedure.getIdentifier();
        String[] observableProperties = getObservablePropertiesForProcedure(identifier);

        // 3 set identification
        abstractProcess.setIdentifications(createIdentifications(identifier));

        // 7 set inputs/outputs --> observableProperties
        if (this.addOutputsToSensorML
                && !"hydrology".equalsIgnoreCase(profileHandler.getActiveProfile().getIdentifier())) {
            TreeSet<String> obsProps = Sets.newTreeSet(Arrays.asList(observableProperties));
            abstractProcess.setInputs(createInputs(obsProps));
            abstractProcess.setOutputs(createOutputs(procedure, obsProps, session));
        }
    }

    private List<SmlIo> createInputs(Set<String> observableProperties) throws OwsExceptionReport {
        final List<SmlIo> inputs = Lists.newArrayListWithExpectedSize(observableProperties.size());
        int i = 1;
        for (String observableProperty : observableProperties) {
            inputs.add(new SmlIo().setIoName("input#" + i++).setIoValue(getInputComponent(observableProperty)));
        }
        return inputs;
    }

    protected abstract SweAbstractDataComponent getInputComponent(String observableProperty);

    /**
     * Create SensorML output list from observableProperties
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param observableProperties
     *            Properties observed by the procedure
     *
     * @return Output list
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<SmlIo> createOutputs(Procedure procedure, Set<String> observableProperties, Session session)
            throws OwsExceptionReport {
        try {
            final List<SmlIo> outputs = Lists.newArrayListWithExpectedSize(observableProperties.size());
            int i = 1;
            final boolean supportsObservationConstellation =
                    HibernateHelper.isEntitySupported(ObservationConstellation.class);
                for (String observableProperty : observableProperties) {
                    final SmlIo output;
                    if (supportsObservationConstellation) {
                        output =
                                createOutputFromObservationConstellation(procedure.getIdentifier(), observableProperty,
                                        session);
                    } else {
                        output =
                                createOutputFromExampleObservation(procedure.getIdentifier(), observableProperty, session);
                    }
                    if (output != null) {
                        output.setIoName("output#" + i++);
                        outputs.add(output);
                    }
            }
            return outputs;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private SmlIo createOutputFromObservationConstellation(String procedure, String observableProperty,
            Session session) throws OwsExceptionReport {
        List<ObservationConstellation> observationConstellations =
                getDaoFactory().getObservationConstellationDAO()
                .getObservationConstellations(procedure, observableProperty, session);
        if (CollectionHelper.isNotEmpty(observationConstellations)) {
            ObservationConstellation oc = observationConstellations.iterator().next();
            String unit = queryUnit(oc, session);
            if (oc.isSetObservationType()) {
                final String observationType = oc.getObservationType().getObservationType();
                if (null != observationType) {
                    switch (observationType) {
                        case OmConstants.OBS_TYPE_MEASUREMENT:
                            final SweQuantity quantity = new SweQuantity();
                            quantity.setDefinition(observableProperty);
                            if (!Strings.isNullOrEmpty(unit)) {
                                quantity.setUom(unit);
                            }
                            return new SmlIo(quantity);
                        case OmConstants.OBS_TYPE_CATEGORY_OBSERVATION:
                            final SweCategory category = new SweCategory();
                            category.setDefinition(observableProperty);
                            if (!Strings.isNullOrEmpty(unit)) {
                                category.setUom(unit);
                            }
                            return new SmlIo(category);
                        case OmConstants.OBS_TYPE_COUNT_OBSERVATION:
                            return new SmlIo(new SweCategory().setDefinition(observableProperty));
                        case OmConstants.OBS_TYPE_TEXT_OBSERVATION:
                            return new SmlIo(new SweText().setDefinition(observableProperty));
                        case OmConstants.OBS_TYPE_TRUTH_OBSERVATION:
                            return new SmlIo(new SweBoolean().setDefinition(observableProperty));
                        case OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION:
                        // TODO implement GeometryObservation
                        case OmConstants.OBS_TYPE_COMPLEX_OBSERVATION:
                        // TODO implement ComplexObservation
                        case OmConstants.OBS_TYPE_UNKNOWN:
                        // TODO implement UnknownObservation
                        case OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION:
                        // TODO implement SWEArrayObservation
                        default:
                            logTypeNotSupported(observationType);
                    }
                }
            }
        }
        return null;
    }
    private String queryUnit(ObservationConstellation oc, Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING,
                session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(ObservationConstellation.PROCEDURE, oc.getProcedure().getIdentifier());
            namedQuery.setParameter(ObservationConstellation.OFFERING, oc.getOffering().getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper
                .isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(ObservationConstellation.PROCEDURE, oc.getProcedure().getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            return (String) namedQuery.uniqueResult();
        } else if (entitiyHelper.isSeriesSupported()) {
            List<Series> series = getDaoFactory().getSeriesDAO().getSeries(Lists.newArrayList(oc.getProcedure().getIdentifier()), Lists.newArrayList(oc.getObservableProperty().getIdentifier()), Lists.<String>newArrayList(), session);
            if (series.iterator().hasNext()) {
                Series next = series.iterator().next();
                if (next.isSetUnit() ) {
                    return next.getUnit().getUnit();
                }
            }
        }
        AbstractObservation<?> exampleObservation = getExampleObservation(oc.getProcedure().getIdentifier(),
                                                                          oc.getObservableProperty().getIdentifier(),
                                                                          session);
        if (exampleObservation != null && exampleObservation.isSetUnit()) {
            return exampleObservation.getUnit().getUnit();
        }
        return null;
    }

    /**
     * Logger method for class
     *
     * @param clazz
     *            Name of not supported class
     */
    private void logTypeNotSupported(Class<?> clazz) {
        LOGGER.debug("Type '{}' is not supported by the current implementation", clazz.getName());
    }

    /**
     * Logger method for class
     *
     * @param observationType
     *            Name of not supported class
     */
    private void logTypeNotSupported(String observationType) {
        LOGGER.debug("ObservationType '{}' is not supported by the current implementation", observationType);
    }

    private SmlIo createOutputFromExampleObservation(String procedure, String observableProperty, Session session)
            throws OwsExceptionReport {
        AbstractObservation<?> exampleObservation = getExampleObservation(procedure, observableProperty, session);
        if (exampleObservation == null) {
            return null;
        }
        if (exampleObservation instanceof BlobObservation) {
            // TODO implement BlobObservations
            logTypeNotSupported(BlobObservation.class);
        } else if (exampleObservation instanceof BooleanObservation) {
            return new SmlIo(new SweBoolean().setDefinition(observableProperty));
        } else if (exampleObservation instanceof CategoryObservation) {
            final SweCategory category = new SweCategory();
            category.setDefinition(observableProperty);
            if (exampleObservation.isSetUnit()) {
                category.setUom(exampleObservation.getUnit().getUnit());
            }
            return new SmlIo(category);
        } else if (exampleObservation instanceof CountObservation) {
            return new SmlIo(new SweCount().setDefinition(observableProperty));
        } else if (exampleObservation instanceof GeometryObservation) {
            // TODO implement GeometryObservations
            logTypeNotSupported(GeometryObservation.class);
        } else if (exampleObservation instanceof NumericObservation) {
            final SweQuantity quantity = new SweQuantity();
            quantity.setDefinition(observableProperty);
            if (exampleObservation.isSetUnit()) {
                quantity.setUom(exampleObservation.getUnit().getUnit());
            }
            return new SmlIo(quantity);
        } else if (exampleObservation instanceof TextObservation) {
            return new SmlIo(new SweText().setDefinition(observableProperty));
        }
        return null;
    }
    /**
     * Create SensorML Position from Hibernate procedure entity
     *
     * @param procedure
     *            Hibernate procedure entity
     *
     * @return SensorML Position
     */
    protected SmlPosition createPosition(Procedure procedure) {
        SmlPosition position = new SmlPosition();
        position.setName(POSITION_NAME);
        position.setFixed(true);
        int srid = geometryHandler.getDefaultResponseEPSG();
        if (procedure.isSetLongLat()) {
            // 8.1 set latlong position
            position.setPosition(createCoordinatesForPosition(procedure.getLongitude(), procedure.getLatitude(),
                    procedure.getAltitude()));

        } else if (procedure.isSetGeometry()) {
            // 8.2 set position from geometry
            if (procedure.getGeom().getSRID() > 0) {
                srid = procedure.getGeom().getSRID();
            }
            final Coordinate c = procedure.getGeom().getCoordinate();
            position.setPosition(createCoordinatesForPosition(c.y, c.x, c.z));
        }
        if (procedure.isSetSrid()) {
            srid = procedure.getSrid();
        }
        position.setReferenceFrame(srsNamePrefixUrl + srid);
        return position;
    }

    /**
     * Create SWE Coordinates for SensorML Position
     *
     * @param longitude
     *            Longitude value
     * @param latitude
     *            Latitude value
     * @param altitude
     *            Altitude value
     *
     * @return List with SWE Coordinate
     */
    private List<SweCoordinate<Double>> createCoordinatesForPosition(Object longitude, Object latitude, Object altitude) {
        SweAbstractSimpleType<Double> yq = createSweQuantity(latitude, SweConstants.Y_AXIS, latLongUom);
        SweAbstractSimpleType<Double> xq = createSweQuantity(longitude, SweConstants.X_AXIS, latLongUom);
        SweAbstractSimpleType<Double> zq = createSweQuantity(altitude, SweConstants.Z_AXIS, altitudeUom);
        return Lists.newArrayList(
                new SweCoordinate<>(SweCoordinateNames.NORTHING, yq),
                new SweCoordinate<>(SweCoordinateNames.EASTING, xq),
                new SweCoordinate<>(SweCoordinateNames.ALTITUDE, zq));
    }

    /**
     * Create SWE Quantity for SWE coordinate
     *
     * @param value
     *            Value
     * @param axis
     *            Axis id
     * @param uom
     *            UnitOfMeasure
     *
     * @return SWE Quantity
     */
    private SweQuantity createSweQuantity(Object value, String axis, String uom) {
        return new SweQuantity().setAxisID(axis).setUom(uom).setValue(JavaHelper.asDouble(value));
    }

    private List<SmlIdentifier> createIdentifications(String identifier) {
        return Lists.newArrayList(createIdentifier(identifier));
    }

    private SmlIdentifier createIdentifier(String identifier) {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                                 OGCConstants.URN_UNIQUE_IDENTIFIER,
                                 identifier);
    }

}

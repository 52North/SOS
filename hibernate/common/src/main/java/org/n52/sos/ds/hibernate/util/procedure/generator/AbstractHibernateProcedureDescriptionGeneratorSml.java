/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Coordinate;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureEntity;
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
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweObservableProperty;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.CodingSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

    public static final String ADD_OUTPUTS_TO_SENSOR_ML = "service.addOutputsToSensorML";

    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY = "getUnitForObservableProperty";

    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE =
            "getUnitForObservablePropertyProcedure";

    public static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING =
            "getUnitForObservablePropertyProcedureOffering";

    protected static final String POSITION_NAME = "sensorPosition";

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractHibernateProcedureDescriptionGeneratorSml.class);

    private static final String QUERY_LOG_TEMPLATE = "QUERY queryUnit(observationConstellation) with NamedQuery: {}";

    private final ProfileHandler profileHandler;

    private final GeometryHandler geometryHandler;

    private String srsNamePrefixUrl;

    private boolean addOutputsToSensorML;

    private String latLongUom;

    private String altitudeUom;

    public AbstractHibernateProcedureDescriptionGeneratorSml(ProfileHandler profileHandler,
            GeometryHandler geometryHandler, DaoFactory daoFactory, I18NDAORepository i18NDAORepository,
            ContentCacheController cacheController) {
        super(daoFactory, i18NDAORepository, cacheController);
        this.profileHandler = profileHandler;
        this.geometryHandler = geometryHandler;
    }

    @Setting(ADD_OUTPUTS_TO_SENSOR_ML)
    public void setAddOutputsToSensorML(boolean addOutputsToSensorML) {
        this.addOutputsToSensorML = addOutputsToSensorML;
    }

    @Setting(CodingSettings.SRS_NAME_PREFIX_URL)
    public void setSrsNamePrefixUrl(String srsNamePrefixUrl) {
        this.srsNamePrefixUrl = srsNamePrefixUrl;
    }

    @Setting(ProcedureDescriptionSettings.LAT_LONG_UOM)
    public void setLatitudeUom(final String latLongUom) {
        this.latLongUom = latLongUom;
    }

    @Setting(ProcedureDescriptionSettings.ALTITUDE_UOM)
    public void setAltitudeUom(final String altitudeUom) {
        this.altitudeUom = altitudeUom;
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
    protected void setCommonValues(ProcedureEntity procedure, AbstractProcess abstractProcess, Session session)
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
    private List<SmlIo> createOutputs(ProcedureEntity procedure, Set<String> observableProperties, Session session)
            throws OwsExceptionReport {
        try {
            final List<SmlIo> outputs = Lists.newArrayListWithExpectedSize(observableProperties.size());
            int i = 1;
            for (String observableProperty : observableProperties) {
                final SmlIo output = createOutputFromDataset(procedure.getIdentifier(), observableProperty, session);
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

    private SmlIo createOutputFromDataset(String procedure, String observableProperty, Session session)
            throws OwsExceptionReport {
        List<DatasetEntity> observationConstellations =
                getDaoFactory().getSeriesDAO().getSeries(procedure, observableProperty, session);
        if (CollectionHelper.isNotEmpty(observationConstellations)) {
            DatasetEntity oc = observationConstellations.iterator().next();
            String unit = queryUnit(oc, session);
            if (oc.isSetOMObservationType()) {
                final String observationType = oc.getOmObservationType().getFormat();
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
                return new SmlIo(new SweObservableProperty().setDefinition(observableProperty));
            }
        }
        return null;
    }

    private String queryUnit(DatasetEntity oc, Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING,
                session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, oc.getObservableProperty().getIdentifier());
            namedQuery.setParameter(DatasetEntity.PROPERTY_PROCEDURE, oc.getProcedure().getIdentifier());
            namedQuery.setParameter(DatasetEntity.PROPERTY_OFFERING, oc.getOffering().getIdentifier());
            LOGGER.debug(QUERY_LOG_TEMPLATE, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE,
                session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, oc.getObservableProperty().getIdentifier());
            namedQuery.setParameter(DatasetEntity.PROPERTY_PROCEDURE, oc.getProcedure().getIdentifier());
            LOGGER.debug(QUERY_LOG_TEMPLATE, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, oc.getObservableProperty().getIdentifier());
            LOGGER.debug(QUERY_LOG_TEMPLATE, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            return (String) namedQuery.uniqueResult();
        } else {
            List<DatasetEntity> series =
                    getDaoFactory().getSeriesDAO().getSeries(Lists.newArrayList(oc.getProcedure().getIdentifier()),
                            Lists.newArrayList(oc.getObservableProperty().getIdentifier()),
                            Lists.<String> newArrayList(), session);
            if (series.iterator().hasNext()) {
                DatasetEntity next = series.iterator().next();
                if (next.isSetUnit()) {
                    return next.getUnit().getUnit();
                }
            }
        }
        return null;
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

    /**
     * Create SensorML Position from Hibernate procedure entity
     *
     * @param procedure
     *            Hibernate procedure entity
     *
     * @return SensorML Position
     */
    protected SmlPosition createPosition(ProcedureEntity procedure) {
        return createPosition(procedure, false);
    }

    /**
     * Create SensorML Position from Hibernate procedure entity
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param vector
     *            Flag to indicate that the geometry should be defined as a
     *            vector
     *
     * @return SensorML Position
     */
    protected SmlPosition createPosition(ProcedureEntity procedure, boolean vector) {
        SmlPosition position = new SmlPosition();
        position.setName(POSITION_NAME);
        position.setFixed(true);
        int srid = geometryHandler.getDefaultResponseEPSG();
        if (procedure.isSetGeometry()) {
            if (procedure.getGeometry().getSRID() > 0) {
                srid = procedure.getGeometry().getSRID();
            }
            final Coordinate c = procedure.getGeometry().getCoordinate();
            if (vector) {
                position.setVector(createVectorForPosition(createCoordinatesForPosition(c.getY(), c.getX(), c.getZ()),
                        srsNamePrefixUrl + srid));
            } else {
                position.setPosition(createCoordinatesForPosition(c.getY(), c.getX(), c.getZ()));
            }
        }
        if (procedure.getGeometry().getSRID() > 0) {
            srid = procedure.getGeometry().getSRID();
        }
        position.setReferenceFrame(srsNamePrefixUrl + srid);
        return position;
    }

    private SweVector createVectorForPosition(List<SweCoordinate<?>> coordinates, String referenceFrame) {
        SweVector vector = new SweVector(coordinates);
        vector.setDefinition(POSITION_NAME);
        vector.setReferenceFrame(referenceFrame);
        return vector;
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
    private List<SweCoordinate<?>> createCoordinatesForPosition(Object longitude, Object latitude, Object altitude) {
        SweQuantity yq = createSweQuantity(latitude, SweConstants.Y_AXIS, latLongUom);
        SweQuantity xq = createSweQuantity(longitude, SweConstants.X_AXIS, latLongUom);
        SweQuantity zq = createSweQuantity(altitude, SweConstants.Z_AXIS, altitudeUom);
        // TODO add Integer: Which SweSimpleType to use?
        return Lists.<SweCoordinate<?>> newArrayList(
                new SweCoordinate<BigDecimal>(SweConstants.SweCoordinateNames.NORTHING, yq),
                new SweCoordinate<BigDecimal>(SweConstants.SweCoordinateNames.EASTING, xq),
                new SweCoordinate<BigDecimal>(SweConstants.SweCoordinateNames.ALTITUDE, zq));
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
        return new SweQuantity().setAxisID(axis).setUom(uom).setValue(BigDecimal.valueOf(JavaHelper.asDouble(value)));
    }

    private List<SmlIdentifier> createIdentifications(String identifier) {
        return Lists.newArrayList(createIdentifier(identifier));
    }

    private SmlIdentifier createIdentifier(String identifier) {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END, OGCConstants.URN_UNIQUE_IDENTIFIER,
                identifier);
    }

}

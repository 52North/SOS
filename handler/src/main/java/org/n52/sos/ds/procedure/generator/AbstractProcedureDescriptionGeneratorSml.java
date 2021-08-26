/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure.generator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.io.request.IoParameters;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.series.db.dao.DatasetDao;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.FeatureDao;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.ows.exception.CodedException;
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
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract generator class for SensorML procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.2.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractProcedureDescriptionGeneratorSml extends AbstractProcedureDescriptionGenerator {

    protected static final String POSITION_NAME = "sensorPosition";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcedureDescriptionGeneratorSml.class);

    private GeometryHandler geometryHandler;

    private String srsNamePrefix;

    private ProfileHandler profileHandler;

    private boolean isAddOutputsToSensorML;

    public AbstractProcedureDescriptionGeneratorSml(ProfileHandler profileHandler, GeometryHandler geometryHandler,
            I18NDAORepository i18NDAORepository, ContentCacheController cacheController, String srsNamePrefix,
            boolean isAddOutputsToSensorML) {
        super(i18NDAORepository, cacheController);
        this.geometryHandler = geometryHandler;
        this.srsNamePrefix = srsNamePrefix;
        this.profileHandler = profileHandler;
        this.isAddOutputsToSensorML = isAddOutputsToSensorML;
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
        List<PhenomenonEntity> observableProperties = getObservablePropertiesForProcedure(procedure, session);
        // 3 set identification
        abstractProcess.setIdentifications(createIdentifications(identifier));

        // 7 set inputs/outputs --> observableProperties
        if (isAddOutputsToSensorML
                && !"hydrology".equalsIgnoreCase(profileHandler.getActiveProfile().getIdentifier())) {
            abstractProcess.setInputs(createInputs(getIdentifierList(observableProperties)));
            abstractProcess.setOutputs(createOutputs(procedure, observableProperties, session));
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
    private List<SmlIo> createOutputs(ProcedureEntity procedure, List<PhenomenonEntity> observableProperties,
            Session session) throws OwsExceptionReport {
        try {
            final List<SmlIo> outputs = Lists.newArrayListWithExpectedSize(observableProperties.size());
            int i = 1;
            for (PhenomenonEntity observableProperty : observableProperties) {
                final SmlIo output = createOutputFromDatasets(procedure, observableProperty, session);
                if (output != null) {
                    output.setIoName("output#" + i++);
                    outputs.add(output);
                }
            }
            return outputs;
        } catch (final HibernateException | DataAccessException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logger method for class
     *
     * @param observationType
     *            Name of not supported class
     */
    private void logTypeNotSupported(ValueType observationType) {
        LOGGER.debug("ObservationType '{}' is not supported by the current implementation", observationType.name());
    }

    @SuppressWarnings("rawtypes")
    private SmlIo createOutputFromDatasets(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            Session session) throws DataAccessException {
        DatasetDao<DatasetEntity> datasetDao = new DatasetDao<>(session);
        List<DatasetEntity> allInstances = datasetDao.getAllInstances(createDbQuery(procedure, observableProperty));
        if (allInstances == null) {
            return null;
        }
        DatasetEntity dataset = allInstances.iterator().next();
        SweAbstractSimpleType simpleType = null;
        switch (dataset.getValueType()) {
            case quantity:
                final SweQuantity quantity = new SweQuantity();
                if (dataset.getUnit() != null) {
                    quantity.setUom(dataset.getUnit().getName());
                }
                simpleType = quantity;
                break;
            case bool:
                simpleType = new SweBoolean();
                break;
            case count:
                simpleType = new SweCount();
                break;
            case text:
                simpleType = new SweText();
                break;
            case category:
                final SweCategory category = new SweCategory();
                if (dataset.getUnit() != null) {
                    category.setUom(dataset.getUnit().getIdentifier());
                }
                simpleType = category;
                break;
            default:
                logTypeNotSupported(dataset.getValueType());
                break;
        }
        if (simpleType != null) {
            simpleType.setDefinition(observableProperty.getIdentifier());
            if (observableProperty.isSetName()) {
                simpleType.setName(observableProperty.getName());
            }
            if (observableProperty.isSetDescription()) {
                simpleType.setDescription(observableProperty.getDescription());
            }
            return new SmlIo(simpleType);
        }
        return null;
    }

    private DbQuery createDbQuery(ProcedureEntity procedure, PhenomenonEntity observableProperty) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));
        map.put(IoParameters.PHENOMENA, Long.toString(observableProperty.getId()));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private DbQuery createDbQuery(ProcedureEntity procedure) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private DbQuery createDbQueryWithLimit(ProcedureEntity procedure) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));
        map.put(IoParameters.LIMIT, Long.toString(1));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    protected boolean isStation(ProcedureEntity procedure, Session session) throws DataAccessException {
        List<DatasetEntity> datasets = new DatasetDao<>(session).getAllInstances(createDbQueryWithLimit(procedure));
        if (datasets != null && !datasets.isEmpty()) {
            DatasetEntity dataset = datasets.iterator().next();
            if (dataset.isInsitu() && !dataset.isMobile()) {
                List<FeatureEntity> features = new FeatureDao(session).getAllInstances(createDbQuery(procedure));
                if (features != null && features.size() == 1) {
                    return features.iterator().next().isSetGeometry();
                }
            }
        }
        return false;
    }

    /**
     * Create SensorML Position from Hibernate procedure entity
     *
     * @param procedure
     *            Hibernate procedure entity
     *
     * @return SensorML Position
     * @throws CodedException If an error occurs
     */
    protected SmlPosition createPosition(ProcedureEntity procedure, Session session) throws CodedException {
        try {
            List<FeatureEntity> features = new FeatureDao(session).getAllInstances(createDbQuery(procedure));
            SmlPosition position = new SmlPosition();
            position.setName(POSITION_NAME);
            position.setFixed(true);
            int srid = geometryHandler.getDefaultResponseEPSG();
            if (features != null && features.size() == 1) {
                FeatureEntity feature = features.iterator().next();
                if (feature.isSetGeometry() && !feature.getGeometryEntity().isEmpty()) {
                    Geometry geometry = feature.getGeometryEntity().getGeometry();
                    // 8.2 set position from geometry
                    Coordinate c = geometry.getCoordinate();
                    position.setPosition(createCoordinatesForPosition(c.getY(), c.getX(), c.getZ()));
                }
            }
            position.setReferenceFrame(srsNamePrefix + srid);
            return position;
        } catch (Exception e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating sensor position!");
        }

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
    private List<SweCoordinate<BigDecimal>> createCoordinatesForPosition(Object longitude, Object latitude,
            Object altitude) {
        SweQuantity yq = createSweQuantity(latitude, SweConstants.Y_AXIS, procedureSettings().getLatLongUom());
        SweQuantity xq = createSweQuantity(longitude, SweConstants.X_AXIS, procedureSettings().getLatLongUom());
        SweQuantity zq = createSweQuantity(altitude, SweConstants.Z_AXIS, procedureSettings().getAltitudeUom());
        // TODO add Integer: Which SweSimpleType to use?
        return Lists.<SweCoordinate<BigDecimal>> newArrayList(new SweCoordinate<>(SweCoordinateNames.NORTHING, yq),
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

    private List<SmlIdentifier> createIdentifications(final String identifier) {
        return Lists.newArrayList(createIdentifier(identifier));
    }

    private SmlIdentifier createIdentifier(final String identifier) {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END, OGCConstants.URN_UNIQUE_IDENTIFIER,
                identifier);
    }

}

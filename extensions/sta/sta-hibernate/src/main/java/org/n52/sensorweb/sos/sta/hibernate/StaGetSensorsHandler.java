/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sensorweb.sos.sta.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.sensorweb.sos.sta.operation.StaAbstractGetSensorsHandler;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaSensor;
import org.n52.shetland.ogc.sta.request.StaGetSensorsRequest;
import org.n52.shetland.ogc.sta.response.StaGetSensorsResponse;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link StaAbstractGetSensorsHandler}
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetSensorsHandler extends StaAbstractGetSensorsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetSensorsHandler.class);

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public StaGetSensorsResponse getSensors(StaGetSensorsRequest request) throws OwsExceptionReport {

        Session session = null;
        try {
            final StaGetSensorsResponse response = new StaGetSensorsResponse(request.getService(), request.getVersion());

            session = sessionHolder.getSession();
            response.setSensors(querySensors(request, session));

            return response;

        } catch (final HibernateException | CodedException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for STA GET Sensors request.");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private Set<StaSensor> querySensors(StaGetSensorsRequest request, Session session) throws CodedException {

        Set<StaSensor> sensors = new HashSet<>();

        ProcedureDAO procedureDao = daoFactory.getProcedureDAO();
        ValidProcedureTimeDAO validProcedureTimeDao = daoFactory.getValidProcedureTimeDAO();

        if (request.getId() != null) {
            // request a single procedure

            Procedure procedure = procedureDao.getProcedureForId(request.getId(), session);
            List<ValidProcedureTime> validProcedureTime = validProcedureTimeDao
                    .getValidProcedureTimes(procedure, procedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat(), null, session);

            if (validProcedureTime != null && validProcedureTime.size() == 1) {

                sensors.add(convertProcedure(procedure, validProcedureTime.get(0).getDescriptionXml()));
            } else {
                throw new NoApplicableCodeException()
                    .withMessage("Error while querying procedure to STA GET Sensors request: multiple description entries");
            }

        } else {
            // request all non-parent procedures, filter
            // TODO add filters

            List<Procedure> list = procedureDao.getProcedureObjects(session);
            list.forEach((Procedure p) -> {
                try {
                    List<ValidProcedureTime> validProcedureTime = validProcedureTimeDao
                            .getValidProcedureTimes(p, p.getProcedureDescriptionFormat().getProcedureDescriptionFormat(), null, session);

                    if (validProcedureTime != null && validProcedureTime.size() == 1) {
                        sensors.add(convertProcedure(p, validProcedureTime.get(0).getDescriptionXml()));
                    } else {
                        throw new NoApplicableCodeException()
                            .withMessage("Error while querying procedure to STA GET Sensors request: multiple description entries");
                    }
                } catch (CodedException ex) {
                    // TODO throw exception
                    LOG.error("Error while querying procedure to STA GET Sensors request.");
                }
            });
        }
        return sensors;
    }

    private StaSensor convertProcedure(Procedure procedure, String descriptionXml) throws CodedException {

        StaSensor sensor = new StaSensor(procedure.getProcedureId());

        sensor.setName(procedure.getName());
        sensor.setDescription(procedure.getDescription());

        // TODO check if descriptionXml or descriptionFile is set and if file is a link if format is xyz
        String descriptionFormat = procedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
        if (descriptionFormat == null || descriptionFormat.isEmpty()) {
            throw new NoApplicableCodeException()
                .withMessage("Error while converting procedure to STA GET Sensors request: no encodingType");

        } else if (descriptionFormat.equalsIgnoreCase(StaConstants.SENSOR_ENCODING_TYPE_PDF)) {

            sensor.setEncodingType(StaConstants.SENSOR_ENCODING_TYPE_PDF);
            sensor.setMetadata(procedure.getDescriptionFile());

        } else if (descriptionFormat.equalsIgnoreCase(StaConstants.SENSOR_ENCODING_TYPE_SENSORML_20)) {

            sensor.setEncodingType(StaConstants.SENSOR_ENCODING_TYPE_SENSORML_20);

            if (procedure.getDescriptionXml() != null && !procedure.getDescriptionXml().isEmpty()) {
                sensor.setMetadata(procedure.getDescriptionXml());

            } else if (descriptionXml != null && !descriptionXml.isEmpty()) {
                sensor.setMetadata(descriptionXml);

            } else {
                throw new NoApplicableCodeException()
                    .withMessage("Error while converting procedure to STA GET Sensors request: no metadata available");
            }
        } else {
            // TODO throw exception, for testing: let pass

//            throw new NoApplicableCodeException()
//                .withMessage("Error while converting procedure to STA GET Sensors request: encodingType '" + descriptionFormat + "' not supported");
        }

        return sensor;
    }

    @Override
    public void init() {
        // no initialization needed
    }
}

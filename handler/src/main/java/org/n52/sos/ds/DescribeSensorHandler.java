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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.io.request.IoParameters;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.series.db.old.dao.ProcedureDao;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.sos.ds.dao.DescribeSensorDao;
import org.n52.sos.ds.procedure.ProcedureConverter;

import com.google.common.collect.Maps;

public class DescribeSensorHandler extends AbstractDescribeSensorHandler {

    private HibernateSessionStore sessionStore;

    private DescribeSensorDao dao;

    private ProcedureConverter procedureConverter;

    public DescribeSensorHandler() {
        super(SosConstants.SOS);
    }

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) {
        Set<OwsDomain> operationParameters = super.getOperationParameters(service, version);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            operationParameters
                    .add(new OwsDomain(Sos2Constants.DescribeSensorParams.validTime, OwsAnyValue.instance()));
        }
        return operationParameters;
    }

    @Inject
    public void setDescribeSensorDao(Optional<DescribeSensorDao> describeSensorDao) {
        if (describeSensorDao.isPresent()) {
            this.dao = describeSensorDao.get();
        }
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setProcedureConverter(ProcedureConverter procedureConverter) {
        this.procedureConverter = procedureConverter;
    }

    @Override
    public DescribeSensorResponse getSensorDescription(final DescribeSensorRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
            final DescribeSensorResponse response = new DescribeSensorResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setOutputFormat(request.getProcedureDescriptionFormat());

            Collection<ProcedureEntity> entities = new ProcedureDao(session).get(createDbQuery(request));
            if (entities == null || entities.isEmpty()) {
                throw new NoApplicableCodeException()
                        .causedBy(new IllegalArgumentException("Parameter 'procedure' should not be null!"))
                        .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
            }
            if (dao != null) {
                response.setSensorDescriptions(dao.querySensorDescriptions(request, session));
            } else {
                response.addSensorDescription(createSensorDescription(entities.iterator().next(), request, session));
            }

            return response;
        } catch (final HibernateException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying data for DescribeSensor document!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private SosProcedureDescription<?> createSensorDescription(ProcedureEntity procedure,
            DescribeSensorRequest request, Session session) throws OwsExceptionReport {
        return procedureConverter.createSosProcedureDescription(Hibernate.unproxy(procedure, ProcedureEntity.class),
                request.getProcedureDescriptionFormat(), request.getVersion(), getRequestedLocale(request), session);
    }

    private DbQuery createDbQuery(DescribeSensorRequest req) {
        Map<String, String> map = Maps.newHashMap();
        if (req.isSetProcedure()) {
            map.put(IoParameters.PROCEDURES, req.getProcedure());
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        map.put(IoParameters.EXPANDED, Boolean.toString(true));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }
}

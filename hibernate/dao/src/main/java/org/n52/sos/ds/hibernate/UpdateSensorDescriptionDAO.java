/*
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
package org.n52.sos.ds.hibernate;

import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.UpdateSensorRequest;
import org.n52.shetland.ogc.sos.response.UpdateSensorResponse;
import org.n52.sos.ds.AbstractUpdateSensorDescriptionHandler;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDescriptionFormatDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;

/**
 * Implementation of the abstract class AbstractUpdateSensorDescriptionHandler
 * @since 4.0.0
 *
 */
public class UpdateSensorDescriptionDAO extends AbstractUpdateSensorDescriptionHandler {

private HibernateSessionHolder sessionHolder;

    public UpdateSensorDescriptionDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized UpdateSensorResponse updateSensorDescription(UpdateSensorRequest request)
            throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            UpdateSensorResponse response = new UpdateSensorResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            for (SosProcedureDescription<?> procedureDescription : request.getProcedureDescriptions()) {
                DateTime currentTime = new DateTime(DateTimeZone.UTC);
                // TODO: check for all validTimes of descriptions for this
                // identifier
                // ITime validTime =
                // getValidTimeForProcedure(procedureDescription);
                Procedure procedure =
                        new ProcedureDAO().getProcedureForIdentifier(request.getProcedureIdentifier(), session);
                if (procedure instanceof TProcedure) {
                    ProcedureDescriptionFormat procedureDescriptionFormat =
                            new ProcedureDescriptionFormatDAO().getProcedureDescriptionFormatObject(
                                    request.getProcedureDescriptionFormat(), session);
                    Set<ValidProcedureTime> validProcedureTimes = ((TProcedure) procedure).getValidProcedureTimes();
                    ValidProcedureTimeDAO validProcedureTimeDAO = new ValidProcedureTimeDAO();
                    for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
                        if (validProcedureTime.getProcedureDescriptionFormat().equals(procedureDescriptionFormat)
                                && validProcedureTime.getEndTime() == null) {
                            validProcedureTime.setEndTime(currentTime.toDate());
                            validProcedureTimeDAO.updateValidProcedureTime(validProcedureTime, session);
                        }
                    }
                    validProcedureTimeDAO.insertValidProcedureTime(procedure, procedureDescriptionFormat,
                            procedureDescription.getXml(), currentTime, session);
                }
            }
            session.flush();
            transaction.commit();
            response.setUpdatedProcedure(request.getProcedureIdentifier());
            return response;
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while processing data for UpdateSensorDescription document!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

}

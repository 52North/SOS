/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.UpdateSensorRequest;
import org.n52.shetland.ogc.sos.response.UpdateSensorResponse;
import org.n52.sos.ds.AbstractUpdateSensorDescriptionHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureHistoryDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

/**
 * Implementation of the abstract class AbstractUpdateSensorDescriptionHandler
 *
 * @since 4.0.0
 *
 */
public class UpdateSensorDescriptionHandler extends AbstractUpdateSensorDescriptionHandler implements Constructable {

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    public UpdateSensorDescriptionHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public void init() {
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
                ProcedureEntity procedure = daoFactory.getProcedureDAO()
                        .getProcedureForIdentifier(request.getProcedureIdentifier(), session);
                FormatEntity procedureDescriptionFormat = new DaoFactory().getProcedureDescriptionFormatDAO()
                        .getFormatEntityObject(request.getProcedureDescriptionFormat(), session);
                Set<ProcedureHistoryEntity> procedureHistories = procedure.getProcedureHistory();
                ProcedureHistoryDAO procedureHistroyDAO = daoFactory.getProcedureHistoryDAO();
                for (ProcedureHistoryEntity procedureHistroy : procedureHistories) {
                    if (procedureHistroy.getFormat().getFormat().equals(procedureDescriptionFormat.getFormat())
                            && procedureHistroy.getEndTime() == null) {
                        procedureHistroy.setEndTime(currentTime.toDate());
                        procedureHistroyDAO.update(procedureHistroy, session);
                    }
                }
                procedureHistroyDAO.insert(procedure, procedureDescriptionFormat,
                        getSensorDescriptionFromProcedureDescription(procedureDescription), currentTime, session);
            }
            session.flush();
            transaction.commit();
            response.setUpdatedProcedure(request.getProcedureIdentifier());
            return response;
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while processing data for UpdateSensorDescription document!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    /**
     * Get SensorDescription String from procedure description
     *
     * @param procedureDescription
     *            Procedure description
     * @return SensorDescription String
     */
    private String getSensorDescriptionFromProcedureDescription(SosProcedureDescription<?> procedureDescription) {
        if (procedureDescription.getProcedureDescription() instanceof SensorML) {
            final SensorML sensorML = (SensorML) procedureDescription.getProcedureDescription();
            // if SensorML is not a wrapper
            if (!sensorML.isWrapper() && sensorML.isSetXml()) {
                return sensorML.getXml();
            } else if (sensorML.isWrapper() && sensorML.getMembers().size() == 1
                    && sensorML.getMembers().get(0).isSetXml()) {
                // if SensorML is a wrapper and member size is 1
                return sensorML.getMembers().iterator().next().getXml();
            } else {
                // TODO: get sensor description for procedure identifier
                return "";
            }
        } else if (procedureDescription.getProcedureDescription().isSetXml()) {
            return procedureDescription.getProcedureDescription().getXml();
        } else if (procedureDescription.isSetXml()) {
            return procedureDescription.getXml();
        }
        return "";
    }

}

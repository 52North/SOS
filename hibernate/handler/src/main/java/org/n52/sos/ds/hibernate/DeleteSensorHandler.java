/*
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
package org.n52.sos.ds.hibernate;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.DeleteSensorRequest;
import org.n52.shetland.ogc.sos.response.DeleteSensorResponse;
import org.n52.sos.ds.AbstractDeleteSensorHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

/**
 * Implementation of the abstract class AbstractDeleteSensorHandler
 *
 * @since 4.0.0
 *
 */
@Configurable
public class DeleteSensorHandler extends AbstractDeleteSensorHandler implements DeleteDataHelper, Constructable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteSensorHandler.class);

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    private Boolean deletePhysically = false;

    public DeleteSensorHandler() {
        super(SosConstants.SOS);
    }

    @Setting("service.transactional.DeletePhysically")
    public void setDeletePhysically(Boolean deletePhysically) {
        this.deletePhysically = deletePhysically;
    }

    @Override
    public void init() {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized DeleteSensorResponse deleteSensor(DeleteSensorRequest request) throws OwsExceptionReport {
        DeleteSensorResponse response = new DeleteSensorResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = session.beginTransaction();
            String identifier = request.getProcedureIdentifier();
            ProcedureEntity procedure = daoFactory.getProcedureDAO().getProcedureForIdentifier(identifier, session);
            deleteSensor(procedure, session);
            transaction.commit();
            response.setDeletedProcedure(request.getProcedureIdentifier());
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while updateing deleted sensor flag data!");
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        return response;
    }

    @Override
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

    @VisibleForTesting
    protected synchronized void initForTesting(DaoFactory daoFactory, ConnectionProvider connectionProvider) {
        this.daoFactory = daoFactory;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public boolean isDeletePhysically() {
        return deletePhysically;
    }

}

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
package org.n52.sos.ds.hibernate;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.sos.ds.AbstractDeleteObservationHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP"})
public class DeleteObservationHandler extends AbstractDeleteObservationHandler implements DeleteObservationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationHandler.class);

    private HibernateSessionHolder sessionHolder;

    @Inject
    private DaoFactory daoFactory;

    private Boolean deletePhysically = false;

    @Setting("service.transactional.DeletePhysically")
    public void setDeletePhysically(Boolean deletePhysically) {
        this.deletePhysically = deletePhysically;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        setSessionHolder(new HibernateSessionHolder(connectionProvider));
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = new DeleteObservationResponse(request.getResponseFormat());
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSessionHolder().getSession();
            transaction = session.beginTransaction();
            if (request.isSetObservationIdentifiers()) {
                deleteObservationsByIdentifier(request, response, session);
            } else {
                deleteObservationByParameter(request, response, session);
            }
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(ERROR_LOG);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage(ERROR_LOG);
        } finally {
            getSessionHolder().returnSession(session);
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
    public boolean isDeletePhysically() {
        return deletePhysically;
    }

    private synchronized HibernateSessionHolder getSessionHolder() {
        return sessionHolder;
    }

    private synchronized void setSessionHolder(HibernateSessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

}

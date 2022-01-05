/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateConstants;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateRequest;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateResponse;
import org.n52.sos.ds.AbstractDeleteResultTemplateHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.sos.concrete.DeleteResultTemplateInvalidParameterValueException;

import com.google.common.collect.Lists;

public class DeleteResultTemplateHandler
        extends AbstractDeleteResultTemplateHandler {

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public DeleteResultTemplateResponse deleteResultTemplates(DeleteResultTemplateRequest request)
            throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        DeleteResultTemplateResponse response = new DeleteResultTemplateResponse();
        response.set(request);
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            if (request.isSetResultTemplates()) {
                response.addDeletedResultTemplates(deleteByTemplateId(session, request.getResultTemplates()));
            } else {
                response.addDeletedResultTemplates(
                        deleteByObservedPropertyOfferingPair(session, request.getObservedPropertyOfferingPairs()));
            }
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            handleHibernateException(he);
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }

    protected void handleHibernateException(HibernateException he) throws OwsExceptionReport {
        HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
        throw new NoApplicableCodeException().causedBy(he).withMessage("Error while deleting result templates!")
                .setStatus(status);
    }

    private List<String> deleteByTemplateId(Session session, List<String> resultTemplates)
            throws InvalidParameterValueException {
        List<String> deletedResultTemplates = Lists.newArrayList();
        for (String resultTemplate : resultTemplates) {
            final ResultTemplateEntity templateObject =
                    daoFactory.getResultTemplateDAO().getResultTemplateObject(resultTemplate, session);
            if (templateObject == null) {
                throw new InvalidParameterValueException(DeleteResultTemplateConstants.PARAMETERS.resultTemplate,
                        resultTemplate);
            }
            session.delete(templateObject);

            deletedResultTemplates.add(resultTemplate);
        }
        return deletedResultTemplates;
    }

    private List<String> deleteByObservedPropertyOfferingPair(Session session,
            List<AbstractMap.SimpleEntry<String, String>> observedPropertyOfferingPairs) throws CompositeOwsException {
        List<String> deletedResultTemplates = Lists.newArrayList();
        CompositeOwsException exceptions = new CompositeOwsException();
        for (Map.Entry<String, String> observedPropertyOfferingPair : observedPropertyOfferingPairs) {
            final String offering = observedPropertyOfferingPair.getValue();
            final String observedProperty = observedPropertyOfferingPair.getKey();
            final ResultTemplateEntity resultTemplateObject =
                    daoFactory.getResultTemplateDAO().getResultTemplateObject(offering, observedProperty, session);
            if (resultTemplateObject == null) {
                exceptions.add(new DeleteResultTemplateInvalidParameterValueException(offering, observedProperty));
            } else {
                final String resultTemplateId = resultTemplateObject.getIdentifier();
                session.delete(resultTemplateObject);
                deletedResultTemplates.add(resultTemplateId);
            }
        }
        exceptions.throwIfNotEmpty();
        return deletedResultTemplates;
    }

}

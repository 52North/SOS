/**
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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.shetland.ogc.sos.urt.UpdateResultTemplateConstants;
import org.n52.sos.ds.AbstractUpdateResultTemplateHandler;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.UpdateResultTemplateRequest;
import org.n52.sos.response.UpdateResultTemplateResponse;
import org.n52.sos.util.http.HTTPStatus;

public class UpdateResultTemplateHandler
        extends AbstractUpdateResultTemplateHandler {

    private String resultTemplateId = "";
    private ResultTemplate resultTemplate = null;
        
    private final HibernateSessionHolder sessionHolder =
            new HibernateSessionHolder();

    private final ResultTemplateDAO resultTemplateDAO = new ResultTemplateDAO();
    
    public UpdateResultTemplateHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public UpdateResultTemplateResponse updateResultTemplate(
            UpdateResultTemplateRequest request)
            throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        boolean updated = false;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            checkAndGetResultTemplate(request, session);
            if (request.isSetResultStructure()) {
                resultTemplate.setResultStructure(request.getResultStructure());
                updated = true;
            }
            if (request.isSetResultEncoding()) {
                resultTemplate.setResultEncoding(request.getResultEncoding());
                updated = true;
            }
            if (updated) {
                session.save(resultTemplate);
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
        return request.getResponse().setUpdatedResultTemplate(resultTemplateId);
    }

    private void checkAndGetResultTemplate(UpdateResultTemplateRequest request, Session session) throws CodedException {
        if (request.isSetResultTemplate()) {
            resultTemplateId = request.getResultTemplate();
            resultTemplate = resultTemplateDAO.getResultTemplateObject(
                    resultTemplateId,
                    session);
            // check result template object
            if (resultTemplate == null) {
                throw new InvalidParameterValueException(
                        UpdateResultTemplateConstants.PARAMS.resultTemplate,
                        resultTemplateId)
                        .withMessage("Could not retrieve result template "
                                + "object from database with id '%s'.",
                                resultTemplateId);
            }
        } else {
            throw new MissingParameterValueException(
                    UpdateResultTemplateConstants.PARAMS.resultTemplate);
        }
    }

    protected void handleHibernateException(HibernateException he)
            throws OwsExceptionReport {
        throw new NoApplicableCodeException()
                .causedBy(he)
                .withMessage("Error while updating result template!")
                .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
    }

}

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
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.sos.ds.AbstractGetResultTemplateHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.sos.concrete.NoSweCommonEncodingForOfferingObservablePropertyCombination;

/**
 * Implementation of the abstract class AbstractGetResultTemplateHandler
 *
 * @since 4.0.0
 *
 */
public class GetResultTemplateHandler
        extends AbstractGetResultTemplateHandler {
    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    public GetResultTemplateHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public GetResultTemplateResponse getResultTemplate(GetResultTemplateRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            ResultTemplateEntity resultTemplate = daoFactory.getResultTemplateDAO()
                    .getResultTemplateObject(request.getOffering(), request.getObservedProperty(), session);
            if (resultTemplate != null) {
                GetResultTemplateResponse response = new GetResultTemplateResponse();
                response.setService(request.getService());
                response.setVersion(request.getVersion());
                response.setResultEncoding(createSosResultEncoding(resultTemplate.getEncoding()));
                response.setResultStructure(createSosResultStructure(resultTemplate.getStructure()));
                return response;
            }
            throw new NoSweCommonEncodingForOfferingObservablePropertyCombination(request.getOffering(),
                    request.getObservedProperty());
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data result template data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }
}

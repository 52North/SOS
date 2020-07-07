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
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.sos.ds.AbstractGetResultTemplateHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.svalbard.util.SweHelper;

/**
 * Implementation of the abstract class AbstractGetResultTemplateHandler
 *
 * @since 4.0.0
 *
 */
public class GetResultTemplateHandler extends AbstractGetResultTemplateHandler
        implements AbstractResultHandler, Constructable {
    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    private ResultHandlingHelper resultHandlingHelper;

    private boolean supportsDatabaseEntities;

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
    public void init() {
        this.supportsDatabaseEntities = HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public SweHelper getSweHelper() {
        return getDaoFactory().getSweHelper();
    }

    @Override
    public ResultHandlingHelper getResultHandlingHelper() {
        if (resultHandlingHelper == null) {
            this.resultHandlingHelper = new ResultHandlingHelper(getDaoFactory().getGeometryHandler(),
                    getDaoFactory().getSweHelper(), getDaoFactory().getDecoderRepository());
        }
        return resultHandlingHelper;
    }

    @Override
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public GetResultTemplateResponse getResultTemplate(GetResultTemplateRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            GetResultTemplateResponse response = new GetResultTemplateResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            ResultTemplateEntity resultTemplate = supportsDatabaseEntities ? getDaoFactory().getResultTemplateDAO()
                    .getResultTemplateObjectForResponse(request.getOffering(), request.getObservedProperty(), session)
                    : null;
            if (resultTemplate != null && resultTemplate.isSetStructure() && resultTemplate.isSetEncoding()) {
                response.setResultEncoding(createSosResultEncoding(resultTemplate.getEncoding()));
                response.setResultStructure(createSosResultStructure(resultTemplate.getStructure()));
            } else {
                response.setResultEncoding(createSosResultEncoding());
                response.setResultStructure(generateSosResultStructure(request.getObservedProperty(),
                        request.getOffering(), null, session));
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data result template data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

}

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
package org.n52.sos.ds;

import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.sos.ds.dao.GetResultTemplateDao;
import org.n52.sos.ds.observation.ObservationHelper;
import org.n52.sos.ds.utils.ResultHandlingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.util.SweHelper;
import org.springframework.transaction.annotation.Transactional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of the abstract class AbstractGetResultTemplateHandler
 *
 * @since 4.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class GetResultTemplateHandler extends AbstractGetResultTemplateHandler
        implements AbstractResultHandler {

    private HibernateSessionStore sessionStore;

    private Optional<GetResultTemplateDao> dao;

    private ResultHandlingHelper resultHandlingHelper;

    private GetResultHandler getResultHandler;

    private ObservationHelper observationHelper;

    public GetResultTemplateHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setGetResultHandler(GetResultHandler getResultHandler) {
        this.getResultHandler = getResultHandler;
    }

    @Inject
    public void setGetResultTemplateDao(Optional<GetResultTemplateDao> getResultTemplateDao) {
        this.dao = getResultTemplateDao;
    }

    @Inject
    public void setObservationHelper(ObservationHelper observationHelper) {
        this.observationHelper = observationHelper;
    }

    @Override
    public boolean isSupported() {
        return getResultHandler.isSupported();
    }


    @Override
    public ResultHandlingHelper getResultHandlingHelper() {
        if (resultHandlingHelper == null) {
            this.resultHandlingHelper = new ResultHandlingHelper(getObservationHelper());
        }
        return resultHandlingHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public GetResultTemplateResponse getResultTemplate(GetResultTemplateRequest request) throws OwsExceptionReport {
        GetResultTemplateResponse response = new GetResultTemplateResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        try {
            session = sessionStore.getSession();
            GetResultTemplateResponse reponse = dao.isPresent() ? dao.get()
                    .queryResultTemplate(request, response, session) : null;
            if (response != null && response.getResultStructure() != null && response.getResultEncoding() != null) {
                return reponse;
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
            sessionStore.returnSession(session);
        }
    }

    @Override
    public SweHelper getSweHelper() {
        return getObservationHelper().getSweHelper();
    }

    private DecoderRepository getDecoderRepository() {
        return getObservationHelper().getDecoderRepository();
    }

    private GeometryHandler getGeometryHandler() {
        return getObservationHelper().getGeometryHandler();
    }

    private ObservationHelper getObservationHelper() {
        return observationHelper;
    }


}

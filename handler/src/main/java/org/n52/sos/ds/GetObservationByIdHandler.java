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
package org.n52.sos.ds;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.series.db.HibernateSessionStore;
import org.n52.sos.ds.dao.GetObservationByIdDao;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.response.GetObservationByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

public class GetObservationByIdHandler extends AbstractGetObservationByIdHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationByIdHandler.class);
    
    private HibernateSessionStore sessionStore;

    private GetObservationByIdDao getObservationByIdDao;
    
    public GetObservationByIdHandler() {
        super(SosConstants.SOS);
    }
    
    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }
    
    @Inject
    public void setGetObservationByIdDao(GetObservationByIdDao getObservationByIdDao) {
        this.getObservationByIdDao = getObservationByIdDao;
    }
    
    @Override
    public GetObservationByIdResponse getObservationById(GetObservationByIdRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
            GetObservationByIdResponse response = request.getResponse();
            List<OmObservation> omObservations = Lists.newArrayList();
            if (getObservationByIdDao != null) {
                omObservations.addAll(getObservationByIdDao.queryObservationsById(request));
            }
            response.setObservationCollection(omObservations);
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!");
        } finally {
            sessionStore.returnSession(session);
        }
    }
}

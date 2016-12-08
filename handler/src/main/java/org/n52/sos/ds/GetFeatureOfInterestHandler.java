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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.proxy.db.dao.ProxyFeatureDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;

public class GetFeatureOfInterestHandler extends AbstractGetFeatureOfInterestHandler {

    private HibernateSessionStore sessionStore;

    private FeatureQueryHandler featureQueryHandler;

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    public GetFeatureOfInterestHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public GetFeatureOfInterestResponse getFeatureOfInterest(GetFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        Session session = sessionStore.getSession();
        try {
            GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());

            response.setAbstractFeature(getFeatures(request, session));
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data for GetFeatureOfInterest!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    /**
     * Get featureOfInterest as a feature collection
     *
     * @param request
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Feature collection with requested featuresOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private FeatureCollection getFeatures(GetFeatureOfInterestRequest request, Session session)
            throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject()
                // .setSpatialFilters(request.getSpatialFilters())
                .setVersion(request.getVersion()).setI18N(LocaleHelper.fromString(request.getRequestedLanguage()));
        if (request.hasParameter()) {
            Set<FeatureEntity> featureEntities = new HashSet<>(queryFeaturesForParameter(request, session));
            if (featureEntities == null || featureEntities.isEmpty()) {
                return new FeatureCollection();
            }
            queryObject.setFeatures(featureEntities.stream().map(f -> f.getDomainId()).collect(Collectors.toSet()));
        }
        return new FeatureCollection(this.featureQueryHandler.getFeatures(queryObject));
    }

    /**
     * Get featureOfInterest identifiers for requested parameters
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Resulting FeatureOfInterest identifiers list
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private List<FeatureEntity> queryFeaturesForParameter(GetFeatureOfInterestRequest req, Session session)
            throws OwsExceptionReport {
        try {
            return new ProxyFeatureDao(session).getAllInstances(createDbQuery(req));
        } catch (DataAccessException dae) {
            throw new NoApplicableCodeException().causedBy(dae)
                    .withMessage("Error while querying data for GetFeatureOfInterest!");
        }
    }

    private DbQuery createDbQuery(GetFeatureOfInterestRequest req) {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        if (req.isSetFeatureOfInterestIdentifiers()) {
            rsps.addParameter(IoParameters.FEATURES, IoParameters.getJsonNodeFrom(req.getFeatureIdentifiers()));
        }
        if (req.isSetProcedures()) {
            rsps.addParameter(IoParameters.PROCEDURES, IoParameters.getJsonNodeFrom(req.getProcedures()));
        }
        if (req.isSetObservableProperties()) {
            rsps.addParameter(IoParameters.PHENOMENA, IoParameters.getJsonNodeFrom(req.getObservedProperties()));
        }
        rsps.addParameter(IoParameters.MATCH_DOMAIN_IDS, IoParameters.getJsonNodeFrom(true));
        return new DbQuery(IoParameters.createFromQuery(rsps));
    }

}

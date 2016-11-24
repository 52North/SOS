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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.series.db.da.FeatureRepository;
import org.n52.sos.config.sqlite.hibernate.HibernateFileType;
import org.n52.sos.ds.dao.GetObservationDao;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetObservationHandler extends AbstractGetObservationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationHandler.class);

    private FeatureQueryHandler featureQueryHandler;
    private GetObservationDao dao;

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }
    
    @Inject
    public void setGetObservationDao(GetObservationDao dao) {
        this.dao = dao;
    }
    
    public GetObservationHandler() {
        super(SosConstants.SOS);
    }
    
    @Override
    public GetObservationResponse getObservation(GetObservationRequest request) throws OwsExceptionReport {
        GetObservationResponse response = request.getResponse();
        response.setObservationCollection(dao.getObservation(request));
        response.setGlobalValues(dao.getGlobalValues(request));
        return response;
    }
    
    
    /**
     * Query the series observations for streaming datasource
     *
     * @param request
     *            The GetObservation request
     * @param sosResponse 
     * @param session
     *            Hibernate Session
     * @return List of internal observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> querySeriesObservationForStreaming(GetObservationRequest request,
            GetObservationResponse response) throws OwsExceptionReport, ConverterException {
        long start = System.currentTimeMillis();
        List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
        if (request.isSetFeatureOfInterest()){
            queryObject.setFeatureIdentifiers(request.getFeatureIdentifiers());
        } else if (request.isSetSpatialFilter()) {
            queryObject.addSpatialFilter(request.getSpatialFilter());
        }
        
        Collection<String> features = featureQueryHandler.getFeatureIDs(queryObject);
        if (features != null && features.isEmpty()) {
            return result;
        }
        List<SosIndeterminateTime> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            if (ServiceConfiguration.getInstance().isOverallExtrema()) {
                result =
                        observationDAO.getSeriesObservationsFor(request, features,
                                sosIndeterminateTime, session);
            } else {
                for (Series series : seriesDAO.getSeries(request, features, session)) {
                    result.addAll(observationDAO.getSeriesObservationsFor(series, request,
                            sosIndeterminateTime, session));
                    
                }
            }
        } else {
            List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, features, session);
            HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
            int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(serieses.size());
            Collection<Series> duplicated = checkAndGetDuplicatedtSeries(serieses, request);
            for (Series series : serieses) {
                Collection<? extends OmObservation> createSosObservationFromSeries =
                        HibernateObservationUtilities
                                .createSosObservationFromSeries(series, request, session);
                OmObservation observationTemplate = createSosObservationFromSeries.iterator().next();
                HibernateSeriesStreamingValue streamingValue = getSeriesStreamingValue(request, series.getSeriesId());
                streamingValue.setResponseFormat(request.getResponseFormat());
                streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
                streamingValue.setObservationTemplate(observationTemplate);
                streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
                observationTemplate.setValue(streamingValue);
                result.add(observationTemplate);
            }
        }
        // query global response values
        
        ObservationTimeExtrema timeExtrema = DaoFactory.getInstance().getValueTimeDAO().getTimeExtremaForSeries(serieses, temporalFilterCriterion, session);
        if (timeExtrema.isSetPhenomenonTimes()) {
            response.setGlobalValues(response.new GlobalGetObservationValues().setPhenomenonTime(timeExtrema.getPhenomenonTime()));
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

}

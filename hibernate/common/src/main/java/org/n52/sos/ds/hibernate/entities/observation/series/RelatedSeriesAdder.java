/**
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
package org.n52.sos.ds.hibernate.entities.observation.series;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.n52.sos.binding.BindingRepository;
import org.n52.sos.ds.hibernate.util.observation.PhenomenonTimeCreator;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationContext;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.http.MediaTypes;

public class RelatedSeriesAdder {

    private OmObservation observation;
    private List<RelatedSeries> hRelatedSeries;

    public RelatedSeriesAdder(OmObservation observation, List<RelatedSeries> hRelatedSeries) {
        this.observation = observation;
        this.hRelatedSeries = hRelatedSeries;
    }

    public void add() throws CodedException { 
        try {
            if (CollectionHelper.isNotEmpty(hRelatedSeries)) {
                for (RelatedSeries relatedSeries : hRelatedSeries) {
                    ReferenceType role = new ReferenceType();
                    if (relatedSeries.isSetRole()) {
                        role.setHref(relatedSeries.getRole());
                    }
                    if (relatedSeries.isSetRelatedUrl()) {
                        observation.addRelatedObservation(new OmObservationContext(role, new ReferenceType(relatedSeries.getRelatedUrl())));
                    } else if (relatedSeries.isSetRelatedSeries()) {
                        if (relatedSeries.getRelatedSeries().isSetIdentifier()) {
                            observation.addRelatedObservation(new OmObservationContext(role, new ReferenceType(createGetObservationByIdUrl(relatedSeries.getRelatedSeries().getIdentifier()))));
                        } else {
                            // TODO check if this should be set because result may not be a unique observation.
                            observation.addRelatedObservation(new OmObservationContext(role, new ReferenceType(createGetObservationUrl(relatedSeries.getRelatedSeries()))));
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException uee) {
            throw new NoApplicableCodeException().causedBy(uee).withMessage("Unable to URL encode.");
        }
    }
    
    private String createGetObservationByIdUrl(String observationIdentifier) throws UnsupportedEncodingException {
        if (isKvpSupported()) {
            final StringBuilder url = new StringBuilder();
            // service URL
            url.append(encodeBaseGetUrl(Sos2Constants.SERVICEVERSION));
            // request
            url.append(encodeRequest(SosConstants.Operations.GetObservationById.name()));
            // observation identifier
            url.append(encodeParam(Sos2Constants.GetObservationByIdParams.observation.name(), true, observationIdentifier));
            return url.toString();
        } else {
            return observationIdentifier;
        }
    }

    private String createGetObservationUrl(Series hSeries) throws DateTimeFormatException, UnsupportedEncodingException {
        if (isKvpSupported()) {
            final StringBuilder url = new StringBuilder();
            // service URL
            url.append(encodeBaseGetUrl(Sos2Constants.SERVICEVERSION));
            // request
            url.append(encodeRequest(SosConstants.Operations.GetObservation.name()));
            // procedure
            url.append(encodeParam(SosConstants.GetObservationParams.procedure.name(), hSeries.getProcedure().getIdentifier()));
            // observedProperty
            url.append(encodeParam(SosConstants.GetObservationParams.observedProperty.name(), hSeries.getObservableProperty().getIdentifier()));
            // featureOfInterest
            url.append(encodeParam(SosConstants.GetObservationParams.featureOfInterest.name(), hSeries.getFeatureOfInterest().getIdentifier()));
            // phenomenonTime
            if (hSeries.isSetFirstLastTime()) {
                url.append(encodeTemporalFilterParam(hSeries));
            }
            return url.toString();
        }
        return "";
    }
    
    private boolean isKvpSupported() {
        return BindingRepository.getInstance().isBindingSupported(MediaTypes.APPLICATION_KVP);
    }
    
    private String getServiceUrl() {
        return ServiceConfiguration.getInstance().getServiceURL();
    }
    
    private String encodeBaseGetUrl(String version) throws UnsupportedEncodingException {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceUrl());
        // ?
        url.append(Constants.QUERSTIONMARK_CHAR);
        // service
        url.append(encodeServiceParam());
        // version
        url.append(encodeVersionParam(version));
        return url.toString();
    }

    private String encodeServiceParam() throws UnsupportedEncodingException {
        return encodeParam(OWSConstants.RequestParams.service.name(), false, SosConstants.SOS);
    }

    private String encodeVersionParam(String version) throws UnsupportedEncodingException {
        return encodeParam(OWSConstants.RequestParams.version.name(), version);
    }
    
    private String encodeRequest(String requestName) throws UnsupportedEncodingException {
        return encodeParam(RequestParams.request.name(), requestName);
    }
    
    private String encodeTemporalFilterParam(Series hSeries) throws DateTimeFormatException, UnsupportedEncodingException {
        Time phenomenonTime = new PhenomenonTimeCreator(hSeries).create();
        final StringBuilder time = new StringBuilder("om:phenomenonTime").append(",");
        if (phenomenonTime instanceof TimeInstant) {
            time.append(DateTimeHelper.formatDateTime2String(((TimeInstant) phenomenonTime).getTimePosition()));
        } else if (phenomenonTime instanceof TimePeriod) {
            time.append(DateTimeHelper.formatDateTime2String(((TimePeriod) phenomenonTime).getStartTimePosition()));
            time.append("/");
            time.append(DateTimeHelper.formatDateTime2String(((TimePeriod) phenomenonTime).getEndTimePosition()));
        } else {
            return "";
        }
        return encodeParam(Sos2Constants.GetObservationParams.temporalFilter.name(), time.toString());
    }

    private String encodeParam(String parameterName,String value) throws UnsupportedEncodingException {
        return encodeParam(parameterName, true, value);
    }
    
    private String encodeParam(String parameterName, boolean withAmpersand, String value) throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        if (withAmpersand) {
            builder.append("&");
        }
        return builder.append(parameterName).append("=").append(URLEncoder.encode(value, "UTF-8")).toString();
    }
        
    
//    url.append(getParameter(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat.name(),
//            URLEncoder.encode(procedureDescriptionFormat, "UTF-8")));

}

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
package org.n52.sos.ds.hibernate.util.observation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.RelatedDatasetEntity;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationContext;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeFormatException;
import org.n52.shetland.util.DateTimeHelper;

public class RelatedSeriesAdder {

    private OmObservation observation;
    private Collection<RelatedDatasetEntity> hRelatedSeries;
    private String serviceURL;
    private boolean isKvpSupported;

    public RelatedSeriesAdder(OmObservation observation, Collection<RelatedDatasetEntity> hRelatedSeries,
            String serviceURL, boolean isKvpSupported) {
        this.observation = observation;
        this.hRelatedSeries = hRelatedSeries;
        this.serviceURL = serviceURL;
        this.isKvpSupported = isKvpSupported;
    }

    public void add() throws CodedException {
        try {
            if (CollectionHelper.isNotEmpty(hRelatedSeries)) {
                for (RelatedDatasetEntity relatedSeries : hRelatedSeries) {
                    ReferenceType role = new ReferenceType();
                    if (relatedSeries.isSetRole()) {
                        role.setHref(relatedSeries.getRole());
                    }
                    if (relatedSeries.isSetRelatedUrl()) {
                        observation.addRelatedObservation(
                                new OmObservationContext(role, new ReferenceType(relatedSeries.getRelatedUrl())));
                    } else if (relatedSeries.getRelatedItem() != null) {
                        if (relatedSeries.getRelatedItem().isSetIdentifier()) {
                            observation.addRelatedObservation(new OmObservationContext(role, new ReferenceType(
                                    createGetObservationByIdUrl(relatedSeries.getRelatedItem().getIdentifier()))));
                        } else {
                            // TODO check if this should be set because result
                            // may not be a unique observation.
                            observation.addRelatedObservation(new OmObservationContext(role,
                                    new ReferenceType(createGetObservationUrl(relatedSeries.getRelatedItem()))));
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException uee) {
            throw new NoApplicableCodeException().causedBy(uee).withMessage("Unable to URL encode.");
        }
    }

    private String createGetObservationByIdUrl(String observationIdentifier) throws UnsupportedEncodingException {
        if (isKvpSupported) {
            final StringBuilder url = new StringBuilder();
            // service URL
            url.append(encodeBaseGetUrl(Sos2Constants.SERVICEVERSION));
            // request
            url.append(encodeRequest(SosConstants.Operations.GetObservationById.name()));
            // observation identifier
            url.append(encodeParam(Sos2Constants.GetObservationByIdParams.observation.name(), true,
                    observationIdentifier));
            return url.toString();
        } else {
            return observationIdentifier;
        }
    }

    private String createGetObservationUrl(DatasetEntity hSeries)
            throws DateTimeFormatException, UnsupportedEncodingException {
        if (isKvpSupported) {
            final StringBuilder url = new StringBuilder();
            // service URL
            url.append(encodeBaseGetUrl(Sos2Constants.SERVICEVERSION));
            // request
            url.append(encodeRequest(SosConstants.Operations.GetObservation.name()));
            // procedure
            url.append(encodeParam(SosConstants.GetObservationParams.procedure.name(),
                    hSeries.getProcedure().getIdentifier()));
            // observedProperty
            url.append(encodeParam(SosConstants.GetObservationParams.observedProperty.name(),
                    hSeries.getObservableProperty().getIdentifier()));
            // featureOfInterest
            url.append(encodeParam(SosConstants.GetObservationParams.featureOfInterest.name(),
                    hSeries.getFeature().getIdentifier()));
            // phenomenonTime
            if (hSeries.isSetFirstValueAt()) {
                url.append(encodeTemporalFilterParam(hSeries));
            }
            return url.toString();
        }
        return "";
    }

    private String encodeBaseGetUrl(String version) throws UnsupportedEncodingException {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(serviceURL);
        // ?
        url.append('?');
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
        return encodeParam(OWSConstants.RequestParams.request.name(), requestName);
    }

    private String encodeTemporalFilterParam(DatasetEntity hSeries)
            throws DateTimeFormatException, UnsupportedEncodingException {
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

    private String encodeParam(String parameterName, String value) throws UnsupportedEncodingException {
        return encodeParam(parameterName, true, value);
    }

    private String encodeParam(String parameterName, boolean withAmpersand, String value)
            throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        if (withAmpersand) {
            builder.append("&");
        }
        return builder.append(parameterName).append("=").append(URLEncoder.encode(value, "UTF-8")).toString();
    }

}

/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.aqd.v1;


import java.util.Set;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.coding.encode.OperationResponseEncoderKey;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.coding.encode.AbstractResponseEncoder;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;

import com.google.common.collect.Sets;

public abstract class AbstractAqdResponseEncoder<T extends OwsServiceResponse> extends AbstractResponseEncoder<T> {

    private ReportObligationRepository reportObligationRepository;
    private ProfileHandler profileHandler;
    private AqdHelper aqdHelper;


    public AbstractAqdResponseEncoder(String operation, Class<T> responseType) {
        super(AqdConstants.AQD, AqdConstants.VERSION, operation, AqdConstants.NS_AQD,
                AqdConstants.NS_AQD_PREFIX, responseType);
    }

    @Inject
    public void setReportObligationRepository(ReportObligationRepository reportObligationRepository) {
        this.reportObligationRepository = reportObligationRepository;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Inject
    public void setAqdHelper(AqdHelper aqdHelper) {
        this.aqdHelper = aqdHelper;
    }

    protected AqdHelper getAqdHelper() {
        return this.aqdHelper;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(AqdConstants.NS_AQD_SCHEMA_LOCATION);
    }

    protected EReportingHeader getEReportingHeader(ReportObligationType type)
            throws OwsExceptionReport {
        return reportObligationRepository.createHeader(type);
    }

    protected Profile getActiveProfile() {
        return this.profileHandler.getActiveProfile();
    }

    protected XmlObject encodeGml(Object o) throws EncodingException {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected XmlObject encodeGml(EncodingContext context, Object o) throws EncodingException {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o, context);
    }

    protected XmlObject encodeOws(Object o) throws EncodingException {
        return encodeObjectToXml(OWSConstants.NS_OWS, o);
    }

    protected XmlObject encodeOws(EncodingContext context, Object o) throws EncodingException {
        return encodeObjectToXml(OWSConstants.NS_OWS, o, context);
    }

    protected XmlObject encodeFes(Object o) throws EncodingException {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o);
    }

    protected XmlObject encodeFes(EncodingContext context, Object o) throws EncodingException {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o, context);
    }

    protected XmlObject encodeSwe(Object o) throws EncodingException {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected XmlObject encodeSwe(EncodingContext context, Object o) throws EncodingException {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o, context);
    }

    protected OwsServiceResponse changeResponseServiceVersion(OwsServiceResponse response) {
        response.setService(SosConstants.SOS);
        response.setVersion(Sos2Constants.SERVICEVERSION);
        return response;
    }

    /**
     * Get the {@link Encoder} for the {@link OwsServiceResponse} and the
     * requested contentType
     *
     * @param asr
     *            {@link OwsServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link OwsServiceResponse}
     */
    protected Encoder<Object, OwsServiceResponse> getEncoder(OwsServiceResponse asr) {
        OperationResponseEncoderKey key = new OperationResponseEncoderKey(new OperationKey(asr), getContentType());
        Encoder<Object, OwsServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new RuntimeException(new NoEncoderForKeyException(key));
        }
        return encoder;
    }

    protected XmlObject encodeWithSosEncoder(T response) throws EncodingException {
        Encoder<Object, OwsServiceResponse> encoder = getEncoder(changeResponseServiceVersion(response));
        if (encoder != null) {
            Object encode = encoder.encode(response);
            if (encode != null && encode instanceof XmlObject) {
                return (XmlObject)encode;
            }
        }
        return null;
    }





}

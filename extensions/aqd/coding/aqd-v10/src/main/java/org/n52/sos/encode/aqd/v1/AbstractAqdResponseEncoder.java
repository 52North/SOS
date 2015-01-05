/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import static org.n52.sos.util.CodingHelper.encodeObjectToXml;

import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

public abstract class AbstractAqdResponseEncoder<T extends AbstractServiceResponse> extends AbstractResponseEncoder<T> {
	
    public AbstractAqdResponseEncoder(String operation, Class<T> responseType) {
        super(AqdConstants.AQD, AqdConstants.VERSION, operation, AqdConstants.NS_AQD,
        		AqdConstants.NS_AQD_PREFIX, responseType);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(AqdConstants.NS_AQD_SCHEMA_LOCATION);
    }
    
	protected EReportingHeader getEReportingHeader(ReportObligationType type)
			throws CodedException {
		return ReportObligationRepository.getInstance().createHeader(type);
	}

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }
    
    protected AqdHelper getAqdHelper() {
        return AqdHelper.getInstance();
    }

    protected XmlObject encodeGml(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected XmlObject encodeGml(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }

    protected XmlObject encodeOws(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(OWSConstants.NS_OWS, o);
    }

    protected XmlObject encodeOws(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(OWSConstants.NS_OWS, o, helperValues);
    }

    protected XmlObject encodeFes(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o);
    }

    protected XmlObject encodeFes(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o, helperValues);
    }

    protected XmlObject encodeSwe(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected XmlObject encodeSwe(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }
    
    protected AbstractServiceResponse changeResponseServiceVersion(AbstractServiceResponse response) {
    	response.setService(SosConstants.SOS);
		response.setVersion(Sos2Constants.SERVICEVERSION);
		return response;
	}
    
    /**
     * Get the {@link Encoder} for the {@link AbstractServiceResponse} and the
     * requested contentType
     * 
     * @param asr
     *            {@link AbstractServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link AbstractServiceResponse}
     */
    protected Encoder<Object, AbstractServiceResponse> getEncoder(AbstractServiceResponse asr) {
        OperationEncoderKey key = new OperationEncoderKey(asr.getOperationKey(), getContentType());
        Encoder<Object, AbstractServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new RuntimeException(new NoEncoderForKeyException(new OperationEncoderKey(asr.getOperationKey(),
                    getContentType())));
        }
        return encoder;
    }
    
    /**
     * Getter for encoder, encapsulates the instance call
     * 
     * @param key
     *            Encoder key
     * @return Matching encoder
     */
    protected <D, S> Encoder<D, S> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }
    
	protected XmlObject encodeWithSosEncoder(T response) throws OwsExceptionReport {
		Encoder<Object, AbstractServiceResponse> encoder = getEncoder(changeResponseServiceVersion(response));
		if (encoder != null) {
			Object encode = encoder.encode(response);
            if (encode != null && encode instanceof XmlObject) {
            	return (XmlObject)encode;
            }
		}
		return null;
	}

}

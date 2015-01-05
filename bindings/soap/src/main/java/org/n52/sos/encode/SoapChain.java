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
package org.n52.sos.encode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.soap.SoapRequest;
import org.n52.sos.soap.SoapResponse;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class SoapChain {
    private final HttpServletRequest httpRequest;

    private final HttpServletResponse httpResponse;

    private AbstractServiceRequest<?> bodyRequest;

    private AbstractServiceResponse bodyResponse;

    private SoapRequest soapRequest;

    private SoapResponse soapResponse = new SoapResponse();

    public SoapChain(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public AbstractServiceRequest<?> getBodyRequest() {
        return bodyRequest;
    }

    public boolean hasBodyRequest() {
        return getBodyRequest() != null;
    }

    public void setBodyRequest(AbstractServiceRequest<?> bodyRequest) {
        this.bodyRequest = bodyRequest;
    }

    public AbstractServiceResponse getBodyResponse() {
        return bodyResponse;
    }

    public boolean hasBodyResponse() {
        return getBodyResponse() != null;
    }

    public void setBodyResponse(AbstractServiceResponse bodyResponse) {
        this.bodyResponse = bodyResponse;
        if (hasSoapResponse()) {
            getSoapResponse().setBodyContent(bodyResponse);
        }
    }

    public SoapRequest getSoapRequest() {
        return soapRequest;
    }

    public boolean hasSoapRequest() {
        return getSoapRequest() != null;
    }

    public void setSoapRequest(SoapRequest soapRequest) {
        this.soapRequest = soapRequest;
    }

    public SoapResponse getSoapResponse() {
        return soapResponse;
    }

    public boolean hasSoapResponse() {
        return getSoapResponse() != null;
    }

    public void setSoapResponse(SoapResponse soapResponse) {
        this.soapResponse = soapResponse;
        if (hasBodyResponse() && !soapResponse.isSetBodyContent()) {
            soapResponse.setBodyContent(getBodyResponse());
        }
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public boolean hasHttpRequest() {
        return getHttpRequest() != null;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public boolean hasHttpResponse() {
        return getHttpResponse() != null;
    }
}

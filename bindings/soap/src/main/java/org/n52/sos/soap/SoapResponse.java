/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.soap;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.SoapHeader;

/**
 * @since 4.0.0
 * 
 */
public class SoapResponse {

    private String soapNamespace;

    private String soapVersion;

    private String soapAction;

    private SoapFault soapFault;

    private XmlObject bodyContent;

    private Map<String, SoapHeader> header;

    private OwsExceptionReport exception;

    public SoapResponse() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the soapNamespace
     */
    public String getSoapNamespace() {
        return soapNamespace;
    }

    /**
     * @param soapNamespace
     *            the soapNamespace to set
     */
    public void setSoapNamespace(String soapNamespace) {
        this.soapNamespace = soapNamespace;
    }

    public boolean hasSoapNamespace() {
        return getSoapNamespace() != null && !getSoapNamespace().isEmpty();
    }

    /**
     * @return the soapVersion
     */
    public String getSoapVersion() {
        return soapVersion;
    }

    /**
     * @param soapVersion
     *            the soapVersion to set
     */
    public void setSoapVersion(String soapVersion) {
        this.soapVersion = soapVersion;
    }

    public boolean hasSoapVersion() {
        return getSoapVersion() != null && !getSoapVersion().isEmpty();
    }

    public void setSoapFault(SoapFault soapFault) {
        this.soapFault = soapFault;
    }

    public SoapFault getSoapFault() {
        return soapFault;
    }

    public XmlObject getSoapBodyContent() {
        return bodyContent;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapBodyContent(XmlObject bodyContent) {
        this.bodyContent = bodyContent;
    }

    public void setHeader(Map<String, SoapHeader> map) {
        this.header = map;
    }

    public Map<String, SoapHeader> getHeader() {
        return header;
    }

    public void setException(OwsExceptionReport owse) {
        this.exception = owse;
    }

    public OwsExceptionReport getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }

}

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
package org.n52.sos.soap;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.service.SoapHeader;

/**
 * @since 4.0.0
 * 
 */
public class SoapRequest {

    private String soapNamespace;

    private String soapVersion;

    private SoapFault soapFault;

    private XmlObject soapBodyContent;

    private String soapAction;

    private List<SoapHeader> soapHeader;

    public SoapRequest(String soapNamespace, String soapVersion) {
        this.soapNamespace = soapNamespace;
        this.soapVersion = soapVersion;
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

    public void setSoapFault(SoapFault fault) {
        this.soapFault = fault;

    }

    public SoapFault getSoapFault() {
        return soapFault;
    }

    public boolean hasSoapFault() {
        return getSoapFault() != null;
    }

    public XmlObject getSoapBodyContent() {
        return soapBodyContent;
    }

    public void setSoapBodyContent(XmlObject soapBodyContent) {
        this.soapBodyContent = soapBodyContent;

    }

    public void setAction(String soapAction) {
        this.soapAction = soapAction;

    }

    public void setSoapHeader(List<SoapHeader> soapHeader) {
        this.soapHeader = soapHeader;
    }

    /**
     * @return the soapAction
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @return the soapHeader
     */
    public List<SoapHeader> getSoapHeader() {
        return soapHeader;
    }

}

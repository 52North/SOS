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
package org.n52.sos.wsa;

import org.n52.sos.service.SoapHeader;

/**
 * @since 4.0.0
 * 
 */
public class WsaHeader implements SoapHeader {

    private String toValue;

    private String actionValue;

    private String replyToAddress;

    private String messageID;

    /**
     * @return the toValue
     */
    public String getToValue() {
        return toValue;
    }

    /**
     * @param toValue
     *            the toValue to set
     */
    public void setToValue(String toValue) {
        this.toValue = toValue;
    }

    /**
     * @return the actionValue
     */
    public String getActionValue() {
        return actionValue;
    }

    /**
     * @param actionValue
     *            the actionValue to set
     */
    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    /**
     * @return the replyToAddress
     */
    public String getReplyToAddress() {
        return replyToAddress;
    }

    /**
     * @param replyToAddress
     *            the replyToAddress to set
     */
    public void setReplyToAddress(String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    /**
     * @return the messageID
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @param messageID
     *            the messageID to set
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

}

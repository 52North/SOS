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
package org.n52.sos.wsa;

import javax.xml.namespace.QName;

/**
 * Constants for WS-Addressing
 * 
 * @since 4.0.0
 * 
 */
public interface WsaConstants {
    /**
     * WSA fault action URI
     */
    String WSA_FAULT_ACTION = "http://www.w3.org/2005/08/addressing/fault";

    /**
     * WSA namespace
     */
    String NS_WSA = "http://www.w3.org/2005/08/addressing";

    /**
     * WSA prefix
     */
    String NS_WSA_PREFIX = "wsa";

    /**
     * WSA to element
     */
    String EN_TO = "To";

    /**
     * WSA action element
     */
    String EN_ACTION = "Action";

    /**
     * WSA replyTo element
     */
    String EN_REPLY_TO = "ReplyTo";

    /**
     * WSA address element
     */
    String EN_ADDRESS = "Address";

    /**
     * WSA messageID element
     */
    String EN_MESSAGE_ID = "MessageID";

    /**
     * WSA relatesTo element
     */
    String EN_RELATES_TO = "RelatesTo";

    QName QN_TO = new QName(NS_WSA, EN_TO, NS_WSA_PREFIX);

    QName QN_ACTION = new QName(NS_WSA, EN_ACTION, NS_WSA_PREFIX);

    QName QN_REPLY_TO = new QName(NS_WSA, EN_REPLY_TO, NS_WSA_PREFIX);

    QName QN_ADDRESS = new QName(NS_WSA, EN_ADDRESS, NS_WSA_PREFIX);

    QName QN_MESSAGE_ID = new QName(NS_WSA, EN_MESSAGE_ID, NS_WSA_PREFIX);

    QName QN_RELATES_TO = new QName(NS_WSA, EN_RELATES_TO, NS_WSA_PREFIX);
}

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
package org.n52.sos.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper class for W3C
 * 
 * @since 4.0.0
 * 
 */
public final class W3cHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(W3cHelper.class);

    /**
     * Parses w3c.Node to String
     * 
     * @param node
     *            Node to parse.
     * 
     * @return Node as String.
     * 
     * @throws OwsExceptionReport
     *             if an error occurs.
     */
    public static String nodeToXmlString(Node node) throws OwsExceptionReport {
        String xmlString = Constants.EMPTY_STRING;
        StringWriter sw = null;
        try {
            sw = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(node), new StreamResult(sw));
            xmlString = sw.toString();
        } catch (TransformerException te) {
            throw new NoApplicableCodeException().causedBy(te).withMessage(
                    "The request was sent in an unknown format or is invalid!");
        } finally {
            try {
                if (sw != null) {
                    sw.close();
                }
            } catch (IOException ioe) {
                LOGGER.error("cannot close string writer", ioe);
            }
        }
        return xmlString;
    }

    /**
     * Get text content from element by namespace.
     * 
     * @param element
     *            element
     * @param namespaceURI
     *            Namespace URI
     * @param localName
     *            local name
     * 
     * @return Text content.
     */
    public static String getContentFromElement(Element element, String namespaceURI, String localName) {
        String elementContent = null;
        NodeList nodes = element.getElementsByTagNameNS(namespaceURI, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            elementContent = nodes.item(i).getTextContent();
        }
        return elementContent;
    }

    private W3cHelper() {
    }
}

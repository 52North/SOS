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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.swes.InvalidRequestException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.util.http.HTTPUtils;
import org.n52.sos.w3c.W3CConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * XML utility class TODO add javadoc to public methods.
 * 
 * @since 4.0.0
 * 
 */
public final class XmlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlHelper.class);

    private XmlHelper() {
    }

    /**
     * Parse XML document from HTTP-Post request.
     * 
     * @param request
     *            HTTP-Post request
     * @return XML document
     * 
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public static XmlObject parseXmlSosRequest(final HttpServletRequest request) throws OwsExceptionReport {
        XmlObject doc;
        try {
            if (request.getParameterMap().isEmpty()) {
                final String requestContent =
                        StringHelper.convertStreamToString(HTTPUtils.getInputStream(request),
                                request.getCharacterEncoding());
                doc = parseXmlString(requestContent);
            } else {
                doc =
                        XmlObject.Factory.parse(SosHelper.parseHttpPostBodyWithParameter(request.getParameterNames(),
                                request.getParameterMap()));
            }
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "An xml error occured when parsing the request! Message: %s", xmle.getMessage());
        } catch (final IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage(
                    "Error while reading request! Message: %s", ioe.getMessage());
        }
        // validateDocument(doc);
        return doc;
    }

    public static XmlObject parseXmlString(final String xmlString) throws OwsExceptionReport {
        try {
            return XmlObject.Factory.parse(xmlString);
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "An xml error occured when parsing the request! Message: %s", xmle.getMessage());
        }
    }

    /**
     * Get element Node from NodeList.
     * 
     * @param nodeList
     *            NodeList.
     * @return Element Node
     */
    public static Node getNodeFromNodeList(final NodeList nodeList) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

    /**
     * checks whether the XMLDocument is valid
     * 
     * @param doc
     *            the document which should be checked
     * 
     * @throws OwsExceptionReport
     *             * if the Document is not valid
     */
    /*
     * TODO Replace this version with a method that uses LaxValidationCases and provides means to access the errors after validating the document
     */
    public static boolean validateDocument(final XmlObject doc) throws OwsExceptionReport {
        // Create an XmlOptions instance and set the error listener.
        final LinkedList<XmlError> validationErrors = new LinkedList<XmlError>();
        final XmlOptions validationOptions = new XmlOptions()
        	.setErrorListener(validationErrors)
        	.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);

        // Validate the GetCapabilitiesRequest XML document
        final boolean isValid = doc.validate(validationOptions);

        // Create Exception with error message if the xml document is invalid
        if (!isValid) {

            String message = null;

            // getValidation error and throw service exception for the first
            // error
            final Iterator<XmlError> iter = validationErrors.iterator();
            final List<XmlError> shouldPassErrors = new LinkedList<XmlError>();
            final List<XmlError> errors = new LinkedList<XmlError>();
            while (iter.hasNext()) {
                final XmlError error = iter.next();
                boolean shouldPass = false;
                if (error instanceof XmlValidationError) {
                    for (final LaxValidationCase lvc : LaxValidationCase.values()) {
                        if (lvc.shouldPass((XmlValidationError) error)) {
                            shouldPass = true;
                            LOGGER.debug("Lax validation case found for XML validation error: {}", error);
                            break;
                        }
                    }
                }
                if (shouldPass) {
                    shouldPassErrors.add(error);
                } else {
                    errors.add(error);
                }
            }
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final XmlError error : errors) {

                // get name of the missing or invalid parameter
                message = error.getMessage();
                if (message != null) {

                    exceptions.add(new InvalidRequestException().at(message).withMessage(
                            "[XmlBeans validation error:] %s", message));

                    /*
                     * TODO check if code can be used for validation of SOS
                     * 1.0.0 requests // check, if parameter is missing or value
                     * of parameter // is // invalid to ensure, that correct //
                     * exceptioncode in exception response is used
                     * 
                     * // invalid parameter value if
                     * (message.startsWith("The value")) { exCode =
                     * OwsExceptionCode.InvalidParameterValue;
                     * 
                     * // split message string to get attribute name String[]
                     * messAndAttribute = message.split("attribute '"); if
                     * (messAndAttribute.length == 2) { parameterName =
                     * messAndAttribute[1].replace("'", ""); } }
                     * 
                     * // invalid enumeration value --> InvalidParameterValue
                     * else if
                     * (message.contains("not a valid enumeration value")) {
                     * exCode = OwsExceptionCode.InvalidParameterValue;
                     * 
                     * // get attribute name String[] messAndAttribute =
                     * message.split(" "); parameterName = messAndAttribute[10];
                     * }
                     * 
                     * // mandatory attribute is missing --> //
                     * missingParameterValue else if
                     * (message.startsWith("Expected attribute")) { exCode =
                     * OwsExceptionCode.MissingParameterValue;
                     * 
                     * // get attribute name String[] messAndAttribute =
                     * message.split("attribute: "); if (messAndAttribute.length
                     * == 2) { String[] attrAndRest =
                     * messAndAttribute[1].split(" in"); if (attrAndRest.length
                     * == 2) { parameterName = attrAndRest[0]; } } }
                     * 
                     * // mandatory element is missing --> //
                     * missingParameterValue else if
                     * (message.startsWith("Expected element")) { exCode =
                     * SwesExceptionCode.InvalidRequest;
                     * 
                     * // get element name String[] messAndElements =
                     * message.split(" '"); if (messAndElements.length >= 2) {
                     * String elements = messAndElements[1]; if
                     * (elements.contains("offering")) { parameterName =
                     * "offering"; } else if
                     * (elements.contains("observedProperty")) { parameterName =
                     * "observedProperty"; } else if
                     * (elements.contains("responseFormat")) { parameterName =
                     * "responseFormat"; } else if
                     * (elements.contains("procedure")) { parameterName =
                     * "procedure"; } else if
                     * (elements.contains("featureOfInterest")) { parameterName
                     * = "featureOfInterest"; } else { // TODO check if other
                     * elements are invalid } } } // invalidParameterValue else
                     * if (message.startsWith("Element")) { exCode =
                     * OwsExceptionCode.InvalidParameterValue;
                     * 
                     * // get element name String[] messAndElements =
                     * message.split(" '"); if (messAndElements.length >= 2) {
                     * String elements = messAndElements[1]; if
                     * (elements.contains("offering")) { parameterName =
                     * "offering"; } else if
                     * (elements.contains("observedProperty")) { parameterName =
                     * "observedProperty"; } else if
                     * (elements.contains("responseFormat")) { parameterName =
                     * "responseFormat"; } else if
                     * (elements.contains("procedure")) { parameterName =
                     * "procedure"; } else if
                     * (elements.contains("featureOfInterest")) { parameterName
                     * = "featureOfInterest"; } else { // TODO check if other
                     * elements are invalid } } } else { // create service
                     * exception OwsExceptionReport se = new
                     * OwsExceptionReport();
                     * se.addCodedException(SwesExceptionCode.InvalidRequest,
                     * message, "[XmlBeans validation error:] " + message);
                     * LOGGER.error("The request is invalid!", se); throw se; }
                     * 
                     * // create service exception OwsExceptionReport se = new
                     * OwsExceptionReport(); se.addCodedException(exCode,
                     * message, "[XmlBeans validation error:] " + message);
                     * LOGGER.error("The request is invalid!", se); throw se;
                     */

                }
            }
            exceptions.throwIfNotEmpty();
        }
        return isValid;
    }

    /**
     * Loads a XML document from File.
     * 
     * @param file
     *            File
     * @return XML document
     * 
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public static XmlObject loadXmlDocumentFromFile(final File file) throws OwsExceptionReport {
        InputStream is = null;
        try {
            is = FileIOHelper.loadInputStreamFromFile(file);
            return XmlObject.Factory.parse(is);
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing file %s!",
                    file.getName());
        } catch (final IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while parsing file %s!",
                    file.getName());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException ioe) {
                    LOGGER.warn("Error while closing the file " + file.getName() + " input stream!", ioe);
                }
            }
        }
    }

    /**
     * Recurse through a node and its children and make all gml:ids unique
     * 
     * @param node
     *            The root node
     */
    public static void makeGmlIdsUnique(final Node node) {
        makeGmlIdsUnique(node, new HashMap<String, Integer>());
    }

    /**
     * Recurse through a node and its children and make all gml:ids unique
     * 
     * @param node
     *            The node to examine
     */
    public static void makeGmlIdsUnique(final Node node, final Map<String, Integer> foundIds) {
        // check this node's attributes
        final NamedNodeMap attributes = node.getAttributes();
        final String nodeNamespace = node.getNamespaceURI();
        if (attributes != null) {
            for (int i = 0, len = attributes.getLength(); i < len; i++) {
                final Attr attr = (Attr) attributes.item(i);
                if (attr.getLocalName().equals(GmlConstants.AN_ID)) {
                    if (checkAttributeForGmlId(attr, nodeNamespace)) {
                        final String gmlId = attr.getValue();
                        if (foundIds.containsKey(gmlId)) {
                            /*
                             * id has already been found, suffix this one with
                             * the found count for this id
                             */
                            attr.setValue(gmlId + foundIds.get(gmlId));
                            // increment the found count for this id
                            foundIds.put(gmlId, foundIds.get(gmlId) + 1);
                        } else {
                            // id is new, add it to the foundIds map
                            foundIds.put(gmlId, 1);
                        }
                    }
                }
            }
        }

        // recurse this node's children
        final NodeList children = node.getChildNodes();
        if (children != null) {
            for (int i = 0, len = children.getLength(); i < len; i++) {
                makeGmlIdsUnique(children.item(i), foundIds);
            }
        }
    }

    public static void updateGmlIDs(final Node node, final String gmlID, String oldGmlID) {
        // check this node's attributes
        if (node != null) {
            final String nodeNamespace = node.getNamespaceURI();
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int i = 0, len = attributes.getLength(); i < len; i++) {
                    final Attr attr = (Attr) attributes.item(i);
                    if (attr.getLocalName().equals(GmlConstants.AN_ID)) {
                        if (checkAttributeForGmlId(attr, nodeNamespace)) {
                            if (oldGmlID == null) {
                                oldGmlID = attr.getValue();
                                attr.setValue((gmlID));
                            } else {
                                String helperString = attr.getValue();
                                helperString = helperString.replace(oldGmlID, gmlID);
                                attr.setValue(helperString);
                            }
                        }
                    }
                }
                // recurse this node's children
                final NodeList children = node.getChildNodes();
                if (children != null) {
                    for (int i = 0, len = children.getLength(); i < len; i++) {
                        updateGmlIDs(children.item(i), gmlID, oldGmlID);
                    }
                }
            }
        }
    }

    /**
     * Check if attribute or node namespace is a GML id.
     * 
     * @param attr
     *            Attribure to check
     * @param nodeNamespace
     *            Node namespace
     * @return <code>true</code>, if attribute or node is a GML id
     */
    private static boolean checkAttributeForGmlId(Attr attr, String nodeNamespace) {
        final String attrNamespace = attr.getNamespaceURI();
        if (GmlConstants.GML_ID_WITH_PREFIX.equals(attr.getName())) {
            return true;
        } else {
            if (StringHelper.isNotEmpty(attrNamespace)) {
                return isNotNullAndEqualsNSs(attrNamespace, getGmlNSs());
            } else {
                return isNotNullAndEqualsNSs(nodeNamespace, getGmlNSs());
            }
        }
    }

    /**
     * Check if namespace is not null and equals GML 3.1.1 or GML 3.2.1
     * namespace.
     * 
     * @param namespaceToCheck
     *            Namespace to check
     * @param namespaces
     *            GML namespaces
     * @return <code>true</code>, if namespaceToCheck is a GML namespace
     */
    private static boolean isNotNullAndEqualsNSs(String namespaceToCheck, Collection<String> namespaces) {
        if (StringHelper.isNotEmpty(namespaceToCheck)) {
            return namespaces.contains(namespaceToCheck);
        }
        return false;
    }

    /**
     * Get set with GML 3.1.1 and GML 3.2.1 namespaces.
     * 
     * @return GML namespace set
     */
    private static Collection<String> getGmlNSs() {
        return Sets.newHashSet(GmlConstants.NS_GML, GmlConstants.NS_GML_32);
    }

    public static String getNamespace(final XmlObject doc) {
        String namespaceURI = doc.getDomNode().getNamespaceURI();
        if (namespaceURI == null && doc.getDomNode().getFirstChild() != null) {
            namespaceURI = doc.getDomNode().getFirstChild().getNamespaceURI();
        }
        /*
         * if document starts with a comment, get next sibling (and ignore
         * initial comment)
         */
        if (namespaceURI == null && doc.getDomNode().getFirstChild() != null
                && doc.getDomNode().getFirstChild().getNextSibling() != null) {
            namespaceURI = doc.getDomNode().getFirstChild().getNextSibling().getNamespaceURI();
        }
        // check with schemaType namespace, necessary for anyType elements
        final String schemaTypeNamespace = getSchemaTypeNamespace(doc);
        if (schemaTypeNamespace == null) {
            return namespaceURI;
        } else {
            if (schemaTypeNamespace.equals(namespaceURI)) {
                return namespaceURI;
            } else {
                return schemaTypeNamespace;
            }
        }

    }

    private static String getSchemaTypeNamespace(final XmlObject doc) {
        QName name = null;
        if (doc.schemaType().isAttributeType()) {
            name = doc.schemaType().getAttributeTypeAttributeName();
        } else {
            // TODO check else/if for ...schemaType().isDocumentType ?
            name = doc.schemaType().getName();
        }
        if (name != null) {
            return name.getNamespaceURI();
        }
        return null;
    }

    public static XmlObject substituteElement(final XmlObject elementToSubstitute, final XmlObject substitutionElement) {
        final Node domNode = substitutionElement.getDomNode();
        QName name = null;
        if (domNode.getNamespaceURI() != null && domNode.getLocalName() != null) {
            final String prefix = getPrefixForNamespace(elementToSubstitute, domNode.getNamespaceURI());
            if (prefix != null && !prefix.isEmpty()) {
                name = new QName(domNode.getNamespaceURI(), domNode.getLocalName(), prefix);
            } else {
                name = new QName(domNode.getNamespaceURI(), domNode.getLocalName());
            }
        } else {
            final QName nameOfElement = substitutionElement.schemaType().getName();
            final String localPart =
                    nameOfElement.getLocalPart().replace(GmlConstants.EN_PART_TYPE, Constants.EMPTY_STRING);
            name =
                    new QName(nameOfElement.getNamespaceURI(), localPart, getPrefixForNamespace(elementToSubstitute,
                            nameOfElement.getNamespaceURI()));
        }
        return substituteElement(elementToSubstitute, substitutionElement.schemaType(), name);
    }

    public static String getPrefixForNamespace(final XmlObject element, final String namespace) {
        final XmlCursor cursor = element.newCursor();
        final String prefix = cursor.prefixForNamespace(namespace);
        cursor.dispose();
        return prefix;
    }

    public static XmlObject substituteElement(final XmlObject elementToSubstitute, final SchemaType schemaType,
            final QName name) {
        return elementToSubstitute.substitute(name, schemaType);
    }

    public static String getLocalName(final XmlObject element) {
        return (element == null) ? null : element.getDomNode().getLocalName();
    }

    /**
     * Interface for providing exceptional cases in XML validation (e.g.
     * substitution groups).
     * 
     * FIXME Review code and use new procedure from OX-F to validate offending content!
     */
    public enum LaxValidationCase { // FIXME make private again
        ABSTRACT_OFFERING {
            @SuppressWarnings("unchecked")
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                return checkQNameIsExpected(xve.getFieldQName(), SwesConstants.QN_OFFERING)
                        && checkExpectedQNamesContainsQNames(xve.getExpectedQNames(),
                                Lists.newArrayList(SwesConstants.QN_ABSTRACT_OFFERING))
                        && checkMessageOrOffendingQName(xve, Sos2Constants.QN_OBSERVATION_OFFERING);
            }
        },
        /**
         * Allow substitutions of gml:AbstractFeature. This lax validation lets
         * pass every child, hence it checks not _if_ this is a valid
         * substitution.
         */
        ABSTRACT_FEATURE_GML {
            @SuppressWarnings("unchecked")
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                return checkExpectedQNamesContainsQNames(xve.getExpectedQNames(), Lists.newArrayList(
                        GmlConstants.QN_ABSTRACT_FEATURE_GML, GmlConstants.QN_ABSTRACT_FEATURE_GML_32));
            }
        },
        ABSTRACT_TIME_GML_3_2_1 {
            @SuppressWarnings("unchecked")
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                return checkExpectedQNamesContainsQNames(xve.getExpectedQNames(),
                        Lists.newArrayList(GmlConstants.QN_ABSTRACT_TIME_32));
            }
        },
        SOS_INSERTION_META_DATA {
            @SuppressWarnings("unchecked")
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                return checkQNameIsExpected(xve.getFieldQName(), SwesConstants.QN_METADATA)
                        && checkExpectedQNamesContainsQNames(xve.getExpectedQNames(),
                                Lists.newArrayList(SwesConstants.QN_INSERTION_METADATA))
                        && checkMessageOrOffendingQName(xve, Sos2Constants.QN_SOS_INSERTION_METADATA);
            }
        },
        SOS_INSERTION_META_DATA_2 {
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                return checkQNameIsExpected(xve.getOffendingQName(), SwesConstants.QN_INSERTION_METADATA)
                        && xve.getCursorLocation().getAttributeText(W3CConstants.QN_XSI_TYPE)
                                .contains(SOS_INSERTION_METADATA_TYPE);
            }
        },
        SOS_GET_DATA_AVAILABILITY_RESPONSE {
            @Override
            public boolean shouldPass(final XmlValidationError xve) {
                if (xve.getObjectLocation() != null && xve.getObjectLocation().getDomNode() != null
                        && xve.getObjectLocation().getDomNode().getFirstChild() != null) {
                    String nodeName = xve.getObjectLocation().getDomNode().getFirstChild().getNodeName();
                    return StringHelper.isNotEmpty(nodeName) && nodeName.contains(GET_DATA_AVAILABILITY);
                }
                return false;
            }
        };

        private static final String BEFORE_END_CONTENT_ELEMENT = "before the end of the content in element";

        private static final String SOS_INSERTION_METADATA_TYPE = "SosInsertionMetadataType";

        private static final String GET_DATA_AVAILABILITY = "GetDataAvailability";

        public abstract boolean shouldPass(XmlValidationError xve);

        /**
         * Check if the QName equals expected QName
         * 
         * @param qName
         *            QName to check
         * @param expected
         *            Expected QName
         * @return <code>true</code>, if QName equals expected QName
         */
        private static boolean checkQNameIsExpected(QName qName, QName expected) {
            return qName != null && qName.equals(expected);
        }

        /**
         * Check if expected QNames contains one QName
         * 
         * @param expected
         *            Expected QNames
         * @param shouldContain
         *            Contains expected QNames this
         * @return <code>true</code>, if expected QNames contains one QName
         */
        private static boolean checkExpectedQNamesContainsQNames(List<QName> expected, List<QName> shouldContain) {
            if (CollectionHelper.isNotEmpty(expected)) {
                for (QName qName : shouldContain) {
                    if (expected.contains(qName)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Check if message contains defined pattern or offending QName equals
         * expected
         * 
         * @param xve
         *            Xml validation error
         * @param expectedOffendingQname
         *            Expected offending QName
         * @return <code>true</code>, if message contains defined pattern or
         *         offending QName equals expected
         */
        private static boolean checkMessageOrOffendingQName(XmlValidationError xve, QName expectedOffendingQname) {
            return xve.getMessage().contains(BEFORE_END_CONTENT_ELEMENT)
                    || checkQNameIsExpected(xve.getOffendingQName(), expectedOffendingQname);
        }
    }

    /**
     * Utility method to append the contents of the child docment to the end of
     * the parent XmlObject. This is useful when dealing with elements without
     * generated methods (like elements with xs:any)
     * 
     * @param parent
     *            Parent to append contents to
     * @param childDoc
     *            Xml document containing contents to be appended
     */
    public static void append(final XmlObject parent, final XmlObject childDoc) {
        final XmlCursor parentCursor = parent.newCursor();
        parentCursor.toEndToken();

        final XmlCursor childCursor = childDoc.newCursor();
        childCursor.toFirstChild();

        childCursor.moveXml(parentCursor);
        parentCursor.dispose();
        childCursor.dispose();
    }

    /**
     * Remove namespace declarations from an xml fragment (useful for moving all
     * declarations to a document root
     * 
     * @param x
     *            The fragment to localize
     */
    public static void removeNamespaces(final XmlObject x) {
        final XmlCursor c = x.newCursor();
        while (c.hasNextToken()) {
            if (c.isNamespace()) {
                c.removeXml();
            } else {
                c.toNextToken();
            }
        }
        c.dispose();
    }

    /**
     * Remove the element from XML document
     * 
     * @param element
     *            Element to remove
     * @return <code>true</code>, if element is removed
     */
    public static boolean removeElement(XmlObject element) {
        XmlCursor cursor = element.newCursor();
        boolean removed = cursor.removeXml();
        cursor.dispose();
        return removed;
    }

    public static void fixNamespaceForXsiType(final XmlObject object, final QName value) {
        final XmlCursor cursor = object.newCursor();
        while (cursor.hasNextToken()) {
            if (cursor.toNextToken().isStart()) {
                final String xsiType = cursor.getAttributeText(W3CConstants.QN_XSI_TYPE);
                if (xsiType != null) {

                    final String[] toks = xsiType.split(":");
                    String localName;
                    String prefix;
                    if (toks.length > 1) {
                        prefix = toks[0];
                        localName = toks[1];
                    } else {
                        localName = toks[0];
                    }
                    if (localName.equals(value.getLocalPart())) {
                        cursor.setAttributeText(
                                W3CConstants.QN_XSI_TYPE,
                                Joiner.on(Constants.COLON_CHAR).join(
                                        XmlHelper.getPrefixForNamespace(object, value.getNamespaceURI()),
                                        value.getLocalPart()));
                    }
                }
            }
        }
        cursor.dispose();
    }

    public static void fixNamespaceForXsiType(XmlObject content, Map<?, ?> namespaces) {
        final XmlCursor cursor = content.newCursor();
        while (cursor.hasNextToken()) {
            if (cursor.toNextToken().isStart()) {
                final String xsiType = cursor.getAttributeText(W3CConstants.QN_XSI_TYPE);
                if (xsiType != null) {
                    final String[] toks = xsiType.split(":");
                    if (toks.length > 1) {
                        String prefix = toks[0];
                        String localName = toks[1];
                        String namespace =(String) namespaces.get(prefix);
                        if (Strings.isNullOrEmpty(namespace)) {
                            namespace = CodingRepository.getInstance().getNamespaceFor(prefix);
                        }
                        if (StringHelper.isNotEmpty(namespace)) {
                            cursor.setAttributeText(
                                    W3CConstants.QN_XSI_TYPE,
                                    Joiner.on(Constants.COLON_CHAR).join(
                                            XmlHelper.getPrefixForNamespace(content, (String)namespaces.get(prefix)),
                                            localName));
                        }
                    }
                   
                }
            }
        }
        cursor.dispose();
        
    }

    public static Map<?, ?> getNamespaces(XmlObject xmlObject) {
      XmlCursor cursor = xmlObject.newCursor();
      Map<?,?> nsMap = Maps.newHashMap();
      cursor.getAllNamespaces(nsMap);
      cursor.dispose();
      return nsMap;
    }

    /**
     * @param prefix
     * @param namespace
     * @return
     */
    public static String getXPathPrefix(String prefix, String namespace) {
        return String.format("declare namespace %s='%s';", prefix, namespace);
    }

}

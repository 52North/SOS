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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Set;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@code Decoder} to handle {@link GetDataAvailabilityRequest}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityXmlDecoder extends AbstractXmlDecoder<GetDataAvailabilityRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(GetDataAvailabilityXmlDecoder.class);

    private static final String BASE_PATH_SOS = getBasePath(Sos2Constants.XPATH_PREFIX_SOS_20,
            SosConstants.NS_SOS_PREFIX);

    private static final String BASE_PATH_GDA = getBasePath(GetDataAvailabilityConstants.XPATH_PREFIXES_GDA,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            Sos2Constants.NS_SOS_20, XmlObject.class), CodingHelper.decoderKeysForElements(
            GetDataAvailabilityConstants.NS_GDA, XmlObject.class), CodingHelper.xmlDecoderKeysForOperation(
            SosConstants.SOS, Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME));

    /**
     * Constructs a new {@code GetDataAvailabilityDecoder}.
     */
    public GetDataAvailabilityXmlDecoder() {
        LOG.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(DECODER_KEYS));
    }

    private static String getBasePath(String basePath, String prefix) {
        StringBuilder builder = new StringBuilder();
        builder.append(basePath);
        builder.append("/");
        builder.append(prefix);
        builder.append(":");
        builder.append("GetDataAvailability");
        return builder.toString();
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public GetDataAvailabilityRequest decode(XmlObject xml) throws OwsExceptionReport {
        return parseGetDataAvailability(xml);
    }

    /**
     * Parses a {@code GetDataAvailabilityRequest}.
     * 
     * @param xml
     *            the request
     * 
     * @return the parsed request
     * @throws OwsExceptionReport
     */
    public GetDataAvailabilityRequest parseGetDataAvailability(XmlObject xml) throws OwsExceptionReport {
        XmlObject[] roots = xml.selectPath(BASE_PATH_SOS);
        if (roots != null && roots.length > 0) {
            return parseGetDataAvailability(xml, BASE_PATH_SOS, Sos2Constants.XPATH_PREFIX_SOS_20,
                    SosConstants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20);
        } else {
            roots = xml.selectPath(BASE_PATH_GDA);
            if (roots != null && roots.length > 0) {
                return parseGetDataAvailability(xml, BASE_PATH_GDA, GetDataAvailabilityConstants.XPATH_PREFIXES_GDA,
                        GetDataAvailabilityConstants.NS_GDA_PREFIX, GetDataAvailabilityConstants.NS_GDA);
            }
        }
        return new GetDataAvailabilityRequest();
    }

    /**
     * Parse the GetDataAvailability XML request
     * 
     * @param xml
     *            GetDataAvailability XML request
     * @param basePath
     *            XPath base path
     * @param xpathPrefix
     *            XPath prefix
     * @param prefix
     *            XML document namespace prefix
     * @param namespace
     *            XML document namespace
     * @return {@code GetDataAvailabilityRequest}
     * @throws OwsExceptionReport
     *             If the document could no be parsed
     */
    private GetDataAvailabilityRequest parseGetDataAvailability(XmlObject xml, String basePath, String xpathPrefix,
            String prefix, String namespace) throws OwsExceptionReport {
        GetDataAvailabilityRequest request = new GetDataAvailabilityRequest();
        request.setNamespace(namespace);
        XmlObject[] roots = xml.selectPath(basePath);
        if (roots != null && roots.length > 0) {
            XmlObject version = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_VERSION);
            if (version == null) {
                version = roots[0].selectAttribute(GetDataAvailabilityConstants.VERSION);
            }
            if (version != null) {
                request.setVersion(parseStringValue(version));
            }
            XmlObject service = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_SERVICE);
            if (service == null) {
                service = roots[0].selectAttribute(GetDataAvailabilityConstants.SERVICE);
            }
            if (service != null) {
            request.setService(parseStringValue(service));
            }
        }

        for (XmlObject x : xml.selectPath(getPath(xpathPrefix, prefix, "observedProperty"))) {
            request.addObservedProperty(parseStringValue(x));
        }
        for (XmlObject x : xml.selectPath(getPath(xpathPrefix, prefix, "procedure"))) {
            request.addProcedure(parseStringValue(x));
        }
        for (XmlObject x : xml.selectPath(getPath(xpathPrefix, prefix, "featureOfInterest"))) {
            request.addFeatureOfInterest(parseStringValue(x));
        }
        for (XmlObject x : xml.selectPath(getPath(xpathPrefix, prefix, "offering"))) {
            request.addOffering(parseStringValue(x));
        }
        request.setExtensions(parseExtensions(xml));
        return request;
    }
    
    private String parseStringValue(XmlObject xmlObject) {
        if (xmlObject instanceof XmlString) {
            return ((XmlString) xmlObject).getStringValue();
        } else if (xmlObject instanceof XmlAnyURI) {
            return ((XmlAnyURI)xmlObject).getStringValue();
        } else {
            return ((XmlAnyTypeImpl) xmlObject).getStringValue();
        }
    }

    /**
     * Parse swes:extensions
     * 
     * @param xml
     *            swes:extension
     * @return parsed {@code SwesExtensions}
     * @throws OwsExceptionReport
     *             if the swes:extension could not be parsed
     */
    private SwesExtensions parseExtensions(XmlObject xml) throws OwsExceptionReport {
        SwesExtensions extensions = new SwesExtensions();
        for (XmlObject x : xml.selectPath(getPath(SwesConstants.XPATH_PREFIXES_SWES, SwesConstants.NS_SWES_PREFIX,
                "extension"))) {
            try {
                if (x.getDomNode().hasChildNodes()) {
                    Object obj =
                            CodingHelper.decodeXmlElement(XmlObject.Factory.parse(XmlHelper.getNodeFromNodeList(x
                                    .getDomNode().getChildNodes())));
                    SwesExtension<?> extension = null;
                    if (!(obj instanceof SwesExtension<?>)) {
                        extension = new SwesExtensionImpl<Object>().setValue(obj);
                        if (isSweAbstractDataComponent(obj)) {
                            extension.setDefinition(((SweAbstractDataComponent) obj).getDefinition());
                        }
                    } else {
                        extension = (SwesExtension<?>) obj;
                    }
                    extensions.addSwesExtension(extension);
                }
            } catch (XmlException xmle) {
                throw new XmlDecodingException("extension", xmle);
            }
        }
        return extensions;
    }

    /**
     * Check if the object is of type {@code SweAbstractDataComponent}
     * 
     * @param object
     *            Object to check
     * @return <code>true</code>, if the object is of type
     *         {@code SweAbstractDataComponent}
     */
    private boolean isSweAbstractDataComponent(final Object object) {
        return object instanceof SweAbstractDataComponent && ((SweAbstractDataComponent) object).isSetDefinition();
    }

    /**
     * Create path from values
     * 
     * @param xpathPrefix
     *            XPath prefix
     * @param prefix
     *            Namespace prefix
     * @param element
     *            Element name
     * @return XPath path
     */
    private String getPath(String xpathPrefix, String prefix, String element) {
        StringBuilder builder = new StringBuilder();
        builder.append(xpathPrefix);
        builder.append(".//");
        builder.append(prefix);
        builder.append(":");
        builder.append(element);
        return builder.toString();
    }
}

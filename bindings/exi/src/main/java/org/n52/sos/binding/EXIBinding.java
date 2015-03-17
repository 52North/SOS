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
package org.n52.sos.binding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.decode.Decoder;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.utils.EXIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * Binding implementation for EXI - Efficient XML Interchange See See <a
 * href="http://www.w3.org/TR/exi/">http://www.w3.org/TR/exi/</a>
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class EXIBinding extends SimpleBinding {

    private static final EXIUtils EXI_UTILS = EXIUtils.getInstance();

	private static final String URL_PATTERN = "/exi";

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/2.0/conf/exi");

    private static final Logger LOGGER = LoggerFactory.getLogger(EXIBinding.class);

    @Override
    public Set<String> getConformanceClasses() {
        return CONFORMANCE_CLASSES;
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public String getUrlPattern() {
        return URL_PATTERN;
    }
    
    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.singleton(MediaTypes.APPLICATION_EXI);
    }

    @Override
    protected boolean isUseHttpResponseCodes() {
        return true;
    }

    @Override
    public boolean checkOperationHttpPostSupported(OperationKey k) throws HTTPException {
        return hasDecoder(k, MediaTypes.TEXT_XML) || hasDecoder(k, MediaTypes.APPLICATION_XML);
    }

    @Override
    public void doPostOperation(HttpServletRequest req, HttpServletResponse res) throws HTTPException, IOException {
        AbstractServiceRequest<?> sosRequest = null;
        try {
            sosRequest = parseRequest(req);
            AbstractServiceResponse sosResponse = getServiceOperator(sosRequest).receiveRequest(sosRequest);
            writeResponse(req, res, sosResponse);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(sosRequest != null ? sosRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    /**
     * Parse and decode the incoming EXI encoded {@link InputStream}
     * 
     * @param request
     *            {@link HttpServletRequest} with EXI encoded
     *            {@link InputStream}
     * @return {@link AbstractServiceRequest} from EXI encoded
     *         {@link InputStream}
     * @throws OwsExceptionReport
     *             If an error occurs during parsing
     */
    protected AbstractServiceRequest<?> parseRequest(HttpServletRequest request) throws OwsExceptionReport {
        XmlObject doc = decode(request);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("EXI-REQUEST: {}", doc.xmlText());
        }
        Decoder<AbstractServiceRequest<?>, XmlObject> decoder = getDecoder(CodingHelper.getDecoderKey(doc));
        return decoder.decode(doc).setRequestContext(getRequestContext(request));
    }

    /**
     * Parse the incoming EXI encoded {@link InputStream} transform to
     * {@link XmlObject}
     * 
     * @param request
     *            {@link HttpServletRequest} with EXI encoded
     *            {@link InputStream}
     * @return {@link XmlObject} created from the EXI encoded
     *         {@link InputStream}
     * @throws OwsExceptionReport
     *             If an error occurs during parsing
     */
    protected XmlObject decode(HttpServletRequest request) throws OwsExceptionReport {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            
            EXIFactory ef = EXI_UTILS.newEXIFactory();
            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            if (ef.isFragment()) {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			}
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // decode EXI encoded InputStream
            EXISource exiSource = new EXISource(ef);
            XMLReader exiReader = exiSource.getXMLReader();
            InputSource inputSource = new InputSource(request.getInputStream());
            inputSource.setEncoding(request.getCharacterEncoding());
            SAXSource saxSource = new SAXSource(inputSource);
            saxSource.setXMLReader(exiReader);
            transformer.transform(saxSource, new StreamResult(os));
            
            // create XmlObject from OutputStream
            return XmlHelper.parseXmlString(os.toString());
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage(
                    "Error while reading request! Message: %s", ioe.getMessage());
        } catch (TransformerException te) {
            throw new NoApplicableCodeException().causedBy(te).withMessage(
                    "Error while transforming request! Message: %s", te.getMessage());
        } catch (EXIException exie) {
            throw new NoApplicableCodeException().causedBy(exie).withMessage(
                    "Error while reading request! Message: %s", exie.getMessage());
        }
    }

}

/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.service.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.encode.Encoder;
import org.n52.iceland.encode.EncoderKey;
import org.n52.iceland.encode.XmlEncoderKey;
import org.n52.iceland.exception.AdministratorException;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.response.ServiceResponse;
import org.n52.iceland.service.ConfiguratedHttpServlet;
import org.n52.iceland.service.Configurator;
import org.n52.iceland.util.XmlOptionsHelper;
import org.n52.iceland.util.http.HTTPHeaders;
import org.n52.iceland.util.http.HTTPUtils;
import org.n52.iceland.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The servlet of the SOS administration backend which receives the incoming
 * HttpGet requests and sends the operation result documents to the client
 * 
 * @since 4.0.0
 */
public class AdminService extends ConfiguratedHttpServlet {
    private static final long serialVersionUID = -3279981432309569992L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    /**
     * initializes the Servlet
     */
    @Override
    public void init() throws ServletException {
        super.init();
        LOGGER.info("Admin endpoint initalized successfully!");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LOGGER.debug("\n**********\n(GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());
        LOGGER.trace("Query String: " + req.getQueryString());

        this.setCorsHeaders(resp);

        ServiceResponse sosResp = null;
        try {
            sosResp = Configurator.getInstance().getAdminServiceOperator().doGetOperation(req);
        } catch (AdministratorException e) {
            handleException(new NoApplicableCodeException().withMessage("Error").causedBy(e));
        } catch (OwsExceptionReport e) {
            sosResp = handleException(e);
        }
        HTTPUtils.writeObject(req, resp, sosResp);
    }

    /**
     * Handles OPTIONS request to enable Cross-Origin Resource Sharing.
     * 
     * @param req
     *            the incoming request
     * 
     * @param resp
     *            the response for the incoming request
     */
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
        this.setCorsHeaders(resp);
    }

    /**
     * Set HTTPHeaders according to CORS to enable Cross-Domain JavaScript
     * access.
     * 
     * @see <a href="http://www.w3.org/TR/cors/">http://www.w3.org/TR/cors/</a>
     */
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HTTPHeaders.CONTENT_TYPE);
    }

    private ServiceResponse handleException(OwsExceptionReport owsExceptionReport) throws ServletException {
        try {

            EncoderKey key = new XmlEncoderKey(owsExceptionReport.getNamespace(), owsExceptionReport.getClass());
            Encoder<?, OwsExceptionReport> encoder = CodingRepository.getInstance().getEncoder(key);
            if (encoder != null) {
                Object encodedObject = encoder.encode(owsExceptionReport);

                if (encodedObject instanceof XmlObject) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    XmlObject xml = (XmlObject) encodedObject;
                    xml.save(baos, XmlOptionsHelper.getInstance().getXmlOptions());
                    baos.flush();
                    return new ServiceResponse(baos, MediaTypes.APPLICATION_XML);
                } else if (encodedObject instanceof ServiceResponse) {
                    return (ServiceResponse) encodedObject;
                } else {
                    throw logExceptionAndCreateServletException(null);
                }
            } else {
                throw logExceptionAndCreateServletException(null);
            }
        } catch (Exception owse) {
            throw logExceptionAndCreateServletException(owse);
        }
    }

    private ServletException logExceptionAndCreateServletException(Exception e) {
        String exceptionText = "Error while encoding exception response!";
        if (e != null) {
            LOGGER.debug(exceptionText, e);
        } else {
            LOGGER.debug(exceptionText);
        }
        return new ServletException(exceptionText);
    }
}

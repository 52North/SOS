/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.ds;

import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.n52.faroe.ConfigurationError;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.pojo.PublicKey;
import org.n52.sos.aquarius.pojo.SessionRequestEntity;
import org.n52.sos.aquarius.requests.DeleteRequest;
import org.n52.sos.aquarius.requests.GetPublicKey;
import org.n52.sos.aquarius.requests.GetSession;
import org.n52.sos.aquarius.requests.KeepAliveRequest;
import org.n52.sos.proxy.Response;
import org.n52.sos.proxy.request.AbstractRequest;
import org.n52.sos.web.HttpClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);

    private static final int MAX_ATTEMPTS = 3;

    private ObjectMapper om = new ObjectMapper();

    private HttpClientHandler httpClientHandler;

    private Session session;

    private AquariusConnection connection;

    public SessionHandler(HttpClientHandler httpClientHandler, AquariusConnection connection) {
        this.httpClientHandler = httpClientHandler;
        this.connection = connection;
    }

    public synchronized void establishConnection(int attempt) {
        establishConnection(connection, attempt);
    }

    private synchronized void establishConnection(AquariusConnection connection, int attempt) {
        try {
            Response response = httpClientHandler.execute(getURL(connection.getBasePath()),
                    new GetSession(new SessionRequestEntity(connection.getUsername(),
                            encryptedPassword(connection.getPassword(), connection))));
            this.session = new Session(response.getEntity(), connection);
        } catch (Exception ae) {
            if (attempt < MAX_ATTEMPTS) {
                establishConnection(attempt + 1);
            }
            throw new ConfigurationError(
                    String.format("Error when establishing a connection to Aquarius for (%s, %s, %s)",
                            connection.getHost(), connection.getUsername(), connection.getPassword()),
                    ae);
        }
    }

    public synchronized void delete(Session session) throws OwsExceptionReport {
        try {
            execute(new DeleteRequest(), session);
        } catch (URISyntaxException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying parameter data");
        } finally {
            httpClientHandler.destroy();
        }
    }

    private Response execute(AbstractRequest request, Session session) throws OwsExceptionReport, URISyntaxException {
        request.addHeader(AquariusConstants.HEADER_AQ_AUTH_TOKEN, session.getToken());
        return httpClientHandler.execute(getURL(session.getConnection()
                .getBasePath()), request);
    }

    private String encryptedPassword(String plaintextPassword, AquariusConnection connection) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            if (!connection.hasCipher()) {
                Response response = httpClientHandler.execute(getURL(connection.getBasePath()), new GetPublicKey());
                PublicKey publicKey = om.readValue(response.getEntity(), PublicKey.class);

                InputSource source = new InputSource(new StringReader(publicKey.getXml()));
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(source);
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                String modulusText = xpath.evaluate("/RSAKeyValue/Modulus", document);
                String exponentText = xpath.evaluate("/RSAKeyValue/Exponent", document);
                byte[] modulusByes = DatatypeConverter.parseBase64Binary(modulusText);
                byte[] exponentBytes = DatatypeConverter.parseBase64Binary(exponentText);

                RSAPublicKeySpec pubSpec =
                        new RSAPublicKeySpec(new BigInteger(1, modulusByes), new BigInteger(1, exponentBytes));
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                cipher.init(1, KeyFactory.getInstance("RSA")
                        .generatePublic(pubSpec));
                connection.setCipher(cipher);
            }
            byte[] encryptedPasswordBlob = connection.getCipher()
                    .doFinal(plaintextPassword.getBytes("UTF-8"));
            return DatatypeConverter.printBase64Binary(encryptedPasswordBlob);
        } catch (Exception e) {
            LOGGER.error("Error encrypting password!", e);
            return plaintextPassword;
        }
    }

    private URI getURL(String host) throws URISyntaxException {
        return new URI(host.startsWith("http") ? host : "http://" + host);
    }

    public synchronized void keepAlive(Session session) throws OwsExceptionReport, URISyntaxException {
        Response response = execute(new KeepAliveRequest(), session);
        if (response.getStatus() == 401) {
            LOGGER.debug("keepAlive return 401! establish new connection!");
            establishConnection(session.getConnection(), 0);
        }
    }

    public synchronized Session getSession() {
        if (session == null) {
            establishConnection(0);
        }
        return session;
    }

}

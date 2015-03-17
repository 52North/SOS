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
package org.n52.sos.request.operator;

import static org.n52.sos.util.HasStatusCode.hasStatusCode;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.RequestContext;
import org.n52.sos.service.TransactionalSecurityConfiguration;
import org.n52.sos.util.net.IPAddress;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @since 4.0.0
 *
 */
public class TransactionalRequestCheckerTest {
    private static TransactionalSecurityConfiguration tsc =
            TransactionalSecurityConfiguration.getInstance();
    private static final IPAddress IP = new IPAddress("123.123.123.123");
    private static final String TOKEN = "I_HAVE_THE_PERMISSION";
    private static final IPAddress INVALID_IP = new IPAddress("234.234.234.234");
    private static final String INVALID_TOKEN = "YOU_ARE_NOT_ALLOWED";
    private static final String NULL = null;
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void shouldPass_NotActive()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(false);
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, true));
    }

    @Test
    public void shouldPass_ActiveIpNoToken_RcValidIpNoToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(NULL);
        new TransactionalRequestChecker(tsc).check(getRequestContextIp(true));
    }

    @Test
    public void shouldPass_ActiveNoIpToken_RcNoIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(NULL);
        tsc.setTransactionalToken(TOKEN);
        new TransactionalRequestChecker(tsc).check(getRequestContextToken(true));
    }

    @Test
    public void shouldPass_ActiveIpToken_RcValidIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, true));
    }

    @Test
    public void shouldException_ActiveIpNoToke_RcInvalidIpNoToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(NULL);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextIp(false));
    }

    @Test
    public void shouldException_ActiveNoIpToken_RcNoIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(NULL);
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextToken(false));
    }

    @Test
    public void shouldException_ActiveIpToke_RcInvalidIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(false, false));
    }

    @Test
    public void shouldException_ActiveIpToken_RcValidIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, false));
    }

    @Test
    public void shouldException_ActiveIpToken_RcInvalidIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(false, true));
    }

    private RequestContext getRequestContextIp(boolean validIp) {
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(validIp ? IP : INVALID_IP);
        return requestContext;
    }

    private RequestContext getRequestContextToken(boolean validToken) {
        RequestContext requestContext = new RequestContext();
        requestContext.setToken(validToken ? TOKEN : INVALID_TOKEN);
        return requestContext;
    }

    private RequestContext getRequestContextBoth(boolean validIp,
                                                 boolean validToken) {
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(validIp ? IP : INVALID_IP);
        requestContext.setToken(validToken ? TOKEN : INVALID_TOKEN);
        return requestContext;
    }
}

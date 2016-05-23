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
package org.n52.sos.decode.kvp.v2;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.GetObservationRequest;

import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class GetObservationKvpDecoderv20Test {

    @Test
    public void should_decode_extension_parameter_MergeObservationsIntoDataArray() throws OwsExceptionReport {
        final Map<String, String> mapTrue = Maps.newHashMap();
        mapTrue.put("MergeObservationsIntoDataArray", "true");
        mapTrue.put("service", "SOS");
        mapTrue.put("version", "2.0.0");
        mapTrue.put("request", "GetObservation");
        final GetObservationKvpDecoderv20 decoder = new GetObservationKvpDecoderv20();
        final GetObservationRequest requestTrue = decoder.decode(mapTrue);

        final Map<String, String> mapFalse = Maps.newHashMap();
        mapFalse.put("MergeObservationsIntoDataArray", "false");
        mapFalse.put("service", "SOS");
        mapFalse.put("version", "2.0.0");
        mapFalse.put("request", "GetObservation");
        final GetObservationRequest requestFalse = decoder.decode(mapFalse);

        assertThat(requestTrue.isSetExtensions(), is(TRUE));
        assertThat(
                requestTrue.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(TRUE));

        assertThat(requestFalse.isSetExtensions(), is(TRUE));
        assertThat(
                requestFalse.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(FALSE));
    }

}

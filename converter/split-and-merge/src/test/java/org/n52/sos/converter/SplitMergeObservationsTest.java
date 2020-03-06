/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.converter;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.n52.sos.util.builder.InsertObservationRequestBuilder.aInsertObservationRequest;
import static org.n52.sos.util.builder.ObservationBuilder.anObservation;

import java.util.List;

import org.junit.Test;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.InsertObservationRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 */
public class SplitMergeObservationsTest {

    @Test
    public void shouldSetIdentifierCorrectWhenSplittingObservations() throws OwsExceptionReport {
        /*
         * VALUES
         */
        String identifierCodeSpace = "identifierCodeSpace";
        String identifier = "identifier";
        String unit = "unit";
        String propertyDefinition = "http://www.52north.org/test/observableProperty/";

        OmObservationConstellation constellation = new OmObservationConstellation();
        constellation.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        constellation.setObservableProperty(new OmObservableProperty(propertyDefinition));

        SweTime sweTime = new SweTime();
        sweTime.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        sweTime.setUom("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");

        SweField timestampField = new SweField("phenomenonTime", sweTime);

        SweQuantity sweQuantity = new SweQuantity();
        sweQuantity.setDefinition(propertyDefinition);
        sweQuantity.setUom(unit);

        SweField valueField = new SweField("test_observable_property", sweQuantity);

        SweDataRecord elementType = new SweDataRecord();
        elementType.addField(timestampField);
        elementType.addField(valueField);

        SweDataArray dataArray = new SweDataArray();
        dataArray.add(newArrayList("2018-11-30T11:59:00+01:00","52"));
        dataArray.add(newArrayList("2018-11-30T10:59:00+01:00","42"));
        dataArray.setElementType(elementType);
        dataArray.setEncoding(new SweTextEncoding());

        SweDataArrayValue valueValue = new SweDataArrayValue();
        valueValue.setUnit(unit);
        valueValue.setValue(dataArray);

        ObservationValue<MultiValue<SweDataArray>> value = new MultiObservationValues<>();
        value.setValue(valueValue);

        SwesExtension<SweBoolean> extension = new SwesExtensionImpl<>();
        extension.setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
        extension.setValue(new SweBoolean().setValue(true));

        InsertObservationRequest request = aInsertObservationRequest()
                .addObservation(anObservation()
                        .setIdentifier(identifierCodeSpace, identifier)
                        .setObservationConstellation(constellation)
                        .setValue(value)
                        .build())
                .build();
        request.addExtension(extension);

        /*
         * TEST
         */
        AbstractServiceRequest<?> modifiedRequest = new SplitMergeObservations().modifyRequest(request);

        /*
         * VERIFY
         */
        assertThat(modifiedRequest, is(instanceOf(InsertObservationRequest.class)));
        List<OmObservation> splittedObservations = ((InsertObservationRequest) modifiedRequest).getObservations();

        assertThat(splittedObservations.size(), is(2));

        OmObservation obs0 = splittedObservations.get(0);
        OmObservation obs1 = splittedObservations.get(1);

        assertThat(obs0.getObservationConstellation(), is(equalTo(obs1.getObservationConstellation())));
        assertThat(obs0.getIdentifierCodeWithAuthority().getCodeSpace(),
                is(equalTo(obs1.getIdentifierCodeWithAuthority().getCodeSpace())));
        assertThat(obs0.getIdentifierCodeWithAuthority().getCodeSpace(), is(identifierCodeSpace));
        assertThat(obs0.getIdentifier(), is(identifier + "1"));
        assertThat(obs1.getIdentifier(), is(identifier + "2"));
    }

}

/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.values.MultiValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.sos.util.builder.InsertObservationRequestBuilder;
import org.n52.sos.util.builder.ObservationBuilder;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike
 *         Hinderk</a>
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
        dataArray.add(Lists.newArrayList("2018-11-30T11:59:00+01:00", "52"));
        dataArray.add(Lists.newArrayList("2018-11-30T10:59:00+01:00", "42"));
        dataArray.setElementType(elementType);
        dataArray.setEncoding(new SweTextEncoding());

        SweDataArrayValue valueValue = new SweDataArrayValue();
        valueValue.setUnit(unit);
        valueValue.setValue(dataArray);

        ObservationValue<MultiValue<SweDataArray>> value = new MultiObservationValues<>();
        value.setValue(valueValue);

        SwesExtension<SweBoolean> extension = new SwesExtension<>();
        extension.setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
        extension.setValue(new SweBoolean().setValue(true));

        InsertObservationRequest request = InsertObservationRequestBuilder.aInsertObservationRequest()
                .addObservation(ObservationBuilder.anObservation().setIdentifier(identifierCodeSpace, identifier)
                        .setObservationConstellation(constellation).setValue(value).build())
                .build();
        request.addExtension(extension);

        /*
         * TEST
         */
        OwsServiceRequest modifiedRequest = new SplitMergeObservations().modifyRequest(request);

        /*
         * VERIFY
         */
       MatcherAssert.assertThat(modifiedRequest, Is.is(CoreMatchers.instanceOf(InsertObservationRequest.class)));
        List<OmObservation> splittedObservations = ((InsertObservationRequest) modifiedRequest).getObservations();

       MatcherAssert.assertThat(splittedObservations.size(), Is.is(2));

        OmObservation obs0 = splittedObservations.get(0);
        OmObservation obs1 = splittedObservations.get(1);

       MatcherAssert.assertThat(obs0.getObservationConstellation(),
                Is.is(CoreMatchers.equalTo(obs1.getObservationConstellation())));
       MatcherAssert.assertThat(obs0.getIdentifierCodeWithAuthority().getCodeSpace(),
                Is.is(CoreMatchers.equalTo(obs1.getIdentifierCodeWithAuthority().getCodeSpace())));
       MatcherAssert.assertThat(obs0.getIdentifierCodeWithAuthority().getCodeSpace(), Is.is(identifierCodeSpace));
       MatcherAssert.assertThat(obs0.getIdentifier(), Is.is(identifier + "1"));
       MatcherAssert.assertThat(obs1.getIdentifier(), Is.is(identifier + "2"));
    }

}

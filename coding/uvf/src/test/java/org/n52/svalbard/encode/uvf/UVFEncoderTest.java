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
package org.n52.svalbard.encode.uvf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.DefaultTVPMeasurementMetadata;
import org.n52.sos.ogc.series.wml.WaterMLConstants.InterpolationType;
import org.n52.sos.response.AbstractObservationResponse.GlobalGetObservationValues;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 */
public class UVFEncoderTest {

    @Rule
    public ExpectedException exp = ExpectedException.none();

    private static final long UTC_TIMESTAMP_1 = 43200000l;
    private static final long UTC_TIMESTAMP_0 = -UTC_TIMESTAMP_1;
    private UVFEncoder encoder;
    private GetObservationResponse responseToEncode;
    private String obsPropIdentifier = "test-obs-prop-identifier";
    private String foiIdentifier = "test-foi-identifier";
    final String foiName = "test-foi-name";
    private String unit = "test-unit";

    @Before
    public void initObjects() throws OwsExceptionReport {
        encoder = new UVFEncoder();

        final OmObservation omObservation = new OmObservation();
        OmObservationConstellation observationConstellation = new OmObservationConstellation();

        // Observed Property
        String valueType = "test-obs-prop-value-type";
        String description = "test-obs-prop-description";
        AbstractPhenomenon observableProperty = new OmObservableProperty(
                obsPropIdentifier,
                description,
                unit,
                valueType);
        observationConstellation.setObservableProperty(observableProperty);

        // Feature Of Interest
        CodeWithAuthority featureIdentifier = new CodeWithAuthority(foiIdentifier);
        AbstractFeature featureOfInterest = new SamplingFeature(featureIdentifier);
        featureOfInterest.addName(new CodeType(foiName));
        int srid = 4326;
        String geomWKT = "POINT(51.9350382 7.6521225)";
        final Geometry point = JTSHelper.createGeometryFromWKT(geomWKT, srid);
        ((SamplingFeature) featureOfInterest).setGeometry(point);
        observationConstellation.setFeatureOfInterest(featureOfInterest);

        // value
        final String uomId = "test-uom";
        final double testValue = 52.0;
        Value<?> measuredValue = new QuantityValue(testValue, uomId);

        // timestamps
        Time phenomenonTime = new TimeInstant(new Date(UTC_TIMESTAMP_1));

        // observation value
        ObservationValue<?> value = new SingleObservationValue<>(phenomenonTime, measuredValue);
        value.setDefaultPointMetadata(new DefaultPointMetadata().setDefaultTVPMeasurementMetadata(
                new DefaultTVPMeasurementMetadata().setInterpolationtype(InterpolationType.Continuous)));
        omObservation.setValue(value);

        // observation type
        observationConstellation.setObservationType(OmConstants.OBS_TYPE_MEASUREMENT);

        // Final package
        omObservation.setObservationConstellation(observationConstellation);
        List<OmObservation> observationCollection = CollectionHelper.list(omObservation);
        responseToEncode = new GetObservationResponse();
        responseToEncode.setObservationCollection(observationCollection);
    }

    @Test
    public void shouldThrowExceptionOnWrongInput() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final Object objToEncode = new Object();

        exp.expect(UnsupportedEncoderInputException.class);
        exp.expectMessage(objToEncode.getClass().getName() + " can not be encoded by Encoder "
                + encoder.getClass().getName() + " because it is not yet implemented!");

        encoder.encode(objToEncode);
    }

    @Test
    public void shouldEncodeGetObservationResponse() throws UnsupportedEncoderInputException, OwsExceptionReport {
        BinaryAttachmentResponse encodedResponse = encoder.encode(responseToEncode);

        Assert.assertThat(encodedResponse, IsNot.not(CoreMatchers.nullValue()));
        final String[] split = new String(encodedResponse.getBytes()).split("\n");
        Assert.assertTrue("Expected >= 10 elements in array, got " + split.length, split.length >= 10);
    }

    @Test
    public void shouldEncodeFunctionInterpretationLine() throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(getResponseString()[0],
                Is.is("$ib Funktion-Interpretation: Linie"));
    }

    @Test
    public void shouldEncodeIndexUnitTime() throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(getResponseString()[1],
                Is.is("$sb Index-Einheit: *** Zeit ***"));
    }

    @Test
    public void shouldEncodeMeasurementIdentifier() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = getResponseString()[2];
        final String expected = "$sb Mess-Groesse: " + obsPropIdentifier
                .substring(obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH, obsPropIdentifier.length());

        Assert.assertThat(actual, Is.is(expected));
        Assert.assertThat(actual.length(), Is.is(33));
    }
    
    @Test
    public void shouldEncodeUnitOfMeasurement() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = getResponseString()[3];
        final String expected = "$sb Mess-Einheit: " + unit;

        Assert.assertThat(actual, Is.is(expected));
    }

    @Test
    public void shouldEncodeMeasurementLocationIdentifier()
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = getResponseString()[4];
        final String expected = "$sb Mess-Stellennummer: " + foiIdentifier
                .substring(foiIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH, foiIdentifier.length());

        Assert.assertThat(actual, Is.is(expected));
        Assert.assertThat(actual.length(), Is.is(39));
    }
    
    @Test
    public void shouldEncodeTimeseriesTypeIdentifierTimebased()
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(getResponseString()[6], Is.is("*Z"));
    }

    @Test
    public void shouldEncodeTimeseriesIdentifierAndCenturies() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        final String actual = getResponseString()[7];
        final String expected = obsPropIdentifier.substring(
            obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH,
            obsPropIdentifier.length()) +
            " " + unit + "     " + 
            "1900 1900";

        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeTimeseriesIdentifierAndCenturiesFromStreamingValues() throws
            UnsupportedEncoderInputException, OwsExceptionReport {
        GlobalGetObservationValues globalValues = responseToEncode.new GlobalGetObservationValues();
        DateTime end = new DateTime(0);
        DateTime start = new DateTime(0);
        Time phenomenonTime = new TimePeriod(start, end);
        globalValues.addPhenomenonTime(phenomenonTime);
        responseToEncode.setGlobalValues(globalValues);
        final String actual = getResponseString()[7];
        final String expected = obsPropIdentifier.substring(
            obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH,
            obsPropIdentifier.length()) +
            " " + unit + "     " + 
            "1900 1900";

        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeMeasurementLocationName() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = getResponseString()[5];
        final String expected = "$sb Mess-Stellenname: " + foiName;

        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeMeasurementLocationIdAndCoordinates() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        final String actual = getResponseString()[8];
        final String expected = "1              7.6521225 51.9350382          ";
        
        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeTemporalBoundingBox() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = getResponseString()[9];
        final String expected = "70010112007001011200Zeit    ";

        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeSingleObservationValueAndTimestamp() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        final String actual = getResponseString()[10];
        final String expected = "700101120052.0      ";
        
        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeShortenedSingleObservationValueAndTimestamp() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        ((QuantityValue)responseToEncode.getObservationCollection().get(0).getValue().getValue()).
            setValue(52.1234567890);
        final String actual = getResponseString()[10];
        final String expected = "700101120052.1234567";

        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeSingleObservationValueAndEndOfTimePeriodPhenomenonTime() throws
            UnsupportedEncoderInputException, OwsExceptionReport {
        Time phenomenonTime = new TimePeriod(new Date(UTC_TIMESTAMP_0), new Date(UTC_TIMESTAMP_1));
        responseToEncode.getObservationCollection().get(0).getValue().setPhenomenonTime(phenomenonTime );
        final String actual = getResponseString()[10];
        final String expected = "700101120052.0      ";
        
        Assert.assertThat(actual, Is.is(expected));
    }

    @Test
    public void shouldEncodeMultiObservationValueTimeValuePair() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        ObservationValue<MultiValue<List<TimeValuePair>>> mv = new MultiObservationValues<>();
        MultiValue<List<TimeValuePair>> value = new TVPValue();
        value.setUnit(unit);
        TimeValuePair tvp1 = new TimeValuePair(new TimeInstant(new Date(UTC_TIMESTAMP_0)),
                new QuantityValue(52.1234567890));
        TimeValuePair tvp2 = new TimeValuePair(new TimeInstant(new Date(UTC_TIMESTAMP_1)),
                new QuantityValue(52.1234567890));
        List<TimeValuePair> valueList = CollectionHelper.list(tvp1, tvp2);
        value.setValue(valueList);
        mv.setValue(value);
        responseToEncode.getObservationCollection().get(0).setValue(mv);

        final String[] encodedLines = getResponseString();

        Assert.assertThat(encodedLines[8], Is.is("69123112007001011200Zeit    "));
        Assert.assertThat(encodedLines[9], Is.is("691231120052.1234567"));
        Assert.assertThat(encodedLines[10], Is.is("700101120052.1234567"));
    }

    @Test
    public void shouldEncodeSingleObservationWithNoDataValue() throws
            UnsupportedEncoderInputException, OwsExceptionReport {
        responseToEncode.getObservationCollection().get(0).getValue().setValue(null);;
        final String actual = getResponseString()[9];
        final String expected = "7001011200-777      ";

        Assert.assertThat(actual, Is.is(expected));
    }

    @Test
    public void shouldNotEncodeUnitOfMeasurementForCountObservations() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        responseToEncode.getObservationCollection().get(0).getObservationConstellation().
                setObservationType(OmConstants.OBS_TYPE_COUNT_OBSERVATION);
        Time phenTime = new TimeInstant(new Date(UTC_TIMESTAMP_1));
        responseToEncode.getObservationCollection().get(0).setValue(new SingleObservationValue<>(phenTime, 
                new CountValue(52)));
        ((OmObservableProperty)responseToEncode.getObservationCollection().get(0).getObservationConstellation()
                .getObservableProperty()).setUnit(null);
        
        final String[] actual = getResponseString();
        final String expected = "$sb Mess-Einheit: " + unit;

        Assert.assertThat(Arrays.asList(actual), IsNot.not(CoreMatchers.hasItems(expected)));
    }

    @Test
    public void shouldEncodeTimeseriesIdentifierAndCenturiesWithoutUnitForCountObservations() throws
    UnsupportedEncoderInputException,
            OwsExceptionReport {
        responseToEncode.getObservationCollection().get(0).getObservationConstellation().
            setObservationType(OmConstants.OBS_TYPE_COUNT_OBSERVATION);
        Time phenTime = new TimeInstant(new Date(UTC_TIMESTAMP_1));
        responseToEncode.getObservationCollection().get(0).setValue(new SingleObservationValue<>(phenTime, 
                new CountValue(52)));
        ((OmObservableProperty)responseToEncode.getObservationCollection().get(0).getObservationConstellation()
                .getObservableProperty()).setUnit(null);
        final String[] actual = getResponseString();
        final String expected = obsPropIdentifier.substring(
            obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH,
            obsPropIdentifier.length()) +
            " " + unit + "     " + 
            "1970 1970";

        Assert.assertThat(Arrays.asList(actual), IsNot.not(CoreMatchers.hasItem(expected)));
    }
    
    @Test
    public void shouldReturnEmptyFileWhenObservationCollectionIsEmpty() throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        List<OmObservation> observationCollection = Collections.emptyList();
        responseToEncode.setObservationCollection(observationCollection);
        
        BinaryAttachmentResponse encodedResponse = encoder.encode(responseToEncode);
        Assert.assertThat(encodedResponse.getSize(), Is.is(-1));
    }
    
    private String[] getResponseString() throws UnsupportedEncoderInputException, OwsExceptionReport {
        return new String(encoder.encode(responseToEncode).getBytes()).split("\n");
    }
}
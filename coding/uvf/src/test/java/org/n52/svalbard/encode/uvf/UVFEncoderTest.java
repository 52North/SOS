/**
 * ﻿Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.svalbard.encode.uvf;

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
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
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

    private UVFEncoder encoder;
    private GetObservationResponse responseToEncode;
    private String obsPropIdentifier = "test-obs-prop-identifier";
    private String foiIdentifier = "test-foi-identifier";
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
        int srid = 4326;
        String geomWKT = "POINT(51.9350382 7.6521225)";
        final Geometry point = JTSHelper.createGeometryFromWKT(geomWKT, srid);
        ((SamplingFeature) featureOfInterest).setGeometry(point);
        observationConstellation.setFeatureOfInterest(featureOfInterest);

        // timestamps
        final String uomId = "test-uom";
        final double testValue = 52.0;
        Value<?> measuredValue = new QuantityValue(testValue, uomId);
        final long testDateUnixTime = 52l;
        Time phenomenonTime = new TimeInstant(new Date(testDateUnixTime));
        ObservationValue<?> value = new SingleObservationValue<>(phenomenonTime, measuredValue);
        omObservation.setValue(value);

        // Final package
        omObservation.setObservationConstellation(observationConstellation);
        List<OmObservation> observationCollection = CollectionHelper.list(omObservation);
        responseToEncode = new GetObservationResponse();
        responseToEncode.setObservationCollection(observationCollection);
    }

    @Rule
    public ExpectedException exp = ExpectedException.none();

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
        Assert.assertTrue(new String(encodedResponse.getBytes()).split("\n").length >= 6);
    }

    @Test
    public void shouldEncodeFunctionInterpretationLine() throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(new String(encoder.encode(responseToEncode).getBytes()).split("\n")[0],
                Is.is("$ib Funktion-Interpretation: Linie"));
    }

    @Test
    public void shouldEncodeIndexUnitTime() throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(new String(encoder.encode(responseToEncode).getBytes()).split("\n")[1],
                Is.is("$sb Index-Einheit: *** Zeit ***"));
    }

    @Test
    public void shouldEncodeMeasurementIdentifiert() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = new String(encoder.encode(responseToEncode).getBytes()).split("\n")[2];
        final String expected = "$sb Mess-Groesse: " + obsPropIdentifier
                .substring(obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH, obsPropIdentifier.length());

        Assert.assertThat(actual, Is.is(expected));
        Assert.assertThat(actual.length(), Is.is(33));
    }

    @Test
    public void shouldEncodeMeasurementLocationIdentifier()
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = new String(encoder.encode(responseToEncode).getBytes()).split("\n")[3];
        final String expected = "$sb Mess-Stellennummer: " + foiIdentifier
                .substring(foiIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH, foiIdentifier.length());

        Assert.assertThat(actual, Is.is(expected));
        Assert.assertThat(actual.length(), Is.is(39));
    }

    @Test
    public void shouldEncodeTimeseriesTypeIdentifierTimebased()
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        Assert.assertThat(new String(encoder.encode(responseToEncode).getBytes()).split("\n")[4], Is.is("*Z"));
    }

    @Test
    public void shouldEncodeTimeseriesIdentifierAndCenturies() throws UnsupportedEncoderInputException, OwsExceptionReport {
        final String actual = new String(encoder.encode(responseToEncode).getBytes()).split("\n")[5];
        final String expected = obsPropIdentifier.substring(
            obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH,
            obsPropIdentifier.length()) +
            " " + unit + "     " + 
            "1970 1970";
    
        Assert.assertThat(actual, Is.is(expected));
    }
    
    @Test
    public void shouldEncodeTimeseriesIdentifierAndCenturiesFromStreamingValues() throws UnsupportedEncoderInputException, OwsExceptionReport {
        GlobalGetObservationValues globalValues = responseToEncode.new GlobalGetObservationValues();
        DateTime end = new DateTime(0);
        DateTime start = new DateTime(0);
        Time phenomenonTime = new TimePeriod(start, end);
        globalValues.addPhenomenonTime(phenomenonTime);
        responseToEncode.setGlobalValues(globalValues);
        final String actual = new String(encoder.encode(responseToEncode).getBytes()).split("\n")[5];
        final String expected = obsPropIdentifier.substring(
            obsPropIdentifier.length() - UVFConstants.MAX_IDENTIFIER_LENGTH,
            obsPropIdentifier.length()) +
            " " + unit + "     " + 
            "1970 1970";
    
        Assert.assertThat(actual, Is.is(expected));
    }
}
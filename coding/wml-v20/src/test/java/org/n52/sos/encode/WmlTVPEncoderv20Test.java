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
package org.n52.sos.encode;

import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.DefaultTVPMeasurementMetadata;
import org.n52.sos.ogc.series.wml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.series.wml.Metadata;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.series.wml.WaterMLConstants.InterpolationType;
import org.n52.sos.util.CollectionHelper;

import net.opengis.gml.x32.ReferenceType;
import net.opengis.waterml.x20.DefaultTVPMeasurementMetadataDocument;
import net.opengis.waterml.x20.MeasurementTimeseriesDocument;
import net.opengis.waterml.x20.MeasurementTimeseriesMetadataType;
import net.opengis.waterml.x20.TVPDefaultMetadataPropertyType;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 */
public class WmlTVPEncoderv20Test {

    private static final long UTC_TIMESTAMP = 43200l;

    private WmlTVPEncoderv20 encoder;

    private ObservationValue<MultiValue<List<TimeValuePair>>> mv;

    @Before
    public void initObjects() {
        encoder = new WmlTVPEncoderv20();

        MultiValue<List<TimeValuePair>> value = new TVPValue();
        String unit = "test-unit";
        value.setUnit(unit);
        TimeValuePair tvp1 = new TimeValuePair(new TimeInstant(new Date(UTC_TIMESTAMP)),
                new QuantityValue(52.1234567890));
        List<TimeValuePair> valueList = CollectionHelper.list(tvp1);
        value.setValue(valueList);

        mv = new MultiObservationValues<>();
        mv.setValue(value);
    }

    @Test
    public void shouldSetDefaultCumulativeProperty() throws OwsExceptionReport {
        XmlObject encodedElement = encoder.encode(mv);

        Assert.assertThat(encodedElement, CoreMatchers.instanceOf(MeasurementTimeseriesDocument.class));
        final MeasurementTimeseriesDocument measurementTimeseriesDocument = (MeasurementTimeseriesDocument)encodedElement;
        Assert.assertThat(measurementTimeseriesDocument.getTimeseries().isSetMetadata(), Is.is(true));
        Assert.assertThat(measurementTimeseriesDocument.getTimeseries().getMetadata().getTimeseriesMetadata(),
                CoreMatchers.instanceOf(MeasurementTimeseriesMetadataType.class));
        final MeasurementTimeseriesMetadataType measurementTimeseriesMetadataType = (MeasurementTimeseriesMetadataType)
                measurementTimeseriesDocument.getTimeseries().getMetadata().getTimeseriesMetadata();
        Assert.assertThat(measurementTimeseriesMetadataType.isSetCumulative(), Is.is(true));
        Assert.assertThat(measurementTimeseriesMetadataType.getCumulative(), Is.is(false));
    }
    
    @Test
    public void shouldEncodeCumulativeProperty() throws OwsExceptionReport {
        mv.setMetadata(
                new Metadata().setTimeseriesmetadata(
                        new MeasurementTimeseriesMetadata().setCumulative(true)));

        XmlObject encodedElement = encoder.encode(mv);

        Assert.assertThat(((MeasurementTimeseriesMetadataType) ((MeasurementTimeseriesDocument)encodedElement)
                .getTimeseries().getMetadata().getTimeseriesMetadata()).getCumulative(), Is.is(true));
    }
    
    @Test
    public void shouldEncodeInterpolationType() throws OwsExceptionReport, XmlException {
        final InterpolationType type = WaterMLConstants.InterpolationType.MinPrec;
        
        mv.setDefaultPointMetadata(
                new DefaultPointMetadata().setDefaultTVPMeasurementMetadata(
                        new DefaultTVPMeasurementMetadata().setInterpolationtype(
                                type)));
        
        XmlObject encodedElement = encoder.encode(mv);

        TVPDefaultMetadataPropertyType defaultPointMetadata = ((MeasurementTimeseriesDocument)encodedElement).getTimeseries().getDefaultPointMetadataArray(0);
        DefaultTVPMeasurementMetadataDocument tvpMeasurementMetadataDocument = DefaultTVPMeasurementMetadataDocument.Factory.parse(defaultPointMetadata.xmlText());
        ReferenceType interpolationType = tvpMeasurementMetadataDocument.getDefaultTVPMeasurementMetadata().getInterpolationType();
        Assert.assertThat(interpolationType.getHref(), Is.is("http://www.opengis.net/def/waterml/2.0/interpolationType/MinPrec"));
        Assert.assertThat(interpolationType.getTitle(), Is.is("MinPrec"));
    }

    @Test
    public void shouldEncodeInterpolationTypeContinuousAsDefault() throws OwsExceptionReport, XmlException {
        XmlObject encodedElement = encoder.encode(mv);

        TVPDefaultMetadataPropertyType defaultPointMetadata = ((MeasurementTimeseriesDocument)encodedElement).getTimeseries().getDefaultPointMetadataArray(0);
        DefaultTVPMeasurementMetadataDocument tvpMeasurementMetadataDocument = DefaultTVPMeasurementMetadataDocument.Factory.parse(defaultPointMetadata.xmlText());
        ReferenceType interpolationType = tvpMeasurementMetadataDocument.getDefaultTVPMeasurementMetadata().getInterpolationType();
        Assert.assertThat(interpolationType.getHref(), Is.is("http://www.opengis.net/def/waterml/2.0/interpolationType/Continuous"));
        Assert.assertThat(interpolationType.getTitle(), Is.is("Continuous"));
    }

    // TODO add tests für sosObservation or remove duplicate code in WmlTVPEncoderv20

}

/**
 * ﻿Copyright (C) 2017
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
import org.n52.sos.ogc.waterml.DefaultPointMetadata;
import org.n52.sos.ogc.waterml.DefaultTVPMeasurementMetadata;
import org.n52.sos.ogc.waterml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.waterml.Metadata;
import org.n52.sos.ogc.waterml.WaterMLConstants;
import org.n52.sos.ogc.waterml.WaterMLConstants.InterpolationType;
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
        ((TVPValue) mv.getValue()).setMetadata(
                new Metadata().setTimeseriesmetadata(
                        new MeasurementTimeseriesMetadata().setCumulative(true)));

        XmlObject encodedElement = encoder.encode(mv);

        Assert.assertThat(((MeasurementTimeseriesMetadataType) ((MeasurementTimeseriesDocument)encodedElement)
                .getTimeseries().getMetadata().getTimeseriesMetadata()).getCumulative(), Is.is(true));
    }
    
    @Test
    public void shouldEncodeInterpolationType() throws OwsExceptionReport, XmlException {
        final InterpolationType type = WaterMLConstants.InterpolationType.MinPrec;
        
        ((TVPValue) mv.getValue()).setDefaultPointMetadata(
                new DefaultPointMetadata().setDefaultTVPMeasurementMetadata(
                        new DefaultTVPMeasurementMetadata().setInterpolationtype(
                                type)));
        
        XmlObject encodedElement = encoder.encode(mv);

        TVPDefaultMetadataPropertyType defaultPointMetadata = ((MeasurementTimeseriesDocument)encodedElement).getTimeseries().getDefaultPointMetadataArray(0);
        DefaultTVPMeasurementMetadataDocument tvpMeasurementMetadataDocument = DefaultTVPMeasurementMetadataDocument.Factory.parse(defaultPointMetadata.xmlText());
        ReferenceType interpolationType = tvpMeasurementMetadataDocument.getDefaultTVPMeasurementMetadata().getInterpolationType();
        Assert.assertThat(interpolationType.getHref(), Is.is(type.getIdentifier()));
        Assert.assertThat(interpolationType.getTitle(), Is.is(type.getTitle()));
    }
    
    @Test
    public void shouldEncodeInterpolationTypeContinuousAsDefault() throws OwsExceptionReport, XmlException {
        final InterpolationType type = WaterMLConstants.InterpolationType.Continuous;
        XmlObject encodedElement = encoder.encode(mv);

        TVPDefaultMetadataPropertyType defaultPointMetadata = ((MeasurementTimeseriesDocument)encodedElement).getTimeseries().getDefaultPointMetadataArray(0);
        DefaultTVPMeasurementMetadataDocument tvpMeasurementMetadataDocument = DefaultTVPMeasurementMetadataDocument.Factory.parse(defaultPointMetadata.xmlText());
        ReferenceType interpolationType = tvpMeasurementMetadataDocument.getDefaultTVPMeasurementMetadata().getInterpolationType();
        Assert.assertThat(interpolationType.getHref(), Is.is(type.getIdentifier()));
        Assert.assertThat(interpolationType.getTitle(), Is.is(type.getTitle()));
    }

}

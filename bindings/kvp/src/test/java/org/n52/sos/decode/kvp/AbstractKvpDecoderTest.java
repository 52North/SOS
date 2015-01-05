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
package org.n52.sos.decode.kvp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.AbstractServiceRequest;

import com.google.common.collect.Lists;

public class AbstractKvpDecoderTest extends AbstractKvpDecoder {
    
    private static final String START_TIME = "2012-11-19T14:00:00+01:00";

    private static final String END_TIME = "2012-11-19T14:15:00+01:00";
    
    private static final String START_END_TIME = "2012-11-19T14:15:00+01:00/" + END_TIME;

    private static final String OM_PHENOMENON_TIME = "om:phenomenonTime";
    

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return null;
    }

    @Override
    public AbstractServiceRequest<?> decode(Map<String, String> objectToDecode) throws OwsExceptionReport,
            UnsupportedDecoderInputException {
        return null;
    }
    
    /*
     * SOS 1.0.0 tests
     */
    
    /*
     * two parameter
     */
    
    @Test
    public void should_decode_eventTime_TM_Equals_2_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, START_TIME)), TimeOperator.TM_Equals);
    }
    
    @Test
    public void should_decode_eventTime_TM_During_2_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, START_END_TIME)), TimeOperator.TM_During);
    }
    
    /*
     * three parameter
     */
    @Test
    public void should_decode_eventTime_TM_After_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_After.name(), START_TIME)), TimeOperator.TM_After);
    }
    
    @Test
    public void should_decode_eventTime_TM_Before_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Before.name(), START_TIME)), TimeOperator.TM_Before);
    }
    
    @Test
    public void should_decode_eventTime_TM_Begins_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Begins.name(), START_TIME)), TimeOperator.TM_Begins);
    }
    
    @Test
    public void should_decode_eventTime_TM_BegunBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_BegunBy.name(), START_TIME)), TimeOperator.TM_BegunBy);
    }
    
    @Test
    public void should_decode_eventTime_TM_Contains_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Contains.name(), START_TIME)), TimeOperator.TM_Contains);
    }
    
    @Test
    public void should_decode_eventTime_TM_During_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_During.name(), START_END_TIME)), TimeOperator.TM_During);
    }
    
    @Test
    public void should_decode_eventTime_TM_EndedBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_EndedBy.name(), START_TIME)), TimeOperator.TM_EndedBy);
    }
    
    @Test
    public void should_decode_eventTime_TM_Ends_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Ends.name(), START_TIME)), TimeOperator.TM_Ends);
    }
    
    @Test
    public void should_decode_eventTime_TM_Equals_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Equals.name(), START_TIME)), TimeOperator.TM_Equals);
    }
    
    @Test
    public void should_decode_eventTime_TM_Meets_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Meets.name(), START_TIME)), TimeOperator.TM_Meets);
    }
    
    @Test
    public void should_decode_eventTime_TM_MetBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_MetBy.name(), START_TIME)), TimeOperator.TM_MetBy);
    }
    
    @Test
    public void should_decode_eventTime_TM_OverlappedBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_OverlappedBy.name(), START_TIME)), TimeOperator.TM_OverlappedBy);
    }
    
    @Test
    public void should_decode_eventTime_TM_Overlaps_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseEventTime(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator.TM_Overlaps.name(), START_TIME)), TimeOperator.TM_Overlaps);
    }
    
    /*
     * SOS 2.0 tests
     */
    
    /*
     * three parameter
     */
    
    @Test
    public void should_decode_temporalFilter_After_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.After.name(), START_TIME)), TimeOperator.TM_After);
    }
    
    @Test
    public void should_decode_temporalFilter_Before_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.Before.name(), START_TIME)), TimeOperator.TM_Before);
    }
    
    @Test
    public void should_decode_temporalFilter_Begins_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.Begins.name(), START_TIME)), TimeOperator.TM_Begins);
    }
    
    @Test
    public void should_decode_temporalFilter_BegunBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.BegunBy.name(), START_TIME)), TimeOperator.TM_BegunBy);
    }
    
    @Test
    public void should_decode_temporalFilter_TContains_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.TContains.name(), START_TIME)), TimeOperator.TM_Contains);
    }
    
    @Test
    public void should_decode_temporalFilter_During_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.During.name(), START_END_TIME)), TimeOperator.TM_During);
    }
    
    @Test
    public void should_decode_temporalFilter_EndedBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.EndedBy.name(), START_TIME)), TimeOperator.TM_EndedBy);
    }
    
    @Test
    public void should_decode_temporalFilter_Ends_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.Ends.name(), START_TIME)), TimeOperator.TM_Ends);
    }
    
    @Test
    public void should_decode_temporalFilter_TEquals_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.TEquals.name(), START_TIME)), TimeOperator.TM_Equals);
    }
    
    @Test
    public void should_decode_temporalFilter_Meets_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.Meets.name(), START_TIME)), TimeOperator.TM_Meets);
    }
    
    @Test
    public void should_decode_temporalFilter_MetBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.MetBy.name(), START_TIME)), TimeOperator.TM_MetBy);
    }
    
    @Test
    public void should_decode_temporalFilter_OverlappedBy_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.OverlappedBy.name(), START_TIME)), TimeOperator.TM_OverlappedBy);
    }
    
    @Test
    public void should_decode_temporalFilter_TOverlaps_3_Param() throws DateTimeParseException, OwsExceptionReport {
        check(parseTemporalFilter(Lists.newArrayList(OM_PHENOMENON_TIME, TimeOperator2.TOverlaps.name(), START_TIME)), TimeOperator.TM_Overlaps);
    }
    
    
    
    private List<TemporalFilter> parseEventTime(List<String> parameterValues) throws DateTimeParseException, OwsExceptionReport {
        return parseTemporalFilter(parameterValues, Sos1Constants.GetObservationParams.eventTime.name());
    }
    
    private List<TemporalFilter> parseTemporalFilter(List<String> parameterValues) throws DateTimeParseException, OwsExceptionReport {
        return parseTemporalFilter(parameterValues, Sos2Constants.GetObservationParams.temporalFilter.name());
    }

    private void check(List<TemporalFilter> parseEventTime, TimeOperator operator) {
        for (TemporalFilter temporalFilter : parseEventTime) {
            assertThat(temporalFilter.getOperator().name(), is(operator.name()));
        }
    }

}

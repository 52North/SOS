package org.n52.sos.decode.kvp;

import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.svalbard.decode.exception.DecodingException;

import java.util.List;

public class ParameterValuesSizeEqualThree implements CreateTemporalFilter{
    public TemporalFilter pmtValueSizeThree(String name, List<String> parameterValues) throws DecodingException {
        return createTemporalFilter(name,
                parameterValues.get(2),
                parameterValues.get(1),
                parameterValues.get(0));
    }

    @Override
    public TemporalFilter decodeTemporalFilter(String name, List<String> parameterValues) throws DecodingException {
        return null;
    }

    @Override
    public TemporalFilter createTemporalFilter(String name, String value, String operator, String valueReference)
            throws DecodingException {
        FilterConstants.TimeOperator timeOperator;
        try {
            timeOperator = FilterConstants.TimeOperator.from(operator);
        } catch (IllegalArgumentException e1) {
            try {
                timeOperator = FilterConstants.TimeOperator.from(FilterConstants.TimeOperator2.from(operator));
            } catch (IllegalArgumentException e2) {
                throw new DecodingException(name, "Unsupported operator '%s'!", operator);
            }
        }
        return createTemporalFilter(name, value, String.valueOf(timeOperator), valueReference);
    }

    @Override
    public TemporalFilter createTemporalFilter(String value, String name, String valueReference) throws DecodingException {
        return null;
    }

    @Override
    public TemporalFilter decodeTemporalFilter() {
        return null;
    }

    @Override
    public TemporalFilter createTemporalFilter() {
        return null;
    }
}

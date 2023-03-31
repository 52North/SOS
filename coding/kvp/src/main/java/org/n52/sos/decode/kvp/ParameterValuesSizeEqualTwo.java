package org.n52.sos.decode.kvp;


import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.svalbard.decode.exception.DecodingException;

import java.util.List;

class ParameterValuesSizeEqualTwo implements CreateTemporalFilter {
    public TemporalFilter pmtValueSizeTwo(String name, List<String> parameterValues) throws DecodingException {
        return createTemporalFilter(parameterValues.get(1), name, parameterValues.get(0));
    }

    @Override
    public TemporalFilter decodeTemporalFilter(String name, List<String> parameterValues) throws DecodingException {
        return null;
    }

    @Override
    public TemporalFilter createTemporalFilter(String name, String value, String operator, String valueReference) throws DecodingException {
        return null;
    }

    @Override
    public TemporalFilter createTemporalFilter(String value, String name, String valueReference)
            throws DecodingException {
        switch (value.split("/").length) {
            case 1:
                return createTemporalFilter(name, value, String.valueOf(FilterConstants.TimeOperator.TM_Equals), valueReference);
            case 2:
                return createTemporalFilter(name, value, String.valueOf(FilterConstants.TimeOperator.TM_During), valueReference);
            default:
                throw new DecodingException(name, "The paramter value '%s' is invalid!", value);
        }
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

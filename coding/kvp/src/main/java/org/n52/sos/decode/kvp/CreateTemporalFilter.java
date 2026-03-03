package org.n52.sos.decode.kvp;

import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.svalbard.decode.exception.DecodingException;

import java.util.List;

public interface CreateTemporalFilter {

    TemporalFilter decodeTemporalFilter(String name, List<String> parameterValues) throws DecodingException;

    TemporalFilter createTemporalFilter(String name, String value, String operator, String valueReference) throws DecodingException;

    TemporalFilter createTemporalFilter(String value, String name, String valueReference)
            throws DecodingException;

    public TemporalFilter decodeTemporalFilter();

    TemporalFilter createTemporalFilter();
}
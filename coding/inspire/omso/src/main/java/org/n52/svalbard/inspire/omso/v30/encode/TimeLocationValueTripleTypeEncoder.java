package org.n52.svalbard.inspire.omso.v30.encode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;

import eu.europa.ec.inspire.schemas.omso.x30.TimeLocationValueTripleType;

public class TimeLocationValueTripleTypeEncoder extends AbstractTimeLocationValueTripleTypeEncoder<TimeLocationValueTripleType> {

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TimeLocationValueTripleType encode(TimeLocationValueTriple timeLocationValueTriple)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(timeLocationValueTriple, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public TimeLocationValueTripleType encode(TimeLocationValueTriple timeLocationValueTriple,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encodeTimeLocationValueTriple(timeLocationValueTriple);
    }

}

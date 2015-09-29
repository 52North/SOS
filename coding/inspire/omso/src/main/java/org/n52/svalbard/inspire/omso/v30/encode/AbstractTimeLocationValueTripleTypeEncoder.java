package org.n52.svalbard.inspire.omso.v30.encode;

import java.util.Map;

import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.inspire.base.InspireBaseConstants;
import org.n52.sos.inspire.omor.InspireOMORConstants;
import org.n52.sos.inspire.omso.InspireOMSOConstants;
import org.n52.sos.ogc.om.TimeLocationValueTriple;

import eu.europa.ec.inspire.schemas.omso.x30.TimeLocationValueTripleType;

public abstract class AbstractTimeLocationValueTripleTypeEncoder<T> extends AbstractSpecificXmlEncoder<T, TimeLocationValueTriple> {


    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(InspireBaseConstants.NS_BASE_30, InspireBaseConstants.NS_BASE_PREFIX);
        nameSpacePrefixMap.put(InspireOMORConstants.NS_OMOR_30, InspireOMORConstants.NS_OMOR_PREFIX);
        nameSpacePrefixMap.put(InspireOMSOConstants.NS_OMSO_30, InspireOMSOConstants.NS_OMSO_PREFIX);
    }
    
    protected TimeLocationValueTripleType encodeTimeLocationValueTriple(
            TimeLocationValueTriple timeLocationValueTriple) {
        TimeLocationValueTripleType tlvtt = TimeLocationValueTripleType.Factory.newInstance();
        tlvtt.addNewLocation();
        tlvtt.addNewTime();
        
        return null;
    }

}

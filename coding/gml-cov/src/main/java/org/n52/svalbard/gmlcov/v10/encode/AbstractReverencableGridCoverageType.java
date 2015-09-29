package org.n52.svalbard.gmlcov.v10.encode;

import java.util.Map;

import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.ogc.gmlcov.GmlCoverageConstants;
import org.n52.sos.ogc.om.values.ReverencableGridCoverage;

public abstract class AbstractReverencableGridCoverageType<T> extends AbstractSpecificXmlEncoder<T, ReverencableGridCoverage> {

    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlCoverageConstants.NS_GML_COV, GmlCoverageConstants.NS_GML_COV_PREFIX);
    }
}

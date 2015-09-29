package org.n52.svalbard.gml.v321.encode;

import java.util.Map;

import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;

import net.opengis.gml.x32.DiscreteCoverageType;

public abstract class AbstractRectifiedGridCoverageTypeEncoder<T> extends AbstractSpecificXmlEncoder<T, RectifiedGridCoverage> {

    protected DiscreteCoverageType encodeRectifiedGridCoverage(RectifiedGridCoverage rectifiedGridCoverage) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }

}

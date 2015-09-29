package org.n52.svalbard.gml.v321.encode;

import java.util.Map;

import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.MultiPointCoverage.PointValueLists;
import org.n52.sos.util.JavaHelper;

import net.opengis.gml.x32.DiscreteCoverageType;

public abstract class AbstractMultiPointCoverageTypeEncoder<T> extends AbstractSpecificXmlEncoder<T, MultiPointCoverage> {

    protected DiscreteCoverageType encodeMultiPointCoverageType(MultiPointCoverage multiPointCoverage) {
        DiscreteCoverageType dct = DiscreteCoverageType.Factory.newInstance();
        dct.setId(JavaHelper.generateID(multiPointCoverage.toString()));
        PointValueLists pointValue = multiPointCoverage.getPointValue();
        dct.addNewDomainSet().addNewAbstractGeometry();
        dct.addNewRangeSet().addNewDataBlock();
        
        // TODO Auto-generated method stub
        return dct;
    }
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }
}

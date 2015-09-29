package org.n52.svalbard.cv.v02.encode;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.XmlPropertyTypeEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.cv.CvConstants;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Point;

import net.opengis.cv.x02.gml32.CVDiscretePointCoverageType;
import net.opengis.cv.x02.gml32.CVPointValuePairPropertyType;
import net.opengis.cv.x02.gml32.CVPointValuePairType;
import net.opengis.gml.x32.PointPropertyType;

public abstract class AbstractCVDiscretePointCoverageTypeEncoder<T> extends AbstractSpecificXmlEncoder<T, CvDiscretePointCoverage> {

    
    protected CVDiscretePointCoverageType encodeCVDiscretePointCoverage(CvDiscretePointCoverage cvDiscretePointCoverage) throws UnsupportedEncoderInputException, OwsExceptionReport {
        CVDiscretePointCoverageType cvdpct = CVDiscretePointCoverageType.Factory.newInstance();
        CVPointValuePairPropertyType cvpvppt = cvdpct.addNewElement();
        cvpvppt.setCVPointValuePair(encodePointValuePair(cvDiscretePointCoverage.getValue()));
        return cvdpct;
    }
    
    private CVPointValuePairType encodePointValuePair(PointValuePair value) throws UnsupportedEncoderInputException, OwsExceptionReport {
        CVPointValuePairType cvpvpt = CVPointValuePairType.Factory.newInstance();
        cvpvpt.setGeometry(encodeGeometry(value.getPoint()));
        cvpvpt.setValue(encodeValue(value.getValue()));
        return cvpvpt;
    }

    @SuppressWarnings("unchecked")
    private PointPropertyType encodeGeometry(Point point) throws UnsupportedEncoderInputException, OwsExceptionReport {
        Encoder<PointPropertyType, Point> encoder = (Encoder<PointPropertyType, Point>) getEncoder(
                new XmlPropertyTypeEncoderKey(CvConstants.NS_CV, Point.class));
        if (encoder != null) {
            return (PointPropertyType) encoder.encode(point);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private XmlObject encodeValue(Value<?> value) throws UnsupportedEncoderInputException, OwsExceptionReport {
        Encoder<?, Value<?>> encoder = (Encoder<?, Value<?>>) getEncoder(
                new XmlPropertyTypeEncoderKey(CvConstants.NS_CV, Value.class));
        if (encoder != null) {
            return (XmlObject) encoder.encode(value);
        }
        return null;
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(CvConstants.NS_CV, CvConstants.NS_CV_PREFIX);
    }
}

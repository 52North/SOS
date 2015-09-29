package org.n52.svalbard.gml.v321.encode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlPropertyTypeEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Point;

import net.opengis.gml.x32.PointPropertyType;
import net.opengis.gml.x32.PointType;

public class PointPropertyTypeEncoder extends AbstractSpecificXmlEncoder<PointPropertyType, Point> {

    protected static final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
            new ClassToClassEncoderKey(PointPropertyType.class, Point.class),
            new XmlPropertyTypeEncoderKey(GmlConstants.NS_GML_32, Point.class));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public PointPropertyType encode(Point point) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(point, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public PointPropertyType encode(Point point, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        PointPropertyType ppt = PointPropertyType.Factory.newInstance();
        ppt.setPoint(encodePointType(point, additionalValues));
        return ppt;
    }

    private PointType encodePointType(Point point, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        return (PointType)encodeGML(point, additionalValues);
    }
    
    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, additionalValues);
    }

}

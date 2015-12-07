package org.n52.svalbard.encode.inspire;

import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility;

public class EnvironmentalMonitoringFaciltityPropertyTypeEncoder extends AbstractEnvironmentalMonitoringFaciltityTypeEncoder {

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public XmlObject encode(final AbstractFeature abstractFeature, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (abstractFeature instanceof EnvironmentalMonitoringFacility) {
            final XmlObject encodedObject = createEnvironmentalMonitoringFaciltityType((EnvironmentalMonitoringFacility)abstractFeature);
            // LOGGER.debug("Encoded object {} is valid: {}",
            // encodedObject.schemaType().toString(),
            // XmlHelper.validateDocument(encodedObject));
            return encodedObject;
        }
        throw new UnsupportedEncoderInputException(this, abstractFeature);
    }

}

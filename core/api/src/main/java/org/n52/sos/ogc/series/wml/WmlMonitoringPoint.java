package org.n52.sos.ogc.series.wml;

import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.FeatureOfInterestVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.AbstractMonitoringFeature;

public class WmlMonitoringPoint extends AbstractMonitoringFeature {

    private static final long serialVersionUID = -6557142188152925124L;

    public WmlMonitoringPoint(CodeWithAuthority featureIdentifier) {
        this(featureIdentifier, null);
    }
    
    public WmlMonitoringPoint(CodeWithAuthority featureIdentifier, String gmlId) {
        super(featureIdentifier, gmlId);
        setDefaultElementEncoding(WaterMLConstants.NS_WML_20);
    }
    
    @Override
    public <X> X accept(FeatureOfInterestVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String
                .format("MonitoringPoint [name=%s, description=%s, xmlDescription=%s, geometry=%s, featureType=%s, url=%s, sampledFeatures=%s, parameters=%s, encode=%b, relatedSamplingFeatures=%s]",
                        getName(), getDescription(), getXmlDescription(), getGeometry(), getFeatureType(), getUrl(),
                        getSampledFeatures(), getParameters(), isEncode(), getRelatedSamplingFeatures());
    }

}

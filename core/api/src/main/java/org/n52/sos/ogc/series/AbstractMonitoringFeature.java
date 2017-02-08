package org.n52.sos.ogc.series;

import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;

public abstract class AbstractMonitoringFeature extends AbstractSamplingFeature {

    private static final long serialVersionUID = -70039769462711980L;

    public AbstractMonitoringFeature(CodeWithAuthority featureIdentifier) {
        this(featureIdentifier, null);
    }
    
    public AbstractMonitoringFeature(CodeWithAuthority featureIdentifier, String gmlId) {
        super(featureIdentifier, gmlId);
    }

}

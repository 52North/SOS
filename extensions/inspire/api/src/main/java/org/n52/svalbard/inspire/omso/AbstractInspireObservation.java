package org.n52.svalbard.inspire.omso;

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;

public abstract class AbstractInspireObservation extends OmObservation {
    
    private static final long serialVersionUID = 3681367197554559966L;

    public AbstractInspireObservation() {
    }

    public AbstractInspireObservation(OmObservation observation) {
        this();
        observation.copyTo(this);
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            SamplingFeature sf = (SamplingFeature)getObservationConstellation().getFeatureOfInterest();
            sf.setEncode(true);
        }
    }
}

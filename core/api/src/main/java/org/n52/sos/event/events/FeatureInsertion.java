package org.n52.sos.event.events;

import org.n52.sos.request.InsertFeatureOfInterestRequest;
import org.n52.sos.response.InsertFeatureOfInterestResponse;

public class FeatureInsertion extends SosInsertionEvent<InsertFeatureOfInterestRequest, InsertFeatureOfInterestResponse> {

    public FeatureInsertion(InsertFeatureOfInterestRequest request, InsertFeatureOfInterestResponse response) {
        super(request, response);
    }

}

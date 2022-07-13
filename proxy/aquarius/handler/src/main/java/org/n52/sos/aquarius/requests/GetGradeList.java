package org.n52.sos.aquarius.requests;

import java.util.Map;

import org.n52.sos.aquarius.AquariusConstants;

public class GetGradeList extends AbstractAquariusGetRequest {

    public GetGradeList() {
        super();
    }

    @Override
    public String getPath() {
        return AquariusConstants.Paths.GET_GRADE_LIST;
    }

    @Override
    public Map<String, String> getQueryParameters() {
        return super.getQueryParameters();
    }
}

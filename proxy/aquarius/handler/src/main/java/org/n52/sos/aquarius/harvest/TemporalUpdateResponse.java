package org.n52.sos.aquarius.harvest;

public class TemporalUpdateResponse {

    private boolean updated = true;
    private String nextToken;

    public TemporalUpdateResponse(String nextToken) {
        this.nextToken = nextToken;
    }

    public TemporalUpdateResponse(boolean updated, String nextToken) {
        this.updated = updated;
        this.nextToken = nextToken;
    }

    public boolean isUpdated() {
        return updated;
    }

    public String getNextToken() {
        return nextToken;
    }



}
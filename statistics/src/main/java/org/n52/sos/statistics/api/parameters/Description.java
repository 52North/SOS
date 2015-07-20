package org.n52.sos.statistics.api.parameters;

public class Description {
    public static enum InformationOrigin {
        RequestEvent, ResponseEvent, CountingStreamEvent, OutgoingResponseEvent, ExceptionEvent, Computed, None;
    }

    public static enum Operation {
        None, Default, Metadata, GetCapabilities, GetObservation, GetObservationById, DescribeSensor, InsertObservation, GetResult, GetFeatureOfInterest, DeleteSensor, GetDataAvailability, GetResultTemplate, InsertResult, InsertResultTemplate, InsertSensor, UpdateSensor;
    }

    private final InformationOrigin informationOrigin;
    private final Operation operation;
    private String desc;

    public Description(InformationOrigin informationOrigin, Operation operation) {
        this.informationOrigin = informationOrigin;
        this.operation = operation;
    }

    public Description(InformationOrigin informationOrigin, Operation operation, String desc) {
        this.informationOrigin = informationOrigin;
        this.operation = operation;
        this.desc = desc;
    }

    public InformationOrigin getInformationOrigin() {
        return informationOrigin;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Description [informationOrigin=" + informationOrigin + ", operation=" + operation + ", desc=" + desc + "]";
    }

}

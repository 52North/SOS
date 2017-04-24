package org.n52.sos.ds.hibernate.util.observation;

import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ogc.om.OmObservation;

public abstract class AbstractAdditionalObservationCreator<T extends Series> implements AdditionalObservationCreator<T> {

    @Override
    public OmObservation create(OmObservation omObservation, Series series) {
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, Observation<?> observation) {
        return omObservation;
    }

    @Override
    public OmObservation add(OmObservation sosObservation, Observation<?> observation) {
        return sosObservation;
    }
}

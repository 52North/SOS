package org.n52.sos.ds.hibernate.dao.observation;

import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ogc.om.OmConstants;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationTypeObservationVisitor implements ObservationVisitor<String> {

    private ObservationTypeObservationVisitor() {
    }

    @Override
    public String visit(NumericObservation o) {
        return OmConstants.OBS_TYPE_MEASUREMENT;
    }

    @Override
    public String visit(BlobObservation o) {
        return OmConstants.OBS_TYPE_UNKNOWN;
    }

    @Override
    public String visit(BooleanObservation o) {
        return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
    }

    @Override
    public String visit(CategoryObservation o) {
        return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
    }

    @Override
    public String visit(ComplexObservation o) {
        return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
    }

    @Override
    public String visit(CountObservation o) {
        return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
    }

    @Override
    public String visit(GeometryObservation o) {
        return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
    }

    @Override
    public String visit(TextObservation o) {
        return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
    }

    @Override
    public String visit(SweDataArrayObservation o) {
        return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
    }

    public static ObservationTypeObservationVisitor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ObservationTypeObservationVisitor INSTANCE
                = new ObservationTypeObservationVisitor();

        private Holder() {
        }
    }

}

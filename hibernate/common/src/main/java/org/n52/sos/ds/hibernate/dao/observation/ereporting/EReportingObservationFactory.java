package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.ContextualReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.TemporalReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingTextObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class EReportingObservationFactory extends ObservationFactory {
    protected EReportingObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Observation> observationClass() {
        return AbstractEReportingObservation.class;
    }

    @Override
    public Class<? extends ContextualReferencedObservation> contextualReferencedClass() {
        return ContextualReferencedEReportingObservation.class;
    }

    @Override
    public Class<? extends TemporalReferencedObservation> temporalReferencedClass() {
        return TemporalReferencedEReportingObservation.class;
    }

    @Override
    public Class<? extends BlobObservation> blobClass() {
        return EReportingBlobObservation.class;
    }

    @Override
    public Class<? extends BooleanObservation> truthClass() {
        return EReportingBooleanObservation.class;
    }

    @Override
    public Class<? extends CategoryObservation> categoryClass() {
        return EReportingCategoryObservation.class;
    }

    @Override
    public Class<? extends CountObservation> countClass() {
        return EReportingCountObservation.class;
    }

    @Override
    public Class<? extends GeometryObservation> geometryClass() {
        return EReportingGeometryObservation.class;
    }

    @Override
    public Class<? extends NumericObservation> numericClass() {
        return EReportingNumericObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayObservation> sweDataArrayClass() {
        return EReportingSweDataArrayObservation.class;
    }

    @Override
    public Class<? extends TextObservation> textClass() {
        return EReportingTextObservation.class;
    }

    @Override
    public Class<? extends ComplexObservation> complexClass() {
        return EReportingComplexObservation.class;
    }

    public static EReportingObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final EReportingObservationFactory INSTANCE
                = new EReportingObservationFactory();

        private Holder() {
        }
    }

}

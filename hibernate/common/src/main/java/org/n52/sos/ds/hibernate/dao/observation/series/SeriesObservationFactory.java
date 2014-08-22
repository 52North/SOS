package org.n52.sos.ds.hibernate.dao.observation.series;

import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesTextObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SeriesObservationFactory extends ObservationFactory {
    protected SeriesObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Observation> observationClass() {
        return AbstractSeriesObservation.class;
    }

    @Override
    public Class<? extends TemporalReferencedObservation> temporalReferencedClass() {
        return TemporalReferencedSeriesObservation.class;
    }

    @Override
    public Class<? extends ContextualReferencedObservation> contextualReferencedClass() {
        return ContextualReferencedSeriesObservation.class;
    }

    @Override
    public Class<? extends BlobObservation> blobClass() {
        return SeriesBlobObservation.class;
    }

    @Override
    public Class<? extends BooleanObservation> truthClass() {
        return SeriesBooleanObservation.class;
    }

    @Override
    public Class<? extends CategoryObservation> categoryClass() {
        return SeriesCategoryObservation.class;
    }

    @Override
    public Class<? extends CountObservation> countClass() {
        return SeriesCountObservation.class;
    }

    @Override
    public Class<? extends GeometryObservation> geometryClass() {
        return SeriesGeometryObservation.class;
    }

    @Override
    public Class<? extends NumericObservation> numericClass() {
        return SeriesNumericObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayObservation> sweDataArrayClass() {
        return SeriesSweDataArrayObservation.class;
    }

    @Override
    public Class<? extends TextObservation> textClass() {
        return SeriesTextObservation.class;
    }

    @Override
    public Class<? extends ComplexObservation> complexClass() {
        return SeriesComplexObservation.class;
    }

    public static SeriesObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final SeriesObservationFactory INSTANCE
                = new SeriesObservationFactory();

        private Holder() {
        }
    }
}

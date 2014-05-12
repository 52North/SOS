package org.n52.sos.ds.hibernate.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.values.ObservationValue;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueDAO extends AbstractValueDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueDAO.class);

    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest,
            Criterion temporalFilterCriterion, Session session) throws HibernateException, OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session).scroll(
                ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest, Session session)
            throws HibernateException, OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session).scroll(ScrollMode.FORWARD_ONLY);
    }

    @SuppressWarnings("unchecked")
    public List<ObservationValue> getStreamingValuesFor(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest,
            Criterion temporalFilterCriterion, int chunkSize, int currentRow, Session session) throws OwsExceptionReport {
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow);
        LOGGER.debug("QUERY getStreamingValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<ObservationValue>) c.list();
    }

    @SuppressWarnings("unchecked")
    public List<ObservationValue> getStreamingValuesFor(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest, int chunkSize,
            int currentRow, Session session) throws OwsExceptionReport {
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow);
        LOGGER.debug("QUERY getStreamingValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<ObservationValue>) c.list();
    }

    private void addChunkValuesToCriteria(Criteria c, int chunkSize, int currentRow) {
        c.addOrder(Order.asc(ObservationValue.PHENOMENON_TIME_START));
        c.setMaxResults(chunkSize).setFirstResult(currentRow);
    }

    private Criteria getValueCriteriaFor(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        final Criteria c =
                getDefaultObservationCriteria(ObservationValue.class, session).createAlias(Observation.PROCEDURE, "p").createAlias(Observation.FEATURE_OF_INTEREST, "f").createAlias(Observation.OBSERVABLE_PROPERTY, "o");

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.add(Restrictions.eq("p." + Procedure.ID, observableProperty));
        c.add(Restrictions.eq("o." + ObservableProperty.ID, observableProperty));
        c.add(Restrictions.eq("f." + FeatureOfInterest.ID, featureOfInterest));

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(Observation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, series, offerings";
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.setReadOnly(true);
    }

    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(ObservationValue.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    public String getUnit(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest, Session session) throws OwsExceptionReport {
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session);
        Unit unit = (Unit)c.setMaxResults(1).setProjection(Projections.property(ObservationValue.UNIT)).uniqueResult();
        if (unit != null && unit.isSetUnit()) {
            return unit.getUnit();
        }
        return null;
    }

}

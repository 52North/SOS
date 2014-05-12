package org.n52.sos.ds.hibernate.values;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.n52.sos.ds.hibernate.entities.values.ObservationValue;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.http.HTTPStatus;

public class HibernateScrollableStreamingValue extends HibernateStreamingValue {

    private static final long serialVersionUID = -1113871324524260053L;
    
    private ScrollableResults scrollableResult;

    public HibernateScrollableStreamingValue(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest) {
        super(request, procedure, observableProperty, featureOfInterest);
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
            if (scrollableResult == null) {
                getNextResults();
                if (scrollableResult != null) {
                    next = scrollableResult.next();
                }
            } else {
                next = scrollableResult.next();
            }
            if (!next) {
                sessionHolder.returnSession(session);
            }
        return next;
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            ObservationValue resultObject = (ObservationValue) scrollableResult.get()[0];
            TimeValuePair value = createTimeValuePairFrom(resultObject);
            session.evict(resultObject);
            return value;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            OmObservation observation = observationTemplate.cloneTemplate();
            ObservationValue resultObject = (ObservationValue) scrollableResult.get()[0];
            addValuesToObservation(observation, resultObject);
            session.evict(resultObject);
            return observation;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void getNextResults() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }
        try {
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    setScrollableResult(valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest,
                            temporalFilterCriterion, session));
                }
                // query without temporal or indeterminate filters
                else {
                    setScrollableResult(valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest, session));
                }
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private void setScrollableResult(ScrollableResults scrollableResult) {
        this.scrollableResult = scrollableResult;
    }

}

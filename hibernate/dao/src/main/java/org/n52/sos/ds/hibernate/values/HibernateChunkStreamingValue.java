package org.n52.sos.ds.hibernate.values;

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.n52.sos.ds.hibernate.entities.values.ObservationValue;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;

public class HibernateChunkStreamingValue extends HibernateStreamingValue {

    private static final long serialVersionUID = -4898252375907510691L;
    
    private Iterator<ObservationValue> valuesResult;

    private int chunkSize;

    private int currentRow;

    public HibernateChunkStreamingValue(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest) {
        super(request, procedure, observableProperty, featureOfInterest);
        this.chunkSize = HibernateStreamingConfiguration.getInstance().getChunkSize();
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
        if (valuesResult == null || !valuesResult.hasNext()) {
            getNextResults();
        }
        if (valuesResult != null) {
            next = valuesResult.hasNext();
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }

        return next;
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            if (hasNextValue()) {
                ObservationValue resultObject = valuesResult.next();
                TimeValuePair value = createTimeValuePairFrom(resultObject);
                session.evict(resultObject);
                return value;
            }
            return null;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            if (hasNextValue()) {
                OmObservation observation = observationTemplate.cloneTemplate();
                ObservationValue resultObject = valuesResult.next();
                addValuesToObservation(observation, resultObject);
                session.evict(resultObject);
                return observation;
            }
            return null;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setObservationTemplate(OmObservation observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    private void getNextResults() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }
        try {
            // query with temporal filter
            if (temporalFilterCriterion != null) {
                setObservationValuesResult(valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest,
                        temporalFilterCriterion, chunkSize, currentRow, session));
            }
            // query without temporal or indeterminate filters
            else {
                setObservationValuesResult(valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest, chunkSize,
                        currentRow, session));
            }
            currentRow += chunkSize;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void setObservationValuesResult(List<ObservationValue> valuesResult) {
        if (CollectionHelper.isNotEmpty(valuesResult)) {
            this.valuesResult = valuesResult.iterator();
        }

    }

}

package org.n52.sos.ds.hibernate.dao.ereporting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.series.SeriesIdentifiers;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.series.Series;

public class EReportingSeriesIdentifiers extends SeriesIdentifiers {
    private EReportingSamplingPoint samplingPoint;

    /**
     * @return the featureOfInterest
     */
    public EReportingSamplingPoint getEReportingSamplingPoint() {
        return samplingPoint;
    }

    /**
     * @param featureOfInterest
     *            the featureOfInterest to set
     */
    public void setEReportingSamplingPoint(EReportingSamplingPoint samplingPoint) {
        this.samplingPoint = samplingPoint;
    }

    public boolean isSetEReportingSamplingPoint() {
        return getEReportingSamplingPoint() != null;
    }

    @Override
    public void addIdentifierRestrictionsToCritera(Criteria c) {
        super.addIdentifierRestrictionsToCritera(c);
        if (isSetEReportingSamplingPoint()) {
            addEReportingSamplingPointToCriteria(c, getEReportingSamplingPoint());
        }
    }

    @Override
    public void addValuesToSeries(Series series) {
        super.addValuesToSeries(series);
        if (isSetEReportingSamplingPoint() && series instanceof EReportingSeries) {
            ((EReportingSeries) series).setEReportingSamplingPoint(getEReportingSamplingPoint());
        }
    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint to add
     */
    private void addEReportingSamplingPointToCriteria(Criteria c, EReportingSamplingPoint samplingPoint) {
        c.add(Restrictions.eq(EReportingSeries.SAMPLING_POINT, samplingPoint));

    }
}

package org.n52.sos.ds.hibernate.dao.series;

import org.hibernate.Criteria;
import org.n52.sos.ds.hibernate.entities.series.Series;

public abstract class AbstractSeriesIdentifiers {
    
    public abstract void addIdentifierRestrictionsToCritera(Criteria c);
    
    public abstract void addValuesToSeries(Series series);

}

package org.n52.sos.ds.dao;

import java.util.List;
import java.util.Map;

import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.ogc.om.NamedValue;

import dao.DataAvailability;

public interface GetDataAvailabilityDao {

    List<TimeInstant> getResultTimes(DataAvailability dataAvailability, GetDataAvailabilityRequest request);

    Map<String, NamedValue> getMetadata(DatasetEntity<?> entity);

}

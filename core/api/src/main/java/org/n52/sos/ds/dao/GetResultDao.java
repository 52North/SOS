package org.n52.sos.ds.dao;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.4.0
 *
 */
public interface GetResultDao extends DefaultDao {

    GetResultResponse queryResultData(GetResultRequest request, GetResultResponse response)
            throws OwsExceptionReport;

    GetResultResponse queryResultData(GetResultRequest request, GetResultResponse response,
            Object connection) throws OwsExceptionReport;

}

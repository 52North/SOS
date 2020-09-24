package org.n52.sos.ds.dao;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.4.0
 *
 */
public interface GetResultTemplateDao extends DefaultDao {

    GetResultTemplateResponse queryResultTemplate(GetResultTemplateRequest request, GetResultTemplateResponse response)
            throws OwsExceptionReport;

    GetResultTemplateResponse queryResultTemplate(GetResultTemplateRequest request, GetResultTemplateResponse response,
            Object connection) throws OwsExceptionReport;

}

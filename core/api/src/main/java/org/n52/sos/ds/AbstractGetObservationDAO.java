/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsParameterValueRange;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.MinMax;
import org.n52.sos.util.SosHelper;

/**
 * interface for getting observations for a passed getObservation request from
 * the data source
 * 
 * @since 4.0.0
 * 
 */
public abstract class AbstractGetObservationDAO extends AbstractOperationDAO {

    public AbstractGetObservationDAO(final String service) {
        super(service, SosConstants.Operations.GetObservation.name());
    }

    @Override
    protected void setOperationsMetadata(final OwsOperation opsMeta, final String service, final String version)
            throws OwsExceptionReport {

        final Collection<String> featureIDs = SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version);
        addOfferingParameter(opsMeta);
        addProcedureParameter(opsMeta);
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.responseFormat, CodingRepository
                .getInstance().getSupportedResponseFormats(SosConstants.SOS, version));

        addObservablePropertyParameter(opsMeta);
        addFeatureOfInterestParameter(opsMeta, featureIDs);

        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            // SOS 2.0 parameter
            final OwsParameterValueRange temporalFilter = new OwsParameterValueRange(getPhenomenonTime());
            opsMeta.addRangeParameterValue(Sos2Constants.GetObservationParams.temporalFilter, temporalFilter);
            SosEnvelope envelope = null;
            if (featureIDs != null && !featureIDs.isEmpty()) {
                envelope = getCache().getGlobalEnvelope();
            }
            if (envelope != null && envelope.isSetEnvelope()) {
                opsMeta.addRangeParameterValue(Sos2Constants.GetObservationParams.spatialFilter,
                        SosHelper.getMinMaxFromEnvelope(envelope.getEnvelope()));
            }
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            // SOS 1.0.0 parameter
            opsMeta.addRangeParameterValue(Sos1Constants.GetObservationParams.eventTime, getPhenomenonTime());
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.srsName);
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.result);
            opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.resultModel, getResultModels());
            opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.responseMode,
                    SosConstants.RESPONSE_MODES);
        }
    }

    /**
     * Get the min/max phenomenon time of contained observations
     * 
     * @return min/max phenomenon time
     * 
     * 
     * @throws OwsExceptionReport
     *             * If an error occurs.
     */
    private MinMax<String> getPhenomenonTime() throws OwsExceptionReport {
        final DateTime minDate = getCache().getMinPhenomenonTime();
        final DateTime maxDate = getCache().getMaxPhenomenonTime();
        return new MinMax<String>().setMinimum(
                minDate != null ? DateTimeHelper.formatDateTime2ResponseString(minDate) : null).setMaximum(
                maxDate != null ? DateTimeHelper.formatDateTime2ResponseString(maxDate) : null);
    }

    /**
     * Get the min/max result time of contained observations
     * 
     * @return min/max result time
     * 
     * 
     * @throws OwsExceptionReport
     *             * If an error occurs.
     */
    protected MinMax<String> getResultTime() throws OwsExceptionReport {
        final DateTime minDate = getCache().getMinResultTime();
        final DateTime maxDate = getCache().getMaxResultTime();
        return new MinMax<String>().setMinimum(
                minDate != null ? DateTimeHelper.formatDateTime2ResponseString(minDate) : null).setMaximum(
                maxDate != null ? DateTimeHelper.formatDateTime2ResponseString(maxDate) : null);
    }

    private List<String> getResultModels() {
        final List<String> resultModelsList = new ArrayList<String>(OmConstants.RESULT_MODELS.size());
        for (final QName qname : OmConstants.RESULT_MODELS) {
            resultModelsList.add(qname.getPrefix() + ":" + qname.getLocalPart());
        }
        return resultModelsList;
    }

    /**
     * process the GetObservation query
     * 
     * @param request
     *            GetObservation object which represents the getObservation
     *            request
     * 
     * @return ObservationDocument representing the requested values in an OGC
     *         conform O&M observation document
     * 
     * @throws OwsExceptionReport
     *             * if query of the database or creating the O&M document
     *             failed
     */
    public abstract GetObservationResponse getObservation(GetObservationRequest request) throws OwsExceptionReport;

}

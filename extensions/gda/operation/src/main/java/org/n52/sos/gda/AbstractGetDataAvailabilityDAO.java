/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.gda;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.AbstractOperationDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.ResultFilterConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosSpatialFilterConstants;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.SosHelper;

import com.google.common.collect.Sets;

/**
 * DAO to get the DataAvailabilities out of the database.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
@Configurable
public abstract class AbstractGetDataAvailabilityDAO extends AbstractOperationDAO {
    
    public static final String INCLUDE_RESULT_TIMES = "IncludeResultTimes";
    public static final String SHOW_COUNT = "ShowCount";
    private boolean forceValueCount = false;
    private boolean forceGDAv20Response = false;

    public AbstractGetDataAvailabilityDAO(String service) {
        super(service, GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport {
        addQueryableProcedureParameter(operation);
        //addPublishedProcedureParameter(operation);
        addPublishedObservablePropertyParameter(operation);
        addPublishedFeatureOfInterestParameter(operation, version);
        addOfferingParameter(operation);
        operation.addAnyParameterValue(ResultFilterConstants.METADATA_RESULT_FILTER);
        final Collection<String> featureIDs = SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version);
        SosEnvelope envelope = null;
        if (featureIDs != null && !featureIDs.isEmpty()) {
            envelope = getCache().getGlobalEnvelope();
        }
        if (envelope != null && envelope.isSetEnvelope()) {
            operation.addRangeParameterValue(Sos2Constants.GetObservationParams.spatialFilter,
                    SosHelper.getMinMaxFromEnvelope(envelope.getEnvelope()));
        } else {
            operation.addAnyParameterValue(Sos2Constants.GetObservationParams.spatialFilter);
        }
    }

    /**
     * Get the DataAvailability out of the Database.
     * 
     * @param sosRequest
     *            the <code>GetDataAvailabilityRequest</code>
     * @return the <code>GetDataAvailabilityResponse</code>
     * 
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    public abstract GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest sosRequest)
            throws OwsExceptionReport;

    /**
     * @return the forceValueCount
     */
    protected boolean isForceValueCount() {
        return forceValueCount;
    }

    /**
     * @param forceValueCount the forceValueCount to set
     */
    @Setting(GetDataAvailabilitySettings.FORCE_GDA_VALUE_COUNT)
    public void setForceValueCount(boolean forceValueCount) {
        this.forceValueCount = forceValueCount;
    }
    
    /**
     * @return the forEachOffering
     */
    protected boolean isForceGDAv20Response() {
        return forceGDAv20Response;
    }

    /**
     * @param forceGDAv20Response the forceGDAv20Response to set
     */
    @Setting(GetDataAvailabilitySettings.FORCE_GDA_20_RESPONSE)
    public void setForceGDAv20Response(boolean forceGDAv20Response) {
        this.forceGDAv20Response = forceGDAv20Response;
    }
    
    @Override
    public Set<String> getConformanceClasses() {
        return Sets.newHashSet(ResultFilterConstants.CONFORMANCE_CLASS_RF, SosSpatialFilterConstants.CONFORMANCE_CLASS_SF);
    }
}

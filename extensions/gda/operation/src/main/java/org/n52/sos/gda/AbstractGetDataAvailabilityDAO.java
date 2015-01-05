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
package org.n52.sos.gda;

import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.AbstractOperationDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;

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

    public AbstractGetDataAvailabilityDAO(String service) {
        super(service, GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport {
        addProcedureParameter(operation);
        addObservablePropertyParameter(operation);
        addFeatureOfInterestParameter(operation, version);
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
}

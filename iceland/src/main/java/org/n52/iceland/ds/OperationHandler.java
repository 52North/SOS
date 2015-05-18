/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ds;

import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.service.ConformanceClass;

/**
 * Interface for all operation Handlers.
 * 
 * In 52N SOS version 4.x called OperationDAO
 * 
 * @since 1.0.0
 */
public interface OperationHandler extends DatasourceDaoIdentifier, ConformanceClass{

    /**
     * Get the operation and service key this Handler supports
     * 
     * @return The supported operation name
     */
    OperationHandlerKeyType getOperationHandlerKeyType();

    /**
     * TODO check if necessary in feature
     * 
     * Get the operation name this Handler supports
     * 
     * @return The supported operation name
     */
    String getOperationName();

    /**
     * Get the OperationsMetadata of the supported SOS operation for the
     * capabilities
     * 
     * @param service
     *            OGC service identifier
     * @param version
     *            Service version
     * @return OperationsMetadata for the operation
     * 
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    OwsOperation getOperationsMetadata(String service, String version) throws OwsExceptionReport;

    // /**
    // * @return the operation specific extension information (&larr; should be
    // available from cache or from code)
    // *
    // * @throws OwsExceptionReport
    // */
    // // SosCapabilitiesExtension getExtension() throws OwsExceptionReport;
    
}

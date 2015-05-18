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

/**
 * In 52N SOS version 4.x called OperationDAOKeyType
 * 
 * @since 1.0.0
 * 
 */
public class OperationHandlerKeyType implements Comparable<OperationHandlerKeyType> {

    private String operationName;

    private String service;

    public OperationHandlerKeyType() {
        super();
    }

    public OperationHandlerKeyType(String service, String operationName) {
        super();
        this.service = service;
        this.operationName = operationName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public int compareTo(OperationHandlerKeyType o) {
        if (o instanceof OperationHandlerKeyType) {
            if (service.equals(o.service) && operationName.equals(o.operationName)) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object paramObject) {
        if (service != null && operationName != null && paramObject instanceof OperationHandlerKeyType) {
            OperationHandlerKeyType toCheck = (OperationHandlerKeyType) paramObject;
            return (service.equals(toCheck.service) && operationName.equals(toCheck.operationName));
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + ((this.service != null) ? this.service.hashCode() : 0);
        hash = prime * hash + ((this.operationName != null) ? this.operationName.hashCode() : 0);
        return hash;
    }

}

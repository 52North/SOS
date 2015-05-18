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
package org.n52.iceland.convert;

/**
 * @since 4.0.0
 * 
 */
public class ConverterKeyType implements Comparable<ConverterKeyType> {

    private String fromNamespace;

    private String toNamespace;

    public ConverterKeyType(String fromNamespace, String toNamespace) {
        this.fromNamespace = fromNamespace;
        this.toNamespace = toNamespace;
    }

    public String getFromNamespace() {
        return fromNamespace;
    }

    public String getToNamespace() {
        return toNamespace;
    }

    @Override
    public int compareTo(ConverterKeyType o) {
        if (o instanceof ConverterKeyType) {
            if (checkParameter(fromNamespace, o.getFromNamespace())
                    && checkParameter(toNamespace, o.getFromNamespace())) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object paramObject) {
        if (paramObject instanceof ConverterKeyType) {
            ConverterKeyType toCheck = (ConverterKeyType) paramObject;
            return (checkParameter(fromNamespace, toCheck.fromNamespace) && checkParameter(toNamespace,
                    toCheck.toNamespace));
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + (this.fromNamespace != null ? this.fromNamespace.hashCode() : 0);
        hash = prime * hash + (this.toNamespace != null ? this.toNamespace.hashCode() : 0);
        return hash;
    }

    private boolean checkParameter(String localParameter, String parameterToCheck) {
        if (localParameter == null && parameterToCheck == null) {
            return true;
        }
        return localParameter != null && parameterToCheck != null && localParameter.equals(parameterToCheck);
    }

    @Override
    public String toString() {
        return String.format("%s[from=%s, to=%s]", getClass().getSimpleName(), fromNamespace, toNamespace);
    }

}

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
package org.n52.iceland.ogc.swe;

import org.n52.iceland.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE dataRecord
 * 
 * @since 4.0.0
 */
public class SweDataRecord extends SweAbstractDataRecord {

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.DataRecord;
    }

    @Override
    public SweDataRecord addField(final SweField field) {
        return (SweDataRecord) super.addField(field);
    }

    @Override
    public int hashCode() {
        final int prime = 42;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getDataComponentType() != null ? getDataComponentType().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("SweDataRecord [fields=%s, definition=%s, label=%s, identifier=%s, xml=%s]", getFields(),
                getDefinition(), getLabel(), getIdentifier(), getXml());
    }

}

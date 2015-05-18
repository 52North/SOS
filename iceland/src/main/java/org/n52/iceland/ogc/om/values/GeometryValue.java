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
package org.n52.iceland.ogc.om.values;

import org.n52.iceland.ogc.gml.AbstractGeometry;
import org.n52.iceland.util.StringHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Geometry measurement representation for observation
 * @since 4.0.0
 * 
 */
public class GeometryValue extends AbstractGeometry implements Value<Geometry> {
    private static final long serialVersionUID = 4634315072352929082L;
    /**
     * Unit of measure
     */
    private String unit;
    
    public GeometryValue(AbstractGeometry abstractGeometry) {
        setDescription(abstractGeometry.getDescription());
        setGeometry(abstractGeometry.getGeometry());
        setIdentifier(abstractGeometry.getIdentifierCodeWithAuthority());
        setName(abstractGeometry.getName());
    }

    /**
     * construcor
     * @param value Geometry value
     */
    public GeometryValue(Geometry value) {
        setValue(value);
    }

    @Override
    public void setValue(Geometry value) {
        setGeometry(value);
    }

    @Override
    public Geometry getValue() {
        return getGeometry();
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("GeometryValue [value=%s, unit=%s]", getValue(), getUnit());
    }

    @Override
    public boolean isSetValue() {
        return isSetGeometry();
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

}

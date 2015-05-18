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

import org.n52.iceland.ogc.gml.CodeType;
import org.n52.iceland.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE field
 * 
 * @since 4.0.0
 */
public class SweField extends SweAbstractDataComponent {

    /**
     * field element
     */
    private SweAbstractDataComponent element;

    /**
     * constructor
     * 
     * @param name
     *            Field name
     * @param element
     *            Field element
     */
    public SweField(final String name, final SweAbstractDataComponent element) {
        super();
        setName(name);
        this.element = element;
    }
    
    public SweField(final CodeType name, final SweAbstractDataComponent element) {
        super();
        setName(name);
        this.element = element;
    }

//    /**
//     * @return the name
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * @param name
//     *            the name to set
//     * @return This SweField
//     */
//    public SweField setName(final String name) {
//        this.name = name;
//        return this;
//    }

    /**
     * @return the element
     */
    public SweAbstractDataComponent getElement() {
        return element;
    }

    /**
     * @param element
     *            the element to set
     * @return This SweField
     */
    public SweField setElement(final SweAbstractDataComponent element) {
        this.element = element;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int hash = 3;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getName() != null ? getName().hashCode() : 0);
        hash = prime * hash + (getElement() != null ? getElement().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweField other = (SweField) obj;
        if ((getName() == null) ? (other.getName() != null) : !getName().equals(other.getName())) {
            return false;
        }
        if (getElement() != other.getElement() && (getElement() == null || !getElement().equals(other.getElement()))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("SosSweField[name=%s, element=%s]", getName(), getElement());
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Field;
    }
}

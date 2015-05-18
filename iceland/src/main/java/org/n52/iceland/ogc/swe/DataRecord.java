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

import java.util.List;
import java.util.Set;

import org.n52.iceland.ogc.swe.simpleType.SweAbstractSimpleType;

/**
 * @since 4.0.0
 * 
 */
public interface DataRecord {

    /**
     * @return the fields
     */
    List<SweField> getFields();

    /**
     * @param fields
     *            the fields to set
     * @return this
     */
    DataRecord setFields(List<SweField> fields);

    /**
     * Adds field to field list
     * 
     * @param field
     *            Field to add
     * @return this
     */
    DataRecord addField(SweField field);

    /**
     * Check if there are field elements
     * 
     * @return Fields not empty
     */
    boolean isSetFields();

    /**
     * Return the index of the first field with the given
     * <tt>fieldNameOrElementDefinition</tt> or -1 if not found.
     * 
     * @param fieldNameOrElementDefinition
     *            a definition identifying a {@link SweField} in this
     *            {@link DataRecord}.
     * @return the index of the first field with the given fieldDefinition or -1
     *         if not found.
     */
    int getFieldIndexByIdentifier(String fieldNameOrElementDefinition);

    @SuppressWarnings("rawtypes")
    Set<SweAbstractSimpleType<?>> getSweAbstractSimpleTypeFromFields(Class clazz);

}

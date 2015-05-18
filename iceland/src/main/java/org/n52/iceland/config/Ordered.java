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
package org.n52.iceland.config;

/**
 * Generic class to implement a Order based on floating point numbers.
 * 
 * @param <T>
 *            the type of the extending class
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface Ordered<T extends Ordered<T>> extends Comparable<Ordered<?>> {

    /**
     * @return the order of this clas represented as a <code>float</code>
     */
    float getOrder();

    /**
     * @param order
     *            the order
     *            <p/>
     * @return <code>this</code>
     */
    T setOrder(float order);
}

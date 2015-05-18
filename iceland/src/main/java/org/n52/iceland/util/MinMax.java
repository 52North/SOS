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
package org.n52.iceland.util;

/**
 * @param <T>
 *            the type
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class MinMax<T> {

    private T minimum;

    private T maximum;

    public MinMax(T minimum, T maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public MinMax() {
    }

    /**
     * Get the value of minimum
     * 
     * @return the value of minimum
     */
    public T getMinimum() {
        return minimum;
    }

    /**
     * Set the value of minimum
     * 
     * @param minimum
     *            new value of minimum
     *            <p/>
     * @return this
     */
    public MinMax<T> setMinimum(T minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get the value of maximum
     * 
     * @return the value of maximum
     */
    public T getMaximum() {
        return maximum;
    }

    /**
     * Set the value of maximum
     * 
     * @param maximum
     *            new value of maximum
     *            <p/>
     * @return this
     */
    public MinMax<T> setMaximum(T maximum) {
        this.maximum = maximum;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            final MinMax<T> other = (MinMax<T>) obj;
            return (getMinimum() == null ? other.getMinimum() == null : other.getMinimum().equals(getMinimum()))
                    && (getMaximum() == null ? other.getMaximum() == null : other.getMaximum().equals(getMaximum()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int hash = 7;
        hash = prime * hash + (getMinimum() != null ? getMinimum().hashCode() : 0);
        hash = prime * hash + (getMaximum() != null ? getMaximum().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("MinMax[minimum=%s, maximum=%s]", getMinimum(), getMaximum());
    }

}

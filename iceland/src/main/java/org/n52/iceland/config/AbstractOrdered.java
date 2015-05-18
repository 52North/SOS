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
 * Abstract, generic implementation of {@code Ordered}.
 * <p/>
 * 
 * @param <T>
 *            the type of the class extending this class
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractOrdered<T extends Ordered<T>> implements Ordered<T> {

    private float order;

    @Override
    public float getOrder() {
        return this.order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setOrder(float order) {
        this.order = order;
        return (T) this;
    }

    @Override
    public int compareTo(Ordered<?> t) {
        int compare = Float.compare(getOrder(), t.getOrder());
        if (compare == 0 && t instanceof AbstractOrdered) {
            AbstractOrdered<?> ao = (AbstractOrdered) t;
            if (getSuborder() == null) {
                return 1;
            } else if (ao.getSuborder() == null) {
                return -1;
            } else {
                return getSuborder().compareTo(ao.getSuborder());
            }
        }
        return compare;
    }

    protected abstract String getSuborder();
}

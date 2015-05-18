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
package org.n52.iceland.binding;

/**
 * Class to identify a Binding. Used to keep the interfaces stable if the
 * identification of a Binding changes from servlet path to e.g. Content-Type.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class BindingKey {
    private final String servletPath;

    public BindingKey(String servletPath) {
        if (servletPath == null) {
            throw new NullPointerException();
        }
        this.servletPath = servletPath;
    }

    public String getServletPath() {
        return servletPath;
    }

    @Override
    public int hashCode() {
        return 41 * 7 + this.servletPath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BindingKey) {
            BindingKey o = (BindingKey) obj;
            return getServletPath() == null ? o.getServletPath() == null : getServletPath().equals(o.getServletPath());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("BindingKey[servletPath=%s]", getServletPath());
    }
}

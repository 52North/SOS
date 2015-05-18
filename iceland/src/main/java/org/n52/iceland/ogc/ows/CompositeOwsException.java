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
package org.n52.iceland.ogc.ows;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.iceland.exception.CodedException;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CompositeOwsException extends OwsExceptionReport {
    private static final long serialVersionUID = -4876354677532448922L;

    private List<CodedException> exceptions = new LinkedList<CodedException>();

    public CompositeOwsException(OwsExceptionReport... exceptions) {
        add(exceptions);
    }

    public CompositeOwsException(Collection<? extends OwsExceptionReport> exceptions) {
        add(exceptions);
    }

    public CompositeOwsException() {
    }

    public CompositeOwsException add(Collection<? extends OwsExceptionReport> exceptions) {
        if (exceptions != null) {
            for (OwsExceptionReport e : exceptions) {
                this.exceptions.addAll(e.getExceptions());
            }
            if (getCause() == null && !this.exceptions.isEmpty()) {
                initCause(this.exceptions.get(0));
            }
        }
        return this;
    }

    public CompositeOwsException add(OwsExceptionReport... exceptions) {
        return add(Arrays.asList(exceptions));
    }

    @Override
    public List<? extends CodedException> getExceptions() {
        return Collections.unmodifiableList(this.exceptions);
    }

    public void throwIfNotEmpty() throws CompositeOwsException {
        if (hasExceptions()) {
            throw this;
        }
    }

    public int size() {
        return this.exceptions.size();
    }

    public boolean hasExceptions() {
        return !this.exceptions.isEmpty();
    }
}

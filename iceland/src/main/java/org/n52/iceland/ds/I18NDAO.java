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
package org.n52.iceland.ds;

import java.util.Collection;
import java.util.Locale;

import org.n52.iceland.i18n.metadata.AbstractI18NMetadata;
import org.n52.iceland.ogc.ows.OwsExceptionReport;

/**
 * Interface for the I18N DAOs
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.2.0
 *
 */
public interface I18NDAO<T extends AbstractI18NMetadata> {
    Class<T> getType();
    boolean isSupported();
    T getMetadata(String id) throws OwsExceptionReport;
    T getMetadata(String id, Locale locale) throws OwsExceptionReport;
    Collection<T> getMetadata() throws OwsExceptionReport;
    Collection<T> getMetadata(Collection<String> id) throws OwsExceptionReport;
    Collection<T> getMetadata(Collection<String> id, Locale locale) throws OwsExceptionReport;
    void saveMetadata(T i18n) throws OwsExceptionReport;
    Collection<Locale> getAvailableLocales() throws OwsExceptionReport;
}

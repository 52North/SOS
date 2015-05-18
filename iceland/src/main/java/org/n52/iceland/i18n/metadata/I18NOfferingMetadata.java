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
package org.n52.iceland.i18n.metadata;

import org.n52.iceland.i18n.MultilingualString;

/**
 * I18N object class for offering
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class I18NOfferingMetadata extends AbstractI18NMetadata {

    public I18NOfferingMetadata(String id, MultilingualString name,
                              MultilingualString description) {
        super(id, name, description);
    }

    public I18NOfferingMetadata(String id) {
        super(id);
    }

}

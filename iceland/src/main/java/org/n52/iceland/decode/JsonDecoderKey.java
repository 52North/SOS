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
package org.n52.iceland.decode;

import org.n52.iceland.util.ClassHelper;

import com.google.common.base.Objects;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class JsonDecoderKey implements DecoderKey {
    private final Class<?> type;

    public JsonDecoderKey(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s[type=%s]", getClass().getSimpleName(), getType());
    }

    @Override
    public int getSimilarity(DecoderKey key) {
        if (key != null && key.getClass() == getClass()) {
            JsonDecoderKey jsonKey = (JsonDecoderKey) key;
            return ClassHelper.getSimiliarity(getType() != null ? getType() : Object.class,
                    jsonKey.getType() != null ? jsonKey.getType() : Object.class);
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(7, 79, getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() != obj.getClass()) {
            final JsonDecoderKey other = (JsonDecoderKey) obj;
            return Objects.equal(getType(), other.getType());
        }
        return false;
    }
}

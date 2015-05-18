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
package org.n52.iceland.encode;

import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.util.http.MediaType;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OperationEncoderKey extends OperationKey implements EncoderKey {
    private final MediaType contentType;

    public OperationEncoderKey(String service, String version, String operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationEncoderKey(String service, String version, Enum<?> operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationEncoderKey(OperationKey key, MediaType contentType) {
        super(key);
        this.contentType = contentType;
    }

    @Override
    public int getSimilarity(EncoderKey key) {
        return equals(key) ? 0 : -1;
    }

    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.contentType);
    }

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);
        if (obj != null && getClass() == obj.getClass()) {
            final OperationEncoderKey other = (OperationEncoderKey) obj;
            return super.equals(obj) && getContentType() != null
                    && getContentType().isCompatible(other.getContentType());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[service=%s, version=%s, operation=%s, contentType=%s]", getClass().getSimpleName(),
                getService(), getVersion(), getOperation(), getContentType());
    }
}

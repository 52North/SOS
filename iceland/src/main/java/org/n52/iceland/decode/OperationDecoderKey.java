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

import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.util.http.MediaType;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class OperationDecoderKey extends OperationKey implements DecoderKey {
    private final MediaType contentType;

    public OperationDecoderKey(String service, String version, String operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationDecoderKey(String service, String version, Enum<?> operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationDecoderKey(OperationKey key, MediaType contentType) {
        super(key);
        this.contentType = contentType;
    }

    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return String.format("%s[service=%s, version=%s, operation=%s, contentType=%s]", getClass().getSimpleName(),
                getService(), getVersion(), getOperation(), getContentType());
    }

    @Override
    public int getSimilarity(DecoderKey key) {
        return equals(key) ? 0 : -1;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getContentType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final OperationDecoderKey o = (OperationDecoderKey) obj;
            return Objects.equal(getService(), o.getService()) && Objects.equal(getVersion(), o.getVersion())
                    && Objects.equal(getOperation(), o.getOperation()) && getContentType() != null
                    && getContentType().isCompatible(o.getContentType());
        }
        return false;
    }
}

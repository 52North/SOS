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
package org.n52.iceland.response;

import org.n52.iceland.ogc.swes.SwesConstants.HasSwesExtension;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.iceland.ogc.swes.SwesExtensions;
import org.n52.iceland.service.AbstractServiceCommunicationObject;
import org.n52.iceland.util.http.MediaType;

/**
 * abstract super class for all service request classes
 * 
 * @since 4.0.0
 */
public abstract class AbstractServiceResponse extends AbstractServiceCommunicationObject implements HasSwesExtension<AbstractServiceResponse> {

    private MediaType contentType;
    
private SwesExtensions extensions;
    
    @Override
    public SwesExtensions getExtensions() {
        return extensions;
    }

    @Override
    public AbstractServiceResponse setExtensions(final SwesExtensions extensions) {
        this.extensions = extensions;
        return this;
    }
    
    @Override
    public AbstractServiceResponse addExtensions(final SwesExtensions extensions) {
        if (getExtensions() == null) {
            setExtensions(extensions);
        } else {
            getExtensions().addSwesExtension(extensions.getExtensions());
        }
        return this;
    }

    @Override
    public AbstractServiceResponse addExtension(final SwesExtension extension) {
        if (getExtensions() == null) {
            setExtensions(new SwesExtensions());
        }
        getExtensions().addSwesExtension(extension);
        return this;
    }

    @Override
    public boolean isSetExtensions() {
        return extensions != null && !extensions.isEmpty();
    }
    
    public AbstractServiceResponse setContentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public MediaType getContentType() {
        return this.contentType;
    }

    public boolean isSetContentType() {
        return getContentType() != null;
    }

}

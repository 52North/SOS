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
package org.n52.iceland.ogc.gml;

import java.io.Serializable;

import org.n52.iceland.util.StringHelper;

/**
 * Abstract class for encoding the feature of interest. Necessary because
 * different feature types should be supported. The SOS database or another
 * feature source (e.g. WFS) should provide information about the application
 * schema.
 * 
 * @since 4.0.0
 */
/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since
 * 
 */
public abstract class AbstractFeature extends AbstractGML implements Serializable {

    /**
     * serial number
     */
    private static final long serialVersionUID = -6117378246552782214L;
    
    private String defaultEncoding;
    
    /**
     * constructor
     */
    public AbstractFeature() {
        super();
    }

    /**
     * constructor
     */
    public AbstractFeature(String identifier) {
        super(identifier);
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            Feature identifier
     */
    public AbstractFeature(CodeWithAuthority featureIdentifier) {
        super(featureIdentifier);
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            Feature identifier
     * @param gmlId
     *            GML id
     */
    public AbstractFeature(CodeWithAuthority featureIdentifier, String gmlId) {
        super(featureIdentifier, gmlId);
    }
    
    public void copyTo(AbstractFeature copyOf) {
        super.copyTo(copyOf);
    }
    
    public AbstractFeature setDefaultElementEncoding(String defaultEncoding) {
    	this.defaultEncoding = defaultEncoding;
    	return this;
    }

    public String getDefaultElementEncoding() {
    	return defaultEncoding;
    }
    
    public boolean isSetDefaultElementEncoding() {
    	return StringHelper.isNotEmpty(getDefaultElementEncoding());
    }

}

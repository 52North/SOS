/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.encode;

import java.util.Map;

import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Maps;

public class EncodingValues {
    
    private Map<HelperValues, String> additionalValues = Maps.newHashMap();
    
    private String gmlId;
    
    private boolean existFoiInDoc = false;
    
    private String version;
    
    private String type;
    
    private boolean asDocument = false;
    
    private boolean asPropertyType = false;
    
    private boolean encode = false;
    
    private String encodingNamespace;
    
    private boolean encodeOwsExceptionOnly = false;
    
    private boolean addSchemaLocation = false;
    
    private int indent = 0;
    
    private boolean embedded = false;
    
    private Encoder<?, ?> encoder;
    
    public EncodingValues() {
        
    }
    
    public EncodingValues(Map<HelperValues, String> additionalValues) {
        setAdditionalValues(additionalValues);
    }

    /**
     * @return the additionalValues
     */
    public Map<HelperValues, String> getAdditionalValues() {
        return additionalValues;
    }

    /**
     * @param additionalValues the additionalValues to set
     */
    public EncodingValues setAdditionalValues(Map<HelperValues, String> additionalValues) {
        this.additionalValues = additionalValues;
        return this;
    }
    
    public boolean hasAddtitionalValues() {
        return CollectionHelper.isEmpty(getAdditionalValues());
    }

    /**
     * @return the gmlId
     */
    public String getGmlId() {
        return gmlId;
    }

    /**
     * @param gmlId the gmlId to set
     */
    public EncodingValues setGmlId(String gmlId) {
        this.gmlId = gmlId;
        return this;
    }
    
    public boolean isSetGmlId() {
        return StringHelper.isNotEmpty(getGmlId());
    }

    /**
     * @return the existFoiInDoc
     */
    public boolean isExistFoiInDoc() {
        return existFoiInDoc;
    }

    /**
     * @param existFoiInDoc the existFoiInDoc to set
     */
    public EncodingValues setExistFoiInDoc(boolean existFoiInDoc) {
        this.existFoiInDoc = existFoiInDoc;
        return this;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public EncodingValues setVersion(String version) {
        this.version = version;
        return this;
    }
    
    public boolean isSetVersion() {
        return StringHelper.isNotEmpty(getVersion());
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public EncodingValues setType(String type) {
        this.type = type;
        return this;
    }
    
    public boolean isSetType() {
        return StringHelper.isNotEmpty(getType());
    }

    /**
     * @return the asDocument
     */
    public boolean isAsDocument() {
        return asDocument;
    }

    /**
     * @param asDocument the asDocument to set
     */
    public EncodingValues setAsDocument(boolean asDocument) {
        this.asDocument = asDocument;
        return this;
    }

    /**
     * @return the asPropertyType
     */
    public boolean isAsPropertyType() {
        return asPropertyType;
    }

    /**
     * @param asPropertyType the asPropertyType to set
     */
    public EncodingValues setAsPropertyType(boolean asPropertyType) {
        this.asPropertyType = asPropertyType;
        return this;
    }

    /**
     * @return the encode
     */
    public boolean isEncode() {
        return encode;
    }

    /**
     * @param encode the encode to set
     */
    public EncodingValues setEncode(boolean encode) {
        this.encode = encode;
        return this;
    }

    /**
     * @return the encodingNamespace
     */
    public String getEncodingNamespace() {
    	if (encodingNamespace == null && hasAddtitionalValues() && getAdditionalValues().containsKey(HelperValues.ENCODE_NAMESPACE)) {
    		setEncodingNamespace(getAdditionalValues().get(HelperValues.ENCODE_NAMESPACE));
    	}
        return encodingNamespace;
    }
    
    public boolean isSetEncodingNamespace() {
        return StringHelper.isNotEmpty(getEncodingNamespace());
    }

    /**
     * @param encodingNamespace the encodingNamespace to set
     */
    public EncodingValues setEncodingNamespace(String encodingNamespace) {
        this.encodingNamespace = encodingNamespace;
        return this;
    }

    /**
     * @return the encodeOwsExceptionOnly
     */
    public boolean isEncodeOwsExceptionOnly() {
        return encodeOwsExceptionOnly;
    }

    /**
     * @param encodeOwsExceptionOnly the encodeOwsExceptionOnly to set
     */
    public EncodingValues setEncodeOwsExceptionOnly(boolean encodeOwsExceptionOnly) {
        this.encodeOwsExceptionOnly = encodeOwsExceptionOnly;
        return this;
    }

    /**
     * @return the addSchemaLocation
     */
    public boolean isAddSchemaLocation() {
        return addSchemaLocation;
    }

    /**
     * @param addSchemaLocation the addSchemaLocation to set
     */
    public void setAddSchemaLocation(boolean addSchemaLocation) {
        this.addSchemaLocation = addSchemaLocation;
    }

    /**
     * @return the indent
     */
    public int getIndent() {
        return indent;
    }

    /**
     * @param indent the indent to set
     */
    public EncodingValues setIndent(int indent) {
        if (indent >= 0) {
            this.indent = indent;
        }
        return this;
    }

    /**
     * @return the embedded
     */
    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * @param embedded the embedded to set
     */
    public EncodingValues setEmbedded(boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    /**
     * @return the encoder
     */
    public Encoder<?, ?> getEncoder() {
        return encoder;
    }

    /**
     * @param encoder the encoder to set
     */
    public void setEncoder(Encoder<?, ?> encoder) {
        this.encoder = encoder;
    }
    
    public boolean isSetEncoder() {
        return getEncoder() != null;
    }

}

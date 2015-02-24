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
package org.n52.sos.ogc.gml;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;

/**
 * Abstract class for encoding the feature of interest. Necessary because
 * different feature types should be supported. The SOS database or another
 * feature source (e.g. WFS) should provide information about the application
 * schema.
 * 
 * @since 4.0.0
 */
public abstract class AbstractFeature implements Serializable {

    /**
     * serial number
     */
    private static final long serialVersionUID = -6117378246552782214L;

    /** Feature identifier */
    private CodeWithAuthority identifier;

    /**
     * List of feature names
     */
    private List<CodeType> names = new LinkedList<CodeType>();

    /**
     * Feature description
     */
    private String description;

    /**
     * GML id
     */
    private String gmlId;

    /**
     * constructor
     */
    public AbstractFeature() {
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            Feature identifier
     */
    public AbstractFeature(CodeWithAuthority featureIdentifier) {
        this.identifier = featureIdentifier;
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
        this.identifier = featureIdentifier;
        this.gmlId = gmlId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractFeature) {
            AbstractFeature feature = (AbstractFeature) o;
            if (feature.isSetIdentifier() && this.isSetIdentifier() && feature.isSetGmlID() && this.isSetGmlID()) {
                return feature.getIdentifier().equals(this.getIdentifier())
                        && feature.getGmlId().equals(this.getGmlId());
            } else if (feature.isSetIdentifier() && this.isSetIdentifier()) {
                return feature.getIdentifier().equals(this.getIdentifier());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier(), getGmlId());
    }

    /**
     * Get identifier
     * 
     * @return Returns the identifier.
     */
    public CodeWithAuthority getIdentifier() {
        return identifier;
    }

    /**
     * Set observation identifier
     * 
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(CodeWithAuthority identifier) {
        this.identifier = identifier;
    }

    /**
     * Set observation identifier
     * 
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(String identifier) {
        setIdentifier(new CodeWithAuthority(identifier));
    }

    /**
     * @return <tt>true</tt>, if identifier is set and value is not an empty
     *         string,<br>
     *         else <tt>false</tt>
     */
    public boolean isSetIdentifier() {
        return identifier != null && identifier.isSetValue();
    }

    /**
     * Get feature names
     * 
     * @return Feature names
     */
    public List<CodeType> getName() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Add feature names
     * 
     * @param name
     *            Feature names to ad
     */
    public void setName(final List<CodeType> name) {
        this.names.addAll(name);
    }

    /**
     * @param name
     */
    public void addName(final CodeType name) {
        this.names.add(name);
    }

    /**
     * Add a feature name
     * 
     * @param name
     *            Feature name to add
     */
    public void addName(final String name) {
        addName(new CodeType(name));
    }

    /**
     * Check whether feature has names
     * 
     * @return <code>true</code> if feature has names
     */
    public boolean isSetNames() {
        return CollectionHelper.isNotEmpty(names);
    }

    /**
     * Get first feature name or null if feature has no names
     * 
     * @return First feature name or null if feature has no names
     */
    public CodeType getFirstName() {
        if (isSetNames()) {
            return names.get(0);
        }
        return null;
    }

    /**
     * Get feature description
     * 
     * @return Feature description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set feature description
     * 
     * @param description
     *            Feature description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }
    
    /**
     * Check whether feature has a description
     * 
     * @return <code>true</code> if feature a description
     */
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }

    /**
     * Get GML id
     * 
     * @return GML id
     */
    public String getGmlId() {
        return gmlId == null ? null : gmlId.replaceFirst(Constants.NUMBER_SIGN_STRING, Constants.EMPTY_STRING);
    }

    /**
     * Set GML id
     * 
     * @param gmlId
     *            GML id to set
     */
    public void setGmlId(String gmlId) {
        this.gmlId = gmlId;
    }

    /**
     * Check whether GML id is set
     * 
     * @return <code>true</code> if GML id is set
     */
    public boolean isSetGmlID() {
        return StringHelper.isNotEmpty(getGmlId());
    }

    /**
     * Check whether feature is still contained in XML document by sign
     * {@link Constants#NUMBER_SIGN_STRING}.
     * 
     * @return <code>true</code> if feature is still contained in XML document
     */
    public boolean isReferenced() {
        return isSetGmlID() && gmlId.startsWith(Constants.NUMBER_SIGN_STRING);
    }

}

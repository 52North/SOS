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

public abstract class AbstractGML implements Serializable{

    private static final long serialVersionUID = 1923602315280257718L;

    /** Feature identifier */
    private CodeWithAuthority identifier;
    
    private CodeWithAuthority humanReadableIdentifier;
    
    private CodeWithAuthority originalIdentifier;

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
    public AbstractGML() {
    }

    /**
     * constructor
     */
    public AbstractGML(String identifier) {
        setIdentifier(new CodeWithAuthority(identifier));
    }

    /**
     * constructor
     *
     * @param identifier
     *            identifier
     */
    public AbstractGML(CodeWithAuthority identifier) {
        setIdentifier(identifier);
    }

    /**
     * constructor
     *
     * @param identifier
     *            identifier
     * @param gmlId
     *            GML id
     */
    public AbstractGML(CodeWithAuthority identifier, String gmlId) {
        setIdentifier(identifier);
        setGmlId(gmlId);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractGML) {
            AbstractGML feature = (AbstractGML) o;
            if (feature.isSetIdentifier() && this.isSetIdentifier() && feature.isSetGmlID() && this.isSetGmlID()) {
                return feature.getIdentifierCodeWithAuthority().equals(this.getIdentifierCodeWithAuthority())
                        && feature.getGmlId().equals(this.getGmlId());
            } else if (feature.isSetIdentifier() && this.isSetIdentifier()) {
                return feature.getIdentifierCodeWithAuthority().equals(this.getIdentifierCodeWithAuthority());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifierCodeWithAuthority(), getGmlId());
    }

    /**
     * Get the string identifier of this abstract feature
     *
     * @return Identifier of this abstract feature
     */
    public String getIdentifier() {
        if (isSetIdentifier()) {
            return getIdentifierCodeWithAuthority().getValue();
        }
        return null;
    }

    /**
     * Get {@link CodeWithAuthority} identifier of this abstract feature
     *
     * @return Returns the identifier of this abstract feature .
     */
    public CodeWithAuthority getIdentifierCodeWithAuthority() {
        return identifier;
    }

    /**
     * Set identifier
     *
     * @param identifier
     *            the identifier to set
     * @return this
     */
    public AbstractGML setIdentifier(CodeWithAuthority identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * Set identifier
     *
     * @param identifier
     *            the identifier to set
     * @return this
     */
    public AbstractGML setIdentifier(String identifier) {
        setIdentifier(new CodeWithAuthority(identifier));
        return this;
    }

    /**
     * @return <tt>true</tt>, if identifier is set and value is not an empty
     *         string,<br>
     *         else <tt>false</tt>
     */
    public boolean isSetIdentifier() {
        return getIdentifierCodeWithAuthority() != null && getIdentifierCodeWithAuthority().isSetValue();
    }
    
    
    /**
     * Get the string human readable identifier of this abstract feature
     *
     * @return Human readable identifier of this abstract feature
     */
    public String getHumanReadableIdentifier() {
        return getHumanReadableIdentifierCodeWithAuthority().getValue();
    }

    /**
     * Get {@link CodeWithAuthority} human readable identifier of this abstract feature
     *
     * @return Returns the human readable identifier of this abstract feature .
     */
    public CodeWithAuthority getHumanReadableIdentifierCodeWithAuthority() {
        return humanReadableIdentifier;
    }

    /**
     * Set human readable  identifier
     *
     * @param identifier
     *            the human readable identifier to set
     * @return this
     */
    public AbstractGML setHumanReadableIdentifier(CodeWithAuthority humanReadableIdentifier) {
        this.humanReadableIdentifier = humanReadableIdentifier;
        return this;
    }

    /**
     * Set human readable identifier
     *
     * @param identifier
     *            the human readable identifier to set
     * @return this
     */
    public AbstractGML setHumanReadableIdentifier(String humanReadableIdentifier) {
        setHumanReadableIdentifier(new CodeWithAuthority(humanReadableIdentifier));
        return this;
    }

    /**
     * @return <tt>true</tt>, if human readable identifier is set and value is not an empty
     *         string,<br>
     *         else <tt>false</tt>
     */
    public boolean isSetHumanReadableIdentifier() {
        return getHumanReadableIdentifierCodeWithAuthority() != null && getHumanReadableIdentifierCodeWithAuthority().isSetValue();
    }
    
    public AbstractGML setHumanReadableIdentifierAsIdentifier() {
    	if (isSetHumanReadableIdentifier()) {
    		originalIdentifier = getIdentifierCodeWithAuthority();
    		setIdentifier(getHumanReadableIdentifierCodeWithAuthority());
    	}
    	return this;
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
     * @return this
     */
    public AbstractGML setName(final List<CodeType> name) {
        this.names.clear();
        this.names = name;
        return this;
    }

    public AbstractGML setName(final CodeType name) {
        this.names.clear();
        this.names.add(name);
        return this;
    }

    /**
     * @param name
     * @return this
     */
    public AbstractGML addName(final CodeType name) {
        if (name != null && name.isSetValue()) {
            this.names.add(name);
        }
        return this;
    }

    /**
     * Add a feature name
     *
     * @param name
     *            Feature name to add
     * @return this
     */
    public AbstractGML addName(final String name) {
        addName(new CodeType(name));
        return this;
    }

    /**
     * Add a feature name
     *
     * @param name
     *            Feature name to add
      * @param codespace
     *            Codespace of the feature name
     * @return this
     */
    public AbstractGML addName(final String name, final String codespace) {
        addName(new CodeType(name, codespace));
        return this;
    }

    /**
     * Check whether feature has a names
     *
     * @return <code>true</code> if feature has names
     */
    public boolean isSetName() {
        return CollectionHelper.isNotEmpty(names);
    }

    /**
     * Get first feature name or null if feature has no names
     *
     * @return First feature name or null if feature has no names
     */
    public CodeType getFirstName() {
        if (isSetName()) {
            return getName().iterator().next();
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
     * @return this
     */
    public AbstractGML setDescription(final String description) {
        this.description = description;
        return this;
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
    
    public void copyTo(AbstractGML copyOf) {
        copyOf.setDescription(getDescription());
        copyOf.setGmlId(getGmlId());
        copyOf.setIdentifier(getIdentifierCodeWithAuthority());
        copyOf.setName(getName());
    }
}

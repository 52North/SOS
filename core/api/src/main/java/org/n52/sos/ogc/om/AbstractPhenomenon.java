/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.om;

import java.io.Serializable;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;

import com.google.common.base.Objects;

/**
 * Abstract class for phenomena
 *
 * @since 4.0.0
 */
public abstract class AbstractPhenomenon extends AbstractFeature
    implements Comparable<AbstractPhenomenon>, Serializable {
    /**
     * serial number
     */
    private static final long serialVersionUID = 8730485367220080360L;

    /**
     * constructor
     *
     * @param identifier
     *            Phenomenon identifier
     */
    public AbstractPhenomenon(final String identifier) {
        super(new CodeWithAuthority(identifier));
    }

    /**
     * constructor
     *
     * @param identifier
     *            Phenomenon identifier
     * @param description
     *            Phenomenon description
     */
    public AbstractPhenomenon(final String identifier, final String description) {
        super(new CodeWithAuthority(identifier));
        setDescription(description);
    }

    @Override
    public boolean equals(final Object paramObject) {
        if (paramObject instanceof AbstractPhenomenon) {
            final AbstractPhenomenon phen = (AbstractPhenomenon) paramObject;
            return getIdentifierCodeWithAuthority().equals(phen.getIdentifierCodeWithAuthority());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifierCodeWithAuthority());
    }

    @Override
    public int compareTo(final AbstractPhenomenon o) {
        return getIdentifierCodeWithAuthority().compareTo(o
                .getIdentifierCodeWithAuthority());
    }

    public abstract boolean isComposite();

    public abstract boolean isObservableProperty();

    public OmObservableProperty asObservableProperty() {
        return (OmObservableProperty) this;
    }

    public OmCompositePhenomenon asCompositePhenomenon() {
        return (OmCompositePhenomenon) this;
    }


	@Override
	public String toString() {
		return Objects.toStringHelper(this)
                .omitNullValues()
                .add("identifier", getIdentifier())
                .add("description", getDescription())
                .toString();
	}
}

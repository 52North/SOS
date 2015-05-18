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
package org.n52.iceland.ogc.om;

import java.io.Serializable;

import org.n52.iceland.ogc.gml.AbstractFeature;
import org.n52.iceland.ogc.gml.CodeWithAuthority;

import com.google.common.base.Objects;

/**
 * Abstract class for phenomena
 * 
 * @since 4.0.0
 */
public class AbstractPhenomenon extends AbstractFeature implements Comparable<AbstractPhenomenon>, Serializable {
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
        return getIdentifierCodeWithAuthority().compareTo(o.getIdentifierCodeWithAuthority());
    }
    
	@Override
	public String toString()
	{
		return String.format("AbstractPhenomenon [identifier=%s, description=%s]", getIdentifierCodeWithAuthority().getValue(), getDescription());
	}    
}

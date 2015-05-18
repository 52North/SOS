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
package org.n52.iceland.ogc.sos;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class for internal Envelope representation TODO should this class offer
 * merging capabilities like SosEnvelope.expandTo(SosEnvelope) considering
 * coordinate transformations?
 * 
 * @since 4.0.0
 */
public class SosEnvelope implements Serializable {
    private static final long serialVersionUID = 6525679408878064331L;

    /**
     * JTS envelope object
     */
    private Envelope envelope;

    /**
     * SRID
     */
    private int srid = -1;

    public SosEnvelope() {
    }

    /**
     * constructor
     * 
     * @param envelope
     *            JTS envelope
     * @param srid
     *            SRID
     */
    public SosEnvelope(final Envelope envelope, final int srid) {
        setEnvelope(envelope);
        setSrid(srid);
    }

    /**
     * Get envelope
     * 
     * @return the envelope
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * Expand this envelope to include the given envelope.
     *
     * @param e the envelope (may be <code>null</code>)
     */
    public void expandToInclude(final Envelope e) {
        if (e != null) {
            if (isSetEnvelope()) {
                getEnvelope().expandToInclude(e);
            } else {
                setEnvelope(e);
            }
        }
    }

    /**
     * Expand this envelope to include the given envelope.
     *
     * @param e the envelope (may be <code>null</code>)
     */
    public void expandToInclude(final SosEnvelope e) {
        if (e != null && e.isSetEnvelope()) {
            expandToInclude(e.getEnvelope());
        }
    }

    /**
     * Set envelope
     * 
     * @param envelope
     *            the envelope to set
     */
    public SosEnvelope setEnvelope(final Envelope envelope) {
        this.envelope = envelope;
        return this;
    }

    /**
     * Get SRID
     * 
     * @return the srid
     */
    public int getSrid() {
        return srid;
    }

    public boolean isSetSrid() {
        return getSrid() > 0;
    }

    /**
     * Set SRID
     * 
     * @param srid
     *            the srid to set
     */
    public SosEnvelope setSrid(final int srid) {
        this.srid = srid;
        return this;
    }

    public boolean isSetEnvelope() {
        return getEnvelope() != null && !getEnvelope().isNull();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getEnvelope() == null) ? 0 : getEnvelope().hashCode());
		result = prime * result + getSrid();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SosEnvelope)) {
			return false;
		}
		final SosEnvelope other = (SosEnvelope) obj;
		if (getEnvelope() == null) {
			if (other.getEnvelope() != null) {
				return false;
			}
		} else if (!getEnvelope().equals(other.getEnvelope())) {
			return false;
		}
		if (getSrid() != other.getSrid()) {
			return false;
		}
		return true;
	}

    @Override
    public String toString() {
        return String.format("SosEnvelope[envelope=%s, srid=%s]", getEnvelope(), getSrid());
    }

    /**
     * Static method to check if an SosEnvelope is not null and is not empty
     * 
     * @param envelope
     *            SosEnvelope to check
     * @return <code>true</code>, if SosEnvelope is not null and not empty.
     */
    public static boolean isNotNullOrEmpty(final SosEnvelope envelope) {
        return envelope != null && envelope.isSetEnvelope();
    }

}

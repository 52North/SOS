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
package org.n52.sos.ogc.sos;

import java.io.Serializable;

import org.n52.sos.util.GeometryHandler;

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

    /**
     * Switches the coordinates of this Envelope if needed.
     *
     * @param srid SRID to check axis order for
     * @return this
     *
     * @see GeometryHandler#isNorthingFirstEpsgCode(int)
     */
    @Deprecated
    public SosEnvelope switchCoordinatesIfNeeded() {
        if (isSetEnvelope() && getGeometryHandler().isNorthingFirstEpsgCode(getSrid())) {
            this.envelope = new Envelope(getEnvelope().getMinY(),
                    getEnvelope().getMaxY(),
                    getEnvelope().getMinX(),
                    getEnvelope().getMaxX());
                }
        return this;
    }

	protected GeometryHandler getGeometryHandler() {
		return GeometryHandler.getInstance();
	}

}

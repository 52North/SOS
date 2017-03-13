/**
 * ﻿Copyright (C) 2017
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.ogc.waterml;

/**
 * This class implements the OGC WaterML 2.0 element <code>metadata</code>. See <code>/req/uml-timeseries-observation/metadata</code>.
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @see http://www.opengeospatial.org/standards/waterml
 * @since 4.4.0
 */
public class MeasurementTimeseriesMetadata extends TimeseriesMetadata {

    private boolean cumulative;

    /**
     * "A series that is defined as cumulative is one where the values indicate a sequentially increasing series; 
     * i.e. each value is added to the last so the value represents the total of a value since accumulation began."
     * (Source: OGC#10-126r3)
     */
    public boolean isCumulative() {
        return cumulative;
    }

    public MeasurementTimeseriesMetadata setCumulative(boolean cumulative) {
        this.cumulative = cumulative;
        return this;
    }

}

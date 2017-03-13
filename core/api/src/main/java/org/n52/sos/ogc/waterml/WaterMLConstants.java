/**
 * ﻿Copyright (C) 2013
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
 * This interface holds all constants required by the OGC WaterML 2.0
 * model objects.
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 * @see https://portal.opengeospatial.org/files/?artifact_id=48531
 */
public interface WaterMLConstants {

    /**
     * Hold allowed values for element <code>interpolationType</code>.
     * 
     * See <code>/req/xsd-measurement-timeseries-tvp/interpolation-type</code>.
     * 
     * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
     * @since 4.4.0
     */
    public enum InterpolationType {
        
        /**
         * Continuous/Instantaneous
         * http://www.opengis.net/def/waterml/2.0/interpolationType/Continuous
         */
        Continuous,
        /**
         * http://www.opengis.net/def/waterml/2.0/interpolationType/Discontinuous
         */
        Discontinuous,
        /**
         * Instantaneous total
         * http://www.opengis.net/def/waterml/2.0/interpolationType/InstantTotal
         */
        InstantTotal,
        /**
         * Average in preceding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/AveragePrec
         */
        AveragePrec,
        /**
         * Maximum in preceding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/MaxPrec
         */
        MaxPrec,
        /**
         * Minimum in preceding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/MinPrec
         */
        MinPrec,
        /**
         * Preceding total
         * http://www.opengis.net/def/waterml/2.0/interpolationType/TotalPrec
         */
        TotalPrec,
        /**
         * Average in succeeding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/AverageSucc
         */
        AverageSucc,
        /**
         * Succeeding total
         * http://www.opengis.net/def/waterml/2.0/interpolationType/TotalSucc
         */
        TotalSucc,
        /**
         * Minimum in succeeding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/MinSucc
         */
        MinSucc,
        /**
         * Maximum in succeeding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/MaxSucc
         */
        MaxSucc,
        /**
         * Constant in preceding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/ConstPrec
         */
        ConstPrec,
        /**
         * Constant in succeeding interval
         * http://www.opengis.net/def/waterml/2.0/interpolationType/ConstSucc
         */
        ConstSucc,
        /**
         * Statistical
         * http://www.opengis.net/def/waterml/2.0/interpolationType/Statistical
         */
        Statistical
    }

}

/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.exception.ows.concrete.InvalidSridException;

import com.vividsolutions.jts.geom.Geometry;

public interface FeatureWith {
    
    
    interface FeatureWithGeometry {
        /**
         * Get feature geometry
         * 
         * @return Feature geometry
         */
        Geometry getGeometry();
    
        /**
         * Set feature geometry, checks whether srid is valid
         * 
         * @param geometry
         *            Geometry to set
         * @throws InvalidSridException
         *             If srid is invalid
         */
        void setGeometry(final Geometry geometry) throws InvalidSridException;
    
        /**
         * Check whether geometry is set
         * 
         * @return <code>true</code>, if geometry is set
         */
         boolean isSetGeometry();
    }
    
    interface FeatureWithFeatureType {
        /**
         * Get feature type
         * 
         * @return Type of this feature
         */
        String getFeatureType();

        /**
         * Set feature type
         * 
         * @param featureType
         *            Type of this feature
         */
        void setFeatureType(final String featureType);

        /**
         * Check whether feature type is set
         * 
         * @return <code>true</code>, if feature type is set
         */
        boolean isSetFeatureType();
    }
    
    interface FeatureWithUrl {
        /**
         * Get URL
         * 
         * @return URL
         */
        String getUrl();

        /**
         * Set URL
         * 
         * @param url
         *            URL to set
         */
        void setUrl(final String url);

        /**
         * Check whether URL is set
         * 
         * @return <code>true</code>, if URL is set
         */
        boolean isSetUrl();
    }
    
    interface FeatureWithXmlDescription {
        /**
         * Get XML representation of this feature
         * 
         * @return XML representation of this feature
         */
        String getXmlDescription();

        /**
         * Check whether XML representation of this feature is set
         * 
         * @return <code>true</code>, if XML representation of this feature is set
         */
        boolean isSetXmlDescription();

        /**
         * Set XML representation of this feature
         * 
         * @param xmlDescription
         *            XML representation of this feature to set
         */
        void setXmlDescription(final String xmlDescription);
    }
    
    interface FeatureWithEncode {
        
        /**
         * Check whether parameters are set
         * 
         * @return <code>true</code>, if parameters are set
         */
        boolean isSetParameter();

        /**
         * Check whether feature should be encoded
         * 
         * @return <code>true</code>, if feature should be encoded
         */
        boolean isEncode();
    }
}

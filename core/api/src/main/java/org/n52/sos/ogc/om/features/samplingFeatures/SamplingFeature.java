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
package org.n52.sos.ogc.om.features.samplingFeatures;

import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Abstract super class for all sampling features
 * 
 * @since 4.0.0
 * 
 */
public class SamplingFeature extends AbstractSamplingFeature {

    /**
     * serial number
     */
    private static final long serialVersionUID = 4660755526492323288L;

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            identifier of sampling feature
     */
    public SamplingFeature(final CodeWithAuthority featureIdentifier) {
        this(featureIdentifier, null);
       
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            identifier of sampling feature
     * @param gmlId
     *            GML of this feature
     */
    public SamplingFeature(final CodeWithAuthority featureIdentifier, final String gmlId) {
        super(featureIdentifier, gmlId);
        setDefaultElementEncoding(SfConstants.NS_SAMS);
    }

    @Override
    public <X> X accept(FeatureOfInterestVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String
                .format("SamplingFeature [name=%s, description=%s, xmlDescription=%s, geometry=%s, featureType=%s, url=%s, sampledFeatures=%s, parameters=%s, encode=%b, relatedSamplingFeatures=%s]",
                        getName(), getDescription(), getXmlDescription(), getGeometry(), getFeatureType(), getUrl(),
                        getSampledFeatures(), getParameters(), isEncode(), getRelatedSamplingFeatures());
    }

}

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
package org.n52.sos.encode.sos.v2;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestResponseEncoder extends AbstractSosResponseEncoder<GetFeatureOfInterestResponse> {
    public GetFeatureOfInterestResponseEncoder() {
        super(SosConstants.Operations.GetFeatureOfInterest.name(), GetFeatureOfInterestResponse.class);
    }

    @Override
    protected XmlObject create(GetFeatureOfInterestResponse response) throws OwsExceptionReport {
        GetFeatureOfInterestResponseDocument document =
                GetFeatureOfInterestResponseDocument.Factory.newInstance(getXmlOptions());
        GetFeatureOfInterestResponseType xbGetFoiResponse = document.addNewGetFeatureOfInterestResponse();
        AbstractFeature feature = response.getAbstractFeature();
        if (feature instanceof FeatureCollection) {
            for (AbstractFeature f : (FeatureCollection) feature) {
                addFeatureOfInterest(f, xbGetFoiResponse);
            }
        } else if (feature instanceof SamplingFeature) {
            addFeatureOfInterest(feature, xbGetFoiResponse);
        }
        XmlHelper.makeGmlIdsUnique(document.getDomNode());
        return document;
    }

    private void addFeatureOfInterest(AbstractFeature feature, GetFeatureOfInterestResponseType response)
            throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = getActiveProfile();
        if (activeProfile.isSetEncodeFeatureOfInterestNamespace()) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                    activeProfile.getEncodingNamespaceForFeatureOfInterest());
        }
        XmlObject encodeObjectToXml = encodeGml(additionalValues, feature);
        response.addNewFeatureMember().set(encodeObjectToXml);
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_FEATURE_OF_INTEREST_SCHEMA_LOCATION);
    }
}

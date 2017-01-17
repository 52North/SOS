/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureDocument;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.sos.FeatureType;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.ConformanceClass;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @since 4.0.0
 *
 */
public class SamplingEncoderv20 extends AbstractXmlEncoder<XmlObject, AbstractFeature>
        implements ConformanceClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingEncoderv20.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(CodingHelper
            .encoderKeysForElements(SfConstants.NS_SAMS, AbstractFeature.class),
                                                                               CodingHelper
                                                                               .encoderKeysForElements(SfConstants.NS_SF, AbstractFeature.class));

    private static final Set<String> CONFORMANCE_CLASSES = Sets
            .newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING, ConformanceClasses.OM_V2_SAMPLING_POINT,
                        ConformanceClasses.OM_V2_SAMPLING_CURVE, ConformanceClasses.OM_V2_SAMPLING_SURFACE);

    private static final Set<SupportedType> SUPPORTED_TYPES = ImmutableSet.<SupportedType>builder()
            .add(new FeatureType(OGCConstants.UNKNOWN))
            .add(new FeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT))
            .add(new FeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE))
            .add(new FeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE)).build();

    public SamplingEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                     Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SfConstants.NS_SAMS, SfConstants.NS_SAMS_PREFIX);
        nameSpacePrefixMap.put(SfConstants.NS_SF, SfConstants.NS_SF_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SfConstants.SF_SCHEMA_LOCATION, SfConstants.SAMS_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final AbstractFeature abstractFeature, final EncodingContext ctx)
            throws EncodingException {
        final XmlObject encodedObject = createFeature(abstractFeature);
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createFeature(final AbstractFeature absFeature) throws EncodingException {
        if (absFeature instanceof SamplingFeature) {
            final SamplingFeature sampFeat = (SamplingFeature) absFeature;
            final StringBuilder builder = new StringBuilder();
            builder.append("ssf_");
            builder.append(JavaHelper.generateID(absFeature.getIdentifierCodeWithAuthority().getValue()));
            absFeature.setGmlId(builder.toString());

            SFSpatialSamplingFeatureDocument xbSampFeatDoc = SFSpatialSamplingFeatureDocument.Factory.newInstance(getXmlOptions());
            if (sampFeat.isSetXml()) {
                try {
                    final XmlObject feature = XmlObject.Factory.parse(sampFeat.getXml(), getXmlOptions());
                    XmlHelper.updateGmlIDs(feature.getDomNode().getFirstChild(), absFeature.getGmlId(), null);
                    if (XmlHelper.getNamespace(feature).equals(SfConstants.NS_SAMS) &&
                             feature instanceof SFSpatialSamplingFeatureType) {
                        xbSampFeatDoc.setSFSpatialSamplingFeature((SFSpatialSamplingFeatureType) feature);
                        encodeShape(xbSampFeatDoc.getSFSpatialSamplingFeature().getShape(), sampFeat);
                        addNameDescription(xbSampFeatDoc.getSFSpatialSamplingFeature(), sampFeat);
                        return xbSampFeatDoc;
                    }
                    encodeShape(((SFSpatialSamplingFeatureDocument) feature).getSFSpatialSamplingFeature().getShape(),
                                sampFeat);
                    addNameDescription(((SFSpatialSamplingFeatureDocument) feature).getSFSpatialSamplingFeature(),
                                       sampFeat);
                    return feature;
                } catch (final XmlException xmle) {
                    throw new EncodingException("Error while encoding GetFeatureOfInterest response, invalid samplingFeature description!", xmle);
                }
            }
            final SFSpatialSamplingFeatureType xbSampFeature = xbSampFeatDoc.addNewSFSpatialSamplingFeature();
            // TODO: CHECK for all fields set gml:id
            xbSampFeature.setId(absFeature.getGmlId());

            if (sampFeat.isSetIdentifier() && SosHelper.checkFeatureOfInterestIdentifierForSosV2(
                sampFeat.getIdentifierCodeWithAuthority().getValue(), Sos2Constants.SERVICEVERSION)) {
                xbSampFeature.addNewIdentifier().set(encodeObjectToXml(GmlConstants.NS_GML_32, sampFeat.getIdentifierCodeWithAuthority()));
            }

            // set type
            if (sampFeat.isSetFeatureType() && !OGCConstants.UNKNOWN.equals(sampFeat.getFeatureType())) {
                xbSampFeature.addNewType().setHref(sampFeat.getFeatureType());
            } else if (sampFeat.isSetGeometry()) {
                addFeatureTypeForGeometry(xbSampFeature, sampFeat.getGeometry());
            }

            addNameDescription(xbSampFeature, sampFeat);

            // set sampledFeatures
            // TODO: CHECK
            if (sampFeat.isSetSampledFeatures()) {
                EncodingContext ctx = EncodingContext.of(SosHelperValues.REFERENCED);
                for (AbstractFeature sampledFeature : sampFeat.getSampledFeatures()) {
                    XmlObject encodeObjectToXml = encodeObjectToXml(GmlConstants.NS_GML_32, sampledFeature, ctx);
                    xbSampFeature.addNewSampledFeature().set(encodeObjectToXml);
                }
                // // Old version before schema was fixed. Now sampledFeatures
                // multiplicity is 1..* and not 1..1.
                // if (sampFeat.getSampledFeatures().size() == 1) {
                // final XmlObject encodeObjectToXml =
                // CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32,
                // sampFeat.getSampledFeatures()
                // .get(0));
                // xbSampFeature.addNewSampledFeature().set(encodeObjectToXml);
                // } else {
                // final FeatureCollection featureCollection = new
                // FeatureCollection();
                // featureCollection.setGmlId("sampledFeatures_" +
                // absFeature.getGmlId());
                // for (final AbstractFeature sampledFeature :
                // sampFeat.getSampledFeatures()) {
                // featureCollection.addMember(sampledFeature);
                // }
                // final XmlObject encodeObjectToXml =
                // CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32,
                // featureCollection);
                // xbSampFeature.addNewSampledFeature().set(encodeObjectToXml);
                // }

            } else {
                xbSampFeature.addNewSampledFeature().setHref(OGCConstants.UNKNOWN);
            }

            if (sampFeat.isSetParameter()) {
                addParameter(xbSampFeature, sampFeat);
            }

            // set position
            encodeShape(xbSampFeature.addNewShape(), sampFeat);
            return xbSampFeatDoc;
        }
        throw new UnsupportedEncoderInputException(this, absFeature);
    }

    private void addFeatureTypeForGeometry(SFSpatialSamplingFeatureType xbSampFeature, Geometry geometry) {
        if (geometry instanceof Point) {
            xbSampFeature.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        } else if (geometry instanceof LineString) {
            xbSampFeature.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
        } else if (geometry instanceof Polygon) {
            xbSampFeature.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE);
        }
    }

    private void encodeShape(ShapeType xbShape, SamplingFeature sampFeat) throws EncodingException {
        Encoder<XmlObject, Geometry> encoder = getEncoder(GmlConstants.NS_GML_32, sampFeat.getGeometry());
        if (encoder != null) {
            XmlObject xmlObject = encoder.encode(sampFeat.getGeometry(), EncodingContext.of(SosHelperValues.GMLID, sampFeat.getGmlId()));
            if (xbShape.isSetAbstractGeometry()) {
                xbShape.getAbstractGeometry().set(xmlObject);
            } else {
                xbShape.addNewAbstractGeometry().set(xmlObject);
            }
            XmlHelper.substituteElement(xbShape.getAbstractGeometry(), xmlObject);
        } else {
            throw new EncodingException("Error while encoding geometry for feature, needed encoder is missing!");
        }
    }

    private void addParameter(final SFSpatialSamplingFeatureType xbSampFeature, final SamplingFeature sampFeat)
            throws EncodingException {
        for (NamedValue<?> namedValuePair : sampFeat.getParameters()) {
            XmlObject encodeObjectToXml = encodeObjectToXml(OmConstants.NS_OM_2, namedValuePair);
            if (encodeObjectToXml != null) {
                xbSampFeature.addNewParameter().addNewNamedValue().set(encodeObjectToXml);
            }
        }
    }

    private void addNameDescription(SFSpatialSamplingFeatureType xbSamplingFeature, SamplingFeature samplingFeature)
            throws EncodingException {
        if (xbSamplingFeature != null) {
            if (samplingFeature.isSetName()) {
                removeExitingNames(xbSamplingFeature);
                for (org.n52.shetland.ogc.gml.CodeType codeType : samplingFeature.getName()) {
                    xbSamplingFeature.addNewName().set(encodeObjectToXml(GmlConstants.NS_GML_32, codeType));
                }
            }

            if (samplingFeature.isSetDescription()) {
                if (!xbSamplingFeature.isSetDescription()) {
                    xbSamplingFeature.addNewDescription();
                }
                xbSamplingFeature.getDescription().setStringValue(samplingFeature.getDescription());
            }
        }
    }

    private void removeExitingNames(SFSpatialSamplingFeatureType xbSamplingFeature) {
        if (CollectionHelper.isNotNullOrEmpty(xbSamplingFeature.getNameArray())) {
            for (int i = 0; i < xbSamplingFeature.getNameArray().length; i++) {
                xbSamplingFeature.removeName(i);
            }
        }
    }
}

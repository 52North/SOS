/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureDocument;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @since 4.0.0
 * 
 */
public class SamplingEncoderv20 extends AbstractXmlEncoder<AbstractFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingEncoderv20.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(
            CodingHelper.encoderKeysForElements(SfConstants.NS_SAMS, AbstractFeature.class),
            CodingHelper.encoderKeysForElements(SfConstants.NS_SF, AbstractFeature.class));

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING,
            ConformanceClasses.OM_V2_SAMPLING_POINT, ConformanceClasses.OM_V2_SAMPLING_CURVE,
            ConformanceClasses.OM_V2_SAMPLING_SURFACE);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType, (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));

    public SamplingEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
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
    public XmlObject encode(final AbstractFeature abstractFeature, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        final XmlObject encodedObject = createFeature(abstractFeature);
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createFeature(final AbstractFeature absFeature) throws OwsExceptionReport {
        if (absFeature instanceof SamplingFeature) {
            final SamplingFeature sampFeat = (SamplingFeature) absFeature;
            final StringBuilder builder = new StringBuilder();
            builder.append("ssf_");
            builder.append(JavaHelper.generateID(absFeature.getIdentifier().getValue()));
            absFeature.setGmlId(builder.toString());

            final SFSpatialSamplingFeatureDocument xbSampFeatDoc =
                    SFSpatialSamplingFeatureDocument.Factory.newInstance(XmlOptionsHelper.getInstance()
                            .getXmlOptions());
            if (sampFeat.getXmlDescription() != null) {
                try {
                    final XmlObject feature =
                            XmlObject.Factory.parse(sampFeat.getXmlDescription(), XmlOptionsHelper.getInstance()
                                    .getXmlOptions());
                    XmlHelper.updateGmlIDs(feature.getDomNode().getFirstChild(), absFeature.getGmlId(), null);
                    if (XmlHelper.getNamespace(feature).equals(SfConstants.NS_SAMS)
                            && feature instanceof SFSpatialSamplingFeatureType) {
                        xbSampFeatDoc.setSFSpatialSamplingFeature((SFSpatialSamplingFeatureType) feature);
                        encodeShape(xbSampFeatDoc.getSFSpatialSamplingFeature().getShape(), sampFeat);
                        return xbSampFeatDoc;
                    }
                    encodeShape(((SFSpatialSamplingFeatureDocument) feature).getSFSpatialSamplingFeature().getShape(),
                            sampFeat);
                    return feature;
                } catch (final XmlException xmle) {
                    throw new NoApplicableCodeException()
                            .causedBy(xmle)
                            .withMessage(
                                    "Error while encoding GetFeatureOfInterest response, invalid samplingFeature description!");
                }
            }
            final SFSpatialSamplingFeatureType xbSampFeature = xbSampFeatDoc.addNewSFSpatialSamplingFeature();
            // TODO: CHECK for all fields set gml:id
            xbSampFeature.setId(absFeature.getGmlId());

            if (sampFeat.isSetIdentifier()
                    && SosHelper.checkFeatureOfInterestIdentifierForSosV2(sampFeat.getIdentifier().getValue(),
                            Sos2Constants.SERVICEVERSION)) {
                xbSampFeature.addNewIdentifier().set(
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sampFeat.getIdentifier()));
            }

            // set type
            if (sampFeat.isSetFeatureType() && !OGCConstants.UNKNOWN.equals(sampFeat.getFeatureType())) {
                xbSampFeature.addNewType().setHref(sampFeat.getFeatureType());
            } else {
                if (sampFeat.isSetGeometry()) {
                    addFeatureTypeForGeometry(xbSampFeature, sampFeat.getGeometry());
                }
            }

            if (sampFeat.isSetNames()) {
                for (final CodeType sosName : sampFeat.getName()) {
                    xbSampFeature.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosName));
                }
            }

            // set sampledFeatures
            // TODO: CHECK
            if (sampFeat.getSampledFeatures() != null && !sampFeat.getSampledFeatures().isEmpty()) {
                if (sampFeat.getSampledFeatures().size() == 1) {
                    final XmlObject encodeObjectToXml =
                            CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sampFeat.getSampledFeatures()
                                    .get(0));
                    xbSampFeature.addNewSampledFeature().set(encodeObjectToXml);
                } else {
                    final FeatureCollection featureCollection = new FeatureCollection();
                    featureCollection.setGmlId("sampledFeatures_" + absFeature.getGmlId());
                    for (final AbstractFeature sampledFeature : sampFeat.getSampledFeatures()) {
                        featureCollection.addMember(sampledFeature);
                    }
                    final XmlObject encodeObjectToXml =
                            CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, featureCollection);
                    xbSampFeature.addNewSampledFeature().set(encodeObjectToXml);
                }

            } else {
                xbSampFeature.addNewSampledFeature().setHref(GmlConstants.NIL_UNKNOWN);
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

    private void encodeShape(final ShapeType xbShape, final SamplingFeature sampFeat) throws OwsExceptionReport {
        final Encoder<XmlObject, Geometry> encoder =
                CodingRepository.getInstance().getEncoder(
                        CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, sampFeat.getGeometry()));
        if (encoder != null) {
            final Map<HelperValues, String> gmlAdditionalValues =
                    new EnumMap<HelperValues, String>(HelperValues.class);
            gmlAdditionalValues.put(HelperValues.GMLID, sampFeat.getGmlId());
            final XmlObject xmlObject = encoder.encode(sampFeat.getGeometry(), gmlAdditionalValues);
            if (xbShape.isSetAbstractGeometry()) {
                xbShape.getAbstractGeometry().set(xmlObject);
            } else {
                xbShape.addNewAbstractGeometry().set(xmlObject);
            }
            XmlHelper.substituteElement(xbShape.getAbstractGeometry(), xmlObject);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding geometry for feature, needed encoder is missing!");
        }
    }

    private void addParameter(final SFSpatialSamplingFeatureType xbSampFeature, final SamplingFeature sampFeat)
            throws OwsExceptionReport {
        for (final NamedValue<?> namedValuePair : sampFeat.getParameters()) {
            final XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(OmConstants.NS_OM_2, namedValuePair);
            if (encodeObjectToXml != null) {
                xbSampFeature.addNewParameter().addNewNamedValue().set(encodeObjectToXml);
            }
        }
    }
}

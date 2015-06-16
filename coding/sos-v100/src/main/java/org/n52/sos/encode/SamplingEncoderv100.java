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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.opengis.gml.FeaturePropertyType;
import net.opengis.sampling.x10.SamplingCurveDocument;
import net.opengis.sampling.x10.SamplingCurveType;
import net.opengis.sampling.x10.SamplingFeatureCollectionDocument;
import net.opengis.sampling.x10.SamplingFeatureCollectionType;
import net.opengis.sampling.x10.SamplingFeaturePropertyType;
import net.opengis.sampling.x10.SamplingFeatureType;
import net.opengis.sampling.x10.SamplingPointDocument;
import net.opengis.sampling.x10.SamplingPointType;
import net.opengis.sampling.x10.SamplingSurfaceDocument;
import net.opengis.sampling.x10.SamplingSurfaceType;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SamplingEncoderv100 extends AbstractXmlEncoder<AbstractFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingEncoderv100.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(CodingHelper.encoderKeysForElements(
            SfConstants.NS_SA, AbstractFeature.class));

    // TODO here also the question, sa:samplingPoint sampling/1.0 vs 2.0 mapping
    // or not and where and how to handle
    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType, (Set<String>) ImmutableSet.of(OGCConstants.UNKNOWN,
                    SfConstants.EN_SAMPLINGPOINT, SfConstants.EN_SAMPLINGSURFACE, SfConstants.EN_SAMPLINGCURVE));

    private static final Set<String> CONFORMANCE_CLASSES = ImmutableSet.of(ConformanceClasses.OM_V2_SPATIAL_SAMPLING,
            ConformanceClasses.OM_V2_SAMPLING_POINT, ConformanceClasses.OM_V2_SAMPLING_CURVE,
            ConformanceClasses.OM_V2_SAMPLING_SURFACE);

    public SamplingEncoderv100() {
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
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SfConstants.NS_SA, SfConstants.NS_SA_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SfConstants.SA_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(AbstractFeature abstractFeature, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = createFeature(abstractFeature);
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createFeature(AbstractFeature absFeature) throws OwsExceptionReport {
        if (absFeature instanceof SamplingFeature) {
            SamplingFeature sampFeat = (SamplingFeature) absFeature;
            if (sampFeat.getFeatureType().equals(SfConstants.FT_SAMPLINGPOINT)
                    || sampFeat.getFeatureType().equals(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT)
                    || sampFeat.getGeometry() instanceof Point) {
                SamplingPointDocument xbSamplingPointDoc =
                        SamplingPointDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                SamplingPointType xbSamplingPoint = xbSamplingPointDoc.addNewSamplingPoint();
                addValuesToFeature(xbSamplingPoint, sampFeat);
                XmlObject xbGeomety = getEncodedGeometry(sampFeat.getGeometry(), absFeature.getGmlId());
                xbSamplingPoint.addNewPosition().addNewPoint().set(xbGeomety);
                return xbSamplingPointDoc;
            } else if (sampFeat.getFeatureType().equals(SfConstants.FT_SAMPLINGCURVE)
                    || sampFeat.getFeatureType().equals(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE)
                    || sampFeat.getGeometry() instanceof LineString) {
                SamplingCurveDocument xbSamplingCurveDoc =
                        SamplingCurveDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                SamplingCurveType xbSamplingCurve = xbSamplingCurveDoc.addNewSamplingCurve();
                addValuesToFeature(xbSamplingCurve, sampFeat);
                XmlObject xbGeomety = getEncodedGeometry(sampFeat.getGeometry(), absFeature.getGmlId());
                xbSamplingCurve.addNewShape().addNewCurve().set(xbGeomety);
                return xbSamplingCurveDoc;
            } else if (sampFeat.getFeatureType().equals(SfConstants.FT_SAMPLINGSURFACE)
                    || sampFeat.getFeatureType().equals(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE)
                    || sampFeat.getGeometry() instanceof Polygon) {
                SamplingSurfaceDocument xbSamplingSurfaceDoc =
                        SamplingSurfaceDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                SamplingSurfaceType xbSamplingSurface = xbSamplingSurfaceDoc.addNewSamplingSurface();
                addValuesToFeature(xbSamplingSurface, sampFeat);
                XmlObject xbGeomety = getEncodedGeometry(sampFeat.getGeometry(), absFeature.getGmlId());
                xbSamplingSurface.addNewShape().addNewSurface().set(xbGeomety);
                return xbSamplingSurfaceDoc;
            }
        } else if (absFeature instanceof FeatureCollection) {
            createFeatureCollection((FeatureCollection) absFeature);
        }
        throw new UnsupportedEncoderInputException(this, absFeature);
    }

    private XmlObject getEncodedGeometry(Geometry geometry, String gmlId) throws UnsupportedEncoderInputException,
            OwsExceptionReport {
        Encoder<XmlObject, Geometry> encoder =
                CodingRepository.getInstance().getEncoder(CodingHelper.getEncoderKey(GmlConstants.NS_GML, geometry));
        if (encoder != null) {
            Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
            additionalValues.put(HelperValues.GMLID, gmlId);
            return encoder.encode(geometry, additionalValues);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding geometry for feature, needed encoder is missing!");
        }
    }

    private void addValuesToFeature(SamplingFeatureType xbSamplingFeature, SamplingFeature sampFeat)
            throws OwsExceptionReport {
        xbSamplingFeature.setId(sampFeat.getGmlId());
        if (sampFeat.isSetIdentifier()
                && SosHelper.checkFeatureOfInterestIdentifierForSosV2(sampFeat.getIdentifierCodeWithAuthority().getValue(),
                        Sos1Constants.SERVICEVERSION)) {
            xbSamplingFeature.addNewName().set(
                    CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, sampFeat.getIdentifierCodeWithAuthority()));
        }

        if (sampFeat.isSetName()) {
            for (CodeType sosName : sampFeat.getName()) {
                xbSamplingFeature.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, sosName));
            }
        }

        // set sampledFeatures
        // TODO: CHECK
        if (sampFeat.getSampledFeatures() != null && !sampFeat.getSampledFeatures().isEmpty()) {
            for (AbstractFeature sampledFeature : sampFeat.getSampledFeatures()) {
                FeaturePropertyType sp = xbSamplingFeature.addNewSampledFeature();
                sp.setHref(sampledFeature.getIdentifier());
                if (sampFeat.isSetName() && sampFeat.getFirstName().isSetValue()) {
                    sp.setTitle(sampFeat.getFirstName().getValue());
                }
//                xbSamplingFeature.addNewSampledFeature().set(createFeature(sampledFeature));
            }
        } else {
            xbSamplingFeature.addNewSampledFeature().setHref(GmlConstants.NIL_UNKNOWN);
        }
    }

    private XmlObject createFeatureCollection(FeatureCollection sosFeatureCollection) throws OwsExceptionReport {
        SamplingFeatureCollectionDocument xbSampFeatCollDoc =
                SamplingFeatureCollectionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        SamplingFeatureCollectionType xbSampFeatColl = xbSampFeatCollDoc.addNewSamplingFeatureCollection();
        xbSampFeatColl.setId("sfc_" + Long.toString(new DateTime().getMillis()));
        for (AbstractFeature sosAbstractFeature : sosFeatureCollection.getMembers().values()) {
            SamplingFeaturePropertyType xbFeatMember = xbSampFeatColl.addNewMember();
            xbFeatMember.set(createFeature(sosAbstractFeature));
        }
        return xbSampFeatCollDoc;
    }
}

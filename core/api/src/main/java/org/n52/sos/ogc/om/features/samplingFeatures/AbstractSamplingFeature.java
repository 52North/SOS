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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithEncode;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithFeatureType;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithGeometry;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithUrl;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithXmlDescription;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractSamplingFeature extends AbstractFeature implements FeatureWithGeometry,
        FeatureWithFeatureType, FeatureWithUrl, FeatureWithXmlDescription, FeatureWithEncode {

    /**
     *  serial number
     */
    private static final long serialVersionUID = 4327454569167724464L;

    /**
     * XML document representing this feature
     */
    private String xmlDescription;

    /**
     * Feature geometry
     */
    private Geometry geometry;

    /**
     * Type of this feature
     */
    private String featureType = OGCConstants.UNKNOWN;

    /**
     * URL to feature representation, e.g. to a WFS
     */
    private String url;

    /**
     * Sampled features, domain feature
     */
    private final List<AbstractFeature> sampledFeatures = new LinkedList<AbstractFeature>();

    /**
     * Parameters
     */
    private final List<NamedValue<?>> parameters = new LinkedList<NamedValue<?>>();

    /**
     * Should this feature be encoded in response
     */
    private boolean encode = true;

    /**
     * Related sampling features
     */
    private Collection<SamplingFeatureComplex> relatedSamplingFeatures;

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            identifier of sampling feature
     */
    public AbstractSamplingFeature(final CodeWithAuthority featureIdentifier) {
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
    public AbstractSamplingFeature(final CodeWithAuthority featureIdentifier, final String gmlId) {
        super(featureIdentifier, gmlId);
    }

    @Override
    public String getXmlDescription() {
        return xmlDescription;
    }

    @Override
    public boolean isSetXmlDescription() {
        return StringHelper.isNotEmpty(getXmlDescription());
    }

    @Override
    public void setXmlDescription(final String xmlDescription) {
        this.xmlDescription = xmlDescription;
    }
    
    @Override
    public boolean isSetGmlID() {
        return super.isSetGmlID();
    }
    
    @Override
    public String getGmlId() {
        if (!super.isSetGmlID()) {
            final StringBuilder builder = new StringBuilder();
            builder.append("ssf_");
            builder.append(JavaHelper.generateID(getIdentifierCodeWithAuthority().getValue()));
            setGmlId(builder.toString());
        }
        return super.getGmlId();
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(final Geometry geometry) throws InvalidSridException {
        if (geometry != null && geometry.getSRID() == 0) {
            throw new InvalidSridException(0);
        }
        this.geometry = geometry;
    }

    @Override
    public boolean isSetGeometry() {
        return getGeometry() != null && !getGeometry().isEmpty();
    }

    @Override
    public String getFeatureType() {
        return featureType;
    }

    @Override
    public void setFeatureType(final String featureType) {
        this.featureType = featureType;
    }

    @Override
    public boolean isSetFeatureType() {
        return StringHelper.isNotEmpty(getFeatureType());
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public boolean isSetUrl() {
        return StringHelper.isNotEmpty(getUrl());
    }

    /**
     * Set sampled features
     * 
     * @param sampledFeatures
     *            Sampled fearure list
     */
    public void setSampledFeatures(final List<AbstractFeature> sampledFeatures) {
        this.sampledFeatures.addAll(sampledFeatures);
    }

    /**
     * Get sampled feaures
     * 
     * @return Sampled feature list
     */
    public List<AbstractFeature> getSampledFeatures() {
        if (isSetSampledFeatures()) {
            return Collections.unmodifiableList(sampledFeatures);
        }
        return Collections.emptyList();
    }

    /**
     * Check whether sampled features are set
     * 
     * @return <code>true</code>, if sampled features are set
     */
    public boolean isSetSampledFeatures() {
        return CollectionHelper.isNotEmpty(sampledFeatures);
    }

    /**
     * Add parameter
     * 
     * @param namedValue
     *            Parameter ro add
     */
    public void addParameter(final NamedValue<?> namedValue) {
        parameters.add(namedValue);
    }

    /**
     * Add parameters
     * 
     * @param parameters
     *            Parameters to add
     */
    public void setParameters(final Collection<NamedValue<?>> parameters) {
        this.parameters.addAll(parameters);
    }

    /**
     * Get parameters
     * 
     * @return Parameter list
     */
    public List<NamedValue<?>> getParameters() {
        return parameters;
    }

    @Override
    public boolean isSetParameter() {
        return CollectionHelper.isNotEmpty(parameters);
    }

    @Override
    public boolean isEncode() {
        return encode;
    }

    /**
     * Set indicator if feature should be encoded
     * 
     * @param encode
     *            Encoding indicator
     */
    public void setEncode(final boolean encode) {
        this.encode = encode;
    }
    
    /**
     * Add related sampling feature
     * 
     * @param relatedSamplingFeature
     *            Related sampling feature to add
     */
    public void addRelatedSamplingFeature(final SamplingFeatureComplex relatedSamplingFeature) {
        if (!isSetRelatedSamplingFeatures()) {
            relatedSamplingFeatures = Sets.newHashSet();
        }
        if (relatedSamplingFeature != null) {
            relatedSamplingFeatures.add(relatedSamplingFeature);
        }
    }

    /**
     * Add related sampling features
     * 
     * @param relatedSamplingFeatures
     *            Related sampling features to add
     */
    public void addAllRelatedSamplingFeatures(final Collection<SamplingFeatureComplex> relatedSamplingFeatures) {
        if (isSetRelatedSamplingFeatures()) {
            this.relatedSamplingFeatures.addAll(relatedSamplingFeatures);
        } else {
            this.relatedSamplingFeatures = relatedSamplingFeatures;
        }
    }

    /**
     * Set related sampling features
     * 
     * @param relatedSamplingFeatures
     *            Related sampling features to set
     */
    public void setRelatedSamplingFeatures(final Collection<SamplingFeatureComplex> relatedSamplingFeatures) {
        this.relatedSamplingFeatures = relatedSamplingFeatures;
    }

    /**
     * Get related sampling features
     * 
     * @return Related sampling features
     */
    public List<SamplingFeatureComplex> getRelatedSamplingFeatures() {
        return relatedSamplingFeatures != null
                ? Lists.newArrayList(relatedSamplingFeatures)
                : Collections.<SamplingFeatureComplex>emptyList();
    }

    /**
     * Check whether related sampling features are set
     * 
     * @return <code>true</code>, if related sampling features are set
     */
    public boolean isSetRelatedSamplingFeatures() {
        return CollectionHelper.isNotEmpty(relatedSamplingFeatures);
    }
    
    @Override
    public String toString() {
        return String
                .format("AbstractSamplingFeature [name=%s, description=%s, xmlDescription=%s, geometry=%s, featureType=%s, url=%s, sampledFeatures=%s, parameters=%s, encode=%b, relatedSamplingFeatures=%s]",
                        getName(), getDescription(), getXmlDescription(), getGeometry(), getFeatureType(), getUrl(),
                        getSampledFeatures(), getParameters(), isEncode(), getRelatedSamplingFeatures());
    }
}

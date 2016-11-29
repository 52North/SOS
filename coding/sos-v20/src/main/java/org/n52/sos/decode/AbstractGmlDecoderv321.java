/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.decode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.opengis.gml.x32.AbstractFeatureType;
import net.opengis.gml.x32.AbstractGMLType;
import net.opengis.gml.x32.CodeType;
import net.opengis.gml.x32.CodeWithAuthorityType;
import net.opengis.gml.x32.ReferenceType;

import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.iceland.service.ConformanceClass;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.AbstractGML;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.util.CollectionHelper;
import org.n52.svalbard.xml.AbstractXmlDecoder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public abstract class AbstractGmlDecoderv321<T, S> extends AbstractXmlDecoder<T, S> implements ConformanceClass {

    protected AbstractGML parseAbstractGMLType(AbstractGMLType agmlt, AbstractGML abstractGML)
            throws DecodingException {
        parseIdentifier(agmlt, abstractGML);
        parseNames(agmlt, abstractGML);
        paresDescription(agmlt, abstractGML);
        parseMetaDataProperty(agmlt, abstractGML);
        return null;
    }

    protected AbstractFeature parseAbstractFeatureType(AbstractFeatureType aft, AbstractFeature abstractFeature)
            throws DecodingException {
        parseAbstractGMLType(aft, abstractFeature);
        parseBoundedBy(aft, abstractFeature);
        parseLocation(aft, abstractFeature);
        return abstractFeature;
    }

    protected AbstractGML parseIdentifier(AbstractGMLType agmlt, AbstractGML abstractGML) throws DecodingException {
        if (agmlt.isSetIdentifier()) {
            abstractGML.setIdentifier(parseCodeWithAuthorityTye(agmlt.getIdentifier()));
        }
        return abstractGML;
    }

    protected AbstractGML parseNames(AbstractGMLType agmlt, AbstractGML abstractGML) throws DecodingException {
        if (CollectionHelper.isNotNullOrEmpty(agmlt.getNameArray())) {
            for (CodeType ct : agmlt.getNameArray()) {
                abstractGML.addName(parseCodeType(ct));
            }
        }
        return abstractGML;
    }

    protected AbstractGML paresDescription(AbstractGMLType agmlt, AbstractGML abstractGML) {
        if (agmlt.isSetDescription()) {
            if (agmlt.getDescription().isSetHref()) {
                abstractGML.setDescription(agmlt.getDescription().getHref());
            } else {
                abstractGML.setDescription(agmlt.getDescription().getStringValue());
            }
        } else if (agmlt.isSetDescriptionReference()) {
            // TODO
        }
        return abstractGML;

    }

    protected AbstractGML parseMetaDataProperty(AbstractGMLType agmlt, AbstractGML abstractGML) {
        if (CollectionHelper.isNotNullOrEmpty(agmlt.getMetaDataPropertyArray())) {
            // TODO
        }
        return abstractGML;
    }

    protected AbstractFeature parseBoundedBy(AbstractFeatureType aft, AbstractFeature abstractFeature) {
        if (aft.isSetBoundedBy()) {
            // TODO
        }
        return abstractFeature;
    }

    protected AbstractFeature parseLocation(AbstractFeatureType aft, AbstractFeature abstractFeature) {
        if (aft.isSetLocation()) {
            // TODO
        }
        return abstractFeature;
    }

    protected CodeWithAuthority parseCodeWithAuthorityTye(CodeWithAuthorityType xbCodeWithAuthority) {
        if (xbCodeWithAuthority.getStringValue() != null && !xbCodeWithAuthority.getStringValue().isEmpty()) {
            CodeWithAuthority sosCodeWithAuthority = new CodeWithAuthority(xbCodeWithAuthority.getStringValue());
            sosCodeWithAuthority.setCodeSpace(xbCodeWithAuthority.getCodeSpace());
            return sosCodeWithAuthority;
        }
        return null;
    }

    protected org.n52.shetland.ogc.gml.CodeType parseCodeType(CodeType element) throws DecodingException {
        org.n52.shetland.ogc.gml.CodeType codeType = new org.n52.shetland.ogc.gml.CodeType(element.getStringValue());
        if (element.isSetCodeSpace()) {
            try {
                codeType.setCodeSpace(new URI(element.getCodeSpace()));
            } catch (URISyntaxException e) {
               throw new DecodingException(e, "Error while creating URI from '{}'", element.getCodeSpace());
            }
        }
        return codeType;
    }

    protected org.n52.shetland.ogc.gml.ReferenceType parseReferenceType(ReferenceType rt) {
        org.n52.shetland.ogc.gml.ReferenceType referenceType = new org.n52.shetland.ogc.gml.ReferenceType("UNKNOWN");
        if (rt.isSetTitle() && !Strings.isNullOrEmpty(rt.getTitle())) {
            referenceType.setTitle(rt.getTitle());
        }
        if (rt.isSetHref() && !Strings.isNullOrEmpty(rt.getHref())) {
            referenceType.setHref(rt.getHref());
        }
        if (rt.isSetRole() && !Strings.isNullOrEmpty(rt.getRole())) {
            referenceType.setRole(rt.getRole());
        }
        return referenceType;
    }


    protected List<org.n52.shetland.ogc.gml.ReferenceType> parseReferenceType(ReferenceType[] referenceTypes) {
        List<org.n52.shetland.ogc.gml.ReferenceType> list = Lists.newArrayList();
        if (CollectionHelper.isNotNullOrEmpty(referenceTypes)) {
            for (ReferenceType referenceType : referenceTypes) {
                list.add(parseReferenceType(referenceType));
            }
        }
        return list;
    }
}

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
package org.n52.sos.util;

import static org.n52.iceland.service.MiscSettings.CHARACTER_ENCODING;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlOptions;

import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.util.Validation;
import org.n52.janmayen.Producer;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.w3c.W3CConstants;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.SchemaAwareEncoder;

/**
 * XML utility class
 *
 * @since 4.0.0
 *
 */
@Configurable
public final class XmlOptionsHelper implements Constructable, Destroyable, Producer<XmlOptions> {
    @Deprecated
    private static XmlOptionsHelper instance;
    private EncoderRepository encoderRepository;
    private final ReentrantLock lock = new ReentrantLock();
    private XmlOptions xmlOptions;
    private String characterEncoding = "UTF-8";
    private boolean prettyPrint = true;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Override
    public void init() {
        XmlOptionsHelper.instance = this;
    }

    // TODO: To be used by other encoders to have common prefixes
    @SuppressWarnings("unchecked")
    private Map<String, String> getPrefixMap() {
        final Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put(OGCConstants.NS_OGC, OGCConstants.NS_OGC_PREFIX);
//        prefixMap.put(OmConstants.NS_OM, OmConstants.NS_OM_PREFIX);
//        prefixMap.put(SfConstants.NS_SA, SfConstants.NS_SA_PREFIX);
//        prefixMap.put(Sos1Constants.NS_SOS, SosConstants.NS_SOS_PREFIX);
        prefixMap.put(W3CConstants.NS_XLINK, W3CConstants.NS_XLINK_PREFIX);
        prefixMap.put(W3CConstants.NS_XSI, W3CConstants.NS_XSI_PREFIX);
        prefixMap.put(W3CConstants.NS_XS, W3CConstants.NS_XS_PREFIX);
        encoderRepository.getEncoders().stream()
                .filter(Functions.instanceOf(SchemaAwareEncoder.class))
                .map(Functions.cast(SchemaAwareEncoder.class))
                .forEach(e -> e.addNamespacePrefixToMap(prefixMap));
        return prefixMap;
    }

    /**
     * Get the XML options for SOS 1.0.0
     *
     * @return SOS 1.0.0 XML options
     */
    public XmlOptions getXmlOptions() {
        if (xmlOptions == null) {
            lock.lock();
            try {
                if (xmlOptions == null) {
                    xmlOptions = new XmlOptions();
                    Map<String, String> prefixes = getPrefixMap();
                    xmlOptions.setSaveSuggestedPrefixes(prefixes);
                    xmlOptions.setSaveImplicitNamespaces(prefixes);
                    xmlOptions.setSaveAggressiveNamespaces();
                    if (prettyPrint) {
                        xmlOptions.setSavePrettyPrint();
                    }
                    xmlOptions.setSaveNamespacesFirst();
                    xmlOptions.setCharacterEncoding(characterEncoding);
                }
            } finally {
                lock.unlock();
            }
        }
        return xmlOptions;
    }

    /**
     * Cleanup, set XML options to null
     */
    @Override
    public void destroy() {
        xmlOptions = null;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        lock.lock();
        try {
            if (this.prettyPrint != prettyPrint) {
                setReload();
            }
            this.prettyPrint = prettyPrint;
        } finally {
            lock.unlock();
        }
    }

    @Setting(CHARACTER_ENCODING)
    public void setCharacterEncoding(String characterEncoding) {
        lock.lock();
        try {
            Validation.notNullOrEmpty("Character Encoding", characterEncoding);
            if (!this.characterEncoding.equals(characterEncoding)) {
                setReload();
            }
            this.characterEncoding = characterEncoding;
        } finally {
            lock.unlock();
        }
    }

    private void setReload() {
        lock.lock();
        try {
            xmlOptions = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public XmlOptions get() {
        return getXmlOptions();
    }

    /**
     * Get INSTANCE from class with default character encoding UTF-8
     *
     * @return INSTANCE
     *
     * @deprecated Use injection:
     * <pre>
     * &#064;Inject
     * private Provider&lt;XmlOptioon&gt; xmloptions;
     * ...
     * XmlOptions options = this.xmlOptions.get();
     * </pre>
     */
    @Deprecated
    public static XmlOptionsHelper getInstance() {
        return instance;
    }
}

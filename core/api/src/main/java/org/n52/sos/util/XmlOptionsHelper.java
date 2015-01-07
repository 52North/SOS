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
package org.n52.sos.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.w3c.W3CConstants;

/**
 * XML utility class
 * 
 * @since 4.0.0
 * 
 */
public final class XmlOptionsHelper {
    /**
     * Get INSTANCE from class with default character encoding UTF-8
     * 
     * @return INSTANCE
     */
    public static XmlOptionsHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final ReentrantLock lock = new ReentrantLock();

    private XmlOptions xmlOptions;

    private String characterEncoding = "UTF-8";

    private boolean prettyPrint = true;

    /**
     * private constructor
     */
    private XmlOptionsHelper() {
    }

    // TODO: To be used by other encoders to have common prefixes
    private Map<String, String> getPrefixMap() {
        final Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put(OGCConstants.NS_OGC, OGCConstants.NS_OGC_PREFIX);
        prefixMap.put(OmConstants.NS_OM, OmConstants.NS_OM_PREFIX);
        prefixMap.put(SfConstants.NS_SA, SfConstants.NS_SA_PREFIX);
        prefixMap.put(Sos1Constants.NS_SOS, SosConstants.NS_SOS_PREFIX);
        prefixMap.put(W3CConstants.NS_XLINK, W3CConstants.NS_XLINK_PREFIX);
        prefixMap.put(W3CConstants.NS_XSI, W3CConstants.NS_XSI_PREFIX);
        prefixMap.put(W3CConstants.NS_XS, W3CConstants.NS_XS_PREFIX);
        for (final Encoder<?, ?> encoder : CodingRepository.getInstance().getEncoders()) {
            encoder.addNamespacePrefixToMap(prefixMap);
        }
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
                    final Map<String, String> prefixes = getPrefixMap();
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
    public void cleanup() {
        xmlOptions = null;
    }

    public void setPrettyPrint(final boolean prettyPrint) {
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

    public void setCharacterEncoding(final String characterEncoding) {
        lock.lock();
        try {
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

    private static class LazyHolder {
        private static final XmlOptionsHelper INSTANCE = new XmlOptionsHelper();
        
        private LazyHolder() {};
    }
}

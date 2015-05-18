/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.xmlbeans.XmlOptions;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.encode.Encoder;
import org.n52.iceland.ogc.OGCConstants;
import org.n52.iceland.w3c.W3CConstants;

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
//        prefixMap.put(OmConstants.NS_OM, OmConstants.NS_OM_PREFIX);
//        prefixMap.put(SfConstants.NS_SA, SfConstants.NS_SA_PREFIX);
//        prefixMap.put(Sos1Constants.NS_SOS, SosConstants.NS_SOS_PREFIX);
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

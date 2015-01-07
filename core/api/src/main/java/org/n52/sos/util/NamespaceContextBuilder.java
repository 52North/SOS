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

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class NamespaceContextBuilder {

    private final ImmutableBiMap.Builder<String, String> bimap = ImmutableBiMap
            .builder();

    public NamespaceContextBuilder add(String namespace, String prefix) {
        this.bimap.put(namespace, prefix);
        return this;
    }

    public NamespaceContextBuilder add(NamespaceContextBuilder other) {
        this.bimap.putAll(other.bimap.build());
        return this;
    }

    public NamespaceContext build() {
        return new BiMapNamespaceContext(this.bimap.build());
    }

    private class BiMapNamespaceContext implements NamespaceContext {
        private final BiMap<String, String> namespaces;

        BiMapNamespaceContext(BiMap<String, String> namespaces) {
            this.namespaces = namespaces;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return this.namespaces.inverse().get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return this.namespaces.get(namespaceURI);
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            String prefix = this.namespaces.get(namespaceURI);
            return prefix == null ? Iterators.<String>emptyIterator()
                                  : Iterators.singletonIterator(prefix);
        }
    }

}

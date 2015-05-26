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
package org.n52.sos.coding.decode;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.coding.decode.DecoderKey;
import org.n52.iceland.coding.decode.NamespaceDecoderKey;
import org.n52.iceland.util.ClassHelper;

import com.google.common.base.Objects;

/**
 * {@link NamespaceDecoderKey} implementation for XML namespace and {@link Class}.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 */
public class XmlNamespaceDecoderKey extends NamespaceDecoderKey<Class<?>> {
    
    private Class<?> type;

    public XmlNamespaceDecoderKey(String namespace, Class<?> type) {
        super(namespace, type);
    }

    @Override
    public int getSimilarity(DecoderKey key) {
        return getSimilarity(key, XmlObject.class);
    }
    
    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    protected void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    protected String getTypeName() {
        return getType().getSimpleName();
    }

    @Override
    protected int getSimilarity(DecoderKey key, Class<?> type) {
        if (key != null && key.getClass() == getClass()) {
            NamespaceDecoderKey<?> xmlKey = (NamespaceDecoderKey<?>) key;
            if (Objects.equal(getNamespace(), xmlKey.getNamespace()) && xmlKey.getType() instanceof Class<?>) {
                return ClassHelper.getSimiliarity(getType() != null ? getType() : type,
                        xmlKey.getType() != null ? (Class<?>)xmlKey.getType() : type);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}

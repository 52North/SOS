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
package org.n52.sos.decode.xml.stream;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.iso.GcoConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Nillable;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class NillableReader<T> extends XmlReader<Nillable<T>> {
    private Nillable<T> nillable;

    protected abstract XmlReader<T> getDelegate();

    protected List<QName> getPossibleNilReasonAttributes() {
        return Arrays.asList(AqdConstants.QN_NIL_REASON,
                             GcoConstants.QN_GCO_NIL_REASON);
    }

    @Override
    protected void begin()
            throws XMLStreamException, OwsExceptionReport {
        Optional<String> attr = attr(W3CConstants.QN_XSI_NIL);
        if (attr.isPresent() && attr.get().equals("true")) {
            List<QName> attributeNames = getPossibleNilReasonAttributes();
            Iterable<Optional<String>> attributes = attr(attributeNames);
            Iterable<String> reasons = Optional.presentInstances(attributes);
            this.nillable = Nillable.nil(Iterables.getFirst(reasons, null));
        } else {
            this.nillable = Nillable.of(delegate(getDelegate()));
        }
    }

    @Override
    protected Nillable<T> finish() {
        return nillable;
    }

}

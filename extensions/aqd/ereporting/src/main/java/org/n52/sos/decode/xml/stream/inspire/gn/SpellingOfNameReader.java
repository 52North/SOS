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
package org.n52.sos.decode.xml.stream.inspire.gn;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.decode.xml.stream.NillableStringReader;
import org.n52.sos.decode.xml.stream.XmlReader;
import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SpellingOfNameReader extends XmlReader<Spelling> {
    private Spelling spelling;

    @Override
    protected void begin() {
        this.spelling = new Spelling();
    }

    @Override
    protected void read(QName name)
            throws XMLStreamException, OwsExceptionReport {
        if (name.equals(AqdConstants.QN_GN_TEXT)) {
            this.spelling.setText(chars());
        } else if (name.equals(AqdConstants.QN_GN_SCRIPT)) {
            this.spelling.setScript(delegate(new NillableStringReader()));
        } else if (name.equals(AqdConstants.QN_GN_TRANSLITERATION_SCHEME)) {
            this.spelling.setTransliterationScheme(delegate(new NillableStringReader()));
        } else {
            ignore();
        }
    }

    @Override
    protected Spelling finish() {
        return this.spelling;
    }

}

/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.decode.xml.stream.NillableStringReader;
import org.n52.sos.decode.xml.stream.XmlReader;
import org.n52.sos.inspire.aqd.Pronunciation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Function;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class PronunciationOfNameReader extends XmlReader<Pronunciation> {
    private static final Function<String, URI> STRING_TO_URI
            = new Function<String, URI>() {
                @Override
                public URI apply(String input) {
                    return URI.create(input);
                }
            };
    private Pronunciation pronunciation;

    @Override
    protected void begin() {
        this.pronunciation = new Pronunciation();
    }

    @Override
    protected void read(QName name)
            throws XMLStreamException, OwsExceptionReport {
        if (name.equals(AqdConstants.QN_GN_PRONUNCIATION_SOUND_LINK)) {
            this.pronunciation.setSoundLink(delegate(new NillableStringReader()).transform(STRING_TO_URI));
        } else if (name.equals(AqdConstants.QN_GN_PRONUNCIATION_IPA)) {
            this.pronunciation.setIPA(delegate(new NillableStringReader()));
        } else {
            ignore();
        }
    }

    @Override
    protected Pronunciation finish() {
        return this.pronunciation;
    }

}

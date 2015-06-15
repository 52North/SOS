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
package org.n52.sos.encode.streaming;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.encode.EncodingValues;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Implementation of {@link AbstractOmV20XmlStreamWriter} to write WaterML 2.0
 * Domain Range encoded {@link OmObservation}s to stream
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class WmlTDREncoderv20XmlStreamWriter extends AbstractOmV20XmlStreamWriter {

    /**
     * constructor
     */
    public WmlTDREncoderv20XmlStreamWriter() {
        super();
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to write to stream
     */
    public WmlTDREncoderv20XmlStreamWriter(OmObservation observation) {
        super(observation);
    }

    @Override
    protected void writeResult(OmObservation observation, EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        // TODO Auto-generated method stub
        super.writeResult(observation, encodingValues);
    }

}

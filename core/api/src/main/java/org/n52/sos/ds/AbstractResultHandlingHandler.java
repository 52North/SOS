/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract class for Result Handling operation Handlers to provide common
 * methods
 *
 * Renamed, in version 4.x called AbstractResultHandlingDAO
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractResultHandlingHandler extends AbstractSosOperationHandler {

    private DecoderRepository decodingRepository;

    public AbstractResultHandlingHandler(String service, String operationName) {
        super(service, operationName);
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decodingRepository) {
        this.decodingRepository = decodingRepository;
    }

    /**
     * Get internal SweDataRecord from internal ResultStructure
     *
     * @param resultStructure
     *            Internal ResultStructure
     * @return internal SweDataRecord
     * @throws OwsExceptionReport
     *             If the ResultStructure is not supported
     */
    protected SweDataRecord setRecordFrom(final SweAbstractDataComponent resultStructure) throws OwsExceptionReport {
        SweDataRecord record = null;
        if (resultStructure instanceof SweDataArray
                && ((SweDataArray) resultStructure).getElementType() instanceof SweDataRecord) {
            final SweDataArray array = (SweDataArray) resultStructure;
            record = (SweDataRecord) array.getElementType();
        } else if (resultStructure instanceof SweDataRecord) {
            record = (SweDataRecord) resultStructure;
        } else {
            throw new NoApplicableCodeException().withMessage("Unsupported ResultStructure!");
        }
        return record;
    }

    protected SosResultStructure createSosResultStructure(String resultStructure) throws CodedException {
        SweAbstractDataComponent abstractDataComponent = (SweAbstractDataComponent) decode(resultStructure);
        if (abstractDataComponent != null) {
            return new SosResultStructure(abstractDataComponent, resultStructure);
        }
        return new SosResultStructure(resultStructure);
    }

    protected SosResultEncoding createSosResultEncoding(String resultEncoding) throws CodedException {
        SweAbstractEncoding abstractEncoding = (SweAbstractEncoding) decode(resultEncoding);
        if (abstractEncoding != null) {
            return new SosResultEncoding(abstractEncoding, resultEncoding);
        }
        return new SosResultEncoding(resultEncoding);
    }

    protected Object decode(String xml) throws CodedException {
        try {
            XmlObject xmlObject = XmlHelper.parseXmlString(xml);
            DecoderKey decoderKey = CodingHelper.getDecoderKey(xmlObject);
            Decoder<Object, Object> decoder = decodingRepository.getDecoder(decoderKey);
            if (decoder != null) {
                return decoder.decode(xmlObject);
            } else {
                throw new NoApplicableCodeException().withMessage("No decoder found for %s",
                        xmlObject.getClass().getName());
            }
        } catch (DecodingException de) {
            throw new NoApplicableCodeException().causedBy(de);
        }
    }

}

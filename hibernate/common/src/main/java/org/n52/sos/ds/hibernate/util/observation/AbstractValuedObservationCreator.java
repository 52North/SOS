/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.util.JavaHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractValuedObservationCreator<T> implements ValuedObservationVisitor<T> {

    private DecoderRepository decoderRepository;
    private boolean noValues;

    public AbstractValuedObservationCreator(DecoderRepository decoderRepository) {
        this(decoderRepository, false);
    }

    public AbstractValuedObservationCreator(DecoderRepository decoderRepository, boolean noValues) {
        this.decoderRepository = decoderRepository;
        this.noValues = noValues;
    }

    protected DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    protected boolean isNoValues() {
        return noValues;
    }

    protected Object decode(XmlObject xml) throws DecodingException {
        return getDecoderRepository().getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
    }

    protected SweDataArray createSweDataArray(DataArrayDataEntity o) throws CodedException {
        try {
            SweDataArray array = new SweDataArray();
            array.setEncoding(getEncoding(o));
            array.setElementType(getStructure(o));
            if (!isNoValues()) {
                if (o.isSetStringValue()) {
                    array.setXml(null);
                    List<List<String>> values = new LinkedList<>();
                    for (String block : o.getStringValue()
                            .split(((SweTextEncoding) array.getEncoding()).getBlockSeparator())) {
                        List<String> v = new LinkedList<>();
                        for (String value : block.split(((SweTextEncoding) array.getEncoding()).getTokenSeparator())) {
                            v.add(value);
                        }
                        values.add(v);
                    }
                    array.setValues(values);
                } else if (o.getValue() != null && !o.getValue()
                        .isEmpty()) {
                    int i = ((SweAbstractDataRecord) array.getElementType()).getFieldIndexByIdentifier(o.getDataset()
                            .getPhenomenon()
                            .getIdentifier()) == 0 ? 1 : 0;
                    List<List<String>> values = new LinkedList<>();
                    for (DataEntity<?> v : o.getValue()) {
                        List<String> value = new LinkedList<>();
                        if (i == 0) {
                            value.add(v.getDataset()
                                    .getPhenomenon()
                                    .getName());
                            value.add(JavaHelper.asString(v.getValue()));
                        } else {
                            value.add(JavaHelper.asString(v.getValue()));
                            value.add(v.getDataset()
                                    .getPhenomenon()
                                    .getName());
                        }
                        values.add(value);
                    }
                    array.setValues(values);
                }
            }
            return array;
        } catch (DecodingException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while creating SweDataArray from database entity!");
        }
    }

    private SweAbstractEncoding getEncoding(DataArrayDataEntity o) throws DecodingException {
        String encoding;
        if (o.getResultTemplate().isSetObservationEncoding()) {
            encoding = o.getResultTemplate().getObservationEncoding();
        } else {
            encoding = o.getResultTemplate().getEncoding();
        }
        return (SweAbstractEncoding) decode(XmlHelper.parseXmlString(encoding));
    }

    private SweAbstractDataComponent getStructure(DataArrayDataEntity o) throws DecodingException {
        String structure;
        if (o.getResultTemplate().isSetObservationStructure()) {
            structure = o.getResultTemplate().getObservationStructure();
        } else {
            structure = o.getResultTemplate().getStructure();
        }
        return (SweAbstractDataComponent) decode(XmlHelper.parseXmlString(structure));
    }

}

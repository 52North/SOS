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
package org.n52.sos.util.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class SweDataArrayBuilder {

    private SweDataRecord elementType;

    private String[] encodingParameter;

    private List<List<String>> blocks;

    public static SweDataArrayBuilder aSweDataArray() {
        return new SweDataArrayBuilder();
    }

    public SweDataArrayBuilder setElementType(SweDataRecord elementType) {
        this.elementType = elementType;
        return this;
    }

    /**
     * 
     * @param encodingParameter
     *            type, block separator, token separator, decimal separator
     * @return
     */
    public SweDataArrayBuilder setEncoding(String... encodingParameter) {
        this.encodingParameter = encodingParameter;
        return this;
    }

    public SweDataArrayBuilder addBlock(String... tokens) {
        if (tokens != null && tokens.length > 0) {
            if (blocks == null) {
                blocks = new ArrayList<List<String>>();
            }
            blocks.add(Arrays.asList(tokens));
        }
        return this;
    }

    public SweDataArray build() {
        SweDataArray dataArray = new SweDataArray();
        dataArray.setElementType(elementType);
        if (encodingParameter != null && encodingParameter.length == 4) {
            if (encodingParameter[0].equals("text")) {
                SweTextEncoding encoding = new SweTextEncoding();
                encoding.setBlockSeparator(encodingParameter[1]);
                encoding.setTokenSeparator(encodingParameter[2]);
                encoding.setDecimalSeparator(encodingParameter[3]);
                dataArray.setEncoding(encoding);
            }
        }
        if (blocks != null && !blocks.isEmpty()) {
            for (List<String> block : blocks) {
                dataArray.add(block);
            }
        }
        return dataArray;
    }

}

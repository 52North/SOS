/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.series.wml.ObservationProcess;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.util.CollectionHelper;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class InspireOmpr30WaterML20Converter implements Converter<SosProcedureDescription, SosProcedureDescription> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InspireOmpr30WaterML20Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL), 
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKeyType(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, "http://inspire.ec.europa.eu/featureconcept/Process"),
            new ConverterKeyType("http://inspire.ec.europa.eu/featureconcept/Process",WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public InspireOmpr30WaterML20Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(SosProcedureDescription objectToConvert) throws ConverterException {
        if (objectToConvert.getDescriptionFormat().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)) {
            return convertWML2ObservationProcessToInspireProcess(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE)
                || objectToConvert.getDescriptionFormat().equals("http://inspire.ec.europa.eu/featureconcept/Process")) {
            return convertInspireProcessToWML2ObservationProcess(objectToConvert);
        }
        return new SosProcedureDescriptionUnknowType(objectToConvert.getIdentifier(), objectToConvert.getDescriptionFormat(), null);
    }

    private SosProcedureDescription convertWML2ObservationProcessToInspireProcess(
            SosProcedureDescription objectToConvert) {
        ObservationProcess op = (ObservationProcess) objectToConvert;
        Process p = new Process();
        p.setIdentifier(op.getIdentifier());
        if (op.isSetName()) {
            p.setName(op.getName());
        }
        if (op.isSetDescription()) {
            p.setDescription(op.getDescription());
        }
        p.setDescriptionFormat(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL);
        return p;
    }

    private SosProcedureDescription convertInspireProcessToWML2ObservationProcess(
            SosProcedureDescription objectToConvert) {
        Process p = (Process) objectToConvert;
        ObservationProcess op = new ObservationProcess();
        op.setIdentifier(p.getIdentifier());
        if (p.isSetName()) {
            op.setName(p.getName());
        }
        if (p.isSetDescription()) {
            op.setDescription(p.getDescription());
        }
        if (p.isSetType()) {
            op.setProcessType(new ReferenceType(p.getType()));
        } else {
            op.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        op.setDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING);
        return op;
    }

}

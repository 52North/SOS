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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterKey;
import org.n52.shetland.inspire.ompr.InspireOMPRConstants;
import org.n52.shetland.inspire.ompr.Process;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.series.wml.ObservationProcess;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class InspireOmpr30WaterML20Converter
        extends
        ProcedureDescriptionConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspireOmpr30WaterML20Converter.class);

    private static final Set<ConverterKey> CONVERTER_KEY_TYPES = CollectionHelper.set(
            new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL),
            new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    InspireOMPRConstants.FEATURE_CONCEPT_PROCESS),
            new ConverterKey(InspireOMPRConstants.FEATURE_CONCEPT_PROCESS,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public InspireOmpr30WaterML20Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public Set<ConverterKey> getKeys() {
        return Collections.unmodifiableSet(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(AbstractFeature objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof SosProcedureDescription<?>) {
            SosProcedureDescription<?> o = (SosProcedureDescription<?>) objectToConvert;
            if (o.getDescriptionFormat().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)) {
                return convertWML2ObservationProcessToInspireProcess(o);
            } else if (o.getDescriptionFormat().equals(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL)
                    || o.getDescriptionFormat().equals(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE)
                    || o.getDescriptionFormat().equals(InspireOMPRConstants.FEATURE_CONCEPT_PROCESS)) {
                return convertInspireProcessToWML2ObservationProcess(o);
            }
            return new SosProcedureDescriptionUnknownType(objectToConvert.getIdentifier(), o.getDescriptionFormat(),
                    null);
        }
        return new SosProcedureDescriptionUnknownType(objectToConvert.getIdentifier(),
                objectToConvert.getDefaultElementEncoding(), null);
    }

    private SosProcedureDescription convertWML2ObservationProcessToInspireProcess(
            SosProcedureDescription objectToConvert) {
        if (objectToConvert.getProcedureDescription() instanceof ObservationProcess) {
            ObservationProcess op = (ObservationProcess) objectToConvert.getProcedureDescription();
            Process p = new Process();
            p.setIdentifier(op.getIdentifier());
            if (op.isSetName()) {
                p.setName(op.getName());
            }
            if (op.isSetDescription()) {
                p.setDescription(op.getDescription());
            }
            SosProcedureDescription<Process> spd = new SosProcedureDescription<Process>(p);
            spd.setDescriptionFormat(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL);
            return spd;
        }
        return objectToConvert;
    }

    private SosProcedureDescription convertInspireProcessToWML2ObservationProcess(
            SosProcedureDescription objectToConvert) {
        if (objectToConvert.getProcedureDescription() instanceof Process) {
            Process p = (Process) objectToConvert.getProcedureDescription();
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
            SosProcedureDescription<ObservationProcess> spd = new SosProcedureDescription<ObservationProcess>(op);
            spd.setDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING);
            return spd;
        }
        return objectToConvert;
    }

}

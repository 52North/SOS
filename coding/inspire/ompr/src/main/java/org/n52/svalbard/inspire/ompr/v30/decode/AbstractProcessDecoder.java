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
package org.n52.svalbard.inspire.ompr.v30.decode;

import static java.util.Collections.singletonMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.ProcedureDecoder;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;

import com.google.common.collect.ImmutableSet;

import eu.europa.ec.inspire.schemas.base.x33.IdentifierType;
import eu.europa.ec.inspire.schemas.ompr.x30.ProcessType;

public abstract class AbstractProcessDecoder<S> implements ProcedureDecoder<Process, S> {

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES =
            singletonMap(SupportedTypeKey.ProcedureDescriptionFormat,
                    (Set<String>) ImmutableSet.of(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE));

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        return Collections.emptySet();
    }

    protected Process parseProcessType(ProcessType pt) {
        Process process = new Process();
        parseInspireId(pt, process);
        return process;
    }

    private void parseInspireId(ProcessType pt, Process process) {
        IdentifierType identifier = pt.getInspireId().getIdentifier();
        String localId = identifier.getLocalId();
        String namespace = identifier.getNamespace();
        CodeWithAuthority codeWithAuthority;
        if (localId.contains(namespace)) {
            codeWithAuthority = new CodeWithAuthority(localId, namespace);
        } else {
            codeWithAuthority = new CodeWithAuthority(getIdentifier(localId, namespace), namespace);
        }
        process.setIdentifier(codeWithAuthority);
    }

    private String getIdentifier(String localId, String namespace) {
        if (namespace.endsWith("=")) {
            return namespace + localId;
        }
        if (namespace.startsWith("urn")) {
            return namespace + ":" + localId;
        } else if (namespace.startsWith("http")) {
            return namespace + "/" + localId;
        }
        return namespace + "-" + localId;
    }
}

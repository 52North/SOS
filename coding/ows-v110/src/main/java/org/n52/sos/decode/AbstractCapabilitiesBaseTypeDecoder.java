/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.decode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.iceland.ogc.ows.DCP;
import org.n52.iceland.ogc.ows.OwsCapabilities;
import org.n52.iceland.ogc.ows.OwsExtendedCapabilities;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.ogc.ows.OwsOperationsMetadata;
import org.n52.iceland.ogc.ows.OwsParameterValue;
import org.n52.iceland.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.iceland.ogc.ows.OwsParameterValueRange;
import org.n52.iceland.ogc.ows.OwsServiceIdentification;
import org.n52.iceland.ogc.ows.OwsServiceProvider;
import org.n52.iceland.util.CollectionHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.opengis.ows.x11.CapabilitiesBaseType;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.RangeType;
import net.opengis.ows.x11.ValueType;
import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.HTTPDocument.HTTP;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;

public abstract class AbstractCapabilitiesBaseTypeDecoder {

    protected OwsCapabilities parseCapabilitiesBaseType(CapabilitiesBaseType cbt)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (cbt != null) {
            OwsCapabilities capabilities = new OwsCapabilities(cbt.getVersion());
            capabilities.setServiceIdentification(parseServiceIdentification(cbt));
            capabilities.setServiceProvider(parseServiceProvider(cbt));
            capabilities.setOperationsMetadata(parseOperationsMetadata(cbt));
            capabilities.setUpdateSequence(parseUpdateSequence(cbt));
            return capabilities;
        }
        return null;
    }

    private OwsServiceIdentification parseServiceIdentification(CapabilitiesBaseType cbt) {
        if (cbt.isSetServiceIdentification()) {
        }
        return null;
    }

    private OwsServiceProvider parseServiceProvider(CapabilitiesBaseType cbt) {
        if (cbt.isSetServiceProvider()) {
        }
        return null;
    }

    private OwsOperationsMetadata parseOperationsMetadata(CapabilitiesBaseType cbt) {
        if (cbt.isSetOperationsMetadata()) {
            OwsOperationsMetadata metadata = new OwsOperationsMetadata();
            OperationsMetadata om = cbt.getOperationsMetadata();
            metadata.setExtendedCapabilities(parseExtendedCapabilities(om));
            // parseConstraint(om);
            // parseParameter(om)
            metadata.setOperations(parseOperations(om));
            return metadata;
        }
        return null;
    }

    private Collection<OwsOperation> parseOperations(OperationsMetadata om) {
        if (CollectionHelper.isNotNullOrEmpty(om.getOperationArray())) {
            Set<OwsOperation> operations = Sets.newHashSet();
            for (Operation o : om.getOperationArray()) {
                OwsOperation operation = new OwsOperation();
                operation.setOperationName(o.getName());
                operation.setDcp(parseDcps(o));
                operation.setParameterValues(parseParameterValues(o));
                operations.add(operation);
            }
            return operations;
        }
        return null;
    }

    private Map<String, ? extends Collection<DCP>> parseDcps(Operation o) {
        if (CollectionHelper.isNotNullOrEmpty(o.getDCPArray())) {
            Map<String, Set<DCP>> dcps = Maps.newLinkedHashMap();
            for (net.opengis.ows.x11.DCPDocument.DCP dcp : o.getDCPArray()) {
                parseHttp(dcp.getHTTP());
            }
            return dcps;
        }
        return null;
    }

    private void parseHttp(HTTP http) {
        http.getGetArray();
        http.getPostArray();
    }

    private Map<String, List<OwsParameterValue>> parseParameterValues(Operation o) {
        if (CollectionHelper.isNotNullOrEmpty(o.getParameterArray())) {
            Map<String, List<OwsParameterValue>> parameterValues = Maps.newLinkedHashMap();
            for (DomainType dt : o.getParameterArray()) {
                if (dt.isSetAllowedValues()) {
                    parameterValues.put(dt.getName(), parseAllowedValues(dt.getAllowedValues()));
                } else {
                    // TODO other types
                }
            }
            return parameterValues;
        }
        return null;
    }

    private List<OwsParameterValue> parseAllowedValues(AllowedValues avs) {
        List<OwsParameterValue> values = Lists.newArrayList();
        if (CollectionHelper.isNotNullOrEmpty(avs.getRangeArray())) {
            for (RangeType rt : avs.getRangeArray()) {
                values.add(parseRangeType(rt));
            }
        } else if (CollectionHelper.isNotNullOrEmpty(avs.getValueArray())) {
            OwsParameterValuePossibleValues possibleValues = new OwsParameterValuePossibleValues();
            for (ValueType vt : avs.getValueArray()) {
                possibleValues.addValue(vt.getStringValue());
            }
            values.add(possibleValues);
        }
        return values;
    }

    private OwsParameterValueRange parseRangeType(RangeType rt) {
        return new OwsParameterValueRange(rt.getMinimumValue().getStringValue(),
                rt.getMaximumValue().getStringValue());
    }

    private OwsExtendedCapabilities parseExtendedCapabilities(OperationsMetadata om) {
        // TODO Auto-generated method stub
        return null;
    }

    private String parseUpdateSequence(CapabilitiesBaseType cbt) {
        if (cbt.isSetUpdateSequence()) {

        }
        return null;
    }
}

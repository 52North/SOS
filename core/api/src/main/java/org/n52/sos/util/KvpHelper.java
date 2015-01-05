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
package org.n52.sos.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;

import com.google.common.base.Strings;

/**
 * Utility class for Key-Value-Pair (KVP) requests
 * 
 * @since 4.0.0
 * 
 */
public final class KvpHelper {
    private KvpHelper() {
    }

    public static Map<String, String> getKvpParameterValueMap(HttpServletRequest req) {
        Map<String, String> kvp = new HashMap<String, String>();
        Enumeration<?> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            // all key names to lower case
            String key = (String) parameterNames.nextElement();
            kvp.put(key.replace("amp;", "").toLowerCase(), req.getParameter(key));
        }
        return kvp;
    }

    public static String checkParameterSingleValue(String value, String name) throws MissingParameterValueException,
            InvalidParameterValueException {
        if (checkParameterMultipleValues(value, name).size() == 1) {
            return value;
        } else {
            throw new InvalidParameterValueException(name, value);
        }
    }

    public static String checkParameterSingleValue(String value, Enum<?> name) throws MissingParameterValueException,
            InvalidParameterValueException {
        return checkParameterSingleValue(value, name.name());
    }

    public static List<String> checkParameterMultipleValues(String values, String name)
            throws MissingParameterValueException {
        if (values.isEmpty()) {
            throw new MissingParameterValueException(name);
        }
        List<String> splittedParameterValues = Arrays.asList(values.split(","));
        for (String parameterValue : splittedParameterValues) {
            if (Strings.isNullOrEmpty(parameterValue)) {
                throw new MissingParameterValueException(name);
            }
        }
        return splittedParameterValues;
    }

    public static List<String> checkParameterMultipleValues(String values, Enum<?> name)
            throws MissingParameterValueException {
        return checkParameterMultipleValues(values, name.name());
    }

    public static void checkParameterMultipleValues(List<String> values, String name)
            throws MissingParameterValueException {
        if (CollectionHelper.isEmpty(values)) {
            throw new MissingParameterValueException(name);
        }
        for (String parameterValue : values) {
            if (Strings.isNullOrEmpty(parameterValue)) {
                throw new MissingParameterValueException(name);
            }
        }
    }

    public static void checkParameterValue(String value, String name) throws MissingParameterValueException,
            InvalidParameterValueException {
        if (Strings.isNullOrEmpty(value)) {
            throw new MissingParameterValueException(name);
        }
    }

    public static void checkParameterValue(String value, Enum<?> name) throws MissingParameterValueException,
            InvalidParameterValueException {
        checkParameterValue(value, name.name());
    }

    private static String getParameterValue(String name, Map<String, String> map) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return map.get(key);
            }
        }
        return null;
    }

    public static String getParameterValue(Enum<?> name, Map<String, String> map) {
        return getParameterValue(name.name(), map);
    }

    /**
     * Perform a sanity check on the request parameter without considering version.
     * 
     * @param value
     * @throws InvalidParameterValueException
     */
    public static void checkRequestParameter(String value) throws InvalidParameterValueException {
        for (RequestOperatorKey rok : RequestOperatorRepository.getInstance().getAllRequestOperatorKeys()) {
            if (value.equals(rok.getOperationName())) {
                return;
            }
        }
        throw new InvalidParameterValueException(RequestParams.request, value);
    }
}

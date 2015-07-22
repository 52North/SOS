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
package org.n52.sos.statistics.api.parameters.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.n52.sos.statistics.api.mappings.MetadataDataMapping;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.Description.InformationOrigin;
import org.n52.sos.statistics.api.parameters.Description.Operation;
import org.n52.sos.statistics.api.parameters.generator.formats.MdFormat;
import org.n52.sos.statistics.sos.SosDataMapping;

import com.google.common.io.Files;

public class ParameterGenerator {

    private static String outputFilePath = "PARAMETER.MD";

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            outputFilePath = args[0];
            ParameterGenerator gen = new ParameterGenerator();
            gen.processClass(MetadataDataMapping.class, ServiceEventDataMapping.class, SosDataMapping.class);
        } else {
            throw new IllegalArgumentException("Output file path not defined as argument e.g: ..\\PARAMETERS.MD");
        }
    }

    private Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters;

    public void processClass(Class<?>... classes) throws IOException {
        parameters = new HashMap<>();
        for (Class<?> klass : classes) {
            organize(klass.getFields());
        }
        MdFormat formatter = new MdFormat();
        formatter.setParameters(parameters);
        String printable = formatter.create();
        // System.out.println(printable);
        try {
            Files.write(printable, new File(outputFilePath), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    private void organize(Field[] fields) {
        Map<Operation, List<AbstractEsParameter>> mid =
                Arrays.asList(fields).stream().map(this::getFieldValue).filter(l -> l != null && l.getDescription() != null)
                        .collect(Collectors.groupingBy(l -> ((AbstractEsParameter) l).getDescription().getOperation()));

        for (Operation op : mid.keySet()) {
            Map<InformationOrigin, List<AbstractEsParameter>> collect =
                    mid.get(op).stream().collect(Collectors.groupingBy(l -> ((AbstractEsParameter) l).getDescription().getInformationOrigin()));
            parameters.put(op, collect);
        }

    }

    private AbstractEsParameter getFieldValue(Field field) {
        boolean bool = Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers());
        bool = bool && field.getType().isAssignableFrom((AbstractEsParameter.class));
        if (bool) {
            try {
                return (AbstractEsParameter) field.get(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

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

    private static final String outputFilePath = "PARAMETER.MD";

    public static void main(String[] args) {
        ParameterGenerator gen = new ParameterGenerator();
        gen.processClass(MetadataDataMapping.class, ServiceEventDataMapping.class, SosDataMapping.class);
    }

    private Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters;

    public void processClass(Class<?>... classes) {
        parameters = new HashMap<>();
        for (Class<?> klass : classes) {
            organize(klass.getFields());
        }
        MdFormat formatter = new MdFormat();
        formatter.setParameters(parameters);
        String printable = formatter.create();
        System.out.println(printable);
        try {
            Files.write(printable, new File(outputFilePath), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
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

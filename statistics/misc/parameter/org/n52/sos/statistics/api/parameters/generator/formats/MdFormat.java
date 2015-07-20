package org.n52.sos.statistics.api.parameters.generator.formats;

import java.util.List;
import java.util.Map;

import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.Description.InformationOrigin;
import org.n52.sos.statistics.api.parameters.Description.Operation;
import org.n52.sos.statistics.api.parameters.ObjectEsParameter;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;

import com.google.api.client.repackaged.com.google.common.base.Strings;

public class MdFormat {

    private Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters;
    private StringBuilder output;
    private static final String LINE_TEMPLATE = " - **<fieldname>** - <desc>.";
    private static final String TYPE_TEMPLATE = " Type: <type>";
    private static final String NEW_LINE = "\n";
    private static final String NO_DESCRIPTION = "No available description";
    private static final String TAB = "\t";

    private String formatLine(AbstractEsParameter parameter,
            int indent) {
        String line = Strings.repeat(TAB, indent);

        // Fieldname
        line += LINE_TEMPLATE.replace("<fieldname>", parameter.getName());

        // Description
        if (parameter.getDescription() != null) {
            if (parameter.getDescription().getDesc() != null) {
                line = line.replace("<desc>", parameter.getDescription().getDesc());
            } else {
                line = line.replace("<desc>", NO_DESCRIPTION);
            }

            // Type
            if (parameter.getType() != null) {
                line += TYPE_TEMPLATE.replace("<type>", parameter.getType().humanReadableType());
            }
        } else {
            line = line.replace("<desc>", NO_DESCRIPTION);
        }

        line += NEW_LINE;
        return line;
    }

    private void format(ObjectEsParameter parameter,
            int indent) {
        output.append(formatLine(parameter, indent));
        parameter.getAllChildren().stream().forEach(l -> this.appendToOutput(l, indent + 1));
    }

    private void appendToOutput(AbstractEsParameter parameter,
            int indent) {
        if (parameter instanceof ObjectEsParameter) {
            format((ObjectEsParameter) parameter, indent);
        } else {
            format((SingleEsParameter) parameter, indent);
        }
    }

    private void format(SingleEsParameter parameter,
            int indent) {
        output.append(formatLine(parameter, indent));
    }

    private String formatH1(String line) {
        return NEW_LINE + "###" + line + NEW_LINE;
    }

    private String formatH2(String line) {
        return NEW_LINE + "####" + line + NEW_LINE;
    }

    public String create() {
        output = new StringBuilder();
        // Header 1
        for (Operation op : parameters.keySet()) {
            output.append(formatH1(op.toString()));
            // Header 2
            for (InformationOrigin origin : parameters.get(op).keySet()) {
                output.append(formatH2(origin.toString()));
                parameters.get(op).get(origin).stream().forEach(l -> appendToOutput(l, 0));
            }
        }
        return output.toString();
    }

    public Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters) {
        this.parameters = parameters;
    }

}

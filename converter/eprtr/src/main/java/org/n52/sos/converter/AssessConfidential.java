package org.n52.sos.converter;

import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ParameterHolder;

import java.util.List;

import static org.n52.sos.converter.EprtrConverter.CONFIDENTIAL_CODE;
import static org.n52.sos.converter.EprtrConverter.CONFIDENTIAL_INDICATOR;

public class AssessConfidential {

    EprtrConverter eprtrConverter = new EprtrConverter();

    boolean containsConfidentialCode(List<NamedValue<?>> parameters) {
        for (NamedValue<?> namedValue : parameters) {
            if (namedValue.getName().getHref().equalsIgnoreCase(CONFIDENTIAL_CODE)) {
                return true;
            }
        }
        return false;
    }

    boolean containsConfidentialIndicator(List<NamedValue<?>> parameters) {
        for (NamedValue<?> namedValue : parameters) {
            if (namedValue.getName().getHref().equalsIgnoreCase(CONFIDENTIAL_INDICATOR)) {
                return true;
            }
        }
        return false;
    }

    String getConfidentialIndicator(String confidentialCode, ParameterHolder parameterHolder) {
        String confidentialIndicator = eprtrConverter.getParameter(parameterHolder, CONFIDENTIAL_INDICATOR);
        return confidentialIndicator != null && !confidentialIndicator.isEmpty() ? confidentialIndicator
                : confidentialCode != null && !confidentialCode.isEmpty() ? "true" : "false";
    }
}

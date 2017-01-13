package org.n52.sos.aqd;

import org.n52.janmayen.function.Functions;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.swe.simpleType.SweText;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class ReportObligations {
    private ReportObligations() {
    }

    public static boolean hasFlow(Extensions extensions) {
        return extensions != null && extensions.containsExtension(AqdConstants.EXTENSION_FLOW);
    }

    public static ReportObligationType getFlow(Extensions extensions) throws OwsExceptionReport {
        return extensions.getExtension(AqdConstants.EXTENSION_FLOW)
                .map(Extension::getValue)
                .flatMap(Functions.castIfInstanceOf(SweText.class))
                .map(SweText::getValue)
                .map(ReportObligationType::from)
                .orElse(ReportObligationType.E2A);
    }
}

package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Locale;

import org.hibernate.Session;

import org.n52.janmayen.component.Component;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.hibernate.entities.Procedure;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface HibernateProcedureDescriptionGenerator extends Component<HibernateProcedureDescriptionGeneratorKey> {

    SosProcedureDescription<?> generateProcedureDescription(Procedure procedure, Locale i18n, Session session)
            throws OwsExceptionReport;

}

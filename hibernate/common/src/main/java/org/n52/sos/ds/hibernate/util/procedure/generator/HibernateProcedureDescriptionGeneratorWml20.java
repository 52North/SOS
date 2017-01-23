package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.wml.ObservationProcess;
import org.n52.shetland.ogc.wml.WaterMLConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.Procedure;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class HibernateProcedureDescriptionGeneratorWml20 extends AbstractHibernateProcedureDescriptionGenerator {
    public static final Set<HibernateProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES = Collections
            .singleton(new HibernateProcedureDescriptionGeneratorKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));


    public HibernateProcedureDescriptionGeneratorWml20(DaoFactory daoFactory,
                                                       I18NDAORepository i18NDAORepository,
                                                       ContentCacheController cacheController) {
        super(daoFactory, i18NDAORepository, cacheController);
    }


    /**
     * Generate procedure description from Hibernate procedure entity if no description (file, XML text) is
     * available
     *
     * @param procedure Hibernate procedure entity
     * @param i18n the locale
     * @param session the session
     *
     * @return Generated procedure description
     *
     * @throws OwsExceptionReport If an error occurs
     */
    @Override
    public SosProcedureDescription<AbstractFeature> generateProcedureDescription(Procedure procedure, Locale i18n,
                                                                                 Session session)
            throws OwsExceptionReport {
        setLocale(i18n);
        final ObservationProcess op = new ObservationProcess();
        setCommonData(procedure, op, session);
        addName(procedure, op);
        op.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        return new SosProcedureDescription<>(op);
    }

    private void addName(Procedure procedure, ObservationProcess op) {
        String name = procedure.getIdentifier();
        if (procedure.isSetName()) {
            name = procedure.getName();
        }
        op.addParameter(createName("shortName", name));
        op.addParameter(createName("longName", name));
    }

    private NamedValue<String> createName(String type, String name) {
        final NamedValue<String> namedValueProperty = new NamedValue<>();
        final ReferenceType refType = new ReferenceType(type);
        refType.setTitle(name);
        namedValueProperty.setName(refType);
        namedValueProperty.setValue(new TextValue(name));
        return namedValueProperty;
    }

    @Override
    public Set<HibernateProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

}

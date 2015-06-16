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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.wml.ObservationProcess;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.util.CollectionHelper;

/**
 * Generator class for WaterML 2.0 procedure descriptions
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactoryWml20 implements
        HibernateProcedureDescriptionGeneratorFactory {

    private static final List<HibernateProcedureDescriptionGeneratorFactoryKeyType> GENERATOR_KEY_TYPES =
            CollectionHelper.list(new HibernateProcedureDescriptionGeneratorFactoryKeyType(
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    @Override
    public List<HibernateProcedureDescriptionGeneratorFactoryKeyType> getHibernateProcedureDescriptionGeneratorFactoryKeyTypes() {
        return GENERATOR_KEY_TYPES;
    }

    @Override
    public SosProcedureDescription create(Procedure procedure, Locale i18n, Session session) throws OwsExceptionReport {
        return new HibernateProcedureDescriptionGeneratorWml20()
                .generateProcedureDescription(procedure, i18n, session);
    }

    private class HibernateProcedureDescriptionGeneratorWml20 extends AbstractHibernateProcedureDescriptionGenerator {

        /**
         * Generate procedure description from Hibernate procedure entity if no
         * description (file, XML text) is available
         *
         * @param procedure
         *            Hibernate procedure entity
         * @param session
         *            the session
         *
         * @return Generated procedure description
         *
         * @throws OwsExceptionReport
         *             If an error occurs
         */
        public ObservationProcess generateProcedureDescription(Procedure procedure, Locale i18n, Session session)
                throws OwsExceptionReport {
            setLocale(i18n);
            final ObservationProcess op = new ObservationProcess();
            setCommonData(procedure, op, session);
            addName(procedure, op);
            op.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
            return op;
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
            final NamedValue<String> namedValueProperty = new NamedValue<String>();
            final ReferenceType refType = new ReferenceType(type);
            refType.setTitle(name);
            namedValueProperty.setName(refType);
            namedValueProperty.setValue(new TextValue(name));
            return namedValueProperty;
        }
    }
}

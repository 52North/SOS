/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.procedure.enrich;

import java.util.function.Predicate;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifierPredicates;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class ClassifierEnrichment
        extends
        SensorMLEnrichment {

    public ClassifierEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    @Override
    protected void enrich(AbstractSensorML description)
            throws OwsExceptionReport {
        addIntendedApplicationClassifier(description);
        addProcedureTypeClassification(description);
    }

    private void addIntendedApplicationClassifier(AbstractSensorML description) {
        addClassifier(description, SmlClassifier.INTENDED_APPLICATION,
                procedureSettings().getClassifierIntendedApplicationDefinition(),
                procedureSettings().getClassifierIntendedApplicationValue());
    }

    private void addProcedureTypeClassification(AbstractSensorML description) {
        addClassifier(description, SmlClassifier.PROCEDURE_TYPE,
                procedureSettings().getClassifierProcedureTypeDefinition(),
                procedureSettings().getClassifierProcedureTypeValue());
    }

    private void addClassifier(AbstractSensorML description, String name, String definition, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            Predicate<SmlClassifier> p = SmlClassifierPredicates.name(name);
            if (!description.findClassifier(p).isPresent()) {
                SmlClassifier classifier = new SmlClassifier(name, definition, null, value);
                description.addClassification(classifier);
            }
        }
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isGenerateClassification()
                && procedureSettings().isEnrichWithDiscoveryInformation();
    }
}

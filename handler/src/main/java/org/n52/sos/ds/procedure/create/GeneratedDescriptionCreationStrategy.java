/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.procedure.create;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGenerator;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGeneratorFactoryRepository;

/**
 * Strategy to generate a description.
 */
public class GeneratedDescriptionCreationStrategy implements
        DescriptionCreationStrategy {

    private ProcedureDescriptionGeneratorFactoryRepository factoryRepository;

    public GeneratedDescriptionCreationStrategy(ProcedureDescriptionGeneratorFactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @Override
    public SosProcedureDescription<?> create(ProcedureEntity p, String descriptionFormat, Locale i18n, Session s)
            throws OwsExceptionReport {
        SosProcedureDescription<?> desc = getFactory(descriptionFormat).generateProcedureDescription(p, i18n, s);
        desc.setDescriptionFormat(descriptionFormat);
        return desc;
    }

    @Override
    public boolean apply(ProcedureEntity p) {
        return true;
    }

    private ProcedureDescriptionGenerator getFactory(String descriptionFormat) {
        return factoryRepository.getFactory(descriptionFormat);
    }
}

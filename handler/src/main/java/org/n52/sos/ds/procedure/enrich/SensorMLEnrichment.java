/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifierPredicates;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;


/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public abstract class SensorMLEnrichment extends ProcedureDescriptionEnrichment {

    public SensorMLEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    protected Predicate<SmlIdentifier> longNamePredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                procedureSettings().getIdentifierLongNameDefinition());
    }

    protected Predicate<SmlIdentifier> shortNamePredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                procedureSettings().getIdentifierShortNameDefinition());
    }

    protected Predicate<SmlIdentifier> uniqueIdPredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                OGCConstants.URN_UNIQUE_IDENTIFIER);
    }

    @Override
    public void enrich() throws OwsExceptionReport {
        enrich((AbstractSensorML) getDescription().getProcedureDescription());
    }

    protected abstract void enrich(AbstractSensorML description) throws OwsExceptionReport;

    @Override
    public boolean isApplicable() {
        return getDescription().getProcedureDescription() instanceof AbstractSensorML;
    }
}

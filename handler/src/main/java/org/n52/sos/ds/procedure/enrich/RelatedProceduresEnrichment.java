/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.HashSet;
import java.util.Set;

import org.n52.iceland.convert.ConverterException;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class RelatedProceduresEnrichment
        extends
        AbstractRelatedProceduresEnrichment<ProcedureEntity> {

    public RelatedProceduresEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    /**
     * Add a collection of child procedures to a procedure
     *
     * @return Set with child procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If creation of child procedure description fails
     */
    protected Set<AbstractSensorML> getChildProcedures()
            throws OwsExceptionReport {

        if (!getProcedure().hasChildren()) {
            return Sets.newHashSet();
        }

        Set<AbstractSensorML> childProcedures = Sets.newHashSet();
        for (ProcedureEntity child : getProcedure().getChildren()) {
            SosProcedureDescription<?> childDescription = getConverter().createSosProcedureDescription(child,
                    getProcedureDescriptionFormat(), getVersion(), getLocale(), getSession());
            if (childDescription.getProcedureDescription() instanceof AbstractSensorML) {
                childProcedures.add((AbstractSensorML) childDescription.getProcedureDescription());
            }
        }
        return childProcedures;
    }

    /**
     * Get parent procedures
     *
     * @throws OwsExceptionReport If an error occurs
     */
    protected Set<String> getParentProcedures()
            throws OwsExceptionReport {
        Set<String> parents = new HashSet<>();
        if (getProcedure().hasParents()) {
            for (ProcedureEntity parent : getProcedure().getParents()) {
                parents.add(parent.getIdentifier());
            }
        }
        return parents;
    }
}

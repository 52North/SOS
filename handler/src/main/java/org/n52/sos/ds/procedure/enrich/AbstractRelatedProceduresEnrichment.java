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

import java.util.Set;

import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.procedure.AbstractProcedureConverter;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.ds.utils.HibernateUnproxy;

import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractRelatedProceduresEnrichment
        extends ProcedureDescriptionEnrichment implements HibernateUnproxy {
    private ProcedureEntity procedure;

    private String procedureDescriptionFormat;

    private AbstractProcedureConverter<ProcedureEntity> converter;

    private TimePeriod validTime;

    public AbstractRelatedProceduresEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    public AbstractRelatedProceduresEnrichment setProcedure(ProcedureEntity procedure) {
        this.procedure = procedure;
        return this;
    }

    public AbstractRelatedProceduresEnrichment setProcedureDescriptionFormat(String pdf) {
        this.procedureDescriptionFormat = Preconditions.checkNotNull(pdf);
        return this;
    }

    public AbstractRelatedProceduresEnrichment setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
        return this;
    }

    public AbstractRelatedProceduresEnrichment setConverter(AbstractProcedureConverter<ProcedureEntity> converter) {
        this.converter = converter;
        return this;
    }

    public ProcedureEntity getProcedure() {
        return procedure;
    }

    /**
     * @return the procedureDescriptionFormat
     */
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * @return the validTime
     */
    public TimePeriod getValidTime() {
        return validTime;
    }

    public AbstractProcedureConverter<ProcedureEntity> getConverter() {
        return converter;
    }

    @Override
    public void enrich()
            throws OwsExceptionReport {
        Set<String> parentProcedures = getParentProcedures();
        if (CollectionHelper.isNotEmpty(parentProcedures)) {
            getDescription().setParentProcedure(new ReferenceType(parentProcedures.iterator().next()));
        }
        Set<AbstractSensorML> childProcedures = getChildProcedures();
        if (CollectionHelper.isNotEmpty(childProcedures)) {
            getDescription().addChildProcedures(childProcedures);
        }
    }

    protected abstract Set<AbstractSensorML> getChildProcedures() throws OwsExceptionReport;

    protected abstract  Set<String> getParentProcedures() throws OwsExceptionReport;

}

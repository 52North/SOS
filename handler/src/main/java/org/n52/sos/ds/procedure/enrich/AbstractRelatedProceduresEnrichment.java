/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.sos.ds.procedure.AbstractProcedureConverter;

public abstract class AbstractRelatedProceduresEnrichment<T> extends ProcedureDescriptionEnrichment {
    private T procedure;
    private String procedureDescriptionFormat;
    private AbstractProcedureConverter<T> converter;
    private TimePeriod validTime;
    private I18NDAORepository i18NDAORepository;

    public AbstractRelatedProceduresEnrichment<T> setProcedure(T procedure) {
        this.procedure = procedure;
        return this;
    }

    public AbstractRelatedProceduresEnrichment<T> setProcedureDescriptionFormat(String pdf) {
        this.procedureDescriptionFormat = checkNotNull(pdf);
        return this;
    }

    public AbstractRelatedProceduresEnrichment<T> setConverter(
            AbstractProcedureConverter<T> c) {
        this.converter = checkNotNull(c);
        return this;
    }

    public AbstractRelatedProceduresEnrichment<T> setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
        return this;
    }

    /**
     * @param i18ndaoRepository the i18NDAORepository to set
     */
    public AbstractRelatedProceduresEnrichment<T> setI18NDAORepository(I18NDAORepository i18ndaoRepository) {
        this.i18NDAORepository = i18ndaoRepository;
        return this;
    }

    /**
     * @return the procedure
     */
    public T getProcedure() {
        return procedure;
    }

    /**
     * @return the procedureDescriptionFormat
     */
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * @return the converter
     */
    public AbstractProcedureConverter<T> getConverter() {
        return converter;
    }

    /**
     * @return the validTime
     */
    public TimePeriod getValidTime() {
        return validTime;
    }

    /**
     * @return the i18NDAORepository
     */
    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

}

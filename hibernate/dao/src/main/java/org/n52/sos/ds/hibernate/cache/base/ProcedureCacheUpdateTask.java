/**
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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
class ProcedureCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdateTask.class);

    private String procedureId;
    
    private ProcedureDAO procedureDAO = new ProcedureDAO();

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded
     * by a session in a different thread *
     * 
     * @param procedureId
     *            Procedure identifier
     */
    ProcedureCacheUpdateTask(String procedureId) {
        this.procedureId = procedureId;
    }

    protected void getProcedureInformationFromDbAndAddItToCacheMaps() throws OwsExceptionReport {
        // temporal extent
        if (checkTimes()) {
            TimeExtrema pte = null;
            if (procedureDAO.isProcedureTimeExtremaNamedQuerySupported(getSession())) {
                pte = procedureDAO.getProcedureTimeExtremaFromNamedQuery(getSession(), procedureId);
            } else {
                if (isSetTimeExtremaEmpty(pte) && DaoFactory.getInstance().isSeriesDAO()) {
                    pte = DaoFactory.getInstance().getSeriesDAO().getProcedureTimeExtrema(getSession(), procedureId);
                }
                if (isSetTimeExtremaEmpty(pte)) {
                    pte = new ProcedureDAO().getProcedureTimeExtrema(getSession(), procedureId);
                }
            }
            if (pte != null && pte.isSetPhenomenonTimes()) {
                getCache().setMinPhenomenonTimeForProcedure(procedureId, pte.getMinPhenomenonTime());
                getCache().setMaxPhenomenonTimeForProcedure(procedureId, pte.getMaxPhenomenonTime());
            }
        }
        getProcedureDescriptionFormats();
    }

    private void getProcedureDescriptionFormats() {
        Procedure procedure = procedureDAO.getProcedureForIdentifier(procedureId, getSession());
        String procedureDescriptionFormat = procedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
        Set<String> formats = Sets.newHashSet(procedureDescriptionFormat);
        Set<String> toNamespaceConverterFrom = ConverterRepository.getInstance().getToNamespaceConverterFrom(procedureDescriptionFormat);
        if (CollectionHelper.isNotEmpty(toNamespaceConverterFrom)) {
            formats.addAll(toNamespaceConverterFrom);
        }
        
        getCache().addProcedureDescriptionFormatsForProcedure(procedureId, formats);
    }

    protected Set<String> getProcedureIdentifiers(Set<Procedure> procedures) {
        Set<String> identifiers = new HashSet<String>(procedures.size());
        for (Procedure procedure : procedures) {
            identifiers.add(procedure.getIdentifier());
        }
        return identifiers;
    }

    private boolean checkTimes() {
        return getCache().getMinPhenomenonTimeForProcedure(procedureId) == null
                || getCache().getMaxPhenomenonTimeForProcedure(procedureId) == null;
    }

    private boolean isSetTimeExtremaEmpty(TimeExtrema te) {
        return te == null || (te != null && !te.isSetPhenomenonTimes());
    }

    @Override
    public void execute() {
        try {
            getProcedureInformationFromDbAndAddItToCacheMaps();
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        } catch (Exception e) {
            getErrors().add(new GenericThrowableWrapperException(e)
                    .withMessage("Error while processing procedure cache update task!"));
        }
    }
}

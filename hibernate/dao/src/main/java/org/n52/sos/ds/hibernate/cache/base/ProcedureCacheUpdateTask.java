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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
class ProcedureCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdateTask.class);

    private String procedureId;

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded by a session in a different thread     * 
     * @param procedureId Procedure identifier
     */
    ProcedureCacheUpdateTask(String procedureId) {
        this.procedureId = procedureId;
    }

    protected void getProcedureInformationFromDbAndAddItToCacheMaps() throws OwsExceptionReport {
        //temporal extent
        ProcedureDAO procedureDAO = new ProcedureDAO();
        TimeExtrema pte = null;
        if (procedureDAO.isProcedureTimeExtremaNamedQuerySupported(getSession())) {
            pte = procedureDAO.getProcedureTimeExtremaFromNamedQuery(getSession(), procedureId);
        } else {
            AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
            if (isSetTimeExtremaEmpty(pte) && seriesDAO != null) {
                pte = seriesDAO.getProcedureTimeExtrema(getSession(), procedureId);
            }
            if (isSetTimeExtremaEmpty(pte)) {
                pte = new ProcedureDAO().getProcedureTimeExtrema(getSession(), procedureId);
            }
        }
        if (pte != null && pte.isSetTimes()) {
            getCache().setMinPhenomenonTimeForProcedure(procedureId, pte.getMinTime());
            getCache().setMaxPhenomenonTimeForProcedure(procedureId, pte.getMaxTime());
        }
    }
    
    private boolean isSetTimeExtremaEmpty(TimeExtrema te) {
        return te == null || (te != null && !te.isSetTimes());
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

    protected Set<String> getProcedureIdentifiers(Set<Procedure> procedures) {
        Set<String> identifiers = new HashSet<String>(procedures.size());
        for (Procedure procedure : procedures) {
            identifiers.add(procedure.getIdentifier());
        }
        return identifiers;
    }
}

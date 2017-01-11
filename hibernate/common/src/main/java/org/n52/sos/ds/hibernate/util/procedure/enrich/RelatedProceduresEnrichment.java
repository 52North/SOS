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
package org.n52.sos.ds.hibernate.util.procedure.enrich;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ds.procedure.enrich.AbstractRelatedProceduresEnrichment;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class RelatedProceduresEnrichment extends AbstractRelatedProceduresEnrichment<Procedure> {

    @Override
    public void enrich() throws OwsExceptionReport {
        Set<String> parentProcedures = getParentProcedures();
        if (parentProcedures != null) {
            getDescription().setParentProcedure(new ReferenceType(parentProcedures.iterator().next()));
        }
        Set<AbstractSensorML> childProcedures = getChildProcedures();
        if (CollectionHelper.isNotEmpty(childProcedures)) {
            getDescription().addChildProcedures(childProcedures);
        }
    }

    /**
     * Add a collection of child procedures to a procedure
     *
     * @param procedure
     *            Parent procedure identifier
     * @param outputFormat
     *            Procedure description format
     * @param version
     *            Service version
     * @param cache
     *            Loaded procedure map
     * @param session
     *            Hibernate session
     * @return Set with child procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If creation of child procedure description fails
     */
    private Set<AbstractSensorML> getChildProcedures()
            throws OwsExceptionReport {

        if (!getProcedure().hasChilds()) {
            return Sets.newHashSet();
        }

        Set<AbstractSensorML> childProcedures = Sets.newHashSet();
        for (Procedure child : getProcedure().getChilds()) {

            //if child has valid vpts, use the most recent one within
            //the validTime to create the child procedure
            ValidProcedureTime childVpt = null;
            if (child instanceof TProcedure) {
                TProcedure tChild = (TProcedure) child;
                for (ValidProcedureTime cvpt : tChild.getValidProcedureTimes()) {
                    TimePeriod thisCvptValidTime = new TimePeriod(cvpt.getStartTime(),
                            cvpt.getEndTime());

                    if (getValidTime() != null && !getValidTime().isSetEnd() && !thisCvptValidTime.isSetEnd()) {
                        childVpt = cvpt;
                    } else {
                        //make sure this child's validtime is within the parent's valid time,
                        //if parent has one
                        if (getValidTime() != null && !thisCvptValidTime.isWithin(getValidTime())){
                            continue;
                        }

                        if (childVpt == null || cvpt.getEndTime() == null ||
                                (cvpt.getEndTime() != null && childVpt.getEndTime() != null &&
                                cvpt.getEndTime().after(childVpt.getEndTime()))) {
                            childVpt = cvpt;
                        }
                    }
                }
            }

            if (childVpt != null) {
                //matching child validProcedureTime was found, use it to build procedure description
                SosProcedureDescription<?> childDescription =
                        ((HibernateProcedureConverter)getConverter()).createSosProcedureDescriptionFromValidProcedureTime(
                                child, getProcedureDescriptionFormat(), childVpt, getVersion(), getLocale(), getI18NDAORepository(), getSession());
                if (childDescription.getProcedureDescription() instanceof AbstractSensorML) {
                    childProcedures.add((AbstractSensorML)childDescription.getProcedureDescription());
                }
            } else  if  (child != null) {
                //no matching child validProcedureTime, generate the procedure description
                SosProcedureDescription<?> childDescription = getConverter().createSosProcedureDescription(
                        child, getProcedureDescriptionFormat(), getVersion(), getLocale(), getI18NDAORepository(), getSession());
                // TODO check if call is necessary because it is also called in
                // createSosProcedureDescription()
                // addValuesToSensorDescription(childProcID,childProcedureDescription,
                // version, outputFormat, session);
                if (childDescription.getProcedureDescription() instanceof AbstractSensorML) {
                    childProcedures.add((AbstractSensorML)childDescription.getProcedureDescription());
                }
            }
        }
        return childProcedures;
    }

    private Map<String, Procedure> createProcedureCache() {
        Set<String> identifiers = getCache().getChildProcedures(getIdentifier(), true, false);
        List<Procedure> children = new ProcedureDAO().getProceduresForIdentifiers(identifiers, getSession());
        Map<String, Procedure> cache = Maps.newHashMapWithExpectedSize(children.size());
        for (Procedure child : children) {
            cache.put(child.getIdentifier(), child);
        }
        return cache;
    }

     /**
     * Add parent procedures to a procedure
     *
     * @param procID
     *            procedure identifier to add parent procedures to
     *
     * @throws OwsExceptionReport
     */
    private Set<String> getParentProcedures() throws OwsExceptionReport {
        return getCache().getParentProcedures(getIdentifier(), false, false);
    }
}

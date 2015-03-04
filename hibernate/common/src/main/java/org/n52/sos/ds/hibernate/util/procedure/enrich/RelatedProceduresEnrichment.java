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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class RelatedProceduresEnrichment extends ProcedureDescriptionEnrichment {
    private String procedureDescriptionFormat;
    private HibernateProcedureConverter converter;
    private Map<String, Procedure> procedureCache;
    private TimePeriod validTime;

    public RelatedProceduresEnrichment setProcedureDescriptionFormat(String pdf) {
        this.procedureDescriptionFormat = checkNotNull(pdf);
        return this;
    }

    public RelatedProceduresEnrichment setConverter(
            HibernateProcedureConverter c) {
        this.converter = checkNotNull(c);
        return this;
    }

    public RelatedProceduresEnrichment setProcedureCache(
            Map<String, Procedure> cache) {
        this.procedureCache = cache;
        return this;
    }

    public RelatedProceduresEnrichment setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
        return this;
    }    

    @Override
    public void enrich() throws OwsExceptionReport {
        Set<String> parentProcedures = getParentProcedures();
        if (CollectionHelper.isNotEmpty(parentProcedures)) {
            getDescription().addParentProcedures(parentProcedures);
        }
        Set<SosProcedureDescription> childProcedures = getChildProcedures();
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
    private Set<SosProcedureDescription> getChildProcedures()
            throws OwsExceptionReport {

        final Collection<String> childIdentfiers =
                getCache().getChildProcedures(getIdentifier(), false, false);

        if (CollectionHelper.isEmpty(childIdentfiers)) {
            return Sets.newHashSet();
        }

        if (procedureCache == null) {
            procedureCache = createProcedureCache();
        }

        Set<SosProcedureDescription> childProcedures = Sets.newHashSet();
        for (String childId : childIdentfiers) {
            Procedure child = procedureCache.get(childId);
            
            //if child has valid vpts, use the most recent one within
            //the validTime to create the child procedure
            ValidProcedureTime childVpt = null;
            if (child instanceof TProcedure) {
                TProcedure tChild = (TProcedure) child;                
                for (ValidProcedureTime cvpt : tChild.getValidProcedureTimes()) {
                    TimePeriod thisCvptValidTime = new TimePeriod(cvpt.getStartTime(),
                            cvpt.getEndTime());
                    
                    if (validTime != null && !validTime.isSetEnd() && !thisCvptValidTime.isSetEnd()) {
                        childVpt = cvpt;
                    } else {
                        //make sure this child's validtime is within the parent's valid time,
                        //if parent has one
                        if (validTime != null && !thisCvptValidTime.isWithin(validTime)){
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
                SosProcedureDescription childDescription =
                        converter.createSosProcedureDescriptionFromValidProcedureTime(
                                child, procedureDescriptionFormat, childVpt, getVersion(), getLocale(), getSession());
                childProcedures.add(childDescription);                
            } else  if  (child != null) {
                //no matching child validProcedureTime, generate the procedure description
                SosProcedureDescription childDescription = converter.createSosProcedureDescription(
                        child, procedureDescriptionFormat, getVersion(), procedureCache, getLocale(), getSession());
                // TODO check if call is necessary because it is also called in
                // createSosProcedureDescription()
                // addValuesToSensorDescription(childProcID,childProcedureDescription,
                // version, outputFormat, session);
                childProcedures.add(childDescription);
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

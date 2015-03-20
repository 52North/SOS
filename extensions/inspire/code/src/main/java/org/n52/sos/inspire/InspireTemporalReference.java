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
package org.n52.sos.inspire;

import java.util.Collection;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * Service internal representation of INSPIRE temporal reference
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireTemporalReference {

    /* DateOfCreation 0..1 */
    private InspireDateOfCreation dateOfCreation;

    /* DateOfLastRevision 0..1 */
    private InspireDateOfLastRevision dateOfLastRevision;

    /* DateOfPublication 0..* */
    private List<InspireDateOfPublication> datesOfPublication = Lists.newArrayList();

    /*
     * TemporalExtent 0..*, IndividualDate == {@link
     * org.n52.sos.ogc.gml.time.TimeInstant} or IntervalOfDates == {@link
     * org.n52.sos.ogc.gml.time.TimePeriod}
     */
    private List<Time> temporalExtents = Lists.newArrayList();

    /**
     * Get the date of creation
     * 
     * @return the dateOfCreation
     */
    public InspireDateOfCreation getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Set the date of creation
     * 
     * @param dateOfCreation
     *            the dateOfCreation to set
     */
    public void setDateOfCreation(InspireDateOfCreation dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    /**
     * Check if the date of creation is set
     * 
     * @return <code>true</code>, if the date of creation is set
     */
    public boolean isSetDateOfCreation() {
        return getDateOfCreation() != null && !getDateOfCreation().isEmpty();
    }

    /**
     * Get the date of last revision
     * 
     * @return the dateOfLastRevision
     */
    public InspireDateOfLastRevision getDateOfLastRevision() {
        return dateOfLastRevision;
    }

    /**
     * Set the date of last revision
     * 
     * @param dateOfLastRevision
     *            the dateOfLastRevision to set
     */
    public void setDateOfLastRevision(InspireDateOfLastRevision dateOfLastRevision) {
        this.dateOfLastRevision = dateOfLastRevision;
    }

    /**
     * Check if the date of last revision is set
     * 
     * @return <code>true</code>, if the date of last revision is set
     */
    public boolean isSetDateOfLastRevision() {
        return getDateOfLastRevision() != null;
    }

    /**
     * gethe dates of publication
     * 
     * @return the datesOfPublication
     */
    public List<InspireDateOfPublication> getDatesOfPublication() {
        return datesOfPublication;
    }

    /**
     * Set the dates of publication, clears the existing collection
     * 
     * @param datesOfPublication
     *            the datesOfPublication to set
     */
    public void setDatesOfPublication(Collection<InspireDateOfPublication> datesOfPublication) {
        getDatesOfPublication().clear();
        if (CollectionHelper.isNotEmpty(datesOfPublication)) {
            getDatesOfPublication().addAll(datesOfPublication);
        }
    }

    /**
     * Add a date of publication
     * 
     * @param dateOfPublication
     *            the date of publication to add
     * @return this
     */
    public InspireTemporalReference addDateOfPublication(InspireDateOfPublication dateOfPublication) {
        getDatesOfPublication().add(dateOfPublication);
        return this;
    }

    /**
     * Check if date of publications are set
     * 
     * @return <code>true</code>, if date of publications are set
     */
    public boolean isSetDatesOfPublication() {
        return CollectionHelper.isNotEmpty(getDatesOfPublication());
    }

    /**
     * Get the temporal extents
     * 
     * @return the temporalExtents
     */
    public List<Time> getTemporalExtents() {
        return temporalExtents;
    }

    /**
     * Set the temporal extents, clears the existing collection
     * 
     * @param temporalExtents
     *            the temporalExtents to set
     */
    public void setTemporalExtents(Collection<Time> temporalExtents) {
        getTemporalExtents().clear();
        if (CollectionHelper.isNotEmpty(temporalExtents)) {
            getTemporalExtents().addAll(temporalExtents);
        }
    }

    /**
     * Add a temporal extent
     * 
     * @param temporalExtent
     *            the temporal extent to add
     * @return this
     */
    public InspireTemporalReference addTemporalExtent(Time temporalExtent) {
        getTemporalExtents().add(temporalExtent);
        return this;
    }

    /**
     * Check if temporal extents are set
     * 
     * @return <code>true</code>, if temporal extents are set
     */
    public boolean isSetTemporalExtents() {
        return CollectionHelper.isNotEmpty(getTemporalExtents());
    }

    @Override
    public String toString() {
        return String
                .format("%s %n[%n dateOfCreation=%s,%n dateOfLastRevision=%s,%n dateOfPublication=%s,%n temporalReferences=%s%n]",
                        this.getClass().getSimpleName(), getDateOfCreation(), getDateOfLastRevision(),
                        CollectionHelper.collectionToString(getDatesOfPublication()),
                        CollectionHelper.collectionToString(getTemporalExtents()));
    }
}

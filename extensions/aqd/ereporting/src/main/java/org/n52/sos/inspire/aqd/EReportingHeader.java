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
package org.n52.sos.inspire.aqd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.aqd.AbstractEReportingHeader;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Referenceable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class EReportingHeader extends AbstractEReportingHeader {

	private static final long serialVersionUID = -552875135737898115L;
	private InspireID inspireID;
    private RelatedParty reportingAuthority;
    private EReportingChange change;
    private Referenceable<Time> reportingPeriod = Referenceable.of(Nillable.<Time>missing());
    private final List<Referenceable<AbstractFeature>> delete= new LinkedList<>();
    private final List<Referenceable<AbstractFeature>> content= new LinkedList<>();
    
    public EReportingHeader() {
    	setDefaultElementEncoding(AqdConstants.NS_AQD);
    }

    public EReportingChange getChange() {
        return change;
    }

    public EReportingHeader setChange(EReportingChange change) {
        this.change = Preconditions.checkNotNull(change);
        return this;
    }

    public InspireID getInspireID() {
        return inspireID;
    }

    public EReportingHeader setInspireID(InspireID inspireID) {
        this.inspireID = Preconditions.checkNotNull(inspireID);
        return this;
    }

    public Referenceable<Time> getReportingPeriod() {
        return reportingPeriod;
    }

    public EReportingHeader setReportingPeriod(
            Referenceable<Time> reportingPeriod) {
        this.reportingPeriod = Preconditions.checkNotNull(reportingPeriod);
        return this;
    }

    public RelatedParty getReportingAuthority() {
        return reportingAuthority;
    }

    public EReportingHeader setReportingAuthority(
            RelatedParty reportingAuthority) {
        this.reportingAuthority = Preconditions.checkNotNull(reportingAuthority);
        return this;
    }

    public List<Referenceable<AbstractFeature>> getDelete() {
        return Collections.unmodifiableList(delete);
    }

    public EReportingHeader addDelete(Referenceable<AbstractFeature> delete) {
        this.delete.add(Preconditions.checkNotNull(delete));
        return this;
    }

    public EReportingHeader addDelete(AbstractFeature delete) {
        return addDelete(Referenceable.of(delete));
    }

    public List<Referenceable<AbstractFeature>> getContent() {
        return Collections.unmodifiableList(content);
    }

    public boolean isSetContent() {
        return CollectionHelper.isNotEmpty(content);
    }

    public EReportingHeader addContent(Referenceable<AbstractFeature> content) {
        this.content.add(Preconditions.checkNotNull(content));
        return this;
    }

    public EReportingHeader addContent(AbstractFeature content) {
        return addContent(Referenceable.of(content));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInspireID(), getReportingAuthority(),
                                getChange(), getReportingPeriod(), getDelete(),
                                getContent());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EReportingHeader) {
            EReportingHeader that = (EReportingHeader) obj;
            return Objects.equal(getInspireID(), that.getInspireID()) &&
                   Objects.equal(getReportingAuthority(), that.getReportingAuthority()) &&
                   Objects.equal(getChange(), that.getChange()) &&
                   Objects.equal(getReportingPeriod(), that.getReportingPeriod()) &&
                   Objects.equal(getDelete(), that.getDelete()) &&
                   Objects.equal(getContent(), that.getContent());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("inspireID", getInspireID())
                .add("reportingAuthority", getReportingAuthority())
                .add("change", getChange())
                .add("reportingPeriod", getReportingPeriod())
                .add("delete", getDelete())
                .add("content", getContent())
                .toString();
    }



}

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

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Referenceable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ReportObligation {
    private InspireID inspireID;
    private EReportingChange change;
    private Referenceable<Time> reportingPeriod
            = Referenceable.of(Nillable.<Time>missing());

    public EReportingChange getChange() {
        return change;
    }

    public ReportObligation setChange(EReportingChange change) {
        this.change = Preconditions.checkNotNull(change);
        return this;
    }

    public boolean isSetChange() {
		return getChange() != null;
	}

	public InspireID getInspireID() {
        return inspireID;
    }

    public ReportObligation setInspireID(InspireID inspireID) {
        this.inspireID = Preconditions.checkNotNull(inspireID);
        return this;
    }

    public boolean isSetInspireID() {
		return getInspireID() != null;
	}

	public Referenceable<Time> getReportingPeriod() {
        return reportingPeriod;
    }

    public ReportObligation setReportingPeriod(
            Referenceable<Time> reportingPeriod) {
        this.reportingPeriod = Preconditions.checkNotNull(reportingPeriod);
        return this;
    }
    
    public boolean isValid() {
    	return isSetInspireID() && isSetChange(); 
    }

    @Override
    public int hashCode() {
        return Objects
                .hashCode(getInspireID(), getChange(), getReportingPeriod());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReportObligation) {
            ReportObligation that = (ReportObligation) obj;
            return Objects.equal(getInspireID(), that.getInspireID()) &&
                   Objects.equal(getChange(), that.getChange()) &&
                   Objects.equal(getReportingPeriod(), that.getReportingPeriod());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("inspireID", getInspireID())
                .add("change", getChange())
                .add("reportingPeriod", getReportingPeriod())
                .toString();
    }
}

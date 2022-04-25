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
package org.n52.sos.aquarius.pojo.location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ReferenceStandard", "DatumPeriods" })
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class LocationDatum implements Serializable {

    private static final long serialVersionUID = -1614312491511942106L;

    @JsonProperty("ReferenceStandard")
    private ReferenceStandard referenceStandard;

    @JsonProperty("DatumPeriods")
    private List<DatumPeriod> datumPeriods = new ArrayList<DatumPeriod>();

    /**
     * No args constructor for use in serialization
     *
     */
    public LocationDatum() {
    }

    public LocationDatum(ReferenceStandard referenceStandard, Collection<DatumPeriod> datumPeriods) {
        super();
        this.referenceStandard = referenceStandard;
        setDatumPeriods(getDatumPeriods());
    }

    @JsonProperty("ReferenceStandard")
    public ReferenceStandard getReferenceStandard() {
        return referenceStandard;
    }

    @JsonProperty("ReferenceStandard")
    public void setReferenceStandard(ReferenceStandard referenceStandard) {
        this.referenceStandard = referenceStandard;
    }

    @JsonProperty("DatumPeriods")
    public List<DatumPeriod> getDatumPeriods() {
        return Collections.unmodifiableList(datumPeriods);
    }

    @JsonProperty("DatumPeriods")
    public void setDatumPeriods(Collection<DatumPeriod> datumPeriods) {
        this.datumPeriods.clear();
        if (datumPeriods != null) {
            this.datumPeriods.addAll(datumPeriods);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("referenceStandard", referenceStandard)
                .append("datumPeriods", datumPeriods).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(referenceStandard).append(datumPeriods).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocationDatum)) {
            return false;
        }
        LocationDatum rhs = (LocationDatum) other;
        return new EqualsBuilder().append(referenceStandard, rhs.referenceStandard)
                .append(datumPeriods, rhs.datumPeriods).isEquals();
    }

}

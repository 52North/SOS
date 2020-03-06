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
package org.n52.sos.ds.hibernate.entities.feature.gml;

import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

/**
 * Hibernate entiity for the coordinateSystemAxis
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CoordinateSystemAxisEntity extends ReferenceEntity {

    private String remarks;
    private String axisAbbrev;
    private Codespace axisAbbrevCodespace;
    private String axisDirection;
    private Codespace axisDirectionCodespace;
    private Double minimumValue;
    private Double maximumValue;
    private String rangeMeaning;
    private Codespace rangeMeaningCodespace;
    private Unit uom;

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSetRemarks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }

    /**
     * @return the axisAbbrev
     */
    public String getAxisAbbrev() {
        return axisAbbrev;
    }

    /**
     * @param axisAbbrev
     *            the axisAbbrev to set
     */
    public void setAxisAbbrev(String axisAbbrev) {
        this.axisAbbrev = axisAbbrev;
    }

    /**
     * @return the axisAbbrevCodespace
     */
    public Codespace getCodespaceAxisAbbrev() {
        return axisAbbrevCodespace;
    }

    /**
     * @param axisAbbrevCodespace
     *            the axisAbbrevCodespace to set
     */
    public void setCodespaceAxisAbbrev(Codespace axisAbbrevCodespace) {
        this.axisAbbrevCodespace = axisAbbrevCodespace;
    }

    public boolean isSetCodespaceAxisAbbrev() {
        return getCodespaceAxisAbbrev() != null && getCodespaceAxisAbbrev().isSetCodespace();
    }

    /**
     * @return the axisDirection
     */
    public String getAxisDirection() {
        return axisDirection;
    }

    /**
     * @param axisDirection
     *            the axisDirection to set
     */
    public void setAxisDirection(String axisDirection) {
        this.axisDirection = axisDirection;
    }

    /**
     * @return the axisDirectionCodespace
     */
    public Codespace getCodespaceAxisDirection() {
        return axisDirectionCodespace;
    }

    /**
     * @param axisDirectionCodespace
     *            the axisDirectionCodespace to set
     */
    public void setCodespaceAxisDirection(Codespace axisDirectionCodespace) {
        this.axisDirectionCodespace = axisDirectionCodespace;
    }

    public boolean isSetCodespaceAxisDirection() {
        return getCodespaceAxisDirection() != null && getCodespaceAxisDirection().isSetCodespace();
    }

    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     *            the minimumValue to set
     */
    public void setMinimumValue(double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public boolean isSetMinimumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     *            the maximumValue to set
     */
    public void setMaximumValue(double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the rangeMeaning
     */
    public String getRangeMeaning() {
        return rangeMeaning;
    }

    /**
     * @param rangeMeaning
     *            the rangeMeaning to set
     */
    public void setRangeMeaning(String rangeMeaning) {
        this.rangeMeaning = rangeMeaning;
    }

    public boolean isSetRangeMeaning() {
        return !Strings.isNullOrEmpty(getRangeMeaning());
    }

    /**
     * @return the rangeMeaningCodespace
     */
    public Codespace getCodespaceRangeMeaning() {
        return rangeMeaningCodespace;
    }

    /**
     * @param rangeMeaningCodespace
     *            the rangeMeaningCodespace to set
     */
    public void setCodespaceRangeMeaning(Codespace rangeMeaningCodespace) {
        this.rangeMeaningCodespace = rangeMeaningCodespace;
    }

    public boolean isSetCodespaceRangeMeaning() {
        return getCodespaceRangeMeaning() != null && getCodespaceRangeMeaning().isSetCodespace();
    }

    /**
     * @return the uom
     */
    public Unit getUom() {
        return uom;
    }

    /**
     * @param uom
     *            the uom to set
     */
    public void setUom(Unit uom) {
        this.uom = uom;
    }
}

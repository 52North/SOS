/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

/**
 *
 * @author adewa
 */
public class CloudjsEntity {

    private String cloudjs;
    private String offering;
    
    private Double lx;
    private Double ly;
    private Double lz;
    private Double ux;
    private Double uy;
    private Double uz;
    private Integer spacing;
    private Double scale;
    private Integer hierarchystepsize;

    public String getCloudjs() {
        return cloudjs;
    }

    public void setCloudjs(String cloudjs) {
        this.cloudjs = cloudjs;
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    /**
     * @return the lx
     */
    public Double getLx() {
        return lx;
    }

    /**
     * @param lx the lx to set
     */
    public void setLx(Double lx) {
        this.lx = lx;
    }

    /**
     * @return the ly
     */
    public Double getLy() {
        return ly;
    }

    /**
     * @param ly the ly to set
     */
    public void setLy(Double ly) {
        this.ly = ly;
    }

    /**
     * @return the lz
     */
    public Double getLz() {
        return lz;
    }

    /**
     * @param lz the lz to set
     */
    public void setLz(Double lz) {
        this.lz = lz;
    }

    /**
     * @return the ux
     */
    public Double getUx() {
        return ux;
    }

    /**
     * @param ux the ux to set
     */
    public void setUx(Double ux) {
        this.ux = ux;
    }

    /**
     * @return the uy
     */
    public Double getUy() {
        return uy;
    }

    /**
     * @param uy the uy to set
     */
    public void setUy(Double uy) {
        this.uy = uy;
    }

    /**
     * @return the uz
     */
    public Double getUz() {
        return uz;
    }

    /**
     * @param uz the uz to set
     */
    public void setUz(Double uz) {
        this.uz = uz;
    }

    /**
     * @return the spacing
     */
    public Integer getSpacing() {
        return spacing;
    }

    /**
     * @param spacing the spacing to set
     */
    public void setSpacing(Integer spacing) {
        this.spacing = spacing;
    }

    /**
     * @return the scale
     */
    public Double getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(Double scale) {
        this.scale = scale;
    }

    /**
     * @return the hierarchystepsize
     */
    public Integer getHierarchystepsize() {
        return hierarchystepsize;
    }

    /**
     * @param hierarchystepsize the hierarchystepsize to set
     */
    public void setHierarchystepsize(Integer hierarchystepsize) {
        this.hierarchystepsize = hierarchystepsize;
    }

}

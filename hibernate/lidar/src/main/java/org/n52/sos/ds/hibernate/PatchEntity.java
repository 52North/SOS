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

import com.vividsolutions.jts.geom.Geometry;

public class PatchEntity {

    private long id;
    private Geometry pa;
    private String feature_id;
    private int pcid;
    private double minz;
    private double maxz;

    public PatchEntity() {

    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the pa
     */
    public Geometry getPa() {
        return pa;
    }

    /**
     * @param pa
     *            the pa to set
     */
    public void setPa(Geometry pa) {
        this.pa = pa;
    }

    /**
     * @return the feature_id
     */
    public String getFeature_id() {
        return feature_id;
    }

    /**
     * @param feature_id
     *            the feature_id to set
     */
    public void setFeature_id(String feature_id) {
        this.feature_id = feature_id;
    }

    /**
     * @return the pcid
     */
    public int getPcid() {
        return pcid;
    }

    /**
     * @param pcid the pcid to set
     */
    public void setPcid(int pcid) {
        this.pcid = pcid;
    }

    /**
     * @return the minz
     */
    public double getMinz() {
        return minz;
    }

    /**
     * @param minz the minz to set
     */
    public void setMinz(double minz) {
        this.minz = minz;
    }

    /**
     * @return the maxz
     */
    public double getMaxz() {
        return maxz;
    }

    /**
     * @param maxz the maxz to set
     */
    public void setMaxz(double maxz) {
        this.maxz = maxz;
    }

}

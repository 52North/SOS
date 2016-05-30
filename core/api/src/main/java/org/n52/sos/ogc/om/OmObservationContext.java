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
package org.n52.sos.ogc.om;

import java.util.Objects;

import org.apache.commons.pool.impl.SoftReferenceObjectPool;
import org.n52.sos.ogc.gml.ReferenceType;

/**
 * Representation of OGC O&M 2.0 ObservationContext
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class OmObservationContext {

    private ReferenceType role;
    private ReferenceType relatedObservation;
    
    /**
     * constructor
     * 
     * @param role
     *            Role of the related observation
     * @param relatedObservation
     *            Reference to related observation
     */
    public OmObservationContext(ReferenceType role, ReferenceType relatedObservation) {
        this.role = role;
        this.relatedObservation = relatedObservation;
    }
    
    /**
     * @return the role
     */
    public ReferenceType getRole() {
        return role;
    }

    /**
     * @return the relatedObservation
     */
    public ReferenceType getRelatedObservation() {
        return relatedObservation;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.role, 234, this.relatedObservation);
    }

}

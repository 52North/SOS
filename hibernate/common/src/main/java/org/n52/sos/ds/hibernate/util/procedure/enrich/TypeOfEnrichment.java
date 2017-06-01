/**
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

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import com.google.common.base.Strings;

/**
 * Enrich {@link SosProcedureDescription} with typeOf information
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class TypeOfEnrichment extends ProcedureDescriptionEnrichment {

    private String typeOfIdentifier;

    private String typeOfFormat;

    @Override
    public void enrich() throws OwsExceptionReport {
        if (isSetTypeOf()) {
            setTypeOfReferenceType(getDescription());
        }
    }
    
    @Override
    public boolean isApplicable() {
        return super.isApplicable() && isSetTypeOf();
    }

    private SosProcedureDescription setTypeOfReferenceType(SosProcedureDescription description)
            throws OwsExceptionReport {
        String href = description.createKvpDescribeSensorOrReturnIdentifier(getTypeOfIdentifier());
        description.setTypeOf(new ReferenceType(href, getTypeOfIdentifier()));
        return description;
    }

    public TypeOfEnrichment setTypeOfIdentifier(String typeOfIdentifier) {
        this.typeOfIdentifier = typeOfIdentifier;
        return this;
    }

    private String getTypeOfIdentifier() {
        return typeOfIdentifier;
    }

    public TypeOfEnrichment setTypeOfFormat(String typeOfFormat) {
        this.typeOfFormat = typeOfFormat;
        return this;
    }

    private String getTypeOfFormat() {
        return typeOfFormat;
    }

    private boolean isSetTypeOf() {
        return isSetTypeOfIdentifier() && isSetTypeOfFormat();
    }

    private boolean isSetTypeOfIdentifier() {
        return !Strings.isNullOrEmpty(getTypeOfIdentifier());
    }

    private boolean isSetTypeOfFormat() {
        return !Strings.isNullOrEmpty(getTypeOfFormat());
    }

}

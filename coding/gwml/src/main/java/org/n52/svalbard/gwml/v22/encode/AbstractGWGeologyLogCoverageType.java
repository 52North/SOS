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
package org.n52.svalbard.gwml.v22.encode;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.JavaHelper;

import net.opengis.gwmlWell.x22.GWGeologyLogCoverageType;

public abstract class AbstractGWGeologyLogCoverageType<T>
        extends AbstractGroundWaterMLEncoder<T, ProfileValue> {

    protected GWGeologyLogCoverageType encodeGWGeologyLogCoverage(ProfileValue gwGeologyLogCoverage)
            throws OwsExceptionReport {
        GWGeologyLogCoverageType gwglct = GWGeologyLogCoverageType.Factory.newInstance();
        setGmlId(gwglct, gwGeologyLogCoverage);
        setIdentifier(gwglct, gwGeologyLogCoverage);
        setDescription(gwglct, gwGeologyLogCoverage);
        setNames(gwglct, gwGeologyLogCoverage);
        setElements(gwglct, gwGeologyLogCoverage);
        return gwglct;
    }

    private void setGmlId(GWGeologyLogCoverageType gwglct, ProfileValue gwGeologyLogCoverage) {
        if (!gwGeologyLogCoverage.isSetGmlID()) {
            gwGeologyLogCoverage.setGmlId(JavaHelper.generateID(Double.toString(System.currentTimeMillis()
                  * Math.random())));
        }
        gwGeologyLogCoverage.setGmlId("gwglc_" + gwGeologyLogCoverage.getGmlId());
        gwglct.setId(gwGeologyLogCoverage.getGmlId());
    }

    private void setIdentifier(GWGeologyLogCoverageType gwglct, ProfileValue gwGeologyLogCoverage)
            throws OwsExceptionReport {
        if (gwGeologyLogCoverage.isSetIdentifier()) {
            gwglct.addNewIdentifier().set(encodeGML(gwGeologyLogCoverage.getIdentifierCodeWithAuthority()));
        }
    }

    private void setDescription(GWGeologyLogCoverageType gwglct, ProfileValue gwGeologyLogCoverage) {
        if (gwGeologyLogCoverage.isSetDescription()) {
            gwglct.addNewDescription().setStringValue(gwGeologyLogCoverage.getDescription());
        }
    }

    private void setNames(GWGeologyLogCoverageType gwglct, ProfileValue gwGeologyLogCoverage)
            throws OwsExceptionReport {
        if (gwGeologyLogCoverage.isSetName()) {
            for (CodeType name : gwGeologyLogCoverage.getName()) {
                gwglct.addNewName().set(encodeGML(name));
            }
        }
    }

    private void setElements(GWGeologyLogCoverageType gwglct, ProfileValue gwGeologyLogCoverage)
            throws OwsExceptionReport {
        if (gwGeologyLogCoverage.isSetValue()) {
            for (ProfileLevel logValue : gwGeologyLogCoverage.getValue()) {
                gwglct.addNewElement().set(encodeGWMLProperty(logValue));
            }
        }
    }
}

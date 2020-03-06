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
package org.n52.sos.ds.hibernate.entities.parameter.observation;

import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.observation.BaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ProfileValuedObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.ValuedParameterVisitor;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class ParameterAdder {
    
    private OmObservation observation;
    private BaseObservation hObservation;

    public ParameterAdder(OmObservation observation, BaseObservation hObservation) {
        this.observation = observation;
        this.hObservation = hObservation;
    }

    public void add() throws OwsExceptionReport {
        if (hObservation.hasParameters()) {
            for (Parameter parameter : hObservation.getParameters()) {
                observation.addParameter(parameter.accept(new ValuedParameterVisitor()));
            }
        }
        if (!(hObservation instanceof ProfileObservation || hObservation instanceof ProfileValuedObservation) && hObservation.hasVerticalFrom() || hObservation.hasVerticalTo()) {
            if (hObservation.hasVerticalFrom()) {
                observation.addParameter(getVerticalFrom(hObservation));
            }
            if (hObservation.hasVerticalTo()) {
                observation.addParameter(getVerticalTo(hObservation));
            }
            
        }
    }

    private NamedValue<?> getVerticalFrom(BaseObservation hObservation) {
        NamedValue<Double> namedValue = new NamedValue<>();
        if (hObservation.hasVerticalFromName()) {
            addName(namedValue, hObservation.getVerticalFromName());
        } else {
            addName(namedValue, "from");
        }
        namedValue.setValue(new QuantityValue(hObservation.getVerticalFrom()));
        if (hObservation.hasVerticalUnit()) {
            addUnit(hObservation.getVerticalUnit(), namedValue.getValue());
        }
        return namedValue;
    }

    private NamedValue<?> getVerticalTo(BaseObservation hObservation) {
        NamedValue<Double> namedValue = new NamedValue<>();
        if (hObservation.hasVerticalToName()) {
            addName(namedValue, hObservation.getVerticalToName());
        } else {
            addName(namedValue, "to");
        }
        namedValue.setValue(new QuantityValue(hObservation.getVerticalTo()));
        if (hObservation.hasVerticalUnit()) {
            addUnit(hObservation.getVerticalUnit(), namedValue.getValue());
        }
        return namedValue;
    }

    private void addUnit(Unit unit, Value<?> v) {
        if (!v.isSetUnit() && unit instanceof HasUnit && ((HasUnit)unit).isSetUnit()) {
            Unit u = ((HasUnit)unit).getUnit();
            UoM uom = new UoM(unit.getUnit());
            if (u.isSetName()) {
                u.setName(unit.getName());
            }
            if (u.isSetLink()) {
                uom.setLink(u.getLink());
            }
            v.setUnit(uom);
        }
    }

    private NamedValue<?> addName(NamedValue<?> namedValue, String name) {
        ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        return namedValue;
    }

}

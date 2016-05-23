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
package org.n52.sos.util.builder;

import java.util.ArrayList;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.sos.SosProcedureDescription;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ObservationConstellationBuilder {

    public static ObservationConstellationBuilder anObservationConstellation() {
        return new ObservationConstellationBuilder();
    }

    private AbstractFeature featureOfInterest;

    private SosProcedureDescription procedure;

    private String observationType;

    private OmObservableProperty observableProperty;

    private ArrayList<String> offerings;

    public ObservationConstellationBuilder setFeature(AbstractFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    public ObservationConstellationBuilder setProcedure(SosProcedureDescription procedure) {
        this.procedure = procedure;
        return this;
    }

    public ObservationConstellationBuilder setObservationType(String observationType) {
        this.observationType = observationType;
        return this;
    }

    public ObservationConstellationBuilder setObservableProperty(OmObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    public ObservationConstellationBuilder addOffering(String offeringIdentifier) {
        if (offeringIdentifier != null && !offeringIdentifier.isEmpty()) {
            if (offerings == null) {
                offerings = new ArrayList<String>();
            }
            offerings.add(offeringIdentifier);
        }
        return this;
    }

    public OmObservationConstellation build() {
        OmObservationConstellation sosObservationConstellation = new OmObservationConstellation();
        sosObservationConstellation.setFeatureOfInterest(featureOfInterest);
        sosObservationConstellation.setObservableProperty(observableProperty);
        sosObservationConstellation.setObservationType(observationType);
        sosObservationConstellation.setProcedure(procedure);
        if (offerings != null && !offerings.isEmpty()) {
            sosObservationConstellation.setOfferings(offerings);
        }
        return sosObservationConstellation;
    }

}

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

import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ObservationBuilder {

    public static ObservationBuilder anObservation() {
        return new ObservationBuilder();
    }

    private OmObservationConstellation observationConstellation;

    private ObservationValue<?> value;

    private String identifer;

    private String identiferCodespace;

    public ObservationBuilder setObservationConstellation(OmObservationConstellation observationConstellation) {
        this.observationConstellation = observationConstellation;
        return this;
    }

    public OmObservation build() {
        OmObservation sosObservation = new OmObservation();
        if (observationConstellation != null) {
            sosObservation.setObservationConstellation(observationConstellation);
        }
        if (value != null) {
            sosObservation.setValue(value);
        }
        if (identifer != null && identiferCodespace != null) {
            sosObservation.setIdentifier(new CodeWithAuthority(identifer, identiferCodespace));
        }
        return sosObservation;
    }

    public ObservationBuilder setValue(ObservationValue<?> observationValue) {
        this.value = observationValue;
        return this;
    }

    public ObservationBuilder setIdentifier(String identifierCodeSpace, String identifier) {
        this.identifer = identifier;
        this.identiferCodespace = identifierCodeSpace;
        return this;
    }

}

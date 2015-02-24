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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IdentificationEnrichment extends SensorMLEnrichment {
    @Override
    protected void enrich(AbstractSensorML description) throws OwsExceptionReport {
        enrichUniqueId(description);
        enrichShortName(description);
        enrichLongName(description);
    }

    private void enrichUniqueId(AbstractSensorML description) {
        if (!description.findIdentification(uniqueIdPredicate()).isPresent()) {
            description.addIdentifier(createUniqueId());
        }
    }

    private void enrichShortName(AbstractSensorML description) {
        if (!description.findIdentification(shortNamePredicate()).isPresent()) {
            description.addIdentifier(createShortName());
        }
    }

    private void enrichLongName(AbstractSensorML description) {
        if (!description.findIdentification(longNamePredicate()).isPresent()) {
            description.addIdentifier(createLongName());
        }
    }

    private SmlIdentifier createUniqueId() {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                                 OGCConstants.URN_UNIQUE_IDENTIFIER,
                                 getIdentifier());
    }

    private SmlIdentifier createLongName() {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                                 procedureSettings().getIdentifierLongNameDefinition(),
                                 getIdentifier());
    }

    private SmlIdentifier createShortName() {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                                 procedureSettings().getIdentifierShortNameDefinition(),
                                 getIdentifier());
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithDiscoveryInformation();
    }

}

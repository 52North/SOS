/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.i18n.AbstractFeatureI18NDAO;
import org.n52.sos.ds.hibernate.entities.i18n.I18NProcedure;
import org.n52.sos.i18n.I18NProcedureObject;
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
    
    private I18NProcedure i18NProcedure = null; 
    
    @Override
    protected void enrich(AbstractSensorML description) throws OwsExceptionReport {
        enrichUniqueId(description);
        if (isSetI18N()) {
            queryI18NProcedure();
        }
        enrichShortName(description);
        enrichLongName(description);
    }

    private void enrichUniqueId(AbstractSensorML description) {
        if (procedureSettings().isEnrichWithDiscoveryInformation() && !description.findIdentification(uniqueIdPredicate()).isPresent()) {
            description.addIdentifier(createUniqueId());
        }
    }

    private void enrichShortName(AbstractSensorML description) {
        if (!description.findIdentification(shortNamePredicate()).isPresent()) {
            description.addIdentifier(createShortName());
        }
        if (isSetI18NProcedure() && description.findIdentification(shortNamePredicate()).isPresent()) {
            SmlIdentifier smlIdentifier = description.findIdentification(shortNamePredicate()).get();
            if (i18NProcedure.isSetShortname()) {
                smlIdentifier.setValue(i18NProcedure.getShortname());
            }
        }
    }

    private void enrichLongName(AbstractSensorML description) {
        if (!description.findIdentification(longNamePredicate()).isPresent()) {
            description.addIdentifier(createLongName());
        }
        if (isSetI18NProcedure() && description.findIdentification(longNamePredicate()).isPresent()) {
            SmlIdentifier smlIdentifier = description.findIdentification(longNamePredicate()).get();
            if (i18NProcedure.isSetLongname()) {
                smlIdentifier.setValue(i18NProcedure.getLongname());
            }
        }
    }

    private SmlIdentifier createUniqueId() {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                                 OGCConstants.URN_UNIQUE_IDENTIFIER,
                                 getIdentifier());
    }

    private SmlIdentifier createLongName() {
        return createLongName(getIdentifier());
    }
    
    private SmlIdentifier createLongName(String longName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                                 procedureSettings().getIdentifierLongNameDefinition(),
                                 longName);
    }

    private SmlIdentifier createShortName() {
        return createShortName(getIdentifier());
    }
    
    private SmlIdentifier createShortName(String shortName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                                 procedureSettings().getIdentifierShortNameDefinition(),
                                 shortName);
    }
    
    private void queryI18NProcedure() {
        AbstractFeatureI18NDAO i18ndao = DaoFactory.getInstance().getI18NDAO(I18NProcedureObject.class, getSession());
        if (i18ndao != null) {
         i18NProcedure =  (I18NProcedure)i18ndao.getObject(getIdentifier(), getI18N(), getSession());
        }
    }

    private boolean isSetI18NProcedure() {
        return i18NProcedure != null;
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && (procedureSettings().isEnrichWithDiscoveryInformation() || isSetI18N());
    }

}

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

import org.n52.sos.ds.I18NDAO;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.metadata.I18NProcedureMetadata;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;

import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IdentificationEnrichment extends SensorMLEnrichment {

    private I18NProcedureMetadata i18n = null;

    @Override
    protected void enrich(AbstractSensorML description) throws OwsExceptionReport {
        enrichUniqueId(description);

        if (isSetLocale()) {
            I18NDAO<I18NProcedureMetadata> dao
                    = I18NDAORepository.getInstance().getDAO(I18NProcedureMetadata.class);
            if (dao != null) {
                i18n = dao.getMetadata(getIdentifier());
            }
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
        Optional<SmlIdentifier> shortName = description.findIdentification(shortNamePredicate());
        if (isSetI18NProcedure() && shortName.isPresent()) {
            SmlIdentifier smlIdentifier = shortName.get();
            Optional<LocalizedString> localization
                    = i18n.getShortName().getLocalizationOrDefault(getLocale());
            if (localization.isPresent()) {
                smlIdentifier.setValue(localization.get().getText());
            }
        }
    }

    private void enrichLongName(AbstractSensorML description) {
        if (!description.findIdentification(longNamePredicate()).isPresent()) {
            description.addIdentifier(createLongName());
        }
        Optional<SmlIdentifier> longName = description.findIdentification(longNamePredicate());
        if (isSetI18NProcedure() && longName.isPresent()) {
            SmlIdentifier smlIdentifier = longName.get();
            Optional<LocalizedString> localization
                    = i18n.getLongName().getLocalizationOrDefault(getLocale());
            if (localization.isPresent()) {
                smlIdentifier.setValue(localization.get().getText());
            }
        }
    }

    private String getLongName() {
        if (isSetI18NProcedure()) {
            Optional<LocalizedString> longName = i18n.getLongName()
                    .getLocalizationOrDefault(getLocale());
            if (longName.isPresent()) {
                return longName.get().getText();
            }
        }
        return getIdentifier();
    }

    private String getShortName() {
        if (isSetI18NProcedure()) {
            Optional<LocalizedString> longName = i18n.getShortName()
                    .getLocalizationOrDefault(getLocale());
            if (longName.isPresent()) {
                return longName.get().getText();
            }
        }
        return getIdentifier();
    }

    private SmlIdentifier createUniqueId() {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                                 OGCConstants.URN_UNIQUE_IDENTIFIER,
                                 getIdentifier());
    }

    private SmlIdentifier createLongName() {
        return createLongName(getLongName());
    }

    private SmlIdentifier createLongName(String longName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                                 procedureSettings().getIdentifierLongNameDefinition(),
                                 longName);
    }

    private SmlIdentifier createShortName() {
        return createShortName(getShortName());
    }

    private SmlIdentifier createShortName(String shortName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                                 procedureSettings().getIdentifierShortNameDefinition(),
                                 shortName);
    }

    private boolean isSetI18NProcedure() {
        return i18n != null;
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && (procedureSettings().isEnrichWithDiscoveryInformation() || isSetLocale());
    }

}

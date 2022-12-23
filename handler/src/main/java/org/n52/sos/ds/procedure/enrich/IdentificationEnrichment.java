/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure.enrich;

import java.util.Locale;
import java.util.Optional;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.series.db.beans.i18n.I18nProcedureEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class IdentificationEnrichment extends SensorMLEnrichment {

    private I18nProcedureEntity i18nEntity;

    public IdentificationEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    @Override
    protected void enrich(AbstractSensorML description) throws OwsExceptionReport {
        enrichUniqueId(description);
        if (isSetLocale() && getProcedure().hasTranslations()) {
            for (Locale locale : LocaleHelper.getEquivalents(getLocale())) {
                I18nEntity entity = getProcedure().getTranslation(LocaleHelper.encode(locale));
                if (entity != null && entity instanceof I18nProcedureEntity) {
                    this.i18nEntity = (I18nProcedureEntity) entity;
                    break;
                }
            }
        }
        enrichShortName(description);
        enrichLongName(description);
    }

    private void enrichUniqueId(AbstractSensorML description) {
        if (procedureSettings().isEnrichWithDiscoveryInformation()
                && !description.findIdentification(uniqueIdPredicate()).isPresent()) {
            description.addIdentifier(createUniqueId());
        }
    }

    private void enrichShortName(AbstractSensorML description) {
        Optional<SmlIdentifier> shortName = description.findIdentification(shortNamePredicate());
        if (!shortName.isPresent()) {
            description.addIdentifier(createShortName());
        }
    }

    private void enrichLongName(AbstractSensorML description) {
        Optional<SmlIdentifier> longName = description.findIdentification(longNamePredicate());
        if (!longName.isPresent()) {
            description.addIdentifier(createLongName());
        }
    }

    private I18nProcedureEntity getI18NEntity() {
        return i18nEntity;
    }

    private boolean isSetI18NEntity() {
        return getI18NEntity() != null;
    }

    private String getLongName() {
        if (isSetI18NEntity() && getI18NEntity().hasLongName()) {
            return getI18NEntity().getLongName();
        }
        return getName() != null && !getName().isEmpty() ? getName() : getIdentifier();
    }

    private String getShortName() {
        if (isSetI18NEntity() && getI18NEntity().hasShortName()) {
            return getI18NEntity().getShortName();
        }
        return getName() != null && !getName().isEmpty() ? getName() : getIdentifier();
    }

    private SmlIdentifier createUniqueId() {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END, OGCConstants.URN_UNIQUE_IDENTIFIER,
                getIdentifier());
    }

    private SmlIdentifier createLongName() {
        return createLongName(getLongName());
    }

    private SmlIdentifier createLongName(String longName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                procedureSettings().getIdentifierLongNameDefinition(), longName);
    }

    private SmlIdentifier createShortName() {
        return createShortName(getShortName());
    }

    private SmlIdentifier createShortName(String shortName) {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                procedureSettings().getIdentifierShortNameDefinition(), shortName);
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable()
                && (procedureSettings().isEnrichWithDiscoveryInformation() || isNotDefaultLocale());
    }

    private boolean isNotDefaultLocale() {
        return isSetLocale() && !getLocale().equals(getDefaultLocale());
    }

    private Locale getDefaultLocale() {
        return getProcedureCreationContext().getDefaultLocale();
    }

}

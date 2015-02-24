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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ProcedureDescriptionSettings;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public abstract class ProcedureDescriptionEnrichment {
    private static final IsApplicable IS_APPLICABLE = new IsApplicable();
    private SosProcedureDescription description;
    private String version;
    private String identifier;

    protected ProcedureDescriptionSettings procedureSettings() {
        return ProcedureDescriptionSettings.getInstance();
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected Collection<SosOffering> getSosOfferings() {
        Collection<String> identifiers = getCache()
                .getOfferingsForProcedure(getIdentifier());
        Collection<SosOffering> offerings = Lists
                .newArrayListWithCapacity(identifiers.size());
        for (String offering : identifiers) {
            offerings.add(new SosOffering(offering, getCache()
                    .getNameForOffering(offering)));
        }
        return offerings;
    }

    public SosProcedureDescription getDescription() {
        return description;
    }

    public ProcedureDescriptionEnrichment setDescription(SosProcedureDescription description) {
        this.description = checkNotNull(description);
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ProcedureDescriptionEnrichment setVersion(String version) {
        this.version = checkNotNull(version);
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ProcedureDescriptionEnrichment setIdentifier(String identifier) {
        this.identifier = checkNotNull(identifier);
        return this;
    }

    public boolean isApplicable() {
        return true;
    }

    public abstract void enrich() throws OwsExceptionReport;

    public static Predicate<ProcedureDescriptionEnrichment> predicate() {
        return IS_APPLICABLE;
    }

    private static class IsApplicable implements
            Predicate<ProcedureDescriptionEnrichment> {
        @Override
        public boolean apply(ProcedureDescriptionEnrichment input) {
            return input.isApplicable();
        }
    }
}

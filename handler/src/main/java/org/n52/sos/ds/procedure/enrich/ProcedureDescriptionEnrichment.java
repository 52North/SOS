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

import java.util.Collection;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.I18NHelper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public abstract class ProcedureDescriptionEnrichment implements I18NHelper {

    private static final IsApplicable IS_APPLICABLE = new IsApplicable();
    private SosProcedureDescription<?> description;
    private String identifier;
    private String name;
    private Locale locale;
    private Session session;
    private final AbstractProcedureCreationContext ctx;
    private String version;

    public ProcedureDescriptionEnrichment(AbstractProcedureCreationContext ctx) {
        this.ctx = ctx;
    }

    @VisibleForTesting
    public ProcedureDescriptionSettings procedureSettings() {
        return getProcedureCreationContext().getProcedureSettings();
    }

    protected SosContentCache getCache() {
        return getProcedureCreationContext().getCache();
    }

    @VisibleForTesting
    public Collection<SosOffering> getSosOfferings()
            throws CodedException {

        Collection<String> identifiers = getCache().getOfferingsForProcedure(getIdentifier());
        Collection<SosOffering> offerings = Lists.newArrayListWithCapacity(identifiers.size());
        for (String offering : identifiers) {
            SosOffering sosOffering = new SosOffering(offering, false);
            // add offering name
            addOfferingNames(getCache(), sosOffering, getLocale(), getLocale(),
                    getProcedureCreationContext().isShowAllLanguageValues());
            // add offering description
            addOfferingDescription(sosOffering, getLocale(), getLocale(), getCache());
            // add to list
            offerings.add(sosOffering);
        }
        return offerings;
    }

    public SosProcedureDescription<?> getDescription() {
        return description;
    }

    public ProcedureDescriptionEnrichment setDescription(SosProcedureDescription<?> description) {
        this.description = Preconditions.checkNotNull(description);
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ProcedureDescriptionEnrichment setVersion(String version) {
        this.version = Preconditions.checkNotNull(version);
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ProcedureDescriptionEnrichment setIdentifier(String identifier) {
        this.identifier = Preconditions.checkNotNull(identifier);
        return this;
    }

    public String getName() {
        return name;
    }

    public ProcedureDescriptionEnrichment setName(String name) {
        this.name = name;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isSetLocale() {
        return getLocale() != null;
    }

    public ProcedureDescriptionEnrichment setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public AbstractProcedureCreationContext getProcedureCreationContext() {
        return ctx;
    }

    public ProcedureDescriptionEnrichment setSession(Session session) {
        this.session = Preconditions.checkNotNull(session);
        return this;
    }

    public Session getSession() {
        return session;
    }

    public boolean isApplicable() {
        return true;
    }

    public abstract void enrich()
            throws OwsExceptionReport;

    public static Predicate<ProcedureDescriptionEnrichment> predicate() {
        return IS_APPLICABLE;
    }

    private static class IsApplicable
            implements
            Predicate<ProcedureDescriptionEnrichment> {
        @Override
        public boolean apply(ProcedureDescriptionEnrichment input) {
            return input != null && input.isApplicable();
        }
    }
}

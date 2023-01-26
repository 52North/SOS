/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import org.hibernate.Session;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.procedure.AbstractProcedureConverter;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractProcedureDescriptionEnrichments<T> {

    private T procedure;

    private String identifier;

    private String name;

    private SosProcedureDescription<?> description;

    private String version;

    private String procedureDescriptionFormat;

    private Session session;

    private AbstractProcedureConverter<T> converter;

    private TimePeriod validTime;

    private final Locale locale;

    private final LocalizedProducer<OwsServiceProvider> serviceProvider;

    private String typeOfIdentifier;

    private String typeOfFormat;

    private AbstractProcedureCreationContext ctx;

    public AbstractProcedureDescriptionEnrichments(Locale locale,
            LocalizedProducer<OwsServiceProvider> serviceProvider, AbstractProcedureCreationContext ctx) {
        this.serviceProvider = serviceProvider;
        this.locale = locale;
        this.ctx = ctx;
    }

    public abstract AbstractRelatedProceduresEnrichment createRelatedProceduresEnrichment();

    public abstract SensorMLEnrichment createIdentificationEnrichment();

    public AbstractProcedureDescriptionEnrichments<T> setProcedure(T procedure) {
        this.procedure = procedure;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }


    public AbstractProcedureDescriptionEnrichments<T> setName(String name) {
        this.name = name;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setDescription(SosProcedureDescription<?> description) {
        this.description = description;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setVersion(String version) {
        this.version = version;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setProcedureDescriptionFormat(String pdf) {
        this.procedureDescriptionFormat = pdf;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setSession(Session session) {
        this.session = session;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setConverter(AbstractProcedureConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setTypeOfIdentifier(String typeOfIdentifier) {
        this.typeOfIdentifier = typeOfIdentifier;
        return this;
    }

    public AbstractProcedureDescriptionEnrichments<T> setTypeOfFormat(String typeOfFormat) {
        this.typeOfFormat = typeOfFormat;
        return this;
    }

    public T getProcedure() {
        return procedure;
    }

    public TimePeriod getValidTime() {
        return validTime;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public AbstractProcedureConverter<T> getConverter() {
        return (AbstractProcedureConverter<T>) converter;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isSetLocale() {
        return getLocale() != null;
    }

    public SosProcedureDescription<?> getDescription() {
        return description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public LocalizedProducer<OwsServiceProvider> getServiceProvider() {
        return serviceProvider;
    }

    public GeometryHandler getGeometryHandler() {
        return ctx.getGeometryHandler();
    }

    public Session getSession() {
        return session;
    }

    public String getVersion() {
        return version;
    }

    public AbstractProcedureCreationContext getProcedureCreationContext() {
        return ctx;
    }

    public Iterable<ProcedureDescriptionEnrichment> createAll() {
        return Iterables.filter(
                Lists.newArrayList(createFeatureOfInterestEnrichment(), createRelatedProceduresEnrichment(),
                        createValidTimeEnrichment(), createOfferingEnrichment(), createBoundingBoxEnrichment(),
                        createClassifierEnrichment(), createIdentificationEnrichment(), createContactsEnrichment(),
                        createKeywordEnrichment(), createObservablePropertyEnrichment(),
                        createTypeOfEnrichmentEnrichment()),
                ProcedureDescriptionEnrichment.predicate());
    }

    public void enrichAll() throws OwsExceptionReport {
        for (ProcedureDescriptionEnrichment enrichment : createAll()) {
            enrichment.enrich();
        }
    }

    public BoundingBoxEnrichment createBoundingBoxEnrichment() {
        return setValues(new BoundingBoxEnrichment(ctx));
    }

    public ClassifierEnrichment createClassifierEnrichment() {
        return setValues(new ClassifierEnrichment(ctx));
    }

    public ContactsEnrichment createContactsEnrichment() {
        return setValues(new ContactsEnrichment(this.serviceProvider, ctx));
    }

    public KeywordEnrichment createKeywordEnrichment() {
        return setValues(new KeywordEnrichment(ctx));
    }

    public FeatureOfInterestEnrichment createFeatureOfInterestEnrichment() {
        return setValues(new FeatureOfInterestEnrichment(ctx));
    }

    public ValidTimeEnrichment createValidTimeEnrichment() {
        return setValues(new ValidTimeEnrichment(ctx)).setValidTime(validTime);
    }

    public OfferingEnrichment createOfferingEnrichment() {
        return setValues(new OfferingEnrichment(ctx));
    }

    public ObservablePropertyEnrichment createObservablePropertyEnrichment() {
        return setValues(new ObservablePropertyEnrichment(ctx));
    }

    public TypeOfEnrichment createTypeOfEnrichmentEnrichment() {
        return setValues(new TypeOfEnrichment(ctx)).setTypeOfIdentifier(typeOfIdentifier)
                .setTypeOfFormat(typeOfFormat);
    }

    protected <S extends ProcedureDescriptionEnrichment> S setValues(S enrichment) {
        enrichment.setDescription(description).setIdentifier(identifier).setName(name).setVersion(version)
                .setLocale(locale).setSession(session);
        return enrichment;
    }
}

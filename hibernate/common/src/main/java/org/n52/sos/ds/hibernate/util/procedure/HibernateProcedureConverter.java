/*
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
package org.n52.sos.ds.hibernate.util.procedure;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.convert.Converter;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.HibernateRelations.HasProcedureDescriptionFormat;
import org.n52.series.db.beans.HibernateRelations.HasXml;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.util.procedure.create.DescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.FileDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.GeneratedDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.LinkedDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.ValidProcedureTimeDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.XmlStringDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.enrich.ProcedureDescriptionEnrichments;
import org.n52.sos.ds.procedure.AbstractProcedureConverter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike
 *         HinderkJ&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 *
 *        TODO - apply description enrichment to all types of procedures
 *        (creates, file, or database) - use setting switches for code flow
 */
public class HibernateProcedureConverter extends AbstractProcedureConverter<ProcedureEntity>
        implements HibernateSqlQueryConstants {

    private final HibernateProcedureCreationContext ctx;

    private LocalizedProducer<OwsServiceProvider> serviceProvider;

    @Inject
    public HibernateProcedureConverter(HibernateProcedureCreationContext ctx) {
        this.ctx = ctx;
        this.serviceProvider = ctx.getServiceMetadataRepository().getServiceProviderFactory(SosConstants.SOS);
    }

    protected ConverterRepository getConverterRepository() {
        return ctx.getConverterRepository();
    }

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            Requested procedure descriptionFormat
     * @param version
     *            Requested SOS version
     * @param i18n
     *            Requested language
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<?> createSosProcedureDescription(ProcedureEntity procedure,
            String requestedDescriptionFormat, String version, Locale i18n, Session session)
            throws OwsExceptionReport {
        if (procedure == null) {
            throw new NoApplicableCodeException()
                    .causedBy(new IllegalArgumentException("Parameter 'procedure' should not be null!"))
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
        if (procedure.hasProcedureHistory()) {
            return createSosProcedureDescriptionFromValidProcedureTime(procedure, requestedDescriptionFormat,
                    procedure.getProcedureHistory().stream().filter(h -> h.getEndTime() == null).findFirst().get(),
                    version, i18n, session);
        }
        checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), procedure, requestedDescriptionFormat,
                getFormat(procedure));
        SosProcedureDescription<?> desc = create(procedure, requestedDescriptionFormat, null, i18n, session).orNull();
        if (desc != null) {
            addHumanReadableName(desc, procedure);
            enrich(desc, procedure, version, requestedDescriptionFormat, null, i18n, session);
            if (!requestedDescriptionFormat.equals(desc.getDescriptionFormat())) {
                desc = convert(desc.getDescriptionFormat(), requestedDescriptionFormat, desc);
                desc.setDescriptionFormat(requestedDescriptionFormat);
            }
        }
        return desc;
    }

    /**
     * Create procedure description from XML text stored in ValidProcedureTime
     * table
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            the requested procedure description format
     * @param vpt
     *            Hibernate ValidProcedureTime entity
     * @param version
     *            Requested SOS version
     * @param i18n
     *            the requested locale
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<?> createSosProcedureDescriptionFromValidProcedureTime(ProcedureEntity procedure,
            String requestedDescriptionFormat, ProcedureHistoryEntity vpt, String version, Locale i18n,
            Session session) throws OwsExceptionReport {
        if (vpt != null) {
            checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), vpt, requestedDescriptionFormat,
                    getFormat(vpt));
        } else {
            checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), procedure, requestedDescriptionFormat,
                    getFormat(procedure));
        }
        Optional<SosProcedureDescription<?>> description =
                create(procedure, requestedDescriptionFormat, vpt, i18n, session);
        if (description.isPresent()) {
            if (!requestedDescriptionFormat.equals(description.get().getDescriptionFormat())) {
                SosProcedureDescription<?> converted = convert(description.get().getDescriptionFormat(),
                        requestedDescriptionFormat, description.get());
                converted.setDescriptionFormat(requestedDescriptionFormat);
                description = Optional.of(converted);
            }
            addHumanReadableName(description.get(), procedure);
            enrich(description.get(), procedure, version, requestedDescriptionFormat, getValidTime(vpt), i18n,
                    session);
        }
        return description.orNull();
    }

    private void addHumanReadableName(SosProcedureDescription<?> desc, ProcedureEntity procedure) {
        if (!desc.isSetHumanReadableIdentifier() && procedure.isSetName()) {
            desc.setHumanReadableIdentifier(procedure.getName());
        }
    }

    protected TimePeriod getValidTime(ProcedureHistoryEntity validProcedureTime) {
        return new TimePeriod(validProcedureTime.getStartTime(), validProcedureTime.getEndTime());
    }

    private String getFormat(HasProcedureDescriptionFormat hpdf) {
        if (hpdf.getFormat() != null && hpdf.getFormat().isSetFormat()) {
            return hpdf.getFormat().getFormat();
        }
        return null;
    }

    /**
     * Checks the requested procedureDescriptionFormat with the datasource
     * procedureDescriptionFormat.
     *
     * @param identifier
     *            the procedure identifier
     * @param procedure
     *            the procedure
     * @param requestedFormat
     *            requested procedureDescriptionFormat
     * @param descriptionFormat
     *            Data source procedureDescriptionFormat
     *
     * @return if the output format is compatible with the source format
     *
     * @throws OwsExceptionReport
     *             If procedureDescriptionFormats are invalid
     */
    @VisibleForTesting
    boolean checkOutputFormatWithDescriptionFormat(String identifier, HasProcedureDescriptionFormat procedure,
            String requestedFormat, String descriptionFormat) throws OwsExceptionReport {
        if ((procedure instanceof HasXml && descriptionFormat != null && ((HasXml) procedure).isSetXml())
                || checkForDescriptionFile(procedure)) {
            if (requestedFormat.equalsIgnoreCase(descriptionFormat)
                    || existConverter(descriptionFormat, requestedFormat)) {
                return true;
            }
        } else {
            if (existsGenerator(requestedFormat)) {
                return true;
            }
        }
        throw new InvalidParameterValueException().at(SosConstants.DescribeSensorParams.procedure)
                .withMessage("The value of the output format is wrong and has to be %s for procedure %s",
                        descriptionFormat, identifier)
                .setStatus(HTTPStatus.BAD_REQUEST);
    }

    private boolean checkForDescriptionFile(HasProcedureDescriptionFormat procedure) {
        if (procedure instanceof ProcedureEntity) {
            return ((ProcedureEntity) procedure).isSetDescription();
        }
        return false;
    }

    private boolean existConverter(String from, String to) {
        return getConverterRepository().hasConverter(from, to);
    }

    private boolean existsGenerator(String descriptionFormat) {
        return ctx.getFactoryRepository().hasProcedureDescriptionGeneratorFactory(descriptionFormat);
    }

    private Optional<SosProcedureDescription<?>> create(ProcedureEntity procedure, String descriptionFormat,
            ProcedureHistoryEntity vpt, Locale i18n, Session session) throws OwsExceptionReport {
        Optional<DescriptionCreationStrategy> strategy = getCreationStrategy(procedure, vpt);
        if (strategy.isPresent()) {
            return Optional.fromNullable(strategy.get().create(procedure, descriptionFormat, i18n, ctx, session));
        } else {
            return Optional.absent();
        }
    }

    private Optional<DescriptionCreationStrategy> getCreationStrategy(ProcedureEntity p, ProcedureHistoryEntity vpt) {
        for (DescriptionCreationStrategy strategy : getCreationStrategies(vpt)) {
            if (strategy.apply(p)) {
                return Optional.of(strategy);
            }
        }
        return Optional.absent();
    }

    protected List<DescriptionCreationStrategy> getCreationStrategies(ProcedureHistoryEntity vpt) {
        return Lists.newArrayList(new ValidProcedureTimeDescriptionCreationStrategy(vpt),
                new LinkedDescriptionCreationStrategy(), new XmlStringDescriptionCreationStrategy(),
                new FileDescriptionCreationStrategy(), new GeneratedDescriptionCreationStrategy());
    }

    /**
     * Enrich the procedure description.
     *
     * @param desc
     *            the description
     * @param procedure
     *            the procedure
     * @param version
     *            the version
     * @param format
     *            the format
     * @param validTime
     *            the valid time
     * @param language
     *            the language
     * @param session
     *            the session
     *
     * @throws OwsExceptionReport
     *             if the enrichment fails
     */
    private void enrich(SosProcedureDescription<?> desc, ProcedureEntity procedure, String version, String format,
            TimePeriod validTime, Locale language, Session session) throws OwsExceptionReport {
        ProcedureDescriptionEnrichments enrichments =
                new ProcedureDescriptionEnrichments(language, serviceProvider, ctx);
        enrichments.setIdentifier(procedure.getIdentifier()).setProcedure(procedure).setVersion(version)
                .setDescription(desc).setProcedureDescriptionFormat(format).setSession(session).setValidTime(validTime)
                .setConverter(this);
        if (procedure.isSetName()) {
            enrichments.setName(procedure.getName());
        }
        if (procedure.isSetTypeOf() && desc.getProcedureDescription() instanceof AbstractProcessV20) {
            ProcedureEntity typeOf = procedure.getTypeOf();
            enrichments.setTypeOfIdentifier(typeOf.getIdentifier()).setTypeOfFormat(format);
        }
        if (desc.getProcedureDescription() instanceof SensorML
                && ((SensorML) desc.getProcedureDescription()).isWrapper()) {
            enrichments.setDescription(desc).createValidTimeEnrichment().enrich();
            for (AbstractProcess abstractProcess : ((SensorML) desc.getProcedureDescription()).getMembers()) {
                SosProcedureDescription<AbstractProcess> sosProcedureDescription =
                        new SosProcedureDescription<>(abstractProcess);
                enrichments.setDescription(sosProcedureDescription).enrichAll();
                desc.add(sosProcedureDescription);
            }
        } else {
            enrichments.enrichAll();
        }
        desc.setValidTime(validTime);
    }

    /**
     * Convert the description to another procedure description format.
     *
     * @param fromFormat
     *            the source format
     * @param toFormat
     *            the target format
     * @param description
     *            the procedure description.
     *
     * @return the converted description
     *
     * @throws OwsExceptionReport
     *             if conversion fails
     */
    private SosProcedureDescription<?> convert(String fromFormat, String toFormat,
            SosProcedureDescription<?> description) throws OwsExceptionReport {
        try {
            Converter<AbstractFeature, AbstractFeature> converter =
                    getConverterRepository().getConverter(fromFormat, toFormat);
            if (converter != null) {
                AbstractFeature convert = converter.convert(description);
                if (convert instanceof SosProcedureDescription) {
                    return (SosProcedureDescription<?>) convert;
                } else {
                    return new SosProcedureDescription<AbstractFeature>(convert).add(description);
                }
            }
            throw new ConverterException(
                    String.format("No converter available to convert from '%s' to '%s'", fromFormat, toFormat));
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce)
                    .withMessage("Error while processing data for DescribeSensor document!");
        }
    }

}

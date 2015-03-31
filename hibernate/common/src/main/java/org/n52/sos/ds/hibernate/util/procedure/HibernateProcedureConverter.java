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
package org.n52.sos.ds.hibernate.util.procedure;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.DescriptionXmlEntity;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.procedure.create.DescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.FileDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.GeneratedDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.LinkedDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.ValidProcedureTimeDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.XmlStringDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.enrich.ProcedureDescriptionEnrichments;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorRepository;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.http.HTTPStatus;

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
public class HibernateProcedureConverter implements HibernateSqlQueryConstants {

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            Requested procedure descriptionFormat
     * @param requestedServiceVersion
     *            Requested SOS version
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescription(Procedure procedure,
            String requestedDescriptionFormat, String requestedServiceVersion, Session session)
            throws OwsExceptionReport {
        // child hierarchy procedures haven't been queried yet, pass null
        return createSosProcedureDescription(procedure, requestedDescriptionFormat, requestedServiceVersion, null,
                session);
    }

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            Requested procedure descriptionFormat
     * @param requestedServiceVersion
     *            Requested SOS version
     * @param loadedProcedures
     *            Loaded procedure hierarchy (passed to recursive requests to
     *            avoid multiple queries)
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescription(Procedure procedure,
            String requestedDescriptionFormat, String requestedServiceVersion,
            Map<String, Procedure> loadedProcedures, Locale i18n, Session session) throws OwsExceptionReport {
        if (procedure == null) {
            throw new NoApplicableCodeException().causedBy(
                    new IllegalArgumentException("Parameter 'procedure' should not be null!")).setStatus(
                    HTTPStatus.INTERNAL_SERVER_ERROR);
        }
        checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), procedure, requestedDescriptionFormat, getFormat(procedure));
        SosProcedureDescription desc = create(procedure, requestedDescriptionFormat, null, i18n, session).orNull();
        if (desc != null) {
            addHumanReadableName(desc, procedure);
            enrich(desc, procedure, requestedServiceVersion, requestedDescriptionFormat, null, loadedProcedures, i18n,
                    session);
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
     * @param vpt
     *            Hibernate ValidProcedureTime entity
     * @param version
     *            Requested SOS version
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescriptionFromValidProcedureTime(Procedure procedure, String requestedDescriptionFormat,
            ValidProcedureTime vpt, String version, Locale i18n, Session session) throws OwsExceptionReport {
        if (vpt != null) {
            checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), vpt, requestedDescriptionFormat, getFormat(vpt));
        } else {
            checkOutputFormatWithDescriptionFormat(procedure.getIdentifier(), procedure, requestedDescriptionFormat, getFormat(procedure));
        }
        Optional<SosProcedureDescription> description = create(procedure, requestedDescriptionFormat, vpt, i18n, session);
        if (description.isPresent()) {
            addHumanReadableName(description.get(), procedure);
            enrich(description.get(), procedure, version, requestedDescriptionFormat, getValidTime(vpt), null, i18n, session);
            if (!requestedDescriptionFormat.equals(description.get().getDescriptionFormat())) {
                SosProcedureDescription converted = convert(description.get().getDescriptionFormat(), requestedDescriptionFormat, description.get());
                converted.setDescriptionFormat(requestedDescriptionFormat);
                return converted;
            }
        }
        return description.orNull();
    }

    public SosProcedureDescription createSosProcedureDescription(Procedure procedure,
            String requestedDescriptionFormat, String requestedServiceVersion, Locale i18n, Session session)
            throws OwsExceptionReport {
        return createSosProcedureDescription(procedure, requestedDescriptionFormat, requestedServiceVersion, null,
                i18n, session);
    }

    private void addHumanReadableName(SosProcedureDescription desc,
			Procedure procedure) {
    	if (!desc.isSetHumanReadableIdentifier() && procedure.isSetName()) {
    		desc.setHumanReadableIdentifier(procedure.getName());
    	}
	}

	protected TimePeriod getValidTime(ValidProcedureTime validProcedureTime) {
        return new TimePeriod(validProcedureTime.getStartTime(), validProcedureTime.getEndTime());
    }

    private String getFormat(HasProcedureDescriptionFormat hpdf) {
        return hpdf.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
    }

    /**
     * Checks the requested procedureDescriptionFormat with the datasource
     * procedureDescriptionFormat.
     * @param identifier 
     *
     * @param procedure
     *            the procedure
     * @param requestedFormat
     *            requested procedureDescriptionFormat
     * @param descriptionFormat
     *            Data source procedureDescriptionFormat
     *
     * @throws OwsExceptionReport
     *             If procedureDescriptionFormats are invalid
     */
    @VisibleForTesting
    boolean checkOutputFormatWithDescriptionFormat(String identifier, DescriptionXmlEntity procedure, String requestedFormat, String descriptionFormat)
            throws OwsExceptionReport {
        if (procedure.isSetDescriptionXml()) {
            if (requestedFormat.equalsIgnoreCase(descriptionFormat) || existConverter(descriptionFormat, requestedFormat)) {
                return true;
            }
        } else {
            if (existsGenerator(requestedFormat)) {
                return true;
            }
        }
        throw new InvalidParameterValueException()
                .at(SosConstants.DescribeSensorParams.procedure)
                .withMessage("The value of the output format is wrong and has to be %s for procedure %s",
                        descriptionFormat, identifier).setStatus(HTTPStatus.BAD_REQUEST);
    }

    private boolean existConverter(String from, String to) {
        return ConverterRepository.getInstance().hasConverter(from, to);
    }
    
    private boolean existsGenerator(String descriptionFormat) {
        return HibernateProcedureDescriptionGeneratorRepository.getInstance()
                .hasHibernateProcedureDescriptionGeneratorFactory(descriptionFormat);
    }

    private Optional<SosProcedureDescription> create(Procedure procedure, String descriptionFormat,
            ValidProcedureTime vpt, Locale i18n, Session session) throws OwsExceptionReport {
        Optional<DescriptionCreationStrategy> strategy = getCreationStrategy(procedure, vpt);
        if (strategy.isPresent()) {
            return Optional.fromNullable(strategy.get().create(procedure, descriptionFormat, i18n, session));
        } else {
            return Optional.absent();
        }
    }

    private Optional<DescriptionCreationStrategy> getCreationStrategy(Procedure p, ValidProcedureTime vpt) {
        for (DescriptionCreationStrategy strategy : getCreationStrategies(vpt)) {
            if (strategy.apply(p)) {
                return Optional.of(strategy);
            }
        }
        return Optional.absent();
    }

    protected ArrayList<DescriptionCreationStrategy> getCreationStrategies(ValidProcedureTime vpt) {
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
     * @param cache
     *            the procedure cache
     * @param language
     *            the language
     * @param session
     *            the session
     *
     * @see HibernateProcedureEnrichment
     * @throws OwsExceptionReport
     *             if the enrichment fails
     */
    private void enrich(SosProcedureDescription desc, Procedure procedure, String version, String format,
            TimePeriod validTime, Map<String, Procedure> cache, Locale language, Session session)
            throws OwsExceptionReport {
        ProcedureDescriptionEnrichments enrichments =
                ProcedureDescriptionEnrichments.create().setIdentifier(procedure.getIdentifier()).setVersion(version)
                        .setDescription(desc).setProcedureDescriptionFormat(format).setSession(session)
                        .setValidTime(validTime).setProcedureCache(cache).setConverter(this).setLanguage(language);
        if (desc instanceof SensorML && ((SensorML) desc).isWrapper()) {
            enrichments.setDescription(desc).createValidTimeEnrichment().enrich();
            for (AbstractProcess abstractProcess : ((SensorML) desc).getMembers()) {
                enrichments.setDescription(abstractProcess).enrichAll();
            }
        } else {
            enrichments.enrichAll();
        }
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
    private SosProcedureDescription convert(String fromFormat, String toFormat, SosProcedureDescription description)
            throws OwsExceptionReport {
        try {
            Converter<SosProcedureDescription, Object> converter =
                    ConverterRepository.getInstance().getConverter(fromFormat, toFormat);
            if (converter != null) {
                return converter.convert(description);
            }
            throw new ConverterException(String.format("No converter available to convert from '%s' to '%s'",
                    fromFormat, toFormat));
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage(
                    "Error while processing data for DescribeSensor document!");
        }
    }
}

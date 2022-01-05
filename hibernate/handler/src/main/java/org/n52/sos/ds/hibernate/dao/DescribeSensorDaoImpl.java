/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.Converter;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Configurable
public class DescribeSensorDaoImpl implements org.n52.sos.ds.dao.DescribeSensorDao, HibernateDao {

    private static final String LOG_PARAMETER_PROCEDURE_NOT_NULL = "Parameter 'procedure' should not be null!";

    private HibernateSessionHolder sessionHolder;

    private HibernateProcedureConverter procedureConverter;

    private ConverterRepository converterRepository;

    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    private DaoFactory daoFactory;

    private Locale defaultLanguage;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConverterRepository(ConverterRepository repo) {
        this.converterRepository = repo;
    }

    @Inject
    public void setProcedureDescriptionFormatRepository(ProcedureDescriptionFormatRepository repo) {
        this.procedureDescriptionFormatRepository = repo;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setHibernateProcedureConverter(HibernateProcedureConverter procedureConverter) {
        this.procedureConverter = procedureConverter;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = LocaleHelper.decode(defaultLanguage);
    }

    @Override
    public List<SosProcedureDescription<?>> querySensorDescriptions(DescribeSensorRequest request)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return queryDescriptions(request, session);
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data for DescribeSensor document!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public List<SosProcedureDescription<?>> querySensorDescriptions(DescribeSensorRequest request, Object connection)
            throws OwsExceptionReport {
        if (checkConnection(connection)) {
            return queryDescriptions(request, HibernateSessionHolder.getSession(connection));
        }
        return querySensorDescriptions(request);
    }

    private List<SosProcedureDescription<?>> queryDescriptions(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        List<SosProcedureDescription<?>> descriptions = new LinkedList<SosProcedureDescription<?>>();
        if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
            descriptions.addAll(getProcedureDescriptions(request, session));
        } else {
            descriptions.add(getProcedureDescription(request, session));
        }
        return descriptions;
    }

    /**
     * Get procedure description for non transactional SOS
     *
     * @param request
     *            DescribeSensorRequest request
     * @param session
     *            Hibernate session
     * @return Matched procedure description
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private SosProcedureDescription<?> getProcedureDescription(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        final ProcedureEntity procedure =
                new ProcedureDAO(daoFactory).getProcedureForIdentifier(request.getProcedure(), session);
        if (procedure == null) {
            throw new NoApplicableCodeException()
                    .causedBy(new IllegalArgumentException(LOG_PARAMETER_PROCEDURE_NOT_NULL))
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
        return procedureConverter.createSosProcedureDescription(procedure, request.getProcedureDescriptionFormat(),
                request.getVersion(), getRequestedLocale(request), session);
    }

    /**
     * @param request
     *            DescribeSensorRequest request
     * @param session
     *            Hibernate session
     * @return Matching procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<SosProcedureDescription<?>> getProcedureDescriptions(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        Set<String> possibleProcedureDescriptionFormats =
                getPossibleProcedureDescriptionFormats(request.getProcedureDescriptionFormat());
        final ProcedureEntity procedure = new ProcedureDAO(daoFactory).getProcedureForIdentifier(
                request.getProcedure(), possibleProcedureDescriptionFormats, request.getValidTime(), session);
        List<SosProcedureDescription<?>> list = Lists.newLinkedList();
        if (procedure != null) {
            if (procedure.hasProcedureHistory()) {
                for (ProcedureHistoryEntity validProcedureTime : daoFactory.getProcedureHistoryDAO()
                        .get(procedure, possibleProcedureDescriptionFormats, request.getValidTime(),
                                session)) {
                    SosProcedureDescription<?> sosProcedureDescription =
                            procedureConverter.createSosProcedureDescriptionFromValidProcedureTime(procedure,
                                    request.getProcedureDescriptionFormat(), validProcedureTime, request.getVersion(),
                                    getRequestedLocale(request), session);
                    list.add(convertProcedureDescription(sosProcedureDescription, request));
                }
            }
        } else {
            SosProcedureDescription<?> procedureDescription = getProcedureDescription(request, session);
            if (procedureDescription != null) {
                list.add(procedureDescription);
            } else {
                if (!request.isSetValidTime()) {
                    throw new NoApplicableCodeException()
                            .causedBy(new IllegalArgumentException(LOG_PARAMETER_PROCEDURE_NOT_NULL))
                            .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return list;
    }

    /**
     * Get possible procedure description formats for this procedure description
     * format. More precise, are there converter available.
     *
     * @param procedureDescriptionFormat
     *            Procedure description format to check
     * @return All possible procedure description formats
     */
    private Set<String> getPossibleProcedureDescriptionFormats(String procedureDescriptionFormat) {
        Set<String> possibleFormats = checkForUrlVsMimeType(procedureDescriptionFormat);
        String matchingPdf = getProcedureDescriptionFormatMatchingString(procedureDescriptionFormat);
        this.procedureDescriptionFormatRepository.getAllProcedureDescriptionFormats().values().stream()
                .flatMap(Set::stream)
                .filter(pdf -> matchingPdf.equals(getProcedureDescriptionFormatMatchingString(pdf)))
                .forEach(possibleFormats::add);
        possibleFormats.addAll(converterRepository.getFromNamespaceConverterTo(procedureDescriptionFormat));
        return possibleFormats;
    }

    /**
     * Get procedure description format matching String, to lower case replace
     * \s
     *
     * @param procedureDescriptionFormat
     *            Procedure description formats to format
     * @return Formatted procedure description format String
     */
    private String getProcedureDescriptionFormatMatchingString(String procedureDescriptionFormat) {
        // match against lowercase string, ignoring whitespace
        return procedureDescriptionFormat.toLowerCase(Locale.ROOT).replaceAll("\\s", "");
    }

    private Set<String> checkForUrlVsMimeType(String procedureDescriptionFormat) {
        Set<String> possibleFormats = Sets.newHashSet();
        possibleFormats.add(procedureDescriptionFormat);
        if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        } else if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE);
        }
        return possibleFormats;
    }

    private SosProcedureDescription<?> convertProcedureDescription(SosProcedureDescription<?> procedureDescription,
            DescribeSensorRequest request) throws CodedException {
        if (!checkForUrlVsMimeType(procedureDescription.getDescriptionFormat())
                .contains(request.getProcedureDescriptionFormat())) {
            Converter<SosProcedureDescription<?>, SosProcedureDescription<?>> converter =
                    converterRepository.getConverter(procedureDescription.getDescriptionFormat(),
                            request.getProcedureDescriptionFormat());
            if (converter != null) {
                try {
                    return converter.convert(procedureDescription);
                } catch (ConverterException e) {
                    throw new NoApplicableCodeException().causedBy(e)
                            .withMessage("Error while converting procedureDescription!");
                }
            } else {
                throw new NoApplicableCodeException().withMessage("No converter (%s -> %s) found!",
                        procedureDescription.getDescriptionFormat(), request.getProcedureDescriptionFormat());
            }
        }
        return procedureDescription;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

}

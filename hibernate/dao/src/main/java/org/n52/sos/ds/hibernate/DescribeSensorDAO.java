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
package org.n52.sos.ds.hibernate;

import static org.n52.sos.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.ds.AbstractDescribeSensorDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.i18n.LocaleHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * Implementation of the abstract class AbstractDescribeSensorDAO
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @author ShaneStClair
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class DescribeSensorDAO extends AbstractDescribeSensorDAO {
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private final HibernateProcedureConverter procedureConverter = new HibernateProcedureConverter();

    /**
     * constructor
     */
    public DescribeSensorDAO() {
        super(SosConstants.SOS);
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public DescribeSensorResponse getSensorDescription(final DescribeSensorRequest request) throws OwsExceptionReport {
        // sensorDocument which should be returned
        Session session = null;
        try {
            final DescribeSensorResponse response = new DescribeSensorResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setOutputFormat(request.getProcedureDescriptionFormat());
            session = sessionHolder.getSession();
            // check for transactional SOS.
            if (HibernateHelper.isEntitySupported(ValidProcedureTime.class)) {
                response.setSensorDescriptions(getProcedureDescriptions(request, session));
            } else {
                response.addSensorDescription(getProcedureDescription(request, session));
            }
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying data for DescribeSensor document!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        super.setOperationsMetadata(opsMeta, service, version);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            opsMeta.addAnyParameterValue(Sos2Constants.DescribeSensorParams.validTime);
        }
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
     * @throws ConverterException
     *             If an error occurs
     */
    private SosProcedureDescription getProcedureDescription(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        final Procedure procedure = new ProcedureDAO().getProcedureForIdentifier(request.getProcedure(), session);
        if (procedure == null) {
            throw new NoApplicableCodeException().causedBy(
                    new IllegalArgumentException("Parameter 'procedure' should not be null!")).setStatus(
                    INTERNAL_SERVER_ERROR);
        }

        return procedureConverter.createSosProcedureDescription(procedure, request.getProcedureDescriptionFormat(),
                request.getVersion(), LocaleHelper.fromRequest(request), session);
    }

    /**
     * @param request
     *            DescribeSensorRequest request
     * @param session
     *            Hibernate session
     * @return Matching procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If an error occurs
     */
    private List<SosProcedureDescription> getProcedureDescriptions(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        Set<String> possibleProcedureDescriptionFormats =
                getPossibleProcedureDescriptionFormats(request.getProcedureDescriptionFormat());
        final TProcedure procedure =
                new ProcedureDAO().getTProcedureForIdentifier(request.getProcedure(),
                        possibleProcedureDescriptionFormats, request.getValidTime(), session);
        List<SosProcedureDescription> list = Lists.newLinkedList();
        if (procedure != null) {
            List<ValidProcedureTime> validProcedureTimes =
                    new ValidProcedureTimeDAO().getValidProcedureTimes(procedure, possibleProcedureDescriptionFormats,
                            request.getValidTime(), session);
            Locale requestedLanguage = LocaleHelper.fromRequest(request);
            for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
                SosProcedureDescription sosProcedureDescription =
                        procedureConverter.createSosProcedureDescriptionFromValidProcedureTime(procedure, request.getProcedureDescriptionFormat(),
                                validProcedureTime, request.getVersion(), requestedLanguage, session);
                list.add(convertProcedureDescription(sosProcedureDescription, request));
            }
        } else {
            SosProcedureDescription procedureDescription = getProcedureDescription(request, session);
            if (procedureDescription != null) {
                list.add(procedureDescription);
            } else {
                if (!request.isSetValidTime()) {
                    throw new NoApplicableCodeException().causedBy(
                            new IllegalArgumentException("Parameter 'procedure' should not be null!")).setStatus(
                            INTERNAL_SERVER_ERROR);
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
        String procedureDescriptionFormatMatchingString =
                getProcedureDescriptionFormatMatchingString(procedureDescriptionFormat);
        for (Entry<ServiceOperatorKey, Set<String>> pdfByServiceOperatorKey : CodingRepository.getInstance()
                .getAllProcedureDescriptionFormats().entrySet()) {
            for (String pdfFromRepository : pdfByServiceOperatorKey.getValue()) {
                if (procedureDescriptionFormatMatchingString
                        .equals(getProcedureDescriptionFormatMatchingString(pdfFromRepository))) {
                    possibleFormats.add(pdfFromRepository);
                }
            }
        }
        possibleFormats.addAll(ConverterRepository.getInstance().getFromNamespaceConverterTo(
                procedureDescriptionFormat));
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
        return procedureDescriptionFormat.toLowerCase().replaceAll("\\s", "");
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
    

    private SosProcedureDescription convertProcedureDescription(SosProcedureDescription procedureDescription,
            DescribeSensorRequest request) throws CodedException {
        if (!checkForUrlVsMimeType(procedureDescription.getDescriptionFormat()).contains(request.getProcedureDescriptionFormat())) {
            Converter<SosProcedureDescription, SosProcedureDescription> converter =
                    ConverterRepository.getInstance().getConverter(procedureDescription.getDescriptionFormat(),
                            request.getProcedureDescriptionFormat());
            if (converter != null) {
                try {
                    return converter.convert(procedureDescription);
                } catch (ConverterException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage(
                            "Error while converting procedureDescription!");
                }
            } else {
                throw new NoApplicableCodeException().withMessage("No converter (%s -> %s) found!",
                        procedureDescription.getDescriptionFormat(), request.getProcedureDescriptionFormat());
            }
        }
        return procedureDescription;
    }
}

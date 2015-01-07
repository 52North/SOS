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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ds.AbstractInsertResultDAO;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.CapabilitiesExtension;
import org.n52.sos.ogc.sos.CapabilitiesExtensionKey;
import org.n52.sos.ogc.sos.CapabilitiesExtensionProvider;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionCapabilities;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.request.InsertResultRequest;
import org.n52.sos.response.InsertResultResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractInsertResultDAO
 * 
 * @since 4.0.0
 * 
 */
public class InsertResultDAO extends AbstractInsertResultDAO implements CapabilitiesExtensionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertResultDAO.class);

    private static final int FLUSH_THRESHOLD = 50;

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    /**
     * constructor
     */
    public InsertResultDAO() {
        super(SosConstants.SOS);
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public InsertResultResponse insertResult(final InsertResultRequest request) throws OwsExceptionReport {
        final InsertResultResponse response = new InsertResultResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        
        Map<String,Codespace> codespaceCache = Maps.newHashMap();
        Map<String,Unit> unitCache = Maps.newHashMap();
        
        try {
            session = sessionHolder.getSession();
            final ResultTemplate resultTemplate =
                    new ResultTemplateDAO().getResultTemplateObject(request.getTemplateIdentifier(), session);
            transaction = session.beginTransaction();
            final OmObservation o =
                    getSingleObservationFromResultValues(response.getVersion(), resultTemplate,
                            request.getResultValues(), session);
            response.setObservation(o);
            final List<OmObservation> observations = getSingleObservationsFromObservation(o);

            final Set<ObservationConstellation> obsConsts =
                    Sets.newHashSet(new ObservationConstellationDAO().getObservationConstellation(
                            resultTemplate.getProcedure(),
                            resultTemplate.getObservableProperty(),
                            Configurator.getInstance().getCache()
                                    .getOfferingsForProcedure(resultTemplate.getProcedure().getIdentifier()), session));

            int insertion = 0;
            final int size = observations.size();
            final AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            LOGGER.debug("Start saving {} observations.", size);
            for (final OmObservation observation : observations) {
                observationDAO.insertObservationSingleValue(obsConsts, resultTemplate.getFeatureOfInterest(),
                        observation, codespaceCache, unitCache, session);
                if ((++insertion % FLUSH_THRESHOLD) == 0) {
                    session.flush();
                    session.clear();
                    LOGGER.debug("Saved {}/{} observations.", insertion, size);
                }
            }
            LOGGER.debug("Saved {} observations.", size);
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            // XXX exception text
            throw new NoApplicableCodeException().causedBy(he);
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    /**
     * Create OmObservation from result values
     * 
     * @param version
     *            Service version
     * @param resultTemplate
     *            Associated result template
     * @param resultValues
     *            Result values
     * @param session
     *            Hibernate session
     * @return OmObservation from result values
     * @throws OwsExceptionReport
     *             If an error occurs during the processing
     */
    private OmObservation getSingleObservationFromResultValues(final String version,
            final ResultTemplate resultTemplate, final String resultValues, final Session session)
            throws OwsExceptionReport {
        final SosResultEncoding resultEncoding = new SosResultEncoding(resultTemplate.getResultEncoding());
        final SosResultStructure resultStructure = new SosResultStructure(resultTemplate.getResultStructure());
        final String[] blockValues = getBlockValues(resultValues, resultEncoding.getEncoding());
        final OmObservation singleObservation =
                getObservation(resultTemplate, blockValues, resultStructure.getResultStructure(),
                        resultEncoding.getEncoding(), session);
        final AbstractFeature feature = getSosAbstractFeature(resultTemplate.getFeatureOfInterest(), version, session);
        singleObservation.getObservationConstellation().setFeatureOfInterest(feature);
        return singleObservation;
    }

    /**
     * Get internal feature from FeatureOfInterest entity
     * 
     * @param featureOfInterest
     * @param version
     *            Service version
     * @param session
     *            Hibernate session
     * @return Internal feature representation
     * @throws OwsExceptionReport
     *             If an error occurs during requesting
     */
    protected AbstractFeature getSosAbstractFeature(final FeatureOfInterest featureOfInterest, final String version,
            final Session session) throws OwsExceptionReport {
        final FeatureQueryHandler featureQueryHandler = Configurator.getInstance().getFeatureQueryHandler();
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject()
            .addFeatureIdentifier(featureOfInterest.getIdentifier())
            .setConnection(session)
            .setVersion(version);
        return featureQueryHandler.getFeatureByID(queryObject);
    }

    /**
     * Unfold internal observation from result values to single internal
     * observations
     * 
     * @param observation
     *            Internal observaiton to unfold
     * @return List with single interal observations
     * @throws OwsExceptionReport
     *             If an error occurs during unfolding
     */
    protected List<OmObservation> getSingleObservationsFromObservation(final OmObservation observation)
            throws OwsExceptionReport {
        try {
            return HibernateObservationUtilities.unfoldObservation(observation);
        } catch (final Exception e) {
            throw new InvalidParameterValueException()
                    .causedBy(e)
                    .at(Sos2Constants.InsertResultParams.resultValues)
                    .withMessage(
                            "The resultValues format does not comply to the resultStructure of the resultTemplate!");
        }
    }

    /**
     * Get internal ObservationConstellation from result template
     * 
     * @param resultTemplate
     * @param session
     *            Hibernate session
     * @return Internal ObservationConstellation
     */
    private OmObservationConstellation getSosObservationConstellation(final ResultTemplate resultTemplate,
            final Session session) {
        // get all offerings for procedure to match all parent procedure
        // offerings
        Set<Offering> procedureOfferings = new HashSet<Offering>();
        procedureOfferings.add(resultTemplate.getOffering());
        Set<String> procedureOfferingIds =
                getCache().getOfferingsForProcedure(resultTemplate.getProcedure().getIdentifier());
        procedureOfferings.addAll(new OfferingDAO().getOfferingsForIdentifiers(procedureOfferingIds, session));

        final List<ObservationConstellation> obsConsts =
                new ObservationConstellationDAO().getObservationConstellationsForOfferings(
                        resultTemplate.getProcedure(), resultTemplate.getObservableProperty(), procedureOfferings,
                        session);
        final Set<String> offerings = Sets.newHashSet();
        String observationType = null;
        for (ObservationConstellation obsConst : obsConsts) {
            offerings.add(obsConst.getOffering().getIdentifier());
            if (observationType == null) {
                observationType = obsConst.getObservationType().getObservationType();
            }
        }
        final SosProcedureDescription procedure = createProcedure(resultTemplate.getProcedure());
        final AbstractPhenomenon observablePropety =
                new OmObservableProperty(resultTemplate.getObservableProperty().getIdentifier());
        final AbstractFeature feature =
                new SamplingFeature(new CodeWithAuthority(resultTemplate.getFeatureOfInterest().getIdentifier()));
        return new OmObservationConstellation(procedure, observablePropety, offerings, feature, observationType);
    }

    /**
     * Create internal ProcedureDescription from Procedure entity
     * 
     * @param hProcedure
     *            Procedure entity
     * @return Internal ProcedureDescription
     */
    private SosProcedureDescription createProcedure(final Procedure hProcedure) {
        final SensorML procedure = new SensorML();
        procedure.setIdentifier(hProcedure.getIdentifier());
        return procedure;
    }

    /**
     * Get internal observation
     * 
     * @param resultTemplate
     *            Associated ResultTemplate
     * @param blockValues
     *            Block values from result values
     * @param resultStructure
     *            Associated ResultStructure
     * @param encoding
     *            Associated ResultEncoding
     * @param session
     *            Hibernate session
     * @return Internal observation
     * @throws OwsExceptionReport
     *             If processing fails
     */
    private OmObservation getObservation(final ResultTemplate resultTemplate, final String[] blockValues,
            final SweAbstractDataComponent resultStructure, final SweAbstractEncoding encoding, final Session session)
            throws OwsExceptionReport {
        final int resultTimeIndex = ResultHandlingHelper.hasResultTime(resultStructure);
        final int phenomenonTimeIndex = ResultHandlingHelper.hasPhenomenonTime(resultStructure);

        final SweDataRecord record = setRecordFrom(resultStructure);

        final Map<Integer, String> observedProperties = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> units = new HashMap<Integer, String>(record.getFields().size() - 1);

        int j = 0;
        for (final SweField swefield : record.getFields()) {
            if (j != resultTimeIndex && j != phenomenonTimeIndex) {
                if (swefield.getElement() instanceof SweAbstractSimpleType<?>) {
                    final Integer index = Integer.valueOf(j);
                    final SweAbstractSimpleType<?> sweAbstractSimpleType =
                            (SweAbstractSimpleType<?>) swefield.getElement();
                    if (sweAbstractSimpleType instanceof SweQuantity) {
                        /* TODO units for other SosSweSimpleTypes? */
                        units.put(index, ((SweQuantity) sweAbstractSimpleType).getUom());
                    }
                    observedProperties.put(index, swefield.getElement().getDefinition());
                } else {
                    throw new NoApplicableCodeException().withMessage("The swe:Field element of type {} is not yet supported!", swefield.getElement().getClass().getName());
                }
            }
            ++j;
        }

        // TODO support for compositePhenomenon
        // if (observedProperties.size() > 1) {
        // }

        final MultiObservationValues<SweDataArray> sosValues =
                createObservationValueFrom(blockValues, record, encoding, resultTimeIndex, phenomenonTimeIndex);

        final OmObservation observation = new OmObservation();
        observation.setObservationConstellation(getSosObservationConstellation(resultTemplate, session));
        observation.setResultType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        observation.setValue(sosValues);
        return observation;
    }

    /**
     * Create internal observation value
     * 
     * @param blockValues
     *            Block values from result values
     * @param recordFromResultStructure
     *            Associated ResultStructure
     * @param encoding
     *            Associated Result encoding
     * @param resultTimeIndex
     *            Result time position
     * @param phenomenonTimeIndex
     *            Phenomenon time positions
     * @return Internal observation value
     * @throws OwsExceptionReport
     *             If processing fails
     */
    private MultiObservationValues<SweDataArray> createObservationValueFrom(final String[] blockValues,
            final SweAbstractDataComponent recordFromResultStructure, final SweAbstractEncoding encoding,
            final int resultTimeIndex, final int phenomenonTimeIndex) throws OwsExceptionReport {
        final SweDataArray dataArray = new SweDataArray();
        dataArray.setElementType(recordFromResultStructure);
        dataArray.setEncoding(encoding);

        final SweDataArrayValue dataArrayValue = new SweDataArrayValue();
        dataArrayValue.setValue(dataArray);

        for (final String block : blockValues) {
            final String[] singleValues = getSingleValues(block, encoding);
            if (singleValues != null && singleValues.length > 0) {
                dataArrayValue.addBlock(Arrays.asList(singleValues));
            }
        }
        final MultiObservationValues<SweDataArray> sosValues = new MultiObservationValues<SweDataArray>();
        sosValues.setValue(dataArrayValue);
        return sosValues;
    }

    // TODO move to helper class
    /**
     * Get internal time object from time String
     * 
     * @param timeString
     *            Time String to parse
     * @return Internal time object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Time getPhenomenonTime(final String timeString) throws OwsExceptionReport {
        try {
            Time phenomenonTime;
            if (timeString.contains("/")) {
                final String[] times = timeString.split("/");
                final DateTime start = DateTimeHelper.parseIsoString2DateTime(times[0].trim());
                final DateTime end = DateTimeHelper.parseIsoString2DateTime(times[1].trim());
                phenomenonTime = new TimePeriod(start, end);
            } else {
                final DateTime dateTime = DateTimeHelper.parseIsoString2DateTime(timeString.trim());
                phenomenonTime = new TimeInstant(dateTime);
            }
            return phenomenonTime;
        } catch (final DateTimeParseException dte) {
            throw dte.at("phenomenonTime");
        }
    }

    /**
     * Get single values from a block value
     * 
     * @param block
     *            Block value
     * @param encoding
     *            ResultEncoding
     * @return Single value array
     */
    private String[] getSingleValues(final String block, final SweAbstractEncoding encoding) {
        if (encoding instanceof SweTextEncoding) {
            final SweTextEncoding textEncoding = (SweTextEncoding) encoding;
            return separateValues(block, textEncoding.getTokenSeparator());
        }
        return null;
    }

    /**
     * Get block values from result values
     * 
     * @param resultValues
     *            Result values
     * @param encoding
     *            ResultEncoding
     * @return Block value array
     */
    private String[] getBlockValues(final String resultValues, final SweAbstractEncoding encoding) {
        if (encoding instanceof SweTextEncoding) {
            final SweTextEncoding textEncoding = (SweTextEncoding) encoding;
            final String[] blockValues = separateValues(resultValues, textEncoding.getBlockSeparator());
            return checkForCountValue(blockValues, textEncoding.getTokenSeparator());
        }
        return null;
    }

    /**
     * Check if the block values from result values contains a preceding count
     * value
     * 
     * @param blockValues
     *            Block values from result values
     * @param tokenSeparator
     *            Token separator
     * @return Block value array without preceding count value
     */
    private String[] checkForCountValue(final String[] blockValues, final String tokenSeparator) {
        if (blockValues != null && blockValues.length > 0) {
            if (blockValues[0].contains(tokenSeparator)) {
                return blockValues;
            } else {
                final String[] blockValuesWithoutCount = new String[blockValues.length - 1];
                System.arraycopy(blockValues, 1, blockValuesWithoutCount, 0, blockValuesWithoutCount.length);
                return blockValuesWithoutCount;
            }
        }
        return null;
    }

    /**
     * Separate values from String with separator
     * 
     * @param values
     *            Value String
     * @param separator
     *            Separator
     * @return Separated values as array
     */
    private String[] separateValues(final String values, final String separator) {
        return values.split(separator);
    }

    @Override
    public CapabilitiesExtension getExtension() {
        final SosInsertionCapabilities insertionCapabilities = new SosInsertionCapabilities();
        insertionCapabilities.addFeatureOfInterestTypes(getCache().getFeatureOfInterestTypes());
        insertionCapabilities.addObservationTypes(getCache().getObservationTypes());
        insertionCapabilities.addProcedureDescriptionFormats(CodingRepository.getInstance()
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        // TODO dynamic
        insertionCapabilities.addSupportedEncoding(SweConstants.ENCODING_TEXT);
        return insertionCapabilities;
    }

    @Override
    public CapabilitiesExtensionKey getCapabilitiesExtensionKey() {
        return new CapabilitiesExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
    }

    @Override
    public boolean hasRelatedOperation() {
        return true;
    }

    @Override
    public String getRelatedOperation() {
        return getOperationName();
    }

}

/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.InsertResultRequest;
import org.n52.shetland.ogc.sos.response.InsertResultResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.ds.AbstractInsertResultHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.ds.hibernate.util.observation.ObservationUnfolder;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.util.SweHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractInsertResultDAO
 *
 * @since 4.0.0
 *
 */
@Configurable
public class InsertResultHandler
        extends AbstractInsertResultHandler
        implements Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertResultHandler.class);
    public static final String CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES =
            "misc.convertComplexProfileToSingleProfiles";
    private static final int FLUSH_THRESHOLD = 50;
    private HibernateSessionHolder sessionHolder;
    private DaoFactory daoFactory;
    private boolean convertComplexProfileToSingleProfiles;
    private GeometryHandler geometryHandler;
    private ResultHandlingHelper helper;
    private SweHelper sweHelper;

    public InsertResultHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setSweHelper(SweHelper sweHelper) {
        this.sweHelper = sweHelper;
    }

    @Override
    public void init() {
        helper = new ResultHandlingHelper(geometryHandler, sweHelper);
    }

    @Override
    public synchronized InsertResultResponse insertResult(final InsertResultRequest request)
            throws OwsExceptionReport {
        final InsertResultResponse response = new InsertResultResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;

        Map<String, CodespaceEntity> codespaceCache = Maps.newHashMap();
        Map<UoM, UnitEntity> unitCache = Maps.newHashMap();

        try {
            session = sessionHolder.getSession();
            final ResultTemplateEntity resultTemplate =
                    daoFactory.getResultTemplateDAO().getResultTemplateObject(request.getTemplateIdentifier(),
                            session);
            transaction = session.beginTransaction();
            final OmObservation o =
                    getSingleObservationFromResultValues(response.getVersion(), resultTemplate,
                            request.getResultValues(), session);
            final List<OmObservation> observations = getSingleObservationsFromObservation(o);
            if (o.getObservationConstellation().isSetFeatureOfInterest()
                    && o.getObservationConstellation().isSetProcedure()) {
                response.setObservation(o);
            } else {
                response.setObservations(observations);
            }

            final AbstractSeriesDAO obsConstDao = daoFactory.getSeriesDAO();
            final FormatDAO obsTypeDao = daoFactory.getObservationTypeDAO();
            Map<OmObservationConstellation, DatasetEntity> obsConsts = new HashMap<>();

            int insertion = 0;
            final int size = observations.size();
            final AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
            LOGGER.debug("Start saving {} observations.", size);
            Map<String, AbstractFeatureEntity> featureEntityMap = new HashMap<>();
            for (final OmObservation observation : observations) {
                OmObservationConstellation omObsConst = observation.getObservationConstellation();
                if (!obsConsts.containsKey(omObsConst)) {
                    DatasetEntity oc = obsConstDao.getSeries(omObsConst, session);
                    if (oc != null) {
                        obsConsts.put(omObsConst, oc);
                    } else if (oc == null && isConvertComplexProfileToSingleProfiles() && observation.isSetValue()
                            && observation.getValue().isSetValue()
                            && observation.getValue().getValue() instanceof ProfileValue) {
                        obsConsts.put(omObsConst, insertObservationConstellationForProfiles(obsConstDao, obsTypeDao,
                                observation, session));
                    }
                }
                DatasetEntity obsConst = obsConsts.get(observation.getObservationConstellation());
                AbstractFeatureEntity feature = null;
                if (resultTemplate.isSetFeature()) {
                    feature = resultTemplate.getFeature();
                } else {
                    if (featureEntityMap.containsKey(omObsConst.getFeatureOfInterestIdentifier())) {
                        feature = featureEntityMap.get(omObsConst.getFeatureOfInterestIdentifier());
                    } else {
                        FeatureOfInterestDAO featureOfInterestDAO = daoFactory.getFeatureOfInterestDAO();
                        feature =
                                featureOfInterestDAO.checkOrInsert(omObsConst.getFeatureOfInterest(),
                                        session);
                        featureOfInterestDAO.checkOrInsertRelatedFeatureRelation(feature,
                                obsConst.getOffering(), session);
                        featureEntityMap.put(feature.getIdentifier(), feature);
                    }
                }
                if (observation.getValue() instanceof SingleObservationValue) {
                    observationDAO.insertObservationSingleValue(obsConst, feature,
                            observation, codespaceCache, unitCache, session);
                } else if (observation.getValue() instanceof MultiObservationValues) {
                    observationDAO.insertObservationMultiValue(obsConst, feature,
                            observation, codespaceCache, unitCache, session);
                }
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

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
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
            final ResultTemplateEntity resultTemplate, final String resultValues, final Session session)
            throws OwsExceptionReport {
        final SosResultEncoding resultEncoding = createSosResultEncoding(resultTemplate.getEncoding());
        final SosResultStructure resultStructure = createSosResultStructure(resultTemplate.getStructure());
        final String[] blockValues = getBlockValues(resultValues, resultEncoding.get().get());
        final OmObservation singleObservation =
                getObservation(resultTemplate, blockValues, resultStructure.get().get(),
                        resultEncoding.get().get(), session);
        // final AbstractFeature feature =
        // getSosAbstractFeature(resultTemplate.getFeatureOfInterest(), version,
        // session);
        // singleObservation.getObservationConstellation().setFeatureOfInterest(feature);
        return singleObservation;
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

            return new ObservationUnfolder(observation, sweHelper).unfold(isConvertComplexProfileToSingleProfiles());
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
     * @throws OwsExceptionReport
     */
    private OmObservationConstellation getSosObservationConstellation(final ResultTemplateEntity resultTemplate,
            final Session session) throws OwsExceptionReport {

        final List<DatasetEntity> obsConsts = daoFactory.getSeriesDAO()
                .getSeriesForOfferings(resultTemplate.getPhenomenon(),
                        Sets.newHashSet(resultTemplate.getOffering()), session);
        final Set<String> offerings = Sets.newHashSet(resultTemplate.getOffering().getIdentifier());
        String observationType = null;
        for (DatasetEntity obsConst : obsConsts) {
            if (observationType == null && obsConst.isSetObservationType()) {
                observationType = obsConst.getObservationType().getFormat();
            }
        }
        OmObservationConstellation omObservationConstellation = new OmObservationConstellation()
                .setObservableProperty(
                        new OmObservableProperty(resultTemplate.getPhenomenon().getIdentifier()))
                .setOfferings(offerings).setObservationType(observationType);
        if (resultTemplate.isSetProcedure()) {
            omObservationConstellation.setProcedure(createProcedure(resultTemplate.getProcedure()));
        }
        if (resultTemplate.isSetFeature()) {
            omObservationConstellation.setFeatureOfInterest(
                    new SamplingFeature(new CodeWithAuthority(resultTemplate.getFeature().getIdentifier())));
        }
        return omObservationConstellation;
    }

    /**
     * Create internal ProcedureDescription from Procedure entity
     *
     * @param hProcedure
     *            Procedure entity
     * @return Internal ProcedureDescription
     */
    private SosProcedureDescription<?> createProcedure(final ProcedureEntity hProcedure) {
        final SensorML procedure = new SensorML();
        procedure.setIdentifier(hProcedure.getIdentifier());
        return new SosProcedureDescription<AbstractFeature>(procedure);
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
    private OmObservation getObservation(final ResultTemplateEntity resultTemplate, final String[] blockValues,
            final SweAbstractDataComponent resultStructure, final SweAbstractEncoding encoding, final Session session)
            throws OwsExceptionReport {
        final int resultTimeIndex = helper.hasResultTime(resultStructure);
        final int phenomenonTimeIndex = helper.hasPhenomenonTime(resultStructure);

        final SweDataRecord record = setRecordFrom(resultStructure);

        final Map<Integer, String> observedProperties = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> units = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> featureOfInterest = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> procedure = new HashMap<Integer, String>(record.getFields().size() - 1);

        int j = 0;
        getIndexFor(record, j, observedProperties, units, featureOfInterest, procedure,
                Sets.newHashSet(resultTimeIndex, phenomenonTimeIndex));

        final MultiObservationValues<SweDataArray> sosValues =
                createObservationValueFrom(blockValues, record, encoding, resultTimeIndex, phenomenonTimeIndex);

        final OmObservation observation = new OmObservation();
        observation.setObservationConstellation(getSosObservationConstellation(resultTemplate, session));
        observation.setResultType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        observation.setValue(sosValues);
        return observation;
    }

    /*
     * TODO: Check if this mehtod is still required!?!
     */
    @VisibleForTesting
    protected void getIndexFor(SweDataRecord record,
            int j,
            Map<Integer, String> observedProperties,
            Map<Integer, String> units,
            Map<Integer, String> featureOfInterest,
            Map<Integer, String> procedure,
            HashSet<Integer> reserved)
            throws CodedException {
        for (final SweField swefield : record.getFields()) {
            if (!reserved.contains(j)) {
                final Integer index = Integer.valueOf(j);
                if (swefield.getElement() instanceof SweAbstractSimpleType<?>) {
                    final SweAbstractSimpleType<?> sweAbstractSimpleType =
                            (SweAbstractSimpleType<?>) swefield.getElement();
                    if (swefield.getElement() instanceof SweText
                            && swefield.getElement().getDefinition().contains(helper.OM_FEATURE_OF_INTEREST)) {
                        featureOfInterest.put(index, swefield.getElement().getDefinition());
                    } else if (swefield.getElement() instanceof SweText
                            && swefield.getElement().getDefinition().contains(helper.OM_PROCEDURE)) {
                        procedure.put(index, swefield.getElement().getDefinition());
                    } else {
                        observedProperties.put(index, swefield.getElement().getDefinition());
                        if (sweAbstractSimpleType instanceof SweAbstractUomType<?>) {
                            units.put(index, ((SweAbstractUomType<?>) sweAbstractSimpleType).getUom());
                        }
                    }
                } else if (swefield.getElement() instanceof SweDataRecord) {
                    getIndexFor((SweDataRecord) swefield.getElement(), j,
                            observedProperties,
                            units,
                            featureOfInterest, procedure, reserved);
                } else if (swefield.getElement() instanceof SweVector) {
                    helper.checkVectorForSamplingGeometry(swefield);
                } else {
                    throw new NoApplicableCodeException().withMessage(
                            "The swe:Field element of type %s is not yet supported!",
                            swefield.getElement().getClass().getName());
                }
            }
            ++j;
        }
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
            final int resultTimeIndex, final int phenomenonTimeIndex)
            throws OwsExceptionReport {
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

    private DatasetEntity insertObservationConstellationForProfiles(AbstractSeriesDAO obsConstDao,
           FormatDAO obsTypeDao, OmObservation o, Session session) throws OwsExceptionReport {
        ProcedureEntity procedure = daoFactory.getProcedureDAO()
                .getProcedureForIdentifier(o.getObservationConstellation().getProcedureIdentifier(), session);
        PhenomenonEntity observableProperty = daoFactory.getObservablePropertyDAO()
                .getOrInsertObservableProperty(o.getObservationConstellation().getObservableProperty(), session);
        OfferingEntity offering = daoFactory.getOfferingDAO()
                .getOfferingForIdentifier(o.getObservationConstellation().getOfferings().iterator().next(), session);
        CategoryEntity category = daoFactory.getObservablePropertyDAO()
                .getOrInsertCategory(observableProperty, session);

        DatasetEntity oc = obsConstDao.checkOrInsertSeries(procedure, observableProperty,
                offering, category, false, session);
        if (o.getObservationConstellation().isSetObservationType()) {
            oc.setObservationType(obsTypeDao
                    .getFormatEntityObject(o.getObservationConstellation().getObservationType(), session));
        }
        return oc;
    }

    @Setting(CONVERT_COMPLEX_PROFILE_TO_SINGLE_PROFILES)
    public void setConvertComplexProfileToSingleProfiles(boolean convertComplexProfileToSingleProfiles) {
        this.convertComplexProfileToSingleProfiles = convertComplexProfileToSingleProfiles;
    }

    private boolean isConvertComplexProfileToSingleProfiles() {
        return this.convertComplexProfileToSingleProfiles;
    }

}

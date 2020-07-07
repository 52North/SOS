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
package org.n52.sos.ds.hibernate.values;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.MediaTypes;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DetectionLimitEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.series.db.beans.ereporting.EReportingQualityEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.om.TimeValuePair;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingQualityDAO;
import org.n52.sos.ds.hibernate.util.observation.EReportingHelper;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;
import org.n52.sos.ds.hibernate.util.observation.ParameterAdder;
import org.n52.sos.ds.hibernate.util.observation.RelatedObservationAdder;
import org.n52.sos.ds.hibernate.util.observation.SpatialFilteringProfileCreator;
import org.n52.svalbard.util.GmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Abstract class for Hibernate streaming values
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingValue extends StreamingValue<DataEntity<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateStreamingValue.class);

    protected final AbstractObservationRequest request;

    protected Criterion temporalFilterCriterion;

    private final HibernateSessionHolder sessionHolder;

    private final DaoFactory daoFactory;

    private final EReportingHelper helper;

    private Session session;

    private BindingRepository bindingRepository;

    private final SpatialFilteringProfileCreator spatialFilteringProfileCreator;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param daoFactory
     *            the DAO factory
     * @param connectionProvider
     *            the connection provider
     */
    public AbstractHibernateStreamingValue(ConnectionProvider connectionProvider, DaoFactory daoFactory,
            AbstractObservationRequest request, BindingRepository bindingRepository) {
        this.request = request;
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
        this.daoFactory = daoFactory;
        this.helper = new EReportingHelper(daoFactory.getSweHelper());
        this.bindingRepository = bindingRepository;
        this.spatialFilteringProfileCreator =  new SpatialFilteringProfileCreator(daoFactory.getGeometryHandler());
    }

    protected Session getSession() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }

        return session;
    }

    protected void returnSession(Session session) {
        this.session = null;
        sessionHolder.returnSession(session);
    }

    @Override
    public ObservationStream merge() throws OwsExceptionReport {
        Map<String, OmObservation> observations = Maps.newHashMap();
        while (hasNext()) {
            DataEntity<?> nextEntity = nextEntity();
            boolean mergableObservationValue = checkForMergability(nextEntity);
            OmObservation observation = null;
            if (observations.containsKey(getDiscriminator(nextEntity)) && mergableObservationValue) {
                observation = observations.get(getDiscriminator(nextEntity));
            } else {
                observation = getObservationTemplate().cloneTemplate();
                addSpecificValuesToObservation(observation, nextEntity, request.getExtensions());
                if (!mergableObservationValue && getDiscriminator(nextEntity) == null) {
                    observations.put(Long.toString(nextEntity.getId()), observation);
                } else {
                    observations.put(getDiscriminator(nextEntity), observation);
                }
            }
            mergeValueToObservation(nextEntity, observation, getResponseFormat());
            sessionHolder.getSession().evict(nextEntity);
        }
        return ObservationStream.of(observations.values());
    }

    private boolean checkForMergability(DataEntity<?> nextEntity) {
        return !(nextEntity instanceof DataArrayDataEntity);
    }

    private void addSpecificValuesToObservation(OmObservation observation, DataEntity<?> value,
            Extensions extensions) {
        boolean newSession = false;
        try {
            if (session == null) {
                session = sessionHolder.getSession();
                newSession = true;
            }
            addValueSpecificDataToObservation(value, observation, session, extensions);
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        } finally {
            if (newSession) {
                sessionHolder.returnSession(session);
            }
        }
    }

    /**
     * Set the temporal filter {@link Criterion}
     *
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     */
    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;

    }

    /**
     * Get the observation ids from {@link DataEntity}s
     *
     * @param abstractValuesResult
     *            {@link DataEntity}s to get ids from
     * @return Set with ids
     */
    protected Set<Long> getObservationIds(Collection<? extends DataEntity> abstractValuesResult) {
        Set<Long> ids = new HashSet<>(abstractValuesResult.size());
        for (DataEntity<?> abstractValue : abstractValuesResult) {
            ids.add(abstractValue.getId());
        }
        return ids;
    }

    /**
     * Create the phenomenon time from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link DataEntity} for get time from
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(DataEntity<?> abstractValue) {
        // create time element
        final DateTime phenStartTime = new DateTime(abstractValue.getSamplingTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (abstractValue.getSamplingTimeEnd() != null) {
            phenEndTime = new DateTime(abstractValue.getSamplingTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create phenomenon time from min and max
     * {@link DataEntity}s
     *
     * @param minTime
     *            minimum {@link DataEntity}
     * @param maxTime
     *            maximum {@link DataEntity}
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(DataEntity<?> minTime, DataEntity<?> maxTime) {
        // create time element

        final DateTime phenStartTime = DateTimeHelper.makeDateTime(minTime.getSamplingTimeStart());
        DateTime phenEndTime;
        if (maxTime.getSamplingTimeEnd() != null) {
            phenEndTime = DateTimeHelper.makeDateTime(minTime.getSamplingTimeEnd());
        } else {
            phenEndTime = phenStartTime;
        }
        return GmlHelper.createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create result time from {@link DataEntity}
     *
     * @param maxTime
     *            {@link DataEntity} to create result
     *            time from
     * @return result time
     */
    protected TimeInstant createResutlTime(DataEntity<?> maxTime) {
        DateTime dateTime = DateTimeHelper.makeDateTime(maxTime.getResultTime());
        return new TimeInstant(dateTime);
    }

    /**
     * Create result time from {@link Date}
     *
     * @param date
     *            {@link Date} to create result time from
     * @return result time
     */
    protected TimeInstant createResutlTime(Date date) {
        DateTime dateTime = new DateTime(date, DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    /**
     * Create valid time from min and max
     * {@link DataEntity}s
     *
     * @param minTime
     *            minimum {@link DataEntity}
     * @param maxTime
     *            maximum {@link DataEntity}
     * @return valid time or null if valid time is not set in datasource
     */
    protected Time createValidTime(DataEntity<?> minTime, DataEntity<?> maxTime) {
        // create time element
        if (minTime.getValidTimeStart() != null && maxTime.getValidTimeEnd() != null) {
            final DateTime startTime = DateTimeHelper.makeDateTime(minTime.getValidTimeStart());
            DateTime endTime = DateTimeHelper.makeDateTime(minTime.getValidTimeEnd());
            return GmlHelper.createTime(startTime, endTime);
        }
        return null;
    }

    /**
     * Create {@link TimePeriod} from {@link Date}s
     *
     * @param start
     *            Start {@link Date}
     * @param end
     *            End {@link Date}
     * @return {@link TimePeriod} or null if {@link Date}s are null
     */
    protected TimePeriod createValidTime(Date start, Date end) {
        // create time element
        if (start != null && end != null) {
            final DateTime startTime = new DateTime(start, DateTimeZone.UTC);
            DateTime endTime = new DateTime(end, DateTimeZone.UTC);
            return new TimePeriod(startTime, endTime);
        }
        return null;
    }

    /**
     * Create {@link Time} from {@link DateTime}s
     *
     * @param start
     *            Start {@link DateTime}
     * @param end
     *            End {@link DateTime}
     * @return Resulting {@link Time}
     */
    protected Time createTime(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return new TimeInstant(start);
        } else {
            return new TimePeriod(start, end);
        }
    }

    /**
     * Create a {@link TimeValuePair} from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link DataEntity} to create
     *            {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    protected TimeValuePair createTimeValuePairFrom(DataEntity<?> abstractValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(abstractValue),
                new ObservationValueCreator(daoFactory.getDecoderRepository()).visit(abstractValue));
    }

    protected NamedValue<?> createDetectionLimit(DetectionLimitEntity detectionLimit, UoM uoM) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        final ReferenceType referenceType =
                new ReferenceType(detectionLimit.getFlag() > 0 ? "exceed limit" : "below limit");
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(detectionLimit.getDetectionLimit(), uoM));
        return namedValue;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Add {@code DataEntity} data to {@code OmObservation}
     *
     * @param observation
     *            {@link OmObservation} to add data
     * @param responseFormat
     *
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public OmObservation addValuesToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(o.getId()));
        if (!observation.isSetIdentifier() && o.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(o.getIdentifier());
            if (o.isSetIdentifierCodespace()) {
                identifier.setCodeSpace(o.getIdentifierCodespace().getName());
            }
            observation.setIdentifier(identifier);
        }
        if (!observation.isSetName() && o.isSetName()) {
            CodeType name = new CodeType(o.getName());
            if (o.isSetNameCodespace()) {
                try {
                    name.setCodeSpace(new URI(o.getNameCodespace().getName()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Invalid codespace value: {}",
                            o.getNameCodespace().getName());
                }
            }
            observation.setName(name);
        }
        if (!observation.isSetDescription() && o.isSetDescription()) {
            observation.setDescription(o.getDescription());
        }
        Value<?> value = new ObservationValueCreator(daoFactory.getDecoderRepository()).visit(o);
        if (!value.isSetUnit()
                && observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
            value.setUnit(((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                    .getUnit());
        }
        if (!value.isSetValue() && o.hasDetectionLimit()) {
            observation.addParameter(createDetectionLimit(o.getDetectionLimit(), value.getUnitObject()));
        }
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(o.getResultTime()));
        observation.setValidTime(createValidTime(o.getValidTimeStart(), o.getValidTimeEnd()));
        if (o.isSetGeometryEntity()) {
            observation.addParameter(spatialFilteringProfileCreator.create(o.getGeometryEntity()
                    .getGeometry()));
        }
        if (o.getDataset().hasVerticalMetadata()) {
            VerticalMetadataEntity verticalMetadata = o.getDataset().getVerticalMetadata();
            if (o.hasVerticalInterval()) {
                observation.addParameter(createParameter(getVerticalFromName(verticalMetadata), o.getVerticalFrom(),
                        verticalMetadata.getVerticalUnit()));
                observation.addParameter(createParameter(getVerticalToName(verticalMetadata), o.getVerticalTo(),
                        verticalMetadata.getVerticalUnit()));
            } else {
                observation.addParameter(createParameter(getVerticalFromName(verticalMetadata), o.getVerticalFrom(),
                        verticalMetadata.getVerticalUnit()));
            }
        }
        addRelatedObservation(o, observation);
        addParameter(o, observation);
        addValueSpecificDataToObservation(o, observation, responseFormat);
        addObservationValueToObservation(o, observation, value, responseFormat);
        return observation;
    }

    private String getVerticalFromName(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.isSetVerticalFromName() ? verticalMetadata.getVerticalFromName()
                : getVerticalToName(verticalMetadata);
    }

    private String getVerticalToName(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.isSetVerticalToName() ? verticalMetadata.getVerticalToName()
                : getNameFromOrientation(verticalMetadata);
    }

    private String getNameFromOrientation(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.getOrientation() != null && verticalMetadata.getOrientation() > 0 ? "height" : "depth";
    }

    private NamedValue<BigDecimal> createParameter(String name, BigDecimal value, UnitEntity unit) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(value, unit != null ? unit.getUnit() : "m"));
        return namedValue;
    }

    public void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, Session session,
            Extensions extensions) throws OwsExceptionReport {
        if (o.hasEreportingProfile()) {
            if (ReportObligations.hasFlow(extensions)) {
                ReportObligationType flow = ReportObligations.getFlow(extensions);
                if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                    int year = DateTimeHelper.makeDateTime(o.getSamplingTimeStart()).getYear();
                    EReportingQualityEntity eReportingQuality = new EReportingQualityDAO().getEReportingQuality(
                            o.getDataset().getId(), year, o.getEreportingProfile().getPrimaryObservation(), session);
                    if (eReportingQuality != null) {
                        observation.setResultQuality(helper.getGmdDomainConsistency(eReportingQuality, true));
                    } else {
                        observation
                                .setResultQuality(helper.getGmdDomainConsistency(new EReportingQualityEntity(), true));
                    }
                }
            }
        }
    }

    protected void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        // nothing to do
    }

    protected void addRelatedObservation(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new RelatedObservationAdder(observation, o, daoFactory.getServiceURL(),
                bindingRepository.isActive(MediaTypes.APPLICATION_KVP)).add();
    }

    protected void addParameter(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new ParameterAdder(observation, o).add();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SingleObservationValue getSingleObservationValue(DataEntity<?> o, Value<?> value)
            throws OwsExceptionReport {
        return new SingleObservationValue(createPhenomenonTime(o), value);
    }

    public OmObservation mergeValueToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat) && o.hasEreportingProfile()) {
            if (!observation.isSetValue()) {
                addValuesToObservation(o, observation, responseFormat);
            } else {
                checkTime(o, observation);
                helper.mergeValues((SweDataArray) observation.getValue().getValue().getValue(),
                        helper.createSweDataArray(observation, o));
            }
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                    .equals(observation.getObservationConstellation().getObservationType())) {
                observation.getObservationConstellation()
                        .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }

        } else {
            if (!observation.isSetValue()) {
                addValuesToObservation(o, observation, responseFormat);
            } else {
                // TODO
                if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                        .equals(observation.getObservationConstellation().getObservationType())) {
                    observation.getObservationConstellation()
                            .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
                }
                observation.mergeWithObservation(getSingleObservationValue(o,
                        new ObservationValueCreator(daoFactory.getDecoderRepository()).visit(o)));
            }
        }
        return observation;
    }

    public void addObservationValueToObservation(DataEntity<?> o, OmObservation observation, Value<?> value,
            String responseFormat) throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat)) {
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                    .equals(observation.getObservationConstellation().getObservationType())) {
                observation.getObservationConstellation()
                        .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }
            observation.setValue(helper.createSweDataArrayValue(observation, o));
        } else {
            observation.setValue(getSingleObservationValue(o, value));
        }
    }

    private boolean checkResponseFormat(String responseFormat) {
        return AqdConstants.NS_AQD.equals(responseFormat);
    }

    public String getDiscriminator(DataEntity<?> o) {
        if (o.hasEreportingProfile()) {
            return o.getEreportingProfile().getPrimaryObservation();
        }
        return null;
    }

    private void checkTime(DataEntity<?> o, OmObservation observation) {
        if (observation.isSetValue()) {
            Time obsPhenTime = observation.getValue().getPhenomenonTime();
            Time valuePhenTime = createPhenomenonTime(o);
            if (obsPhenTime != null) {
                TimePeriod timePeriod;
                if (obsPhenTime instanceof TimePeriod) {
                    timePeriod = (TimePeriod) obsPhenTime;
                } else {
                    timePeriod = new TimePeriod();
                    timePeriod.extendToContain(obsPhenTime);
                }
                timePeriod.extendToContain(valuePhenTime);
                observation.getValue().setPhenomenonTime(timePeriod);
            } else {
                observation.getValue().setPhenomenonTime(valuePhenTime);
            }
        }
        TimeInstant rt = createResutlTime(o.getResultTime());
        if (observation.getResultTime().getValue().isBefore(rt.getValue())) {
            observation.setResultTime(rt);
        }
        if (isSetValidTime()) {
            TimePeriod vt = createValidTime(o.getValidTimeStart(), o.getValidTimeEnd());
            if (observation.isSetValidTime()) {
                observation.getValidTime().extendToContain(vt);
            } else {
                observation.setValidTime(vt);
            }
        }
    }

}

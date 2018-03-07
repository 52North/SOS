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
package org.n52.sos.ds.hibernate.values;

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
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ereporting.EReportingQualityEntity;
import org.n52.series.db.beans.ereporting.HiberanteEReportingRelations.EReportingData;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
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
import org.n52.shetland.ogc.om.values.GeometryValue;
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
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSConverter;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.util.GmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import org.locationtech.jts.geom.Geometry;

/**
 * Abstract class for Hibernate streaming values
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingValue extends StreamingValue<DataEntity<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateStreamingValue.class);
    protected final HibernateSessionHolder sessionHolder;
    protected Session session;
    protected final AbstractObservationRequest request;
    protected Criterion temporalFilterCriterion;
    private final DaoFactory daoFactory;
    private final EReportingHelper helper;
    protected final DecoderRepository decoderRepository;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param daoFactory the DAO factory
     * @param connectionProvider the connection provider
     */
    public AbstractHibernateStreamingValue(
            ConnectionProvider connectionProvider, DaoFactory daoFactory, AbstractObservationRequest request,
            DecoderRepository decoderRepository) {
        this.request = request;
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
        this.daoFactory = daoFactory;
        this.helper = new EReportingHelper(daoFactory.getSweHelper());
        this.decoderRepository = decoderRepository;
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

    private void addSpecificValuesToObservation(OmObservation observation, DataEntity<?> value, Extensions extensions) {
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
     * Create a {@link TimeValuePair} from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link AbstractValueObservation} to create {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    protected TimeValuePair createTimeValuePairFrom(DataEntity<?> abstractValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(abstractValue), new ObservationValueCreator(decoderRepository).visit(abstractValue));
    }

    /**
     * Add {@link DataEntity} data to {@link OmObservation}
     *
     * @param observation
     *            {@link OmObservation} to add data
     * @param abstractValue
     *            {@link DataEntity} to get data from
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Deprecated
    protected void addValuesToObservation(OmObservation observation, DataEntity<?> abstractValue)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(abstractValue.getId()));
        if (abstractValue.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(abstractValue.getIdentifier());
            if (abstractValue.isSetIdentifierCodespace()) {
                identifier.setCodeSpace(abstractValue.getIdentifierCodespace().getName());
            }
            observation.setIdentifier(identifier);
        }
        if (abstractValue.isSetDescription()) {
            observation.setDescription(abstractValue.getDescription());
        }
        Value<?> value = new ObservationValueCreator(decoderRepository).visit(abstractValue);
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(abstractValue.getResultTime()));
        observation.setValidTime(createValidTime(abstractValue.getValidTimeStart(), abstractValue.getValidTimeEnd()));
        observation.setValue(new SingleObservationValue(createPhenomenonTime(abstractValue), value));
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
        for (DataEntity abstractValue : abstractValuesResult) {
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
        final DateTime phenStartTime = new DateTime(abstractValue.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (abstractValue.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(abstractValue.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create phenomenon time from min and max {@link AbstractTemporalReferencedObservation}s
     *
     * @param minTime
     *            minimum {@link AbstractTemporalReferencedObservation}
     * @param maxTime
     *            maximum {@link AbstractTemporalReferencedObservation}
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(DataEntity<?> minTime, DataEntity<?> maxTime) {
        // create time element

        final DateTime phenStartTime = DateTimeHelper.makeDateTime(minTime.getPhenomenonTimeStart());
        DateTime phenEndTime;
        if (maxTime.getPhenomenonTimeEnd() != null) {
            phenEndTime = DateTimeHelper.makeDateTime(minTime.getPhenomenonTimeEnd());
        } else {
            phenEndTime = phenStartTime;
        }
        return GmlHelper.createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create result time from {@link AbstractTemporalReferencedObservation}
     *
     * @param maxTime
     *            {@link AbstractTemporalReferencedObservation} to create result time from
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
     * Create valid time from min and max {@link AbstractTemporalReferencedObservation}s
     *
     * @param minTime
     *            minimum {@link AbstractTemporalReferencedObservation}
     * @param maxTime
     *            maximum {@link AbstractTemporalReferencedObservation}
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
     * Get internal {@link Value} from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link DataEntity} to get {@link Value} from
     * @return {@link Value} or null if the concrete {@link DataEntity} is
     *         not supported
     * @throws OwsExceptionReport
     *             If an error occurs when creating
     *             {@link org.n52.shetland.ogc.om.values.SweDataArrayValue}
     *
     * User {@link Observation#accept(org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor)}
     */
    @Deprecated
    protected Value<?> getValueFrom(DataEntity<?> abstractValue) throws OwsExceptionReport {
        Value<?> value = new ObservationValueCreator(decoderRepository).visit(abstractValue);
//        if (value != null && abstractValue.isSetUnit()) {
//            value.setUnit(abstractValue.getUnit().getUnit());
//        }
        return value;
    }

    @Deprecated
    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(new GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Add {@code AbstractValue} data to {@code OmObservation}
     *
     * @param observation {@link OmObservation} to add data
     * @param responseFormat
     *
     * @throws OwsExceptionReport If an error occurs when getting the value
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
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Invalid codespace value: {}", o.getNameCodespace().getName());
                }
            }
            observation.setName(name);
        }
        if (!observation.isSetDescription() && o.isSetDescription()) {
            observation.setDescription(o.getDescription());
        }
        Value<?> value = new ObservationValueCreator(decoderRepository).visit(o);
        if (!value.isSetUnit()
                && observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
            value.setUnit( ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .getUnit());
        }
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(o.getResultTime()));
        observation.setValidTime(createValidTime(o.getValidTimeStart(), o.getValidTimeEnd()));
        if (o.isSetGeometryEntity()) {
            observation.addParameter(createSpatialFilteringProfileParameter(JTSConverter.convert(o.getGeometryEntity().getGeometry())));
        }
        addRelatedObservation(o, observation);
        addParameter(o, observation);
        addValueSpecificDataToObservation(o, observation, responseFormat);
        addObservationValueToObservation(o, observation, value, responseFormat);
        return observation;
    }

    protected void addRelatedObservation(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new RelatedObservationAdder(observation, o).add();
    }

    protected void addParameter(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new ParameterAdder(observation, o).add();
    }

    protected void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, String responseFormat) throws
    OwsExceptionReport {
    // nothing to do
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SingleObservationValue getSingleObservationValue(DataEntity<?> o, Value<?> value) throws OwsExceptionReport {
        return new SingleObservationValue(createPhenomenonTime(o), value);
    }

    public OmObservation mergeValueToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat) && o instanceof EReportingData) {
            if (!observation.isSetValue()) {
                addValuesToObservation(o, observation, responseFormat);
            } else {
                checkTime(o, observation);
                helper.mergeValues((SweDataArray) observation.getValue().getValue().getValue(),
                        helper.createSweDataArray(observation, (EReportingData) o));
            }
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observation.getObservationConstellation()
                    .getObservationType())) {
                observation.getObservationConstellation().setObservationType(
                        OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
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
                observation.mergeWithObservation(getSingleObservationValue(o, new ObservationValueCreator(decoderRepository).visit(o)));
            }
        }
        return observation;
    }

    public void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, Session session, Extensions extensions)
            throws OwsExceptionReport {
        if (o instanceof EReportingData) {
            if (ReportObligations.hasFlow(extensions)) {
                ReportObligationType flow = ReportObligations.getFlow(extensions);
                if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                    int year = DateTimeHelper.makeDateTime(o.getPhenomenonTimeStart()).getYear();
                    EReportingQualityEntity eReportingQuality =
                            new EReportingQualityDAO().getEReportingQuality(o.getDataset().getId(), year,
                                    ((EReportingData)o).getPrimaryObservation(), session);
                    if (eReportingQuality != null) {
                        observation.setResultQuality(helper.getGmdDomainConsistency(eReportingQuality, true));
                    } else {
                        observation.setResultQuality(helper.getGmdDomainConsistency(new EReportingQualityEntity(), true));
                    }
                }
            }
        }
    }

    public void addObservationValueToObservation(DataEntity<?> o, OmObservation observation, Value<?> value, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat)) {
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observation.getObservationConstellation()
                    .getObservationType())) {
                observation.getObservationConstellation().setObservationType(
                        OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }
            observation.setValue(helper.createSweDataArrayValue(observation, (EReportingData) o));
        } else {
            observation.setValue(getSingleObservationValue(o, value));
        }
    }

    private boolean checkResponseFormat(String responseFormat) {
        return AqdConstants.NS_AQD.equals(responseFormat);
    }

    public String getDiscriminator(DataEntity<?> o) {
        if (o instanceof EReportingData) {
            return ((EReportingData) o).getPrimaryObservation();
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

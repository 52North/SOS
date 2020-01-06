/**
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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.entities.observation.AbstractTemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.BaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.valued.SweDataArrayValuedLegacyObservation;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;
import org.n52.sos.ds.hibernate.util.observation.PhenomenonTimeCreator;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.OMHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract class for Hibernate streaming values
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingValue extends StreamingValue<AbstractValuedLegacyObservation<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateStreamingValue.class);

    private static final long serialVersionUID = -8355955808723620476L;

    protected final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private Session session;

    protected final AbstractObservationRequest request;

    protected Criterion temporalFilterCriterion;
    
    protected Session getSession() throws OwsExceptionReport {
        if (session  == null) {
            session = sessionHolder.getSession();
        }
        return session;
    }

    @Override
    public Collection<OmObservation> mergeObservation() throws OwsExceptionReport {
        Map<String, OmObservation> observations = Maps.newHashMap();
        while (hasNextValue()) {
            AbstractValuedLegacyObservation<?> nextEntity = nextEntity();
            if (nextEntity != null) {
                boolean mergableObservationValue = checkForMergability(nextEntity);
                OmObservation observation = null;
                String key = getKey(nextEntity);
                if (observations.containsKey(key) && mergableObservationValue) {
                    observation = observations.get(key);
                } else {
                    observation = observationTemplate.cloneTemplate(true);
                    addSpecificValuesToObservation(observation, nextEntity, request.getExtensions());
                    if (!mergableObservationValue && nextEntity.getDiscriminator() == null) {
                        observations.put(Long.toString(nextEntity.getObservationId()), observation);
                    } else {
                        observations.put(key, observation);
                    }
                }
                nextEntity.mergeValueToObservation(observation, getResponseFormat());
                getSession().evict(nextEntity);
            }
        }
        return observations.values();
    }

    private String getKey(AbstractValuedLegacyObservation<?> nextEntity) {
        if (nextEntity.getDiscriminator() != null) {
            return nextEntity.getDiscriminator();
        } else if (getObservationMergeIndicator() != null && getObservationMergeIndicator().isSetResultTime()) {
            return nextEntity.getResultTime().toString();
        }
        return null;
    }

    private boolean checkForMergability(AbstractValuedLegacyObservation<?> nextEntity) {
        return !(nextEntity instanceof SweDataArrayValuedLegacyObservation);
    }

    private void addSpecificValuesToObservation(OmObservation observation, AbstractValuedLegacyObservation<?> value,
            SwesExtensions swesExtensions) {
        boolean newSession = false;
        try {
            if (session == null) {
                session = sessionHolder.getSession();
                newSession = true;
            }
            value.addValueSpecificDataToObservation(observation, session, swesExtensions);
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        } finally {
            if (newSession) {
                sessionHolder.returnSession(session);
            }
        }
    }

    /**
     * constructor
     *
     * @param request
     *            {@link AbstractObservationRequest}
     */
    public AbstractHibernateStreamingValue(AbstractObservationRequest request) {
        this.request = request;
    }

    /**
     * Set the observation template which contains all metadata
     *
     * @param observationTemplate
     *            Observation template to set
     */
    public void setObservationTemplate(OmObservation observationTemplate) {
        this.observationTemplate = observationTemplate;
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
     * Create a {@link TimeValuePair} from {@link AbstractValuedLegacyObservation}
     *
     * @param abstractValue
     *            {@link AbstractValuedLegacyObservation} to create {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    protected TimeValuePair createTimeValuePairFrom(ValuedObservation<?> abstractValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(abstractValue), abstractValue.accept(new ObservationValueCreator()));
    }

    /**
     * Add {@link AbstractValuedLegacyObservation} data to {@link OmObservation}
     *
     * @param observation
     *            {@link OmObservation} to add data
     * @param abstractValue
     *            {@link AbstractValuedLegacyObservation} to get data from
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Deprecated
    protected void addValuesToObservation(OmObservation observation, ValuedObservation<?> abstractValue)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(abstractValue.getObservationId()));
        if (abstractValue.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(abstractValue.getIdentifier());
            if (abstractValue.isSetCodespace()) {
                identifier.setCodeSpace(abstractValue.getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (abstractValue.isSetDescription()) {
            observation.setDescription(abstractValue.getDescription());
        }
        Value<?> value = abstractValue.accept(new ObservationValueCreator());
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(abstractValue.getResultTime()));
        observation.setValidTime(createValidTime(abstractValue.getValidTimeStart(), abstractValue.getValidTimeEnd()));
        observation.setValue(new SingleObservationValue(createPhenomenonTime(abstractValue), value));
    }

    /**
     * Get the observation ids from {@link AbstractValuedLegacyObservation}s
     *
     * @param abstractValuesResult
     *            {@link AbstractValuedLegacyObservation}s to get ids from
     * @return Set with ids
     */
    protected Set<Long> getObservationIds(Collection<? extends BaseObservation> abstractValuesResult) {
        Set<Long> ids = new HashSet<>(abstractValuesResult.size());
        for (BaseObservation abstractValue : abstractValuesResult) {
            ids.add(abstractValue.getObservationId());
        }
        return ids;
    }

    /**
     * Create the phenomenon time from {@link AbstractValuedLegacyObservation}
     *
     * @param abstractValue
     *            {@link AbstractValuedLegacyObservation} for get time from
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(TemporalReferencedObservation abstractValue) {
        return new PhenomenonTimeCreator(abstractValue).create();
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
    protected Time createPhenomenonTime(TemporalReferencedObservation minTime, TemporalReferencedObservation maxTime) {
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
    protected TimeInstant createResutlTime(TemporalReferencedObservation maxTime) {
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
    protected Time createValidTime(TemporalReferencedObservation minTime, TemporalReferencedObservation maxTime) {
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
     * Get internal {@link Value} from {@link AbstractValuedLegacyObservation}
     *
     * @param abstractValue
     *            {@link AbstractValuedLegacyObservation} to get {@link Value} from
     * @return {@link Value} or null if the concrete {@link AbstractValuedLegacyObservation} is
     *         not supported
     * @throws OwsExceptionReport
     *             If an error occurs when creating
     *             {@link org.n52.sos.ogc.om.values.SweDataArrayValue}
     *             
     * User {@link Observation#accept(org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor)}
     */
    @Deprecated
    protected Value<?> getValueFrom(ValuedObservation<?> abstractValue) throws OwsExceptionReport {
        Value<?> value = abstractValue.accept(new ObservationValueCreator());
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
        namedValue.setValue(new org.n52.sos.ogc.om.values.GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

}

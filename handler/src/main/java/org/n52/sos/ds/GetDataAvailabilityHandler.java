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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.io.request.IoParameters;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.series.db.old.dao.DatasetDao;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.FormatDescriptor;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.ObservationFormatDescriptor;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.ProcedureDescriptionFormatDescriptor;
import org.n52.sos.ds.dao.GetDataAvailabilityDao;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class GetDataAvailabilityHandler extends AbstractGetDataAvailabilityHandler
        implements ApiQueryHelper, DatabaseQueryHelper {

    private HibernateSessionStore sessionStore;

    private Optional<GetDataAvailabilityDao> dao = Optional.empty();

    private DbQueryFactory dbQueryFactory;

    public GetDataAvailabilityHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setGetDataAvaolabilityDao(Optional<GetDataAvailabilityDao> getDataAvailabilityDao) {
        if (getDataAvailabilityDao != null) {
            this.dao = getDataAvailabilityDao;
        }
    }

    @Inject
    public void setDbQueryFactory(DbQueryFactory dbQueryFactory) {
        this.dbQueryFactory = dbQueryFactory;
    }

    @Override
    @Transactional()
    public GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest request)
            throws OwsExceptionReport {
        GetDataAvailabilityResponse response = new GetDataAvailabilityResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        response.setResponseFormat(request.getResponseFormat());
        if (checkForGDAv20(request)) {
            response.setResponseFormat(GetDataAvailabilityConstants.NS_GDA_20);
        }
        for (DataAvailability da : queryDataAvailabilityValues(request)) {
            if (da != null) {
                response.addDataAvailability(da);
            }
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private List<DataAvailability> queryDataAvailabilityValues(GetDataAvailabilityRequest request)
            throws OwsExceptionReport {
        Session session = sessionStore.getSession();
        try {
            GDARequestContext context = new GDARequestContext(request);
            boolean gdaV20 = checkForGDAv20(request);
            for (final DatasetEntity entity : new DatasetDao<>(session).getAllInstances(createDbQuery(request))) {
                if (gdaV20) {
                    processDataAvailabilityV2(entity, context, session);
                } else {
                    processDataAvailability(entity, context, session);
                }
            }
            if (!gdaV20) {
                return checkForDuplictation(context.getDataAvailabilityList());
            }
            return context.getDataAvailabilityList();
        } finally {
            sessionStore.returnSession(session);
        }
    }

    private DataAvailability defaultProcessDataAvailability(DatasetEntity entity, GDARequestContext context,
            Session session) throws OwsExceptionReport {
        TimePeriod timePeriod = createTimePeriod(entity);
        if (timePeriod != null && !timePeriod.isEmpty()) {
            DataAvailability dataAvailability = new DataAvailability(getProcedureReference(entity, context),
                    getObservedPropertyReference(entity, context), getFeatureOfInterestReference(entity, context),
                    getOfferingReference(entity, context), timePeriod);
            if (isShowCount(context.getRequest()) && entity.getObservationCount() >= 0) {
                dataAvailability.setCount(entity.getObservationCount());
            }
            if (isIncludeResultTime(context.getRequest()) && dao.isPresent()) {
                dataAvailability.setResultTimes(dao.get()
                        .getResultTimes(dataAvailability, context.getRequest(), session));
            }
            return dataAvailability;
        }
        return null;
    }

    /**
     * Get {@link DataAvailability}s for each series
     *
     * @param entity
     *            the {@link DatasetEntity} to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailability(DatasetEntity entity, GDARequestContext context, Session session)
            throws OwsExceptionReport {
        DataAvailability dataAvailability = defaultProcessDataAvailability(entity, context, session);
        if (dataAvailability != null) {
            context.addDataAvailability(dataAvailability);
        }
    }

    /**
     * Get {@link DataAvailability}s for each offering of a series
     *
     * @param entity
     *            the {@link DatasetEntity} to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailabilityV2(DatasetEntity entity, GDARequestContext context, Session session)
            throws OwsExceptionReport {
        DataAvailability dataAvailability = defaultProcessDataAvailability(entity, context, session);
        if (dataAvailability != null) {
            dataAvailability.setFormatDescriptor(getFormatDescriptor(context, entity));
            if (dao.isPresent()) {
                dataAvailability.setMetadata(dao.get()
                        .getMetadata(dataAvailability, session));
            }
            context.addDataAvailability(dataAvailability);
        }
        checkForParentOfferings(context, entity.getOffering());
    }

    private TimePeriod createTimePeriod(DatasetEntity entity) {
        return new TimePeriod(entity.getFirstValueAt(), entity.getLastValueAt());
    }

    private void checkForParentOfferings(GDARequestContext context, OfferingEntity offeringEntity) {
        if (context.isSetDataAvailabilityList()) {
            List<String> requestedOfferings = context.getRequest().getOfferings();
            for (String requestedOffering : requestedOfferings) {
                Set<String> childOfferings = getChildOfferings(offeringEntity);
                if (!childOfferings.isEmpty()) {
                    if (context.hasDataAvailability(requestedOffering)) {
                        Set<DataAvailability> parentDataAvailabilities =
                                context.getDataAvailability(requestedOffering);
                        for (String childOffering : childOfferings) {
                            Set<DataAvailability> childDataAvailabilities = context.getDataAvailability(childOffering);
                            for (DataAvailability childDataAvailability : childDataAvailabilities) {
                                for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
                                    parentDataAvailability.merge(childDataAvailability, true);
                                }
                            }
                        }
                    } else {
                        Set<DataAvailability> parentDataAvailabilities = Sets.newHashSet();
                        for (String childOffering : childOfferings) {
                            Set<DataAvailability> childDataAvailabilities = context.getDataAvailability(childOffering);
                            for (DataAvailability childDataAvailability : childDataAvailabilities) {
                                addParentDataAvailabilityIfMissing(parentDataAvailabilities, childDataAvailability,
                                        new ReferenceType(requestedOffering));
                                for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
                                    parentDataAvailability.merge(childDataAvailability, true);
                                }
                            }
                        }
                        context.addDataAvailabilities(parentDataAvailabilities);
                    }
                }
            }
        }
    }

    private Set<String> getChildOfferings(OfferingEntity offering) {
        Set<String> childs = Sets.newTreeSet();
        if (offering.hasChildren()) {
            for (OfferingEntity child : offering.getChildren()) {
                childs.add(child.getIdentifier());
                childs.addAll(getChildOfferings(child));
            }
        }
        return childs;
    }

    private boolean checkForGDAv20(GetDataAvailabilityRequest request) {
        return request.isSetResponseFormat()
                && GetDataAvailabilityConstants.NS_GDA_20.equals(request.getResponseFormat())
                || GetDataAvailabilityConstants.NS_GDA_20.equals(request.getNamespace()) || isForceGDAv20Response();
    }

    private List<DataAvailability> checkForDuplictation(List<DataAvailability> dataAvailabilityValues) {
        List<DataAvailability> checked = Lists.newLinkedList();
        for (DataAvailability dataAvailability : dataAvailabilityValues) {
            if (checked.isEmpty()) {
                checked.add(dataAvailability);
            } else {
                boolean notDuplicated = true;
                for (DataAvailability checkedDA : checked) {
                    if (dataAvailability.equals(checkedDA)) {
                        checkedDA.getPhenomenonTime().extendToContain(dataAvailability.getPhenomenonTime());
                        notDuplicated = false;
                    }
                }
                if (notDuplicated) {
                    checked.add(dataAvailability);
                }
            }
        }
        return checked;
    }

    private ReferenceType getProcedureReference(DatasetEntity entity, GDARequestContext context) {
        String identifier = entity.getProcedure().getIdentifier();
        if (!context.hasProcedures(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getProcedure().isSetName()) {
                referenceType.setTitle(entity.getProcedure().getName());
            }
            context.addProcedures(identifier, referenceType);
        }
        return context.getProcedure(identifier);
    }

    private ReferenceType getObservedPropertyReference(DatasetEntity entity, GDARequestContext context) {
        String identifier = entity.getPhenomenon().getIdentifier();
        if (!context.hasObservableProperties(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getPhenomenon().isSetName()) {
                referenceType.setTitle(entity.getPhenomenon().getName());
            }
            context.addObservableProperties(identifier, referenceType);
        }
        return context.getObservableProperty(identifier);
    }

    private ReferenceType getFeatureOfInterestReference(DatasetEntity entity, GDARequestContext context) {
        String identifier = entity.getFeature().getIdentifier();
        if (!context.hasFeaturesOfInterest(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getFeature().isSetName()) {
                referenceType.setTitle(entity.getFeature().getName());
            }
            context.addFeaturesOfInterest(identifier, referenceType);
        }
        return context.getFeatureOfInterest(identifier);
    }

    private ReferenceType getOfferingReference(DatasetEntity entity, GDARequestContext context) {
        String identifier = entity.getOffering().getIdentifier();
        if (!context.hasOfferings(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getOffering().isSetName()) {
                referenceType.setTitle(entity.getOffering().getName());
            }
            context.addOfferings(identifier, referenceType);
        }
        return context.getOffering(identifier);
    }

    /**
     * Check if optional count should be added
     *
     * @param request
     *            GetDataAvailability request
     * @return <code>true</code>, if optional count should be added
     */
    private boolean isShowCount(GetDataAvailabilityRequest request) {
        if (request.hasExtension(SHOW_COUNT)) {
            return request.getBooleanExtension(SHOW_COUNT);
        }
        return isForceValueCount();
    }

    /**
     * Check if result times should be added
     *
     * @param request
     *            GetDataAvailability request
     * @return <code>true</code>, if result times should be added
     */
    private boolean isIncludeResultTime(GetDataAvailabilityRequest request) {
        if (request.hasExtension(INCLUDE_RESULT_TIMES)) {
            return request.getBooleanExtension(INCLUDE_RESULT_TIMES)
                    || hasPhenomenonTimeFilter(request.getExtensions());
        }
        return false;
    }

    /**
     * Check if extensions contains a temporal filter with valueReference phenomenonTime
     *
     * @param extensions
     *            Extensions to check
     * @return <code>true</code>, if extensions contains a temporal filter with valueReference phenomenonTime
     */
    private boolean hasPhenomenonTimeFilter(Extensions extensions) {
        boolean hasFilter = false;
        for (Extension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof TemporalFilter) {
                TemporalFilter filter = (TemporalFilter) extension.getValue();
                if (SosConstants.PHENOMENON_TIME_VALUE_REFERENCE.equals(filter.getValueReference())) {
                    hasFilter = true;
                }
            }
        }
        return hasFilter;
    }

    private FormatDescriptor getFormatDescriptor(GDARequestContext context, DatasetEntity entity) {
        return new FormatDescriptor(getProcedureDescriptionFormatDescriptor(entity.getProcedure()),
                getObservationFormatDescriptors(entity, context));
    }

    private ProcedureDescriptionFormatDescriptor getProcedureDescriptionFormatDescriptor(ProcedureEntity procedure) {
        String format = SensorML20Constants.NS_SML_20;
        if (procedure.getFormat() != null && procedure.getFormat().isSetFormat()) {
            format = procedure.getFormat().getFormat();
        }
        return new ProcedureDescriptionFormatDescriptor(format);
    }

    private Set<ObservationFormatDescriptor> getObservationFormatDescriptors(DatasetEntity entity,
            GDARequestContext context) {
        Map<String, Set<String>> responsFormatObservationTypesMap = Maps.newHashMap();
        Set<String> observationTypes = new HashSet<>();
        if (entity.isSetOffering() && entity.getOffering().hasObservationTypes()) {
            observationTypes.addAll(toStringSet(entity.getOffering().getObservationTypes()));
        } else {
            observationTypes.add(getObservationType(entity));
        }
        for (String observationType : observationTypes) {
            Set<String> responseFormats = getResponseFormatsForObservationType(observationType,
                    context.getRequest().getService(), context.getRequest().getVersion());
            for (String responseFormat : responseFormats) {
                if (responsFormatObservationTypesMap.containsKey(responseFormat)) {
                    responsFormatObservationTypesMap.get(responseFormat).add(observationType);
                } else {
                    responsFormatObservationTypesMap.put(responseFormat, Sets.newHashSet(observationType));
                }
            }
        }
        Set<ObservationFormatDescriptor> formatDescriptors = Sets.newHashSet();
        for (Entry<String, Set<String>> entry : responsFormatObservationTypesMap.entrySet()) {
            formatDescriptors.add(new ObservationFormatDescriptor(entry.getKey(), entry.getValue()));
        }
        return formatDescriptors;
    }

    private boolean addParentDataAvailabilityIfMissing(Set<DataAvailability> parentDataAvailabilities,
            DataAvailability childDataAvailability, ReferenceType offering) {
        boolean notContained = true;
        for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
            if (parentDataAvailability.sameConstellation(childDataAvailability)) {
                notContained = false;
            }
        }
        if (notContained) {
            parentDataAvailabilities.add(childDataAvailability.copy());
        }
        return notContained;
    }

    private DbQuery createDbQuery(GetDataAvailabilityRequest req) {
        Map<String, String> map = Maps.newHashMap();
        if (req.isSetFeaturesOfInterest()) {
            map.put(IoParameters.FEATURES, listToString(req.getFeaturesOfInterest()));
        }
        if (req.isSetProcedures()) {
            map.put(IoParameters.PROCEDURES, listToString(req.getProcedures()));
        }
        if (req.isSetObservedProperties()) {
            map.put(IoParameters.PHENOMENA, listToString(req.getObservedProperties()));
        }
        if (req.isSetOfferings()) {
            map.put(IoParameters.OFFERINGS, listToString(req.getOfferings()));
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        return createDbQuery(IoParameters.createFromSingleValueMap(map));
    }

    @Override
    public DbQuery createDbQuery(IoParameters parameters) {
        return dbQueryFactory.createFrom(parameters);
    }

    public static class GDARequestContext {
        private GetDataAvailabilityRequest request;

        private List<DataAvailability> dataAvailabilityValues = Lists.newArrayList();

        private Map<String, ReferenceType> procedures = new HashMap<>();

        private Map<String, ReferenceType> observableProperties = new HashMap<>();

        private Map<String, ReferenceType> featuresOfInterest = new HashMap<>();

        private Map<String, ReferenceType> offerings = new HashMap<>();

        public GDARequestContext(GetDataAvailabilityRequest request) {
            this.request = request;
        }

        public GetDataAvailabilityRequest getRequest() {
            return request;
        }

        public Map<String, ReferenceType> getFeaturesOfInterest() {
            return Collections.unmodifiableMap(featuresOfInterest);
        }

        public void addFeaturesOfInterest(String key, ReferenceType reference) {
            featuresOfInterest.put(key, reference);
        }

        public ReferenceType getFeatureOfInterest(String key) {
            return featuresOfInterest.get(key);
        }

        public boolean hasFeaturesOfInterest(String key) {
            return featuresOfInterest.containsKey(key);
        }

        public Map<String, ReferenceType> getObservableProperties() {
            return Collections.unmodifiableMap(observableProperties);
        }

        public void addObservableProperties(String key, ReferenceType reference) {
            observableProperties.put(key, reference);
        }

        public ReferenceType getObservableProperty(String key) {
            return observableProperties.get(key);
        }

        public boolean hasObservableProperties(String key) {
            return observableProperties.containsKey(key);
        }

        public Map<String, ReferenceType> getProcedures() {
            return Collections.unmodifiableMap(procedures);
        }

        public void addProcedures(String key, ReferenceType reference) {
            procedures.put(key, reference);
        }

        public ReferenceType getProcedure(String key) {
            return procedures.get(key);
        }

        public boolean hasProcedures(String key) {
            return procedures.containsKey(key);
        }

        public Map<String, ReferenceType> getOfferings() {
            return Collections.unmodifiableMap(offerings);
        }

        public void addOfferings(String key, ReferenceType reference) {
            offerings.put(key, reference);
        }

        public ReferenceType getOffering(String key) {
            return offerings.get(key);
        }

        public boolean hasOfferings(String key) {
            return offerings.containsKey(key);
        }

        public GDARequestContext setDataAvailabilityList(List<DataAvailability> dataAvailabilityValues) {
            this.dataAvailabilityValues.clear();
            return addDataAvailabilities(dataAvailabilityValues);
        }

        public GDARequestContext addDataAvailability(DataAvailability dataAvailability) {
            if (dataAvailability != null) {
                this.dataAvailabilityValues.add(dataAvailability);
            }
            return this;
        }

        public GDARequestContext addDataAvailabilities(Collection<DataAvailability> dataAvailabilityValues) {
            if (dataAvailabilityValues != null) {
                this.dataAvailabilityValues.addAll(dataAvailabilityValues);
            }
            return this;
        }

        public List<DataAvailability> getDataAvailabilityList() {
            return Lists.newArrayList(dataAvailabilityValues);
        }

        public boolean hasDataAvailability(String requestedOffering) {
            for (DataAvailability dataAvailability : dataAvailabilityValues) {
                if (requestedOffering.equals(dataAvailability.getOfferingString())) {
                    return true;
                }
            }
            return false;
        }

        public Set<DataAvailability> getDataAvailability(String offering) {
            Set<DataAvailability> das = Sets.newHashSet();
            for (DataAvailability dataAvailability : dataAvailabilityValues) {
                if (offering.equals(dataAvailability.getOfferingString())) {
                    das.add(dataAvailability);
                }
            }
            return das;
        }

        public boolean isSetDataAvailabilityList() {
            return getDataAvailabilityList() != null && !getDataAvailabilityList().isEmpty();
        }

    }
}

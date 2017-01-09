/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.coding.CodingRepository;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.proxy.db.dao.ProxyDatasetDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.FormatDescriptor;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.ObservationFormatDescriptor;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.ProcedureDescriptionFormatDescriptor;
import org.n52.sos.coding.encode.ObservationEncoder;
import org.n52.sos.ds.dao.GetDataAvailabilityDao;
import org.n52.svalbard.encode.Encoder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GetDataAvailabilityHandler extends AbstractGetDataAvailabilityHandler {

    private HibernateSessionStore sessionStore;
    private CodingRepository codingRepository;
    private GetDataAvailabilityDao dao;


    public GetDataAvailabilityHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setCodingRepository(CodingRepository codingRepository) {
        this.codingRepository = codingRepository;
    }

    @Autowired(required=false)
    public void setGetDataAvaolabilityDao(GetDataAvailabilityDao getDataAvailabilityDao) {
        this.dao = getDataAvailabilityDao;
    }

    @Override
    public GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest request)
            throws OwsExceptionReport {
        GetDataAvailabilityResponse response = new GetDataAvailabilityResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        response.setResponseFormat(request.getResponseFormat());
        for (DataAvailability da : queryDataAvailabilityValues(request)) {
            if (da != null) {
                response.addDataAvailability(da);
            }
        }
        return response;
    }

    private List<DataAvailability> queryDataAvailabilityValues(GetDataAvailabilityRequest request) throws OwsExceptionReport {
        Session session = sessionStore.getSession();
        try {
            GDARequestContext context = new GDARequestContext(request);
            boolean gdaV20 = checkForGDAv20(request);
            for (final DatasetEntity<?> entity : new ProxyDatasetDao<>(session).getAllInstances(createDbQuery(request))) {
                if (gdaV20) {
                    processDataAvailabilityV2(entity, context, session);
                    return context.getDataAvailabilityList();
                } else {
                    processDataAvailability(entity, context, session);
                    return checkForDuplictation(context.getDataAvailabilityList());
                }
            }
            return context.getDataAvailabilityList();
        } catch (DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for GetDataAvailability!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    private DbQuery createDbQuery(GetDataAvailabilityRequest req) {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        if (req.isSetFeaturesOfInterest()) {
            rsps.addParameter(IoParameters.FEATURES, IoParameters.getJsonNodeFrom(req.getFeaturesOfInterest()));
        }
        if (req.isSetProcedures()) {
            rsps.addParameter(IoParameters.PROCEDURES, IoParameters.getJsonNodeFrom(req.getProcedures()));
        }
        if (req.isSetObservedProperties()) {
            rsps.addParameter(IoParameters.PHENOMENA, IoParameters.getJsonNodeFrom(req.getObservedProperties()));
        }
        if (req.isSetOfferings()) {
            rsps.addParameter(IoParameters.OFFERINGS, IoParameters.getJsonNodeFrom(req.getOfferings()));
        }
        rsps.addParameter(IoParameters.MATCH_DOMAIN_IDS, IoParameters.getJsonNodeFrom(true));
        return new DbQuery(IoParameters.createFromQuery(rsps));
    }

    private DataAvailability defaultProcessDataAvailability(DatasetEntity<?> entity, GDARequestContext context, Session session) throws OwsExceptionReport {
            TimePeriod timePeriod = createTimePeriod(entity);
            if (timePeriod != null && !timePeriod.isEmpty()) {
                DataAvailability dataAvailability =
                        new DataAvailability(getProcedureReference(entity, context.getProcedures()), getObservedPropertyReference(
                                entity, context.getObservableProperties()), getFeatureOfInterestReference(entity,
                                        context.getFeaturesOfInterest()), getOfferingReference(entity, context.getOfferings()), timePeriod);
                if (isShowCount(context.getRequest()) && entity.getObservationCount() >= 0) {
                    dataAvailability.setCount(entity.getObservationCount());
                }
                if (isIncludeResultTime(context.getRequest()) && dao != null) {
                    dataAvailability.setResultTimes(dao.getResultTimes(dataAvailability, context.getRequest()));
                }
                return dataAvailability;
            }
            return null;
        }

    /**
     * Get {@link DataAvailability}s for each series
     *
     * @param series
     *            Series to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailability(DatasetEntity<?> entity, GDARequestContext context, Session session) throws OwsExceptionReport {
        DataAvailability dataAvailability = defaultProcessDataAvailability(entity, context, session);
        if (dataAvailability != null) {
            context.addDataAvailability(dataAvailability);
        }
    }

    /**
     * Get {@link DataAvailability}s for each offering of a series
     *
     * @param series
     *            Series to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailabilityV2(DatasetEntity<?> entity, GDARequestContext context, Session session)
            throws OwsExceptionReport {
        DataAvailability dataAvailability = defaultProcessDataAvailability(entity, context, session);
        if (dataAvailability != null) {
            dataAvailability.setFormatDescriptor(getFormatDescriptor(context, entity));
            if (dao != null) {
                dataAvailability.setMetadata(dao.getMetadata(dataAvailability));
            }
            context.addDataAvailability(dataAvailability);
        }
        checkForParentOfferings(context, entity.getOffering());
    }

    private TimePeriod createTimePeriod(DatasetEntity<?> entity) {
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
      if (offering.hasChilds()) {
          for (OfferingEntity child : offering.getChilds()) {
              childs.add(child.getDomainId());
              childs.addAll(getChildOfferings(child));
          }
      }
      return childs;
    }

    private boolean checkForGDAv20(GetDataAvailabilityRequest request) {
        return (request.isSetResponseFormat() && GetDataAvailabilityConstants.NS_GDA_20.equals(request.getResponseFormat()))
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

    private ReferenceType getProcedureReference(DatasetEntity<?> entity, Map<String, ReferenceType> procedures) {
        String identifier = entity.getProcedure().getDomainId();
        if (!procedures.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getProcedure().isSetName()) {
                referenceType.setTitle(entity.getProcedure().getName());
            }
            procedures.put(identifier, referenceType);
        }
        return procedures.get(identifier);
    }

    private ReferenceType getObservedPropertyReference(DatasetEntity<?> entity, Map<String, ReferenceType> observableProperties) {
        String identifier = entity.getPhenomenon().getDomainId();
        if (!observableProperties.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getPhenomenon().isSetName()) {
                referenceType.setTitle(entity.getPhenomenon().getName());
            }
            observableProperties.put(identifier, referenceType);
        }
        return observableProperties.get(identifier);
    }

    private ReferenceType getFeatureOfInterestReference(DatasetEntity<?> entity, Map<String, ReferenceType> featuresOfInterest) {
        String identifier = entity.getFeature().getDomainId();
        if (!featuresOfInterest.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getFeature().isSetName() ) {
                referenceType.setTitle(entity.getFeature().getName());
            }
            featuresOfInterest.put(identifier, referenceType);
        }
        return featuresOfInterest.get(identifier);
    }

    private ReferenceType getOfferingReference(DatasetEntity<?> entity, Map<String, ReferenceType> offerings){
        String identifier = entity.getOffering().getDomainId();
        if (!offerings.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (entity.getOffering().isSetName()) {
                referenceType.setTitle(entity.getOffering().getName());
            }
            offerings.put(identifier, referenceType);
        }
        return offerings.get(identifier);
    }

    /**
     * Check if optional count should be added
     *
     * @param request
     *            GetDataAvailability request
     * @return <code>true</code>, if optional count should be added
     */
    private boolean isShowCount(GetDataAvailabilityRequest request) {
        if (request.isSetExtensions()) {
            return request.getExtensions().isBooleanExtensionSet(SHOW_COUNT);
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
        if (request.isSetExtensions()) {
            return request.getExtensions().isBooleanExtensionSet(INCLUDE_RESULT_TIMES)
                    || hasPhenomenonTimeFilter(request.getExtensions());
        }
        return false;
    }

    /**
     * Check if extensions contains a temporal filter with valueReference
     * phenomenonTime
     *
     * @param extensions
     *            Extensions to check
     * @return <code>true</code>, if extensions contains a temporal filter with
     *         valueReference phenomenonTime
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

    private FormatDescriptor getFormatDescriptor(GDARequestContext context, DatasetEntity<?> entity) {
        return new FormatDescriptor(
                new ProcedureDescriptionFormatDescriptor(
                        entity.getProcedure().getProcedureDescriptionFormat()),
                getObservationFormatDescriptors(entity.getOffering(), context));
    }

    private Set<ObservationFormatDescriptor> getObservationFormatDescriptors(OfferingEntity entity, GDARequestContext context) {
        Map<String, Set<String>> responsFormatObservationTypesMap = Maps.newHashMap();
        for (String observationType : entity.getObservationTypes()) {
            Set<String> responseFormats = getResponseFormatsForObservationType(observationType, context.getRequest().getService(), context.getRequest().getVersion());
            for (String responseFormat : responseFormats) {
                if (responsFormatObservationTypesMap.containsKey(responseFormat)) {
                    responsFormatObservationTypesMap.get(responseFormat).add(observationType);
                } else {
                    responsFormatObservationTypesMap.put(responseFormat, Sets.newHashSet(observationType));
                }
            }
        }
        Set<ObservationFormatDescriptor> formatDescriptors = Sets.newHashSet();
        for (String responsFormat : responsFormatObservationTypesMap.keySet()) {
            formatDescriptors.add(new ObservationFormatDescriptor(responsFormat, responsFormatObservationTypesMap.get(responsFormat)));
        }
        return formatDescriptors;
    }

    private Set<String> getResponseFormatsForObservationType(String observationType, String service, String version) {
        Set<String> responseFormats = Sets.newHashSet();
        for (Encoder<?, ?> e : codingRepository.getEncoders()) {
            if (e instanceof ObservationEncoder) {
                final ObservationEncoder<?, ?> oe = (ObservationEncoder<?, ?>) e;
                Map<String, Set<String>> supportedResponseFormatObservationTypes = oe.getSupportedResponseFormatObservationTypes();
                if (supportedResponseFormatObservationTypes != null && !supportedResponseFormatObservationTypes.isEmpty()) {
                    for (final String responseFormat : supportedResponseFormatObservationTypes.keySet()) {
                        responseFormats.addAll(supportedResponseFormatObservationTypes.get(responseFormat));
                    }
                }
            }
        }
        return responseFormats;
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
            parentDataAvailabilities.add(childDataAvailability.clone());
        }
        return notContained;
    }

    public class GDARequestContext {
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
            return featuresOfInterest;
        }

        public Map<String, ReferenceType> getObservableProperties() {
            return observableProperties;
        }

        public Map<String, ReferenceType> getProcedures() {
            return procedures;
        }

        public Map<String, ReferenceType> getOfferings() {
            return offerings;
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

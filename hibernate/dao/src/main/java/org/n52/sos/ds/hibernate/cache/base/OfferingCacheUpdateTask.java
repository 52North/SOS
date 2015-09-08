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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.i18n.LocalizedString;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.i18n.metadata.I18NOfferingMetadata;
import org.n52.iceland.ogc.OGCConstants;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.Constants;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.cache.ProcedureFlag;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.ogc.sos.SosEnvelope;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class OfferingCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate {

    private final FeatureOfInterestDAO featureDAO = new FeatureOfInterestDAO();
    private final String offeringId;
    private final Collection<ObservationConstellationInfo> observationConstellationInfos;
    private final Offering offering;
    private boolean obsConstSupported;
    private final boolean hasSamplingGeometry;

    private final Locale defaultLanguage;
    private final I18NDAORepository i18NDAORepository;
    private final FeatureQueryHandler featureQueryHandler;

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded
     * by a session in a different thread
     *
     * @param offering
     *            Offering identifier
     * @param observationConstellationInfos
     *            Observation Constellation info collection, passed in from
     *            parent update if supported
     * @param hasSamplingGeometry
     *            Indicator to execute or not the extent query for the Spatial
     *            Filtering Profile
     */
    public OfferingCacheUpdateTask(Offering offering,
                                   Collection<ObservationConstellationInfo> observationConstellationInfos,
                                   boolean hasSamplingGeometry,
                                   Locale defaultLanguage,
                                   I18NDAORepository i18NDAORepository,
                                   FeatureQueryHandler featureQueryHandler) {
        this.offering = offering;
        this.offeringId = offering.getIdentifier();
        this.observationConstellationInfos = observationConstellationInfos;
        this.hasSamplingGeometry = hasSamplingGeometry;
        this.defaultLanguage = defaultLanguage;
        this.i18NDAORepository = i18NDAORepository;
        this.featureQueryHandler = featureQueryHandler;
    }

    protected void getOfferingInformationFromDbAndAddItToCacheMaps(Session session) throws OwsExceptionReport {
        // process all offering updates here (in multiple threads) which have
        // the potential to perform large
        // queries that aren't able to be loaded all at once. many (but not all)
        // of these can be avoided
        // if ObservationConstellation is supported

        // NOTE: Don't perform queries or load obecjts here unless you have to,
        // since they are performed once per offering


        getCache().addOffering(offeringId);
        addOfferingNamesAndDescriptionsToCache(offeringId, session);
        // only check once, check flag in other methods
        obsConstSupported = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        // Procedures
        final Map<ProcedureFlag, Set<String>> procedureIdentifiers = getProcedureIdentifier(session);
        getCache().setProceduresForOffering(offeringId, procedureIdentifiers.get(ProcedureFlag.PARENT));
        Set<String> hiddenChilds = procedureIdentifiers.get(ProcedureFlag.HIDDEN_CHILD);
        if (!hiddenChilds.isEmpty()) {
            getCache().setHiddenChildProceduresForOffering(offeringId, hiddenChilds);
        }

        // Observable properties
        getCache().setObservablePropertiesForOffering(offeringId, getObservablePropertyIdentifier(session));

        // Observation types
        getCache().setObservationTypesForOffering(offeringId, getObservationTypes(session));

        // Features of Interest
        List<String> featureOfInterestIdentifiers =
                featureDAO.getFeatureOfInterestIdentifiersForOffering(offeringId, session);
        getCache().setFeaturesOfInterestForOffering(offeringId,
                getValidFeaturesOfInterestFrom(featureOfInterestIdentifiers));
        getCache().setFeatureOfInterestTypesForOffering(offeringId,
                getFeatureOfInterestTypes(featureOfInterestIdentifiers, session));

        // Spatial Envelope
        getCache().setEnvelopeForOffering(offeringId,
                getEnvelopeForOffering(featureOfInterestIdentifiers, session));
        // Spatial Filtering Profile Spatial Envelope
        addSpatialFilteringProfileEnvelopeForOffering(offeringId, session);
    }

    protected void addOfferingNamesAndDescriptionsToCache(String offeringId, Session session)
            throws OwsExceptionReport {
        final MultilingualString name;
        final MultilingualString description;

        I18NDAO<I18NOfferingMetadata> dao = i18NDAORepository.getDAO(I18NOfferingMetadata.class);

        if (dao != null) {
            I18NOfferingMetadata metadata = dao.getMetadata(offeringId);
            name = metadata.getName();
            description = metadata.getDescription();
        } else {
            name = new MultilingualString();
            description = new MultilingualString();
            if (offering.isSetName()) {
                final Locale locale;
                if (offering.isSetCodespaceName()) {
                    locale = LocaleHelper.fromString(offering.getCodespaceName().getCodespace());
                } else {
                    locale = defaultLanguage;

                }
                name.addLocalization(locale, offering.getName());
            } else {
                String offeringName = offeringId;
                if (offeringName.startsWith("http")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf(Constants.SLASH_CHAR) + 1,
                                    offeringName.length());
                } else if (offeringName.startsWith("urn")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf(Constants.COLON_CHAR) + 1,
                                    offeringName.length());
                }
                if (offeringName.contains(Constants.NUMBER_SIGN_STRING)) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf(Constants.NUMBER_SIGN_CHAR) + 1,
                                    offeringName.length());
                }
                name.addLocalization(defaultLanguage, offeringName);
            }
            if (offering.isSetDescription()) {
                final Locale locale;
                if (offering.isSetCodespaceName()) {
                    locale = LocaleHelper.fromString(offering.getCodespaceName().getCodespace());
                } else {
                    locale = defaultLanguage;
                }
                description.addLocalization(locale, offering.getDescription());
            }
        }

        getCache().setI18nDescriptionForOffering(offeringId, description);
        getCache().setI18nNameForOffering(offeringId, name);
        addHumanReadableIdentifier(offeringId, offering, name);
    }

    private void addHumanReadableIdentifier(String offeringId, Offering offering, MultilingualString name) {
        if (offering.isSetName()) {
            getCache().addOfferingIdentifierHumanReadableName(offeringId, offering.getName());
        } else {
            if (!name.isEmpty()) {
                Optional<LocalizedString> defaultName = name.getLocalization(defaultLanguage);
                if (defaultName.isPresent()) {
                    getCache().addOfferingIdentifierHumanReadableName(offeringId, defaultName.get().getText());
                } else {
                    getCache().addOfferingIdentifierHumanReadableName(offeringId, offeringId);
                }
            }
        }

    }

    protected Map<ProcedureFlag, Set<String>> getProcedureIdentifier(Session session) throws OwsExceptionReport {
        Set<String> procedures = new HashSet<>(0);
        Set<String> hiddenChilds = new HashSet<>(0);
        if (obsConstSupported) {
            if (CollectionHelper.isNotEmpty(observationConstellationInfos)) {
                procedures.addAll(DatasourceCacheUpdateHelper
                        .getAllProcedureIdentifiersFromObservationConstellationInfos(observationConstellationInfos,
                                ProcedureFlag.PARENT));
                hiddenChilds.addAll(DatasourceCacheUpdateHelper
                        .getAllProcedureIdentifiersFromObservationConstellationInfos(observationConstellationInfos,
                                ProcedureFlag.HIDDEN_CHILD));
            }
        } else {
            List<String> list = new ProcedureDAO().getProcedureIdentifiersForOffering(offeringId, session);
            for (String procedureIdentifier : list) {
                procedures.add(procedureIdentifier);
            }
        }
        Map<ProcedureFlag, Set<String>> allProcedures = Maps.newEnumMap(ProcedureFlag.class);
        allProcedures.put(ProcedureFlag.PARENT, procedures);
        allProcedures.put(ProcedureFlag.HIDDEN_CHILD, hiddenChilds);
        return allProcedures;
    }

    protected Collection<String> getValidFeaturesOfInterestFrom(Collection<String> featureOfInterestIdentifiers) {
        Set<String> features = new HashSet<>(featureOfInterestIdentifiers.size());
        for (String featureIdentifier : featureOfInterestIdentifiers) {
            features.add(featureIdentifier);
        }
        return features;
    }

    protected Set<String> getObservablePropertyIdentifier(Session session) throws OwsExceptionReport {
        if (obsConstSupported) {
            if (CollectionHelper.isNotEmpty(observationConstellationInfos)) {
                return DatasourceCacheUpdateHelper
                        .getAllObservablePropertyIdentifiersFromObservationConstellationInfos(observationConstellationInfos);
            } else {
                return Sets.newHashSet();
            }
        } else {
            Set<String> observableProperties = Sets.newHashSet();
            List<String> list =
                    new ObservablePropertyDAO().getObservablePropertyIdentifiersForOffering(offeringId, session);
            for (String observablePropertyIdentifier : list) {
                observableProperties.add(observablePropertyIdentifier);
            }
            return observableProperties;
        }
    }

    protected Set<String> getObservationTypes(Session session) throws OwsExceptionReport {
        if (obsConstSupported) {
            if (CollectionHelper.isNotEmpty(observationConstellationInfos)) {
                Set<String> observationTypes = Sets.newHashSet();
                for (ObservationConstellationInfo oci : observationConstellationInfos) {
                    if (oci.getObservationType() != null) {
                        observationTypes.add(oci.getObservationType());
                    }
                }
                return observationTypes;
            } else {
                return Sets.newHashSet();
            }
        } else {
            return getObservationTypesFromObservations(session);
        }
    }

    private Set<String> getObservationTypesFromObservations(Session session) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        Set<String> observationTypes = Sets.newHashSet();
        if (observationDAO.checkNumericObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_MEASUREMENT);
        } else if (observationDAO.checkCategoryObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION);
        } else if (observationDAO.checkCountObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_COUNT_OBSERVATION);
        } else if (observationDAO.checkTextObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_TEXT_OBSERVATION);
        } else if (observationDAO.checkBooleanObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_TRUTH_OBSERVATION);
        } else if (observationDAO.checkBlobObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_OBSERVATION);
        } else if (observationDAO.checkGeometryObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION);
        } else if (observationDAO.checkComplexObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION);
        } else if (observationDAO.checkSweDataArrayObservationsFor(offeringId, session)) {
            observationTypes.add(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return observationTypes;
    }

    protected Set<String> getFeatureOfInterestTypes(List<String> featureOfInterestIdentifiers, Session session) {
        if (CollectionHelper.isNotEmpty(featureOfInterestIdentifiers)) {
            List<FeatureOfInterest> featureOfInterestObjects =
                    featureDAO.getFeatureOfInterestObject(featureOfInterestIdentifiers, session);
            if (CollectionHelper.isNotEmpty(featureOfInterestObjects)) {
                Set<String> featureTypes = Sets.newHashSet();
                for (FeatureOfInterest featureOfInterest : featureOfInterestObjects) {
                    if (!OGCConstants.UNKNOWN.equals(featureOfInterest.getFeatureOfInterestType()
                            .getFeatureOfInterestType())) {
                        featureTypes.add(featureOfInterest.getFeatureOfInterestType().getFeatureOfInterestType());
                    }
                }
                return featureTypes;
            }
        }
        return Sets.newHashSet();
    }

    protected SosEnvelope getEnvelopeForOffering(Collection<String> featureOfInterestIdentifiers, Session session)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(featureOfInterestIdentifiers)) {
            FeatureQueryHandlerQueryObject queryHandler =
                    new FeatureQueryHandlerQueryObject().setFeatureIdentifiers(featureOfInterestIdentifiers)
                            .setConnection(session);
            return this.featureQueryHandler.getEnvelopeForFeatureIDs(queryHandler);
        }
        return null;
    }

    /**
     * Get SpatialFilteringProfile envelope if exist and supported
     *
     * @param prefixedOfferingId
     *            Offering identifier used in requests and responses
     * @param offeringID
     *            Database Offering identifier to get envelope for
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected void addSpatialFilteringProfileEnvelopeForOffering(String offeringID,
            Session session) throws OwsExceptionReport {
        if (hasSamplingGeometry) {
            getCache().setSpatialFilteringProfileEnvelopeForOffering(
                    offeringId,
                    DaoFactory.getInstance().getObservationDAO()
                            .getSpatialFilteringProfileEnvelopeForOfferingId(offeringID, session));
        }
    }

    @Override
    public void execute() {
        try {
            getOfferingInformationFromDbAndAddItToCacheMaps(getSession());
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        }
    }
}

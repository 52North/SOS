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
package org.n52.sos.ds.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.locationtech.jts.geom.Envelope;
import org.n52.iceland.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.metadata.I18NOfferingMetadata;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.ds.ApiQueryHelper;
import org.n52.sos.ds.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.cache.ProcedureFlag;
import org.n52.sos.util.JTSConverter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class OfferingCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate implements ApiQueryHelper {

//    private final FeatureOfInterestDAO featureDAO = new FeatureOfInterestDAO();
    private final String identifier;
    private final Collection<DatasetEntity> datasets;
    private final OfferingEntity offering;
    private final Locale defaultLanguage;
    private final I18NDAORepository i18NDAORepository;

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded
     * by a session in a different thread
     *
     * @param offering
     *            Offering identifier
     * @param observationConstellationInfos
     *            Observation Constellation info collection, passed in from
     *            parent update if supported
     */
    public OfferingCacheUpdateTask(OfferingEntity offering,
                                   Collection<DatasetEntity> datasets,
                                   Locale defaultLanguage,
                                   I18NDAORepository i18NDAORepository) {
        this.offering = offering;
        this.identifier = offering.getIdentifier();
        this.datasets = datasets;
        this.defaultLanguage = defaultLanguage;
        this.i18NDAORepository = i18NDAORepository;
    }

    protected void getOfferingInformationFromDbAndAddItToCacheMaps(Session session) throws OwsExceptionReport {
        // process all offering updates here (in multiple threads) which have
        // the potential to perform large
        // queries that aren't able to be loaded all at once. many (but not all)
        // of these can be avoided
        // if ObservationConstellation is supported

        // NOTE: Don't perform queries or load obecjts here unless you have to,
        // since they are performed once per offering

        getCache().addOffering(identifier);
        if (datasets != null && !datasets.isEmpty() && datasets.stream().anyMatch(d -> d.isPublished())) {
            getCache().addPublishedOffering(identifier);
        }
        addOfferingNamesAndDescriptionsToCache(identifier, session);
        // only check once, check flag in other methods
        // Procedures
        final Map<ProcedureFlag, Set<String>> procedureIdentifiers = getProcedureIdentifier(session);
        getCache().setProceduresForOffering(identifier, procedureIdentifiers.get(ProcedureFlag.PARENT));
        Set<String> hiddenChilds = procedureIdentifiers.get(ProcedureFlag.HIDDEN_CHILD);
        if (!hiddenChilds.isEmpty()) {
            getCache().setHiddenChildProceduresForOffering(identifier, hiddenChilds);
        }

        // Observable properties
        getCache().setObservablePropertiesForOffering(identifier, getObservablePropertyIdentifier(session));

        // Observation types
        getCache().setObservationTypesForOffering(identifier, getObservationTypes());
        getCache().setAllowedObservationTypeForOffering(identifier, toStringSet(offering.getObservationTypes()));

        // Related features
        if (offering.hasRelatedFeatures()) {
            getCache().setRelatedFeaturesForOffering(identifier, getRelatedFeatures(offering.getRelatedFeatures()));
        }

        // Features of Interest
        getCache().setFeaturesOfInterestForOffering(identifier, DatasourceCacheUpdateHelper.getAllFeatureIdentifiersFromDatasets(datasets));
        getCache().setFeatureOfInterestTypesForOffering(identifier, getFeatureTypes());
        getCache().setAllowedFeatureOfInterestTypeForOffering(identifier, toStringSet(offering.getFeatureTypes()));

        // Spatial Envelope
        getCache().setEnvelopeForOffering(identifier, getEnvelopeForOffering(offering));

        // Temporal extent
        getCache().setMinPhenomenonTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getSamplingTimeStart()));
        getCache().setMaxPhenomenonTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getSamplingTimeEnd()));
        getCache().setMinResultTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getResultTimeStart()));
        getCache().setMaxResultTimeForOffering(identifier,DateTimeHelper.makeDateTime(offering.getResultTimeEnd()));
    }

    protected void addOfferingNamesAndDescriptionsToCache(String identifier, Session session)
            throws OwsExceptionReport {
        final MultilingualString name;
        final MultilingualString description;

        I18NDAO<I18NOfferingMetadata> dao = i18NDAORepository.getDAO(I18NOfferingMetadata.class);

        if (dao != null) {
            I18NOfferingMetadata metadata = dao.getMetadata(identifier);
            name = metadata.getName();
            description = metadata.getDescription();
        } else {
            name = new MultilingualString();
            description = new MultilingualString();
            if (offering.isSetName()) {
                final Locale locale = defaultLanguage;
                name.addLocalization(locale, offering.getName());
            } else {
                String offeringName = identifier;
                if (offeringName.startsWith("http")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf('/') + 1,
                                    offeringName.length());
                } else if (offeringName.startsWith("urn")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf(':') + 1,
                                    offeringName.length());
                }
                if (offeringName.contains("#")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf('#') + 1,
                                    offeringName.length());
                }
                name.addLocalization(defaultLanguage, offeringName);
            }
            if (offering.isSetDescription()) {
                final Locale locale  = defaultLanguage;
                description.addLocalization(locale, offering.getDescription());
            }
        }

        getCache().setI18nDescriptionForOffering(identifier, description);
        getCache().setI18nNameForOffering(identifier, name);
        addHumanReadableIdentifier(identifier, offering, name);
    }

    private void addHumanReadableIdentifier(String offeringId, OfferingEntity offering, MultilingualString name) {
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
        if (CollectionHelper.isNotEmpty(datasets)) {
            procedures.addAll(DatasourceCacheUpdateHelper
                    .getAllProcedureIdentifiersFromDatasets(datasets,
                            ProcedureFlag.PARENT));
            hiddenChilds.addAll(DatasourceCacheUpdateHelper
                    .getAllProcedureIdentifiersFromDatasets(datasets,
                            ProcedureFlag.HIDDEN_CHILD));
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
        if (CollectionHelper.isNotEmpty(datasets)) {
            return DatasourceCacheUpdateHelper
                    .getAllObservablePropertyIdentifiersFromDatasets(datasets);
        } else {
            return Sets.newHashSet();
        }
    }

    protected ReferencedEnvelope getEnvelopeForOffering(OfferingEntity offering) throws OwsExceptionReport {
        if (offering.isSetGeometry()) {
            return new ReferencedEnvelope(JTSConverter.convert(offering.getGeometry()).getEnvelopeInternal(), offering.getGeometry().getSRID());
        } else if (datasets != null && !datasets.isEmpty()) {
            Envelope e = new Envelope();
            int srid = -1;
            for (DatasetEntity de : datasets) {
                if (de.isSetFeature() && de.getFeature().isSetGeometry() && !de.getFeature().getGeometryEntity().isEmpty()) {
                    if (srid < 0 ) {
                        srid = de.getFeature().getGeometryEntity().getGeometry().getSRID();
                    }
                    e.expandToInclude(JTSConverter.convert(de.getFeature().getGeometryEntity().getGeometry()).getEnvelopeInternal());
                }
            }
            return new ReferencedEnvelope(e, srid);
        }
        return new ReferencedEnvelope();
    }

    protected Collection<String> getRelatedFeatures(Set<RelatedFeatureEntity> relatedFeatures) {
        return relatedFeatures.stream().map(rf -> rf.getFeature().getIdentifier()).collect(Collectors.toSet());
    }

    private Collection<String> getObservationTypes() {
        return datasets.stream().filter(d -> d.isSetObservationType()).map(d -> d.getObservationType().getFormat())
                .collect(Collectors.toSet());
    }

    private Collection<String> getFeatureTypes() {
        return datasets.stream().filter(d -> d.isSetFeature()).filter(d -> d.getFeature().isSetFeatureType())
                .map(d -> d.getFeature().getFeatureType().getFormat()).collect(Collectors.toSet());
    }

    @Override
    public void execute() {
        try {
            getOfferingInformationFromDbAndAddItToCacheMaps(getSession());
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        } catch (Exception e) {
            getErrors().add(new GenericThrowableWrapperException(e)
                    .withMessage(String.format("Error while processing offering cache update task for '%s'!"), identifier));
        }
    }
}

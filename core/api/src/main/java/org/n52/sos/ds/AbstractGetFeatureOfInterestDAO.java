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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.SosHelper;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractGetFeatureOfInterestDAO extends AbstractOperationDAO {
    public AbstractGetFeatureOfInterestDAO(final String service) {
        super(service, SosConstants.Operations.GetFeatureOfInterest.name());
    }

    @Override
    protected void setOperationsMetadata(final OwsOperation opsMeta, final String service, final String version)
            throws OwsExceptionReport {

        final Collection<String> featureIDs = SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version);

        addProcedureParameter(opsMeta);
        addFeatureOfInterestParameter(opsMeta, version);
        addObservablePropertyParameter(opsMeta);

        // TODO constraint srid
        String parameterName = Sos2Constants.GetFeatureOfInterestParams.spatialFilter.name();
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            parameterName = Sos1Constants.GetFeatureOfInterestParams.location.name();
        }

        SosEnvelope envelope = null;
        if (featureIDs != null && !featureIDs.isEmpty()) {
            envelope = getCache().getGlobalEnvelope();
        }

        if (envelope != null && envelope.isSetEnvelope()) {
            opsMeta.addRangeParameterValue(parameterName, SosHelper.getMinMaxFromEnvelope(envelope.getEnvelope()));
        } else {
            opsMeta.addAnyParameterValue(parameterName);
        }
    }

    public abstract GetFeatureOfInterestResponse getFeatureOfInterest(GetFeatureOfInterestRequest request)
            throws OwsExceptionReport;

    protected boolean isRelatedFeature(final String featureIdentifier) {
        return getCache().getRelatedFeatures().contains(featureIdentifier);
    }

    protected Set<String> getFeatureIdentifiers(final List<String> featureIdentifiers) {
        final Set<String> allFeatureIdentifiers = new HashSet<String>();
        for (final String featureIdentifier : featureIdentifiers) {
            if (isRelatedFeature(featureIdentifier)) {
                allFeatureIdentifiers.addAll(getCache().getChildFeatures(featureIdentifier, true, true));
            } else {
                allFeatureIdentifiers.add(featureIdentifier);
            }
        }
        return allFeatureIdentifiers;
    }

    /*
     * Now, we return the list of the child features and not the relatedFeature
     * itself // TODO add javadoc // FIXME where to add check for
     * samplingFeature types? protected FeatureCollection
     * processRelatedFeatures( final List<String> requestedFeatures, final
     * FeatureCollection featuresToProcess, final String
     * relatedSamplingFeatureRole) { // TODO Eike: relatedFeatures: compare
     * feature collection with requested features if
     * (isNotEmpty(requestedFeatures) && featuresToProcess != null &&
     * featuresToProcess.isSetMembers()) { final Map<String,Collection<String>>
     * relatedFeatureIdentifiersWithChilds =
     * getRelatedFeatureIdentifiersWithChilds(requestedFeatures);
     * 
     * final FeatureCollection requestedFeatureObjects =
     * removeNotRequestedFeatures(requestedFeatures, featuresToProcess);
     * 
     * for (final String featureIdentifier : requestedFeatures) { if
     * (isRelatedFeature(featureIdentifier)) { final Collection<String>
     * childIdentifier =
     * relatedFeatureIdentifiersWithChilds.get(featureIdentifier); final
     * Collection<AbstractFeature> childFeatures =
     * getChildFeatures(childIdentifier,featuresToProcess); final
     * SamplingFeature relatedFeature; if
     * (featuresToProcess.getMembers().keySet().contains(featureIdentifier)) {
     * relatedFeature = (SamplingFeature)
     * featuresToProcess.getMembers().get(featureIdentifier); } else {
     * relatedFeature = new SamplingFeature(new
     * CodeWithAuthority(featureIdentifier)); }
     * addRelatedChilds(relatedFeature,childFeatures
     * ,relatedSamplingFeatureRole);
     * requestedFeatureObjects.addMember(relatedFeature); } } return
     * requestedFeatureObjects; } else { return featuresToProcess; } }
     * 
     * private Map<String, Collection<String>>
     * getRelatedFeatureIdentifiersWithChilds(final List<String>
     * requestedFeatures) { final Map<String, Collection<String>>
     * featureIdsWithChilds = CollectionHelper.map(); for (final String
     * featureIdentifier : requestedFeatures) { if
     * (isRelatedFeature(featureIdentifier) &&
     * getCache().isRelatedFeatureSampled(featureIdentifier)) {
     * featureIdsWithChilds.put(featureIdentifier,
     * getCache().getChildFeatures(featureIdentifier, true, false)); } } return
     * featureIdsWithChilds; }
     * 
     * private SamplingFeature addRelatedChilds( final SamplingFeature
     * relatedFeature, final Collection<AbstractFeature> childFeatures, final
     * String relatedSamplingFeatureRole) { for (final AbstractFeature
     * childFeature : childFeatures) {
     * relatedFeature.addRelatedSamplingFeature(new
     * SamplingFeatureComplex(relatedSamplingFeatureRole,(SamplingFeature)
     * childFeature)); } return relatedFeature; }
     * 
     * private Collection<AbstractFeature> getChildFeatures(final
     * Collection<String> childIdentifiers, final FeatureCollection
     * featuresToProcess) { final Collection<AbstractFeature> childFeatures =
     * CollectionHelper.set(); for (final AbstractFeature abstractFeature :
     * featuresToProcess.getMembers().values()) { if
     * (childIdentifiers.contains(abstractFeature.getIdentifier().getValue())) {
     * childFeatures.add(abstractFeature); } } return childFeatures; }
     * 
     * private FeatureCollection removeNotRequestedFeatures(final List<String>
     * requestedFeatures, final FeatureCollection featuresToProcess) { final
     * FeatureCollection processedFeatures = new FeatureCollection(); for (final
     * AbstractFeature abstractFeature :
     * featuresToProcess.getMembers().values()) { final String identifier =
     * abstractFeature.getIdentifier().getValue(); if
     * (requestedFeatures.contains(identifier) && !isRelatedFeature(identifier))
     * { processedFeatures.addMember(abstractFeature); } } return
     * processedFeatures; }
     */
}

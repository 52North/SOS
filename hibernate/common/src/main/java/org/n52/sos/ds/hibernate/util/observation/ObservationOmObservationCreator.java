/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.CountObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.TextObservation;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class ObservationOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationOmObservationCreator.class);

    private final Collection<AbstractObservation> observations;

    private final String resultModel;

    private final Map<String, AbstractFeature> features = Maps.newHashMap();

    private final Map<String, AbstractPhenomenon> observedProperties = Maps.newHashMap();

    private final Map<String, SosProcedureDescription> procedures = Maps.newHashMap();

    private final Set<OmObservationConstellation> observationConstellations = Sets.newHashSet();

    private final HibernateProcedureConverter procedureConverter;

    private final FeatureQueryHandler featureQueryHandler;

    private final boolean encodeProcedureInObservation;

    private SpatialFilteringProfileAdder spatialFilteringProfileAdder;

    private List<OmObservation> observationCollection;

    /**
     * Constructor
     * 
     * @param observations
     *            Collection of observation objects
     * @param spatialFilteringProfile
     *            Map with spatial filtering profile entities, key observation
     *            entity id
     * @param version
     *            Service version
     * @param resultModel
     *            Requested result model
     * @param session
     *            Hibernate session
     */
    public ObservationOmObservationCreator(Collection<AbstractObservation> observations,
            Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfile, String version, String resultModel,
            Session session) {
        super(version, session);
        this.resultModel = resultModel;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
        spatialFilteringProfileAdder = new SpatialFilteringProfileAdder(spatialFilteringProfile);
        this.procedureConverter = new HibernateProcedureConverter();
        this.featureQueryHandler = getFeatureQueryHandler();
        this.encodeProcedureInObservation = getActiveProfile().isEncodeProcedureInObservation();
    }

    public ObservationOmObservationCreator(Collection<AbstractObservation> observations, String version, String resultModel,
            Session session) {
        super(version, session);
        this.resultModel = resultModel;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
        this.procedureConverter = new HibernateProcedureConverter();
        this.featureQueryHandler = getFeatureQueryHandler();
        this.encodeProcedureInObservation = getActiveProfile().isEncodeProcedureInObservation();
    }

    private Collection<AbstractObservation> getObservations() {
        return observations;
    }

    private SpatialFilteringProfileAdder getSpatialFilteringProfileAdder() {
        return spatialFilteringProfileAdder;
    }
    
    private boolean isSetSpatialFilteringProfileAdder() {
        return getSpatialFilteringProfileAdder() != null;
    }

    private String getResultModel() {
        return resultModel;
    }

    private SosProcedureDescription getProcedure(String procedureId) {
        return procedures.get(procedureId);
    }

    private AbstractPhenomenon getObservedProperty(String phenomenonId) {
        return observedProperties.get(phenomenonId);
    }

    private AbstractFeature getFeature(String featureId) {
        return features.get(featureId);
    }

    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        if (getObservations() == null) {
            return Collections.emptyList();
        } else if (this.observationCollection == null) {
            this.observationCollection = Lists.newLinkedList();
            // now iterate over resultset and create Measurement for each row
            for (AbstractObservation hObservation : getObservations()) {
                // check remaining heap size and throw exception if minimum is
                // reached
                SosHelper.checkFreeMemory();

                String procedureId = createProcedure(hObservation);
                String featureId = createFeatureOfInterest(hObservation);
                String phenomenonId = createPhenomenon(hObservation);
                // TODO: add offering ids to response if needed later.
                // String offeringID =
                // hoc.getOffering().getIdentifier();
                // String mimeType = SosConstants.PARAMETER_NOT_SET;

                createValue(hObservation, phenomenonId, procedureId, featureId);
            }
        }
        return this.observationCollection;
    }

    private void checkOrSetObservablePropertyUnit(AbstractPhenomenon phen, String unit) {
        if (phen instanceof OmObservableProperty) {
            final OmObservableProperty obsProp = (OmObservableProperty) phen;
            if (obsProp.getUnit() == null && unit != null) {
                obsProp.setUnit(unit);
            }
        }
    }

    /**
     * Get observation value from all value tables for an Observation object
     * 
     * @param hObservation
     *            Observation object
     * 
     * @return Observation value
     * 
     * @throws OwsExceptionReport
     * @throws CodedException
     */
    private Value<?> getValueFromObservation(final AbstractObservation hObservation) throws CodedException,
            OwsExceptionReport {
        if (hObservation instanceof NumericObservation) {
            return new QuantityValue(((NumericObservation) hObservation).getValue());
        } else if (hObservation instanceof BooleanObservation) {
            return new org.n52.sos.ogc.om.values.BooleanValue(Boolean.valueOf(((BooleanObservation) hObservation)
                    .getValue()));
        } else if (hObservation instanceof CategoryObservation) {
            return new org.n52.sos.ogc.om.values.CategoryValue(((CategoryObservation) hObservation).getValue());
        } else if (hObservation instanceof CountObservation) {
            return new org.n52.sos.ogc.om.values.CountValue(Integer.valueOf(((CountObservation) hObservation)
                    .getValue()));
        } else if (hObservation instanceof TextObservation) {
            return new org.n52.sos.ogc.om.values.TextValue(((TextObservation) hObservation).getValue().toString());
        } else if (hObservation instanceof GeometryObservation) {
            return new org.n52.sos.ogc.om.values.GeometryValue(((GeometryObservation) hObservation).getValue());
        } else if (hObservation instanceof BlobObservation) {
            return new UnknownValue(((BlobObservation) hObservation).getValue());
        } else if (hObservation instanceof SweDataArrayObservation) {
            SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
            final XmlObject xml = XmlHelper.parseXmlString(((SweDataArrayObservation) hObservation).getValue());
            sweDataArrayValue.setValue((SweDataArray) CodingHelper.decodeXmlElement(xml));
            return sweDataArrayValue;
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createNewObservation(OmObservationConstellation oc, AbstractObservation ho, Value<?> value) {
        final OmObservation o = new OmObservation();
        o.setObservationID(Long.toString(ho.getObservationId()));
        if (ho.isSetIdentifier() && !ho.getIdentifier().startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            final CodeWithAuthority identifier = new CodeWithAuthority(ho.getIdentifier());
            if (ho.isSetCodespace()) {
                identifier.setCodeSpace(ho.getCodespace().getCodespace());
            }
            o.setIdentifier(identifier);
        }
        if (ho.isSetDescription()) {
            o.setDescription(ho.getDescription());
        }
        o.setNoDataValue(getActiveProfile().getResponseNoDataPlaceholder());
        o.setTokenSeparator(getTokenSeparator());
        o.setTupleSeparator(getTupleSeparator());
        o.setObservationConstellation(oc);
        o.setResultTime(new TimeInstant(new DateTime(ho.getResultTime(), DateTimeZone.UTC)));
        o.setValue(new SingleObservationValue(getPhenomenonTime(ho), value));
        return o;
    }

    private Time getPhenomenonTime(final AbstractObservation hObservation) {
        // create time element
        final DateTime phenStartTime = new DateTime(hObservation.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (hObservation.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(hObservation.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        Time phenomenonTime;
        if (phenStartTime.equals(phenEndTime)) {
            phenomenonTime = new TimeInstant(phenStartTime);
        } else {
            phenomenonTime = new TimePeriod(phenStartTime, phenEndTime);
        }
        return phenomenonTime;
    }

    private String createPhenomenon(final AbstractObservation hObservation) {
        LOGGER.trace("Creating Phenomenon...");
        final String phenID = hObservation.getObservableProperty().getIdentifier();
        if (!observedProperties.containsKey(phenID)) {
            final String description = hObservation.getObservableProperty().getDescription();
            observedProperties.put(phenID, new OmObservableProperty(phenID, description, null, null));
        }
        LOGGER.trace("Creating Phenomenon done.");
        return phenID;
    }

    private String createProcedure(final AbstractObservation hObservation) throws OwsExceptionReport,
            ConverterException {
        // TODO sfp full description
        LOGGER.trace("Creating Procedure...");
        final String procedureId = hObservation.getProcedure().getIdentifier();
        if (!procedures.containsKey(procedureId)) {
            final Procedure hProcedure = hObservation.getProcedure();
            final String pdf = hProcedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
            final SosProcedureDescription procedure;
            if (encodeProcedureInObservation) {
                procedure =
                        procedureConverter.createSosProcedureDescription(hProcedure, pdf, getVersion(), getSession());
            } else {
                procedure = new SosProcedureDescriptionUnknowType(procedureId, pdf, null);
            }
            procedures.put(procedureId, procedure);
        }
        LOGGER.trace("Creating Procedure done.");
        return procedureId;
    }

    private String createFeatureOfInterest(final AbstractObservation hObservation) throws OwsExceptionReport {
        LOGGER.trace("Creating Feature...");
        final String foiID = hObservation.getFeatureOfInterest().getIdentifier();
        if (!features.containsKey(foiID)) {
            final AbstractFeature featureByID =
                    featureQueryHandler.getFeatureByID(foiID, getSession(), getVersion(), -1);
            features.put(foiID, featureByID);
        }
        LOGGER.trace("Creating Feature done.");
        return foiID;
    }

    private void createValue(AbstractObservation hObservation, String phenomenonId, String procedureId,
            String featureId) throws OwsExceptionReport {
        LOGGER.trace("Creating Value...");
        final Value<?> value = getValueFromObservation(hObservation);
        if (value != null) {
            if (hObservation.getUnit() != null) {
                value.setUnit(hObservation.getUnit().getUnit());
            }
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst =
                    createObservationConstellation(hObservation, procedureId, phenomenonId, featureId);
            final OmObservation sosObservation = createNewObservation(obsConst, hObservation, value);
            // add SpatialFilteringProfile
            if (hObservation.hasSamplingGeometry()) {
                sosObservation.addParameter(createSpatialFilteringProfileParameter(hObservation.getSamplingGeometry()));
            } else if (isSetSpatialFilteringProfileAdder()) {
                getSpatialFilteringProfileAdder().add(hObservation.getObservationId(), sosObservation);
            } else {
            	if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()) {
            		AbstractFeature feature = getFeature(featureId);
            		if (feature != null && feature instanceof SamplingFeature && ((SamplingFeature)feature).isSetGeometry())
            		sosObservation.addParameter(createSpatialFilteringProfileParameter(((SamplingFeature)feature).getGeometry()));
                }
            }
            observationCollection.add(sosObservation);
            getSession().evict(hObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
        }
        LOGGER.trace("Creating Value done.");
    }

    private OmObservationConstellation createObservationConstellation(AbstractObservation hObservation,
            String procedureId, String phenomenonId, String featureId) {
        OmObservationConstellation obsConst =
                new OmObservationConstellation(getProcedure(procedureId), getObservedProperty(phenomenonId),
                        getFeature(featureId));

        /* sfp the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            final Set<String> offerings =
                    Sets.newHashSet(getCache().getOfferingsForObservableProperty(
                            obsConst.getObservableProperty().getIdentifier()));
            offerings.retainAll(getCache().getOfferingsForProcedure(obsConst.getProcedure().getIdentifier()));
            obsConst.setOfferings(offerings);
        }
        if (!observationConstellations.contains(obsConst)) {
            if (StringHelper.isNotEmpty(getResultModel())) {
                obsConst.setObservationType(getResultModel());
            }
            final ObservationConstellationDAO dao = new ObservationConstellationDAO();
            final ObservationConstellation hoc =
                    dao.getFirstObservationConstellationForOfferings(hObservation.getProcedure(),
                            hObservation.getObservableProperty(), hObservation.getOfferings(), getSession());
            if (hoc != null && hoc.getObservationType() != null) {
                obsConst.setObservationType(hoc.getObservationType().getObservationType());
            }
            observationConstellations.add(obsConst);
        }
        return obsConst;
    }

}

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
package org.n52.sos.ds.hibernate.dao.observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.CategoryDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationTypeDAO;
import org.n52.sos.ds.hibernate.dao.ParameterDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationContext;
import org.n52.sos.ds.hibernate.entities.Category;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ReferenceObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.om.values.ProfileLevelVisitor;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.GeometryHandler;

import com.vividsolutions.jts.geom.Geometry;

public class ObservationPersister implements ValueVisitor<Observation<?>>, ProfileLevelVisitor<Observation<?>> {

    private static final ObservationVisitor<String> SERIES_TYPE_VISITOR = new SeriesTypeVisitor();
    private final ObservationConstellation observationConstellation;
    private final AbstractFeatureOfInterest featureOfInterest;
    private final ObservationPersister.Caches caches;
    private final Session session;
    private final Geometry samplingGeometry;
    private final ObservationPersister.DAOs daos;
    private final ObservationFactory observationFactory;
    private final OmObservation sosObservation;
    private final boolean childObservation;
    private final Set<Offering> offerings;
    private boolean checkForDuplicity;

    public ObservationPersister(
            AbstractObservationDAO observationDao,
            OmObservation sosObservation,
            ObservationConstellation hObservationConstellation,
            AbstractFeatureOfInterest hFeature,
            Map<String, Codespace> codespaceCache,
            Map<UoM, Unit> unitCache,
            Set<Offering> hOfferings,
            boolean checkForDuplicity,
            Session session)
            throws OwsExceptionReport {
        this(new DAOs(observationDao),
             new Caches(codespaceCache, unitCache),
             sosObservation,
             hObservationConstellation,
             hFeature,
             getSamplingGeometry(sosObservation),
             hOfferings,
             checkForDuplicity,
             session,
             false);
    }

    private ObservationPersister(
            ObservationPersister.DAOs daos,
            ObservationPersister.Caches caches,
            OmObservation observation,
            ObservationConstellation hObservationConstellation,
            AbstractFeatureOfInterest hFeature,
            Geometry samplingGeometry,
            Set<Offering> hOfferings,
            boolean checkForDuplicity,
            Session session,
            boolean childObservation)
            throws OwsExceptionReport {
        observationConstellation = hObservationConstellation;
        featureOfInterest = hFeature;
        this.caches = caches;
        sosObservation = observation;
        this.samplingGeometry = samplingGeometry;
        this.session = session;
        this.daos = daos;
        observationFactory = daos.observation().getObservationFactory();
        this.childObservation = childObservation;
        offerings = hOfferings;
        this.checkForDuplicity = checkForDuplicity;
        checkForDuplicity();
    }

    private void checkForDuplicity() throws OwsExceptionReport {
        /*
         *  TODO check if observation exists in database for
         *  - series, phenTimeStart, phenTimeEnd, resultTime
         *  - series, phenTimeStart, phenTimeEnd, resultTime, depth/height parameter (same observation different depth/height)
         */
        if (checkForDuplicity) {
            daos.observation.checkForDuplicatedObservations(sosObservation, observationConstellation, session);
        }
    }

    @Override
    public Observation<?> visit(BooleanValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.truth(), value);
    }

    @Override
    public Observation<?> visit(CategoryValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.category(), value);
    }

    @Override
    public Observation<?> visit(CountValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.count(), value);
    }

    @Override
    public Observation<?> visit(GeometryValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.geometry(), value);
    }

    @Override
    public Observation<?> visit(QuantityValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.numeric(), value);
    }


    @Override
    public Observation<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(TextValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.text(), value);
    }

    @Override
    public Observation<?> visit(UnknownValue value)
            throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.blob(), value);
    }

    @Override
    public Observation<?> visit(SweDataArrayValue value)
            throws OwsExceptionReport {
        return persist(observationFactory.sweDataArray(), value.getValue().getXml());
    }

    @Override
    public Observation<?> visit(ComplexValue value)
            throws OwsExceptionReport {
        ComplexObservation complex = observationFactory.complex();
        complex.setParent(true);
        return persist(complex, persistChildren(value.getValue()));
    }

    @Override
    public Observation<?> visit(HrefAttributeValue value)
            throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(NilTemplateValue value)
            throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(ReferenceValue value)
            throws OwsExceptionReport {
        return persist(observationFactory.reference(), value.getValue());
    }

    @Override
    public Observation<?> visit(TVPValue value)
            throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(TLVTValue value)
            throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public Observation<?> visit(ProfileValue value) throws OwsExceptionReport {
        ProfileObservation profile = observationFactory.profile();
        profile.setParent(true);
        sosObservation.getValue().setPhenomenonTime(value.getPhenomenonTime());
        return persist(profile, persistChildren(value.getValue()));
    }

    @Override
    public Collection<Observation<?>> visit(ProfileLevel value) throws OwsExceptionReport {
        List<Observation<?>> childObservations = new ArrayList<>();
        if (value.isSetValue()) {
            for (Value<?> v : value.getValue()) {
                childObservations.add(v.accept(this));
            }
        }
        return childObservations;
    }

    @Override
    public Observation<?> visit(XmlValue value)
            throws OwsExceptionReport {
        throw notSupported(value);
    }



    private Set<Observation<?>> persistChildren(SweAbstractDataRecord dataRecord)
            throws HibernateException, OwsExceptionReport {
        Set<Observation<?>> children = new TreeSet<>();
        for (SweField field : dataRecord.getFields()) {
            ObservableProperty observableProperty = getObservablePropertyForField(field);
            ObservationPersister childPersister = createChildPersister(observableProperty);
            children.add(field.accept(ValueCreatingSweDataComponentVisitor.getInstance()).accept(childPersister));
        }
        session.flush();
        return children;
    }

    private Set<Observation<?>> persistChildren(List<ProfileLevel> values) throws OwsExceptionReport {
        Set<Observation<?>> children = new TreeSet<>();
        for (ProfileLevel level : values) {
            if (level.isSetValue()) {
                for (Value<?> v : level.getValue()) {
                    if (v instanceof SweAbstractDataComponent && ((SweAbstractDataComponent) v).isSetDefinition()) {
                        children.add(v.accept(createChildPersister(level, ((SweAbstractDataComponent) v).getDefinition())));
                    } else {
                        children.add(v.accept(createChildPersister(level)));
                    }
                }
            }
        }
        session.flush();
        return children;
    }

    private OmObservation getObservationWithLevelParameter(ProfileLevel level) {
        OmObservation o = new OmObservation();
        sosObservation.copyTo(o);
        o.setParameter(level.getLevelStartEndAsParameter());
        if (level.isSetPhenomenonTime()) {
            o.setValue(new SingleObservationValue<>());
            o.getValue().setPhenomenonTime(level.getPhenomenonTime());
        }
        return o;
    }

    private ObservationPersister createChildPersister(ProfileLevel level, String observableProperty) throws OwsExceptionReport {
        return new ObservationPersister(daos, caches, getObservationWithLevelParameter(level),
                getObservationConstellation(getObservableProperty(observableProperty)), featureOfInterest,
                getSamplingGeometryFromLevel(level), offerings, checkForDuplicity, session, true);
    }

    private ObservationPersister createChildPersister(ProfileLevel level) throws OwsExceptionReport {
        return new ObservationPersister(daos, caches, getObservationWithLevelParameter(level),
                observationConstellation, featureOfInterest,
                getSamplingGeometryFromLevel(level), offerings, checkForDuplicity, session, true);

    }

    private ObservationPersister createChildPersister(ObservableProperty observableProperty) throws OwsExceptionReport {
        return new ObservationPersister(daos, caches, sosObservation,
                getObservationConstellation(observableProperty), featureOfInterest,
                samplingGeometry, offerings, checkForDuplicity, session, true);
    }

    private ObservationConstellation getObservationConstellation(ObservableProperty observableProperty) {
        return daos.observationConstellation()
                .checkOrInsertObservationConstellation(
                        observationConstellation.getProcedure(),
                        observableProperty,
                        observationConstellation.getOffering(),
                        true,
                        session);

    }

     private OwsExceptionReport notSupported(Value<?> value)
            throws OwsExceptionReport {
        throw new NoApplicableCodeException()
                .withMessage("Unsupported observation value %s", value
                             .getClass().getCanonicalName());
    }

    private ObservableProperty getObservablePropertyForField(SweField field) {
        String definition = field.getElement().getDefinition();
        return getObservableProperty(definition);
    }

    private ObservableProperty getObservableProperty(String observableProperty) {
        return daos.observableProperty().getObservablePropertyForIdentifier(observableProperty, session);
    }

    private <V, T extends Observation<V>> T setUnitAndPersist(T observation, Value<V> value) throws OwsExceptionReport {
        observation.setUnit(getUnit(value));
        return persist(observation, value.getValue());
    }

    private Unit getUnit(Value<?> value) {
        return value.isSetUnit() ? daos.observation().getUnit(value.getUnitObject(), caches.units(), session) : null;
    }

    private <V, T extends Observation<V>> T persist(T observation, V value) throws OwsExceptionReport {
        if (!observation.isSetUnit()) {
            observation.setUnit(getUnit(sosObservation.getValue().getValue()));
        }
        observation.setDeleted(false);

        if (!childObservation) {
            daos.observation().addIdentifier(sosObservation, observation, session);
        } else {
            observation.setChild(true);
        }
        
        daos.observation().addName(sosObservation, observation, session);
        daos.observation().addDescription(sosObservation, observation);
        daos.observation().addTime(sosObservation, observation);
        observation.setValue(value);
        observation.setSamplingGeometry(samplingGeometry);
        checkUpdateFeatureOfInterestGeometry();

        ObservationContext observationContext = daos.observation().createObservationContext();

        String observationType = observation.accept(ObservationTypeObservationVisitor.getInstance());
        if (!isProfileObservation() || isProfileObservation() && !childObservation) {
            if (!daos.observationConstellation().checkObservationType(observationConstellation, observationType, session)) {
                throw new InvalidParameterValueException()
                .withMessage("The requested observationType (%s) is invalid for procedure = %s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                                observationType,
                                observationConstellation.getProcedure().getIdentifier(),
                                observationConstellation.getObservableProperty().getIdentifier(),
                                observationConstellation.getOffering().getIdentifier(),
                                observationConstellation.getObservationType().getObservationType());
            }
            if (sosObservation.isSetSeriesType()) {
                observationContext.setSeriesType(sosObservation.getSeriesType());
            } else {
                observationContext.setSeriesType(observation.accept(SERIES_TYPE_VISITOR));
            }
        }
        
        // category
        if (HibernateHelper.isColumnSupported(Series.class, Series.CATEGORY) && observationContext instanceof SeriesObservationContext) {
            if (caches.category() == null) {
                if (sosObservation.isSetCategoryParameter()) {
                    NamedValue<String> categoryParameter = (NamedValue<String>) sosObservation.getCategoryParameter();
                    caches.setCategory(daos.category().getOrInsertCategory(categoryParameter, session));
                    sosObservation.removeCategoryParameter();
                } else {
                    caches.setCategory(daos.category().getOrInsertCategory(observationConstellation.getObservableProperty(), session));
                }
            }
            ((SeriesObservationContext) observationContext).setCategory(caches.category());
        }

        if (observationConstellation != null) {
            observationContext.setObservableProperty(observationConstellation.getObservableProperty());
            observationContext.setProcedure(observationConstellation.getProcedure());
            observationContext.setOffering(observationConstellation.getOffering());
        }
        // currently only profiles with one observedProperty are supported
        if (childObservation && !isProfileObservation()) {
            observationContext.setHiddenChild(true);
        }
        observationContext.setFeatureOfInterest(featureOfInterest);
        observation.setOfferings(offerings);
        daos.observation().fillObservationContext(observationContext, sosObservation, session);
        daos.observation().addObservationContextToObservation(observationContext, observation, session);

        session.saveOrUpdate(observation);

        if (sosObservation.isSetParameter()) {
            daos.parameter.insertParameter(sosObservation.getParameter(), observation.getObservationId(), caches.units, session);
        }
        return observation;
    }

    private boolean isProfileObservation() {
        return observationConstellation.isSetObservationType()
                && (OmConstants.OBS_TYPE_PROFILE_OBSERVATION.equals(observationConstellation.getObservationType().getObservationType())
                || GWMLConstants.OBS_TYPE_GEOLOGY_LOG.equals(observationConstellation.getObservationType().getObservationType())
                || GWMLConstants.OBS_TYPE_GEOLOGY_LOG_COVERAGE.equals(observationConstellation.getObservationType().getObservationType()));
    }

    private Geometry getSamplingGeometryFromLevel(ProfileLevel level) throws OwsExceptionReport {
        if (level.isSetLocation()) {
            return GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(level.getLocation());
        }
        return null;
    }

    private static Geometry getSamplingGeometry(OmObservation sosObservation) throws OwsExceptionReport {
        if (!sosObservation.isSetSpatialFilteringProfileParameter()) {
            return null;
        }
        if (sosObservation.isSetValue() && sosObservation.getValue().isSetValue() && sosObservation.getValue().getValue() instanceof ProfileValue
                && ((ProfileValue)sosObservation.getValue().getValue()).isSetGeometry()) {
            return GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(((ProfileValue)sosObservation.getValue().getValue()).getGeometry());
        }
        NamedValue<Geometry> spatialFilteringProfileParameter = sosObservation.getSpatialFilteringProfileParameter();
        Geometry geometry = spatialFilteringProfileParameter.getValue().getValue();
        return GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(geometry);
    }

    private void checkUpdateFeatureOfInterestGeometry() {
        // check if flag is set and if this observation is not a child observation
        if (samplingGeometry != null && ServiceConfiguration.getInstance().isUpdateFeatureGeometry() && !childObservation) {
            new FeatureOfInterestDAO().updateFeatureOfInterestGeometry(featureOfInterest, samplingGeometry, session);
        }
    }

    private static class Caches {
        private final Map<String, Codespace> codespaces;
        private final Map<UoM, Unit> units;
        private Category category;
        
        Caches(Map<String, Codespace> codespaces, Map<UoM, Unit> units) {
            this(codespaces, units, null);
        }

        Caches(Map<String, Codespace> codespaces, Map<UoM, Unit> units, Category category) {
            this.codespaces = codespaces;
            this.units = units;
            this.category = category;
        }

        public Map<String, Codespace> codespaces() {
            return codespaces;
        }

        public Map<UoM, Unit> units() {
            return units;
        }
        
        public Category category() {
            return category;
        }
        
        public void setCategory(Category category) {
          this.category = category;
        }
    }

    

    private static class DAOs {
        private final ObservablePropertyDAO observableProperty;
        private final ObservationConstellationDAO observationConstellation;
        private final AbstractObservationDAO observation;
        private final ObservationTypeDAO observationType;
        private final CategoryDAO category;
        private final ParameterDAO parameter;

        DAOs(AbstractObservationDAO observationDAO) {
            observation = observationDAO;
            observableProperty = new ObservablePropertyDAO();
            observationConstellation = new ObservationConstellationDAO();
            observationType = new ObservationTypeDAO();
            category = new CategoryDAO();
            parameter = new ParameterDAO();
        }

        public ObservablePropertyDAO observableProperty() {
            return observableProperty;
        }

        public ObservationConstellationDAO observationConstellation() {
            return observationConstellation;
        }

        public AbstractObservationDAO observation() {
            return observation;
        }

        public ObservationTypeDAO observationType() {
            return observationType;
        }
        
        public CategoryDAO category() {
            return category;
        }

        public ParameterDAO parameter() {
            return parameter;
        }
    }

    private static class SeriesTypeVisitor implements ObservationVisitor<String> {

        @Override
        public String visit(NumericObservation o) throws OwsExceptionReport {
            return "quantity";
        }

        @Override
        public String visit(BlobObservation o) throws OwsExceptionReport {
            return "blob";
        }

        @Override
        public String visit(BooleanObservation o) throws OwsExceptionReport {
            return "boolean";
        }

        @Override
        public String visit(CategoryObservation o) throws OwsExceptionReport {
            return "category";
        }

        @Override
        public String visit(ComplexObservation o) throws OwsExceptionReport {
            return "complex";
        }

        @Override
        public String visit(CountObservation o) throws OwsExceptionReport {
            return "count";
        }

        @Override
        public String visit(GeometryObservation o) throws OwsExceptionReport {
            return "geometry";
        }

        @Override
        public String visit(TextObservation o) throws OwsExceptionReport {
            return "text";
        }

        @Override
        public String visit(SweDataArrayObservation o) throws OwsExceptionReport {
            return "swedataarray";
        }

        @Override
        public String visit(ProfileObservation o) throws OwsExceptionReport {
            if (o.isSetValue()) {
                for (Observation<?> value : o.getValue()) {
                    return value.accept(this) + "-profile";
                }
            }
            return "profile";
        }

        @Override
        public String visit(ReferenceObservation o)
                throws OwsExceptionReport {
            return "reference";
        }
    }
}
/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gwml.GWMLConstants;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.CvDiscretePointCoverage;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.MultiPointCoverage;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityRangeValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.RectifiedGridCoverage;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TLVTValue;
import org.n52.shetland.ogc.om.values.TVPValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.TimeRangeValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ProfileLevelVisitor;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ParameterDAO;
import org.n52.sos.ds.hibernate.dao.PlatformDAO;
import org.n52.sos.ds.hibernate.dao.UnitDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.util.GeometryHandler;

public class ObservationPersister
        implements ValueVisitor<DataEntity<?>, OwsExceptionReport>, ProfileLevelVisitor<DataEntity<?>> {

    private final DatasetEntity dataset;

    private final AbstractFeatureEntity<?> featureOfInterest;

    private final Caches caches;

    private final Session session;

    private final Geometry samplingGeometry;

    private final DAOs daos;

    private final ObservationFactory observationFactory;

    private final OmObservation omObservation;

    private final Set<OfferingEntity> offerings;

    private GeometryHandler geometryHandler;

    private Long parent;

    public ObservationPersister(GeometryHandler geometryHandler, AbstractObservationDAO observationDao,
            DaoFactory daoFactory, OmObservation sosObservation, DatasetEntity hDataset,
            AbstractFeatureEntity<?> hFeature, Map<String, CodespaceEntity> codespaceCache,
            Map<UoM, UnitEntity> unitCache, Set<OfferingEntity> hOfferings, Session session)
            throws OwsExceptionReport {
        this(geometryHandler, new DAOs(observationDao, daoFactory), new Caches(codespaceCache, unitCache),
                sosObservation, hDataset, hFeature, null, hOfferings, session, null);
    }

    private ObservationPersister(GeometryHandler geometryHandler, DAOs daos, Caches caches, OmObservation observation,
            DatasetEntity hDataset, AbstractFeatureEntity<?> hFeature, Geometry samplingGeometry,
            Set<OfferingEntity> hOfferings, Session session, Long parentId) throws OwsExceptionReport {
        this.geometryHandler = geometryHandler;
        this.dataset = hDataset;
        this.featureOfInterest = hFeature;
        this.caches = caches;
        this.omObservation = observation;
        this.samplingGeometry =
                samplingGeometry != null ? samplingGeometry : getSamplingGeometry(omObservation, geometryHandler);
        this.session = session;
        this.daos = daos;
        this.observationFactory = daos.observation().getObservationFactory();
        this.parent = parentId;
        this.offerings = hOfferings;
    }

    @Override
    public DataEntity<?> visit(BooleanValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.truth(), value);
    }

    @Override
    public DataEntity<?> visit(CategoryValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.category(), value);
    }

    @Override
    public DataEntity<?> visit(CountValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.count(), value);
    }

    @Override
    public DataEntity<?> visit(GeometryValue value) throws OwsExceptionReport {
        GeometryDataEntity geometry = observationFactory.geometry();
        return persist((DataEntity) geometry, value.getValue());
    }

    @Override
    public DataEntity<?> visit(QuantityValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.numeric(), value);
    }

    @Override
    public DataEntity<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(TextValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.text(), value);
    }

    @Override
    public DataEntity<?> visit(UnknownValue value) throws OwsExceptionReport {
        return setUnitAndPersist(observationFactory.blob(), value);
    }

    @Override
    public DataEntity<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
        // return persist(observationFactory.sweDataArray(), value.getValue());
        // TODO
        return null;
    }

    @Override
    public DataEntity<?> visit(ComplexValue value) throws OwsExceptionReport {
        ComplexDataEntity complex = observationFactory.complex();
        DataEntity complexyDataEntity = persist((DataEntity) complex, new HashSet<DataEntity<?>>());
        persistChildren(value.getValue(), complexyDataEntity.getId());
        return complexyDataEntity;
    }

    @Override
    public DataEntity<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(NilTemplateValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(ReferenceValue value) throws OwsExceptionReport {
        ReferencedDataEntity reference = observationFactory.reference();
        reference.setName(value.getValue().getTitle());
        return persist(reference, value.getValue().getHref());
    }

    @Override
    public DataEntity<?> visit(TVPValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(TLVTValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(ProfileValue value) throws OwsExceptionReport {
        ProfileDataEntity profile = observationFactory.profile();
        if (value.isSetFromLevel()) {
            profile.setVerticalFrom(value.getFromLevel().getValue());
            profile.setVerticalFromName(value.getFromLevel().getDefinition());
            if (value.getFromLevel().isSetUom()) {
                profile.setVerticalUnit(getUnit(value.getFromLevel().getUomObject(), caches.units, session));
            }
        }
        if (value.isSetToLevel()) {
            profile.setVerticalTo(value.getToLevel().getValue());
            profile.setVerticalToName(value.getToLevel().getDefinition());
            if (!profile.hasVerticalUnit() && value.getToLevel().isSetUom()) {
                profile.setVerticalUnit(getUnit(value.getToLevel().getUomObject(), caches.units, session));
            }
        }
        omObservation.getValue().setPhenomenonTime(value.getPhenomenonTime());
        DataEntity profileDataEntity = persist((DataEntity) profile, new HashSet<DataEntity<?>>());
        persistChildren(value.getValue(), profileDataEntity.getId());
        return profileDataEntity;
    }

    @Override
    public Collection<DataEntity<?>> visit(ProfileLevel value) throws OwsExceptionReport {
        List<DataEntity<?>> childObservations = new ArrayList<>();
        if (value.isSetValue()) {
            for (Value<?> v : value.getValue()) {
                DataEntity<?> d = v.accept(this);
                if (value.isSetLevelStart()) {
                    d.setVerticalFrom(value.getLevelStart().getValue());
                }
                if (value.isSetLevelEnd()) {
                    d.setVerticalTo(value.getLevelEnd().getValue());
                }
                session.saveOrUpdate(d);
                childObservations.add(d);
            }
        }
        return childObservations;
    }

    @Override
    public DataEntity<?> visit(XmlValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public DataEntity<?> visit(TimeRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    private Set<DataEntity<?>> persistChildren(SweAbstractDataRecord dataRecord, Long parent)
            throws HibernateException, OwsExceptionReport {
        Set<DataEntity<?>> children = new TreeSet<>();
        for (SweField field : dataRecord.getFields()) {
            PhenomenonEntity observableProperty = getObservablePropertyForField(field);
            ObservationPersister childPersister = createChildPersister(observableProperty, parent);
            children.add(field.accept(ValueCreatingSweDataComponentVisitor.getInstance()).accept(childPersister));
        }
        session.flush();
        return children;
    }

    private Set<DataEntity<?>> persistChildren(List<ProfileLevel> values, Long parent) throws OwsExceptionReport {
        Set<DataEntity<?>> children = new TreeSet<>();
        for (ProfileLevel level : values) {
            if (level.isSetValue()) {
                // for (Value<?> v : level.getValue()) {
                // if (v instanceof SweAbstractDataComponent &&
                // ((SweAbstractDataComponent) v).isSetDefinition()) {
                // children.add(v.accept(createChildPersister(level,
                // ((SweAbstractDataComponent) v).getDefinition())));
                // } else {
                children.addAll(level.accept(createChildPersister(level, parent)));
                // }
                // }
            }
        }
        session.flush();
        return children;
    }

    private OmObservation getObservationWithLevelParameter(ProfileLevel level) {
        OmObservation o = new OmObservation();
        omObservation.copyTo(o);
        // o.setParameter(level.getLevelStartEndAsParameter());
        if (level.isSetPhenomenonTime()) {
            o.setValue(new SingleObservationValue<>());
            o.getValue().setPhenomenonTime(level.getPhenomenonTime());
        }
        return o;
    }

    private ObservationPersister createChildPersister(ProfileLevel level, Long parent) throws OwsExceptionReport {
        return new ObservationPersister(geometryHandler, daos, caches, getObservationWithLevelParameter(level),
                dataset, featureOfInterest, getSamplingGeometryFromLevel(level), offerings, session, parent);

    }

    private ObservationPersister createChildPersister(PhenomenonEntity observableProperty, Long parent)
            throws OwsExceptionReport {
        return new ObservationPersister(geometryHandler, daos, caches, omObservation,
                getObservationConstellation(observableProperty), featureOfInterest, samplingGeometry, offerings,
                session, parent);
    }

    private DatasetEntity getObservationConstellation(PhenomenonEntity observableProperty) throws OwsExceptionReport {
        return daos.dataset().checkOrInsertSeries(dataset.getProcedure(), observableProperty, dataset.getOffering(),
                dataset.getCategory(), featureOfInterest, dataset.getPlatform(), true, session);
    }

    private OwsExceptionReport notSupported(Value<?> value) throws OwsExceptionReport {
        throw new NoApplicableCodeException().withMessage("Unsupported observation value %s",
                value.getClass().getCanonicalName());
    }

    private PhenomenonEntity getObservablePropertyForField(SweField field) {
        String definition = field.getElement().getDefinition();
        if (omObservation.getObservationConstellation().getObservableProperty() instanceof OmCompositePhenomenon) {
            for (OmObservableProperty component : ((OmCompositePhenomenon) omObservation.getObservationConstellation()
                    .getObservableProperty()).getPhenomenonComponents()) {
                if (component.getIdentifier().equals(definition)) {
                    getObservableProperty(component);
                }
            }
        }
        return getObservableProperty(new OmObservableProperty(definition));
    }

    private PhenomenonEntity getObservableProperty(AbstractPhenomenon observableProperty) {
        return daos.observableProperty().getOrInsertObservableProperty(observableProperty, session);
    }

    private <V, T extends DataEntity<V>> T setUnitAndPersist(T observation, Value<V> value) throws OwsExceptionReport {
        if (!dataset.hasUnit()) {
            dataset.setUnit(getUnit(value));
        }
        return persist(observation, value.getValue());
    }

    private UnitEntity getUnit(Value<?> value) {
        return value.isSetUnit() ? daos.observation().getUnit(value.getUnitObject(), caches.units(), session) : null;
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session
     *            the session
     * @return Unit
     */
    protected UnitEntity getUnit(String unit, Map<UoM, UnitEntity> localCache, Session session) {
        return getUnit(new UoM(unit), localCache, session);
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session
     *            the session
     * @return Unit
     */
    protected UnitEntity getUnit(UoM unit, Map<UoM, UnitEntity> localCache, Session session) {
        if (localCache != null && localCache.containsKey(unit)) {
            return localCache.get(unit);
        } else {
            // query unit and set cache
            UnitEntity hUnit = daos.unit.getOrInsertUnit(unit, session);
            if (localCache != null) {
                localCache.put(unit, hUnit);
            }
            return hUnit;
        }
    }

    private <V, T extends DataEntity<V>> T persist(T observation, V value) throws OwsExceptionReport {
        observation.setDeleted(false);

        if (parent == null) {
            daos.observation().addIdentifier(omObservation, observation, session);
        } else {
            observation.setParent(parent);
        }

        daos.observation().addName(omObservation, observation, session);
        daos.observation().addDescription(omObservation, observation);
        daos.observation().addTime(omObservation, observation);
        observation.setValue(value);
        if (samplingGeometry != null) {
            GeometryEntity geometryEntity = new GeometryEntity();
            geometryEntity.setGeometry(samplingGeometry);
            observation.setGeometryEntity(geometryEntity);
            checkUpdateFeatureOfInterestGeometry();
            omObservation.removeSpatialFilteringProfileParameter();
        }
        ObservationContext observationContext = daos.observation().createObservationContext();

        String observationType = ObservationTypeObservationVisitor.getInstance().visit((DataEntity) observation);

        observationContext
                .setObservationType(daos.observationType().getOrInsertFormatEntity(observationType, session));

        if (dataset != null) {
            if (!isProfileObservation(dataset) || (isProfileObservation(dataset) && parent == null)) {
                offerings.add(dataset.getOffering());
                if (!daos.dataset().checkObservationType(dataset, observationType, session)) {
                    throw new InvalidParameterValueException().withMessage(
                            "The requested observationType (%s) is invalid for procedure = "
                                    + "%s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                            observationType, observation.getDataset().getProcedure().getIdentifier(),
                            dataset.getObservableProperty().getIdentifier(), dataset.getOffering().getIdentifier(),
                            dataset.getOmObservationType().getFormat());
                }
            }

            observationContext.setPhenomenon(dataset.getObservableProperty());
            observationContext.setProcedure(dataset.getProcedure());
            observationContext.setOffering(dataset.getOffering());
            observationContext.setCategory(dataset.getCategory());
            observationContext.setPlatform(dataset.getPlatform());
        }
        // currently only profiles with one observedProperty are supported
        if (parent != null && !isProfileObservation(dataset)) {
            observationContext.setHiddenChild(true);
        }
        observationContext.setFeatureOfInterest(featureOfInterest);
        if (!observationContext.isSetPlatform()) {
            observationContext.setPlatform(daos.platform().getOrInsertPlatform(featureOfInterest, session));
        }

        daos.observation().fillObservationContext(observationContext, omObservation, session);
        DatasetEntity persitedDataset =
                daos.observation().addObservationContextToObservation(observationContext, observation, session);
        if (omObservation.isSetParameter()) {
            Set<ParameterEntity<?>> insertParameter =
                    daos.parameter().insertParameter(omObservation.getParameter(), caches.units, session);
            observation.setParameters(insertParameter);
        }
        session.saveOrUpdate(observation);
        session.flush();
        session.refresh(observation);
        daos.dataset.updateSeriesWithFirstLatestValues(persitedDataset, (DataEntity<?>) observation, session);

        return observation;
    }

    private boolean isProfileObservation(DatasetEntity observationConstellation) {
        return observationConstellation.isSetOmObservationType() && (OmConstants.OBS_TYPE_PROFILE_OBSERVATION
                .equals(observationConstellation.getOmObservationType().getFormat())
                || GWMLConstants.OBS_TYPE_GEOLOGY_LOG
                        .equals(observationConstellation.getOmObservationType().getFormat())
                || GWMLConstants.OBS_TYPE_GEOLOGY_LOG_COVERAGE
                        .equals(observationConstellation.getOmObservationType().getFormat()));
    }

    private Geometry getSamplingGeometryFromLevel(ProfileLevel level) throws OwsExceptionReport {
        if (level.isSetLocation()) {
            return geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(level.getLocation());
        }
        return null;
    }

    private Geometry getSamplingGeometry(OmObservation sosObservation, GeometryHandler geometryHandler)
            throws OwsExceptionReport {
        if (!sosObservation.isSetSpatialFilteringProfileParameter()) {
            return null;
        }
        if (sosObservation.isSetValue() && sosObservation.getValue().isSetValue()
                && sosObservation.getValue().getValue() instanceof ProfileValue
                && ((ProfileValue) sosObservation.getValue().getValue()).isSetGeometry()) {
            return geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(
                    ((ProfileValue) sosObservation.getValue().getValue()).getGeometry());
        }
        NamedValue<org.locationtech.jts.geom.Geometry> spatialFilteringProfileParameter =
                sosObservation.getSpatialFilteringProfileParameter();
        return geometryHandler
                .switchCoordinateAxisFromToDatasourceIfNeeded(spatialFilteringProfileParameter.getValue().getValue());
    }

    private void checkUpdateFeatureOfInterestGeometry() throws CodedException {
        // check if flag is set and if this observation is not a child
        // observation
        if (samplingGeometry != null && isUpdateFeatureGeometry() && parent != null) {
            daos.feature.updateFeatureOfInterestGeometry(featureOfInterest, samplingGeometry, session);
        }
    }

    private boolean isUpdateFeatureGeometry() {
        // TODO
        return true;
    }

    private static class Caches {
        private final Map<String, CodespaceEntity> codespaces;

        private final Map<UoM, UnitEntity> units;

        Caches(Map<String, CodespaceEntity> codespaces, Map<UoM, UnitEntity> units) {
            this.codespaces = codespaces;
            this.units = units;
        }

        public Map<String, CodespaceEntity> codespaces() {
            return codespaces;
        }

        public Map<UoM, UnitEntity> units() {
            return units;
        }

    }

    private static class DAOs {
        private final ObservablePropertyDAO observableProperty;

        private final AbstractObservationDAO observation;

        private final FormatDAO observationType;

        private final ParameterDAO parameter;

        private final FeatureOfInterestDAO feature;

        private final PlatformDAO platform;

        private final AbstractSeriesDAO dataset;

        private final UnitDAO unit;

        DAOs(AbstractObservationDAO observationDao, DaoFactory daoFactory) {
            this.observation = observationDao;
            this.observableProperty = daoFactory.getObservablePropertyDAO();
            this.observationType = daoFactory.getObservationTypeDAO();
            this.parameter = daoFactory.getParameterDAO();
            this.feature = daoFactory.getFeatureOfInterestDAO();
            this.platform = daoFactory.getPlatformDAO();
            this.dataset = daoFactory.getSeriesDAO();
            this.unit = daoFactory.getUnitDAO();
        }

        public ObservablePropertyDAO observableProperty() {
            return this.observableProperty;
        }

        public AbstractObservationDAO observation() {
            return this.observation;
        }

        public FormatDAO observationType() {
            return this.observationType;
        }

        public ParameterDAO parameter() {
            return this.parameter;
        }

        public FeatureOfInterestDAO feature() {
            return this.feature;
        }

        public PlatformDAO platform() {
            return this.platform;
        }

        public AbstractSeriesDAO dataset() {
            return this.dataset;
        }

        public UnitDAO unit() {
            return this.unit;
        }
    }

}

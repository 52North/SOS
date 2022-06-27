/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.HibernateRelations.HasUnit;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.BooleanParameterEntity;
import org.n52.series.db.beans.parameter.CategoryParameterEntity;
import org.n52.series.db.beans.parameter.ComplexParameterEntity;
import org.n52.series.db.beans.parameter.CountParameterEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.ParameterFactory;
import org.n52.series.db.beans.parameter.ParameterFactory.ValueType;
import org.n52.series.db.beans.parameter.QuantityParameterEntity;
import org.n52.series.db.beans.parameter.TemporalParameterEntity;
import org.n52.series.db.beans.parameter.TextParameterEntity;
import org.n52.series.db.beans.parameter.TimeRange;
import org.n52.series.db.beans.parameter.ValuedParameter;
import org.n52.series.db.beans.parameter.XmlParameterEntity;
import org.n52.series.db.beans.parameter.feature.FeatureParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationParameterEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.CvDiscretePointCoverage;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.MultiPointCoverage;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
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
import org.n52.shetland.ogc.om.values.TimeValue;
import org.n52.shetland.ogc.om.values.TrajectoryValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.sos.ds.hibernate.dao.UnitDAO;
import org.n52.sos.ds.hibernate.dao.observation.ValueCreatingSweDataComponentVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Hibernate DAO class to om:pramameter
 *
 * @since 4.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class ParameterCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterCreator.class);

    public Set<ParameterEntity<?>> createParameter(Collection<NamedValue<?>> parameter, Map<UoM, UnitEntity> unitCache,
            DescribableEntity entity, Session session) throws OwsExceptionReport {
        Set<ParameterEntity<?>> parameters = new HashSet<>();
        for (NamedValue<?> namedValue : parameter) {
            if (!Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                ParameterVisitor persister = new ParameterVisitor(this, namedValue, unitCache, entity, session);
                checkParameter(entity, namedValue.getValue().accept(persister), parameters);
            }
        }
        entity.addParameters(parameters);
        return parameters;
    }

    private <
            T extends DataEntity<?>> void checkParameter(DescribableEntity entity, ParameterEntity<?> parameter,
                    Set<ParameterEntity<?>> newParams) {
        if (parameter instanceof ComplexParameterEntity && parameter.getValue() != null) {
            ComplexParameterEntity<?> complex = (ComplexParameterEntity<?>) parameter;
            for (Object v : (Set<?>) complex.getValue()) {
                if (v instanceof ParameterEntity) {
                    ParameterEntity<?> child = (ParameterEntity<?>) v;
                    checkParameter(entity, child, newParams);
                    child.setParent((ParameterEntity<?>) complex);
                }
            }
            complex.setValue(null);
        }
        newParams.add(parameter);
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
            UnitEntity hUnit = new UnitDAO().getOrInsertUnit(unit, session);
            if (localCache != null) {
                localCache.put(unit, hUnit);
            }
            return hUnit;
        }
    }

    public static class ParameterVisitor implements ValueVisitor<ParameterEntity<?>, OwsExceptionReport> {
        private final Caches caches;

        private final Session session;

        private final NamedValue<?> namedValue;

        private final DAOs daos;

        private DescribableEntity entity;

        private ParameterEntity<?> parent;

        public ParameterVisitor(ParameterCreator parameterDAO, NamedValue<?> namedValue, Map<UoM, UnitEntity> unitCache,
                DescribableEntity entity, Session session) {
            this(new DAOs(parameterDAO), new Caches(unitCache), namedValue, entity, session);
        }

        public ParameterVisitor(DAOs daos, Caches caches, NamedValue<?> namedValue, DescribableEntity entity,
                Session session) {
           this(daos, namedValue, caches, entity, null, session);
        }

        public ParameterVisitor(DAOs daos, NamedValue<?> namedValue, Caches caches,
                DescribableEntity entity, ParameterEntity<?> parent, Session session) {
            this.caches = caches;
            this.session = session;
            this.daos = daos;
            this.entity = entity;
            this.namedValue = namedValue;
            this.entity = entity;
            this.parent = parent;
        }

        @Override
        public ParameterEntity<?> visit(BooleanValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.BOOLEAN);
            ((BooleanParameterEntity) param).setValue(value.getValue());
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(CategoryValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.CATEGORY);
            ((CategoryParameterEntity) param).setValue(value.getValue());
            return setUnitAndPersist(param, value);
        }

        @Override
        public ParameterEntity<?> visit(ComplexValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.COMPLEX);
            ((ComplexParameterEntity) param).setValue(new HashSet<ValuedParameter<?>>());
            ParameterEntity<?> complexyDataEntity = persist(param);
            Set<ParameterEntity<?>> persistChildren = persistChildren(value.getValue(), complexyDataEntity);
            ((ComplexParameterEntity) param).setValue(persistChildren);
            return param;
        }

        @Override
        public ParameterEntity<?> visit(CountValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.COUNT);
            ((CountParameterEntity) param).setValue(value.getValue());
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(GeometryValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(NilTemplateValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(QuantityValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.QUANTITY);
            ((QuantityParameterEntity) param).setValue(value.getValue());
            return setUnitAndPersist(param, value);
        }

        @Override
        public ParameterEntity<?> visit(ReferenceValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.TEXT);
            ((TextParameterEntity) param).setValue(value.getValue().getHref());
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(TVPValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(TextValue value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.TEXT);
            ((TextParameterEntity) param).setValue(value.getValue());
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(TimeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(TimeRangeValue value) throws OwsExceptionReport {
            ParameterEntity<?> param = ParameterFactory.from(entity, ValueType.TEMPORAL);
            ((TemporalParameterEntity) param).setValue(new TimeRange(value.getValue()
                    .getRangeStart()
                    .toDate(),
                    value.getValue()
                            .getRangeEnd()
                            .toDate()));
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(UnknownValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(TLVTValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(ProfileValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(TrajectoryValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(XmlValue<?> value) throws OwsExceptionReport {
            ParameterEntity<?> param =  ParameterFactory.from(entity, ValueType.XML);
            ((XmlParameterEntity) param).setValue(value.getValue().toString());
            return persist(param);
        }

        @Override
        public ParameterEntity<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        private Set<ParameterEntity<?>> persistChildren(SweAbstractDataRecord dataRecord, ParameterEntity<?> parameter)
                throws HibernateException, OwsExceptionReport {
            Set<ParameterEntity<?>> children = new LinkedHashSet<>();
            for (SweField field : dataRecord.getFields()) {
                String name = field.getName().getValue();
                Value<?> value = field.accept(ValueCreatingSweDataComponentVisitor.getInstance());
                ParameterVisitor childPersister =
                        createChildPersister(new NamedValue<>(new ReferenceType(name), value), parameter);
                children.add(value.accept(childPersister));
            }
            return children;
        }

        private ParameterVisitor createChildPersister(NamedValue<?> namedValue, ParameterEntity<?> parameter) {
            return new ParameterVisitor(this.daos, namedValue, caches, entity, parameter, session);
        }

        private OwsExceptionReport notSupported(Value<?> value) throws OwsExceptionReport {
            throw new NoApplicableCodeException().withMessage("Unsupported om:parameter value %s", value.getClass()
                    .getCanonicalName());
        }

        private ParameterEntity<?> setUnitAndPersist(ParameterEntity<?> param, Value<?> value)
                throws OwsExceptionReport {
            if (param instanceof HasUnit) {
                ((HasUnit) param).setUnit(getUnit(value));
            }
            return persist(param);
        }

        private UnitEntity getUnit(Value<?> value) {
            return value.isSetUnit() ? daos.parameter()
                    .getUnit(value.getUnitObject(), caches.units(), session) : null;
        }


        private ParameterEntity<?> persist(ParameterEntity<?> parameter) throws OwsExceptionReport {
            if (parameter instanceof ObservationParameterEntity && entity instanceof DataEntity) {
                ((ObservationParameterEntity) parameter).setObservation((DataEntity) entity);
            } else if (parameter instanceof FeatureParameterEntity && entity instanceof AbstractFeatureEntity) {
                ((FeatureParameterEntity) parameter).setFeature((AbstractFeatureEntity) entity);
            } else {
                throw new NoApplicableCodeException().withMessage("Unable to insert parameter!");
            }
            if (parent != null) {
                parameter.setParent(parent);
            }
            parameter.setName(namedValue.getName().getHref());
            parameter.setDescribeableEntity(entity);
//            session.saveOrUpdate(parameter);
            return parameter;
        }

        private static class Caches {
            private final Map<UoM, UnitEntity> units;

            Caches(Map<UoM, UnitEntity> units) {
                this.units = units;
            }

            public Map<UoM, UnitEntity> units() {
                return units;
            }
        }

        private static class DAOs {
            private final ParameterCreator parameter;

            DAOs(ParameterCreator parameter) {
                this.parameter = parameter;
            }

            public ParameterCreator parameter() {
                return this.parameter;
            }
        }
    }

}

/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.RootEntityResultTransformer;
import org.n52.series.db.beans.HibernateRelations.HasUnit;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.ValuedParameter;
import org.n52.shetland.ogc.UoM;
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
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ParameterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate DAO class to om:pramameter
 *
 * @since 4.0.0
 *
 */
public class ParameterDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterDAO.class);

    public Set<ParameterEntity<?>> insertParameter(Collection<NamedValue<?>> parameter, Map<UoM, UnitEntity> unitCache,
            Session session) throws OwsExceptionReport {
        Set<ParameterEntity<?>> parameters = new HashSet<>();
        for (NamedValue<?> namedValue : parameter) {
            if (!Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                ParameterPersister persister = new ParameterPersister(this, namedValue, unitCache, session);
                parameters.add(namedValue.getValue().accept(persister));
            }
        }
        return parameters;
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session the session
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
     * @param session the session
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

    public ParameterFactory getParameterFactory() {
        return ParameterFactory.getInstance();
    }

    public static class ParameterPersister implements ValueVisitor<ParameterEntity<?>, OwsExceptionReport> {
        private final Caches caches;

        private final Session session;

        private final NamedValue<?> namedValue;

        private final DAOs daos;

        private final ParameterFactory parameterFactory;

        public ParameterPersister(ParameterDAO parameterDAO, NamedValue<?> namedValue, Map<UoM, UnitEntity> unitCache,
                Session session) {
            this(new DAOs(parameterDAO), new Caches(unitCache), namedValue, session);
        }

        public ParameterPersister(DAOs daos, Caches caches, NamedValue<?> namedValue, Session session) {
            this.caches = caches;
            this.session = session;
            this.daos = daos;
            this.namedValue = namedValue;
            this.parameterFactory = daos.parameter.getParameterFactory();
        }

        @Override
        public ParameterEntity<?> visit(BooleanValue value) throws OwsExceptionReport {
            return persist(parameterFactory.truth(), value);
        }

        @Override
        public ParameterEntity<?> visit(CategoryValue value) throws OwsExceptionReport {
            return setUnitAndPersist(parameterFactory.category(), value);
        }

        @Override
        public ParameterEntity<?> visit(ComplexValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(CountValue value) throws OwsExceptionReport {
            return persist(parameterFactory.count(), value);
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
            return setUnitAndPersist(parameterFactory.quantity(), value);
        }

        @Override
        public ParameterEntity<?> visit(ReferenceValue value) throws OwsExceptionReport {
            return persist(parameterFactory.category(), value.getValue().getHref());
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
            return persist(parameterFactory.text(), value);
        }

        @Override
        public ParameterEntity<?> visit(TimeRangeValue value) throws OwsExceptionReport {
            throw notSupported(value);
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
        public ParameterEntity<?> visit(XmlValue<?> value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ParameterEntity<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        private OwsExceptionReport notSupported(Value<?> value) throws OwsExceptionReport {
            throw new NoApplicableCodeException().withMessage("Unsupported om:parameter value %s",
                    value.getClass().getCanonicalName());
        }

        private <V, T extends ParameterEntity<V>> T setUnitAndPersist(T parameter, Value<V> value)
                throws OwsExceptionReport {
            if (parameter instanceof HasUnit) {
                ((HasUnit) parameter).setUnit(getUnit(value));
            }
            return persist(parameter, value.getValue());
        }

        private UnitEntity getUnit(Value<?> value) {
            return value.isSetUnit() ? daos.parameter().getUnit(value.getUnitObject(), caches.units(), session) : null;
        }

        private <V, T extends ParameterEntity<V>> T persist(T parameter, Value<V> value) throws OwsExceptionReport {
            return persist(parameter, value.getValue());
        }

        private <V, T extends ParameterEntity<V>> T persist(T parameter, V value) throws OwsExceptionReport {
            Criteria c = session.createCriteria(parameter.getClass())
                    .setResultTransformer(RootEntityResultTransformer.INSTANCE)
                    .add(Restrictions.eq(ValuedParameter.NAME, namedValue.getName().getHref()))
                    .add(Restrictions.eq(ValuedParameter.VALUE, value));
            if (parameter instanceof HasUnit && !((HasUnit) parameter).isSetUnit()
                    && getUnit(namedValue.getValue()) != null) {
                ((HasUnit) parameter).setUnit(getUnit(namedValue.getValue()));
                c.add(Restrictions.eq(HasUnit.UNIT, ((HasUnit) parameter).getUnit()));
            }
            LOG.trace("QUERY parameter: {}", HibernateHelper.getSqlString(c));
            ParameterEntity p = (ParameterEntity) c.uniqueResult();
            if (p != null) {
                return (T) p;
            }
            parameter.setName(namedValue.getName().getHref());
            parameter.setValue(value);
            session.saveOrUpdate(parameter);
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
            private final ParameterDAO parameter;

            DAOs(ParameterDAO parameter) {
                this.parameter = parameter;
            }

            public ParameterDAO parameter() {
                return this.parameter;
            }
        }
    }

}

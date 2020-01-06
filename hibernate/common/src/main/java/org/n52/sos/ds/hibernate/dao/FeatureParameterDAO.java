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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
import java.util.Map;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.parameter.ValuedParameter;
import org.n52.sos.ds.hibernate.entities.parameter.feature.FeatureParameterFactory;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
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


/**
 * Hibernate DAO class to sf:pramameter
 * 
 * @since 4.0.0
 * 
 */
public class FeatureParameterDAO {

    public void insertParameter(Collection<NamedValue<?>> parameter, long featureOfInterestId, Map<UoM, Unit> unitCache, Session session) throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameter) {
            ParameterPersister persister = new ParameterPersister(
                    this,
                    namedValue,
                    featureOfInterestId,
                    unitCache,
                    session
            );
            namedValue.getValue().accept(persister);
        }
    }
    
    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session
     * @return Unit
     */
    protected Unit getUnit(String unit, Map<UoM, Unit> localCache, Session session) {
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
     * @return Unit
     */
    protected Unit getUnit(UoM unit, Map<UoM, Unit> localCache, Session session) {
        if (localCache != null && localCache.containsKey(unit)) {
            return localCache.get(unit);
        } else {
            // query unit and set cache
            Unit hUnit = new UnitDAO().getOrInsertUnit(unit, session);
            if (localCache != null) {
                localCache.put(unit, hUnit);
            }
            return hUnit;
        }
    }

    public FeatureParameterFactory getParameterFactory() {
        return FeatureParameterFactory.getInstance();
    }

    public static class ParameterPersister implements ValueVisitor<ValuedParameter<?>> {
        private final Caches caches;
        private final Session session;
        private final long featureOfInterestId;
        private final NamedValue<?> namedValue;
        private final DAOs daos;
        private final FeatureParameterFactory parameterFactory;

        public ParameterPersister(FeatureParameterDAO parameterDAO, NamedValue<?> namedValue, long featureOfInterestId, Map<UoM, Unit> unitCache, Session session) {
            this(new DAOs(parameterDAO),
                    new Caches(unitCache),
                    namedValue,
                    featureOfInterestId,
                    session);
        }
        
        public ParameterPersister(DAOs daos, Caches caches, NamedValue<?> namedValue, long featureOfInterestId, Session session) {
            this.featureOfInterestId = featureOfInterestId;
            this.caches = caches;
            this.session = session;
            this.daos = daos;
            this.namedValue = namedValue;
            this.parameterFactory = daos.parameter.getParameterFactory();
        }

        private static class Caches {
            private final Map<UoM, Unit> units;

            Caches( Map<UoM, Unit> units) {
                this.units = units;
            }

            public Map<UoM, Unit> units() {
                return units;
            }
        }
    
        private static class DAOs {
            private final FeatureParameterDAO parameter;

            DAOs(FeatureParameterDAO parameter) {
                this.parameter = parameter;
            }

            public FeatureParameterDAO parameter() {
                return this.parameter;
            }
        }
        
        private <V, T extends ValuedParameter<V>> T setUnitAndPersist(T parameter, Value<V> value) throws OwsExceptionReport {
            if (parameter instanceof HasUnit) {
                ((HasUnit)parameter).setUnit(getUnit(value));
            }
            return persist(parameter, value.getValue());
        }
        
        private Unit getUnit(Value<?> value) {
            return value.isSetUnit() ? daos.parameter().getUnit(value.getUnitObject(), caches.units(), session) : null;
        }
        
        private <V, T extends ValuedParameter<V>> T persist(T parameter, Value<V> value) throws OwsExceptionReport {
            return persist(parameter, value.getValue());
        }
        
        private <V, T extends ValuedParameter<V>> T persist(T parameter, V value) throws OwsExceptionReport {
            if (parameter instanceof org.n52.sos.ds.hibernate.entities.parameter.feature.FeatureParameter) {
                if (parameter instanceof HasUnit && !((HasUnit)parameter).isSetUnit()) {
                    ((HasUnit)parameter).setUnit(getUnit(namedValue.getValue()));
                }
                
                ((org.n52.sos.ds.hibernate.entities.parameter.feature.FeatureParameter)parameter).setFeatureOfInterestId(featureOfInterestId);
                parameter.setName(namedValue.getName().getHref());
                parameter.setValue(value);
                session.saveOrUpdate(parameter);
                session.flush();
            }
            return null;
        }

        @Override
        public ValuedParameter<?> visit(BooleanValue value) throws OwsExceptionReport {
            return persist(parameterFactory.truth(), value);
        }

        @Override
        public ValuedParameter<?> visit(CategoryValue value) throws OwsExceptionReport {
            return setUnitAndPersist(parameterFactory.category(), value);
        }

        @Override
        public ValuedParameter<?> visit(ComplexValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(CountValue value) throws OwsExceptionReport {
            return persist(parameterFactory.count(), value);
        }

        @Override
        public ValuedParameter<?> visit(GeometryValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(NilTemplateValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(QuantityValue value) throws OwsExceptionReport {
            return setUnitAndPersist(parameterFactory.quantity(), value);
        }

        @Override
        public ValuedParameter<?> visit(ReferenceValue value) throws OwsExceptionReport {
            return persist(parameterFactory.category(), value.getValue().getHref());
        }

        @Override
        public ValuedParameter<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(TVPValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(TextValue value) throws OwsExceptionReport {
            return persist(parameterFactory.text(), value);
        }

        @Override
        public ValuedParameter<?> visit(UnknownValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }
        
        @Override
        public ValuedParameter<?> visit(TLVTValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(ProfileValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public ValuedParameter<?> visit(XmlValue value)
                throws OwsExceptionReport {
            return persist(parameterFactory.xml(), value.getValue().xmlText());
        }

        @Override
        public ValuedParameter<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        private OwsExceptionReport notSupported(Value<?> value)
                throws OwsExceptionReport {
            throw new NoApplicableCodeException()
                    .withMessage("Unsupported sf:parameter value %s", value
                                 .getClass().getCanonicalName());
        }
    }

}

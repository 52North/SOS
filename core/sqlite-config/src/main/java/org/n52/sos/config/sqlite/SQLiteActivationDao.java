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
package org.n52.sos.config.sqlite;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import org.n52.iceland.binding.BindingKey;
import org.n52.iceland.binding.PathBindingKey;
import org.n52.iceland.config.ActivationDao;
import org.n52.iceland.ogc.ows.extension.OwsExtendedCapabilitiesProviderKey;
import org.n52.iceland.ogc.swes.OfferingExtensionKey;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.sos.config.sqlite.entities.Activatable;
import org.n52.sos.config.sqlite.entities.Binding;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtension;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtensionKey;
import org.n52.sos.config.sqlite.entities.DynamicOwsExtendedCapabilities;
import org.n52.sos.config.sqlite.entities.DynamicOwsExtendedCapabilitiesKey;
import org.n52.sos.config.sqlite.entities.Operation;
import org.n52.sos.config.sqlite.entities.OperationKey;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SQLiteActivationDao
        extends AbstractSQLiteDao
        implements ActivationDao {

    // OFFERING EXTENSION
    @Override
    public void setOfferingExtensionStatus(OfferingExtensionKey oek,
                                           boolean active) {
        setActive(DynamicOfferingExtension.class, new DynamicOfferingExtension(oek), active);
    }

    @Override
    public boolean isOfferingExtensionActive(OfferingExtensionKey oek) {
        return isActive(DynamicOfferingExtension.class, new DynamicOfferingExtensionKey(oek));
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeys() {
        return asOfferingExtensionKeys(getKeys(DynamicOfferingExtension.class));
    }

    private Set<OfferingExtensionKey> asOfferingExtensionKeys(
            List<DynamicOfferingExtensionKey> hkeys) {
        Set<OfferingExtensionKey> keys = new HashSet<>(hkeys.size());
        for (DynamicOfferingExtensionKey key : hkeys) {
            keys.add(new OfferingExtensionKey(new OwsServiceKey(key.getService(), key.getVersion()), key.getDomain()));
        }
        return keys;
    }

    // BINDING
    @Override
    public void setBindingStatus(BindingKey bk, boolean active) {
        setActive(Binding.class, new Binding(bk.getKeyAsString()), active);
    }

    @Override
    public boolean isBindingActive(BindingKey bk) {
        return isActive(Binding.class, bk.getKeyAsString());
    }

    @Override
    public Set<BindingKey> getBindingKeys() {
        return asBindingKeys(getKeys(Binding.class));
    }

    private Set<BindingKey> asBindingKeys(List<String> hkeys) {
        Set<BindingKey> keys = new HashSet<>(hkeys.size());
        for (String key : hkeys) {
            keys.add(new PathBindingKey(key));
        }
        return keys;
    }

    // OWS EXTENDED CAPABILITIES
    @Override
    public boolean isOwsExtendedCapabilitiesProviderActive(
            OwsExtendedCapabilitiesProviderKey oeck) {
        return isActive(DynamicOwsExtendedCapabilities.class, new DynamicOwsExtendedCapabilitiesKey(oeck));
    }

    @Override
    public void setOwsExtendedCapabilitiesStatus(
            OwsExtendedCapabilitiesProviderKey oeck, boolean active) {
        setActive(DynamicOwsExtendedCapabilities.class, new DynamicOwsExtendedCapabilities(oeck), active);
    }

    @Override
    public Set<OwsExtendedCapabilitiesProviderKey> getOwsExtendedCapabilitiesProviderKeys() {
        return asOwsExtendedCapabilitiesProviderKeys(getKeys(DynamicOwsExtendedCapabilities.class));
    }

    private Set<OwsExtendedCapabilitiesProviderKey> asOwsExtendedCapabilitiesProviderKeys(
            List<DynamicOwsExtendedCapabilitiesKey> hkeys) {
        Set<OwsExtendedCapabilitiesProviderKey> keys = new HashSet<>(hkeys
                .size());
        for (DynamicOwsExtendedCapabilitiesKey key : hkeys) {
            keys
                    .add(new OwsExtendedCapabilitiesProviderKey(new OwsServiceKey(key
                                            .getService(), key.getVersion()), key
                                                                .getDomain()));
        }
        return keys;
    }

    // REQUEST OPERATOR
    @Override
    public boolean isRequestOperatorActive(
            RequestOperatorKey requestOperatorKeyType) {
        return isActive(Operation.class, new OperationKey(requestOperatorKeyType), requestOperatorKeyType
                        .isDefaultActive());
    }

    @Override
    public void setOperationStatus(RequestOperatorKey key, boolean active) {
        setActive(Operation.class, new Operation(key), active);
    }

    @Override
    public Set<RequestOperatorKey> getRequestOperatorKeys() {
        return asRequestOperatorKeys(getKeys(Operation.class));
    }

    private Set<RequestOperatorKey> asRequestOperatorKeys(
            List<OperationKey> hkeys) {
        Set<RequestOperatorKey> keys = new HashSet<>(hkeys.size());
        for (OperationKey key : hkeys) {
            keys.add(new RequestOperatorKey(new OwsServiceKey(key
                    .getService(), key.getVersion()), key.getOperationName()));
        }
        return keys;
    }

    protected <K extends Serializable, T extends Activatable<K, T>> void setActive(
            Class<T> type, T activatable, boolean active) {
        execute(new SetActiveAction<>(type, activatable, active));
    }

    protected <K extends Serializable, T extends Activatable<K, T>> boolean isActive(
            Class<T> c, K key) {
        return execute(new IsActiveAction<>(c, key));
    }

    protected <K extends Serializable, T extends Activatable<K, T>> boolean isActive(
            Class<T> c, K key, boolean defaultActive) {
        return execute(new IsActiveAction<>(c, key, defaultActive));
    }

    protected <K extends Serializable, T extends Activatable<K, T>> List<K> getKeys(
            Class<T> c) {
        return execute(new GetKeysAction<>(c));
    }

    protected class GetKeysAction<K extends Serializable, T extends Activatable<K, T>>
            implements HibernateAction<List<K>> {

        private final Class<T> type;

        GetKeysAction(Class<T> type) {
            this.type = type;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<K> call(Session session) {
            return session.createCriteria(type)
                    .setProjection(Projections.property("key"))
                    .list();
        }
    }

    protected class IsActiveAction<K extends Serializable, T extends Activatable<K, T>>
            implements HibernateAction<Boolean> {
        private final K key;

        private final Class<T> type;

        private boolean defaultActive;

        IsActiveAction(Class<T> type, K key) {
            this(type, key, true);
        }

        IsActiveAction(Class<T> type, K key, boolean defaultActive) {
            this.type = type;
            this.key = key;
            this.defaultActive = defaultActive;
        }

        @Override
        public Boolean call(Session session) {
            @SuppressWarnings("unchecked")
            T o = (T) session.get(type, key);
            return (o == null) ? defaultActive : o.isActive();
        }
    }

    protected class SetActiveAction<K extends Serializable, T extends Activatable<K, T>>
            extends VoidHibernateAction {
        private final Activatable<K, T> activatable;

        private final Class<T> type;

        private final boolean active;

        SetActiveAction(Class<T> type, T activatable, boolean active) {
            this.activatable = activatable;
            this.type = type;
            this.active = active;
        }

        @Override
        protected void run(Session session) {
            @SuppressWarnings("unchecked")
            T o = (T) session.get(type, activatable.getKey());
            if (o != null) {
                if (active != o.isActive()) {
                    session.update(o.setActive(active));
                }
            } else {
                session.save(activatable.setActive(active));
            }
        }
    }
}

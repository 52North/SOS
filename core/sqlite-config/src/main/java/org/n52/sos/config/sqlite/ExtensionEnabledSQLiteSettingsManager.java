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
package org.n52.sos.config.sqlite;

import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.CapabilitiesExtensionManager;
import org.n52.sos.config.sqlite.SQLiteManager.HibernateAction;
import org.n52.sos.config.sqlite.SQLiteManager.ThrowingHibernateAction;
import org.n52.sos.config.sqlite.SQLiteManager.ThrowingVoidHibernateAction;
import org.n52.sos.config.sqlite.SQLiteManager.VoidHibernateAction;
import org.n52.sos.config.sqlite.entities.Activatable;
import org.n52.sos.config.sqlite.entities.CapabilitiesExtensionImpl;
import org.n52.sos.config.sqlite.entities.OfferingExtensionIdentifier;
import org.n52.sos.config.sqlite.entities.OfferingExtensionImpl;
import org.n52.sos.config.sqlite.entities.StaticCapabilitiesImpl;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.StaticCapabilities;
import org.n52.sos.ogc.ows.StringBasedCapabilitiesExtension;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.LinkedListMultiMap;
import org.n52.sos.util.ListMultiMap;

public class ExtensionEnabledSQLiteSettingsManager extends SQLiteSettingsManager implements CapabilitiesExtensionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionEnabledSQLiteSettingsManager.class);
    private final ReadWriteLock scLock = new ReentrantReadWriteLock();
    private final ReadWriteLock ceLock = new ReentrantReadWriteLock();
    private final ReadWriteLock oeLock = new ReentrantReadWriteLock();
    private String cachedScId;
    private String cachedSc;
    private Map<String, StringBasedCapabilitiesExtension> cachedCe;
    private Map<String, Map<String, String>> cachedOe;

    private void checkOffering(final String offering) throws NoSuchOfferingException {
        if (!getCache().hasOffering(offering)) {
            throw new NoSuchOfferingException(offering);
        }
    }

    @Override
    protected <T> T execute(final HibernateAction<T> action) {
        try {
            return super.execute(action);
        } catch (ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected <T> T throwingExecute(ThrowingHibernateAction<T> action)
            throws NoSuchExtensionException {
        try {
            return super.throwingExecute(action);
        } catch (Exception ex) {
            if (ex instanceof NoSuchExtensionException) {
                throw (NoSuchExtensionException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }


    @Override
    public void setActiveStaticCapabilities(final String identifier) throws NoSuchExtensionException {
        if (identifier != null) {
            throwingExecute(new SetActiveStaticCapabilitiesAction(identifier));
        } else {
            execute(new DeactivateStaticCapabilitiesAction());
        }
        scLock.writeLock().lock();
        try {
            cachedScId = identifier;
            cachedSc = null;
        } finally {
            scLock.writeLock().unlock();
        }
    }

    @Override
    public String getActiveStaticCapabilities() {
        scLock.readLock().lock();
        try {
            if (cachedScId == null) {
                cachedScId = execute(new GetActiveStaticCapabilitiesAction());
            }
        } finally {
            scLock.readLock().unlock();
        }
        return cachedScId;
    }

    @Override
    public String getActiveStaticCapabilitiesDocument() {
        boolean load = false;
        scLock.readLock().lock();
        try {
            if (cachedScId != null && cachedSc == null) {
                load = true;
            }
        } finally {
            scLock.readLock().unlock();
        }
        if (load) {
            scLock.readLock().lock();
            try {
                if (cachedScId != null && cachedSc == null) {
                    cachedSc = execute(new GetActiveStaticCapabilitiesDocumentAction());
                }
            } finally {
                scLock.readLock().unlock();
            }
        }
        return cachedSc;
    }

    @Override
    public boolean isStaticCapabilitiesActive() {
        return getActiveStaticCapabilities() != null;
    }

    @Override
    public ListMultiMap<String, OfferingExtension> getOfferingExtensions() {
        return execute(new GetOfferingExtensionsAction());
    }

    @Override
    public ListMultiMap<String, OfferingExtension> getActiveOfferingExtensions() {
        boolean load = false;
        oeLock.readLock().lock();
        try {
            if (cachedOe == null) {
                load = true;
            }
        } finally {
            oeLock.readLock().unlock();
        }
        if (load) {
            oeLock.writeLock().lock();
            try {
                if (cachedOe == null) {
                    final ListMultiMap<String, OfferingExtension> execute = execute(new GetActiveOfferingExtensionsAction());
                    cachedOe = new HashMap<>(execute.size());
                    for (final String offering : execute.keySet()) {
                        final List<OfferingExtension> oes = execute.get(offering);
                        final Map<String, String> map = new HashMap<>(oes.size());
                        for (final OfferingExtension oe : oes) {
                            map.put(oe.getIdentifier(), oe.getExtension());
                        }
                        cachedOe.put(offering, map);
                    }
                }
            } finally {
                oeLock.writeLock().unlock();
            }
        }
        final ListMultiMap<String, OfferingExtension> map = new LinkedListMultiMap<>();
        for (final String offering : cachedOe.keySet()) {
            final Map<String, String> oes = cachedOe.get(offering);
            if (oes != null) {
                for (final Entry<String, String> oe : oes.entrySet()) {
                    map.add(offering, new OfferingExtensionImpl(offering, oe.getKey(), oe.getValue()));
                }
            }
        }
        return map;
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    @Override
    public void saveOfferingExtension(final String offering, final String identifier, final String value) throws
            NoSuchOfferingException {
        checkOffering(offering);
        execute(new SaveOfferingExtensionAction(offering, identifier, value));
        oeLock.writeLock().lock();
        try {
            if (cachedOe != null) {
                Map<String, String> forOffering = cachedOe.get(offering);
                if (forOffering == null) {
                    cachedOe.put(offering, forOffering = new HashMap<>());
                }
                forOffering.put(identifier, value);
            }
        } finally {
            oeLock.writeLock().unlock();
        }
    }

    @Override
    public void disableOfferingExtension(final String offering, final String identifier, final boolean disabled) throws
            NoSuchExtensionException, NoSuchOfferingException {
        checkOffering(offering);
        throwingExecute(new SetActiveOfferingExtensionAction(offering, identifier, disabled));
        oeLock.writeLock().lock();
        try {
            if (cachedOe != null) {
                Map<String, String> forOffering = cachedOe.get(offering);
                if (forOffering == null) {
                    cachedOe.put(offering, forOffering = new HashMap<>());
                }
                if (disabled) {
                    forOffering.remove(identifier);
                } else if (!forOffering.containsKey(identifier)) {
                    final OfferingExtension oe = execute(new GetOfferingExtensionAction(offering, identifier));
                    forOffering.put(oe.getIdentifier(), oe.getExtension());
                }
            }
        } finally {
            oeLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteOfferingExtension(final String offering, final String identifier) throws
            NoSuchOfferingException, NoSuchExtensionException {
        checkOffering(offering);
        throwingExecute(new DeleteOfferingExtensionAction(offering, identifier));
        oeLock.writeLock().lock();
        try {
            if (cachedOe != null && cachedOe.get(offering) != null) {
            	if (cachedOe.get(offering).remove(identifier)!=null) {
            		LOGGER.debug("Removed extension '{}' for offering '{}' from offering extension cache.",identifier,offering);
            	} else {
            		LOGGER.debug("Removing failed for extension '{}' for offering '{}' from offering extension cache.",identifier,offering);
            	}
            }
        } finally {
            oeLock.writeLock().unlock();
        }
}

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions() {
        boolean load = false;
        ceLock.readLock().lock();
        try {
            if (cachedCe == null) {
                load = true;
            }
        } finally {
            ceLock.readLock().unlock();
        }
        ceLock.writeLock().lock();
        try {
            if (load) {
                if (cachedCe == null) {
                    final Map<String, StringBasedCapabilitiesExtension> ext =
                                                                  execute(new GetActiveCapabilitiesExtensionAction());
                    cachedCe = new HashMap<>(ext.size());
                    for (final Entry<String, StringBasedCapabilitiesExtension> e : ext.entrySet()) {
                        cachedCe.put(e.getKey(), new CapabilitiesExtensionImpl(
                                e.getValue().getSectionName(), e.getValue().getExtension()));
                    }
                }
            }
        } finally {
            ceLock.writeLock().unlock();
        }
        return Collections.unmodifiableMap(cachedCe);
    }

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions() {
        return execute(new GetAllCapabilitiesExtensionAction());
    }

    @Override
    public void saveCapabilitiesExtension(final String identifier, final String value) {
        execute(new SaveCapabilitesExtensionAction(identifier, value));
        ceLock.writeLock().lock();
        try {
            if (cachedCe != null) {
                cachedCe.put(identifier, new CapabilitiesExtensionImpl(identifier, value));
            }
        } finally {
            ceLock.writeLock().unlock();
        }
    }

    @Override
    public void disableCapabilitiesExtension(final String identifier, final boolean disabled) throws
            NoSuchExtensionException {
        throwingExecute(new DisableCapabiliesExtensionAction(identifier, disabled));
        ceLock.writeLock().lock();
        try {
            if (cachedCe != null) {
                if (disabled) {
                    cachedCe.remove(identifier);
                } else {
                    cachedCe.put(identifier, execute(new GetCapabilitiesExtensionAction(identifier)));
                }
            }
        } finally {
            ceLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteCapabiltiesExtension(final String identifier) throws NoSuchExtensionException {
        throwingExecute(new DeleteCapabilitiesExtensionAction(identifier));
        ceLock.writeLock().lock();
        try {
            if (cachedCe != null) {
                cachedCe.remove(identifier);
            }
        } finally {
            ceLock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, StaticCapabilities> getStaticCapabilities() {
        return execute(new GetStaticCapabilitiesAction());
    }

    @Override
    public void saveStaticCapabilities(final String identifier, final String document) {
        execute(new SaveStaticCapabilitiesAction(identifier, document));
        scLock.writeLock().lock();
        try {
            if (cachedScId != null
                && cachedScId.equals(identifier)) {
                cachedSc = document;
            }
        } finally {
            scLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteStaticCapabilities(final String identifier) throws NoSuchExtensionException {
        throwingExecute(new DeleteStaticCapabilitiesAction(identifier));
        scLock.writeLock().lock();
        try {
            if (cachedScId != null
                && cachedScId.equals(identifier)) {
                cachedScId = null;
                cachedSc = null;
            }
        } finally {
            scLock.writeLock().unlock();
        }
    }

    @Override
    public StaticCapabilities getStaticCapabilities(final String id) {
        scLock.readLock().lock();
        try {
            if (cachedScId != null
                && cachedScId.equals(id)
                && cachedSc != null) {
                return new StaticCapabilitiesImpl(id, cachedSc);
            }
        } finally {
            scLock.readLock().unlock();
        }
        return execute(new GetStaticCapabilitiesWithIdAction(id));
    }

    private class SetActiveStaticCapabilitiesAction extends ThrowingVoidHibernateAction {
        private final String identifier;

        SetActiveStaticCapabilitiesAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final StaticCapabilitiesImpl sci = (StaticCapabilitiesImpl) session
                    .get(StaticCapabilitiesImpl.class, identifier);
            if (sci == null) {
                throw new NoSuchExtensionException(identifier);
            }
            if (!sci.isActive()) {
                final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                        .createCriteria(StaticCapabilitiesImpl.class)
                        .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                        .uniqueResult();
                if (cur != null) {
                    session.update(cur.setActive(false));
                }
                session.update(sci.setActive(true));
            }
        }
    }

    private class GetActiveStaticCapabilitiesAction implements HibernateAction<String> {
        @Override
        public String call(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            return cur == null ? null : cur.getIdentifier();
        }
    }

    private class GetActiveStaticCapabilitiesDocumentAction implements HibernateAction<String> {
        @Override
        public String call(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            return cur == null ? null : cur.getDocument();
        }
    }

    private class GetOfferingExtensionsAction implements HibernateAction<ListMultiMap<String, OfferingExtension>> {
        @Override
        public ListMultiMap<String, OfferingExtension> call(final Session session) {
            @SuppressWarnings("unchecked")
			final
            List<OfferingExtensionImpl> extensions = session.createCriteria(OfferingExtensionImpl.class).list();
            final ListMultiMap<String, OfferingExtension> map = new LinkedListMultiMap<>(
                    getCache().getOfferings().size());

            for (final OfferingExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded OfferingExtension: {}", extension);
                map.add(extension.getOfferingName(), extension);
            }
            return map;
        }
    }

    private class GetOfferingExtensionAction implements HibernateAction<OfferingExtension> {
        private final String offering;
        private final String identifier;

        GetOfferingExtensionAction(final String offering, final String identifier) {
            this.offering = offering;
            this.identifier = identifier;
        }

        @Override
        public OfferingExtension call(final Session session) {
            return (OfferingExtension) session.createCriteria(OfferingExtensionImpl.class)
                    .add(and(eq(Activatable.COMPOSITE_KEY + "." + OfferingExtensionIdentifier.IDENTIFIER, identifier),
                             eq(Activatable.COMPOSITE_KEY + "." + OfferingExtensionIdentifier.OFFERING, offering)))
                    .uniqueResult();
        }
    }

    private class GetActiveOfferingExtensionsAction implements HibernateAction<ListMultiMap<String, OfferingExtension>> {
        @Override
        public ListMultiMap<String, OfferingExtension> call(final Session session) {
            @SuppressWarnings("unchecked")
			final
            List<OfferingExtensionImpl> extensions = session.createCriteria(OfferingExtensionImpl.class)
                    .add(eq(OfferingExtensionImpl.ACTIVE, true)).list();
            final ListMultiMap<String, OfferingExtension> map = new LinkedListMultiMap<>(
                    getCache().getOfferings().size());

            for (final OfferingExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded OfferingExtension: {}", extension);
                map.add(extension.getOfferingName(), extension);
            }
            return map;
        }
    }

    private class SaveOfferingExtensionAction extends VoidHibernateAction {
        private final String offering;
        private final String identifier;
        private final String value;

        SaveOfferingExtensionAction(final String offering, final String identifier, final String value) {
            this.offering = offering;
            this.identifier = identifier;
            this.value = value;
        }

        @Override
        protected void run(final Session session) {
            session.saveOrUpdate(new OfferingExtensionImpl(offering, identifier, value));
        }
    }

    private class SetActiveOfferingExtensionAction extends ThrowingVoidHibernateAction {
        private final String offering;
        private final String identifier;
        private final boolean disabled;

        SetActiveOfferingExtensionAction(final String offering, final String identifier, final boolean disabled) {
            this.offering = offering;
            this.identifier = identifier;
            this.disabled = disabled;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final OfferingExtensionImpl oe = (OfferingExtensionImpl) session
                    .get(OfferingExtensionImpl.class, new OfferingExtensionIdentifier(offering, identifier));
            if (oe == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.update(oe.setActive(!disabled));
        }
    }

    private class DeleteOfferingExtensionAction extends ThrowingVoidHibernateAction {
        private final String offering;
        private final String identifier;

        DeleteOfferingExtensionAction(final String offering, final String identifier) {
            this.offering = offering;
            this.identifier = identifier;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final OfferingExtensionImpl oe = (OfferingExtensionImpl) session
                    .get(OfferingExtensionImpl.class, new OfferingExtensionIdentifier(offering, identifier));
            if (oe == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(oe);
        }
    }

    private class GetActiveCapabilitiesExtensionAction implements HibernateAction<Map<String, StringBasedCapabilitiesExtension>> {
        @Override
        public Map<String, StringBasedCapabilitiesExtension> call(final Session session) {
            @SuppressWarnings("unchecked")
			final
            List<CapabilitiesExtensionImpl> extensions = session
                    .createCriteria(CapabilitiesExtensionImpl.class)
                    .add(eq(CapabilitiesExtensionImpl.ACTIVE, true))
                    .list();
            final HashMap<String, StringBasedCapabilitiesExtension> map =
                                                              new HashMap<>(extensions
                    .size());
            for (final CapabilitiesExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded CapabiltiesExtension: {}", extension);
                map.put(extension.getKey(), extension);
            }
            return map;
        }
    }

    private class GetAllCapabilitiesExtensionAction implements HibernateAction<Map<String, StringBasedCapabilitiesExtension>> {
        @Override
        public Map<String, StringBasedCapabilitiesExtension> call(final Session session) {
            @SuppressWarnings("unchecked")
			final
            List<CapabilitiesExtensionImpl> extensions = session
                    .createCriteria(CapabilitiesExtensionImpl.class).list();
            final HashMap<String, StringBasedCapabilitiesExtension> map =
                                                              new HashMap<>(extensions
                    .size());
            for (final CapabilitiesExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded CapabiltiesExtension: {}", extension);
                map.put(extension.getKey(), extension);
            }
            return map;
        }
    }

    private class GetCapabilitiesExtensionAction implements HibernateAction<CapabilitiesExtensionImpl> {
        private final String identifier;

        GetCapabilitiesExtensionAction(final String identifier) {
            this.identifier = identifier;
        }
        @Override
        public CapabilitiesExtensionImpl call(final Session session) {
            return (CapabilitiesExtensionImpl) session.get(CapabilitiesExtensionImpl.class, identifier);
        }
    }

    private class SaveCapabilitesExtensionAction extends VoidHibernateAction {
        private final String identifier;
        private final String value;

        SaveCapabilitesExtensionAction(final String identifier, final String value) {
            this.identifier = identifier;
            this.value = value;
        }

        @Override
        protected void run(final Session session) {
            session.saveOrUpdate(new CapabilitiesExtensionImpl(identifier, value));
        }
    }

    private class DisableCapabiliesExtensionAction extends ThrowingVoidHibernateAction {
        private final String identifier;
        private final boolean disabled;

        DisableCapabiliesExtensionAction(final String identifier, final boolean disabled) {
            this.identifier = identifier;
            this.disabled = disabled;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final CapabilitiesExtensionImpl ce = (CapabilitiesExtensionImpl) session
                    .get(CapabilitiesExtensionImpl.class, identifier);

            if (ce == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.update(ce.setActive(!disabled));
        }
    }

    private class DeleteCapabilitiesExtensionAction extends ThrowingVoidHibernateAction {
        private final String identifier;

        DeleteCapabilitiesExtensionAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final CapabilitiesExtensionImpl ce = (CapabilitiesExtensionImpl) session
                    .get(CapabilitiesExtensionImpl.class, identifier);
            if (ce == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(ce);
        }
    }

    private class GetStaticCapabilitiesAction implements HibernateAction<Map<String, StaticCapabilities>> {
        @Override
        public Map<String, StaticCapabilities> call(final Session session) {
            @SuppressWarnings("unchecked")
			final
            List<StaticCapabilitiesImpl> scs = session.createCriteria(StaticCapabilitiesImpl.class).list();
            final HashMap<String, StaticCapabilities> map = new HashMap<>(scs.size());
            for (final StaticCapabilitiesImpl sc : scs) {
                LOGGER.debug("Loaded StaticCapabilities: {}", sc);
                map.put(sc.getIdentifier(), sc);
            }
            return map;
        }
    }

    private class SaveStaticCapabilitiesAction extends VoidHibernateAction {
        private final String identifier;
        private final String document;

        SaveStaticCapabilitiesAction(final String identifier, final String document) {
            this.identifier = identifier;
            this.document = document;
        }

        @Override
        protected void run(final Session session) {
            session.saveOrUpdate(new StaticCapabilitiesImpl(identifier, document));
        }
    }

    private class DeleteStaticCapabilitiesAction extends ThrowingVoidHibernateAction {
        private final String identifier;

        DeleteStaticCapabilitiesAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        protected void run(final Session session) throws NoSuchExtensionException {
            final StaticCapabilitiesImpl sc = (StaticCapabilitiesImpl) session
                    .get(StaticCapabilitiesImpl.class, identifier);
            if (sc == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(sc);
        }
    }

    private class DeactivateStaticCapabilitiesAction extends VoidHibernateAction {
        @Override
        protected void run(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            if (cur != null) {
                session.update(cur.setActive(false));
            }
        }
    }

    private class GetStaticCapabilitiesWithIdAction implements HibernateAction<StaticCapabilities> {
        private final String id;

        GetStaticCapabilitiesWithIdAction(final String id) {
            this.id = id;
        }

        @Override
        public StaticCapabilities call(final Session session) {
            return (StaticCapabilitiesImpl) session.get(StaticCapabilitiesImpl.class, id);
        }
    }
}

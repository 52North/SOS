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
package org.n52.sos.config.sqlite;

import static java.util.stream.Collectors.toMap;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.ogc.ows.extension.StaticCapabilities;
import org.n52.iceland.util.collections.LinkedListMultiMap;
import org.n52.iceland.util.collections.ListMultiMap;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.function.ThrowingConsumer;
import org.n52.janmayen.function.ThrowingFunction;
import org.n52.shetland.ogc.ows.extension.StringBasedCapabilitiesExtension;
import org.n52.shetland.ogc.sos.extension.SosObservationOfferingExtension;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.config.sqlite.entities.Activatable;
import org.n52.sos.config.sqlite.entities.CapabilitiesExtensionImpl;
import org.n52.sos.config.sqlite.entities.OfferingExtensionIdentifier;
import org.n52.sos.config.sqlite.entities.OfferingExtensionImpl;
import org.n52.sos.config.sqlite.entities.StaticCapabilitiesImpl;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SQLiteCapabilitiesExtensionService
        extends AbstractSQLiteDao
        implements CapabilitiesExtensionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteCapabilitiesExtensionService.class);
    private final ReadWriteLock scLock = new ReentrantReadWriteLock();
    private final ReadWriteLock ceLock = new ReentrantReadWriteLock();
    private final ReadWriteLock oeLock = new ReentrantReadWriteLock();
    private String cachedScId;
    private String cachedSc;
    private Map<String, StringBasedCapabilitiesExtension> cachedCe;
    private Map<String, Map<String, String>> cachedOe;

    private ContentCacheController contentCacheController;

    @Inject
    public void setContentCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    protected SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

    private void checkOffering(final String offering) throws NoSuchOfferingException {
        if (!getCache().hasOffering(offering)) {
            throw new NoSuchOfferingException(offering);
        }
    }

    @Override
    protected <T> T throwingExecute(ThrowingFunction<Session, T, ? extends Exception> action) throws NoSuchExtensionException {
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
    protected void throwingExecute(ThrowingConsumer<Session, ? extends Exception> action) throws NoSuchExtensionException {
        try {
            super.throwingExecute(action);
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
    public ListMultiMap<String, SosObservationOfferingExtension> getOfferingExtensions() {
        return execute(new GetOfferingExtensionsAction());
    }

    @Override
    public ListMultiMap<String, SosObservationOfferingExtension> getActiveOfferingExtensions() {
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
                    final ListMultiMap<String, SosObservationOfferingExtension> execute
                            = execute(new GetActiveOfferingExtensionsAction());
                    cachedOe = new HashMap<>(execute.size());

                    for (final String offering : execute.keySet()) {
                        cachedOe.put(offering, execute.get(offering).stream()
                                     .collect(toMap(SosObservationOfferingExtension::getIdentifier,
                                                    SosObservationOfferingExtension::getExtension)));
                    }
                }
            } finally {
                oeLock.writeLock().unlock();
            }
        }
        final ListMultiMap<String, SosObservationOfferingExtension> map = new LinkedListMultiMap<>();
        for (String offering : cachedOe.keySet()) {
            Map<String, String> oes = cachedOe.get(offering);
            if (oes != null) {
                for (Entry<String, String> oe : oes.entrySet()) {
                    map.add(offering, new OfferingExtensionImpl(offering, oe.getKey(), oe.getValue()));
                }
            }
        }
        return map;
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
                Map<String, String> forOffering = cachedOe.computeIfAbsent(offering, Functions.forSupplier(HashMap::new));
                if (disabled) {
                    forOffering.remove(identifier);
                } else if (!forOffering.containsKey(identifier)) {
                    SosObservationOfferingExtension oe = execute(new GetOfferingExtensionAction(offering, identifier));
                    forOffering.put(oe.getIdentifier(), oe.getExtension());
                }
            }
        } finally {
            oeLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteOfferingExtension(final String offering, final String identifier) throws NoSuchOfferingException,
                                                                                               NoSuchExtensionException {
        checkOffering(offering);
        throwingExecute(new DeleteOfferingExtensionAction(offering, identifier));
        oeLock.writeLock().lock();
        try {
            if (cachedOe != null && cachedOe.get(offering) != null) {
                if (cachedOe.get(offering).remove(identifier) != null) {
                    LOGGER.debug("Removed extension '{}' for offering '{}' from offering extension cache.", identifier, offering);
                } else {
                    LOGGER.debug("Removing failed for extension '{}' for offering '{}' from offering extension cache.", identifier, offering);
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
                    final Map<String, StringBasedCapabilitiesExtension> ext
                            = execute(new GetActiveCapabilitiesExtensionAction());
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
            if (cachedScId != null &&
                     cachedScId.equals(identifier)) {
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
            if (cachedScId != null &&
                     cachedScId.equals(identifier)) {
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
            if (cachedScId != null && cachedScId.equals(id) && cachedSc != null) {
                return new StaticCapabilitiesImpl(id, cachedSc);
            }
        } finally {
            scLock.readLock().unlock();
        }
        return execute(new GetStaticCapabilitiesWithIdAction(id));
    }

    private class SetActiveStaticCapabilitiesAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String identifier;

        SetActiveStaticCapabilitiesAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
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

    private class GetActiveStaticCapabilitiesAction implements Function<Session, String> {
        @Override
        public String apply(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            return cur == null ? null : cur.getIdentifier();
        }
    }

    private class GetActiveStaticCapabilitiesDocumentAction implements Function<Session, String> {
        @Override
        public String apply(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            return cur == null ? null : cur.getDocument();
        }
    }

    private class GetOfferingExtensionsAction implements Function<Session, ListMultiMap<String, SosObservationOfferingExtension>> {
        @Override
        public ListMultiMap<String, SosObservationOfferingExtension> apply(final Session session) {
            @SuppressWarnings("unchecked")
            final List<OfferingExtensionImpl> extensions = session.createCriteria(OfferingExtensionImpl.class).list();
            final ListMultiMap<String, SosObservationOfferingExtension> map = new LinkedListMultiMap<>(
                    getCache().getOfferings().size());

            for (final OfferingExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded OfferingExtension: {}", extension);
                map.add(extension.getOfferingName(), extension);
            }
            return map;
        }
    }

    private class GetOfferingExtensionAction implements Function<Session, SosObservationOfferingExtension> {
        private final String offering;
        private final String identifier;

        GetOfferingExtensionAction(final String offering, final String identifier) {
            this.offering = offering;
            this.identifier = identifier;
        }

        @Override
        public SosObservationOfferingExtension apply(final Session session) {
            return (SosObservationOfferingExtension) session.createCriteria(OfferingExtensionImpl.class)
                    .add(and(eq(Activatable.COMPOSITE_KEY + "." + OfferingExtensionIdentifier.IDENTIFIER, identifier),
                             eq(Activatable.COMPOSITE_KEY + "." + OfferingExtensionIdentifier.OFFERING, offering)))
                    .uniqueResult();
        }
    }

    private class GetActiveOfferingExtensionsAction implements Function<Session, ListMultiMap<String, SosObservationOfferingExtension>> {
        @Override
        public ListMultiMap<String, SosObservationOfferingExtension> apply(final Session session) {
            @SuppressWarnings("unchecked")
            final List<OfferingExtensionImpl> extensions = session.createCriteria(OfferingExtensionImpl.class)
                    .add(eq(OfferingExtensionImpl.ACTIVE, true)).list();
            final ListMultiMap<String, SosObservationOfferingExtension> map = new LinkedListMultiMap<>(
                    getCache().getOfferings().size());

            for (final OfferingExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded OfferingExtension: {}", extension);
                map.add(extension.getOfferingName(), extension);
            }
            return map;
        }
    }

    private class SaveOfferingExtensionAction implements Consumer<Session> {
        private final String offering;
        private final String identifier;
        private final String value;

        SaveOfferingExtensionAction(final String offering, final String identifier, final String value) {
            this.offering = offering;
            this.identifier = identifier;
            this.value = value;
        }

        @Override
        public void accept(final Session session) {
            session.saveOrUpdate(new OfferingExtensionImpl(offering, identifier, value));
        }
    }

    private class SetActiveOfferingExtensionAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String offering;
        private final String identifier;
        private final boolean disabled;

        SetActiveOfferingExtensionAction(final String offering, final String identifier, final boolean disabled) {
            this.offering = offering;
            this.identifier = identifier;
            this.disabled = disabled;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
            final OfferingExtensionImpl oe = (OfferingExtensionImpl) session
                    .get(OfferingExtensionImpl.class, new OfferingExtensionIdentifier(offering, identifier));
            if (oe == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.update(oe.setActive(!disabled));
        }
    }

    private class DeleteOfferingExtensionAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String offering;
        private final String identifier;

        DeleteOfferingExtensionAction(final String offering, final String identifier) {
            this.offering = offering;
            this.identifier = identifier;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
            final OfferingExtensionImpl oe = (OfferingExtensionImpl) session
                    .get(OfferingExtensionImpl.class, new OfferingExtensionIdentifier(offering, identifier));
            if (oe == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(oe);
        }
    }

    private class GetActiveCapabilitiesExtensionAction implements Function<Session, Map<String, StringBasedCapabilitiesExtension>> {
        @Override
        public Map<String, StringBasedCapabilitiesExtension> apply(final Session session) {
            @SuppressWarnings("unchecked")
            final List<CapabilitiesExtensionImpl> extensions = session
                    .createCriteria(CapabilitiesExtensionImpl.class)
                    .add(eq(CapabilitiesExtensionImpl.ACTIVE, true))
                    .list();
            final HashMap<String, StringBasedCapabilitiesExtension> map = new HashMap<>(extensions
                    .size());
            for (final CapabilitiesExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded CapabiltiesExtension: {}", extension);
                map.put(extension.getKey(), extension);
            }
            return map;
        }
    }

    private class GetAllCapabilitiesExtensionAction implements Function<Session, Map<String, StringBasedCapabilitiesExtension>> {
        @Override
        public Map<String, StringBasedCapabilitiesExtension> apply(final Session session) {
            @SuppressWarnings("unchecked")
            final List<CapabilitiesExtensionImpl> extensions = session
                    .createCriteria(CapabilitiesExtensionImpl.class).list();
            final HashMap<String, StringBasedCapabilitiesExtension> map = new HashMap<>(extensions
                    .size());
            for (final CapabilitiesExtensionImpl extension : extensions) {
                LOGGER.debug("Loaded CapabiltiesExtension: {}", extension);
                map.put(extension.getKey(), extension);
            }
            return map;
        }
    }

    private class GetCapabilitiesExtensionAction implements Function<Session, CapabilitiesExtensionImpl> {
        private final String identifier;

        GetCapabilitiesExtensionAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public CapabilitiesExtensionImpl apply(final Session session) {
            return (CapabilitiesExtensionImpl) session.get(CapabilitiesExtensionImpl.class, identifier);
        }
    }

    private class SaveCapabilitesExtensionAction implements Consumer<Session> {
        private final String identifier;
        private final String value;

        SaveCapabilitesExtensionAction(final String identifier, final String value) {
            this.identifier = identifier;
            this.value = value;
        }

        @Override
        public void accept(final Session session) {
            session.saveOrUpdate(new CapabilitiesExtensionImpl(identifier, value));
        }
    }

    private class DisableCapabiliesExtensionAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String identifier;
        private final boolean disabled;

        DisableCapabiliesExtensionAction(final String identifier, final boolean disabled) {
            this.identifier = identifier;
            this.disabled = disabled;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
            final CapabilitiesExtensionImpl ce = (CapabilitiesExtensionImpl) session
                    .get(CapabilitiesExtensionImpl.class, identifier);

            if (ce == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.update(ce.setActive(!disabled));
        }
    }

    private class DeleteCapabilitiesExtensionAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String identifier;

        DeleteCapabilitiesExtensionAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
            final CapabilitiesExtensionImpl ce = (CapabilitiesExtensionImpl) session
                    .get(CapabilitiesExtensionImpl.class, identifier);
            if (ce == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(ce);
        }
    }

    private class GetStaticCapabilitiesAction implements Function<Session, Map<String, StaticCapabilities>> {
        @Override
        public Map<String, StaticCapabilities> apply(final Session session) {
            @SuppressWarnings("unchecked")
            final List<StaticCapabilitiesImpl> scs = session.createCriteria(StaticCapabilitiesImpl.class).list();
            final HashMap<String, StaticCapabilities> map = new HashMap<>(scs.size());
            for (final StaticCapabilitiesImpl sc : scs) {
                LOGGER.debug("Loaded StaticCapabilities: {}", sc);
                map.put(sc.getIdentifier(), sc);
            }
            return map;
        }
    }

    private class SaveStaticCapabilitiesAction implements Consumer<Session> {
        private final String identifier;
        private final String document;

        SaveStaticCapabilitiesAction(final String identifier, final String document) {
            this.identifier = identifier;
            this.document = document;
        }

        @Override
        public void accept(final Session session) {
            session.saveOrUpdate(new StaticCapabilitiesImpl(identifier, document));
        }
    }

    private class DeleteStaticCapabilitiesAction implements ThrowingConsumer<Session, NoSuchExtensionException> {
        private final String identifier;

        DeleteStaticCapabilitiesAction(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void accept(final Session session) throws NoSuchExtensionException {
            final StaticCapabilitiesImpl sc = (StaticCapabilitiesImpl) session
                    .get(StaticCapabilitiesImpl.class, identifier);
            if (sc == null) {
                throw new NoSuchExtensionException(identifier);
            }
            session.delete(sc);
        }
    }

    private class DeactivateStaticCapabilitiesAction implements Consumer<Session> {
        @Override
        public void accept(final Session session) {
            final StaticCapabilitiesImpl cur = (StaticCapabilitiesImpl) session
                    .createCriteria(StaticCapabilitiesImpl.class)
                    .add(eq(StaticCapabilitiesImpl.ACTIVE, true))
                    .uniqueResult();
            if (cur != null) {
                session.update(cur.setActive(false));
            }
        }
    }

    private class GetStaticCapabilitiesWithIdAction implements Function<Session, StaticCapabilities> {
        private final String id;

        GetStaticCapabilitiesWithIdAction(final String id) {
            this.id = id;
        }

        @Override
        public StaticCapabilities apply(final Session session) {
            return (StaticCapabilitiesImpl) session.get(StaticCapabilitiesImpl.class, id);
        }
    }
}

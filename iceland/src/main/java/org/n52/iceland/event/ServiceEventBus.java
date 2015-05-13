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
package org.n52.iceland.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.iceland.util.ClassHelper;
import org.n52.iceland.util.GroupedAndNamedThreadFactory;
import org.n52.iceland.util.MultiMaps;
import org.n52.iceland.util.SetMultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ServiceEventBus {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEventBus.class);

    private static final boolean ASYNCHRONOUS_EXECUTION = false;

    private static final int THREAD_POOL_SIZE = 3;

    private static final String THREAD_GROUP_NAME = "SosEventBus-Worker";

    public static ServiceEventBus getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void fire(final ServiceEvent event) {
        getInstance().submit(event);
    }

    private static boolean checkEvent(final ServiceEvent event) {
        if (event == null) {
            LOG.warn("Submitted event is null!");
            return false;
        }
        return true;
    }

    private static boolean checkListener(final ServiceEventListener listener) {
        if (listener == null) {
            LOG.warn("Tried to unregister SosEventListener null");
            return false;
        }
        if (listener.getTypes() == null || listener.getTypes().isEmpty()) {
            LOG.warn("Listener {} has no EventTypes", listener);
            return false;
        }
        return true;
    }

    private final ClassCache classCache = new ClassCache();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new GroupedAndNamedThreadFactory(
            THREAD_GROUP_NAME));

    private final SetMultiMap<Class<? extends ServiceEvent>, ServiceEventListener> listeners = MultiMaps.newSetMultiMap();

    private final Queue<HandlerExecution> queue = new ConcurrentLinkedQueue<HandlerExecution>();

    private ServiceEventBus() {
        loadListenerImplementations();
    }

    private void loadListenerImplementations() {
        final ServiceLoader<ServiceEventListener> serviceLoader = ServiceLoader.load(ServiceEventListener.class);
        final Iterator<ServiceEventListener> iter = serviceLoader.iterator();
        while (iter.hasNext()) {
            try {
                register(iter.next());
            } catch (final ServiceConfigurationError e) {
                LOG.error("Could not load Listener implementation", e);
            }
        }
    }

    private Set<ServiceEventListener> getListenersForEvent(final ServiceEvent event) {
        final LinkedList<ServiceEventListener> result = new LinkedList<ServiceEventListener>();
        lock.readLock().lock();
        try {
            for (final Class<? extends ServiceEvent> eventType : classCache.getClasses(event.getClass())) {
                final Set<ServiceEventListener> listenersForClass = listeners.get(eventType);

                if (listenersForClass != null) {
                    LOG.trace("Adding {} Listeners for event {} (eventType={})", listenersForClass.size(), event,
                            eventType);
                    result.addAll(listenersForClass);
                } else {
                    LOG.trace("Adding 0 Listeners for event {} (eventType={})", event, eventType);
                }

            }
        } finally {
            lock.readLock().unlock();
        }
        return new HashSet<ServiceEventListener>(result);
    }

    public void submit(final ServiceEvent event) {
        boolean submittedEvent = false;
        if (!checkEvent(event)) {
            return;
        }
        lock.readLock().lock();
        try {
            for (final ServiceEventListener listener : getListenersForEvent(event)) {
                submittedEvent = true;
                LOG.debug("Queueing Event {} for Listener {}", event, listener);
                queue.offer(new HandlerExecution(event, listener));
            }
        } finally {
            lock.readLock().unlock();
        }
        HandlerExecution r;
        while ((r = queue.poll()) != null) {
            if (ASYNCHRONOUS_EXECUTION) {
                executor.execute(r);
            } else {
                r.run();
            }
        }
        if (!submittedEvent) {
            LOG.debug("No Listeners for SosEvent {}", event);
        }
    }

    public void register(final ServiceEventListener listener) {
        if (!checkListener(listener)) {
            return;
        }
        lock.writeLock().lock();
        try {
            for (final Class<? extends ServiceEvent> eventType : listener.getTypes()) {
                LOG.debug("Subscibing Listener {} to EventType {}", listener, eventType);
                listeners.add(eventType, listener);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregister(final ServiceEventListener listener) {
        if (!checkListener(listener)) {
            return;
        }
        lock.writeLock().lock();
        try {
            for (final Class<? extends ServiceEvent> eventType : listener.getTypes()) {
                final Set<ServiceEventListener> listenersForKey = listeners.get(eventType);
                if (listenersForKey.contains(listener)) {
                    LOG.debug("Unsubscibing Listener {} from EventType {}", listener, eventType);
                    listenersForKey.remove(listener);
                } else {
                    LOG.warn("Listener {} was not registered for SosEvent Type {}", listener, eventType);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static class LazyHolder {
        private static final ServiceEventBus INSTANCE = new ServiceEventBus();

        private LazyHolder() {}
    }

    private class ClassCache {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        private final SetMultiMap<Class<? extends ServiceEvent>, Class<? extends ServiceEvent>> cache = MultiMaps.newSetMultiMap();

        public Set<Class<? extends ServiceEvent>> getClasses(final Class<? extends ServiceEvent> eventClass) {
            lock.readLock().lock();
            try {
                final Set<Class<? extends ServiceEvent>> r = cache.get(eventClass);
                if (r != null) {
                    return r;
                }
            } finally {
                lock.readLock().unlock();
            }
            lock.writeLock().lock();
            try {
                Set<Class<? extends ServiceEvent>> r = cache.get(eventClass);
                if (r != null) {
                    return r;
                }
                r = flatten(eventClass);
                cache.put(eventClass, r);
                return r;
            } finally {
                lock.writeLock().unlock();
            }
        }

        private Set<Class<? extends ServiceEvent>> flatten(final Class<? extends ServiceEvent> eventClass) {
            return ClassHelper.flattenPartialHierachy(ServiceEvent.class, eventClass);
        }
    }

    private class HandlerExecution implements Runnable {
        private final ServiceEvent event;

        private final ServiceEventListener listener;

        HandlerExecution(final ServiceEvent event, final ServiceEventListener listener) {
            this.event = event;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                LOG.debug("Submitting Event {} to Listener {}", event, listener);
                listener.handle(event);
            } catch (final Throwable t) {
                LOG.error(String.format("Error handling event %s by handler %s", event, listener), t);
            }
        }
    }
}

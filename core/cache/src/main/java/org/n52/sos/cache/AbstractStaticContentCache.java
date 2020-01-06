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
package org.n52.sos.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Readable content cache that gets static information from the configurator (or
 * its delegates).
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractStaticContentCache implements ContentCache {
    private static final long serialVersionUID = -3494345412582194615L;

    @Override
    public Set<String> getObservationTypes() {
        return CodingRepository.getInstance().getObservationTypes();
    }

    @Override
    public Set<String> getFeatureOfInterestTypes() {
        return CodingRepository.getInstance().getFeatureOfInterestTypes();
    }

    /**
     * Creates a new synchronized map from the specified map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap(Map<K, V> map) {
        if (map == null) {
            return CollectionHelper.synchronizedMap(0);
        } else {
            return Collections.synchronizedMap(new HashMap<>(map));
        }
    }

    /**
     * Creates a new synchronized set from the specified elements.
     *
     * @param <T>
     *                 the element type
     * @param elements
     *                 the elements
     *
     * @return the synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet(
            Iterable<T> elements) {
        if (elements == null) {
            return CollectionHelper.synchronizedSet(0);
        } else {
            if (elements instanceof Collection) {
                return Collections
                        .synchronizedSet(new HashSet<>((Collection<T>) elements));
            } else {
                HashSet<T> hashSet = new HashSet<>();
                for (T t : elements) {
                    hashSet.add(t);
                }
                return Collections.synchronizedSet(hashSet);
            }
        }
    }

    /**
     * Creates a new empty synchronized map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap() {
        return newSynchronizedMap(null);
    }

    /**
     * Creates a new empty synchronized set.
     *
     * @param <T>
     *            the element type
     *
     * @return a synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet() {
        return newSynchronizedSet(null);
    }

    /**
     * Creates a unmodifiable copy of the specified set.
     *
     * @param <T>
     *            the element type
     * @param set
     *            the set
     *
     * @return a unmodifiable copy
     */
    protected static <T> Set<T> copyOf(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(new HashSet<>(set));
        }
    }
    
    /**
     * Creates a unmodifiable copy of the specified collection of sets.
     *
     * @param <T>
     *            the element type
     * @param set
     *            the set
     *
     * @return a unmodifiable copy
     */
    protected static <T> Set<T> copyOf(Collection<Set<T>> set) {
        if (set == null) {
            return Collections.emptySet();
        } else {
            HashSet<T> newHashSet = Sets.newHashSet();
            Iterator<Set<T>> iterator = set.iterator();
            while (iterator.hasNext()) {
                newHashSet.addAll((Set<T>) iterator.next());
            }
            return Collections.unmodifiableSet(newHashSet);
        }
    }

    /**
     * Creates a copy of the specified envelope.
     *
     * @param e
     *          the envelope
     *
     * @return a copy
     */
    protected static SosEnvelope copyOf(SosEnvelope e) {
        if (e == null) {
            // TODO empty envelope
            return null;
        } else {
            return new SosEnvelope(e.getEnvelope() == null ? null
                                   : new Envelope(e.getEnvelope()), e.getSrid()).setMinZ(e.getMinZ()).setMaxZ(e.getMaxZ());
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a
     * {@code IllegalArgumentException} if value is <= 0.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                                  if value is null
     * @throws IllegalArgumentException
     *                                  if value is <= 0
     */
    protected static void greaterZero(String name, Integer value)
            throws NullPointerException, IllegalArgumentException {
        notNull(name, value);
        if (value <= 0) {
            throw new IllegalArgumentException(name +
                                               " may not less or equal 0!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a
     * {@code IllegalArgumentException} if value is empty.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                                  if value is null
     * @throws IllegalArgumentException
     *                                  if value is empty
     */
    protected static void notNullOrEmpty(String name, String value)
            throws NullPointerException, IllegalArgumentException {
        notNull(name, value);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value
     * within is null.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                              if value == null or value contains null
     */
    protected static void noNullValues(String name,
                                       Collection<?> value)
            throws NullPointerException {
        notNull(name, value);
        for (Object o : value) {
            if (o == null) {
                throw new NullPointerException(name +
                                               " may not contain null elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value
     * within is null or empty.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                                  if value == null or value contains null
     * @throws IllegalArgumentException
     *                                  if any value is empty
     */
    protected static void noNullOrEmptyValues(String name,
                                              Collection<String> value)
            throws NullPointerException, IllegalArgumentException {
        notNull(name, value);
        for (String o : value) {
            if (o == null) {
                throw new NullPointerException(name +
                                               " may not contain null elements!");
            }
            if (o.isEmpty()) {
                throw new IllegalArgumentException(name +
                                                   " may not contain empty elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any key or
     * value within is null.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                              if value == null or value contains null values
     */
    protected static void noNullValues(String name,
                                       Map<?, ?> value)
            throws NullPointerException {
        notNull(name, value);
        for (Entry<?, ?> e : value.entrySet()) {
            if (e == null || e.getKey() == null || e.getValue() == null) {
                throw new NullPointerException(name +
                                               " may not contain null elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null.
     *
     * @param name
     *              the name of the value
     * @param value
     *              the value to check
     *
     * @throws NullPointerException
     *                              if value == null
     */
    protected static void notNull(String name, Object value)
            throws NullPointerException {
        if (value == null) {
            throw new NullPointerException(name + " may not be null!");
        }
    }

    /**
     * Creates a {@code TimePeriod} for the specified {@code ITime}.
     *
     * @param time
     *            the abstract time
     *
     * @return the period describing the abstract time
     */
    protected static TimePeriod toTimePeriod(final Time time) {
        if (time instanceof TimeInstant) {
            final DateTime instant = ((TimeInstant) time).getValue();
            return new TimePeriod(instant, instant);
        } else {
            return (TimePeriod) time;
        }
    }
    
    /**
     * Remove value from map or complete entry if values for key are empty
     * 
     * @param map
     *            Map to check
     * @param value
     *            Value to remove
     */
    protected static <K, V> void removeValue(Map<K, Set<String>> map, String value) {
        for (K key : map.keySet()) {
            if (map.get(key).contains(value)) {
                if (map.get(key).size() > 1) {
                    map.get(key).remove(value);
                } else {
                    map.remove(key);
                }
            }
        }
    }
}

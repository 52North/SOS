/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import static org.n52.sos.util.ClassHelper.getSimiliarity;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.ClassHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Comparables;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class ResponseWriterRepository extends AbstractConfiguringServiceLoaderRepository<ResponseWriter> {
	private static class LazyHolder {
		private static final ResponseWriterRepository INSTANCE = new ResponseWriterRepository();
		
		private LazyHolder() {};
	}


    private final Map<Class<?>, ResponseWriter<?>> writersByClass = CollectionHelper.synchronizedMap();

    private final Set<ResponseWriter<?>> writers = CollectionHelper.synchronizedSet();

    public ResponseWriterRepository() {
        super(ResponseWriter.class, false);
        load(false);
    }

    public static ResponseWriterRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    protected void processConfiguredImplementations(final Set<ResponseWriter> implementations) throws ConfigurationException {
        writersByClass.clear();
        writers.clear();
        for (final ResponseWriter<?> i : implementations) {
            writers.add(i);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseWriter<T> getWriter(final Class<? extends T> clazz) {
        if (!writersByClass.containsKey(clazz)) {
            final Set<ResponseWriter<?>> compatible = Sets.newHashSet();
            for (final ResponseWriter<?> w : writers) {
                if (ClassHelper.getSimiliarity(w.getType(), clazz) >= 0) {
                    compatible.add(w);
                }
            }
            writersByClass.put(clazz, chooseWriter(compatible, clazz));
        }
        return (ResponseWriter<T>) writersByClass.get(clazz);
    }

    private ResponseWriter<?> chooseWriter(final Set<ResponseWriter<?>> compatible, final Class<?> clazz) {
        return compatible.isEmpty() ? null : Collections.min(compatible, new ResponseWriterComparator(clazz));
    }

    private class ResponseWriterComparator implements Comparator<ResponseWriter<?>> {
        private final Class<?> clazz;

        ResponseWriterComparator(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public int compare(final ResponseWriter<?> o1, final ResponseWriter<?> o2) {
            return Comparables.compare(getSimiliarity(o1.getType(), clazz), getSimiliarity(o2.getType(), clazz));
        }
    }
}

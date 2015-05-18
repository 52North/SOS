/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.encode;

import static org.n52.iceland.util.ClassHelper.getSimiliarity;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.iceland.util.ClassHelper;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.Comparables;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @author CarstenHollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class ResponseWriterRepository extends AbstractConfiguringServiceLoaderRepository<ResponseWriterFactory> {
	private static class LazyHolder {
		private static final ResponseWriterRepository INSTANCE = new ResponseWriterRepository();
		
		private LazyHolder() {};
	}


    private final Map<Class<?>, ResponseWriterFactory<?,?>> writersByClass = CollectionHelper.synchronizedMap();

    private final Set<ResponseWriterFactory<?,?>> writers = CollectionHelper.synchronizedSet();

    public ResponseWriterRepository() {
        super(ResponseWriterFactory.class, false);
        load(false);
    }

    public static ResponseWriterRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    protected void processConfiguredImplementations(final Set<ResponseWriterFactory> implementations) throws ConfigurationException {
        writersByClass.clear();
        writers.clear();
        for (final ResponseWriterFactory<?,?> i : implementations) {
            writers.add(i);
        }
    }

    @SuppressWarnings("unchecked")
	public <T> ResponseWriter<T> getWriter(final Class<? extends T> clazz) {
        if (!writersByClass.containsKey(clazz)) {
            final Set<ResponseWriterFactory<?,?>> compatible = Sets.newHashSet();
            for (final ResponseWriterFactory<?,?> w : writers) {
                if (ClassHelper.getSimiliarity(w.getType(), clazz) >= 0) {
                    compatible.add(w);
                }
            }
            writersByClass.put(clazz, chooseWriter(compatible, clazz));
        }
        return (ResponseWriter<T>) writersByClass.get(clazz).getResponseWriter();
    }

    private ResponseWriterFactory<?,?> chooseWriter(final Set<ResponseWriterFactory<?,?>> compatible, final Class<?> clazz) {
        return compatible.isEmpty() ? null : Collections.min(compatible, new ResponseWriterFactoryComparator(clazz));
    }

    private class ResponseWriterFactoryComparator implements Comparator<ResponseWriterFactory<?,?>> {
        private final Class<?> clazz;

        ResponseWriterFactoryComparator(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public int compare(final ResponseWriterFactory<?,?> o1, final ResponseWriterFactory<?,?> o2) {
            return Comparables.compare(getSimiliarity(o1.getType(), clazz), getSimiliarity(o2.getType(), clazz));
        }
    }
}

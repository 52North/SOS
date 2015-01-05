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
package org.n52.sos.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;

import com.google.common.collect.Sets;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class ConverterRepository extends AbstractConfiguringServiceLoaderRepository<Converter> {

	private static class LazyHolder {
		private static final ConverterRepository INSTANCE = new ConverterRepository();
		
		private LazyHolder() {};
	}


    public static ConverterRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Map<ConverterKeyType, Converter<?, ?>> converter = new HashMap<ConverterKeyType, Converter<?, ?>>(0);

    private ConverterRepository() {
        super(Converter.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<Converter> converter) throws ConfigurationException {
        this.converter.clear();
        for (final Converter<?, ?> aConverter : converter) {
            for (final ConverterKeyType converterKeyType : aConverter.getConverterKeyTypes()) {
                this.converter.put(converterKeyType, aConverter);
            }
        }
        // TODO check for encoder/decoder used by converter
    }

    public <T, F> Converter<T, F> getConverter(final String fromNamespace, final String toNamespace) {
        return getConverter(new ConverterKeyType(fromNamespace, toNamespace));
    }

    @SuppressWarnings("unchecked")
    public <T, F> Converter<T, F> getConverter(final ConverterKeyType key) {
        return (Converter<T, F>) converter.get(key);
    }

    /**
     * Get all namespaces for which a converter is available to convert from
     * requested format to default format
     * 
     * @param toNamespace
     *            Requested format
     * @return Swt with all possible formats
     */
    public Set<String> getFromNamespaceConverterTo(final String toNamespace) {
        final Set<String> fromNamespaces = Sets.newHashSet();
        for (final ConverterKeyType converterKey : converter.keySet()) {
            if (toNamespace.equals(converterKey.getToNamespace())) {
                fromNamespaces.add(converterKey.getFromNamespace());
            }
        }
        return fromNamespaces;
    }

    /**
     * Checks if a converter is available to convert the stored object from the
     * default format to the requested format
     * 
     * @param fromNamespace
     *            Default format
     * @param toNamespace
     *            Requested fromat
     * @return If a converter is available
     */
    public boolean hasConverter(final String fromNamespace, final String toNamespace) {
        return getConverter(new ConverterKeyType(fromNamespace, toNamespace)) != null;
    }
}

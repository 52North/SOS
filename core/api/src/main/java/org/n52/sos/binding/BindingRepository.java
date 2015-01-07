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
package org.n52.sos.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Activatable;
import org.n52.sos.util.http.MediaType;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class BindingRepository extends AbstractConfiguringServiceLoaderRepository<Binding> {
    private static final Logger LOG = LoggerFactory.getLogger(BindingRepository.class);

    private static class Holder {
    	static BindingRepository INSTANCE = new BindingRepository();
    }

    public static BindingRepository getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Bindings by URL path.
     */
    private final Map<String, Activatable<Binding>> byPath = new HashMap<String, Activatable<Binding>>(0);
    /**
     * Bindings by Content-Type.
     */
    private final Map<MediaType, Activatable<Binding>> byMediaType = new HashMap<MediaType, Activatable<Binding>>(0);

    /**
     * reads the requestListeners from the configFile and returns a
     * RequestOperator containing the requestListeners
     *
     * @throws ConfigurationException
     *             if initialization of a RequestListener failed
     */
    private BindingRepository() throws ConfigurationException {
        super(Binding.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<Binding> bindings) throws ConfigurationException {
        this.byPath.clear();
        this.byMediaType.clear();
        final SettingsManager sm = SettingsManager.getInstance();
        try {
            for (final Binding binding : bindings) {
                boolean isActive = sm.isActive(new BindingKey(binding.getUrlPattern()));
                Activatable<Binding> activatable = Activatable.from(binding, isActive);
                this.byPath.put(binding.getUrlPattern(), activatable);
                if (binding.getSupportedEncodings() != null) {
                    for (MediaType mediaType :binding.getSupportedEncodings()) {
                        Activatable<Binding> previous
                                = this.byMediaType.put(mediaType, activatable);
                        if (previous != null) {
                            LOG.warn("{} is overwriting {} for MediaType {}",
                                     binding, previous.getInternal(), mediaType);
                        }
                    }
                }
            }
        } catch (final ConnectionProviderException ex) {
            throw new ConfigurationException("Could not check status of Binding", ex);
        }
        if (this.byPath.isEmpty()) {
            final StringBuilder exceptionText = new StringBuilder();
            exceptionText.append("No Binding implementation could be loaded! ");
            exceptionText.append("If the SOS is not used as webapp, this has no effect! ");
            exceptionText.append("Else add a Binding implementation!");
            LOG.warn(exceptionText.toString());
        }
    }

    public Binding getBinding(final String urlPattern) {
        final Activatable<Binding> binding = byPath.get(urlPattern);
        return binding == null ? null : binding.get();
    }

    public Binding getBinding(final MediaType mediaType) {
        final Activatable<Binding> binding = byMediaType.get(mediaType);
        return binding == null ? null : binding.get();
    }

    public boolean isBindingSupported(final String urlPattern) {
        return byPath.containsKey(urlPattern);
    }

    public boolean isBindingSupported(final MediaType mediaType) {
        return byMediaType.containsKey(mediaType);
    }

    public Map<String, Binding> getBindings() {
        return Activatable.filter(byPath);
    }

    public Map<MediaType, Binding> getBindingsByMediaType() {
        return Activatable.filter(byMediaType);
    }

    public Map<String, Binding> getAllBindings() {
        return Activatable.unfiltered(byPath);
    }

    public Map<MediaType, Binding> getAllBindingsByMediaType() {
        return Activatable.unfiltered(byMediaType);
    }

    public void setActive(final BindingKey bk, final boolean active) {
        if (byPath.containsKey(bk.getServletPath())) {
            byPath.get(bk.getServletPath()).setActive(active);
        }
    }
}

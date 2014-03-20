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
package org.n52.sos.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Activatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Map<String, Activatable<Binding>> bindings = new HashMap<String, Activatable<Binding>>(0);

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
        this.bindings.clear();
        final SettingsManager sm = SettingsManager.getInstance();
        try {
            for (final Binding binding : bindings) {
                this.bindings.put(binding.getUrlPattern(),
                        Activatable.from(binding, sm.isActive(new BindingKey(binding.getUrlPattern()))));
            }
        } catch (final ConnectionProviderException ex) {
            throw new ConfigurationException("Could not check status of Binding", ex);
        }
        if (this.bindings.isEmpty()) {
            final StringBuilder exceptionText = new StringBuilder();
            exceptionText.append("No Binding implementation could be loaded! ");
            exceptionText.append("If the SOS is not used as webapp, this has no effect! ");
            exceptionText.append("Else add a Binding implementation!");
            LOG.warn(exceptionText.toString());
        }
    }

    public Binding getBinding(final String urlPattern) {
        final Activatable<Binding> binding = bindings.get(urlPattern);
        return binding == null ? null : binding.get();
    }

    public boolean isBindingSupported(final String urlPattern) {
        return bindings.containsKey(urlPattern);
    }

    public Map<String, Binding> getBindings() {
        return Activatable.filter(bindings);
    }

    public Map<String, Binding> getAllBindings() {
        return Activatable.unfiltered(bindings);
    }

    public void setActive(final BindingKey bk, final boolean active) {
        if (bindings.containsKey(bk.getServletPath())) {
            bindings.get(bk.getServletPath()).setActive(active);
        }
    }
}

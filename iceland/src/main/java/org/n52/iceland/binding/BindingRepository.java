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
package org.n52.iceland.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.iceland.util.Activatable;
import org.n52.iceland.util.http.MediaType;
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

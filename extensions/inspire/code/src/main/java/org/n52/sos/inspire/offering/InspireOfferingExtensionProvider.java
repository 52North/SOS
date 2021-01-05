/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.inspire.offering;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.inspire.InspireConstants;
import org.n52.shetland.inspire.InspireObject;
import org.n52.shetland.inspire.InspireUniqueResourceIdentifier;
import org.n52.shetland.ogc.ows.extension.CapabilitiesExtension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.sos.inspire.AbstractInspireProvider;
import org.n52.sos.inspire.settings.InspireSettings;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionKey;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionProvider;
import org.n52.sos.util.SosHelper;

import com.google.common.base.Strings;

/**
 * Implementation of {@link SosObservationOfferingExtensionProvider} for INSPIRE
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
@Configurable
public class InspireOfferingExtensionProvider extends AbstractInspireProvider
        implements SosObservationOfferingExtensionProvider {
    private static final Set<SosObservationOfferingExtensionKey> KEYS = Collections
            .singleton(new SosObservationOfferingExtensionKey(SosConstants.SOS,
                                                Sos2Constants.SERVICEVERSION,
                                                InspireConstants.INSPIRE));

    private boolean enabled;

    private String namespace;

    private SosHelper sosHelper;

    @Setting(InspireSettings.INSPIRE_ENABLED_KEY)
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param namespace
     *            the namespace to set
     */
    @Setting(InspireSettings.INSPIRE_NAMESPACE_KEY)
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Inject
    public void setSosHelperL(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }


    @Override
    public Set<SosObservationOfferingExtensionKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public Extensions getOfferingExtensions(String identifier) {
        return Stream.of(getSupportedLanguages(), getSupportedCRS(), getSpatialDataSetIdentifier(identifier))
                .map(o -> new CapabilitiesExtension<InspireObject>(o)
                        .setNamespace(InspireConstants.NS_INSPIRE_COMMON))
                .collect(Extensions::new,
                         Extensions::addExtension,
                         Extensions::addExtension);
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
        return this.enabled && getCache().getOfferings().contains(identifier);
    }

    private InspireUniqueResourceIdentifier getSpatialDataSetIdentifier(String identifier) {
        InspireUniqueResourceIdentifier iuri = new InspireUniqueResourceIdentifier(identifier);
        if (!Strings.isNullOrEmpty(namespace)) {
            iuri.setNamespace(namespace);
        } else {
            iuri.setNamespace(sosHelper.getServiceURL());
        }
        return iuri;
    }

}

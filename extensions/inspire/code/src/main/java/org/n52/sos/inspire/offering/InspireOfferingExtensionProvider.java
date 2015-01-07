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
package org.n52.sos.inspire.offering;

import java.util.Set;

import org.n52.sos.inspire.AbstractInspireProvider;
import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireHelper;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;

import com.google.common.collect.Sets;

/**
 * Implementation of {@link OfferingExtensionProvider} for INSPIRE
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireOfferingExtensionProvider extends AbstractInspireProvider implements OfferingExtensionProvider {

    Set<OfferingExtensionKey> providerKeys = Sets.newHashSet(new OfferingExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, InspireConstants.INSPIRE));

    public InspireOfferingExtensionProvider() {
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeyTypes() {
        return providerKeys;
    }

    @Override
    public SwesExtensions getOfferingExtensions(String identifier) {
        SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<InspireSupportedLanguages>().setValue(
                getSupportedLanguages()).setNamespace(InspireConstants.NS_INSPIRE_COMMON));
        extensions.addSwesExtension(new SwesExtensionImpl<InspireSupportedCRS>().setValue(getSupportedCRS())
                .setNamespace(InspireConstants.NS_INSPIRE_COMMON));
        return extensions;
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
    	if (InspireHelper.getInstance().isEnabled()) {
    		 return getCache().getOfferings().contains(identifier);
    	}
        return false;
    }

}

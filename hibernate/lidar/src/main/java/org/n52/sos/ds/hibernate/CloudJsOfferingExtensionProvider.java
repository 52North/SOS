/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import java.util.Set;

import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;

import com.google.common.collect.Sets;

public class CloudJsOfferingExtensionProvider implements OfferingExtensionProvider {
    
    Set<OfferingExtensionKey> providerKeys = Sets.newHashSet(new OfferingExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, "CloudJS"));

    public CloudJsOfferingExtensionProvider() {
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeyTypes() {
        return providerKeys;
    }

    @Override
    public SwesExtensions getOfferingExtensions(String identifier) {
        SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<SweText>().setValue(
                new SweText().setValue(getCloudJsForOffering(identifier))));
        return extensions;
    }

    private String getCloudJsForOffering(String identifier) {
        // TODO get cloud.js for offering 
        return "{\n"
                + "	\"version\": \"1.6\",\n"
                + "	\"octreeDir\": \"data\",\n"
                + "	\"boundingBox\": {\n"
                + "		\"lx\": 635577.79,\n"
                + "		\"ly\": 848882.15,\n"
                + "		\"lz\": 406.14,\n"
                + "		\"ux\": 640233.3,\n"
                + "		\"uy\": 853537.66,\n"
                + "		\"uz\": 5061.65000000001\n"
                + "	},\n"
                + "	\"tightBoundingBox\": {\n"
                + "		\"lx\": 635597.86,\n"
                + "		\"ly\": 848885.46,\n"
                + "		\"lz\": 406.92,\n"
                + "		\"ux\": 638995.8,\n"
                + "		\"uy\": 853537.66,\n"
                + "		\"uz\": 589.96\n"
                + "	},\n"
                + "	\"pointAttributes\": \"LAZ\",\n"
                + "	\"spacing\": 500,\n"
                + "	\"scale\": 0.01,\n"
                + "	\"hierarchyStepSize\": 6\n"
                + "}";
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
        return true;
    }
}

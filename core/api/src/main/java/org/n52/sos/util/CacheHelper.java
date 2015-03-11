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
package org.n52.sos.util;

import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;

/**
 * Helper class that contains method to add/remove prefixes to/from offering,
 * procedure, observableProperty, and featureOfInterest. Especially for legacy
 * databases without URI identifiers.
 * 
 * @since 4.0.0
 * 
 */
public final class CacheHelper {

    protected static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static String addPrefixOrGetOfferingIdentifier(String offering) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return ServiceConfiguration.getInstance().getDefaultOfferingPrefix() + offering;
        }
        return offering;
    }

    public static String removePrefixAndGetOfferingIdentifier(String offering) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return offering.replace(ServiceConfiguration.getInstance().getDefaultOfferingPrefix(),
                    Constants.EMPTY_STRING);
        }
        return offering;
    }

    public static String addPrefixOrGetProcedureIdentifier(String procedure) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return ServiceConfiguration.getInstance().getDefaultProcedurePrefix() + procedure;
        }
        return procedure;
    }

    public static String removePrefixAndGetProcedureIdentifier(String procedure) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return procedure.replace(ServiceConfiguration.getInstance().getDefaultProcedurePrefix(),
                    Constants.EMPTY_STRING);
        }
        return procedure;
    }

    public static String addPrefixOrGetFeatureIdentifier(String feature) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return ServiceConfiguration.getInstance().getDefaultFeaturePrefix() + feature;
        }
        return feature;
    }

    public static String removePrefixAndGetFeatureIdentifier(String feature) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return feature.replace(ServiceConfiguration.getInstance().getDefaultFeaturePrefix(),
                    Constants.EMPTY_STRING);
        }
        return feature;
    }

    public static String addPrefixOrGetObservablePropertyIdentifier(String observableProperty) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return ServiceConfiguration.getInstance().getDefaultObservablePropertyPrefix() + observableProperty;
        }
        return observableProperty;
    }

    public static String removePrefixAndGetObservablePropertyIdentifier(String observableProperty) {
        if (ServiceConfiguration.getInstance().isUseDefaultPrefixes()) {
            return observableProperty.replace(ServiceConfiguration.getInstance().getDefaultObservablePropertyPrefix(),
                    Constants.EMPTY_STRING);
        }
        return observableProperty;
    }

    private CacheHelper() {
    }
}

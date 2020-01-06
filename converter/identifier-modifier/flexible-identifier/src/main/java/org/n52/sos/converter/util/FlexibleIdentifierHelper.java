/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.converter.util;


import java.util.Optional;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.faroe.ConfigurationError;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.util.JavaHelper;

@Configurable
public class FlexibleIdentifierHelper implements Constructable {


    public static final String RETURN_HUMAN_READABLE_IDENTIFIER = "returnHumanReadableIdentifier";

    private boolean returnHumanReadableIdentifier;

    private boolean includeOffering = true;

    private boolean includeProcedure = true;

    private boolean includeObservableProperty = true;

    private boolean includeFeatureOfInterest = true;

    @Override
    public void init() {
    }

    @Setting(FlexibleIdentifierSettings.RETURN_HUMAN_READABLE_IDENTIFIER_KEY)
    public void setReturnHumanReadableIdentifier(final boolean returnHumanReadableIdentifier)
            throws ConfigurationError {
        this.returnHumanReadableIdentifier = returnHumanReadableIdentifier;
    }

    public boolean isSetReturnHumanReadableIdentifier() {
        return returnHumanReadableIdentifier;
    }

    @Setting(FlexibleIdentifierSettings.INCLUDE_OFFERING_KEY)
    public void setIncludeOffering(final boolean includeOffering) throws ConfigurationError {
        this.includeOffering = includeOffering;
    }

    public boolean isSetIncludeOffering() {
        return includeOffering;
    }

    @Setting(FlexibleIdentifierSettings.INCLUDE_PROCEDURE_KEY)
    public void setIncludeProcedure(final boolean includeProcedure) throws ConfigurationError {
        this.includeProcedure = includeProcedure;
    }

    public boolean isSetIncludeProcedure() {
        return includeProcedure;
    }

    @Setting(FlexibleIdentifierSettings.INCLUDE_OBSERVABLE_PROPERTY_KEY)
    public void setIncludeObservableProperty(final boolean includeObservableProperty) throws ConfigurationError {
        this.includeObservableProperty = includeObservableProperty;
    }

    public boolean isSetIncludeObservableProperty() {
        return includeObservableProperty;
    }

    @Setting(FlexibleIdentifierSettings.INCLUDE_FEATURE_OF_INTEREST_KEY)
    public void setIncludeFeatureOfInterest(final boolean includeFeatureOfInterest) throws ConfigurationError {
        this.includeFeatureOfInterest = includeFeatureOfInterest;
    }

    public boolean isSetIncludeFeatureOfInterest() {
        return includeFeatureOfInterest;
    }

    public boolean checkIsReturnHumanReadableIdentifierFlagExtensionSet(Extensions extensions)
            throws InvalidParameterValueException {
        if (extensions == null) {
            return false;
        }
        Optional<Extension<?>> extension = extensions.getExtension(RETURN_HUMAN_READABLE_IDENTIFIER);
        if (extension.isPresent()) {
            Object value = extension.get().getValue();
            if (!(value instanceof SweBoolean)) {
                throw new InvalidParameterValueException(RETURN_HUMAN_READABLE_IDENTIFIER, JavaHelper.asString(value));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean checkForReturnHumanReadableIdentifierFlagExtension(Extensions extensions)
            throws InvalidParameterValueException {
        if (extensions == null) {
            return false;
        }
        Optional<Extension<?>> extension = extensions.getExtension(RETURN_HUMAN_READABLE_IDENTIFIER);
        if (extension.isPresent()) {
            Object value = extension.get().getValue();
            if (!(value instanceof SweBoolean)) {
                throw new InvalidParameterValueException(RETURN_HUMAN_READABLE_IDENTIFIER, JavaHelper.asString(value));
            }
            return ((SweBoolean) value).getValue();
        } else {
            return false;
        }
    }

}

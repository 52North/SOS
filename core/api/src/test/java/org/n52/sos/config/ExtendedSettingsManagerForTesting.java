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
package org.n52.sos.config;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.n52.iceland.config.AbstractSettingValueFactory;
import org.n52.iceland.config.BooleanSettingValueForTesting;
import org.n52.iceland.config.ChoiceSettingValueForTesting;
import org.n52.iceland.config.FileSettingValueForTesting;
import org.n52.iceland.config.IntegerSettingValueForTesting;
import org.n52.iceland.config.MultilingualStringValueForTesting;
import org.n52.iceland.config.NumericSettingValueForTesting;
import org.n52.iceland.config.SettingValue;
import org.n52.iceland.config.SettingValueFactory;
import org.n52.iceland.config.SettingsManagerForTesting;
import org.n52.iceland.config.StringSettingValueForTesting;
import org.n52.iceland.config.TimeInstantSettingValueForTesting;
import org.n52.iceland.config.UriSettingValueForTesting;
import org.n52.iceland.exception.NoSuchExtensionException;
import org.n52.iceland.exception.NoSuchOfferingException;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.ows.OfferingExtension;
import org.n52.iceland.ogc.ows.StaticCapabilities;
import org.n52.iceland.ogc.ows.StringBasedCapabilitiesExtension;

public class ExtendedSettingsManagerForTesting extends SettingsManagerForTesting implements CapabilitiesExtensionManager {
    
    public static final SettingValueFactory SETTING_FACTORY = new ExtendedSettingFactoryForTesting();
    
    private static class ExtendedSettingFactoryForTesting extends AbstractSettingValueFactory {

        @Override
        protected SettingValue<Boolean> newBooleanSettingValue() {
            return new BooleanSettingValueForTesting();
        }

        @Override
        protected SettingValue<Integer> newIntegerSettingValue() {
            return new IntegerSettingValueForTesting();
        }

        @Override
        protected SettingValue<String> newStringSettingValue() {
            return new StringSettingValueForTesting();
        }

        @Override
        protected SettingValue<File> newFileSettingValue() {
            return new FileSettingValueForTesting();
        }

        @Override
        protected SettingValue<URI> newUriSettingValue() {
            return new UriSettingValueForTesting();
        }

        @Override
        protected SettingValue<Double> newNumericSettingValue() {
            return new NumericSettingValueForTesting();
        }

        @Override
        protected SettingValue<TimeInstant> newTimeInstantSettingValue() {
            return new TimeInstantSettingValueForTesting();
        }

        @Override
        protected SettingValue<MultilingualString> newMultiLingualStringSettingValue() {
            return new MultilingualStringValueForTesting();
        }

        @Override
        protected SettingValue<String> newChoiceSettingValue() {
            return new ChoiceSettingValueForTesting();
        }
    }

    @Override
    public Map<String, List<OfferingExtension>> getOfferingExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<OfferingExtension>> getActiveOfferingExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveOfferingExtension(String offering, String identifier, String value) throws NoSuchOfferingException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disableOfferingExtension(String offering, String identifier, boolean disabled)
            throws NoSuchExtensionException, NoSuchOfferingException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteOfferingExtension(String offering, String identifier) throws NoSuchOfferingException,
            NoSuchExtensionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCapabilitiesExtension(String identifier, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disableCapabilitiesExtension(String identifier, boolean disabled) throws NoSuchExtensionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteCapabiltiesExtension(String identfier) throws NoSuchExtensionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setActiveStaticCapabilities(String identifier) throws NoSuchExtensionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getActiveStaticCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getActiveStaticCapabilitiesDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isStaticCapabilitiesActive() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, StaticCapabilities> getStaticCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StaticCapabilities getStaticCapabilities(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveStaticCapabilities(String identifier, String document) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteStaticCapabilities(String identifier) throws NoSuchExtensionException {
        // TODO Auto-generated method stub
        
    }

}

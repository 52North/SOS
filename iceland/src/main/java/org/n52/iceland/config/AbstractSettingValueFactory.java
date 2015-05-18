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
package org.n52.iceland.config;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import org.n52.iceland.config.settings.BooleanSettingDefinition;
import org.n52.iceland.config.settings.ChoiceSettingDefinition;
import org.n52.iceland.config.settings.FileSettingDefinition;
import org.n52.iceland.config.settings.IntegerSettingDefinition;
import org.n52.iceland.config.settings.MultilingualStringSettingDefinition;
import org.n52.iceland.config.settings.NumericSettingDefinition;
import org.n52.iceland.config.settings.StringSettingDefinition;
import org.n52.iceland.config.settings.TimeInstantSettingDefinition;
import org.n52.iceland.config.settings.UriSettingDefinition;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.exception.ows.concrete.DateTimeParseException;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.util.DateTimeHelper;
import org.n52.iceland.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;



public abstract class AbstractSettingValueFactory implements SettingValueFactory {

    private static final Set<String> VALID_FALSE_VALUES = ImmutableSet.of("false", "no", "off", "0");

    private static final Set<String> VALID_TRUE_VALUES = ImmutableSet.of("true", "yes", "on", "1");

    @Override
    public SettingValue<Boolean> newBooleanSettingValue(BooleanSettingDefinition setting, String stringValue) {
        return newBooleanSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Boolean> newBooleanSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newBooleanSettingValue().setValue(parseBoolean(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<Integer> newIntegerSettingValue(IntegerSettingDefinition setting, String stringValue) {
        return newIntegerSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Integer> newIntegerSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newIntegerSettingValue().setValue(parseInteger(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<Double> newNumericSettingValue(NumericSettingDefinition setting, String stringValue) {
        return newNumericSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<Double> newNumericSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newNumericSettingValue().setValue(parseDouble(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<String> newStringSettingValue(StringSettingDefinition setting, String stringValue) {
        return newStringSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<String> newStringSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newStringSettingValue().setValue(parseString(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<File> newFileSettingValue(FileSettingDefinition setting, String stringValue) {
        return newFileSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<File> newFileSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newFileSettingValue().setValue(parseFile(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<URI> newUriSettingValue(UriSettingDefinition setting, String stringValue) {
        return newUriSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<URI> newUriSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newUriSettingValue().setValue(parseUri(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<TimeInstant> newTimeInstantSettingValue(TimeInstantSettingDefinition setting, String stringValue) {
        return newTimeInstantSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<TimeInstant> newTimeInstantSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting,
            String stringValue) {
        return newTimeInstantSettingValue().setValue(parseTimeInstant(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<MultilingualString> newMultiLingualStringValue(MultilingualStringSettingDefinition setting, String stringValue) {
        return newMultiLingualStringSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<MultilingualString> newMultiLingualStringSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting, String stringValue) {
       return newMultiLingualStringSettingValue().setValue(parseMultilingualString(stringValue)).setKey(setting.getKey());
    }

    @Override
    public SettingValue<String> newChoiceSettingValue(ChoiceSettingDefinition setting, String stringValue) {
        return newChoiceSettingValueFromGenericDefinition(setting, stringValue);
    }

    private SettingValue<String> newChoiceSettingValueFromGenericDefinition(SettingDefinition<?, ?> setting, String stringValue) {
        ChoiceSettingDefinition def = (ChoiceSettingDefinition) setting;
        if (!def.hasOption(stringValue)) {
            throw new ConfigurationException("Invalid choice value");
        }
       return newChoiceSettingValue().setValue(stringValue).setKey(setting.getKey());
    }



    @Override
    public SettingValue<?> newSettingValue(SettingDefinition<?, ?> setting, String value) {
        switch (setting.getType()) {
        case BOOLEAN:
            return newBooleanSettingValueFromGenericDefinition(setting, value);
        case FILE:
            return newFileSettingValueFromGenericDefinition(setting, value);
        case INTEGER:
            return newIntegerSettingValueFromGenericDefinition(setting, value);
        case NUMERIC:
            return newNumericSettingValueFromGenericDefinition(setting, value);
        case STRING:
            return newStringSettingValueFromGenericDefinition(setting, value);
        case URI:
            return newUriSettingValueFromGenericDefinition(setting, value);
        case TIMEINSTANT:
            return newTimeInstantSettingValueFromGenericDefinition(setting, value);
        case MULTILINGUAL_STRING:
            return newMultiLingualStringSettingValueFromGenericDefinition(setting, value);
        case CHOICE:
            return newChoiceSettingValueFromGenericDefinition(setting, value);
        default:
            throw new IllegalArgumentException(String.format("Type %s not supported", setting.getType()));
        }
    }

    /**
     * Parses the a string to a {@code Boolean}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Boolean parseBoolean(String stringValue) throws IllegalArgumentException {
        if (nullOrEmpty(stringValue)) {
            return Boolean.FALSE;
        }
        stringValue = stringValue.trim().toLowerCase();
        if (VALID_FALSE_VALUES.contains(stringValue)) {
            return Boolean.FALSE;
        } else if (VALID_TRUE_VALUES.contains(stringValue)) {
            return Boolean.TRUE;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a valid boolean value", stringValue));
        }
    }

    /**
     * Parses the a string to a {@code File}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected File parseFile(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : new File(stringValue);
    }

    /**
     * Parses the a string to a {@code Integer}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Integer parseInteger(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : Integer.valueOf(stringValue, 10);
    }

    /**
     * Parses the a string to a {@code Double}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected Double parseDouble(String stringValue) throws IllegalArgumentException {
        return nullOrEmpty(stringValue) ? null : Double.parseDouble(stringValue);
    }

    /**
     * Parses the a string to a {@code URI}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected URI parseUri(String stringValue) throws IllegalArgumentException {
        if (nullOrEmpty(stringValue)) {
            return null;
        }
        try {
            return new URI(stringValue);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses the a string to a {@code String}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected String parseString(String stringValue) {
        return nullOrEmpty(stringValue) ? null : stringValue;
    }

    /**
     * Parses the a string to a {@code String}.
     * <p/>
     *
     * @param stringValue
     *            the string value
     *            <p/>
     * @return the parsed value
     *         <p/>
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    protected TimeInstant parseTimeInstant(String stringValue) {
        if (nullOrEmpty(stringValue)) {
            return null;
        } else {
            try {
                return (TimeInstant)DateTimeHelper.parseIsoString2DateTime2Time(stringValue);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
     private MultilingualString parseMultilingualString(String stringValue) {
        MultilingualString ms = new MultilingualString();
        if (!nullOrEmpty(stringValue)) {
            JsonNode json = JSONUtils.loadString(stringValue);
            Iterator<String> it = json.fieldNames();
            while(it.hasNext()) {
                String key = it.next();
                String value = json.path(key).asText();
                ms.addLocalization(LocaleHelper.fromString(key), value);
            }
        }
        return ms;
    }

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Boolean> newBooleanSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Integer> newIntegerSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<String> newStringSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<String> newChoiceSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<File> newFileSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<URI> newUriSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<Double> newNumericSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<TimeInstant> newTimeInstantSettingValue();

    /**
     * @return a implementation specific instance
     */
    protected abstract SettingValue<MultilingualString> newMultiLingualStringSettingValue();

    /**
     * @param stringValue
     *            <p/>
     * @return <code>stringValue == null || stringValue.trim().isEmpty()</code>
     */
    protected boolean nullOrEmpty(String stringValue) {
        return stringValue == null || stringValue.trim().isEmpty();
    }


}

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


import org.n52.iceland.config.SettingType;
import org.n52.iceland.config.SettingValue;
import org.n52.iceland.i18n.MultilingualString;

class MultilingualStringValueForTestin implements
        SettingValue<MultilingualString> {

    private String key;

    private MultilingualString value;

    public MultilingualStringValueForTestin() {
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public MultilingualString getValue() {
        return this.value;
    }

    @Override
    public SettingValue<MultilingualString> setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public SettingValue<MultilingualString> setValue(MultilingualString value) {
        this.value = value;
        return this;
    }

    @Override
    public SettingType getType() {
        return SettingType.MULTILINGUAL_STRING;
    }

}

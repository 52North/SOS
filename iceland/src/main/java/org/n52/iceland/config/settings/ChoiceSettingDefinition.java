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
package org.n52.iceland.config.settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingType;
import org.n52.iceland.util.CollectionHelper;

import com.google.common.base.Preconditions;

/**
 * {@link SettingDefinition} resulting in a drop down menu offering different
 * options.By default the options are sorted by their display name (it's set 
 * to value if not provided).
 * 
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * 
 * @since 4.2.0
 *
 */
public class ChoiceSettingDefinition extends AbstractSettingDefinition<ChoiceSettingDefinition, String> {
	
    private final Map<String, String> options = new HashMap<>();

    public ChoiceSettingDefinition() {
        super(SettingType.CHOICE);
    }

    public Map<String, String> getOptions() {
        return Collections.unmodifiableMap(CollectionHelper.sortByValue(options));

    }

    public boolean hasOption(String value) {
        return this.options.containsKey(value);
    }

    public ChoiceSettingDefinition addOption(String option) {
        String value = Preconditions.checkNotNull(option);
        this.options.put(value, value);
        return this;
    }

    public ChoiceSettingDefinition addOption(String option, String displayName) {
        this.options.put(
        		Preconditions.checkNotNull(option),
        		Preconditions.checkNotNull(displayName));
        return this;
    }

}

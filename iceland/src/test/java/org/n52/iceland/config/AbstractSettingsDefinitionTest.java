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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.settings.BooleanSettingDefinition;

public class AbstractSettingsDefinitionTest {
	
	
	@Test
	public void isNotEquals() {
		assertThat(getBooleanSettingOne().equals(getBooleanSettingTwo()), is(false));
	}

	private BooleanSettingDefinition getBooleanSettingOne() {
		return getDefaultBooleanSetting().setKey("key.one")
		 .setTitle("Test setting one")
	        .setDescription("Test setting one");
	}

	private BooleanSettingDefinition getBooleanSettingTwo() {
		return getDefaultBooleanSetting().setKey("key.two")
				 .setTitle("Test setting two")
			        .setDescription("Test setting two");
		
	}
	
	private BooleanSettingDefinition getDefaultBooleanSetting() {
		return new BooleanSettingDefinition()
        .setGroup(getGroup()).setOrder(1).setDefaultValue(false).setOptional(true);
       
	}

	private SettingDefinitionGroup getGroup() {
		return new SettingDefinitionGroup().setTitle("Test").setOrder(2);
	}

}

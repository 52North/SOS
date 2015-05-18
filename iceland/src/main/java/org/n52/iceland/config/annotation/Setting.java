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
package org.n52.iceland.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that should be applied to a method that takes a single Setting as
 * a parameter. The parameter of this method should be of the same type as the
 * {@link org.n52.iceland.config.SettingDefinition} declared with the same
 * {@code key} in a {@link org.n52.iceland.config.SettingDefinitionProvider}.
 * <p/>
 * It is needed to apply the {@code Configurable} annotation to a class with a
 * method annotated with this annotations for the {@code SettingsManager} to
 * recognize it.
 * <p/>
 * <b>Example usage:</b>
 * 
 * <pre>
 * &#064;Setting(MiscellaneousSettingDefinitions.TOKEN_SEPERATOR_KEY)
 * public void setTokenSeperator(String separator) {
 *     this.separator = separator;
 * }
 * </pre>
 * <p/>
 * 
 * @see Configurable
 * @see org.n52.iceland.config.SettingDefinition
 * @see org.n52.iceland.config.SettingDefinitionProvider
 * @see org.n52.iceland.config.SettingsManager <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {

    /**
     * The key of the setting.
     * <p/>
     * 
     * @return the key
     */
    String value();
}

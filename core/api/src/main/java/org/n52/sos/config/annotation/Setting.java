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
package org.n52.sos.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that should be applied to a method that takes a single Setting as
 * a parameter. The parameter of this method should be of the same type as the
 * {@link org.n52.sos.config.SettingDefinition} declared with the same
 * {@code key} in a {@link org.n52.sos.config.SettingDefinitionProvider}.
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
 * @see org.n52.sos.config.SettingDefinition
 * @see org.n52.sos.config.SettingDefinitionProvider
 * @see org.n52.sos.config.SettingsManager <p/>
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

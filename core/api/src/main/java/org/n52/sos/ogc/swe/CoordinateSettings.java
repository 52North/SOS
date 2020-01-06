/**
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
package org.n52.sos.ogc.swe;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.service.MiscSettings;

import com.google.common.collect.ImmutableSet;

/**
 * {@link SettingDefinitionProvider} for SWE coordinates
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class CoordinateSettings implements SettingDefinitionProvider {
	
	public static final String NORTHING_COORDINATE_NAME = "swe.coordinate.northing";
	
	public static final String EASTING_COORDINATE_NAME = "swe.coordinate.easting";
	
	public static final String ALTITUDE_COORDINATE_NAME = "swe.coordinate.altitude";
	
	private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?,?>>of(

			new StringSettingDefinition()
				    .setGroup(MiscSettings.GROUP)
				    .setOrder(ORDER_17)
				    .setKey(NORTHING_COORDINATE_NAME)
				    .setDefaultValue("")
				    .setOptional(true)
				    .setTitle("SweCoordinate names <tt>northing/latitude</tt>")
				    .setDescription("Provide a comma separated list of allowed names for <tt>northing/latitude</tt> "
				            + "that will <strong>replace</strong> the default values: "
				    		+ "<tt>northing</tt>, <tt>latitude</tt>, <tt>southing</tt>."),
	
			new StringSettingDefinition()
				    .setGroup(MiscSettings.GROUP)
				    .setOrder(ORDER_18)
				    .setKey(EASTING_COORDINATE_NAME)
				    .setDefaultValue("")
				    .setOptional(true)
				    .setTitle("SweCoordinate names <tt>easting/longitude</tt>")
				    .setDescription("Provide a comma separated list of allowed names for <tt>easting/longitude</tt> "
                            + "that will <strong>replace</strong> the default values: "
                            + "<tt>easting</tt>, <tt>longitude</tt>, <tt>westing</tt>."),
		    
			new StringSettingDefinition()
				    .setGroup(MiscSettings.GROUP)
				    .setOrder(ORDER_19)
				    .setKey(ALTITUDE_COORDINATE_NAME)
				    .setDefaultValue("")
				    .setOptional(true)
				    .setTitle("SweCoordinate names <tt>altitude<tt>")
				    .setDescription("Provide a comma separated list of allowed names for <tt>altitude</tt> "
                            + "that will <strong>replace</strong> the default values: "
                            + "<tt>altitude</tt>, <tt>height</tt>, <tt>depth</tt>.")
	);
					    

	@Override
	public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
		return Collections.unmodifiableSet(DEFINITIONS);
	}
	
	

}

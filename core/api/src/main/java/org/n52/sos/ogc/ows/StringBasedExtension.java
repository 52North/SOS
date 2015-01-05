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
package org.n52.sos.ogc.ows;

import org.n52.sos.ogc.swes.SwesExtension;

/**
 * <!--@deprecated use {@link org.n52.sos.ogc.swes.SwesExtension&lt;java.lang.String&gt;}-->
 * 
 * FIXME should this one replaced by SwesExtension<String> or should {@link SwesExtension} provide a direct and easy way to get the string represenation of this extension Object?
 *
 */
public interface StringBasedExtension extends SwesExtension<String> {

    /**
     * Get this extension as a String.
     *
     * @return the extension as a xml text
     * 
     * <!--@deprecated use {@link Encoder.encode(Object).toString()}-->
     */
	public String getExtension();
}

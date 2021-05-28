/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.netcdf;

import org.junit.Assert;

import org.junit.Test;
import org.n52.janmayen.http.MediaType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NetcdfConstantsTest implements NetcdfConstants {

    @Test
    public void test_netcdf_zip_version_3() {
        Assert.assertTrue(CONTENT_TYPE_NETCDF_3_ZIP.toString()
                .equals(new MediaType(APPLICATION, ZIP,
                        ImmutableMap.of(SUBTYPE, ImmutableList.of(NETCDF), PARAM_VERSION, ImmutableList.of("3")))
                                .toString()));
        Assert.assertTrue(CONTENT_TYPE_NETCDF_4_ZIP.toString()
                .equals(new MediaType(APPLICATION, ZIP,
                        ImmutableMap.of(SUBTYPE, ImmutableList.of(NETCDF), PARAM_VERSION, ImmutableList.of("4")))
                                .toString()));
    }

}

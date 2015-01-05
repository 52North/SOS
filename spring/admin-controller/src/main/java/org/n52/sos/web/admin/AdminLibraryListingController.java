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
package org.n52.sos.web.admin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
@Controller
public class AdminLibraryListingController extends AbstractController {
    private static final FileFilter FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname != null && pathname.isFile();
        }
    };

    @RequestMapping(ControllerConstants.Paths.ADMIN_LIBRARY_LIST)
    public ModelAndView view() {
        List<String> libs;
        File lib = new File(getContext().getRealPath("WEB-INF/lib"));
        if (lib.exists() && lib.isDirectory() && lib.canRead()) {
            final File[] listFiles = lib.listFiles(FILE_FILTER);
            libs = new ArrayList<String>(listFiles.length);
            for (File f : listFiles) {
                libs.add(f.getName());
            }
        } else {
            libs = Collections.emptyList();
        }
        Collections.sort(libs);
        return new ModelAndView(ControllerConstants.Views.ADMIN_LIBRARY_LIST,
                ControllerConstants.LIBRARIES_MODEL_ATTRIBUTE, libs);
    }

}

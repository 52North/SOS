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
package org.n52.sos.web.install;

import org.n52.sos.web.ControllerConstants;

/**
 * @since 4.0.0
 * 
 */
public interface InstallConstants {
    /* request parameters */
    String OVERWRITE_TABLES_PARAMETER = "overwrite_tables";

    String CREATE_TEST_DATA_PARAMETER = "create_test_data";

    String CREATE_TABLES_PARAMETER = "create_tables";
    
    String UPDATE_TABLES_PARAMETER = "update_tables";

    String DATASOURCE_PARAMETER = "datasource";

    enum Step {
        /* DECLARATION ORDER IS IMPORTANT! */
        WELCOME(ControllerConstants.Paths.INSTALL_INDEX, ControllerConstants.Views.INSTALL_INDEX), DATASOURCE(
                ControllerConstants.Paths.INSTALL_DATASOURCE, ControllerConstants.Views.INSTALL_DATASOURCE), SETTINGS(
                ControllerConstants.Paths.INSTALL_SETTINGS, ControllerConstants.Views.INSTALL_SETTINGS), FINISH(
                ControllerConstants.Paths.INSTALL_FINISH, ControllerConstants.Views.INSTALL_FINISH);
        private final String path;

        private final String view;

        private final String completionAttribute;

        private Step(String path, String view) {
            this.view = view;
            this.path = path;
            this.completionAttribute = view + "_complete";
        }

        public Step getNext() {
            final Step[] all = values();
            final int me = ordinal();
            return (me < all.length - 1) ? all[me + 1] : null;
        }

        public Step getPrevious() {
            final Step[] all = values();
            final int me = ordinal();
            return (me == 0) ? null : all[me - 1];
        }

        public String getPath() {
            return path;
        }

        public String getView() {
            return view;
        }

        public String getCompletionAttribute() {
            return completionAttribute;
        }

    }
}

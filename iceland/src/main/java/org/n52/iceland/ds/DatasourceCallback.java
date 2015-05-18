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
package org.n52.iceland.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class DatasourceCallback {

    public void onFirstConnection(Connection connection)
            throws SQLException {
    }

    public Properties onInit(Properties properties) {
        return properties;
    }

    public static DatasourceCallback nullCallback() {
        return new DatasourceCallback() {
        };
    }

    public static DatasourceCallback chain(final DatasourceCallback first,
                                           final DatasourceCallback second) {
        return new DatasourceCallback() {
            @Override
            public void onFirstConnection(Connection connection)
                    throws SQLException {
                first.onFirstConnection(connection);
                second.onFirstConnection(connection);
            }

            @Override
            public Properties onInit(Properties properties) {
                return second.onInit(first.onInit(properties));
            }
        };
    }
}

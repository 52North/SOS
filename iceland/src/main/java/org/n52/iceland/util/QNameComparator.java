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
package org.n52.iceland.util;

import java.util.Comparator;

import javax.xml.namespace.QName;

/**
 * Comparator for {@link QName}s.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class QNameComparator implements Comparator<QName> {
    public static final QNameComparator INSTANCE = new QNameComparator();

    @Override
    public int compare(QName o1, QName o2) {
        if (o1.getPrefix() != null) {
            if (o2.getPrefix() != null) {
                int prefix = o1.getPrefix().compareTo(o2.getPrefix());
                if (prefix != 0) {
                    return prefix;
                }
            } else {
                return 1;
            }
        } else if (o2.getPrefix() != null) {
            return -1;
        }
        return o1.getLocalPart().compareTo(o2.getLocalPart());
    }

}

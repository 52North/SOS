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
package org.n52.iceland.w3c.xlink;

import org.n52.iceland.util.StringHelper;

public class W3CHrefAttribute {
    
    private String href;

    public W3CHrefAttribute() {
    }
    
    public W3CHrefAttribute(String href) {
        setHref(href);
    }

    public String getHref() {
        return href;
    }
    
    public W3CHrefAttribute setHref(String href) {
       this.href = href;
       return this;
    }

    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }
}

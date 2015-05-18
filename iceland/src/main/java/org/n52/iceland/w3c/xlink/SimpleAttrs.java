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
import org.n52.iceland.w3c.W3CConstants;

public abstract class SimpleAttrs {
    
    private String href;
    
    private String role;
    
    private String arcrole;
    
    private String title;
    
    private W3CConstants.ShowType show;
    
    private W3CConstants.ActuateType actuate;

    /**
     * @return the type
     */
    public W3CConstants.TypeType getType() {
        return W3CConstants.TypeType.simple;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public SimpleAttrs setHref(String href) {
        this.href = href;
        return this;
    }
    
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public SimpleAttrs setRole(String role) {
        this.role = role;
        return this;
    }
    
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(getRole());
    }

    /**
     * @return the arcrole
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * @param arcrole the arcrole to set
     */
    public SimpleAttrs setArcrole(String arcrole) {
        this.arcrole = arcrole;
        return this;
    }
    
    public boolean isSetArcrole() {
        return StringHelper.isNotEmpty(getArcrole());
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public SimpleAttrs setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    /**
     * @return the show
     */
    public W3CConstants.ShowType getShow() {
        return show;
    }

    /**
     * @param show the show to set
     */
    public SimpleAttrs setShow(W3CConstants.ShowType show) {
        this.show = show;
        return this;
    }
    
    public boolean isSetShow() {
        return getShow() != null;
    }

    /**
     * @return the actuate
     */
    public W3CConstants.ActuateType getActuate() {
        return actuate;
    }

    /**
     * @param actuate the actuate to set
     */
    public SimpleAttrs setActuate(W3CConstants.ActuateType actuate) {
        this.actuate = actuate;
        return this;
    }
    
    public boolean isSetActuate() {
        return getActuate() != null;
    }

}

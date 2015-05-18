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
package org.n52.iceland.ogc.gml;

import org.n52.iceland.util.Constants;
import org.n52.iceland.util.StringHelper;
import org.n52.iceland.w3c.xlink.W3CHrefAttribute;

public class AbstractReferenceType {

    /**
     * Href
     */
    private W3CHrefAttribute href;

    /**
     * Title
     */
    private String title;

    /**
     * Role
     */
    private String role;

    /**
     * Get href
     * 
     * @return Href
     */
    public String getHref() {
    	if (href != null) {
    		return href.getHref();
    	}
        return null;
    }

    /**
     * Get title
     * 
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get role
     * 
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set href
     * 
     * @param href
     *            Href to set
     */
    public void setHref(String href) {
        this.href = new W3CHrefAttribute(href);
    }

    /**
     * Set title
     * 
     * @param title
     *            Title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set role
     * 
     * @param role
     *            Role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Check whether href is set
     * 
     * @return <code>true</code>, if href is set
     */
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }

    /**
     * Check whether title is set
     * 
     * @return <code>true</code>, if title is set
     */
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(title);
    }

    /**
     * Check whether role is set
     * 
     * @return <code>true</code>, if role is set
     */
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(role);
    }

    /**
     * Check whether href, title, and role are set
     * 
     * @return <code>true</code>, if href, title, and role are set
     */
    public boolean hasValues() {
        return isSetHref() && isSetRole() && isSetTitle();
    }

    /**
     * Get title from href.<br>
     * Cuts href: <br>
     * - starts with 'http': cuts string at last {@link Constants#SLASH_CHAR}<br>
     * - starts with 'urn': cuts string at last {@link Constants#COLON_CHAR}<br>
     * - contains {@link Constants#NUMBER_SIGN_STRING}: cuts string at last
     * {@link Constants#NUMBER_SIGN_CHAR}<br>
     * 
     * @return Title from href
     */
    public String getTitleFromHref() {
        String title = getHref();
        if (title.startsWith("http")) {
            title = title.substring(title.lastIndexOf(Constants.SLASH_CHAR) + 1, title.length());
        } else if (title.startsWith("urn")) {
            title = title.substring(title.lastIndexOf(Constants.COLON_CHAR) + 1, title.length());
        }
        if (title.contains(Constants.NUMBER_SIGN_STRING)) {
            title = title.substring(title.lastIndexOf(Constants.NUMBER_SIGN_CHAR) + 1, title.length());
        }
        return title;
    }

    @Override
    public String toString() {
        return String.format("AbstractReferenceType [title=%s, role=%s, href=%s]", getTitle(), getRole(), getHref());
    }
}

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
package org.n52.sos.i18n;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;
import java.util.Locale;

import org.n52.sos.ogc.gml.CodeType;

import com.google.common.base.Objects;

/**
 * Immutable localized variant of a string.
 *
 * @author Christian Autermann
 * @since 4.2.0
 */
public class LocalizedString implements Serializable {
    private static final Locale NULL_LOCALE = new Locale("");
    private static final long serialVersionUID = 8336541273458492969L;
    private final Locale lang;
    private final String text;

    public LocalizedString(String value) {
        this(null, value);
    }

    public LocalizedString(Locale lang, String value) {
        checkArgument(!isNullOrEmpty(value));
        this.text = value;
        this.lang = lang == null ? NULL_LOCALE : lang;

    }

    /**
     * @return the value of this localized string
     */
    public String getText() {
        return this.text;
    }

    /**
     * @return the language of this localized string
     */
    public Locale getLang() {
        return this.lang;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("lang", getLang())
                .add("text", getText())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLang(), getText());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocalizedString) {
            LocalizedString that = (LocalizedString) obj;
            return Objects.equal(this.getLang(), that.getLang()) &&
                   Objects.equal(this.getText(), that.getText());
        }
        return false;
    }

    public CodeType asCodeType() {
        return new CodeType(getText(), LocaleHelper.toString(getLang()));
    }

}

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
package org.n52.iceland.i18n;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;
import java.util.Locale;

import org.n52.iceland.ogc.gml.CodeType;

import com.google.common.base.MoreObjects;
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
        return MoreObjects.toStringHelper(this)
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

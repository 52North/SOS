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
package org.n52.sos.inspire.aqd;

import java.net.URI;

import org.n52.sos.util.Nillable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class Pronunciation {

    private Nillable<String> ipa = Nillable.missing();
    private Nillable<URI> soundLink = Nillable.missing();

    public Nillable<String> getIPA() {
        return ipa;
    }

    public Pronunciation setIPA(Nillable<String> ipa) {
        this.ipa = Preconditions.checkNotNull(ipa);
        return this;
    }

    public Pronunciation setIPA(String ipa) {
        return setIPA(Nillable.of(ipa));
    }

    public Nillable<URI> getSoundLink() {
        return soundLink;
    }

    public Pronunciation setSoundLink(Nillable<URI> soundLink) {
        this.soundLink = Preconditions.checkNotNull(soundLink);
        return this;
    }

    public Pronunciation setSoundLink(URI soundLink) {
        return setSoundLink(Nillable.of(soundLink));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIPA(), getSoundLink());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("ipa", getIPA())
                .add("soundLink", getSoundLink())
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pronunciation) {
            Pronunciation that = (Pronunciation) obj;
            return Objects.equal(this.getIPA(), that.getIPA()) &&
                   Objects.equal(this.getSoundLink(), that.getSoundLink());
        }
        return false;
    }

}

/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.sos.util.CollectionHelper;

/**
 * Internal representation of the ISO GMD Telephone.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CiTelephone extends AbstractObject {

    private List<String> voice = new ArrayList<>();
    private List<String> facsimile = new ArrayList<>();
    
    public boolean isSetVoice() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(voice);
    }

    public List<String> getVoice() {
        return voice;
    }

    public CiTelephone setVoice(final Collection<String> voice) {
        voice.clear();
        if (voice != null) {
            this.voice.addAll(voice);
        }
        return this;
    }

    public CiTelephone addVoice(final String voice) {
        if (voice != null) {
            this.voice.add(voice);
        }
        return this;
    }

    public boolean isSetFacsimile() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(facsimile);
    }

    public List<String> getFacsimile() {
        return facsimile;
    }

    public CiTelephone addFacsimile(final String facsimile) {
        if (facsimile != null) {
            this.facsimile.add(facsimile);
        }
        return this;
    }

    public CiTelephone setFacsimile(final Collection<String> facsimile) {
        this.facsimile.clear();
        if (facsimile != null) {
            this.facsimile.addAll(facsimile);
        }
        return this;
    }

}

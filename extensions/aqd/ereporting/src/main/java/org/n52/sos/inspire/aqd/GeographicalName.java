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


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Nillable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class GeographicalName {

    private Nillable<String> language = Nillable.missing();
    private Nillable<CodeType> nativeness = Nillable.missing();
    private Nillable<CodeType> nameStatus = Nillable.missing();
    private Nillable<String> sourceOfName = Nillable.missing();
    private Nillable<Pronunciation> pronunciation = Nillable.missing();
    private List<Spelling> spelling = new LinkedList<>();
    private Nillable<CodeType> grammaticalGender = Nillable.missing();
    private Nillable<CodeType> grammaticalNumber = Nillable.missing();
    
    public Nillable<String> getLanguage() {
        return language;
    }

    public GeographicalName setLanguage(Nillable<String> language) {
        this.language = Preconditions.checkNotNull(language);
        return this;
    }

    public GeographicalName setLanguage(String language) {
        return setLanguage(Nillable.of(language));
    }

    public Nillable<CodeType> getNativeness() {
        return nativeness;
    }

    public GeographicalName setNativeness(Nillable<CodeType> nativeness) {
        this.nativeness = Preconditions.checkNotNull(nativeness);
        return this;
    }

    public GeographicalName setNativeness(CodeType nativeness) {
        return setNativeness(Nillable.of(nativeness));
    }

    public Nillable<CodeType> getNameStatus() {
        return nameStatus;
    }

    public GeographicalName setNameStatus(Nillable<CodeType> nameStatus) {
        this.nameStatus = Preconditions.checkNotNull(nameStatus);
        return this;
    }

    public GeographicalName setNameStatus(CodeType nameStatus) {
        return setNameStatus(Nillable.of(nameStatus));
    }

    public Nillable<String> getSourceOfName() {
        return sourceOfName;
    }

    public GeographicalName setSourceOfName(Nillable<String> sourceOfName) {
        this.sourceOfName = Preconditions.checkNotNull(sourceOfName);
        return this;
    }

    public GeographicalName setSourceOfName(String sourceOfName) {
        return setSourceOfName(Nillable.of(sourceOfName));
    }

    public Nillable<Pronunciation> getPronunciation() {
        return pronunciation;
    }

    public GeographicalName setPronunciation(
                                             Nillable<Pronunciation> pronunciation) {
        this.pronunciation = Preconditions.checkNotNull(pronunciation);
        return this;
    }

    public GeographicalName setPronunciation(
                                             Pronunciation pronunciation) {
        return setPronunciation(Nillable.of(pronunciation));
    }

    public List<Spelling> getSpelling() {
        if (CollectionHelper.isEmpty(spelling)) {
            addSpelling(new Spelling());
        }
        return Collections.unmodifiableList(spelling);
    }

    public GeographicalName setSpelling(List<Spelling> spelling) {
        this.spelling = Preconditions.checkNotNull(spelling);
        return this;
    }

    public GeographicalName addSpelling(Spelling spelling) {
        this.spelling.add(Preconditions.checkNotNull(spelling));
        return this;
    }

    public Nillable<CodeType> getGrammaticalGender() {
        return grammaticalGender;
    }

    public GeographicalName setGrammaticalGender(
                                                 Nillable<CodeType> grammaticalGender) {
        this.grammaticalGender = Preconditions.checkNotNull(grammaticalGender);
        return this;
    }

    public GeographicalName setGrammaticalGender(CodeType grammaticalGender) {
        return setGrammaticalGender(Nillable.of(grammaticalGender));
    }

    public Nillable<CodeType> getGrammaticalNumber() {
        return grammaticalNumber;
    }

    public GeographicalName setGrammaticalNumber(
                                                 Nillable<CodeType> grammaticalNumber) {
        this.grammaticalNumber = Preconditions.checkNotNull(grammaticalNumber);
        return this;
    }

    public GeographicalName setGrammaticalNumber(CodeType grammaticalNumber) {
        return setGrammaticalNumber(Nillable.of(grammaticalNumber));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("language", getLanguage())
                .add("nativeness", getNativeness())
                .add("nameStatus", getNameStatus())
                .add("grammaticalGender", getGrammaticalGender())
                .add("grammaticalNumber", getGrammaticalNumber())
                .add("pronunciation", getPronunciation())
                .add("sourceOfName", getSourceOfName())
                .add("spelling", getSpelling())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLanguage(),getNativeness(),getNameStatus(),
                getGrammaticalNumber(), getGrammaticalGender(), getPronunciation(),
                getSourceOfName(), getSpelling());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeographicalName) {
            GeographicalName that = (GeographicalName) obj;
            return Objects.equal(this.getGrammaticalGender(), that.getGrammaticalGender()) &&
                   Objects.equal(this.getGrammaticalNumber(), that.getGrammaticalNumber()) &&
                   Objects.equal(this.getLanguage(), that.getLanguage()) &&
                   Objects.equal(this.getNameStatus(), that.getNameStatus()) &&
                   Objects.equal(this.getNativeness(), that.getNativeness()) &&
                   Objects.equal(this.getPronunciation(), that.getPronunciation()) &&
                   Objects.equal(this.getSourceOfName(), that.getSourceOfName()) &&
                   Objects.equal(this.getSpelling(), that.getSpelling());
        }
        return false;
    }







}

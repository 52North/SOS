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
package org.n52.sos.ogc.sensorML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @since 4.0.0
 * 
 */
public class AbstractSensorML extends SosProcedureDescription {
    private static final long serialVersionUID = -5715790909736521952L;
    private List<String> keywords = new ArrayList<String>(0);
    private List<SmlIdentifier> identifications = new ArrayList<SmlIdentifier>(0);
    private List<SmlClassifier> classifications = new ArrayList<SmlClassifier>(0);
    private List<SmlCharacteristics> characteristics = new ArrayList<SmlCharacteristics>(0);
    private final List<SmlCapabilities> capabilities = new ArrayList<SmlCapabilities>(0);
    private List<SmlContact> contacts = new ArrayList<SmlContact>(0);
    private final List<AbstractSmlDocumentation> documentations = new ArrayList<AbstractSmlDocumentation>(0);
    private String history;
    private String gmlId;
    
    @Override
    public SosProcedureDescription setIdentifier(final String identifier) {
        super.setIdentifier(identifier);
        return this;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public AbstractSensorML setKeywords(final List<String> keywords) {
        this.keywords = keywords;
        return this;
    }
    
    public AbstractSensorML addKeywords(final List<String> keywords) {
        if (isSetKeywords()) {
            this.keywords.addAll(keywords);
        } else {
            this.keywords = keywords;
        }
        return this;
    }

    public List<SmlIdentifier> getIdentifications() {
        return identifications;
    }

    public AbstractSensorML setIdentifications(final List<SmlIdentifier> identifications) {
        if (this.identifications.isEmpty()) {
            this.identifications = identifications;
        } else {
            this.identifications.addAll(identifications);
        }
        return this;
    }

    public Optional<SmlIdentifier> findIdentification(Predicate<SmlIdentifier> predicate) {
        if (isSetIdentifications()) {
            return Iterables.tryFind(getIdentifications(), predicate);
        }
        return Optional.absent();
    }

    public boolean isIdentificationSet(Predicate<SmlIdentifier> predicate) {
        return findIdentification(predicate).isPresent();
    }

    public List<SmlClassifier> getClassifications() {
        return classifications;
    }

    public AbstractSensorML setClassifications(
            final List<SmlClassifier> classifications) {
        this.classifications = classifications;
        return this;
    }

    public AbstractSensorML addClassifications(
            List<SmlClassifier> classifications) {
        if (isSetClassifications()) {
            this.classifications.addAll(classifications);
        }
        return this;
    }

    public Optional<SmlClassifier> findClassifier(Predicate<SmlClassifier> predicate) {
        if (isSetClassifications()) {
            return Iterables.tryFind(this.classifications, predicate);
        } else {
            return Optional.absent();
        }
    }

    public AbstractSensorML addClassification(final SmlClassifier classifier) {
        classifications.add(classifier);
        return this;
    }

    @Override
    public AbstractSensorML setValidTime(final Time validTime) {
        super.setValidTime(validTime);
        return this;
    }

    public List<SmlCharacteristics> getCharacteristics() {
        return characteristics;
    }

    public AbstractSensorML setCharacteristics(final List<SmlCharacteristics> characteristics) {
        if (isSetCharacteristics()) {
            this.characteristics.addAll(characteristics);
        } else {
            this.characteristics = characteristics;
        }
        return this;
    }

    public AbstractSensorML addCharacteristic(final SmlCharacteristics characteristic) {
        characteristics.add(characteristic);
        return this;
    }

    public List<SmlCapabilities> getCapabilities() {
        return capabilities;
    }

    public AbstractSensorML addCapabilities(final List<SmlCapabilities> capabilities) {
        if (capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
        return this;
    }

    public Optional<SmlCapabilities> findCapabilities(Predicate<SmlCapabilities> predicate) {
        if (this.capabilities != null) {
            return Iterables.tryFind(this.capabilities, predicate);
        } else {
            return Optional.absent();
        }
    }
    
    public void removeCapabilities(SmlCapabilities caps) {
        if (this.capabilities != null) {
            this.capabilities.remove(caps);
        }
    }

    public AbstractSensorML addCapabilities(final SmlCapabilities capabilities) {
        return addCapabilities(Collections.singletonList(capabilities));
    }

    public List<SmlContact> getContact() {
        return contacts;
    }

    public AbstractSensorML setContact(final List<SmlContact> contacts) {
        if (isSetContacts()) {
            this.contacts.addAll(contacts);
        } else {
            this.contacts = contacts;
        }
        return this;
    }

    private boolean isSetContacts() {
        return contacts != null && !contacts.isEmpty();
    }

    public AbstractSensorML addContact(final SmlContact contact) {
        if (this.contacts == null) {
            this.contacts = new LinkedList<SmlContact>();
        }
        this.contacts.add(contact);
        return this;
    }

    public List<AbstractSmlDocumentation> getDocumentation() {
        return documentations;
    }

    public AbstractSensorML setDocumentation(final List<AbstractSmlDocumentation> documentations) {
        this.documentations.addAll(documentations);
        return this;
    }

    public AbstractSensorML addDocumentation(final AbstractSmlDocumentation documentation) {
        documentations.add(documentation);
        return this;
    }

    public String getHistory() {
        return history;
    }

    public AbstractSensorML setHistory(final String history) {
        this.history = history;
        return this;
    }

    public AbstractSensorML addIdentifier(final SmlIdentifier identifier) {
        identifications.add(identifier);
        return this;
    }

    public boolean isSetKeywords() {
        return keywords != null && !keywords.isEmpty();
    }

    public boolean isSetIdentifications() {
        return identifications != null && !identifications.isEmpty();
    }

    public boolean isSetClassifications() {
        return classifications != null && !classifications.isEmpty();
    }

    public boolean isSetCharacteristics() {
        return characteristics != null && !characteristics.isEmpty();
    }

    public boolean isSetCapabilities() {
        return capabilities != null && !capabilities.isEmpty();
    }

    public boolean isSetDocumentation() {
        return documentations != null && !documentations.isEmpty();
    }

    public boolean isSetContact() {
        return contacts != null && !contacts.isEmpty();
    }

    public boolean isSetHistory() {
        return history != null && !history.isEmpty();
    }

    public String getGmlId() {
        return gmlId;
    }

    public void setGmlId(String gmlId) {
        this.gmlId = gmlId;
    }

    public boolean isSetGmlId() {
        return StringHelper.isNotEmpty(gmlId);
    }
    
    public void copyTo(AbstractSensorML copyOf) {
        super.copyTo(copyOf);
        copyOf.setCharacteristics(getCharacteristics());
        copyOf.setClassifications(getClassifications());
        copyOf.setContact(getContact());
        copyOf.setDocumentation(getDocumentation());
        copyOf.setHistory(getHistory());
        copyOf.setIdentifications(getIdentifications());
        copyOf.setKeywords(getKeywords());
    }
}

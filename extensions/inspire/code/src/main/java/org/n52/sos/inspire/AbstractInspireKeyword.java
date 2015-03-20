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
package org.n52.sos.inspire;

import org.n52.sos.inspire.InspireOriginatingControlledVocabulary;

/**
 * Abstract service internal representation of INSPIRE keywords
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public abstract class AbstractInspireKeyword<T> {

    private InspireOriginatingControlledVocabulary originatingControlledVocabulary;

    /**
     * constructor
     */
    public AbstractInspireKeyword() {
    }

    /**
     * constructor
     * 
     * @param originatingControlledVocabulary
     *            the keyword
     */
    public AbstractInspireKeyword(InspireOriginatingControlledVocabulary originatingControlledVocabulary) {
        setOriginatingControlledVocabulary(originatingControlledVocabulary);
    }

    /**
     * Get the keyword
     * 
     * @return the originatingControlledVocabulary
     */
    public InspireOriginatingControlledVocabulary getOriginatingControlledVocabulary() {
        return originatingControlledVocabulary;
    }

    /**
     * Set the keyword
     * 
     * @param originatingControlledVocabulary
     *            the originatingControlledVocabulary to set
     */
    private void setOriginatingControlledVocabulary(
            InspireOriginatingControlledVocabulary originatingControlledVocabulary) {
        this.originatingControlledVocabulary = originatingControlledVocabulary;
    }

    /**
     * Check if the keyword is set
     * 
     * @return <code>true</code>, if the keyword is set
     */
    public boolean isSetOriginatingControlledVocabulary() {
        return getOriginatingControlledVocabulary() != null;
    }

    /**
     * Get the keyword value
     * 
     * @return the keyword value
     */
    public abstract T getKeywordValue();

    /**
     * Set the keyword value
     * 
     * @param keywordValue
     *            the keyword value to set
     */
    protected abstract void setKeywordValue(T keywordValue);

    @Override
    public String toString() {
        return String.format("%s %n[%n originatingControlledVocabulary=%s,%n keywordValue=%s%n]", this.getClass()
                .getSimpleName(), getOriginatingControlledVocabulary(), getKeywordValue());
    }

}

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
package org.n52.svalbard.inspire.base2;


import java.util.List;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.AbstractGML;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

import com.google.common.collect.Lists;

public class DocumentCitation extends AbstractGML implements AttributeSimpleAttrs  {
    
    private static final long serialVersionUID = 4804669479492697969L;
    private SimpleAttrs simpleAttrs;
    private Nillable<DateTime> date;
    private List<Nillable<String>> links = Lists.newArrayList();

    @Override
    public void setSimpleAttrs(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
    }

    @Override
    public SimpleAttrs getSimpleAttrs() {
        return simpleAttrs;
    }

    @Override
    public boolean isSetSimpleAttrs() {
        return simpleAttrs != null;
    }

    /**
     * @return the date
     */
    public Nillable<DateTime> getDate() {
        return date;
    }

    /**
     * @param date the date to set
     * @return 
     */
    public DocumentCitation setDate(Nillable<DateTime> date) {
        this.date = date;
        return this;
    }

    /**
     * @param date the date to set
     * @return 
     */
    public DocumentCitation setDate(DateTime date) {
        setDate(Nillable.present(date));
        return this;
    }
    
    /**
     * @return the links
     */
    public List<Nillable<String>> getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     * @return 
     */
    public DocumentCitation setLinks(List<Nillable<String>> links) {
        getLinks().clear();
        getLinks().addAll(links);
        return this;
    }

    public DocumentCitation addLink(String link) {
        addLink(Nillable.present(link));
        return this;
    }
    
    public DocumentCitation addLink(Nillable<String> link) {
        getLinks().add(link);
        return this;
    }

    public boolean isSetDate() {
        return getDate() != null && getDate().isPresent();
    }

    public boolean isSetLinks() {
        return getLinks() != null && !getLinks().isEmpty();
    }

}

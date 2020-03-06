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
package org.n52.svalbard.inspire.ompr;

import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;
import org.n52.svalbard.inspire.base.Identifier;
import org.n52.svalbard.inspire.base2.DocumentCitation;
import org.n52.svalbard.inspire.base2.RelatedParty;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class Process extends SosProcedureDescription {

    private static final long serialVersionUID = -103848856778331552L;

    /**
     * 0..1 name
     */

    /**
     * 1..1
     */
    private String type;

    /**
     * 0..*
     */
    private List<DocumentCitation> documentation = Lists.newArrayList();

    /**
     * 0..*
     */
    private List<ProcessParameter> processParameter = Lists.newArrayList();

    /**
     * 1..*
     */
    private List<RelatedParty> responsibleParty = Lists.newArrayList();

    public Process() {
        setDescriptionFormat(InspireOMPRConstants.NS_OMPR_30);
        setDefaultElementEncoding(InspireOMPRConstants.NS_OMPR_30);
    }
    
    public Identifier getInspireId() {
        return new Identifier(getIdentifierCodeWithAuthority());
    }

    /**
     * @param name the name to set
     */
    public Process setName(String name) {
        super.setName(new CodeType(name));
        return this;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public Process setType(String type) {
        this.type = type;
        return this;
    }
    
    public boolean isSetType() {
        return !Strings.isNullOrEmpty(getType());
    }

    /**
     * @return the documentation
     */
    public List<DocumentCitation> getDocumentation() {
        return documentation;
    }

    /**
     * @param documentation the documentation to set
     */
    public Process setDocumentation(List<DocumentCitation> documentation) {
        this.documentation.clear();
        if (documentation != null) {
            this.documentation.addAll(documentation);
        }
        return this;
    }
    
    /**
     * @param documentation the documentation to add
     */
    public Process addDocumentation(DocumentCitation documentation) {
        if (documentation != null) {
            this.documentation.add(documentation);
        }
        return this;
    }

    public boolean isSetDocumentation() {
        return CollectionHelper.isNotEmpty(getDocumentation());
    }
    
    /**
     * @return the processParameter
     */
    public List<ProcessParameter> getProcessParameter() {
        return processParameter;
    }

    /**
     * @param processParameter the processParameter to set
     */
    public Process setProcessParameter(List<ProcessParameter> processParameter) {
        this.processParameter.clear();
        if (processParameter != null) {
            this.processParameter.addAll(processParameter);
        }
        return this;
    }
    
    /**
     * @param processParameter the processParameter to add
     */
    public Process addProcessParameter(ProcessParameter processParameter) {
        if (processParameter != null) {
            this.processParameter.add(processParameter);
        }
        return this;
    }
    
    public boolean isSetProcessParameter() {
        return CollectionHelper.isNotEmpty(getProcessParameter());
    }

    /**
     * @return the responsibleParty
     */
    public List<RelatedParty> getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * @param responsibleParty the responsibleParty to set
     */
    public Process setResponsibleParty(List<RelatedParty> responsibleParty) {
        this.responsibleParty.clear();
        if (responsibleParty != null) {
            this.responsibleParty.addAll(responsibleParty);
        }
        return this;
    }
    
    /**
     * @param responsibleParty the responsibleParty to add
     */
    public Process addResponsibleParty(RelatedParty responsibleParty) {
        if (responsibleParty != null) {
            this.responsibleParty.add(responsibleParty);
        }
        return this;
    }
    

    public boolean isSetResponsibleParty() {
        return CollectionHelper.isNotEmpty(getResponsibleParty());
    }

}

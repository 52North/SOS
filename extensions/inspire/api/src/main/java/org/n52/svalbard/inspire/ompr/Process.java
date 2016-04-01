/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
    private List<DocumentCitation> documentation;

    /**
     * 0..*
     */
    private List<ProcessParameter> processParameter;

    /**
     * 1..*
     */
    private List<RelatedParty> responsibleParty;

    public Process() {
        setDescriptionFormat(InspireOMPRConstants.NS_OMPR_30);
    }
    
    public Identifier getInspireId() {
        return new Identifier(getIdentifierCodeWithAuthority());
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        super.setName(new CodeType(name));
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
    public void setType(String type) {
        this.type = type;
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
    public void setDocumentation(List<DocumentCitation> documentation) {
        this.documentation = documentation;
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
    public void setProcessParameter(List<ProcessParameter> processParameter) {
        this.processParameter = processParameter;
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
    public void setResponsibleParty(List<RelatedParty> responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public boolean isSetResponsiblePartyn() {
        return CollectionHelper.isNotEmpty(getResponsibleParty());
    }

}

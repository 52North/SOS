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
package org.n52.sos.util.builder;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.util.StringHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ProcedureDescriptionBuilder {

    private String procedureIdentifer;

    private String offeringIdentifier;

    private CodeType offeringName;
    
    private String offeringDescription;

    public static ProcedureDescriptionBuilder aSensorMLProcedureDescription() {
        return new ProcedureDescriptionBuilder();
    }

    public ProcedureDescriptionBuilder setIdentifier(String procedureIdentifer) {
        this.procedureIdentifer = procedureIdentifer;
        return this;
    }

    public ProcedureDescriptionBuilder setOffering(String offeringIdentifier, String offeringName) {
        this.offeringIdentifier = offeringIdentifier;
        this.offeringName = new CodeType(offeringName);
        return this;
    }
    
    public ProcedureDescriptionBuilder setOffering(String offeringIdentifier, CodeType offeringName) {
        this.offeringIdentifier = offeringIdentifier;
        this.offeringName = offeringName;
        return this;
    }

    public SosProcedureDescription build() {
        SosProcedureDescription description = new SosProcedureDescriptionUnknowType(procedureIdentifer, null, null);
        if (offeringIdentifier != null && offeringName != null) {
            SosOffering sosOffering = new SosOffering(offeringIdentifier, offeringName);
            if (StringHelper.isNotEmpty(offeringDescription)) {
                sosOffering.setDescription(offeringDescription);
            }
            ((SosProcedureDescriptionUnknowType) description).addOffering(sosOffering);
        }
        return description;
    }
}

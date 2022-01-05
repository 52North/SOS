/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.create;

import java.util.Locale;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureCreationContext;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

import com.google.common.base.Strings;

/**
 * Strategy to create the {@link SosProcedureDescription} from a XML string.
 */
public class XmlStringDescriptionCreationStrategy implements DescriptionCreationStrategy {

    @Override
    public SosProcedureDescription<?> create(ProcedureEntity p, String descriptionFormat, Locale i18n,
            HibernateProcedureCreationContext ctx, Session s) throws OwsExceptionReport {
        SosProcedureDescription<?> desc = new SosProcedureDescription<>(readXml(p.getDescriptionFile(), ctx));
        desc.setIdentifier(p.getIdentifier());
        desc.setDescriptionFormat(p.getFormat().getFormat());
        return desc;
    }

    @Override
    public boolean apply(ProcedureEntity p) {
        return p != null && !Strings.isNullOrEmpty(p.getDescriptionFile()) && p.getDescriptionFile().startsWith("<");
    }

    protected AbstractFeature readXml(String xml, HibernateProcedureCreationContext ctx) throws OwsExceptionReport {
        try {
            XmlObject parsed = XmlHelper.parseXmlString(xml);
            return (AbstractFeature) ctx.getDecoderRepository().getDecoder(CodingHelper.getDecoderKey(parsed))
                    .decode(parsed);
        } catch (DecodingException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while creating procedure description from XML string");
        }
    }
}

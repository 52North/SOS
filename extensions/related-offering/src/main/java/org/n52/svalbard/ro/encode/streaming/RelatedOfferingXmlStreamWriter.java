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
package org.n52.svalbard.ro.encode.streaming;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.OfferingContext;
import org.n52.sos.ogc.sos.RelatedOfferingConstants;
import org.n52.sos.ogc.sos.RelatedOfferings;
import org.n52.sos.w3c.W3CConstants;

public class RelatedOfferingXmlStreamWriter extends XmlStreamWriter<RelatedOfferings> {
    
    private RelatedOfferings relatedOfferings;
    
    public RelatedOfferingXmlStreamWriter(RelatedOfferings relatedOfferings) {
        setRelatedOfferings(relatedOfferings);
    }

    public RelatedOfferingXmlStreamWriter() {
    }

    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getRelatedOfferings(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getRelatedOfferings(), out, encodingValues);
    }

    @Override
    public void write(RelatedOfferings response, OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(response, out, new EncodingValues());
    }

    @Override
    public void write(RelatedOfferings relatedOfferings, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            setRelatedOfferings(relatedOfferings);
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeRelatedOfferingsDoc(encodingValues);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    private void writeRelatedOfferingsDoc(EncodingValues encodingValues) throws XMLStreamException {
        start(RelatedOfferingConstants.QN_RO_RELATED_OFFERINGS);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(RelatedOfferingConstants.NS_RO_PREFIX, RelatedOfferingConstants.NS_RO);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        addXlinkHrefAttr(RelatedOfferingConstants.RELATED_OFFERINGS);
        addXlinkTitleAttr(RelatedOfferingConstants.RELATED_OFFERINGS);
        for (OfferingContext offeringContext : getRelatedOfferings().getValue()) {
            start(RelatedOfferingConstants.QN_RO_RELATED_OFFERING);
            writeOfferingContext(offeringContext);
            end(RelatedOfferingConstants.QN_RO_RELATED_OFFERING);
        }
        end(RelatedOfferingConstants.QN_RO_RELATED_OFFERINGS);
        
    }

    private void writeOfferingContext(OfferingContext offeringContext) throws XMLStreamException {
        start(RelatedOfferingConstants.QN_RO_OFFERING_CONTEXT);
        writeRole(offeringContext.getRole());
        writeRelatedOffering(offeringContext.getRelatedOffering());
        end(RelatedOfferingConstants.QN_RO_OFFERING_CONTEXT);
    }

    private void writeRole(ReferenceType role) throws XMLStreamException {
        empty(RelatedOfferingConstants.QN_RO_ROLE);
        addXlinkHrefAttr(role.getHref());
    }

    private void writeRelatedOffering(ReferenceType relatedOffering) throws XMLStreamException {
        empty(RelatedOfferingConstants.QN_RO_RELATED_OFFERING);
        addXlinkHrefAttr(relatedOffering.getHref());
        if (relatedOffering.isSetTitle()) {
            addXlinkTitleAttr(relatedOffering.getTitle());
        } else {
            addXlinkTitleAttr(relatedOffering.getTitleFromHref());
        }
    }

    /**
     * @return the relatedOfferings
     */
    protected RelatedOfferings getRelatedOfferings() {
        return relatedOfferings;
    }

    /**
     * @param relatedOfferings the relatedOfferings to set
     */
    protected void setRelatedOfferings(RelatedOfferings relatedOfferings) {
        this.relatedOfferings = relatedOfferings;
    }

}

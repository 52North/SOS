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
package org.n52.svalbard.inspire.ompr.v30.encode;

import static java.util.Collections.singletonMap;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractGmlEncoderv321;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ProcedureEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.base.InspireBaseConstants;
import org.n52.svalbard.inspire.base2.DocumentCitation;
import org.n52.svalbard.inspire.base2.InspireBase2Constants;
import org.n52.svalbard.inspire.base2.RelatedParty;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;
import org.n52.svalbard.inspire.ompr.ProcessParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.ompr.x30.ProcessDocument;
import eu.europa.ec.inspire.schemas.ompr.x30.ProcessPropertyType;
import eu.europa.ec.inspire.schemas.ompr.x30.ProcessType;
import eu.europa.ec.inspire.schemas.ompr.x30.ProcessType.InspireId;
import net.opengis.gml.x32.FeaturePropertyType;

public class ProcessTypeEncoder extends AbstractGmlEncoderv321<Process>
        implements ProcedureEncoder<XmlObject, Process> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTypeEncoder.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES =
            singletonMap(SupportedTypeKey.ProcedureDescriptionFormat,
                    (Set<String>) ImmutableSet.of(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE));

    private static final Map<String, ImmutableMap<String, Set<String>>> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(SosConstants.SOS,
                    ImmutableMap.<String, Set<String>> builder()
                            .put(Sos2Constants.SERVICEVERSION,
                                    ImmutableSet.of(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL))
                            .put(Sos1Constants.SERVICEVERSION,
                                    ImmutableSet.of(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE))
                            .build());

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(InspireOMPRConstants.NS_OMPR_30, SosProcedureDescription.class, Process.class),
            encoderKeysForElements(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE, SosProcedureDescription.class,
                    Process.class),
            encoderKeysForElements("http://inspire.ec.europa.eu/featureconcept/Process", SosProcedureDescription.class,
                    Process.class));

    public ProcessTypeEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireOMPRConstants.NS_OMPR_30, InspireOMPRConstants.NS_OMPR_PREFIX);
    }

    @Override
    public MediaType getContentType() {
        return InspireOMPRConstants.OMPR_30_CONTENT_TYPE;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireOMPRConstants.OMPR_SCHEMA_LOCATION);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        if (SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.containsKey(service)
                && SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).containsKey(version)) {
            return SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).get(version);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public XmlObject encode(Process process) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(process, Collections.<HelperValues, String> emptyMap());
    }

    @Override
    public XmlObject encode(Process process, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return createProcess(process);
    }

    protected ProcessType createProcess(Process process) throws OwsExceptionReport {
        if (process.isSetSensorDescriptionXmlString()) {
            XmlObject encodedObject = null;
            try {
                encodedObject = XmlObject.Factory.parse(process.getSensorDescriptionXmlString());
                if (encodedObject instanceof ProcessType) {
                    ProcessType pt = (ProcessType) encodedObject;
                    checkForInspireId(pt, process);
                    return pt;
                } else if (encodedObject instanceof ProcessDocument) {
                    return ((ProcessDocument) encodedObject).getProcess();
                } else if (encodedObject instanceof ProcessPropertyType) {
                    return ((ProcessPropertyType) encodedObject).getProcess();
                } else {
                    throw new UnsupportedEncoderInputException(this, process);
                }
            } catch (final XmlException xmle) {
                throw new NoApplicableCodeException().causedBy(xmle);
            }
        } else {
            ProcessType pt = ProcessType.Factory.newInstance();
            if (!process.isSetGmlID()) {
                process.setGmlId("p_" + JavaHelper.generateID(process.toString()));
            }
            pt.setId(process.getGmlId());

            addInspireId(pt, process);
            addName(pt, process);
            addType(pt, process);
            addDocumentation(pt, process);
            addProcessParameter(pt, process);
            addResponsibleParty(pt, process);
            return pt;
        }
    }

    private void checkForInspireId(ProcessType pt, Process process) throws OwsExceptionReport {
        if (pt.getInspireId() == null) {
            if (process.isSetIdentifier()) {
                addInspireId(pt, process);
            } else {
                InspireId iId = pt.addNewInspireId();
                iId.setNil();
                iId.setNilReason("unknown");
            }
        }
        
    }

    private void addInspireId(ProcessType pt, Process process) throws OwsExceptionReport {
        pt.addNewInspireId().set(encodeBASEPropertyType(process.getInspireId()));
    }

    private void addName(ProcessType pt, Process process) {
        if (process.isSetName()) {
            pt.addNewName2().setStringValue(process.getFirstName().getValue());
        }
    }

    private void addType(ProcessType pt, Process process) {
        if (process.isSetType()) {
            pt.addNewType().setStringValue(process.getType());
        } else {
            pt.addNewType().setNil();
        }
    }

    private void addDocumentation(ProcessType pt, Process process) throws OwsExceptionReport {
        if (process.isSetDocumentation()) {
            for (DocumentCitation documentCitation : process.getDocumentation()) {
                pt.addNewDocumentation().addNewDocumentCitation().set(encodeBASE2(documentCitation));
            }
        }
    }

    private void addProcessParameter(ProcessType pt, Process process) throws OwsExceptionReport {
        if (process.isSetProcessParameter()) {
            for (ProcessParameter processParameter : process.getProcessParameter()) {
                pt.addNewProcessParameter().addNewProcessParameter().set(encodeOMPR(processParameter));
            }
        }
    }

    private void addResponsibleParty(ProcessType pt, Process process) throws OwsExceptionReport {
        if (process.isSetResponsibleParty()) {
            for (RelatedParty relatedParty : process.getResponsibleParty()) {
                pt.addNewResponsibleParty().addNewRelatedParty().set(encodeBASE2(relatedParty));
            }
        }
    }

    @Override
    protected XmlObject createFeature(FeaturePropertyType featurePropertyType, AbstractFeature abstractFeature,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (additionalValues.containsKey(HelperValues.ENCODE)
                && additionalValues.get(HelperValues.ENCODE).equals("false")) {
            featurePropertyType.setHref(abstractFeature.getIdentifierCodeWithAuthority().getValue());
            if (abstractFeature.isSetName()) {
                featurePropertyType.setTitle(abstractFeature.getFirstName().getValue());
            }
            return featurePropertyType;
        }
        return encodeOMPRDocument(abstractFeature);
    }

    protected static XmlObject encodeOMPR(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireOMPRConstants.NS_OMPR_30, o);
    }

    protected static XmlObject encodeOMPRDocument(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireBaseConstants.NS_BASE, o);
    }

    protected static XmlObject encodeBASE(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBaseConstants.NS_BASE, o);
    }

    protected static XmlObject encodeBASEPropertyType(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(InspireBaseConstants.NS_BASE, o);
    }

    protected static XmlObject encodeBASEDocument(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireBaseConstants.NS_BASE, o);
    }

    protected static XmlObject encodeBASE(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBaseConstants.NS_BASE, o, helperValues);
    }

    protected static XmlObject encodeBASE2(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBase2Constants.NS_BASE2, o);
    }

    protected static XmlObject encodeBASE2PropertyType(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(InspireBase2Constants.NS_BASE2, o);
    }

    protected static XmlObject encodeBASE2Document(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireBase2Constants.NS_BASE2, o);
    }

    protected static XmlObject encodeBASE2(Object o, Map<HelperValues, String> helperValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBase2Constants.NS_BASE2, o, helperValues);
    }

}

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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.opengis.sensorml.x20.AbstractPhysicalProcessType;
import net.opengis.sensorml.x20.AbstractProcessDocument;
import net.opengis.sensorml.x20.AbstractProcessType;
import net.opengis.sensorml.x20.AggregateProcessDocument;
import net.opengis.sensorml.x20.AggregateProcessPropertyType;
import net.opengis.sensorml.x20.DescribedObjectDocument;
import net.opengis.sensorml.x20.DescribedObjectType;
import net.opengis.sensorml.x20.PhysicalComponentDocument;
import net.opengis.sensorml.x20.PhysicalComponentPropertyType;
import net.opengis.sensorml.x20.PhysicalSystemDocument;
import net.opengis.sensorml.x20.PhysicalSystemPropertyType;
import net.opengis.sensorml.x20.SimpleProcessDocument;
import net.opengis.sensorml.x20.SimpleProcessPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.sos.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.sos.ogc.sensorML.v20.AggregateProcess;
import org.n52.sos.ogc.sensorML.v20.DescribedObject;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class SensorMLDecoderV20 implements Decoder<AbstractSensorML, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLDecoderV20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            SensorML20Constants.NS_SML_20, DescribedObjectDocument.class, SimpleProcessDocument.class,
            PhysicalComponentDocument.class, PhysicalSystemDocument.class, AbstractProcessDocument.class);

    private static final Set<String> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS = Collections
            .singleton(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);

    public SensorMLDecoderV20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.singletonMap(SupportedTypeKey.ProcedureDescriptionFormat,
                SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS);
    }

    @Override
    public AbstractSensorML decode(XmlObject element) throws OwsExceptionReport, UnsupportedDecoderInputException {
        AbstractSensorML sml = null;
        if (element instanceof PhysicalSystemDocument) {
            sml = parsePhysicalSystem((PhysicalSystemDocument) element);
        } else if (element instanceof PhysicalSystemPropertyType) {
            sml = parsePhysicalSystemPropertyType((PhysicalSystemPropertyType) element);
        } else if (element instanceof PhysicalComponentDocument) {
            sml = parsePhysicalComponent((PhysicalComponentDocument) element);
        } else if (element instanceof PhysicalComponentPropertyType) {
            sml = parsePhysicalComponentPropertyType((PhysicalComponentPropertyType) element);
        } else if (element instanceof SimpleProcessDocument) {
            sml = parseSimpleProcess((SimpleProcessDocument) element);
        } else if (element instanceof SimpleProcessPropertyType) {
            sml = parseSimpleProcessPropertyType((SimpleProcessPropertyType) element);
        } else if (element instanceof AggregateProcessDocument) {
            sml = parseAggregateProcess((AggregateProcessDocument) element);
        } else if (element instanceof AggregateProcessPropertyType) {
            sml = parseAggregateProcessPropertyType((AggregateProcessPropertyType) element);
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
        if (sml != null) {
            setXmlDescription(element, sml);
        }
        return sml;
    }

    private void setXmlDescription(XmlObject xml, AbstractSensorML sml) {
        sml.setSensorDescriptionXmlString(xml.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
    }

    private DescribedObject parsePhysicalSystem(PhysicalSystemDocument describedObject) throws OwsExceptionReport {
        PhysicalSystem ps = new PhysicalSystem();
        parseAbstractPhysicalProcess(describedObject.getAbstractPhysicalProcess(), ps);
        parseAbstractProcess(describedObject.getAbstractProcess(), ps);
        parseDescribedObject(describedObject.getDescribedObject(), ps);
        return ps;
    }

    private DescribedObject parsePhysicalSystemPropertyType(PhysicalSystemPropertyType describedObject) throws OwsExceptionReport {
        PhysicalSystem ps = new PhysicalSystem();
        parseAbstractPhysicalProcess(describedObject.getPhysicalSystem(), ps);
        parseAbstractProcess(describedObject.getPhysicalSystem(), ps);
        parseDescribedObject(describedObject.getPhysicalSystem(), ps);
        return ps;
    }

    private DescribedObject parsePhysicalComponent(PhysicalComponentDocument describedObject) throws OwsExceptionReport {
        PhysicalComponent pc = new PhysicalComponent();
        parseAbstractPhysicalProcess(describedObject.getAbstractPhysicalProcess(), pc);
        parseAbstractProcess(describedObject.getAbstractProcess(), pc);
        parseDescribedObject(describedObject.getDescribedObject(), pc);
        return pc;
    }

    private AbstractSensorML parsePhysicalComponentPropertyType(PhysicalComponentPropertyType describedObject) throws OwsExceptionReport {
        PhysicalSystem ps = new PhysicalSystem();
        parseAbstractPhysicalProcess(describedObject.getPhysicalComponent(), ps);
        parseAbstractProcess(describedObject.getPhysicalComponent(), ps);
        parseDescribedObject(describedObject.getPhysicalComponent(), ps);
        return ps;
    }

    private DescribedObject parseSimpleProcess(SimpleProcessDocument describedObject) throws OwsExceptionReport {
        SimpleProcess sp = new SimpleProcess();
        parseAbstractProcess(describedObject.getAbstractProcess(), sp);
        parseDescribedObject(describedObject.getDescribedObject(), sp);
        return sp;
    }

    private DescribedObject parseSimpleProcessPropertyType(SimpleProcessPropertyType describedObject) throws OwsExceptionReport {
        SimpleProcess sp = new SimpleProcess();
        parseAbstractProcess(describedObject.getSimpleProcess(), sp);
        parseDescribedObject(describedObject.getSimpleProcess(), sp);
        return sp;
    }

    private DescribedObject parseAggregateProcess(AggregateProcessDocument describedObject) throws OwsExceptionReport {
        AggregateProcess ap = new AggregateProcess();
        parseAbstractProcess(describedObject.getAbstractProcess(), ap);
        parseDescribedObject(describedObject.getDescribedObject(), ap);
        return ap;
    }

    private DescribedObject parseAggregateProcessPropertyType(AggregateProcessPropertyType describedObject) throws OwsExceptionReport {
        AggregateProcess ap = new AggregateProcess();
        parseAbstractProcess(describedObject.getAggregateProcess(), ap);
        parseDescribedObject(describedObject.getAggregateProcess(), ap);
        return ap;
    }

    private void parseDescribedObject(DescribedObjectType dot, DescribedObject dob) throws OwsExceptionReport {
        if (dot.isSetIdentifier()) {
            dob.setIdentifier((CodeWithAuthority) CodingHelper.decodeXmlElement(dot.getIdentifier()));
            checkIdentifierCodeSpace(dob);
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getExtensionArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getKeywordsArray())) {
                        
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getIdentificationArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getClassificationArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getValidTimeArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getSecurityConstraintsArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getLegalConstraintsArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getCharacteristicsArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getCapabilitiesArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getContactsArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getDocumentationArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getHistoryArray())) {
            
        }
        if (dot.isSetLocation()) {
            
        }
    }

    private void parseAbstractProcess(AbstractProcessType apt, AbstractProcessV20 ap) {
        if (apt.isSetTypeOf()) {
            
        }
        if (apt.isSetConfiguration()) {
            
        }
        if (apt.isSetFeaturesOfInterest()) {
            
        }
        if (apt.isSetFeaturesOfInterest()) {
            
        }
        if (apt.isSetInputs()) {
            
        }
        if (apt.isSetOutputs()) {
        
        }
        if (CollectionHelper.isNotNullOrEmpty(apt.getModesArray())) {
            
        }
    }

    private void parseAbstractPhysicalProcess(AbstractPhysicalProcessType appt, AbstractPhysicalProcess app) {
        
        if (appt.isSetAttachedTo()) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getLocalReferenceFrameArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getLocalTimeFrameArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getPositionArray())) {
            
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getTimePositionArray())) {
            
        }
    }

    private boolean checkIdentifierCodeSpace(AbstractProcessV20 ap) throws InvalidParameterValueException {
        if (ap.getIdentifierCodeWithAuthority().isSetCodeSpace()
                && OGCConstants.UNIQUE_ID.equals(ap.getIdentifierCodeWithAuthority().getCodeSpace())) {
            return true;
        } else {
            throw new InvalidParameterValueException("gml:identifier[@codesSpace]", ap
                    .getIdentifierCodeWithAuthority().getCodeSpace());
        }

    }

}

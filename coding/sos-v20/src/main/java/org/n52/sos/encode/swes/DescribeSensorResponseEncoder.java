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
package org.n52.sos.encode.swes;

import java.util.Set;

import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorResponseEncoder extends AbstractSwesResponseEncoder<DescribeSensorResponse> {
    public DescribeSensorResponseEncoder() {
        super(SosConstants.Operations.DescribeSensor.name(), DescribeSensorResponse.class);
    }

    @Override
    protected XmlObject create(DescribeSensorResponse response) throws OwsExceptionReport {
        DescribeSensorResponseDocument doc =
                DescribeSensorResponseDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        DescribeSensorResponseType dsr = doc.addNewDescribeSensorResponse();
        dsr.setProcedureDescriptionFormat(response.getOutputFormat());
        for (SosProcedureDescription sosProcedureDescription : response.getProcedureDescriptions()) {
            SensorDescriptionType sensorDescription = dsr.addNewDescription().addNewSensorDescription();
            sensorDescription.addNewData().set(getSensorDescription(response, sosProcedureDescription ));
            if (sosProcedureDescription.isSetValidTime()) {
                XmlObject xmlObjectValidtime =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosProcedureDescription.getValidTime());
                XmlObject substitution =
                        sensorDescription
                                .addNewValidTime()
                                .addNewAbstractTimeGeometricPrimitive()
                                .substitute(GmlHelper.getGml321QnameForITime(sosProcedureDescription.getValidTime()),
                                        xmlObjectValidtime.schemaType());
                substitution.set(xmlObjectValidtime);
            }
        }
        return doc;
    }

    private XmlObject getSensorDescription(DescribeSensorResponse response, SosProcedureDescription sosProcedureDescription) throws OwsExceptionReport {
        if (sosProcedureDescription instanceof SosProcedureDescriptionUnknowType && sosProcedureDescription.isSetSensorDescriptionXmlString()) {
            return  XmlHelper.parseXmlString(sosProcedureDescription.getSensorDescriptionXmlString());
        } 
        return CodingHelper.encodeObjectToXml(response.getOutputFormat(), sosProcedureDescription);
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(SwesConstants.SWES_20_DESCRIBE_SENSOR_SCHEMA_LOCATION);
    }
}

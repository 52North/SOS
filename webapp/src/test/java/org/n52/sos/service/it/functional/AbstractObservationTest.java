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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTime;
import org.junit.rules.ErrorCollector;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.SettingValueFactory;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.it.AbstractComplianceSuiteTest;
import org.n52.sos.service.it.Client;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.InsertObservationDocument;
import net.opengis.sos.x20.InsertObservationType;
import net.opengis.sos.x20.SosInsertionMetadataType;
import net.opengis.swes.x20.InsertSensorDocument;
import net.opengis.swes.x20.InsertSensorType;

public abstract class AbstractObservationTest extends AbstractComplianceSuiteTest {
    private static final GeometryFactory GEOM_FACTORY_4326 = JTSHelper.getGeometryFactoryForSRID(4326);
    private static final GeometryFactory GEOM_FACTORY_4979 = JTSHelper.getGeometryFactoryForSRID(4979);
    private static final String APPLICATION_XML = MediaTypes.APPLICATION_XML.toString();
    protected static final String CODESPACE = "codespace";

    protected void checkObservationCount(int count, XmlObject response, ErrorCollector errors) {
        assertThat(response, is(instanceOf(GetObservationResponseDocument.class)));
        GetObservationResponseDocument document = (GetObservationResponseDocument) response;
        errors.checkThat(document.getGetObservationResponse().getObservationDataArray(), arrayWithSize(count));
    }

    protected Client pox() {
        return pox(APPLICATION_XML);
    }

    protected Client pox(String accept) {
        return getExecutor().pox()
                .contentType(APPLICATION_XML)
                .accept(accept);
    }

    protected Client kvp(Enum<?> operation) {
        return kvp(Sos2Constants.SERVICEVERSION, APPLICATION_XML, operation);
    }

    protected Client kvp(String version, String accept, Enum<?> operation) {
        return getExecutor().kvp()
                .accept(accept)
                .query(OWSConstants.RequestParams.service, SosConstants.SOS)
                .query(OWSConstants.RequestParams.version, version)
                .query(OWSConstants.RequestParams.request, operation);
    }

    protected InsertSensorDocument createInsertSensorRequest(String procedure, String offering,
            String observableProperty) throws OwsExceptionReport {
        return createInsertSensorRequest(createProcedure(procedure, offering, createObservableProperty(observableProperty)),
                observableProperty);
    }

    protected InsertSensorDocument createInsertSensorRequest(SensorML procedure, String observableProperty) throws OwsExceptionReport {
        InsertSensorDocument document = InsertSensorDocument.Factory.newInstance();
        InsertSensorType insertSensor = document.addNewInsertSensor();
        insertSensor.setService(SosConstants.SOS);
        insertSensor.setVersion(Sos2Constants.SERVICEVERSION);
        insertSensor.addObservableProperty(observableProperty);
        insertSensor.setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
        insertSensor.addNewMetadata().addNewInsertionMetadata().set(createSensorInsertionMetadata());
        insertSensor.addNewProcedureDescription().set(CodingHelper.encodeObjectToXml(SensorMLConstants.NS_SML, procedure));
        return document;
    }

    private SosInsertionMetadataType createSensorInsertionMetadata() {
        SosInsertionMetadataType sosInsertionMetadata = SosInsertionMetadataType.Factory.newInstance();
        sosInsertionMetadata.addFeatureOfInterestType(OGCConstants.UNKNOWN);
        sosInsertionMetadata.addFeatureOfInterestType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        for (String observationType : OmConstants.OBSERVATION_TYPES) {
            sosInsertionMetadata.addObservationType(observationType);
        }
        return sosInsertionMetadata;
    }

    protected SensorML createProcedure(String procedure, String offering, AbstractPhenomenon observableProperty) {
        SensorML wrapper = new SensorML();
        org.n52.sos.ogc.sensorML.System sensorML = new org.n52.sos.ogc.sensorML.System();
        wrapper.addMember(sensorML);
        sensorML.addIdentifier(new SmlIdentifier(OGCConstants.UNIQUE_ID, OGCConstants.URN_UNIQUE_IDENTIFIER, procedure));
        sensorML.addCapabilities(createOfferingCapabilities(offering));
        sensorML.addPhenomenon(observableProperty);
        wrapper.setIdentifier(new CodeWithAuthority(procedure, CODESPACE));
        return wrapper;
    }

    private SmlCapabilities createOfferingCapabilities(String offering) {
        return new SmlCapabilities(SensorMLConstants.ELEMENT_NAME_OFFERINGS,
                new SweSimpleDataRecord().addField(createOfferingField(offering)));
    }

    private SweField createOfferingField(String offering) {
        return new SweField(offering, new SweText().setValue(offering).setDefinition(OGCConstants.URN_OFFERING_ID));
    }

    protected InsertObservationDocument createInsertObservationRequest(OmObservation observation, String offering)
            throws OwsExceptionReport {
        return createInsertObservationRequest(Collections.singleton(observation), offering);
    }

    protected InsertObservationDocument createInsertObservationRequest(Collection<OmObservation> observations, String offering)
            throws OwsExceptionReport {
        InsertObservationDocument document = InsertObservationDocument.Factory.newInstance();
        InsertObservationType insertObservation = document.addNewInsertObservation();
        insertObservation.setService(SosConstants.SOS);
        insertObservation.setVersion(Sos2Constants.SERVICEVERSION);
        insertObservation.addNewOffering().setStringValue(offering);
        if (observations != null) {
            for (OmObservation observation : observations) {
                insertObservation.addNewObservation().addNewOMObservation().set(CodingHelper
                        .encodeObjectToXml(OmConstants.NS_OM_2, observation));
            }
        }
        return document;
    }

    protected OmObservation createObservation(String type, String procedure, String offering, AbstractPhenomenon observableProperty,
            AbstractSamplingFeature samplingFeature, DateTime time, Value<?> value) {
        TimeInstant resultTime = new TimeInstant(time);
        TimeInstant phenomenonTime = new TimeInstant(time);
        TimePeriod validTime = new TimePeriod(time.minusMinutes(5), time.plusMinutes(5));

        OmObservation observation = new OmObservation();
        observation.setObservationConstellation(createObservationConstellation(procedure, offering,
                type, observableProperty, samplingFeature));
        observation.setResultTime(resultTime);
        observation.setValidTime(validTime);
        observation.setValue(new SingleObservationValue<>(phenomenonTime, value));

        return observation;
    }

    private OmObservationConstellation createObservationConstellation(String procedure, String offering, String observationType,
            AbstractPhenomenon observableProperty, AbstractSamplingFeature samplingFeature) {
        OmObservationConstellation observationConstellation = new OmObservationConstellation();
        observationConstellation.setFeatureOfInterest(samplingFeature);
        observationConstellation.setObservableProperty(observableProperty);
        observationConstellation.setObservationType(observationType);
        observationConstellation.setProcedure(createProcedure(procedure, offering, observableProperty));
        return observationConstellation;
    }

    protected static AbstractSamplingFeature createFeature(String featureOfInterest, Geometry geom) {
        AbstractSamplingFeature samplingFeature = new SamplingFeature(new CodeWithAuthority(featureOfInterest));
        try {
            samplingFeature.setGeometry(geom);
        } catch (InvalidSridException e) {
            throw new IllegalArgumentException("Invalid srid", e);
        }
        return samplingFeature;
    }

    protected OmObservableProperty createObservableProperty(String observableProperty) {
        return new OmObservableProperty(observableProperty);
    }

    protected static void changeSetting(String setting, String value) {
        SettingsManager sm = SettingsManager.getInstance();
        SettingValueFactory sf = sm.getSettingFactory();
        SettingDefinition<?, ?> sd = sm.getDefinitionByKey(setting);
        SettingValue<?> sv = sf.newSettingValue(sd, value);
        try {
            sm.changeSetting(sv);
        } catch (ConfigurationException |
                 ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

    protected static class NamespaceContextImpl implements NamespaceContext {
        private final ImmutableBiMap<String, String> map = ImmutableBiMap
                .<String, String>builder()
                .put(Sos2Constants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20)
                .put(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS)
                .put(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20)
                .put(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2)
                .put(W3CConstants.NS_XSI_PREFIX, W3CConstants.NS_XSI)
                .put(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK)
                .put(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32)
                .put(SfConstants.NS_SAMS_PREFIX, SfConstants.NS_SAMS)
                .build();

        @Override
        public String getNamespaceURI(String prefix) {
            return map.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return map.inverse().get(namespaceURI);
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return Iterators.singletonIterator(getPrefix(namespaceURI));
        }
    }

    protected static Point createPoint4326(double lat, double lng) {
        return GEOM_FACTORY_4326.createPoint(new Coordinate(lng, lat));
    }

    protected static Point createPoint4979(double lat, double lng, double height) {
        return GEOM_FACTORY_4979.createPoint(new Coordinate(lng, lat, height));
    }

    protected static Point createRandomPoint4326() {
        return GEOM_FACTORY_4326.createPoint(new Coordinate(randomLng(), randomLat()));
    }

    protected static Point createRandomPoint4979() {
        return GEOM_FACTORY_4979.createPoint(new Coordinate(randomLng(), randomLat(),
                randomInRange(-10.0, 50.0, 2)));
    }

    protected static double randomLng(){
        //make test data lngs three digits for easy differentiation from lats
        double lng = randomInRange(100.0, 180.0, 6);
        //return a negative number if lng is even
        if ((int) Math.round(lng) % 2 == 0) {
            return 0 - lng;
        } else {
            return lng;
        }
    }

    protected static double randomLat(){
        //stay away from the poles because they often break software
        return randomInRange(-75.0, 75.0, 6);
    }

    protected static double randomInRange(double min, double max, int decimalPlaces){
        double unroundedValue = min + Math.random() * (max - min);
        double co = Math.pow(10, decimalPlaces);
        return Math.round(unroundedValue * co) / co;
    }
}

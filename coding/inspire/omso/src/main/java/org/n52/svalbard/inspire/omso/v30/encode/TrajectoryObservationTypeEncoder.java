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
package org.n52.svalbard.inspire.omso.v30.encode;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.n52.svalbard.inspire.omso.TrajectoryObservation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.omso.x30.TrajectoryObservationType;
import net.opengis.om.x20.OMObservationType;
import net.opengis.waterml.x20.CategoricalTimeseriesDocument;
import net.opengis.waterml.x20.CategoricalTimeseriesType;
import net.opengis.waterml.x20.DefaultCategoricalTVPMetadataType;
import net.opengis.waterml.x20.DefaultTVPCategoricalMetadataDocument;
import net.opengis.waterml.x20.DefaultTVPMeasurementMetadataDocument;
import net.opengis.waterml.x20.MeasurementTimeseriesDocument;
import net.opengis.waterml.x20.MeasurementTimeseriesType;
import net.opengis.waterml.x20.TVPDefaultMetadataPropertyType;
import net.opengis.waterml.x20.TVPMeasurementMetadataType;

/**
 * {@link Encoder} implementation for {@link TrajectoryObservation} to
 * {@link TrajectoryObservationType}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class TrajectoryObservationTypeEncoder extends AbstractOmInspireEncoder {

    private static final Set<EncoderKey> ENCODER_KEYS =
            CodingHelper.encoderKeysForElements(InspireOMSOConstants.NS_OMSO_30, TrajectoryObservation.class);

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return Collections.singletonMap(InspireOMSOConstants.NS_OMSO_30,
                (Set<String>) Sets.newHashSet(InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION));
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return super.encode(element, additionalValues);
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        encodingValues.setEncoder(this);
        super.encode(objectToEncode, outputStream, encodingValues);
    }

    @Override
    protected XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport {
        return encodeResult(sosObservation.getValue());
    }

    @Override
    protected XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport {
        if (observationValue instanceof SingleObservationValue
                && observationValue.getValue() instanceof TimeLocationValueTriple) {
            if (observationValue.getValue().getValue() instanceof QuantityValue
                    || observationValue.getValue().getValue() instanceof CountValue) {
                return createMeasurementTimeseries((AbstractObservationValue<?>) observationValue);
            } else if (observationValue.getValue().getValue() instanceof CategoryValue) {
                return createCategoricalTimeseries((AbstractObservationValue<?>) observationValue);
            } else {
                // TODO throw exception
            }
        } else if (observationValue instanceof MultiObservationValues) {
            if (observationValue.getValue() instanceof TLVTValue) {
                TimeLocationValueTriple value = (TimeLocationValueTriple) ((TLVTValue) observationValue.getValue())
                        .getValue().iterator().next();
                if (value.getValue() instanceof QuantityValue || value.getValue() instanceof CountValue) {
                    return createMeasurementTimeseries((AbstractObservationValue<?>) observationValue);
                } else if (value.getValue() instanceof CategoryValue) {
                    return createCategoricalTimeseries((AbstractObservationValue<?>) observationValue);
                } else {
                    // TODO throw exception
                }
            }
        }
        return null;
    }

    @Override
    protected String getObservationType() {
        return InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION;
    }

    protected OMObservationType createOmObservationType() {
        return TrajectoryObservationType.Factory.newInstance(getXmlOptions());
    }

    /**
     * Encode {@link AbstractObservationValue} to
     * {@link MeasurementTimeseriesDocument}
     * 
     * @param observationValue
     *            The {@link AbstractObservationValue} to encode
     * @return The encoded {@link AbstractObservationValue}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject createMeasurementTimeseries(AbstractObservationValue<?> observationValue)
            throws OwsExceptionReport {
        MeasurementTimeseriesDocument measurementTimeseriesDoc = MeasurementTimeseriesDocument.Factory.newInstance();
        MeasurementTimeseriesType measurementTimeseries = measurementTimeseriesDoc.addNewMeasurementTimeseries();
        if (!observationValue.isSetObservationID()) {
            observationValue.setObservationID(JavaHelper.generateID(observationValue.toString()));
        }
        measurementTimeseries.setId("timeseries." + observationValue.getObservationID());
        measurementTimeseries.addNewMetadata().addNewTimeseriesMetadata().addNewTemporalExtent()
                .setHref("#" + observationValue.getPhenomenonTime().getGmlId());

        TVPDefaultMetadataPropertyType xbMetaComponent = measurementTimeseries.addNewDefaultPointMetadata();

        DefaultTVPMeasurementMetadataDocument xbDefMeasureMetaComponent =
                DefaultTVPMeasurementMetadataDocument.Factory.newInstance();
        TVPMeasurementMetadataType defaultTVPMeasurementMetadata =
                xbDefMeasureMetaComponent.addNewDefaultTVPMeasurementMetadata();
        defaultTVPMeasurementMetadata.addNewInterpolationType()
                .setHref("http://www.opengis.net/def/timeseriesType/WaterML/2.0/continuous");

        xbDefMeasureMetaComponent.getDefaultTVPMeasurementMetadata().getInterpolationType().setTitle("Instantaneous");
        String unit = null;
        if (observationValue instanceof SingleObservationValue) {
            SingleObservationValue<?> singleObservationValue = (SingleObservationValue<?>) observationValue;
            unit = singleObservationValue.getValue().getUnit();
            if (observationValue.getValue() instanceof TimeLocationValueTriple) {

                measurementTimeseries.addNewPoint().addNewMeasurementTVP()
                        .set(encodeTLVT((TimeLocationValueTriple) observationValue.getValue()));
            }
        } else if (observationValue instanceof MultiObservationValues) {
            MultiObservationValues<?> multiObservationValue = (MultiObservationValues<?>) observationValue;
            if (multiObservationValue.getValue() instanceof TLVTValue) {
                TLVTValue tlvtValue = (TLVTValue) multiObservationValue.getValue();
                List<TimeLocationValueTriple> timeLocationValueTriples = tlvtValue.getValue();
                unit = tlvtValue.getUnit();
                int counter = 0;
                for (TimeLocationValueTriple timeLocationValueTriple : timeLocationValueTriples) {
                    timeLocationValueTriple.getLocation()
                            .setUserData(getUserObject(observationValue.getObservationID(), counter));
                    measurementTimeseries.addNewPoint().addNewMeasurementTVP()
                            .set(encodeTLVT(timeLocationValueTriple));
                    counter++;
                }
            } else {
                // TODO throw exception
            }
        }
        if (unit != null && !unit.isEmpty()) {
            defaultTVPMeasurementMetadata.addNewUom().setCode(unit);
        }

        xbMetaComponent.set(xbDefMeasureMetaComponent);
        return measurementTimeseriesDoc;
    }

    /**
     * Encode {@link TimeLocationValueTriple}
     * 
     * @param value
     *            The {@link TimeLocationValueTriple} to encode
     * @return Encoded {@link TimeLocationValueTriple}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject encodeTLVT(TimeLocationValueTriple value) throws OwsExceptionReport {
        return encodeInspireOMSO(value);
    }

    /**
     * Encode {@link AbstractObservationValue} to
     * {@link CategoricalTimeseriesDocument}
     * 
     * @param observationValue
     *            The {@link AbstractObservationValue} to encode
     * @return The encoded {@link AbstractObservationValue}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject createCategoricalTimeseries(AbstractObservationValue<?> observationValue)
            throws OwsExceptionReport {
        CategoricalTimeseriesDocument categoricalTimeseriesDoc = CategoricalTimeseriesDocument.Factory.newInstance();
        CategoricalTimeseriesType categoricalTimeseries = categoricalTimeseriesDoc.addNewCategoricalTimeseries();
        categoricalTimeseries.setId("timeseries." + observationValue.getObservationID());
        categoricalTimeseries.addNewMetadata().addNewTimeseriesMetadata().addNewTemporalExtent()
                .setHref("#" + observationValue.getPhenomenonTime().getGmlId());

        TVPDefaultMetadataPropertyType xbMetaComponent = categoricalTimeseries.addNewDefaultPointMetadata();

        DefaultTVPCategoricalMetadataDocument xbDefCateMetaComponent =
                DefaultTVPCategoricalMetadataDocument.Factory.newInstance();
        DefaultCategoricalTVPMetadataType defaultTVPCateMetadata =
                xbDefCateMetaComponent.addNewDefaultTVPCategoricalMetadata();
        String unit = null;
        if (observationValue instanceof SingleObservationValue) {
            SingleObservationValue<?> singleObservationValue = (SingleObservationValue<?>) observationValue;
            unit = singleObservationValue.getValue().getUnit();
            if (observationValue.getValue() instanceof TimeLocationValueTriple) {
                categoricalTimeseries.addNewPoint().addNewCategoricalTVP()
                        .set(encodeTLVT((TimeLocationValueTriple) observationValue.getValue()));
            }
        } else if (observationValue instanceof MultiObservationValues) {
            MultiObservationValues<?> multiObservationValue = (MultiObservationValues<?>) observationValue;
            if (multiObservationValue.getValue() instanceof TLVTValue) {
                TLVTValue tlvtValue = (TLVTValue) multiObservationValue.getValue();
                List<TimeLocationValueTriple> timeLocationValueTriples = tlvtValue.getValue();
                unit = tlvtValue.getUnit();
                int counter = 0;
                for (TimeLocationValueTriple timeLocationValueTriple : timeLocationValueTriples) {
                    timeLocationValueTriple.getLocation()
                            .setUserData(getUserObject(observationValue.getObservationID(), counter));
                    categoricalTimeseries.addNewPoint().addNewCategoricalTVP()
                            .set(encodeTLVT(timeLocationValueTriple));
                    counter++;
                }
            } else {
                // TODO throw exception
            }
        }
        if (unit != null && !unit.isEmpty()) {
            defaultTVPCateMetadata.setCodeSpace(unit);
        }

        xbMetaComponent.set(xbDefCateMetaComponent);
        return categoricalTimeseriesDoc;
    }

    /**
     * @param observationID
     * @param counter
     * @return
     */
    private Object getUserObject(String observationID, int counter) {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(1);
        map.put(HelperValues.GMLID.name(), observationID + "_" + counter);
        return map;
    }

    protected static XmlObject encodeInspireOMSO(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, o);
    }

    protected static XmlObject encodeInspireOMSO(Object o, Map<HelperValues, String> helperValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, o, helperValues);
    }

}

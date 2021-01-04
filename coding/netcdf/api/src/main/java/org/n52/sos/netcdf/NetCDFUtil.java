/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.netcdf.data.dataset.IdentifierDatasetSensor;
import org.n52.sos.netcdf.data.dataset.TimeSeriesProfileSensorDataset;
import org.n52.sos.netcdf.data.dataset.TimeSeriesSensorDataset;
import org.n52.sos.netcdf.data.dataset.TrajectoryProfileSensorDataset;
import org.n52.sos.netcdf.data.dataset.TrajectorySensorDataset;
import org.n52.sos.netcdf.data.subsensor.BinProfileSubSensor;
import org.n52.sos.netcdf.data.subsensor.PointProfileSubSensor;
import org.n52.sos.netcdf.data.subsensor.SubSensor;
import org.n52.sos.netcdf.feature.FeatureUtil;
import org.n52.sos.netcdf.om.NetCDFObservation;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

import ucar.nc2.constants.CF;

/**
 * Utility class for netCDF encoding.
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface NetCDFUtil {

    GeometryHandler getGeometryHandler();

    NetcdfHelper getNetcdfHelper();

    /**
     * Organizes OmObservation collection into a list of NetCDFObservation
     * blocks, each of which contain a single feature type
     *
     * @param omObservations
     *            The collection of observations to transform
     * @return List&lt;NetCDFObservation&gt; ready for encoding
     * @throws EncodingException
     *             if an error occurs
     */
    default List<NetCDFObservation> createNetCDFSosObservations(ObservationStream omObservations)
            throws EncodingException, OwsExceptionReport {
        // the main map of observation value strings by asset, time, phenomenon,
        // and subsensor (height, profile bin, etc)
        Map<String, Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>>> obsValuesMap = new HashMap<>();

        SetMultimap<String, OmObservableProperty> sensorPhens = HashMultimap.create();
        Map<String, AbstractFeature> sensorProcedure = Maps.newHashMap();
        // Map<StationAsset,TimePeriod> stationPeriodMap = new
        // HashMap<StationAsset,TimePeriod>();

        // maps to keep track of unique dimension values by sensor (these may or
        // may not vary, determining the feature type)
        SetMultimap<String, Double> sensorLngs = HashMultimap.create();
        SetMultimap<String, Double> sensorLats = HashMultimap.create();
        SetMultimap<String, Double> sensorHeights = HashMultimap.create();

        while (omObservations.hasNext()) {
            OmObservation sosObs = omObservations.next();
            if (sosObs.getValue() instanceof StreamingValue<?>) {
                StreamingValue<?> streaming = (StreamingValue<?>) sosObs.getValue();
                while (streaming.hasNext()) {
                    processObservation(streaming.next(), sensorPhens, sensorProcedure, sensorLngs, sensorLats,
                            sensorHeights, obsValuesMap);
                }
            } else {
                processObservation(sosObs, sensorPhens, sensorProcedure, sensorLngs, sensorLats, sensorHeights,
                        obsValuesMap);
            }
        }

        // now we know about each station's dimensions, sort into CF feature
        // types

        // sampling time periods
        // TimePeriod pointSamplingTimePeriod = new TimePeriod();
        TimePeriod timeSeriesSamplingTimePeriod = new TimePeriod();
        // TimePeriod profileSamplingTimePeriod = new TimePeriod();
        TimePeriod timeSeriesProfileSamplingTimePeriod = new TimePeriod();
        TimePeriod trajectorySamplingTimePeriod = new TimePeriod();
        TimePeriod trajectoryProfileSamplingTimePeriod = new TimePeriod();

        // station datasets
        // Map<SensorAsset,PointSensorDataset> pointSensorDatasets =
        // new HashMap<SensorAsset,PointSensorDataset>();
        Map<String, TimeSeriesSensorDataset> timeSeriesSensorDatasets = new HashMap<>();
        // Map<SensorAsset,ProfileSensorDataset> profileSensorDatasets =
        // new HashMap<SensorAsset,ProfileSensorDataset>();
        Map<String, TimeSeriesProfileSensorDataset> timeSeriesProfileSensorDatasets = new HashMap<>();
        Map<String, TrajectorySensorDataset> trajectorySensorDatasets = new HashMap<>();
        Map<String, TrajectoryProfileSensorDataset> trajectoryProfileSensorDatasets = new HashMap<>();

        // phenomena
        // Set<OmObservableProperty> pointPhenomena = new
        // HashSet<OmObservableProperty>();
        Set<OmObservableProperty> timeSeriesPhenomena = new HashSet<>();
        // Set<OmObservableProperty> profilePhenomena = new
        // HashSet<OmObservableProperty>();
        Set<OmObservableProperty> timeSeriesProfilePhenomena = new HashSet<>();
        Set<OmObservableProperty> trajectoryPhenomena = new HashSet<>();
        Set<OmObservableProperty> trajectoryProfilePhenomena = new HashSet<>();

        // envelopes
        // Envelope pointEnvelope = new Envelope();
        Envelope timeSeriesEnvelope = new Envelope();
        // Envelope profileEnvelope = new Envelope();
        Envelope timeSeriesProfileEnvelope = new Envelope();
        Envelope trajectoryEnvelope = new Envelope();
        Envelope trajectoryProfileEnvelope = new Envelope();

        for (Map.Entry<String, Map<Time, Map<OmObservableProperty,
                Map<SubSensor, Value<?>>>>> obsValuesEntry : obsValuesMap
                .entrySet()) {
            IdentifierDatasetSensor datasetSensor = new IdentifierDatasetSensor(obsValuesEntry.getKey());
            String sensor = datasetSensor.getSensorIdentifier();
            Set<Time> sensorTimes = obsValuesEntry.getValue().keySet();

            int lngCount = sensorLngs.get(sensor).size();
            int latCount = sensorLats.get(sensor).size();
            int heightCount = sensorHeights.get(sensor).size();
            // int timeCount = sensorTimes.size();

            boolean locationVaries = lngCount > 0 && latCount > 0 && (lngCount > 1 || latCount > 1);
            boolean heightVaries = heightCount > 1;
            // boolean timeVaries = timeCount > 1;

            // set static dimension values where applicable
            Double staticLng = null;
            Double staticLat = null;
            Double staticHeight = null;
            // Time staticTime = null;
            if (!locationVaries) {
                if (!sensorLngs.get(sensor).isEmpty()) {
                    staticLng = sensorLngs.get(sensor).iterator().next();
                }
                if (!sensorLats.get(sensor).isEmpty()) {
                    staticLat = sensorLats.get(sensor).iterator().next();
                }
            }
            if (!heightVaries) {
                if (!sensorHeights.get(sensor).isEmpty()) {
                    staticHeight = sensorHeights.get(sensor).iterator().next();
                }
            }
            // if( !timeVaries ){
            // if( !sensorTimes.isEmpty() ){
            // staticTime = sensorTimes.iterator().next();
            // }
            // }

            // put data on applicable feature type maps
            // if( !locationVaries && !heightVaries && !timeVaries ){
            // //point
            // pointSamplingTimePeriod.extendToContain( sensorTimes );
            // pointSensorDatasets.put( sensor, new PointSensorDataset( sensor,
            // staticLng, staticLat,
            // staticHeight, staticTime, obsValuesEntry.getValue() ) );
            // pointPhenomena.addAll( sensorPhens.get( sensor ) );
            // if( staticLng != null && staticLat != null ){
            // pointEnvelope.expandToInclude( staticLng, staticLat );
            // }
            // pointStationPoints.putAll( station, stationPoints.get( station )
            // );
            // if( sensorHeights.get( sensor ) != null ){
            // pointSensorHeights.putAll( sensor, sensorHeights.get( sensor ) );
            // }
            // } else if( !locationVaries && !heightVaries && timeVaries){
            if (!locationVaries && !heightVaries) {
                // time series
                timeSeriesSamplingTimePeriod.extendToContain(sensorTimes);
                timeSeriesSensorDatasets.put(sensor, new TimeSeriesSensorDataset(datasetSensor, staticLng, staticLat,
                        staticHeight, obsValuesEntry.getValue(), sensorProcedure.get(sensor)));
                timeSeriesPhenomena.addAll(sensorPhens.get(sensor));
                if (staticLng != null && staticLat != null) {
                    timeSeriesEnvelope.expandToInclude(staticLng, staticLat);
                }
            } else if (!locationVaries && heightVaries) {
                // time series profile
                timeSeriesProfileSamplingTimePeriod.extendToContain(sensorTimes);
                timeSeriesProfileSensorDatasets.put(sensor, new TimeSeriesProfileSensorDataset(datasetSensor,
                        staticLng, staticLat, obsValuesEntry.getValue(), sensorProcedure.get(sensor)));
                timeSeriesProfilePhenomena.addAll(sensorPhens.get(sensor));
                if (staticLng != null && staticLat != null) {
                    timeSeriesProfileEnvelope.expandToInclude(staticLng, staticLat);
                }
            } else if (locationVaries && !heightVaries) {
                // trajectory
                trajectorySamplingTimePeriod.extendToContain(sensorTimes);
                trajectorySensorDatasets.put(sensor, new TrajectorySensorDataset(datasetSensor, staticHeight,
                        obsValuesEntry.getValue(), sensorProcedure.get(sensor)));
                trajectoryPhenomena.addAll(sensorPhens.get(sensor));
                expandEnvelopeToInclude(trajectoryEnvelope, sensorLngs.get(sensor), sensorLats.get(sensor));
            } else if (locationVaries && heightVaries) {
                // trajectory profile
                trajectoryProfileSamplingTimePeriod.extendToContain(sensorTimes);
                trajectoryProfileSensorDatasets.put(sensor, new TrajectoryProfileSensorDataset(datasetSensor,
                        obsValuesEntry.getValue(), sensorProcedure.get(sensor)));
                trajectoryProfilePhenomena.addAll(sensorPhens.get(sensor));
                expandEnvelopeToInclude(trajectoryProfileEnvelope, sensorLngs.get(sensor), sensorLats.get(sensor));
            }
        }

        // build NetCDFObservations
        List<NetCDFObservation> iSosObsList =
                new ArrayList<>(timeSeriesSensorDatasets.size() + timeSeriesProfileSensorDatasets.size()
                        + trajectorySensorDatasets.size() + trajectoryProfileSensorDatasets.size());

        // timeSeries
        if (timeSeriesSensorDatasets.size() > 0) {
            iSosObsList.add(new NetCDFObservation(CF.FeatureType.timeSeries, timeSeriesSamplingTimePeriod,
                    timeSeriesSensorDatasets, timeSeriesPhenomena, timeSeriesEnvelope));
        }

        // time series profile
        if (timeSeriesProfileSensorDatasets.size() > 0) {
            iSosObsList
                    .add(new NetCDFObservation(CF.FeatureType.timeSeriesProfile, timeSeriesProfileSamplingTimePeriod,
                            timeSeriesProfileSensorDatasets, timeSeriesProfilePhenomena, timeSeriesProfileEnvelope));
        }

        // trajectory
        if (trajectorySensorDatasets.size() > 0) {
            iSosObsList.add(new NetCDFObservation(CF.FeatureType.trajectory, trajectorySamplingTimePeriod,
                    trajectorySensorDatasets, trajectoryPhenomena, trajectoryEnvelope));
        }

        // trajectoryProfile
        if (trajectoryProfileSensorDatasets.size() > 0) {
            iSosObsList
                    .add(new NetCDFObservation(CF.FeatureType.trajectoryProfile, trajectoryProfileSamplingTimePeriod,
                            trajectoryProfileSensorDatasets, trajectoryProfilePhenomena, trajectoryProfileEnvelope));
        }
        return iSosObsList;
    }

    default void processObservation(OmObservation sosObs, SetMultimap<String, OmObservableProperty> sensorPhens,
            Map<String, AbstractFeature> sensorProcedure, SetMultimap<String, Double> sensorLngs,
            SetMultimap<String, Double> sensorLats, SetMultimap<String, Double> sensorHeights,
            Map<String, Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>>> obsValuesMap)
            throws EncodingException {

        OmObservationConstellation obsConst = sosObs.getObservationConstellation();

        // first, resolve the procId to an asset type
        String sensor = obsConst.getProcedure().getIdentifier();
        if (!sensorProcedure.containsKey(sensor)) {
            sensorProcedure.put(sensor, obsConst.getProcedure());
        }

        AbstractPhenomenon absPhen = obsConst.getObservableProperty();
        Map<String, OmObservableProperty> phenomenaMap = new HashMap<>();
        if (absPhen instanceof OmCompositePhenomenon) {
            for (OmObservableProperty phen : ((OmCompositePhenomenon) absPhen).getPhenomenonComponents()) {
                // TODO should the unit be set like this? seems sketchy
                if (phen.getUnit() == null && sosObs.getValue() != null && sosObs.getValue().getValue() != null
                        && sosObs.getValue().getValue().getUnit() != null) {
                    phen.setUnit(sosObs.getValue().getValue().getUnit());
                }
                phenomenaMap.put(phen.getIdentifier(), phen);
            }
        } else {
            OmObservableProperty phen = (OmObservableProperty) absPhen;
            // TODO should the unit be set like this? seems sketchy
            if (phen.getUnit() == null && sosObs.getValue() != null && sosObs.getValue().getValue() != null
                    && sosObs.getValue().getValue().getUnit() != null) {
                phen.setUnit(sosObs.getValue().getValue().getUnit());
            }
            phenomenaMap.put(phen.getIdentifier(), phen);
        }
        List<OmObservableProperty> phenomena = new ArrayList<>(phenomenaMap.values());
        sensorPhens.putAll(sensor, phenomena);

        // get foi
        AbstractFeature aFoi = obsConst.getFeatureOfInterest();
        if (!(aFoi instanceof AbstractSamplingFeature)) {
            throw new EncodingException("Encountered a feature which isn't a SamplingFeature");
        }
        AbstractSamplingFeature foi = (AbstractSamplingFeature) aFoi;

        for (Point point : FeatureUtil.getFeaturePoints(foi)) {
            try {
                // TODO is this correct?
                Point p = (Point) getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(point);
                sensorLngs.put(sensor, p.getX());
                sensorLats.put(sensor, p.getY());
            } catch (OwsExceptionReport e) {
                throw new EncodingException("Exception while normalizing feature coordinate axis order.", e);
            }
        }
        Set<Double> featureHeights = FeatureUtil.getFeatureHeights(foi);
        sensorHeights.putAll(sensor, featureHeights);

        String phenId = obsConst.getObservableProperty().getIdentifier();
        ObservationValue<?> iObsValue = sosObs.getValue();
        if (!(iObsValue instanceof SingleObservationValue)) {
            throw new EncodingException("Only SingleObservationValues are supported.");
        }
        SingleObservationValue<?> singleObsValue = (SingleObservationValue<?>) iObsValue;
        Time obsTime = singleObsValue.getPhenomenonTime();

        // TODO Quality

        Value<?> obsValue = singleObsValue.getValue();
        if (!(obsValue instanceof QuantityValue)) {
            throw new EncodingException("Only QuantityValues are supported.");
        }
        QuantityValue quantityValue = (QuantityValue) obsValue;

        // axes shouldn't be composite phenomena
        if (phenomena.size() == 1) {
            OmObservableProperty phenomenon = phenomena.get(0);
            // add dimensional values to procedure dimension tracking maps
            if (isLng(phenomenon.getIdentifier())) {
                sensorLngs.get(sensor).add(quantityValue.getValue().doubleValue());
            }

            if (isLat(phenomenon.getIdentifier())) {
                sensorLats.get(sensor).add(quantityValue.getValue().doubleValue());
            }

            if (isZ(phenomenon.getIdentifier())) {
                Double zValue = quantityValue.getValue().doubleValue();
                sensorHeights.get(sensor).add(zValue);
            }
        }

        // check for samplingGeometry in observation
        if (sosObs.isSetParameter()) {
            if (sosObs.isSetHeightDepthParameter()) {
                if (sosObs.isSetHeightParameter()) {
                    sensorHeights.get(sensor).add(sosObs.getHeightParameter().getValue().getValue().doubleValue());
                } else if (sosObs.isSetDepthParameter()) {
                    sensorHeights.get(sensor).add(sosObs.getDepthParameter().getValue().getValue().doubleValue());
                }
            }
            if (hasSamplingGeometry(sosObs)) {
                Geometry geometry = getSamplingGeometryGeometry(sosObs);
                Set<Point> points = FeatureUtil.getPoints(geometry);
                for (Point point : points) {
                    try {
                        Point p = (Point) getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(point);
                        sensorLngs.put(sensor, p.getX());
                        sensorLats.put(sensor, p.getY());
                    } catch (OwsExceptionReport e) {
                        throw new EncodingException(
                                "Exception while normalizing sampling geometry coordinate axis order.");
                    }
                }
                sensorHeights.putAll(sensor, FeatureUtil.getHeights(points));
            }
        }

        // get the sensor's data map
        Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> sensorObsMap = obsValuesMap.get(sensor);
        if (sensorObsMap == null) {
            sensorObsMap = new HashMap<>();
            obsValuesMap.put(sensor, sensorObsMap);
        }

        // get the map of the asset's phenomena by time
        Map<OmObservableProperty, Map<SubSensor, Value<?>>> obsPropMap = sensorObsMap.get(obsTime);
        if (obsPropMap == null) {
            obsPropMap = new HashMap<>();
            sensorObsMap.put(obsTime, obsPropMap);
        }

        OmObservableProperty phen = phenomenaMap.get(phenId);
        Map<SubSensor, Value<?>> subSensorMap = obsPropMap.get(phen);
        if (subSensorMap == null) {
            subSensorMap = new HashMap<>();
            obsPropMap.put(phen, subSensorMap);
        }

        // add obs value to subsensor map (null subsensors are ok)
        if (sosObs.isSetParameter() && hasSamplingGeometry(sosObs)) {
            subSensorMap.put(createSubSensor(sensor, getSamplingGeometryGeometry(sosObs)), obsValue);
        } else {
            subSensorMap.put(createSubSensor(sensor, foi), obsValue);
        }
    }

    default void expandEnvelopeToInclude(Envelope env, Set<Double> lngs, Set<Double> lats) {
        lngs.stream().forEach(lng -> env.expandToInclude(lng, env.getMinY()));
        lats.stream().forEach(lat -> env.expandToInclude(env.getMinX(), lat));
    }

    default Envelope createEnvelope(Collection<OmObservation> observationCollection) {
        Envelope envelope = null;

        for (OmObservation sosObservation : observationCollection) {
            sosObservation.getObservationConstellation().getFeatureOfInterest();
            AbstractSamplingFeature samplingFeature =
                    (AbstractSamplingFeature) sosObservation.getObservationConstellation().getFeatureOfInterest();
            if (samplingFeature != null && samplingFeature.getGeometry() != null) {
                if (envelope == null) {
                    envelope = samplingFeature.getGeometry().getEnvelopeInternal();
                } else {
                    envelope.expandToInclude(samplingFeature.getGeometry().getEnvelopeInternal());
                }
            }
        }
        return envelope;
    }

    default Envelope swapEnvelopeAxisOrder(Envelope envelope) {
        if (envelope == null) {
            return null;
        }
        return new Envelope(envelope.getMinY(), envelope.getMaxY(), envelope.getMinX(), envelope.getMaxX());
    }

    // default void checkSrid( int srid, Logger logger ) throws
    // InvalidParameterValueException{
    // if( !Ioos52nConstants.ALLOWED_EPSGS.contains( srid ) ){
    // throw new InvalidParameterValueException("EPSG", Integer.toString( srid )
    // );
    // }
    // }

    default SubSensor createSubSensor(String sensor, AbstractSamplingFeature foi) {
        // return null if sensor or station id is same as foi
        if (sensor.equals(foi.getIdentifierCodeWithAuthority().getValue())) {
            return null;
        }
        return createSubSensor(sensor, foi.getGeometry());
    }

    // default void checkSrid( int srid, Logger logger ) throws
    // InvalidParameterValueException{
    // if( !Ioos52nConstants.ALLOWED_EPSGS.contains( srid ) ){
    // throw new InvalidParameterValueException("EPSG", Integer.toString( srid )
    // );
    // }
    // }

    // default void checkSrid( int srid, Logger logger ) throws
    // InvalidParameterValueException{
    // if( !Ioos52nConstants.ALLOWED_EPSGS.contains( srid ) ){
    // throw new InvalidParameterValueException("EPSG", Integer.toString( srid )
    // );
    // }
    // }

    default SubSensor createSubSensor(String sensor, Geometry geom) {
        SubSensor subSensor = null;
        if (geom instanceof Point) {
            Point point = (Point) geom;
            // profile height
            if (!Double.isNaN(point.getCoordinate().getZ())) {
                subSensor = new PointProfileSubSensor(point.getCoordinate().getZ());
            } else {
                subSensor = new PointProfileSubSensor(0.0);
            }
        } else if (geom instanceof LineString) {
            LineString lineString = (LineString) geom;
            // profile bin
            if (lineString.getNumPoints() == 2) {
                Point topPoint = lineString.getPointN(0);
                Point bottomPoint = lineString.getPointN(1);

                if (FeatureUtil.equal2d(topPoint, bottomPoint) && !Double.isNaN(topPoint.getCoordinate().getZ())
                        && !Double.isNaN(bottomPoint.getCoordinate().getZ())) {
                    double topHeight = Math.max(topPoint.getCoordinate().getZ(), bottomPoint.getCoordinate().getZ());
                    double bottomHeight =
                            Math.min(topPoint.getCoordinate().getZ(), bottomPoint.getCoordinate().getZ());
                    subSensor = new BinProfileSubSensor(topHeight, bottomHeight);
                }
            }
        }
        return subSensor;
    }

    default boolean isLng(String phenomenon) {
        return getNetcdfHelper().getLatitude().contains(phenomenon.toLowerCase(Locale.ROOT));
    }

    default boolean isLat(String phenomenon) {
        return getNetcdfHelper().getLongitude().contains(phenomenon.toLowerCase(Locale.ROOT));
    }

    default boolean isZ(String phenomenon) {
        return getNetcdfHelper().getZ().contains(phenomenon.toLowerCase(Locale.ROOT));
    }

    default boolean hasSamplingGeometry(OmObservation sosObs) {
        return getSamplingGeometryGeometry(sosObs) != null;
    }

    default Geometry getSamplingGeometryGeometry(OmObservation sosObs) {
        for (NamedValue<?> parameter : sosObs.getParameter()) {
            if (parameter.isSetName() && parameter.getName().isSetHref()
                    && OmConstants.PARAM_NAME_SAMPLING_GEOMETRY.equals(parameter.getName().getHref())
                    && parameter.isSetValue() && parameter.getValue() instanceof GeometryValue
                    && parameter.getValue().isSetValue()) {
                return (Geometry) parameter.getValue().getValue();
            }
        }
        return null;
    }

}

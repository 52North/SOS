/*
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
package org.n52.sos.encode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.request.handler.OperationHandler;
import org.n52.iceland.request.handler.OperationHandlerRepository;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.iso.CodeList.CiRoleCodes;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsOnlineResource;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.SmlContact;
import org.n52.shetland.ogc.sensorML.SmlResponsibleParty;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifierPredicates;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifierPredicates;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.BinaryAttachmentResponse;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.ds.AbstractDescribeSensorHandler;
import org.n52.sos.netcdf.Nc4ForceTimeChunkingStategy;
import org.n52.sos.netcdf.NetCDFUtil;
import org.n52.sos.netcdf.NetcdfConstants;
import org.n52.sos.netcdf.NetcdfHelper;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
import org.n52.sos.netcdf.data.dataset.StaticLocationDataset;
import org.n52.sos.netcdf.data.subsensor.BinProfileSubSensor;
import org.n52.sos.netcdf.data.subsensor.ProfileSubSensor;
import org.n52.sos.netcdf.data.subsensor.SubSensor;
import org.n52.sos.netcdf.om.NetCDFObservation;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;

import com.axiomalaska.cf4j.CFStandardNames;
import com.axiomalaska.cf4j.constants.ACDDConstants;
import com.axiomalaska.cf4j.constants.CFConstants;
import com.axiomalaska.cf4j.constants.NODCConstants;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.CDMNode;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.CF;
import ucar.nc2.jni.netcdf.Nc4Iosp;

/**
 * Abstract class of {@link ObservationEncoder} for netCDF encoding.
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public abstract class AbstractNetcdfEncoder
        implements ObservationEncoder<BinaryAttachmentResponse, Object>, NetCDFUtil {

    private final Set<SupportedType> SUPPORTED_TYPES =
            ImmutableSet.<SupportedType>builder().add(OmConstants.OBS_TYPE_TRUTH_OBSERVATION_TYPE).build();

    private final Set<String> CONFORMANCE_CLASSES = ImmutableSet
            .of("http://www.opengis.net/spec/OMXML/1.0/conf/measurement");

    private OwsServiceMetadataRepository serviceMetadataRepository;
    private OperationHandlerRepository operationHandlerRepository;
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;
    private GeometryHandler geometryHandler;
    private NetcdfHelper netcdfHelper;

    public AbstractNetcdfEncoder() {

    }

    @Inject
    public void setProcedureDescriptionFormatRepository(
            ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository) {
        this.procedureDescriptionFormatRepository = procedureDescriptionFormatRepository;
    }

    @Inject
    public void setOperationHandlerRepository(OperationHandlerRepository operationHandlerRepository) {
        this.operationHandlerRepository = operationHandlerRepository;
    }

    @Inject
    public void setServiceMetadataRepository(OwsServiceMetadataRepository serviceMetadataRepository) {
        this.serviceMetadataRepository = serviceMetadataRepository;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Override
    public NetcdfHelper getNetcdfHelper() {
        return netcdfHelper;
    }

    @Inject
    public void setNetcdfHelper(NetcdfHelper netcdfHelper) {
        this.netcdfHelper = netcdfHelper;
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return false;
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return false;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public Map<String, Set<SupportedType>> getSupportedResponseFormatObservationTypes() {
        return Collections.singletonMap(NetcdfConstants.CONTENT_TYPE_NETCDF.toString(), getSupportedTypes());
    }

    @Override
    public BinaryAttachmentResponse encode(Object element) throws EncodingException {
        return encode(element, EncodingContext.empty());
    }

    @Override
    public BinaryAttachmentResponse encode(Object objectToEncode, EncodingContext additionalValues)
            throws EncodingException {
        if (objectToEncode instanceof AbstractObservationResponse) {
            AbstractObservationResponse aor = (AbstractObservationResponse) objectToEncode;
            Version version = getVersion(aor);
            return encodeGetObsResponse(aor.getObservationCollection(), version);
        }
        throw new UnsupportedEncoderInputException(this, objectToEncode);
    }

    private Version getVersion(AbstractObservationResponse aor) {
        MediaType contentType = getBestFitContentType(aor);
        if (contentType != null && contentType.hasParameter(NetcdfConstants.PARAM_VERSION)) {
            Collection<String> parameter = contentType.getParameter(NetcdfConstants.PARAM_VERSION);
            if (parameter.contains("3")) {
                return Version.netcdf3;
            } else if (parameter.contains("4")) {
                return Version.netcdf4;
            }
        }
        return getDefaultVersion();
    }

    protected Version getDefaultVersion() {
        return getNetcdfHelper().getNetcdfVersion();
    }

    private MediaType getBestFitContentType(AbstractObservationResponse aor) {
        MediaType responseFormatContentType = null;
        MediaType contentType = null;
        if (aor.isSetResponseFormat()) {
            try {
                responseFormatContentType = MediaType.parse(aor.getResponseFormat());
            } catch (Exception e) {
                // nothing to do
            }
        }
        if (aor.isSetContentType()) {
            contentType = aor.getContentType();
        }
        if (responseFormatContentType != null && contentType != null) {
            if (responseFormatContentType.isCompatible(contentType)) {
                return responseFormatContentType;
            } else {
                if (getContentType().isCompatible(responseFormatContentType.withoutParameters())) {
                    return responseFormatContentType;
                } else if (getContentType().isCompatible(contentType.withoutParameters())) {
                    return responseFormatContentType;
                }
            }
        } else if (responseFormatContentType == null && contentType != null
                && getContentType().isCompatible(contentType.withoutParameters())) {
            return contentType;
        } else if (responseFormatContentType != null && contentType == null
                && getContentType().isCompatible(responseFormatContentType.withoutParameters())) {
            return responseFormatContentType;
        }
        return null;
    }

    private BinaryAttachmentResponse encodeGetObsResponse(ObservationStream sosObservationCollection, Version version)
            throws EncodingException {
        List<NetCDFObservation> netCDFSosObsList;
        try {
            netCDFSosObsList = createNetCDFSosObservations(sosObservationCollection);
        } catch (OwsExceptionReport ex) {
            throw new  EncodingException(ex);
        }

        if (netCDFSosObsList.isEmpty()) {
            throw new EncodingException("No feature types to encode.");
        }

        if (getNetcdfHelper().getNetcdfVersion().isNetdf4format() && !Nc4Iosp.isClibraryPresent()) {
            throw new EncodingException("Can't encode to netCDF because the native netCDF4 C library isn't installed. "
                            + "See http://www.unidata.ucar.edu/software/netcdf/docs/winbin.html");
        }

        try {
            return encodeNetCDFObsToNetcdf(netCDFSosObsList, version);
        } catch (IOException ex) {
            throw new EncodingException(ex);
        }
    }

    protected abstract BinaryAttachmentResponse encodeNetCDFObsToNetcdf(List<NetCDFObservation> netCDFSosObsList,
            Version version) throws EncodingException, IOException;

    protected abstract void addProfileSpecificGlobalAttributes(NetcdfFileWriter writer,
            AbstractSensorDataset sensorDataset) throws EncodingException;

    protected NetcdfFileWriter getNetcdfFileWriter(File netcdfFile) throws IOException {
        return getNetcdfFileWriter(netcdfFile, getNetcdfHelper().getNetcdfVersion());
    }

    protected NetcdfFileWriter getNetcdfFileWriter(File netcdfFile, Version version) throws IOException {
        return NetcdfFileWriter.createNew(version, netcdfFile.getAbsolutePath(), new Nc4ForceTimeChunkingStategy(
                getNetcdfHelper().getChunkSizeTime()));
    }

    protected void encodeSensorDataToNetcdf(File netcdfFile, AbstractSensorDataset sensorDataset, Version version)
            throws EncodingException, IOException {
        String sensor = sensorDataset.getSensorIdentifier();
        sensorDataset.getSensor().setSensorDescription(
                getProcedureDescription(sensor, sensorDataset.getProcedureDescription()));

        NetcdfFileWriter writer = getNetcdfFileWriter(netcdfFile, version);
        // set fill on, doesn't seem to have any effect though
        writer.setFill(true);

        Map<Variable, Array> variableArrayMap = Maps.newHashMap();
        int numTimes = sensorDataset.getTimes().size();
        // FIXME shouldn't assume that all subsensors are heights (or rename
        // subsensors if they are)
        int numHeightDepth = sensorDataset.getSubSensors().size() > 0 ? sensorDataset.getSubSensors().size() : 1;

        // global attributes
        addGlobaleAttributes(writer, sensorDataset);

        // add appropriate dims for feature type
        List<Dimension> timeDims = Lists.newArrayList();
        List<Dimension> latLngDims = Lists.newArrayList();
        List<Dimension> latDims = Lists.newArrayList();
        List<Dimension> lngDims = Lists.newArrayList();
        List<Dimension> zDims = Lists.newArrayList();
        List<Dimension> obsPropDims = Lists.newArrayList();

        // REQUIRED for NetCDF-3
        // add Dimensions
        // dTime = writer.addDimension(null, CFStandardNames.TIME.getName(),
        // numTimes);
        Dimension dTime = writer.addUnlimitedDimension(getVariableDimensionCaseName(CFStandardNames.TIME.getName()));
        dTime.setLength(numTimes);
        timeDims.add(dTime);
        if (!(sensorDataset instanceof StaticLocationDataset)) {
            zDims.add(dTime);
        }
        obsPropDims.add(dTime);

        // set up lat/lng dimensions
        // FIXME do not set time dimension for static location dataset
        // if ((sensorDataset instanceof StaticLocationDataset)) {
        // latLngDims.add(dTime);
        // }

        // set up z dimensions
        String dimensionName;
        if (useHeight()) {
            dimensionName = getVariableDimensionCaseName(CFStandardNames.HEIGHT.getName());
        } else {
            dimensionName = getVariableDimensionCaseName(CFStandardNames.DEPTH.getName());
        }
        // profile/timeSeriesProfile
        Dimension dZ = writer.addDimension(null, dimensionName, numHeightDepth);
        if (!(sensorDataset instanceof StaticLocationDataset)) {
            // trajectory
            zDims.add(dTime);
        }
        zDims.add(dZ);
        obsPropDims.add(dZ);

        variableArrayMap.putAll(getNetcdfProfileSpecificVariablesArrays(writer, sensorDataset));

        // time var
        Variable vTime = addVariableTime(writer, timeDims);
        if (numTimes > 1 && writer.getVersion().isNetdf4format()) {
            vTime.addAttribute(new Attribute(CDM.CHUNK_SIZES, getNetcdfHelper().getChunkSizeTime()));
        }
        ArrayDouble timeArray = new ArrayDouble(getDimShapes(timeDims));
        initArrayWithFillValue(timeArray, getNetcdfHelper().getFillValue());

        Array latArray = getLatitudeArray(sensorDataset);
        Array lonArray = getLongitudeArray(sensorDataset);

        // add lat/long dimensions
        long latSize = 1;
        if (latArray != null) {
            latSize = latArray.getSize();
        }
        Dimension dLat =
                writer.addDimension(null, getVariableDimensionCaseName(CFStandardNames.LATITUDE.getName()),
                        (int) latSize);
        latDims.add(dLat);
        long lonSize = 1;
        if (lonArray != null) {
            lonSize = lonArray.getSize();
        }
        Dimension dLon =
                writer.addDimension(null, getVariableDimensionCaseName(CFStandardNames.LONGITUDE.getName()),
                        (int) lonSize);
        lngDims.add(dLon);

        // lat/lon var
        Variable vLat;
        Variable vLon;
        if (latLngDims.size() > 0) {
            vLat = addVariableLatitude(writer, latLngDims);
            vLon = addVariableLongitude(writer, latLngDims);
        } else {
            vLat = addVariableLatitude(writer, latDims);
            vLon = addVariableLongitude(writer, lngDims);
        }

        // height/depth var
        Variable vHeightDepth;
        if (useHeight()) {
            vHeightDepth = addVariableHeight(writer, zDims);
        } else {
            vHeightDepth = addVariableDepth(writer, zDims);
        }

        String coordinateString =
                Joiner.on(' ').join(
                        Lists.newArrayList(vTime.getFullName(), vLat.getFullName(), vLon.getFullName(),
                                vHeightDepth.getFullName()));

        Map<OmObservableProperty, Variable> obsPropVarMap = Maps.newHashMap();
        Map<Variable, Array> varDataArrayMap = Maps.newHashMap();
        for (OmObservableProperty obsProp : sensorDataset.getPhenomena()) {
            // obs prop var
            Variable vObsProp = addVariableForObservedProperty(writer, obsProp, obsPropDims, coordinateString);
            obsPropVarMap.put(obsProp, vObsProp);

            // init obs prop data array
            Array obsPropArray = getArray(obsPropDims);
            initArrayWithFillValue(obsPropArray, getNetcdfHelper().getFillValue());
            varDataArrayMap.put(vObsProp, obsPropArray);
        }

        // populate heights array for profile
        Array heightDephtArray = null;
        if (zDims.size() == 1 && hasDimension(zDims, dZ) && !sensorDataset.getSubSensors().isEmpty()) {
            heightDephtArray = initHeightDephtArray(zDims);
            Double consistentBinHeight = populateHeightDepthArray(sensorDataset, heightDephtArray, vHeightDepth);
            String verticalResolution = null;
            if (consistentBinHeight == null) {
                verticalResolution = ACDDConstants.POINT;
            } else if (consistentBinHeight != getNetcdfHelper().getFillValue()) {
                verticalResolution = consistentBinHeight + " " + CFConstants.UNITS_METERS + " " + ACDDConstants.BINNED;
            }
            if (verticalResolution != null) {
                writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_RESOLUTION,
                        verticalResolution));
            }

        }

        // iterate through sensorDataset, set values
        int timeCounter = 0;
        for (Time time : sensorDataset.getTimes()) {
            // set time value
            Index timeIndex = timeArray.getIndex();
            int timeIndexCounter = 0;
            // if (hasDimension(timeDims, dFeatureTypeInstance)) {
            // timeIndex.setDim(timeIndexCounter++, 0);
            // }
            if (hasDimension(timeDims, dTime)) {
                timeIndex.setDim(timeIndexCounter++, timeCounter++);
            }
            timeArray.set(timeIndex, getTimeValue(time));

            // data values
            Map<OmObservableProperty, Map<SubSensor, Value<?>>> obsPropMap = sensorDataset.getDataValues().get(time);
            for (Entry<OmObservableProperty, Map<SubSensor, Value<?>>> entry : obsPropMap.entrySet()) {
                OmObservableProperty obsProp = entry.getKey();
                Variable variable = obsPropVarMap.get(obsProp);
                Array array = varDataArrayMap.get(variable);
                for (Entry<SubSensor, Value<?>> subSensorEntry : obsPropMap.get(obsProp).entrySet()) {
                    SubSensor subSensor = subSensorEntry.getKey();
                    Value<?> value = subSensorEntry.getValue();
                    Object valObj = value.getValue();
                    if (!(valObj instanceof Number)) {
                        throw new EncodingException("Value class %s not supported",
                                valObj.getClass().getCanonicalName());
                    }
                    Index index = array.getIndex();
                    int obsPropDimCounter = 0;
                    for (Dimension dim : obsPropDims) {
                        // if (dim.equals(dFeatureTypeInstance)){
                        // feature type instance index
                        // index.setDim(obsPropDimCounter++, 0);
                        // } else if (dim.equals(dTime)){
                        if (dim.equals(dTime)) {
                            // time index dim
                            index.setDim(obsPropDimCounter++, timeCounter - 1);
                        } else if (dim.equals(dZ) && dim.getLength() > 1) {
                            // height/depth index dim
                            index.setDim(obsPropDimCounter++, sensorDataset.getSubSensors().indexOf(subSensor));
                        }
                    }
                    if (array instanceof ArrayFloat) {
                        ((ArrayFloat) array).set(index, ((Number) valObj).floatValue());
                    } else {
                        ((ArrayDouble) array).set(index, ((Number) valObj).doubleValue());
                    }
                }
            }
        }

        // create the empty netCDF with dims/vars/attributes defined
        variableArrayMap.put(vTime, timeArray);
        if (latArray != null) {
            variableArrayMap.put(vLat, latArray);
        }
        if (lonArray != null) {
            variableArrayMap.put(vLon, lonArray);
        }
        if (heightDephtArray != null) {
            variableArrayMap.put(vHeightDepth, heightDephtArray);
        }
        variableArrayMap.putAll(varDataArrayMap);
        // create the empty netCDF with dims/vars/attributes defined
        writeToFile(writer, variableArrayMap);
        writer.close();
    }

    protected void addGlobaleAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset)
            throws EncodingException {
        // convetion
        addConventions(writer);
        // metadata conventions
        addMetadataConventions(writer);
        // feature type
        addFeatureType(writer, sensorDataset);
        // CDM data type
        addCdmDataType(writer, sensorDataset);
        // NODC template version
        addNodcTemplateVersion(writer, sensorDataset);
        // standardName vocabulary
        addStandardNameVocabulary(writer, sensorDataset);
        // platform
        addPlatform(writer, sensorDataset);
        // instrument
        addInstrument(writer, sensorDataset);
        // title
        addTitle(writer, sensorDataset);
        // summary
        addSummary(writer, sensorDataset);
        // date created
        addCreateDate(writer);
        // license
        addLicense(writer, sensorDataset);
        // id
        addId(writer, sensorDataset);
        // uuid
        addUUID(writer, sensorDataset);
        // keywords vocabulary
        addKeywordsVocabulary(writer);
        // keywords
        addKeywords(writer, sensorDataset);
        // operator -> contributor
        addContributor(writer, sensorDataset);
        // publisher
        addPublisher(writer, sensorDataset);
        // geospatial extent
        addGeospatialAttributes(writer, sensorDataset);
        // geospatial_vertical_min/max/units/resolution/positive
        addGeospatialVerticalAttributes(writer, sensorDataset);
        // time coverage
        addTimeCoverageAttributes(writer, sensorDataset);
        // additional global attributes
        addProfileSpecificGlobalAttributes(writer, sensorDataset);
    }

    protected CDMNode addCreateDate(NetcdfFileWriter writer) {
        return writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.DATE_CREATED, new DateTime(DateTimeZone.UTC).toString()));

    }

    protected CDMNode addMetadataConventions(NetcdfFileWriter writer) {
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.METADATA_CONVENTIONS,
                ACDDConstants.UNIDATA_DATASET_DISCOVERY_1_0));

    }

    protected CDMNode addFeatureType(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null, new Attribute(CFConstants.FEATURE_TYPE, sensorDataset.getFeatureType()
                .name()));

    }

    protected CDMNode addCdmDataType(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.CDM_DATA_TYPE, CF.FeatureType.convert(sensorDataset.getFeatureType())
                        .name()));

    }

    protected CDMNode addNodcTemplateVersion(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset)
            throws EncodingException {
        return writer.addGroupAttribute(null, new Attribute(NODCConstants.NODC_TEMPLATE_VERSION,
                getNodcTemplateVersion(sensorDataset.getFeatureType())));

    }

    protected CDMNode addStandardNameVocabulary(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.STANDARD_NAME_VOCABULARY, CFConstants.CF_1_6));

    }

    protected CDMNode addKeywords(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.KEYWORDS, Joiner.on(",").join(getKeywords(sensorDataset))));
    }

    protected CDMNode addKeywordsVocabulary(NetcdfFileWriter writer) {
        // TODO define setting? Choice?
        // return writer.addGroupAttribute(null, new
        // Attribute(ACDDConstants.KEYWORDS_VOCABULARY, ""));
        return null;

    }

    protected CDMNode addId(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) throws EncodingException {
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.ID, sensorDataset.getSensorIdentifier()));
    }

    protected CDMNode addUUID(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null, new Attribute(NODCConstants.UUID, UUID.randomUUID().toString()));
    }

    protected CDMNode addSummary(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        String summary;
        if (sensorDataset.getSensor().isSetSensorDescription()
                && sensorDataset.getSensor().getSensorDescritpion().isSetDescription()) {
            summary = sensorDataset.getSensor().getSensorDescritpion().getDescription();
        } else {
            summary =
                    "Sensor observations for " + sensorDataset.getSensorIdentifier() + ", feature type "
                            + sensorDataset.getFeatureType().name();
        }
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.SUMMARY, summary));
    }

    protected CDMNode addTitle(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {

        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.TITLE, sensorDataset.getSensorIdentifier()));
    }

    protected void addGeospatialAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset)
            throws EncodingException {
        // FIXME when trajectories are implemented, bbox should be calculated in
        // AbstractSensorDataset during construction
        if (sensorDataset instanceof StaticLocationDataset) {
            StaticLocationDataset LocationDataset = (StaticLocationDataset) sensorDataset;
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_LAT_MIN, LocationDataset.getLat()));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_LAT_MAX, LocationDataset.getLat()));
            writer.addGroupAttribute(null,
                    new Attribute(ACDDConstants.GEOSPATIAL_LAT_UNITS, CFConstants.UNITS_DEGREES_NORTH));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_LON_MIN, LocationDataset.getLng()));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_LON_MAX, LocationDataset.getLng()));
            writer.addGroupAttribute(null,
                    new Attribute(ACDDConstants.GEOSPATIAL_LON_UNITS, CFConstants.UNITS_DEGREES_EAST));
        } else {
            throw new EncodingException("Trajectory encoding is not supported (bbox)");
        }

    }

    protected void addTimeCoverageAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset)
            throws EncodingException {
        List<Time> times = Lists.newArrayList(sensorDataset.getTimes());
        Collections.sort(times);
        DateTime firstTime = getDateTime(times.get(0));
        DateTime lastTime = getDateTime(times.get(times.size() - 1));

        // temporal extent
        writer.addGroupAttribute(null, new Attribute(ACDDConstants.TIME_COVERAGE_START, firstTime.toString()));
        writer.addGroupAttribute(null, new Attribute(ACDDConstants.TIME_COVERAGE_END, lastTime.toString()));

    }

    protected void addGeospatialVerticalAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_UNITS, CFConstants.UNITS_METERS));
        double min = getGeospatialVerticalMin(sensorDataset);
        double max = getGeospatialVerticalMax(sensorDataset);
        if (useHeight()) {
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_POSITIVE,
                    CFConstants.POSITIVE_UP));
        } else {
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_POSITIVE,
                    CFConstants.POSITIVE_DOWN));
            min = min != 0.0 ? min * (-1.0) : min;
            max = max != 0.0 ? max * (-1.0) : max;
            if (min > max) {
                double tmp = min;
                min = max;
                max = tmp;
            }
        }
        writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_MIN, min));
        writer.addGroupAttribute(null, new Attribute(ACDDConstants.GEOSPATIAL_VERTICAL_MAX, max));
    }

    protected CDMNode addPlatform(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(NODCConstants.PLATFORM, sensorDataset.getSensorIdentifier()));
    }

    protected CDMNode addInstrument(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(NODCConstants.INSTRUMENT, sensorDataset.getSensorIdentifier()));
    }

    private Double populateHeightDepthArray(AbstractSensorDataset sensorDataset, Array heightDephtArray, Variable v)
            throws EncodingException {
        Index index = heightDephtArray.getIndex();
        int indexCounter = 0;
        Double consistentBinHeight = null;
        for (SubSensor subSensor : sensorDataset.getSubSensors()) {
            if (subSensor instanceof ProfileSubSensor) {
                index.setDim(0, indexCounter++);
                heightDephtArray.setDouble(index, checkValue(v, ((ProfileSubSensor) subSensor).getHeight()));
                // check for consistent bin size
                if (subSensor instanceof BinProfileSubSensor) {
                    double binHeight = checkValue(v, ((BinProfileSubSensor) subSensor).getBinHeight());
                    if (consistentBinHeight == null) {
                        consistentBinHeight = binHeight;
                    } else if (consistentBinHeight != getNetcdfHelper().getFillValue()
                            && consistentBinHeight != binHeight) {
                        // mark bin height as inconsistent
                        consistentBinHeight = getNetcdfHelper().getFillValue();
                    }
                }
            } else {
                throw new EncodingException("Non-profile subsensors not supported.");
            }
        }
        return consistentBinHeight;
    }

    private double checkValue(Variable v, Double value) {
        Attribute aStandardName = v.findAttributeIgnoreCase(CFConstants.STANDARD_NAME);
        if (aStandardName.isString() && CFStandardNames.DEPTH.getName().equals(aStandardName.getStringValue())) {
            return value != 0.0 ? value * (-1.0) : value;
        }
        return value;
    }

    private Array initHeightDephtArray(List<Dimension> zDims) {
        Array array = getArray(zDims);
        initArrayWithFillValue(array, getNetcdfHelper().getFillValue());
        return array;
    }

    protected Array getLatitudeArray(AbstractSensorDataset sensorDataset) throws EncodingException {
        if (sensorDataset instanceof StaticLocationDataset) {
            StaticLocationDataset locationDataset = (StaticLocationDataset) sensorDataset;
            if (locationDataset.getLat() != null) {
                Array array = getArray();
                initArrayWithFillValue(array, getNetcdfHelper().getFillValue());
                Index index = array.getIndex();
                index.set(0);
                array.setDouble(index, locationDataset.getLat());
            }
        } else {
            // TODO support varying lat
            throw new EncodingException("Varying lat are not yet supported.");
        }
        return null;
    }

    protected Array getLongitudeArray(AbstractSensorDataset sensorDataset) throws EncodingException {
        if (sensorDataset instanceof StaticLocationDataset) {
            StaticLocationDataset locationDataset = (StaticLocationDataset) sensorDataset;
            if (locationDataset.getLat() != null) {
                Array array = getArray();
                initArrayWithFillValue(array, getNetcdfHelper().getFillValue());
                Index index = array.getIndex();
                index.set(0);
                array.setDouble(index, locationDataset.getLat());
            }
        } else {
            // TODO support varying lat
            throw new EncodingException("Varying longs are not yet supported.");
        }
        return null;
    }

    private Array getArray(int[] dims) {
        if (DataType.FLOAT.equals(getDataType())) {
            return new ArrayFloat(dims);
        }
        return new ArrayDouble(dims);
    }

    private Array getArray(List<Dimension> zDims) {
        return getArray(getDimShapes(zDims));
    }

    private Array getArray() {
        if (DataType.FLOAT.equals(getDataType())) {
            return new ArrayFloat.D1(1);
        }
        return new ArrayDouble.D1(1);
    }

    protected Map<Variable, Array> getNetcdfProfileSpecificVariablesArrays(NetcdfFileWriter writer,
            AbstractSensorDataset dataset) throws EncodingException {
        return Maps.newHashMap();
    }

    private void writeToFile(NetcdfFileWriter writer, Map<Variable, Array> variableArrayMap)
            throws EncodingException, IOException {
        writer.create();

        // fill the netCDF file with data
        try {
            for (Entry<Variable, Array> varEntry : variableArrayMap.entrySet()) {
                writer.write(varEntry.getKey(), varEntry.getValue());
            }
        } catch (InvalidRangeException e) {
            throw new EncodingException("Error writing netCDF variable data");
        }
    }

    private double getGeospatialVerticalMin(AbstractSensorDataset dataset) {
        if (dataset.isSetSubSensors()) {
            SubSensor subSensor = dataset.getSubSensors().get(dataset.getSubSensors().size() - 1);
            if (subSensor instanceof ProfileSubSensor) {
                return ((ProfileSubSensor) subSensor).getHeight();
            }
        }
        return 0;
    }

    private double getGeospatialVerticalMax(AbstractSensorDataset dataset) {
        if (dataset.isSetSubSensors()) {
            SubSensor subSensor = dataset.getSubSensors().get(0);
            if (subSensor instanceof ProfileSubSensor) {
                return ((ProfileSubSensor) subSensor).getHeight();
            }
        }
        return 0;
    }

    protected boolean useHeight() {
        return CFStandardNames.HEIGHT.equals(getNetcdfHelper().getHeightDepth());
    }

    protected Iterable<?> getKeywords(AbstractSensorDataset sensorDataset) {
        LinkedHashSet<String> keywords = Sets.newLinkedHashSet();
        // keywords.add(sensor.getAuthority());
        // keywords.add(sensor.getStation());
        // keywords.add(sensor.getSensor());
        sensorDataset.getPhenomena()
                .forEach(obsProp -> {
                    keywords.add(obsProp.getIdentifier());
                });
        return keywords;
    }

    protected Variable addVariableForObservedProperty(NetcdfFileWriter writer, OmObservableProperty obsProp,
            List<Dimension> obsPropDims, String coordinateString) {
        String standardName = getObservedPropertyStandardName(obsProp);
        String longName = getObservedPropertyLongName(obsProp);
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(standardName), getDataType(), obsPropDims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, standardName));
        if (!Strings.isNullOrEmpty(longName)) {
            v.addAttribute(new Attribute(CFConstants.LONG_NAME, longName));
        }
        v.addAttribute(new Attribute(CFConstants.COORDINATES, coordinateString));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        // if (obsProp.getUnit() != null) {
        // vObsProp.addAttribute(new Attribute(CFConstants.UNITS,
        // IoosSosUtil.getNameFromUri(obsProp.getUnit())));
        // }
        if (obsProp.getUnit() != null) {
            v.addAttribute(new Attribute(CFConstants.UNITS, obsProp.getUnit()));
        }
        return v;
    }

    protected String getObservedPropertyStandardName(OmObservableProperty obsProp) {
        return obsProp.getIdentifier();
    }

    protected String getObservedPropertyLongName(OmObservableProperty obsProp) {
        return obsProp.getIdentifier();
    }

    protected Variable addVariableTime(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(CFStandardNames.TIME.getName()),
                DataType.DOUBLE, dims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, CFStandardNames.TIME.getName()));
        v.addAttribute(new Attribute(CFConstants.LONG_NAME, getLongName(CFStandardNames.TIME.getName())));
        v.addAttribute(new Attribute(CFConstants.UNITS, getTimeUnits()));
        v.addAttribute(new Attribute(CFConstants.AXIS, CFConstants.AXIS_T));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        return v;
    }

    protected Variable addVariableLatitude(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(CFStandardNames.LATITUDE.getName()),
                getDataType(), dims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, CFStandardNames.LATITUDE.getName()));
        v.addAttribute(new Attribute(CFConstants.LONG_NAME, getLongName(CFStandardNames.LATITUDE.getName())));
        v.addAttribute(new Attribute(CFConstants.UNITS, CFConstants.UNITS_DEGREES_NORTH));
        v.addAttribute(new Attribute(CFConstants.AXIS, CFConstants.AXIS_Y));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        return v;

    }

    protected Variable addVariableLongitude(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(CFStandardNames.LONGITUDE.getName()),
                getDataType(), dims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, CFStandardNames.LONGITUDE.getName()));
        v.addAttribute(new Attribute(CFConstants.LONG_NAME, getLongName(CFStandardNames.LONGITUDE.getName())));
        v.addAttribute(new Attribute(CFConstants.UNITS, CFConstants.UNITS_DEGREES_EAST));
        v.addAttribute(new Attribute(CFConstants.AXIS, CFConstants.AXIS_X));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        return v;
    }

    protected Variable addVariableHeight(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(CFStandardNames.HEIGHT.getName()),
                getDataType(), dims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, CFStandardNames.HEIGHT.getName()));
        v.addAttribute(new Attribute(CFConstants.LONG_NAME, getLongName(CFStandardNames.HEIGHT.getName())));
        v.addAttribute(new Attribute(CFConstants.UNITS, CFConstants.UNITS_METERS));
        v.addAttribute(new Attribute(CFConstants.AXIS, CFConstants.AXIS_Z));
        v.addAttribute(new Attribute(CFConstants.POSITIVE, CFConstants.POSITIVE_UP));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        return v;
    }

    protected Variable addVariableDepth(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable v = writer.addVariable(null, getVariableDimensionCaseName(CFStandardNames.DEPTH.getName()),
                getDataType(), dims);
        v.addAttribute(new Attribute(CFConstants.STANDARD_NAME, CFStandardNames.DEPTH.getName()));
        v.addAttribute(new Attribute(CFConstants.LONG_NAME, getLongName(CFStandardNames.DEPTH.getName())));
        v.addAttribute(new Attribute(CFConstants.UNITS, CFConstants.UNITS_METERS));
        v.addAttribute(new Attribute(CFConstants.AXIS, CFConstants.AXIS_Z));
        v.addAttribute(new Attribute(CFConstants.POSITIVE, CFConstants.POSITIVE_DOWN));
        v.addAttribute(new Attribute(CFConstants.FILL_VALUE, getNetcdfHelper().getFillValue()));
        return v;
    }

    protected String getVariableDimensionCaseName(String name) {
        if (getNetcdfHelper().isUpperCaseNames()) {
            return name.toUpperCase(Locale.ROOT);
        }
        return name;
    }

    protected String getTimeUnits() {
        return CFConstants.UNITS_TIME;
    }

    private String getLongName(String name) {
        return String.format("%s of measurement", name);
    }

    protected DataType getDataType() {
        if (Float.class.getSimpleName().equals(getNetcdfHelper().getVariableType())) {
            return DataType.FLOAT;
        }
        return DataType.DOUBLE;
    }

    protected String getLongNameEPSG() {
        return OGCConstants.URN_DEF_CRS_EPSG + 4326;
    }

    private String getNodcTemplateVersion(CF.FeatureType featureType) throws EncodingException {
        if (featureType.equals(CF.FeatureType.timeSeries)) {
            return NODCConstants.NODC_TIMESERIES_ORTHOGONAL_TEMPLATE_1_0;
        } else if (featureType.equals(CF.FeatureType.timeSeriesProfile)) {
            return NODCConstants.NODC_TIMESERIESPROFILE_ORTHOGONAL_TEMPLATE_1_0;
        }
        throw new EncodingException("Feature type %s is not supported for netCDF output", featureType.name());
    }

    protected String getCfRole(CF.FeatureType featureType) throws CodedException {
        switch (featureType) {
            case timeSeries:
                return CF.TIMESERIES_ID;
            case timeSeriesProfile:
                return CF.TIMESERIES_ID;
            case trajectory:
            case trajectoryProfile:
                return CF.TRAJECTORY_ID;
            default:
                throw new NoApplicableCodeException().withMessage("Feature type " + featureType.name()
                                                                  + " is not supported for netCDF output");
        }
    }

    protected DateTime getDateTime(Time time) throws EncodingException {
        if (!(time instanceof TimeInstant)) {
            throw new EncodingException("Time class %s not supported", time.getClass().getCanonicalName());
        }
        TimeInstant timeInstant = (TimeInstant) time;
        return timeInstant.getValue();
    }

    protected double getTimeValue(Time time) throws EncodingException {
        return DateTimeHelper.getSecondsSinceEpoch(getDateTime(time));
    }

    private int[] getDimShapes(List<Dimension> dims) {
        int[] dimShapes = new int[dims.size()];
        int dimCounter = 0;
        for (Dimension dim : dims) {
            dimShapes[dimCounter++] = dim.getLength();
        }
        return dimShapes;
    }

    private boolean hasDimension(List<Dimension> dims, Dimension dim) {
        return dim != null && dims.contains(dim);
    }

    private void initArrayWithFillValue(Array array, Object fillValue) {
        IndexIterator indexIterator = array.getIndexIterator();
        while (indexIterator.hasNext()) {
            indexIterator.setObjectNext(fillValue);
        }
    }

    private AbstractSensorML getProcedureDescription(String procedure, AbstractFeature procedureDescription)
            throws EncodingException {
        // query full procedure description if necessary
        if (procedureDescription == null || procedureDescription instanceof SosProcedureDescriptionUnknownType) {
            return getProcedureDescription(procedure, queryProcedureDescription(procedure));
        }
        AbstractSensorML abstractSensor = null;
        if (procedureDescription instanceof SosProcedureDescription
                && ((SosProcedureDescription) procedureDescription).getProcedureDescription() != null
                && ((SosProcedureDescription) procedureDescription)
                        .getProcedureDescription() instanceof AbstractSensorML) {
            abstractSensor =
                    (AbstractSensorML) ((SosProcedureDescription) procedureDescription).getProcedureDescription();
            // check for SensorML to get members
            if (abstractSensor instanceof SensorML) {
                SensorML sml = (SensorML) abstractSensor;
                if (sml.isWrapper()) {
                    abstractSensor = (AbstractSensorML) sml.getMembers().get(0);
                }
            }
            // set the identifier if missing
            if (!abstractSensor.isSetIdentifier()) {
                abstractSensor.setIdentifier(procedure);
            }
        }
        if (abstractSensor == null) {
            throw new EncodingException("Only SensorML procedure descriptions are supported, found %s for %s",
                    procedureDescription.getClass().getName(), procedure);
        }
        return abstractSensor;
    }

    private SosProcedureDescription<?> queryProcedureDescription(String procedure) throws EncodingException {
        DescribeSensorRequest req = new DescribeSensorRequest();
        req.setService(SosConstants.SOS);
        req.setVersion(Sos2Constants.SERVICEVERSION);
        req.setProcedure(procedure);
        Set<String> pdfs =
                (this.procedureDescriptionFormatRepository).getAllSupportedProcedureDescriptionFormats(SosConstants.SOS,
                        Sos2Constants.SERVICEVERSION);
        if (pdfs.contains(SensorML20Constants.NS_SML)) {
            req.setProcedureDescriptionFormat(SensorML20Constants.NS_SML);
        } else if (pdfs.contains(SensorMLConstants.NS_SML)) {
            req.setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
        } else {
            throw new EncodingException(
                    "Error getting sensor description for %s! Required procedureDescriptionFormats are not supported!",
                    procedure);
        }
        DescribeSensorResponse resp;
        try {
            resp = getDescribeSensorHandler().getSensorDescription(req);
        } catch (OwsExceptionReport e) {
            throw new EncodingException(e, "Error getting sensor description for %s", procedure);
        }
        return resp.getProcedureDescriptions().get(0);
    }

    protected AbstractDescribeSensorHandler getDescribeSensorHandler() throws CodedException {
        OperationHandler operationHandler =
                (this.operationHandlerRepository).getOperationHandler(SosConstants.SOS,
                        SosConstants.Operations.DescribeSensor.toString());
        if (operationHandler != null && operationHandler instanceof AbstractDescribeSensorHandler) {
            return (AbstractDescribeSensorHandler) operationHandler;
        }
        throw new NoApplicableCodeException().withMessage("Could not get DescribeSensor handler");
    }

    protected String getIdentifier(AbstractSensorML sml, String identifierDefinition) {
        Optional<SmlIdentifier> identifier =
                sml.findIdentification(SmlIdentifierPredicates.definition(identifierDefinition));
        if (identifier.isPresent()) {
            return identifier.get().getValue();
        }
        return null;
    }

    /**
     * Adds an attribute to {@link NetcdfFileWriter} if the definition was found
     * in {@link AbstractSensorML}
     *
     * @param writer
     *            {@link NetcdfFileWriter} to add attribute
     * @param sml
     *            {@link AbstractSensorML} to check for
     * @param identifier
     *            The definition of the value
     * @param attributeName
     *            The attribute name
     * @return <code>true</code>, if the attribute was added
     */
    protected boolean addAttributeIfExists(NetcdfFileWriter writer, AbstractSensorML sml, String identifier,
            String attributeName) {
        if (!addAttributeIfIdentifierExists(writer, sml, identifier, attributeName)) {
            return addAttributeIfClassifierExists(writer, sml, identifier, attributeName);
        }
        return false;
    }

    /**
     * Adds an attribute to {@link Variable} if the definition was found in
     * {@link AbstractSensorML}
     *
     * @param variable
     *            {@link Variable} to add attribute
     * @param sml
     *            {@link AbstractSensorML} to check for
     * @param identifier
     *            The definition of the value
     * @param attributeName
     *            The attribute name
     * @return <code>true</code>, if the attribute was added
     */
    protected boolean addAttributeIfExists(Variable variable, AbstractSensorML sml, String identifier,
            String attributeName) {
        if (!addAttributeIfIdentifierExists(variable, sml, identifier, attributeName)) {
            return addAttributeIfClassifierExists(variable, sml, identifier, attributeName);
        }
        return false;
    }

    protected boolean addAttributeIfIdentifierExists(NetcdfFileWriter writer, AbstractSensorML sml,
            String identifierDefinition, String attributeName) {
        String value = getIdentifier(sml, identifierDefinition);
        if (value != null) {
            writer.addGroupAttribute(null, new Attribute(attributeName, value));
            return true;
        }
        return false;
    }

    protected boolean addAttributeIfIdentifierExists(Variable variable, AbstractSensorML sml,
            String identifierDefinition, String attributeName) {
        String value = getIdentifier(sml, identifierDefinition);
        if (value != null) {
            variable.addAttribute(new Attribute(attributeName, value));
            return true;
        }
        return false;
    }

    protected String getClassifierValue(AbstractSensorML sml, String classifierDefinition) {
        Optional<SmlClassifier> classifier =
                sml.findClassifier(SmlClassifierPredicates.definition(classifierDefinition));
        if (classifier.isPresent()) {
            return classifier.get().getValue();
        }
        return null;
    }

    protected boolean addAttributeIfClassifierExists(NetcdfFileWriter writer, AbstractSensorML sml,
            String classifierDefinition, String attributeName) {
        String value = getClassifierValue(sml, classifierDefinition);
        if (value != null) {
            writer.addGroupAttribute(null, new Attribute(attributeName, value));
            return true;
        }
        return false;
    }

    protected boolean addAttributeIfClassifierExists(Variable variable, AbstractSensorML sml,
            String classifierDefinition, String attributeName) {
        String value = getClassifierValue(sml, classifierDefinition);
        if (value != null) {
            variable.addAttribute(new Attribute(attributeName, value));
            return true;
        }
        return false;
    }

    protected SmlResponsibleParty getResponsibleParty(AbstractSensorML sml, String contactRole) {
        SmlContact contact = sml.getContact(contactRole);
        if (contact != null && contact instanceof SmlResponsibleParty) {
            return (SmlResponsibleParty) contact;
        }
        return null;
    }

    private void addConventions(NetcdfFileWriter writer) {
        writer.addGroupAttribute(null, new Attribute(CFConstants.CONVENTIONS, getConventionsValue()));
    }

    protected String getConventionsValue() {
        return CFConstants.CF_1_6;
    }

    private CDMNode addLicense(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.LICENSE, getLicenseValue()));
    }

    protected String getLicenseValue() {
        return ACDDConstants.LICENSE_FREELY_DISTRIBUTED;
    }

    protected boolean addPublisher(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            AbstractSensorML sml = sensorDataset.getSensor().getSensorDescritpion();
            if (addPublisher(sml, getNetcdfHelper().getPublisher(), writer)) {
                return true;
            }
            return addPublisher(sml, CiRoleCodes.CI_RoleCode_publisher, writer);
        }
        return false;
    }

    protected boolean addPublisher(AbstractSensorML sml, CiRoleCodes ciRoleCode, NetcdfFileWriter writer) {
        return addPublisher(sml, ciRoleCode.getIdentifier(), writer);
    }

    protected boolean addPublisher(AbstractSensorML sml, String contactRole, NetcdfFileWriter writer) {
        SmlResponsibleParty responsibleParty = getResponsibleParty(sml, contactRole);
        if (responsibleParty != null) {
            if (responsibleParty.isSetOrganizationName()) {
                writer.addGroupAttribute(null,
                        new Attribute(ACDDConstants.PUBLISHER_NAME, responsibleParty.getOrganizationName()));
            }
            if (responsibleParty.isSetEmail()) {
                writer.addGroupAttribute(null,
                        new Attribute(ACDDConstants.PUBLISHER_EMAIL, responsibleParty.getEmail()));
            }
            if (responsibleParty.isSetOnlineResources()) {
                writer.addGroupAttribute(null, new Attribute(ACDDConstants.PUBLISHER_URL, responsibleParty
                        .getOnlineResources().get(0)));
            }
        } else {
            String mail = getServiceProvider().getServiceContact().getContactInfo().flatMap(OwsContact::getAddress)
                    .map(OwsAddress::getElectronicMailAddress).map(l -> Iterables.getFirst(l, null)).orElse(null);
            String name = getServiceProvider().getProviderName();
            String url = getServiceProvider().getProviderSite().flatMap(OwsOnlineResource::getHref).map(URI::toString)
                    .orElse(null);


            writer.addGroupAttribute(null, new Attribute(ACDDConstants.PUBLISHER_NAME, name));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.PUBLISHER_EMAIL, mail));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.PUBLISHER_URL, url));
        }
        return true;
    }

    protected boolean addContributor(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            AbstractSensorML sml = sensorDataset.getSensor().getSensorDescritpion();
            if (addContributor(sml, getNetcdfHelper().getContributor(), writer)) {
                return true;
            } else if (addContributor(sml, CiRoleCodes.CI_RoleCode_principalInvestigator, writer)) {
                return true;
            }
            return addContributor(sml, CiRoleCodes.CI_RoleCode_author, writer);
        }
        return false;
    }

    protected boolean addContributor(AbstractSensorML sml, CiRoleCodes ciRoleCode, NetcdfFileWriter writer) {
        return addContributor(sml, ciRoleCode.getIdentifier(), writer);
    }

    protected boolean addContributor(AbstractSensorML sml, String contactRole, NetcdfFileWriter writer) {
        SmlResponsibleParty responsibleParty = getResponsibleParty(sml, contactRole);
        if (responsibleParty != null) {
            if (responsibleParty.isSetOrganizationName()) {
                writer.addGroupAttribute(null,
                        new Attribute(ACDDConstants.CONTRIBUTOR_NAME, responsibleParty.getOrganizationName()));
            }
            if (responsibleParty.isSetEmail()) {
                writer.addGroupAttribute(null,
                        new Attribute(ACDDConstants.CONTRIBUTOR_ROLE, responsibleParty.getRole()));
            }
            if (responsibleParty.isSetOnlineResources()) {
                writer.addGroupAttribute(null,
                        new Attribute(NetcdfConstants.CONTRIBUTOR_EMAIL, responsibleParty.getEmail()));
            }
        } else {
            String individualName = getServiceProvider().getServiceContact().getIndividualName().orElse(null);
            String positionName = getServiceProvider().getServiceContact().getPositionName().orElse(null);
            String mail = getServiceProvider().getServiceContact().getContactInfo().flatMap(OwsContact::getAddress)
                    .map(OwsAddress::getElectronicMailAddress).map(l -> Iterables.getFirst(l, null)).orElse(null);
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.CONTRIBUTOR_NAME, individualName));
            writer.addGroupAttribute(null, new Attribute(ACDDConstants.CONTRIBUTOR_ROLE, positionName));
            writer.addGroupAttribute(null, new Attribute(NetcdfConstants.CONTRIBUTOR_EMAIL, mail));
        }
        return true;
    }

    protected OwsServiceProvider getServiceProvider() {
        return this.serviceMetadataRepository.getServiceProviderFactory(SosConstants.SOS).get();
    }

    protected Attribute getAttribute(NetcdfFileWriter writer, String name) {
        return writer.findGlobalAttribute(name);
    }

    protected String getPrefixlessIdentifier(String identifier) {
        String splitter = "";
        if (identifier.startsWith("urn")) {
            splitter = ":";
        } else if (identifier.startsWith("http")) {
            splitter = "/";
        }
        if (identifier.lastIndexOf(splitter) < identifier.length()) {
            return identifier.substring(identifier.lastIndexOf(splitter) + 1);
        }
        return identifier;
    }

    protected String makeDateSafe(DateTime dt) {
        return dt.toString().replace(":", "");
    }

    protected String getFilename(AbstractSensorDataset sensorDataset) throws EncodingException {
        List<Time> times = Lists.newArrayList(sensorDataset.getTimes());
        Collections.sort(times);
        DateTime firstTime = getDateTime(times.get(0));
        DateTime lastTime = getDateTime(times.get(times.size() - 1));

        StringBuilder pathBuffer = new StringBuilder();
        pathBuffer.append(sensorDataset.getSensorIdentifier().replaceAll("http://", "").replaceAll("/", "_"));
        pathBuffer.append("_").append(sensorDataset.getFeatureType().name().toLowerCase(Locale.ROOT));
        pathBuffer.append("_").append(makeDateSafe(firstTime));
        // if (!(sensorDataset instanceof IStaticTimeDataset)) {
        pathBuffer.append("_").append(makeDateSafe(lastTime));
        // }
        pathBuffer.append("_").append(Long.toString(java.lang.System.nanoTime())).append(".nc");
        return pathBuffer.toString();
    }

    @Override
    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

}

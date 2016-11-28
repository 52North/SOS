package org.n52.sos.decode.kvp;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Supplier;

import org.n52.iceland.binding.kvp.AbstractKvpDecoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.service.MiscSettings;
import org.n52.iceland.util.Validation;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.util.CRSHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.DateTimeParseException;
import org.n52.janmayen.function.ThrowingBiConsumer;
import org.n52.janmayen.function.ThrowingTriConsumer;
import org.n52.sos.ds.FeatureQuerySettingsProvider;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 * @param <R> the request type
 */
@Configurable
public abstract class AbstractSosKvpDecoder<R extends AbstractServiceRequest> extends AbstractKvpDecoder<R> {
    private int storageEPSG;
    private int storage3DEPSG;
    private int defaultResponseEPSG;
    private int defaultResponse3DEPSG;
    private String srsNamePrefixV2;
    private String srsNamePrefixV1;

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String version, String operation) {
        this(supplier, SosConstants.SOS, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String version, Enum<?> operation) {
        this(supplier, SosConstants.SOS, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, DecoderKey... keys) {
        super(supplier, keys);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, Collection<? extends DecoderKey> keys) {
        super(supplier, keys);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String service, String version, String operation) {
        super(supplier, service, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String service, String version, Enum<?> operation) {
        super(supplier, service, version, operation);
    }

    /**
     * Set storage EPSG code from settings
     *
     * @param epsgCode
     *                 EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_EPSG)
    public void setStorageEPSG(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        this.storageEPSG = epsgCode;
    }

    /**
     * Set storage 3D EPSG code from settings
     *
     * @param epsgCode3D
     *                   3D EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_3D_EPSG)
    public void setStorage3DEPSG(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        this.storage3DEPSG = epsgCode3D;
    }

    /**
     * Set default response EPSG code from settings
     *
     * @param epsgCode
     *                 EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_EPSG)
    public void setDefaultResponseEPSG(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        this.defaultResponseEPSG = epsgCode;
    }

    /**
     * Set default response 3D EPSG code from settings
     *
     * @param epsgCode3D
     *                   3D EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_3D_EPSG)
    public void setDefaultResponse3DEPSG(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        this.defaultResponse3DEPSG = epsgCode3D;
    }

    @Setting(MiscSettings.SRS_NAME_PREFIX_SOS_V2)
    public void setSrsNamePrefixForSosV2(String prefix) {
        this.srsNamePrefixV2 = CRSHelper.asHttpPrefix(prefix);
    }

    @Setting(MiscSettings.SRS_NAME_PREFIX_SOS_V1)
    public void setSrsNamePrefixForSosV1(String prefix) {
        srsNamePrefixV1 = CRSHelper.asUrnPrefix(prefix);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void getCommonRequestParameterDefinitions(Builder<R> builder) {
        super.getCommonRequestParameterDefinitions(builder);
        builder.add(OWSConstants.AdditionalRequestParams.language, AbstractServiceRequest::addSweTextExtension);
        builder.add(OWSConstants.AdditionalRequestParams.crs, AbstractServiceRequest::addSweTextExtension);
        builder.add(OWSConstants.AdditionalRequestParams.returnHumanReadableIdentifier, AbstractServiceRequest::addSweBooleanExtension);
    }

    protected ThrowingBiConsumer<R, String, DecodingException> decodeNamespaces(
            ThrowingBiConsumer<? super R, ? super Map<String, String>, DecodingException> delegate) {
        return (request, value) -> delegate.accept(request, decodeNamespaces(value));
    }

    protected Map<String, String> decodeNamespaces(String value) {
        return Arrays.stream(value.replaceAll("\\),", "").replaceAll("\\)", "").split("xmlns\\("))
                .map(Strings::emptyToNull)
                .filter(Objects::nonNull)
                .map(string -> string.split(","))
                .collect(toMap(s -> s[0], s -> s[1]));
    }

    protected ThrowingTriConsumer<R, String, List<String>, DecodingException> decodeTemporalFilter(
            ThrowingBiConsumer<? super R, ? super TemporalFilter, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeTemporalFilter(name, value));
    }

    protected ThrowingTriConsumer<R, String, String, DecodingException> decodeTime(
            ThrowingBiConsumer<? super R, ? super Time, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeTime(name, value));
    }

    protected Time decodeTime(String name, String value) throws DecodingException {
        String[] times = name.split("/");
        switch (times.length) {
            case 1:
                return decodeTimeInstant(name, value);
            case 2:
                return decodeTimePeriod(name, times);
            default:
                throw new DecodingException(value, "The parameter value is not valid!");
        }
    }

    private TimePeriod decodeTimePeriod(String name, String[] times) throws DecodingException {
        try {
            return new TimePeriod(DateTimeHelper.parseIsoString2DateTime(times[0]),
                                  DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(times[1]));
        } catch (DateTimeParseException ex) {
            throw new DecodingException(ex, name);
        }
    }

    private TimeInstant decodeTimeInstant(String name, String time) throws DecodingException {
        try {
            return new TimeInstant(DateTimeHelper.parseIsoString2DateTime(time),
                                   DateTimeHelper.getTimeLengthBeforeTimeZone(time));
        } catch (DateTimeParseException ex) {
            return new TimeInstant(new IndeterminateValue(time));
        }
    }

    protected TemporalFilter decodeTemporalFilter(String name, List<String> parameterValues)
            throws DecodingException {
        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        }
        String value;
        String valueReference;
        String operator;

        switch (parameterValues.size()) {
            case 2:
                valueReference = parameterValues.get(0);
                value = parameterValues.get(1);
                return createTemporalFilter(value, name, valueReference);
            case 3:
                valueReference = parameterValues.get(0);
                operator = parameterValues.get(1);
                value = parameterValues.get(2);
                return createTemporalFilter(name, value, operator, valueReference);
            default:
                throw new DecodingException(name, "The parameter value is not valid!");
        }
    }

    private TemporalFilter createTemporalFilter(String value, String name, String valueReference)
            throws DecodingException {
        switch (value.split("/").length) {
            case 1:
                return createTemporalFilter(name, value, TimeOperator.TM_Equals, valueReference);
            case 2:
                return createTemporalFilter(name, value, TimeOperator.TM_During, valueReference);
            default:
                throw new DecodingException(name, "The paramter value '%s' is invalid!", value);
        }
    }

    private TemporalFilter createTemporalFilter(String name, String value, String operator, String valueReference)
            throws DecodingException {
        TimeOperator timeOperator;
        try {
            timeOperator = TimeOperator.from(operator);
        } catch (IllegalArgumentException e1) {
            try {
                timeOperator = TimeOperator.from(TimeOperator2.from(operator));
            } catch (IllegalArgumentException e2) {
                throw new DecodingException(name, "Unsupported operator '%s'!", operator);
            }
        }
        return createTemporalFilter(name, value, timeOperator, valueReference);
    }

    private TemporalFilter createTemporalFilter(String name, String value, TimeOperator timeOperator,
                                                String valueReference) throws DecodingException {
        String[] times = value.split("/");
        final Time time;
        if (times.length == 1 && timeOperator != TimeOperator.TM_During) {
            time = decodeTimeInstant(name, times[0]);
        } else if (times.length == 2 & timeOperator == TimeOperator.TM_During) {
            time = decodeTimePeriod(name, times);
        } else {
            throw new DecodingException(name, "The parameter value '%s' is invalid!", value);
        }
        return new TemporalFilter(timeOperator, time, valueReference);
    }

    protected ThrowingTriConsumer<R, String, List<String>, DecodingException> decodeSpatialFilter(
            ThrowingBiConsumer<? super R, ? super SpatialFilter, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeSpatialFilter(name, value));
    }

    protected SpatialFilter decodeSpatialFilter(String name, List<String> parameterValues)
            throws DecodingException {

        List<String> values;
        Geometry geometry;
        GeometryFactory factory;
        int srid;
        String valueReference;

        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        } else if (parameterValues.size() < 5 || parameterValues.size() > 6) {
            throw new DecodingException(name, "The parameter value is not valid!");
        } else if (parameterValues instanceof RandomAccess) {
            values = parameterValues;
        } else {
            values = new ArrayList<>(parameterValues);
        }

        valueReference = values.get(0);
        values = values.subList(1, values.size());

        String crs = values.get(values.size() - 1);
        if (crs.startsWith(this.srsNamePrefixV2) || crs.startsWith(this.srsNamePrefixV1)) {
            values = values.subList(0, values.size() - 1);
            srid = CRSHelper.parseSrsName(crs);
        } else {
            srid = this.storageEPSG;
        }

        if (values.size() != 4) {
            throw new DecodingException(name, "The parameter value is not valid!");
        }

        if (srid > 0) {
            factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
        } else {
            factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
        }

        double[] coordinates = values.stream().mapToDouble(Double::valueOf).toArray();

        geometry = factory.createPolygon(new Coordinate[] {
            new Coordinate(coordinates[0], coordinates[1]),
            new Coordinate(coordinates[0], coordinates[3]),
            new Coordinate(coordinates[2], coordinates[3]),
            new Coordinate(coordinates[2], coordinates[1]),
            new Coordinate(coordinates[0], coordinates[1])
        });

        return new SpatialFilter(SpatialOperator.BBOX, geometry, valueReference);
    }
}

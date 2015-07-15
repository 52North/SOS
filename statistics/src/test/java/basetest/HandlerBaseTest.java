package basetest;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.request.RequestContext;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.util.net.IPAddress;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.statistics.api.interfaces.geolocation.IStatisticsLocationUtil;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Geometry;

public class HandlerBaseTest extends MockitoBaseTest {

    protected static RequestContext requestContext;
    protected static SpatialFilter spatialFilter;
    protected static TemporalFilter temporalFilter;
    protected static OmObservation omObservation;

    @Mock
    protected IStatisticsLocationUtil locationUtil;

    @BeforeClass
    public static void beforeClass() throws OwsExceptionReport {
        requestContext = new RequestContext();
        requestContext.setContentType("application/json");
        requestContext.setAcceptType(Arrays.asList(new MediaType("*", "*")));
        requestContext.setIPAddress(new IPAddress("123.123.123.123"));

        Geometry geom = JTSHelper.createGeometryFromWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", 4326);
        spatialFilter = new SpatialFilter(SpatialOperator.BBOX, geom, "value-ref");

        temporalFilter = new TemporalFilter(TimeOperator.TM_Equals, new TimeInstant(DateTime.now()), "nothing");

        createOmObservation();
    }

    private static void createOmObservation() throws OwsExceptionReport {
        omObservation = new OmObservation();
        omObservation.setIdentifier("id");

        // constellation
        OmObservationConstellation constellation = new OmObservationConstellation();
        constellation.setProcedure(new SosProcedureDescriptionUnknowType("id", "format", "xml"));
        constellation.setObservableProperty(new OmObservableProperty("id", "desc", "unit", "value"));
        constellation.setFeatureOfInterest(new OmObservation() {
            {
                setIdentifier("foi");
            }
        });
        constellation.setObservationType("obstype");
        omObservation.setObservationConstellation(constellation);

        // result time
        // valid time
        omObservation.setValidTime(new TimePeriod(DateTime.now(), DateTime.now().plusHours(1)));
        omObservation.setResultTime(new TimeInstant(DateTime.now()));

        // pheomenon time
        SingleObservationValue<String> value = new SingleObservationValue<String>();
        value.setValue(new TextValue("anyadat"));
        value.setPhenomenonTime(new TimeInstant(DateTime.now()));
        omObservation.setValue(value);

        // spatial profile
        NamedValue<Geometry> spatial = new NamedValue<>();
        spatial.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        GeometryValue geometryValue = new GeometryValue(JTSHelper.createGeometryFromWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", 4326));
        spatial.setValue(geometryValue);
        omObservation.addParameter(spatial);
    }
}

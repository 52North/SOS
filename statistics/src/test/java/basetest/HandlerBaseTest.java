package basetest;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.request.RequestContext;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.util.net.IPAddress;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.statistics.api.interfaces.geolocation.IStatisticsLocationUtil;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Geometry;

public class HandlerBaseTest extends MockitoBaseTest {

    protected static RequestContext requestContext;
    protected static SpatialFilter spatialFilter;
    protected static TemporalFilter temporalFilter;

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
    }
}

package basetest;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.mockito.Mock;
import org.n52.iceland.request.RequestContext;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.util.net.IPAddress;
import org.n52.sos.statistics.api.interfaces.geolocation.IStatisticsLocationUtil;

public class HandlerBaseTest extends MockitoBaseTest {

    protected static RequestContext requestContext;

    @Mock
    IStatisticsLocationUtil locationUtil;

    @BeforeClass
    public static void beforeClass() {
        requestContext = new RequestContext();
        requestContext.setContentType("application/json");
        requestContext.setAcceptType(Arrays.asList(new MediaType("*", "*")));
        requestContext.setIPAddress(new IPAddress("123.123.123.123"));
    }
}

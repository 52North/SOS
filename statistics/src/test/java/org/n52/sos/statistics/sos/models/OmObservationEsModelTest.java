package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.statistics.sos.SosDataMapping;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Geometry;

public class OmObservationEsModelTest {

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() throws OwsExceptionReport {
        OmObservation obs = new OmObservation();
        obs.setIdentifier("id");

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
        obs.setObservationConstellation(constellation);

        // result time
        // valid time
        obs.setValidTime(new TimePeriod(DateTime.now(), DateTime.now().plusHours(1)));
        obs.setResultTime(new TimeInstant(DateTime.now()));

        // pheomenon time
        SingleObservationValue<String> value = new SingleObservationValue<String>();
        value.setValue(new TextValue("anyadat"));
        value.setPhenomenonTime(new TimeInstant(DateTime.now()));
        obs.setValue(value);

        // spatial profile
        NamedValue<Geometry> spatial = new NamedValue<>();
        spatial.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        GeometryValue geometryValue = new GeometryValue(JTSHelper.createGeometryFromWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", 4326));
        spatial.setValue(geometryValue);
        obs.addParameter(spatial);

        Map<String, Object> map = OmObservationEsModel.convert(obs);

        Assert.assertNotNull(map.get(SosDataMapping.OMOBS_RESULT_TIME));
        Assert.assertNotNull(map.get(SosDataMapping.OMOBS_VALID_TIME));
        Assert.assertNotNull(map.get(SosDataMapping.OMOBS_PHENOMENON_TIME));

        Map<String, Object> constellationMap = (Map<String, Object>) map.get(SosDataMapping.OMOBS_CONSTELLATION);
        Assert.assertEquals(constellationMap.get(SosDataMapping.OMOCONSTELL_PROCEDURE), "id");
        Assert.assertEquals(constellationMap.get(SosDataMapping.OMOCONSTELL_OBSERVABLE_PROPERTY), "id");
        Assert.assertEquals(constellationMap.get(SosDataMapping.OMOCONSTELL_OBSERVATION_TYPE), "obstype");
        Assert.assertEquals(constellationMap.get(SosDataMapping.OMOCONSTELL_FEATURE_OF_INTEREST), "foi");

        Assert.assertNotNull(map.get(SosDataMapping.OMOBS_SAMPLING_GEOMETRY));

    }
}

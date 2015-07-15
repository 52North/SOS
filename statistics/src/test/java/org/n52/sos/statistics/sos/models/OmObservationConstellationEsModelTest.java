package org.n52.sos.statistics.sos.models;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.statistics.sos.SosDataMapping;

public class OmObservationConstellationEsModelTest {

    @Test
    public void validateAllFields() {
        OmObservationConstellation obs = new OmObservationConstellation();
        obs.setProcedure(new SosProcedureDescriptionUnknowType("id", "format", "xml"));
        obs.setObservableProperty(new OmObservableProperty("id", "desc", "unit", "value"));
        obs.setFeatureOfInterest(new OmObservation() {
            {
                setIdentifier("foi");
            }
        });
        obs.setObservationType("obstype");

        Map<String, Object> map = OmObservationConstellationEsModel.convert(obs);

        Assert.assertEquals("id", map.get(SosDataMapping.OMOCONSTELL_PROCEDURE));
        Assert.assertEquals("id", map.get(SosDataMapping.OMOCONSTELL_OBSERVABLE_PROPERTY));
        Assert.assertEquals("obstype", map.get(SosDataMapping.OMOCONSTELL_OBSERVATION_TYPE));
        Assert.assertEquals("foi", map.get(SosDataMapping.OMOCONSTELL_FEATURE_OF_INTEREST));
    }

    @Test
    public void nullInputValue() {
        Assert.assertNull(OmObservationConstellationEsModel.convert((OmObservationConstellation) null));
        Assert.assertNull(OmObservationConstellationEsModel.convert((List<OmObservationConstellation>) null));
    }
}

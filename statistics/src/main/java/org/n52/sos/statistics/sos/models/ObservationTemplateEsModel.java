package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.statistics.sos.SosDataMapping;

public class ObservationTemplateEsModel extends AbstractElasticsearchModel {

    private final OmObservationConstellation constellation;

    private ObservationTemplateEsModel(OmObservationConstellation constellation) {
        this.constellation = constellation;
    }

    public static Map<String, Object> convert(OmObservationConstellation observationConstellation) {
        return new ObservationTemplateEsModel(observationConstellation).getAsMap();
    }

    @Override
    protected Map<String, Object> getAsMap() {
        put(SosDataMapping.OMOC_PROCEDURE, constellation.getProcedure().getIdentifier());
        put(SosDataMapping.OMOC_OBSERVABLE_PROPERTY, constellation.getObservableProperty().getFirstName());
        put(SosDataMapping.OMOC_FEATURE_OF_INTEREST, constellation.getFeatureOfInterest().getFirstName());

        return dataMap;
    }

}

package org.n52.sos.statistics.sos.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.statistics.sos.SosDataMapping;

public class OmObservationConstellationEsModel extends AbstractElasticsearchModel {

    private final OmObservationConstellation constellation;

    private OmObservationConstellationEsModel(OmObservationConstellation constellation) {
        this.constellation = constellation;
    }

    public static Map<String, Object> convert(OmObservationConstellation observationConstellation) {
        if (observationConstellation == null) {
            return null;
        }
        return new OmObservationConstellationEsModel(observationConstellation).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<OmObservationConstellation> observationConstellation) {
        if (observationConstellation == null || observationConstellation.isEmpty()) {
            return null;
        }
        return observationConstellation.stream().map(OmObservationConstellationEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        if (constellation.getProcedure() != null) {
            put(SosDataMapping.OMOCONSTELL_PROCEDURE, constellation.getProcedure().getIdentifier());
        }
        if (constellation.getObservableProperty() != null) {
            put(SosDataMapping.OMOCONSTELL_OBSERVABLE_PROPERTY, constellation.getObservableProperty().getIdentifier());
        }
        if (constellation.getFeatureOfInterest() != null) {
            put(SosDataMapping.OMOCONSTELL_FEATURE_OF_INTEREST, constellation.getFeatureOfInterest().getIdentifier());
        }

        put(SosDataMapping.OMOCONSTELL_OBSERVATION_TYPE, constellation.getObservationType());

        return dataMap;
    }
}

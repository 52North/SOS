package org.n52.sos.statistics.sos.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.statistics.sos.SosDataMapping;

public class OmObservationEsModel extends AbstractElasticsearchModel {

    private final OmObservation observation;

    private OmObservationEsModel(OmObservation observation) {
        this.observation = observation;
    }

    public static Map<String, Object> convert(OmObservation observation) {
        if (observation == null) {
            return null;
        }
        return new OmObservationEsModel(observation).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<OmObservation> observation) {
        if (observation == null || observation.isEmpty()) {
            return null;
        }
        return observation.stream().map(OmObservationEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        Map<String, Object> constellation = OmObservationConstellationEsModel.convert(observation.getObservationConstellation());
        put(SosDataMapping.OMOBS_CONSTELLATION, constellation);

        if (observation.getSpatialFilteringProfileParameter() != null) {
            SpatialFilter dummy = new SpatialFilter(null, observation.getSpatialFilteringProfileParameter().getValue().getValue(), null);
            put(SosDataMapping.OMOBS_SAMPLING_GEOMETRY, SpatialFilterEsModel.convert(dummy));
        }

        put(SosDataMapping.OMOBS_PHENOMENON_TIME, TimeEsModel.convert(observation.getPhenomenonTime()));
        put(SosDataMapping.OMOBS_RESULT_TIME, TimeEsModel.convert(observation.getResultTime()));
        put(SosDataMapping.OMOBS_VALID_TIME, TimeEsModel.convert(observation.getValidTime()));

        return dataMap;
    }

}

package org.n52.sos.converter;

import com.google.common.collect.Lists;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.util.CollectionHelper;

import java.util.LinkedList;
import java.util.List;

import static org.n52.sos.converter.EprtrConverter.INDICATOR;

public class MergeObservation {

    EprtrConverter eprtrConverter = new EprtrConverter();
    OwsServiceResponse mergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        response.setObservationCollection(
                ObservationStream.of(mergeObservations(eprtrConverter.mergeStreamingData(response.getObservationCollection()))));
        eprtrConverter.checkObservationFeatures(response.getObservationCollection());
        return response;
    }

    private List<OmObservation> mergeObservations(List<OmObservation> observations) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(observations)) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observations) {
                if (eprtrConverter.checkForProcedure(sosObservation)) {
                    if (mergedObservations.isEmpty()) {
                        if (!sosObservation.isSetGmlID()) {
                            sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                        }
                        mergedObservations.add(eprtrConverter.convertObservation(sosObservation));
                    } else {
                        boolean combined = false;
                        for (final OmObservation combinedSosObs : mergedObservations) {
                            if (eprtrConverter.checkForMerge(combinedSosObs, sosObservation, INDICATOR)) {
                                eprtrConverter.mergeValues(combinedSosObs, eprtrConverter.convertObservation(sosObservation));
                                combined = true;
                                break;
                            }
                        }
                        if (!combined) {
                            mergedObservations.add(eprtrConverter.convertObservation(sosObservation));
                        }
                    }
                }
            }
            return mergedObservations;
        }
        return Lists.newArrayList(observations);
    }
}

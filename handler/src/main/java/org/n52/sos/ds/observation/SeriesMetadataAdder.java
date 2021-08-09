package org.n52.sos.ds.observation;

import java.util.Optional;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

public abstract class SeriesMetadataAdder {
    private OmObservation omObservation;

    private DatasetEntity dataset;

    public SeriesMetadataAdder(OmObservation omObservation, DatasetEntity dataset) {
        this.omObservation = omObservation;
        this.dataset = dataset;
    }
    
    protected Optional<Object> getMetadataElement(DatasetEntity dataset, String domain, String name) {
        if (dataset.hasParameters()) {
            for (ParameterEntity<?> parameter : dataset.getParameters()) {
                if (domain.equals(parameter.getDomain()) && name.equals(parameter.getName())) {
                    return Optional.ofNullable(parameter.getValue());
                }
            }
        }
        return Optional.empty();
    }

    protected CodedException createMetadataInvalidException(String metadataKey, String metadataContent,
            IllegalArgumentException iae) {
        CodedException e = new NoApplicableCodeException().withMessage(
                "Series Metadata '%s' for Series '%s' "
                        + "could not be parsed '%s'. Please contact the administrator of this service.",
                metadataKey, getDataset().getId(), metadataContent);
        if (iae != null) {
            return e.causedBy(iae);
        } else {
            return e;
        }
    }

    public OmObservation result() {
        return getObservation();
    }
    
    protected OmObservation getObservation() {
        return omObservation;
    }
    
    protected DatasetEntity getDataset() {
        return dataset;
    }
}

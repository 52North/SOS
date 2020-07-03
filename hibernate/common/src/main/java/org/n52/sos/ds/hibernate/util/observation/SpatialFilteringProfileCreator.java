package org.n52.sos.ds.hibernate.util.observation;

import org.locationtech.jts.geom.Geometry;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.util.GeometryHandler;

public class SpatialFilteringProfileCreator {

    private final GeometryHandler geometryHandler;

    public SpatialFilteringProfileCreator(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    public NamedValue<?> create(Geometry samplingGeometry) throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(
                new GeometryValue(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

    private GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

}

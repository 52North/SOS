package org.n52.sos.ds.hibernate.create;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.feature.SpecimenEntity;
import org.n52.series.db.beans.feature.inspire.EnvironmentalMonitoringFacilityEntity;
import org.n52.series.db.beans.feature.wml.MonitoringPointEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.feature.FeatureVisitor;
import org.n52.sos.ds.feature.create.EnvironmentalMonitoringFacilityCreator;
import org.n52.sos.ds.feature.create.FeatureOfInterestCreator;
import org.n52.sos.ds.feature.create.MonitoringPointCreator;
import org.n52.sos.ds.feature.create.SpecimenCreator;

import com.google.common.collect.Lists;

public class HibernateFeatureVisitor implements FeatureVisitor<AbstractFeature> {

    private HibernateFeatureVisitorContext context;

    public HibernateFeatureVisitor(HibernateFeatureVisitorContext context) {
        this.context = context;
    }

    public AbstractFeature visit(FeatureEntity f) throws OwsExceptionReport {
        if (f instanceof SpecimenEntity) {
            return visit((SpecimenEntity) f);
        } else if (f instanceof EnvironmentalMonitoringFacilityEntity) {
            return visit((EnvironmentalMonitoringFacilityEntity) f);
        } else if (f instanceof MonitoringPointEntity) {
            return visit((MonitoringPointEntity) f);
        }
        return checkGeometry(new FeatureOfInterestCreator(context).create(f), f);
    }

    public AbstractFeature visit(SpecimenEntity f) throws OwsExceptionReport {
        return checkGeometry(new SpecimenCreator(context).create(f), f);
    }

    public AbstractFeature visit(EnvironmentalMonitoringFacilityEntity f) throws OwsExceptionReport {
        return checkGeometry(new EnvironmentalMonitoringFacilityCreator(context)
                .create(f), f);
    }

    @Override
    public AbstractFeature visit(MonitoringPointEntity f) throws OwsExceptionReport {
        return checkGeometry(new MonitoringPointCreator(context).create(f), f);
    }

    private AbstractFeature checkGeometry(AbstractFeature feature, AbstractFeatureEntity f) throws OwsExceptionReport {
        if (feature instanceof AbstractSamplingFeature && !((AbstractSamplingFeature) feature).isSetGeometry()
                && !f.isSetGeometry() && !f.isSetUrl() && getContext().getSession() != null) {
            if (getContext().createFeatureGeometryFromSamplingGeometries()) {
                int srid = getContext().getGeometryHandler().getStorageEPSG();
                if (getContext().getDaoFactory().getObservationDAO()
                        .getSamplingGeometriesCount(feature.getIdentifier(), getContext().getSession())
                        .longValue() < 100) {
                    List<Geometry> geometries = getContext().getDaoFactory().getObservationDAO()
                            .getSamplingGeometries(feature.getIdentifier(), getContext().getSession());
                    if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(geometries)) {
                        List<Coordinate> coordinates = Lists.newLinkedList();
                        Geometry lastGeoemtry = null;
                        for (Geometry geometry : geometries) {
                            if (geometry != null) {
                                if (lastGeoemtry == null || !geometry.equalsTopo(lastGeoemtry)) {
                                    coordinates.add(getContext().getGeometryHandler()
                                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)
                                            .getCoordinate());
                                    lastGeoemtry = geometry;
                                }
                                if (geometry.getSRID() != srid) {
                                    srid = geometry.getSRID();
                                }
                            }
                        }
                        Geometry geom = null;
                        if (coordinates.size() == 1) {
                            geom = new GeometryFactory().createPoint(coordinates.iterator().next());
                        } else {
                            geom = new GeometryFactory()
                                    .createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
                        }
                        geom.setSRID(srid);
                        ((AbstractSamplingFeature) feature).setGeometry(geom);
                    }
                }
            }
        }
        return feature;
    }

    private HibernateFeatureVisitorContext getContext() {
        return context;
    }

}

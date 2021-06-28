package org.n52.sos.proxy.harvest;

import java.util.List;

import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.sensorweb.server.db.query.DatasetQuerySpecifications;
import org.n52.sensorweb.server.db.repositories.core.DatasetRepository;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.sos.proxy.da.InsertionRepository;
import org.springframework.data.jpa.domain.Specification;

public interface HarvesterHelper {

    default ServiceEntity getServiceEntity() {
        ServiceEntity serviceEntity = getServiceEntityFactory().getServiceEntity();
        if (serviceEntity != null && (serviceEntity.getConnector() == null || serviceEntity.getConnector()
                .isEmpty())) {
            serviceEntity.setConnector(getConnectorName());
        }
        return serviceEntity;
    }

    default ServiceEntity getOrInsertServiceEntity() {
        return getInsertionRepository().insertService(getServiceEntity());
    }

    default Specification<DatasetEntity> getDatasetServicQS(ServiceEntity service) {
        DatasetQuerySpecifications dQs = getInsertionRepository().getDatasetQuerySpecification();
        return dQs.matchServices(Long.toString(service.getId()));
    }

    default List<DatasetEntity> getAllDatasets(ServiceEntity service) {
        return getDatasetRepository().findAll(getDatasetServicQS(service));
    }

    InsertionRepository getInsertionRepository();

    ServiceEntityFactory getServiceEntityFactory();

    DatasetRepository getDatasetRepository();

    String getConnectorName();

}

package org.n52.sensorweb.server.db;

import org.n52.sensorweb.server.db.repositories.ParameterDataRepository;
import org.n52.series.db.beans.ServiceEntity;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ServiceRepository extends ParameterDataRepository<ServiceEntity> {

}

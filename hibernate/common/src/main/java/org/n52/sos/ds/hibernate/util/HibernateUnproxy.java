package org.n52.sos.ds.hibernate.util;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.n52.series.db.beans.DataEntity;

public interface HibernateUnproxy {

    default DataEntity<?> unproxy(DataEntity<?> dataEntity, Session session) {
        return !(dataEntity instanceof HibernateProxy) ? dataEntity
                : ((HibernateProxy) dataEntity).getHibernateLazyInitializer()
                        .getSession() == null ? unproxy(session.load(DataEntity.class, dataEntity.getId()), session)
                                : (DataEntity<?>) Hibernate.unproxy(dataEntity);
    }

}

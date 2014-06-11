package org.n52.sos.ds.datasource;

import org.n52.sos.ds.EnviroCarHibernateConstants;

public class EnviroCarMongoDBDatasoure extends MongoDBDatasource {
    
    
    public EnviroCarMongoDBDatasoure() {
        setDatabaseDefault("envirocar");
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return EnviroCarHibernateConstants.ENVIROCAR_DATASOURCE_DAO_IDENTIFIER;
    }
    
}

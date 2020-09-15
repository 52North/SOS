package org.n52.sos.ds.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    private static final String SIMPLE = "simple";

    private static final String EREPORTING = "ereporting";

    private static final String TRANSACTIONAL = "transactional";

    private static final String DATASET = "dataset";

    private static final String SAMPLING = "sampling";

    @Inject
    private SessionFactoryProvider provider;

//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        Properties p = provider.getConfiguration()
//                .getProperties();
//        Map<String, String> map = new HashMap<>();
//        for (Entry<Object, Object> entry : p.entrySet()) {
//            map.put(entry.getKey()
//                    .toString(),
//                    entry.getValue()
//                            .toString());
//        }
//        return (EntityManagerFactory) sessionFactory();
//    }

    @Bean(name = {"sessionFactory", "entityManagerFactory"})
    public SessionFactory sessionFactory() {
        return provider.getConfiguration()
                .buildSessionFactory(provider.getServiceRegistry());
    }

    @Bean
    public DataSource dataSource(SessionFactory sessionFactory) {
        ConnectionProvider cp = sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(ConnectionProvider.class);
        return cp.unwrap(DataSource.class);
    }

    private String getPersitenceUnit(Properties p) {
        return p.contains(HibernateDatasourceConstants.HIBERNATE_DIRECTORY)
                ? getPersitenceUnit(p.getProperty(HibernateDatasourceConstants.HIBERNATE_DIRECTORY))
                : getPersitenceUnit("");
    }

    private String getPersitenceUnit(String value) {
        return getConcept(value) + "-" + getExtension(value);
    }

    private String getConcept(String value) {
        return value.contains(SIMPLE) ? SIMPLE : value.contains(EREPORTING) ? EREPORTING : TRANSACTIONAL;
    }

    private String getExtension(String value) {
        return value.contains(SAMPLING) ? SAMPLING : DATASET;
    }

}

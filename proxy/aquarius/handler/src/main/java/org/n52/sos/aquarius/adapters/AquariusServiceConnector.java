package org.n52.sos.aquarius.adapters;

import javax.inject.Inject;

import org.n52.sensorweb.server.helgoland.adapters.connector.AbstractConnector;
import org.n52.sensorweb.server.helgoland.adapters.connector.AbstractServiceConstellation;
import org.n52.sensorweb.server.helgoland.adapters.connector.ConnectorConfiguration;
import org.n52.sos.aquarius.adapters.harvest.AquariusServiceConstellation;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;

public class AquariusServiceConnector extends AbstractConnector {

    @Inject
    private AquariusConnectionFactory factory;

    @Override
    public AbstractServiceConstellation getConstellation(ConnectorConfiguration configuration) {
        return new AquariusServiceConstellation(factory);
    }

}

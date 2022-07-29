package org.n52.sos.aquarius.adapters.config;

import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.adapters.AquariusServiceConnector;
import org.n52.sos.prox.config.AbstractProxyConfigurationProvider;

public class AquariusConfigurationProvider extends AbstractProxyConfigurationProvider {

    @Override
    protected String getGroup() {
        return AquariusConstants.GROUP;
    }

    @Override
    protected String getName() {
        return AquariusConstants.NAME;
    }

    @Override
    protected String getType() {
        return AquariusConstants.TYPE;
    }

    @Override
    protected String getConnector() {
        return AquariusServiceConnector.class.getName();
    }

    @Override
    protected String getItemName() {
        return "Aquarius Proxy";
    }

    @Override
    protected String getRestPath() {
        return AquariusConstants.AQUARIUS_PATH;
    }

}

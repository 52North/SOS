/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
/**

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH

 *

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 *

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 *

 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 *

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.

 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.

 */
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;

import org.hibernate.Session;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.profile.Profile;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractOmObservationCreator {
    private final String version;
    private final Session session;
    private final String i18n;

    public AbstractOmObservationCreator(String version, Session session) {
        this.version = version;
        this.session = session;
        this.i18n = ServiceConfiguration.getInstance().getDefaultLanguage();
    }
    
    public AbstractOmObservationCreator(String version, String i18n, Session session) {
        this.version = version;
        this.session = session;
        this.i18n = i18n;
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return Configurator.getInstance().getFeatureQueryHandler();
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    protected String getTokenSeparator() {
        return ServiceConfiguration.getInstance().getTokenSeparator();
    }

    protected String getTupleSeparator() {
        return ServiceConfiguration.getInstance().getTupleSeparator();
    }

    protected String getNoDataValue() {
        return getActiveProfile().getResponseNoDataPlaceholder();
    }

    public abstract List<OmObservation> create() throws OwsExceptionReport,
                                                        ConverterException;

    public String getVersion() {
        return version;
    }

    public Session getSession() {
        return session;
    }
    
    public String getI18N() {
        return i18n;
    }
    
}

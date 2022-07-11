/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure.generator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.DescribedObject;
import org.n52.shetland.ogc.sensorML.v20.PhysicalComponent;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sensorML.v20.SimpleProcess;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

class ProcedureDescriptionGeneratorSml20 extends AbstractProcedureDescriptionGeneratorSml {

    public static final Set<ProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES = CollectionHelper.set(
            new ProcedureDescriptionGeneratorKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
            new ProcedureDescriptionGeneratorKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL));

    ProcedureDescriptionGeneratorSml20(ProfileHandler profileHandler, GeometryHandler geometryHandler,
            I18NDAORepository i18ndaoRepository, ContentCacheController cacheController, String srsNamePrefixUrl,
            boolean isAddOutputsToSensorML, ProcedureDescriptionSettings procedureSettings,
            DbQueryFactory dbQueryFactory) {
        super(profileHandler, geometryHandler, i18ndaoRepository, cacheController, srsNamePrefixUrl,
                isAddOutputsToSensorML, procedureSettings, dbQueryFactory);
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription<?> generateProcedureDescription(ProcedureEntity procedure, Locale i18n,
            Session session) throws OwsExceptionReport {
        setLocale(i18n);
        // 2 try to get position from entity
        if (isStation(procedure, session)) {
            // 2.1 if position is available -> system -> own class <- should
            // be compliant with SWE lightweight profile
            if (procedure.hasChildren()) {
                return new SosProcedureDescription<>(createPhysicalSystem(procedure, session));
            } else {
                return new SosProcedureDescription<>(createPhysicalComponent(procedure, session));
            }
        } else {
            // 2.2 if no position is available -> SimpleProcess -> own class
            if (procedure.hasChildren()) {
                return new SosProcedureDescription<>(createAggregateProcess(procedure, session));
            } else {
                return new SosProcedureDescription<>(createSimpleProcess(procedure, session));
            }
        }
    }

    private PhysicalComponent createPhysicalComponent(ProcedureEntity procedure, Session session)
            throws OwsExceptionReport {
        PhysicalComponent physicalComponent = new PhysicalComponent();
        setIdentifier(physicalComponent, procedure);
        setCommonValues(procedure, physicalComponent, session);
        return physicalComponent;
    }

    private PhysicalSystem createPhysicalSystem(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        PhysicalSystem physicalSystem = new PhysicalSystem();
        setIdentifier(physicalSystem, procedure);
        setCommonValues(procedure, physicalSystem, session);
        physicalSystem.setPosition(createPosition(procedure, session));
        return physicalSystem;
    }

    private SimpleProcess createSimpleProcess(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        SimpleProcess simpleProcess = new SimpleProcess();
        setIdentifier(simpleProcess, procedure);
        setCommonValues(procedure, simpleProcess, session);
        return simpleProcess;
    }

    private AggregateProcess createAggregateProcess(ProcedureEntity procedure, Session session)
            throws OwsExceptionReport {
        AggregateProcess aggregateProcess = new AggregateProcess();
        setIdentifier(aggregateProcess, procedure);
        setCommonValues(procedure, aggregateProcess, session);
        return aggregateProcess;
    }

    private void setIdentifier(DescribedObject describedObject, ProcedureEntity procedure) {
        CodeWithAuthority cwa = new CodeWithAuthority(procedure.getIdentifier(), OGCConstants.UNIQUE_ID);
        describedObject.setIdentifier(cwa);
    }

    @Override
    protected SweAbstractDataComponent getInputComponent(String observableProperty) {
        return new SweText().setDefinition(observableProperty);
    }

}

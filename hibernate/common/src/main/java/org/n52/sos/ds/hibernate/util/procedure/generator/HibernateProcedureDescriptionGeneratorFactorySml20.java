/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.v20.AggregateProcess;
import org.n52.sos.ogc.sensorML.v20.DescribedObject;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.util.CollectionHelper;

/**
 * Generator class for SensorML 2.0 procedure descriptions
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactorySml20 implements HibernateProcedureDescriptionGeneratorFactory {

    private static final List<HibernateProcedureDescriptionGeneratorFactoryKeyType> GENERATOR_KEY_TYPES = CollectionHelper.list(
            new HibernateProcedureDescriptionGeneratorFactoryKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), 
            new HibernateProcedureDescriptionGeneratorFactoryKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL));

    @Override
    public List<HibernateProcedureDescriptionGeneratorFactoryKeyType> getHibernateProcedureDescriptionGeneratorFactoryKeyTypes() {
        return GENERATOR_KEY_TYPES;
    }
    
    @Override
    public SosProcedureDescription create(Procedure procedure, Locale i18n, Session session) throws OwsExceptionReport {
        return new HibernateProcedureDescriptionGeneratorSml20().generateProcedureDescription(procedure, i18n, session);
    }
        
    private class HibernateProcedureDescriptionGeneratorSml20 extends AbstractHibernateProcedureDescriptionGeneratorSml {
        
    
        @Override
        public SosProcedureDescription generateProcedureDescription(Procedure procedure, Locale i18n, Session session)
                throws OwsExceptionReport {
            setLocale(i18n);
            // 2 try to get position from entity
            if (procedure.isSpatial()) {
                // 2.1 if position is available -> system -> own class <- should
                // be compliant with SWE lightweight profile
                if (hasChildProcedure(procedure.getIdentifier())) {
                    return createPhysicalSystem(procedure, session);
                } else {
                    return createPhysicalComponent(procedure, session);
                }
            } else {
                // 2.2 if no position is available -> SimpleProcess -> own class
//                if (hasChildProcedure(procedure.getIdentifier())) {
//                    return createAggregateProcess(procedure, session);
//                } else {
                    return createSimpleProcess(procedure, session);
//                }
            }
        }

        private SosProcedureDescription createPhysicalComponent(Procedure procedure, Session session) throws OwsExceptionReport {
            PhysicalComponent physicalComponent = new PhysicalComponent();
            setIdentifier(physicalComponent, procedure);
            setCommonValues(procedure, physicalComponent, session);
            return physicalComponent;
        }

        private SosProcedureDescription createPhysicalSystem(Procedure procedure, Session session) throws OwsExceptionReport {
            PhysicalSystem physicalSystem = new PhysicalSystem();
            setIdentifier(physicalSystem, procedure);
            setCommonValues(procedure, physicalSystem, session);
            physicalSystem.setPosition(createPosition(procedure));
            return physicalSystem;
        }
    
        private SosProcedureDescription createSimpleProcess(Procedure procedure, Session session) throws OwsExceptionReport {
            SimpleProcess simpleProcess = new SimpleProcess();
            setIdentifier(simpleProcess, procedure);
            setCommonValues(procedure, simpleProcess, session);
            return simpleProcess;
        }
    
        private SosProcedureDescription createAggregateProcess(Procedure procedure, Session session) throws OwsExceptionReport {
            AggregateProcess aggregateProcess = new AggregateProcess();
            setIdentifier(aggregateProcess, procedure);
            setCommonValues(procedure, aggregateProcess, session);
            return aggregateProcess;
        }

        private void setIdentifier(DescribedObject describedObject, Procedure procedure) {
            CodeWithAuthority cwa = new CodeWithAuthority(procedure.getIdentifier());
            if (procedure.isSetCodespace()) {
                cwa.setCodeSpace(procedure.getCodespace().getCodespace());
            } else {
                cwa.setCodeSpace(OGCConstants.UNIQUE_ID);
            }
            describedObject.setIdentifier(cwa);
        }

        @Override
        protected SweAbstractDataComponent getInputComponent(String observableProperty) {
            return  new SweText().setDefinition(observableProperty);
        }
    }
}

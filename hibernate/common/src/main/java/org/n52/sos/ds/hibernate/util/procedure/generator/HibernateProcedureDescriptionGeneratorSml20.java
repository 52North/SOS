package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
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
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class HibernateProcedureDescriptionGeneratorSml20 extends AbstractHibernateProcedureDescriptionGeneratorSml {
    public static final Set<HibernateProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES = CollectionHelper.set(
            new HibernateProcedureDescriptionGeneratorKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
            new HibernateProcedureDescriptionGeneratorKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL));

    public HibernateProcedureDescriptionGeneratorSml20(ProfileHandler profileHandler, EntitiyHelper entitiyHelper,
                                                       GeometryHandler geometryHandler, DaoFactory daoFactory,
                                                       I18NDAORepository i18NDAORepository,
                                                       ContentCacheController cacheController) {
        super(profileHandler, entitiyHelper, geometryHandler, daoFactory, i18NDAORepository, cacheController);
    }

    @Override
    public SosProcedureDescription<?> generateProcedureDescription(Procedure procedure, Locale i18n, Session session)
            throws OwsExceptionReport {
        setLocale(i18n);
        // 2 try to get position from entity
        if (procedure.isSpatial()) {
            // 2.1 if position is available -> system -> own class <- should
            // be compliant with SWE lightweight profile
            if (hasChildProcedure(procedure.getIdentifier())) {
                return new SosProcedureDescription<>(createPhysicalSystem(procedure, session));
            } else {
                return new SosProcedureDescription<>(createPhysicalComponent(procedure, session));
            }
        } else {
            // 2.2 if no position is available -> SimpleProcess -> own class
            //                if (hasChildProcedure(procedure.getIdentifier())) {
            //                    return createAggregateProcess(procedure, session);
            //                } else {
            return new SosProcedureDescription<>(createSimpleProcess(procedure, session));
            //                }
        }
    }

    private PhysicalComponent createPhysicalComponent(Procedure procedure, Session session) throws OwsExceptionReport {
        PhysicalComponent physicalComponent = new PhysicalComponent();
        setIdentifier(physicalComponent, procedure);
        setCommonValues(procedure, physicalComponent, session);
        return physicalComponent;
    }

    private PhysicalSystem createPhysicalSystem(Procedure procedure, Session session) throws OwsExceptionReport {
        PhysicalSystem physicalSystem = new PhysicalSystem();
        setIdentifier(physicalSystem, procedure);
        setCommonValues(procedure, physicalSystem, session);
        physicalSystem.setPosition(createPosition(procedure));
        return physicalSystem;
    }

    private SimpleProcess createSimpleProcess(Procedure procedure, Session session) throws OwsExceptionReport {
        SimpleProcess simpleProcess = new SimpleProcess();
        setIdentifier(simpleProcess, procedure);
        setCommonValues(procedure, simpleProcess, session);
        return simpleProcess;
    }

    private AggregateProcess createAggregateProcess(Procedure procedure, Session session) throws OwsExceptionReport {
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
        return new SweText().setDefinition(observableProperty);
    }

    @Override
    public Set<HibernateProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

}

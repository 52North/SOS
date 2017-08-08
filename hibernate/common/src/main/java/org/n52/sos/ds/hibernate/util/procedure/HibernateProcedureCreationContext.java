package org.n52.sos.ds.hibernate.util.procedure;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class HibernateProcedureCreationContext
        extends
        AbstractProcedureCreationContext {

    private DaoFactory daoFactory;

    @Inject
    public HibernateProcedureCreationContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            HibernateProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            DaoFactory daoFactory,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler,
            BindingRepository bindingRepository,
            ServiceOperatorRepository serviceOperatorRepository,
            ContentCacheController contentCacheController,
            ProcedureDescriptionSettings procedureSettings) {
        super(serviceMetadataRepository, decoderRepository, factoryRepository, i18nr, converterRepository,
                geometryHandler, bindingRepository, serviceOperatorRepository, contentCacheController, procedureSettings);
        this.daoFactory = daoFactory;
    }

    /**
     * @return the daoFactory
     */
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

}

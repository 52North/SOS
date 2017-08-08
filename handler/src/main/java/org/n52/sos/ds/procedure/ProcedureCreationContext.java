package org.n52.sos.ds.procedure;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class ProcedureCreationContext
        extends
        AbstractProcedureCreationContext {

    @Inject
    public ProcedureCreationContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            ProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler,
            BindingRepository bindingRepository,
            ServiceOperatorRepository serviceOperatorRepository,
            ContentCacheController contentCacheController,
            ProcedureDescriptionSettings procedureSettings) {
        super(serviceMetadataRepository, decoderRepository, factoryRepository, i18nr, converterRepository,
                geometryHandler, bindingRepository, serviceOperatorRepository, contentCacheController,
                procedureSettings);
    }

}

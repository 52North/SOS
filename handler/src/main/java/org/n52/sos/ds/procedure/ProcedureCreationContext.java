package org.n52.sos.ds.procedure;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.SosSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class ProcedureCreationContext {

    private DecoderRepository decoderRepository;
    private ProcedureDescriptionGeneratorFactoryRepository factoryRepository;
    private String sensorDirectory;
    private I18NDAORepository i18nr;
    private ConverterRepository converterRepository;
    private GeometryHandler geometryHandler;
    private OwsServiceMetadataRepository serviceMetadataRepository;

    @Inject
    public ProcedureCreationContext(OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            ProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler) {
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.decoderRepository = decoderRepository;
        this.factoryRepository = factoryRepository;
        this.i18nr = i18nr;
        this.converterRepository = converterRepository;
        this.geometryHandler = geometryHandler;
    }

    /**
     * @return the decoderRepository
     */
    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    /**
     * @return the factoryRepository
     */
    public ProcedureDescriptionGeneratorFactoryRepository getFactoryRepository() {
        return factoryRepository;
    }

    @Setting(SosSettings.SENSOR_DIRECTORY)
    public void setSensorDirectory(String dir) {
        this.sensorDirectory = dir;
    }

    public String getSensorDirectory() {
        return sensorDirectory;
    }

    /**
     * @return the i18nr
     */
    public I18NDAORepository getI18nr() {
        return i18nr;
    }

    /**
     * @return the converterRepository
     */
    public ConverterRepository getConverterRepository() {
        return converterRepository;
    }

    /**
     * @return the geometryHandler
     */
    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    /**
     * @return the serviceMetadataRepository
     */
    public OwsServiceMetadataRepository getServiceMetadataRepository() {
        return serviceMetadataRepository;
    }

}

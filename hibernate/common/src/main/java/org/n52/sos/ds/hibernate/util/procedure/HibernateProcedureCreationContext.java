package org.n52.sos.ds.hibernate.util.procedure;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.SosSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class HibernateProcedureCreationContext {

    private DecoderRepository decoderRepository;
    private HibernateProcedureDescriptionGeneratorFactoryRepository factoryRepository;
    private String sensorDirectory;
    private I18NDAORepository i18nr;
    private DaoFactory daoFactory;
    private ConverterRepository converterRepository;
    private GeometryHandler geometryHandler;
    private OwsServiceMetadataRepository serviceMetadataRepository;

    @Inject
    public HibernateProcedureCreationContext(OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            HibernateProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            DaoFactory daoFactory,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler) {
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.decoderRepository = decoderRepository;
        this.factoryRepository = factoryRepository;
        this.i18nr = i18nr;
        this.daoFactory = daoFactory;
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
    public HibernateProcedureDescriptionGeneratorFactoryRepository getFactoryRepository() {
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
     * @return the daoFactory
     */
    public DaoFactory getDaoFactory() {
        return daoFactory;
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

package org.n52.sos.ds.procedure.generator;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.SettingsService;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

public class ProcedureDescriptionGeneratorFactoryInspireOmpr30 implements ProcedureDescriptionGeneratorFactory {

    private final SettingsService settingsService;
    private final GeometryHandler geometryHandler;
    private final I18NDAORepository i18NDAORepository;
    private final ContentCacheController cacheController;
    private final ProfileHandler profileHandler;

    @Inject
    public ProcedureDescriptionGeneratorFactoryInspireOmpr30(SettingsService settingsService,
                                                               GeometryHandler geometryHandler,
                                                               I18NDAORepository i18NDAORepository,
                                                               ContentCacheController cacheController,
                                                               ProfileHandler profileHandler) {
        this.settingsService = settingsService;
        this.geometryHandler = geometryHandler;
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;
        this.profileHandler = profileHandler;
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(ProcedureDescriptionGeneratorInspireOmpr30.GENERATOR_KEY_TYPES);
    }

    @Override
    public ProcedureDescriptionGenerator create(ProcedureDescriptionGeneratorKey key) {
        ProcedureDescriptionGenerator generator
                = new ProcedureDescriptionGeneratorInspireOmpr30(getProfileHandler(),
                                                                   getGeometryHandler(),
                                                                   getI18NDAORepository(),
                                                                   getCacheController());
        getSettingsService().configureOnce(key);
        return generator;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

    public ContentCacheController getCacheController() {
        return cacheController;
    }

    public ProfileHandler getProfileHandler() {
        return profileHandler;
    }
}

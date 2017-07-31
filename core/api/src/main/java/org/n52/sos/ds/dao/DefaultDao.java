package org.n52.sos.ds.dao;

import java.util.Locale;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;

public interface DefaultDao {

    Locale getDefaultLanguage();

    default Locale getRequestedLocale(OwsServiceRequest request) {
        return LocaleHelper.decode(request.getRequestedLanguage(), getDefaultLanguage());
    }
}

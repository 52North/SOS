package org.n52.sos.ds.hibernate.dao;

import java.util.Locale;

import org.n52.faroe.annotation.Setting;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.sos.ds.dao.DefaultDao;

public abstract class AbstractDaoImpl implements DefaultDao, HibernateDao {

    private Locale defaultLanguage;
    
    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = LocaleHelper.decode(defaultLanguage);
    }
    
    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

}

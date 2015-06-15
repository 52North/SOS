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
package org.n52.sos.web.admin.i18n.ajax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.i18n.metadata.I18NOfferingMetadata;
import org.n52.sos.web.ControllerConstants;

import com.google.common.base.Optional;

@Controller
@RequestMapping(ControllerConstants.Paths.OFFERING_I18N_AJAX_ENDPOINT)
public class OfferingI18NAjaxEndpoint extends AbstractAdminI18NAjaxEndpoint<I18NOfferingMetadata> {

    @Override
    protected boolean isValid(ContentCache cache, String id) {
        return cache.hasOffering(id);
    }

    @Override
    protected I18NOfferingMetadata create(String id) {
        return new I18NOfferingMetadata(id);
    }

    @Override
    protected Class<I18NOfferingMetadata> getType() {
        return I18NOfferingMetadata.class;
    }

    @Override
    protected ContentCacheUpdate getContentCacheUpdate(
            final I18NOfferingMetadata i18n) {
        return new ContentCacheUpdate() {
            @Override
            public void execute() {
                getCache().addSupportedLanguage(i18n.getLocales());
                getCache().setI18nNameForOffering(i18n.getIdentifier(), i18n.getName());
                getCache().setI18nDescriptionForOffering(i18n.getIdentifier(), i18n.getDescription());
            }
        };
    }

}

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
package org.n52.sos.inspire.aqd.persistence;

import javax.inject.Inject;

import org.hibernate.Session;

import org.n52.iceland.coding.decode.DecoderRepository;
import org.n52.iceland.coding.encode.EncoderRepository;
import org.n52.iceland.coding.decode.Decoder;
import org.n52.iceland.coding.decode.JsonDecoderKey;
import org.n52.iceland.coding.encode.Encoder;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.util.JSONUtils;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.config.sqlite.AbstractSQLiteDao;
import org.n52.sos.config.sqlite.AbstractSQLiteDao.ThrowingHibernateAction;
import org.n52.sos.config.sqlite.AbstractSQLiteDao.VoidHibernateAction;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.inspire.aqd.ReportObligation;

import com.fasterxml.jackson.databind.JsonNode;

public class ReportingHeaderSQLiteManager
        extends AbstractSQLiteDao  {
    protected static final String REPORTING_AUTHORITY_KEY = "reportingAuthority";

    protected static final String REPORT_OBLIGATION_KEY_PREFIX = "reportObligation_";

    @Inject
    private DecoderRepository decoderRepository;
    @Inject
    private EncoderRepository encoderRepository;

    public void save(RelatedParty relatedParty) {
        try {
            save(REPORTING_AUTHORITY_KEY, relatedParty);
        } catch (ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save(ReportObligationType type, ReportObligation reportObligation) {
        try {
            save(REPORT_OBLIGATION_KEY_PREFIX + type, reportObligation);
        } catch (ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RelatedParty loadRelatedParty() {
        try {
            return throwingExecute(new LoadReportingAuthorityAction());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ReportObligation loadReportObligation(ReportObligationType type) {
        try {
            return throwingExecute(new LoadJSONFragmentAction<>(REPORT_OBLIGATION_KEY_PREFIX + type, ReportObligation.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void save(final String key, final Object o) throws ConnectionProviderException {
        execute(new SaveAction(o, key));
    }

    private class LoadReportingAuthorityAction extends LoadJSONFragmentAction<RelatedParty> {

        LoadReportingAuthorityAction() {
            super(REPORTING_AUTHORITY_KEY, RelatedParty.class);
        }

    }

    private class SaveAction extends VoidHibernateAction {
        private final String key;

        private final Object o;

        SaveAction(Object o, String key) {
            this.o = o;
            this.key = key;
        }

        @Override
        protected void run(Session session) {
            try {
                Encoder<JsonNode, Object> encoder =
                        encoderRepository.getEncoder(new JSONEncoderKey(o.getClass()));
                JsonNode node = encoder.encode(o);
                String json = JSONUtils.print(node);
                session.saveOrUpdate(new JSONFragment().setID(key).setJSON(json));
            } catch (OwsExceptionReport ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class LoadJSONFragmentAction<T> implements ThrowingHibernateAction<T> {

        private final String key;

        private final Class<T> type;

        LoadJSONFragmentAction(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        @Override
        public T call(Session session) throws OwsExceptionReport {
            JSONFragment entity = (JSONFragment) session.get(JSONFragment.class, this.key);
            return entity == null ? null : decode(entity);
        }

        protected T decode(JSONFragment entity) throws OwsExceptionReport {
            Decoder<T, JsonNode> decoder = decoderRepository.getDecoder(new JsonDecoderKey(type));
            JsonNode node = JSONUtils.loadString(entity.getJSON());
            return decoder.decode(node);
        }

    }
}

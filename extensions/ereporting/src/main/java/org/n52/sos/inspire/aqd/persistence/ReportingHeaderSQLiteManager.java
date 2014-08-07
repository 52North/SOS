package org.n52.sos.inspire.aqd.persistence;

import org.hibernate.Session;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.sqlite.SQLiteManager;
import org.n52.sos.config.sqlite.SQLiteManager.ThrowingHibernateAction;
import org.n52.sos.config.sqlite.SQLiteManager.VoidHibernateAction;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.inspire.aqd.ReportObligation;
import org.n52.sos.inspire.aqd.ReportObligationType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class ReportingHeaderSQLiteManager {
    protected static final String REPORTING_AUTHORITY_KEY = "reportingAuthority";
    protected static final String REPORT_OBLIGATION_KEY_PREFIX
            = "reportObligation_";
    private final SQLiteManager manager
            = new SQLiteManager() {
                @Override
                protected ConnectionProvider createDefaultConnectionProvider() {
                    return new ReportingHeaderSQLiteSessionFactory();
                }
            };

    public void save(RelatedParty relatedParty) {
        try {
            save(REPORTING_AUTHORITY_KEY, relatedParty);
        } catch (ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save(ReportObligationType type,
                     ReportObligation reportObligation) {
        try {
            save(REPORT_OBLIGATION_KEY_PREFIX + type, reportObligation);
        } catch (ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RelatedParty loadRelatedParty() {
        try {
            return manager.execute(new LoadReportingAuthorityAction());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ReportObligation loadReportObligation(ReportObligationType type) {
        try {
            return manager
                    .execute(new LoadJSONFragmentAction<>(REPORT_OBLIGATION_KEY_PREFIX +
                                                          type, ReportObligation.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void save(final String key, final Object o)
            throws ConnectionProviderException {
        manager.execute(new SaveAction(o, key));
    }

    private static class SaveAction extends VoidHibernateAction {
        private final String key;
        private final Object o;

        SaveAction(Object o, String key) {
            this.o = o;
            this.key = key;
        }

        @Override
        protected void run(Session session) {
            try {
                Encoder<JsonNode, Object> encoder = CodingRepository
                        .getInstance().getEncoder(new JSONEncoderKey(o
                                        .getClass()));
                JsonNode node = encoder.encode(o);
                String json = JSONUtils.print(node);
                session.save(new JSONFragment().setID(key).setJSON(json));
            } catch (OwsExceptionReport ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class LoadJSONFragmentAction<T> implements
            ThrowingHibernateAction<T> {

        private final String key;
        private final Class<T> type;

        LoadJSONFragmentAction(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        @Override
        public T call(Session session)
                throws OwsExceptionReport {
            JSONFragment get = (JSONFragment) session
                    .get(JSONFragment.class, this.key);
            if (get == null) {
                return null;
            } else {
                Decoder<T, JsonNode> decoder
                        = CodingRepository.getInstance()
                        .getDecoder(new JsonDecoderKey(type));
                String json = get.getJSON();
                JsonNode node = JSONUtils.loadString(json);
                return decoder.decode(node);
            }

        }

    }

    private static class LoadReportingAuthorityAction extends LoadJSONFragmentAction<RelatedParty> {

        LoadReportingAuthorityAction() {
            super(REPORTING_AUTHORITY_KEY, RelatedParty.class);
        }

    }
}

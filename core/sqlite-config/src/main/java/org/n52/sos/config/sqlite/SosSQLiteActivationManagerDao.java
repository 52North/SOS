package org.n52.sos.config.sqlite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.iceland.config.SosActivationDao;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.encode.ProcedureDescriptionFormatKey;
import org.n52.iceland.encode.ResponseFormatKey;
import org.n52.iceland.ogc.swes.OfferingExtensionKey;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtension;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtensionKey;
import org.n52.sos.config.sqlite.entities.ObservationEncoding;
import org.n52.sos.config.sqlite.entities.ObservationEncodingKey;
import org.n52.sos.config.sqlite.entities.ProcedureEncoding;
import org.n52.sos.config.sqlite.entities.ProcedureEncodingKey;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SosSQLiteActivationManagerDao
        extends SQLiteActivationManagerDao
        implements SosActivationDao {

    // RESPONSE FORMAT
    @Override
    public void setResponseFormatStatus(ResponseFormatKey rfkt, boolean active)
            throws ConnectionProviderException {
        setActive(ObservationEncoding.class, new ObservationEncoding(rfkt), active);
    }

    @Override
    public boolean isResponseFormatActive(ResponseFormatKey rfkt)
            throws ConnectionProviderException {
        return isActive(ObservationEncoding.class, new ObservationEncodingKey(rfkt));
    }

    @Override
    public Set<ResponseFormatKey> getResponseFormatKeys()
            throws ConnectionProviderException {
        return asResponseFormatKeys(getKeys(ObservationEncoding.class));
    }

    private Set<ResponseFormatKey> asResponseFormatKeys(
            List<ObservationEncodingKey> hkeys) {
        Set<ResponseFormatKey> keys = new HashSet<>(hkeys.size());
        for (ObservationEncodingKey key : hkeys) {
            keys.add(new ResponseFormatKey(new ServiceOperatorKey(key
                    .getService(), key.getVersion()), key.getEncoding()));
        }
        return keys;
    }

    // PROCEDURE DESCRIPTION FORMAT
    @Override
    public void setProcedureDescriptionFormatStatus(
            ProcedureDescriptionFormatKey pdfkt,
            boolean active)
            throws ConnectionProviderException {
        setActive(ProcedureEncoding.class, new ProcedureEncoding(pdfkt), active);
    }

    @Override
    public boolean isProcedureDescriptionFormatActive(
            ProcedureDescriptionFormatKey pdfkt)
            throws ConnectionProviderException {
        return isActive(ProcedureEncoding.class, new ProcedureEncodingKey(pdfkt));
    }

    @Override
    public Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys()
            throws ConnectionProviderException {
        return asProcedureDescriptionFormatKeys(getKeys(ProcedureEncoding.class));
    }

    private Set<ProcedureDescriptionFormatKey> asProcedureDescriptionFormatKeys(
            List<ProcedureEncodingKey> hkeys) {
        Set<ProcedureDescriptionFormatKey> keys = new HashSet<>(hkeys.size());
        for (ProcedureEncodingKey key : hkeys) {
            keys
                    .add(new ProcedureDescriptionFormatKey(new ServiceOperatorKey(key
                                            .getService(), key.getVersion()), key
                                                           .getEncoding()));
        }
        return keys;
    }

    // OFFERING EXTENSION
    @Override
    public void setOfferingExtensionStatus(OfferingExtensionKey oek,
                                           boolean active)
            throws ConnectionProviderException {
        setActive(DynamicOfferingExtension.class, new DynamicOfferingExtension(oek), active);
    }

    @Override
    public boolean isOfferingExtensionActive(OfferingExtensionKey oek)
            throws ConnectionProviderException {
        return isActive(DynamicOfferingExtension.class, new DynamicOfferingExtensionKey(oek));
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeys()
            throws ConnectionProviderException {
        return asOfferingExtensionKeys(getKeys(DynamicOfferingExtension.class));
    }

    private Set<OfferingExtensionKey> asOfferingExtensionKeys(
            List<DynamicOfferingExtensionKey> hkeys) {
        Set<OfferingExtensionKey> keys = new HashSet<>(hkeys.size());
        for (DynamicOfferingExtensionKey key : hkeys) {
            keys.add(new OfferingExtensionKey(new ServiceOperatorKey(key
                    .getService(), key.getVersion()), key.getDomain()));
        }
        return keys;
    }

}

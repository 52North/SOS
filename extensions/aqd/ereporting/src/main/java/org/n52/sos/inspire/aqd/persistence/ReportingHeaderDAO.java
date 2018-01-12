package org.n52.sos.inspire.aqd.persistence;

import org.n52.shetland.aqd.ReportObligation;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.inspire.base2.RelatedParty;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public interface ReportingHeaderDAO {
    RelatedParty loadRelatedParty();

    ReportObligation loadReportObligation(ReportObligationType type);

    void save(RelatedParty relatedParty);

    void save(ReportObligationType type, ReportObligation reportObligation);

}

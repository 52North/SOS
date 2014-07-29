package org.n52.sos.ds.hibernate.dao.ereporting;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.aqd.AqdSamplingPoint;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EReportingSamplingPointDAO extends AbstractIdentifierNameDescriptionDAO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesDAO.class);
    
    /**
     * Get default Hibernate Criteria for querying series, deleted flag ==
     * <code>false</code>
     * 
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(EReportingSamplingPoint.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }
    
    public EReportingSamplingPoint getEReportingSamplingPoint(long samplingPointId, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.ID, samplingPointId));
        LOGGER.debug("QUERY getEReportingSamplingPoint(samplingPointId): {}",
                HibernateHelper.getSqlString(c));
        return (EReportingSamplingPoint) c.uniqueResult();
    }
    
    public EReportingSamplingPoint getEReportingSamplingPoint(String identifier, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getEReportingSamplingPoint(identifier): {}",
                HibernateHelper.getSqlString(c));
        return (EReportingSamplingPoint) c.uniqueResult();
    }
    
    public EReportingSamplingPoint getOrIntert(AqdSamplingPoint samplingPoint, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, samplingPoint.getIdentifier()));
        LOGGER.debug("QUERY getOrIntert(samplingPoint): {}",
                HibernateHelper.getSqlString(c));
        EReportingSamplingPoint eReportingSamplingPoint = (EReportingSamplingPoint) c.uniqueResult();
        if (eReportingSamplingPoint == null) {
            eReportingSamplingPoint = new EReportingSamplingPoint();
            addIdentifierNameDescription(samplingPoint, eReportingSamplingPoint, session);
            session.save(eReportingSamplingPoint);
            session.flush();
            session.refresh(eReportingSamplingPoint);
        }
        return eReportingSamplingPoint;
    }

}

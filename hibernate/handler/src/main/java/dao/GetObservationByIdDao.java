package dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.ogc.ows.ServiceMetadataRepository;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.request.GetObservationByIdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class GetObservationByIdDao implements org.n52.sos.ds.dao.GetObservationByIdDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationByIdDao.class);

    private HibernateSessionHolder sessionHolder;
    private ServiceMetadataRepository serviceMetadataRepository;

    @Inject
    public void setServiceMetadataRepository(ServiceMetadataRepository repo) {
        this.serviceMetadataRepository = repo;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    
    @Override
    public Collection<OmObservation> queryObservationsById(GetObservationByIdRequest request) {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            List<OmObservation> omObservations = Lists.newArrayList();
            if (DaoFactory.getInstance().isSeriesDAO()) {
                omObservations.addAll(querySeriesObservation(request, session));
            }
            List<Observation<?>> observations = Lists.newArrayList();
            observations.addAll(queryObservation(request, session));
            omObservations.addAll(HibernateObservationUtilities.createSosObservationsFromObservations(
                    checkObservations(observations, request), request, LocaleHelper.fromRequest(request), session));
            return omObservations;

        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }
    

    private List<OmObservation> querySeriesObservation(GetObservationByIdRequest request,
            Session session) throws OwsExceptionReport, ConverterException {
        List<OmObservation> observations = Lists.newArrayList();
        if (HibernateStreamingConfiguration.getInstance().isForceDatasourceStreaming()) {
            observations.addAll(querySeriesObservationForStreaming(request, session));
        } else {
            observations.addAll(querySeriesObservationForNonStreaming(request, session));
        }
        return observations;
    }

    private List<Observation<?>> checkObservations(List<Observation<?>> queryObservation, GetObservationByIdRequest request) {
        if (!request.isCheckForDuplicity()) {
            return queryObservation;
        }
        List<Observation<?>> checkedObservations = Lists.newArrayList();
        Set<String> identifiers = Sets.newHashSet();
        for (Observation<?> observation : queryObservation) {
            if (!identifiers.contains(observation.getIdentifier())) {
                identifiers.add(observation.getIdentifier());
                checkedObservations.add(observation);
            }
        }
        return checkedObservations;
    }

    /**
     * Query observations for observation identifiers
     *
     * @param request
     *            GetObservationById request
     * @param session
     *            Hibernate session
     * @return Resulting observations
     * @throws CodedException
     *             If an error occurs during querying the database
     */
    @SuppressWarnings("unchecked")
    private List<Observation<?>> queryObservation(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport {
        Criteria c =
                DaoFactory.getInstance().getObservationDAO()
                        .getObservationClassCriteriaForResultModel(request.getResultModel(), session);
        c.add(Restrictions.in(AbstractObservation.IDENTIFIER, request.getObservationIdentifier()));
        LOGGER.debug("QUERY queryObservation(request): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }
    
    /**
     * Query the series observations for streaming datasource
     *
     * @param request
     *            The GetObservation request
     * @param session
     *            Hibernate Session
     * @return List of internal observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> querySeriesObservationForStreaming(GetObservationByIdRequest request,
            final Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        for (Series series : serieses) {
            Collection<? extends OmObservation> createSosObservationFromSeries =
                    HibernateObservationUtilities
                            .createSosObservationFromSeries(series, request, session);
            OmObservation observationTemplate = createSosObservationFromSeries.iterator().next();
            HibernateSeriesStreamingValue streamingValue = getSeriesStreamingValue(request, series.getSeriesId());
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setObservationTemplate(observationTemplate);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    protected List<OmObservation> querySeriesObservationForNonStreaming(
            GetObservationByIdRequest request, Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        Collection<Observation<?>> seriesObservations = Lists.newArrayList();
        List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        SeriesObservationDAO observationDAO = (SeriesObservationDAO)DaoFactory.getInstance().getObservationDAO();
        for (Series series : serieses) {
           seriesObservations.addAll(observationDAO.getSeriesObservationFor(series, null, session));
        }
        final List<OmObservation> result = new LinkedList<OmObservation>();
        result.addAll(HibernateGetObservationHelper.toSosObservation(seriesObservations, request, session));
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Get the series streaming observation value for the observations
     *
     * @param request
     *            GetObservation request
     * @param seriesId
     *            Series id
     * @return Streaming observation value
     * @throws CodedException 
     */
    private HibernateSeriesStreamingValue getSeriesStreamingValue(GetObservationByIdRequest request, long seriesId) throws CodedException {
        if (HibernateStreamingConfiguration.getInstance().isChunkDatasourceStreaming()) {
            return new HibernateChunkSeriesStreamingValue(request, seriesId, request.isCheckForDuplicity());
        } else {
            return new HibernateScrollableSeriesStreamingValue(request, seriesId, request.isCheckForDuplicity());
        }
    }


}

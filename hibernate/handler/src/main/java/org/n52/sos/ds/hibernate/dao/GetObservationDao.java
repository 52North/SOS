package dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.ogc.ows.ServiceMetadataRepository;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.http.HTTPStatus;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.legacy.LegacyObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;
import org.n52.sos.ds.hibernate.values.HibernateStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractObservationResponse.GlobalGetObservationValues;
import org.n52.sos.response.GetObservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GetObservationDao implements org.n52.sos.ds.dao.GetObservationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationDao.class);

    private HibernateSessionHolder sessionHolder;
    private FeatureQueryHandler featureQueryHandler;
    private ServiceMetadataRepository serviceMetadataRepository;

    @Inject
    public void setServiceMetadataRepository(ServiceMetadataRepository repo) {
        this.serviceMetadataRepository = repo;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }
    
    @Override
    public List<OmObservation> getObservation(GetObservationRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            List<OmObservation> observations = new ArrayList<>();
            if (CollectionHelper.isEmpty(request.getFirstLatestTemporalFilter())) {
                observations.addAll(querySeriesObservationForStreaming(request, session));
            } else {
                AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
                observations.addAll(querySeriesObservation(request, (AbstractSeriesObservationDAO)observationDAO, session));
            }
            return observations;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
       
    }
    
    @Override
    public GlobalGetObservationValues getGlobalValues(GetObservationRequest request) {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            GlobalGetObservationValues globalValues = new GlobalGetObservationValues();
            final Set<String> features = QueryHelper.getFeatures(request, session);
            if (features != null && features.isEmpty()) {
                return globalValues;
            }
            Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
            List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, features, session);
            ObservationTimeExtrema timeExtrema = DaoFactory.getInstance().getValueTimeDAO().getTimeExtremaForSeries(serieses, temporalFilterCriterion, session);
            if (timeExtrema.isSetPhenomenonTimes()) {
               globalValues.setPhenomenonTime(timeExtrema.getPhenomenonTime());
            }
            return globalValues;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }
    
    /**
     * Query observations from database depending on requested filters
     *
     * @param request
     *            GetObservation request
     * @param observationDAO
     * @param session
     *            Hibernate session
     * @return List of internal Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs during requesting
     * @throws ConverterException
     *             If an error occurs during converting
     */
    // TODO move this and associated methods to ObservationDAO
    protected List<OmObservation> queryObservation(final GetObservationRequest request, LegacyObservationDAO observationDAO, final Session session)
            throws OwsExceptionReport, ConverterException {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }

        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return new ArrayList<OmObservation>();
        }
        // temporal filters
        final List<SosIndeterminateTime> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        // final List<OmObservation> result = new LinkedList<OmObservation>();
        Collection<Observation<?>> observations = Lists.newArrayList();
        // query with temporal filter
        if (filterCriterion != null) {
            observations = observationDAO.getObservationsFor(request, features, filterCriterion, session);
        }
        // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (SosIndeterminateTime sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (ServiceConfiguration.getInstance().isOverallExtrema()) {
                    observations =
                            observationDAO.getObservationsFor(request, features, sosIndeterminateTime, session);
                } else {
                    for (ObservationConstellation oc : HibernateGetObservationHelper
                            .getAndCheckObservationConstellationSize(request, session)) {
                        for (String feature : HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features,
                                session)) {
                            observations.addAll(observationDAO.getObservationsFor(oc, Sets.newHashSet(feature),
                                    request, sosIndeterminateTime, session));
                        }
                    }
                }
            }
        }
        // query without temporal or indeterminate filters
        else {
            observations = observationDAO.getObservationsFor(request, features, session);
        }

        int metadataObservationsCount = 0;

        List<OmObservation> result = HibernateGetObservationHelper.toSosObservation(observations, request, request.getRequestedLocale(), session);
        Set<OmObservationConstellation> timeSeries = Sets.newHashSet();
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()
                || ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries() > 0) {
            for (OmObservation omObservation : result) {
                timeSeries.add(omObservation.getObservationConstellation());
            }
        }
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            for (ObservationConstellation oc : HibernateGetObservationHelper.getAndCheckObservationConstellationSize(
                    request, session)) {
                final List<String> featureIds =
                        HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, session);
                for (OmObservation omObservation : HibernateObservationUtilities.createSosObservationFromObservationConstellation(oc,
                        featureIds, request, request.getRequestedLocale(), session)) {
                    if (!timeSeries.contains(omObservation.getObservationConstellation())) {
                        result.add(omObservation);
                        timeSeries.add(omObservation.getObservationConstellation());
                    }
                }
            }
        }

        HibernateGetObservationHelper
                .checkMaxNumberOfReturnedSeriesSize(timeSeries.size() + metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(observations.size());
        LOGGER.debug("Time to query observations needed {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Query observation if the series mapping is supported.
     *
     * @param request
     *            GetObservation request
     * @param observationDAO
     * @param session
     *            Hibernate session
     * @return List of internal Observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> querySeriesObservation(GetObservationRequest request, AbstractSeriesObservationDAO observationDAO, Session session)
            throws OwsExceptionReport, ConverterException {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }

        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return new LinkedList<>();
        }
        // temporal filters
        final List<SosIndeterminateTime> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        final List<OmObservation> result = new LinkedList<>();
        Collection<SeriesObservation<?>> seriesObservations = Lists.newArrayList();

        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();

        // query with temporal filter
        if (filterCriterion != null) {
            seriesObservations =
                    checkObservationsForDuplicity(observationDAO.getSeriesObservationsFor(request, features, filterCriterion, session), request);
        }
        // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (SosIndeterminateTime sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (ServiceConfiguration.getInstance().isOverallExtrema()) {
                    seriesObservations =
                            observationDAO.getSeriesObservationsFor(request, features,
                                    sosIndeterminateTime, session);
                } else {
                    for (Series series : seriesDAO.getSeries(request, features, session)) {
                        seriesObservations.addAll(observationDAO.getSeriesObservationsFor(series, request,
                                sosIndeterminateTime, session));
                        
                    }
                    seriesObservations = checkObservationsForDuplicity(observationDAO.getSeriesObservationsFor(request, features, session), request);
                }
            }
        }
        // query without temporal or indeterminate filters
        else {
            seriesObservations = checkObservationsForDuplicity(observationDAO.getSeriesObservationsFor(request, features, session), request);
        }
        

        // if active profile demands observation metadata for series without
        // matching observations,
        // a "result" observation without values is created.
        // TODO does this apply for indeterminate time first/latest filters?
        // Yes.
        int metadataObservationsCount = 0;
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            Map<Long, Series> seriesToCheckMap = Maps.newHashMap();
            for (Series series : seriesDAO.getSeries(request, features, session)) {
                seriesToCheckMap.put(series.getSeriesId(), series);
            }

            // check observations and remove any series found from the map
            for (SeriesObservation<?> seriesObs : seriesObservations) {
                long seriesId = seriesObs.getSeries().getSeriesId();
                if (seriesToCheckMap.containsKey(seriesId)) {
                    seriesToCheckMap.remove(seriesId);
                }
            }
            // now we're left with the series without matching observations in
            // the check map,
            // add "result" observations for them
            metadataObservationsCount = seriesToCheckMap.size();
            for (Series series : seriesToCheckMap.values()) {
                result.addAll(HibernateObservationUtilities.createSosObservationFromSeries(series,
                        request, request.getRequestedLocale(), session));
            }
        }
        HibernateGetObservationHelper
                .checkMaxNumberOfReturnedTimeSeries(seriesObservations, metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(seriesObservations.size());

        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        Collection<Observation<?>> abstractObservations = Lists.newArrayList();
        abstractObservations.addAll(seriesObservations);
        result.addAll(HibernateGetObservationHelper.toSosObservation(abstractObservations, request, request.getRequestedLocale(), session));
        return result;
    }

    /**
     * Query the observations for streaming datasource
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
    protected List<OmObservation> queryObservationForStreaming(GetObservationRequest request, final Session session)
            throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return result;
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        List<ObservationConstellation> observations = HibernateGetObservationHelper.getAndCheckObservationConstellationSize(
                request, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(observations.size());
        int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(observations.size());
        for (ObservationConstellation oc : observations) {
            final List<String> featureIds =
                    HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, session);
            for (OmObservation observationTemplate : HibernateObservationUtilities
                    .createSosObservationFromObservationConstellation(oc, featureIds, request, session)) {
                AbstractFeatureOfInterest featureOfInterest =
                        new FeatureOfInterestDAO().getFeatureOfInterest(observationTemplate
                                .getObservationConstellation().getFeatureOfInterest().getIdentifier(),
                                session);
                HibernateStreamingValue streamingValue =
                        getStreamingValue(request, oc.getProcedure().getProcedureId(), oc.getObservableProperty()
                                .getObservablePropertyId(), featureOfInterest.getFeatureOfInterestId());
                streamingValue.setResponseFormat(request.getResponseFormat());
                streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
                streamingValue.setObservationTemplate(observationTemplate);
                streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
                observationTemplate.setValue(streamingValue);
                result.add(observationTemplate);
            }
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
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
    protected List<OmObservation> querySeriesObservationForStreaming(GetObservationRequest request, Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return result;
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, features, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(serieses.size());
        checkSeriesOfferings(serieses, request);
        Collection<Series> duplicated = checkAndGetDuplicatedtSeries(serieses, request);
        for (Series series : serieses) {
            Collection<? extends OmObservation> createSosObservationFromSeries =
                    HibernateObservationUtilities
                            .createSosObservationFromSeries(series, request, session);
            OmObservation observationTemplate = createSosObservationFromSeries.iterator().next();
            HibernateSeriesStreamingValue streamingValue = new HibernateChunkSeriesStreamingValue(request, series.getSeriesId(), duplicated.contains(series));
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
            streamingValue.setObservationTemplate(observationTemplate);
            streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    private void checkSeriesOfferings(List<Series> serieses, GetObservationRequest request) {
        boolean allSeriesWithOfferings = true;
        for (Series series : serieses) {
            allSeriesWithOfferings = !series.isSetOffering() ?  false : allSeriesWithOfferings;
        }
        if (allSeriesWithOfferings) {
            request.setOfferings(Lists.<String>newArrayList());
        }
    }

    private Collection<Series> checkAndGetDuplicatedtSeries(List<Series> serieses, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return Sets.newHashSet();
        }
        Set<Series> single = Sets.newHashSet();
        Set<Series> duplicated = Sets.newHashSet();
        for (Series series : serieses) {
            if (!single.isEmpty()) {
                if (isDuplicatedSeries(series, single)) {
                    duplicated.add(series);
                }
            } else {
                single.add(series);
            }
        }
        return duplicated;
    }
    
    private boolean isDuplicatedSeries(Series series, Set<Series> serieses) {
        for (Series s : serieses) {
            if (series.hasSameObservationIdentifier(s)) {
                return true;
            }
        }
        return false;
    }
    
    private Collection<SeriesObservation<?>> checkObservationsForDuplicity(Collection<SeriesObservation<?>> seriesObservations, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return seriesObservations;
        }
        Collection<SeriesObservation<?>> checked = Lists.newArrayList();
        Set<Series> serieses = Sets.newHashSet();
        Set<Series> duplicated = Sets.newHashSet();
        for (SeriesObservation<?> seriesObservation : seriesObservations) {
            if (serieses.isEmpty()) {
                serieses.add(seriesObservation.getSeries());
            } else {
                if (!serieses.contains(seriesObservation.getSeries()) && !duplicated.contains(seriesObservation)
                        && isDuplicatedSeries(seriesObservation.getSeries(), serieses)) {
                    duplicated.add(seriesObservation.getSeries());
                }
            }

            if (serieses.contains(seriesObservation.getSeries()) || (duplicated.contains(seriesObservation.getSeries())
                    && seriesObservation.getOfferings().size() == 1)) {
                checked.add(seriesObservation);
            }
        }
        return checked;
    }
}

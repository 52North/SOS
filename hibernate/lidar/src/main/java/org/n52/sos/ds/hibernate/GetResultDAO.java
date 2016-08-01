/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import static org.n52.sos.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;
import org.n52.sos.ds.AbstractGetResultDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.n52.lidar.importer.core.DockerAppContext;
import org.n52.lidar.importer.core.SosLidarImportSettings;
import org.n52.lidar.importer.core.db.PostgresSettings;
import org.n52.lidar.importer.pdal.PdalContainer;
import org.n52.sos.exception.ows.concrete.MissingFeatureOfInterestTypeException;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Implementation of the abstract class AbstractGetResultDAO
 *
 * @since 4.0.0
 *
 */
public class GetResultDAO extends AbstractGetResultDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultDAO.class);
    private static final String BASE_OUTPUT_DIR = "C:\\Users\\adewa\\data\\";

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    /**
     * constructor
     */
    public GetResultDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public GetResultResponse getResult(final GetResultRequest request) throws OwsExceptionReport {
        Session session = null;
        try {

            final GetResultResponse response = new GetResultResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());

            if (!request.isSetFeatureOfInterest()) {
                throw new MissingFeatureOfInterestTypeException()
                        .setStatus(HTTPStatus.NOT_FOUND);
            } else if (request.getFeatureIdentifiers().size() != 1) {
                // TODO exception too many feature
            }

            File exportFeature = exportFeature(request);
            byte[] bytes = loadFile(exportFeature);
            String content = Base64.encodeBase64String(bytes);
//            String content = IOUtils.toString(new FileInputStream(exportFeature));

            response.setResultValues(content);
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException()
                    .causedBy(he)
                    .withMessage("Error while querying result data!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } catch (FileNotFoundException ex) {
            throw new NoApplicableCodeException()
                    .causedBy(ex)
                    .withMessage("Error while reading result file!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } catch (IOException ex) {
            throw new NoApplicableCodeException()
                    .causedBy(ex)
                    .withMessage("Error while reading result file!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private byte[] loadFile(File file) throws IOException {
        byte[] bytes;
        try (InputStream is = new FileInputStream(file)) {
            long length = file.length();
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        }
        return bytes;
    }

    private File exportFeature(GetResultRequest request) throws OwsExceptionReport {
        String feature = request.getFeatureIdentifiers().iterator().next();
        String offering = request.getOffering();
        // TODO get filename for feature and offering

        // Get the database properties
        Session session = sessionHolder.getSession();
        Properties properties = ((SessionFactoryImpl) session.getSessionFactory()).getProperties();
        String password = properties.getProperty("hibernate.connection.password");
        String username = properties.getProperty("hibernate.connection.username");
        String jdbcUrl = properties.getProperty("hibernate.connection.url");

        // Lidar import settings with focus on SOS attributes
        String observedProperty = "http://www.52north.org/test/observableProperty/9_3";
        String procedure = "http://www.52north.org/test/procedure/9";
        String sosUrl = "http://localhost:8080//webapp/service";
        String pcPatchesTable = "patches_new";
        String cloudjsTable = "patches_cloudjs";
        SosLidarImportSettings importSettings = new SosLidarImportSettings(
                offering, procedure, observedProperty, sosUrl, pcPatchesTable, cloudjsTable);

        // Instantiate a new PDAL Container handler
        PdalContainer pdal = new PdalContainer(
                DockerAppContext.createDefaultAppConfigBuilder().build(),
                importSettings,
                new PostgresSettings(jdbcUrl, "192.168.99.1", username, password)
        );

        // Export the feature to the file;
        pdal.exportDBEntryToLas(feature);

        // return the file of the exported feature
        return getExportedFile(feature);
    }

    private File getExportedFile(String feature) {
        String folder = feature.substring(0, feature.lastIndexOf("-"));
        return new File(BASE_OUTPUT_DIR + folder + "/" + feature + ".laz");
    }

    @Override
    public Set<String> getConformanceClasses() {
        try {
            Session session = sessionHolder.getSession();
            if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()) {
                return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
            }
            sessionHolder.returnSession(session);
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while getting Spatial Filtering Profile conformance class!", owse);
        }
        return super.getConformanceClasses();
    }

//    /**
//     * Query observations from database depending on requested filters
//     *
//     * @param request
//     *            GetObservation request
//     * @param featureIdentifiers
//     *            Set of feature identifiers. If <tt>null</tt>, query filter
//     *            will not be added. If <tt>empty</tt>, <tt>null</tt> will be
//     *            returned.
//     * @param session
//     *            Hibernate session
//     * @return List of Observation objects
//     *
//     *
//     * @throws OwsExceptionReport
//     *             If an error occurs.
//     */
//    @SuppressWarnings("unchecked")
//    protected List<Observation<?>> queryObservation(final GetResultRequest request,
//            final Set<String> featureIdentifiers, final Session session) throws OwsExceptionReport {
//        final Criteria c = createCriteriaFor(AbstractLegacyObservation.class, session);
//        addSpatialFilteringProfileRestrictions(c, request, session);
//
//        if (isEmpty(featureIdentifiers)) {
//            return null; // because no features where found regarding the
//                         // filters
//        } else if (isNotEmpty(featureIdentifiers)) {
//            c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST).add(
//                    Restrictions.in(FeatureOfInterest.IDENTIFIER, featureIdentifiers));
//        }
//        if (request.isSetObservedProperty()) {
//            c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY).add(
//                    Restrictions.eq(ObservableProperty.IDENTIFIER, request.getObservedProperty()));
//        }
//        if (request.isSetOffering()) {
//            addOfferingRestriction(c, request.getOffering());
//        }
//        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
//            addTemporalFilter(c, request.getTemporalFilter());
//        }
//        c.addOrder(Order.asc(AbstractLegacyObservation.PHENOMENON_TIME_START));
//
//        LOGGER.debug("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
//        return c.list();
//
//    }
//
//    /**
//     * Query series observations from database depending on requested filters
//     *
//     * @param request
//     *            GetObservation request
//     * @param featureIdentifiers
//     *            Set of feature identifiers. If <tt>null</tt>, query filter
//     *            will not be added. If <tt>empty</tt>, <tt>null</tt> will be
//     *            returned.
//     * @param session
//     *            Hibernate session
//     * @return List of Observation objects
//     * @throws OwsExceptionReport
//     *             If an error occurs.
//     */
//    @SuppressWarnings("unchecked")
//    protected List<Observation<?>> querySeriesObservation(GetResultRequest request,
//            Collection<String> featureIdentifiers, Session session) throws OwsExceptionReport {
//        final Criteria c = createCriteriaFor(AbstractSeriesObservation.class, session);
//        addSpatialFilteringProfileRestrictions(c, request, session);
//
//        List<Series> series = DaoFactory.getInstance().getSeriesDAO().getSeries(request.getObservedProperty(), featureIdentifiers, session);
//        if (CollectionHelper.isEmpty(series)) {
//            return null;
//        } else {
//            c.add(Restrictions.in(AbstractSeriesObservation.SERIES, series));
//        }
//
//        if (request.isSetOffering()) {
//            addOfferingRestriction(c, request.getOffering());
//        }
//        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
//            addTemporalFilter(c, request.getTemporalFilter());
//        }
//
//        LOGGER.debug("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
//        return c.list();
//
//    }
//
//    /**
//     * Query corresponding ResultTemplate
//     *
//     * @param request
//     *            GetResult request
//     * @param featureIdentifier
//     *            Associated featureOfInterest identifier
//     * @param session
//     *            Hibernate session
//     * @return Resulting ResultTemplates as list
//     */
//    private List<ResultTemplate> queryResultTemplate(final GetResultRequest request,
//            final Set<String> featureIdentifier, final Session session) {
//        final List<ResultTemplate> resultTemplates =
//                new ResultTemplateDAO().getResultTemplateObject(request.getOffering(), request.getObservedProperty(),
//                        featureIdentifier, session);
//        return resultTemplates;
//    }
//
//    /**
//     * Add offering identifier restriction to Hibernate Criteria
//     *
//     * @param c
//     *            Hibernate Criteria to add restriction
//     * @param offering
//     *            Offering identifier ot add
//     */
//    private void addOfferingRestriction(Criteria c, String offering) {
//        c.createCriteria(AbstractObservation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
//    }
//
//    /**
//     * Add offering identifier restriction to Hibernate Criteria
//     *
//     * @param c
//     *            Hibernate Criteria to add restriction
//     * @param temporalFilter
//     *            Temporal filters to add
//     * @throws UnsupportedTimeException
//     *             If the time is not supported
//     * @throws UnsupportedValueReferenceException
//     *             If the valueReference is not supported
//     * @throws UnsupportedOperatorException
//     *             If the temporal operator is not supported
//     */
//    private void addTemporalFilter(Criteria c, List<TemporalFilter> temporalFilter) throws UnsupportedTimeException,
//            UnsupportedValueReferenceException, UnsupportedOperatorException {
//        c.add(TemporalRestrictions.filter(temporalFilter));
//    }
//
//    /**
//     * Create Hibernate Criteria for the class and add ascending of phenomenon
//     * start time
//     *
//     * @param clazz
//     *            The class for the Criteria
//     * @param session
//     *            Hibernate session
//     * @return Hibernate Criteria for the class and add ascending of phenomenon
//     *         start time
//     */
//    @SuppressWarnings("rawtypes")
//    private Criteria createCriteriaFor(Class clazz, Session session) {
//        return session.createCriteria(clazz).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
//                .add(Restrictions.eq(AbstractLegacyObservation.DELETED, false))
//                .addOrder(Order.asc(AbstractLegacyObservation.PHENOMENON_TIME_START));
//    }
//
//    /**
//     * @param criteria
//     *            Hibernate Criteria to add restriction
//     * @param request
//     *            GetResult request
//     * @param session
//     *            Hibernate session
//     * @throws OwsExceptionReport
//     *             If Spatial Filtering Profile is not supported or an error
//     *             occurs
//     */
//    private void addSpatialFilteringProfileRestrictions(Criteria criteria, GetResultRequest request, Session session)
//            throws OwsExceptionReport {
//        if (request.hasSpatialFilteringProfileSpatialFilter()) {
//            if (GeometryHandler.getInstance().isSpatialDatasource()) {
//                criteria.add(SpatialRestrictions.filter(
//                        AbstractObservation.SAMPLING_GEOMETRY,
//                        request.getSpatialFilter().getOperator(),
//                        GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(
//                                request.getSpatialFilter().getGeometry())));
//            } else {
//                // TODO add filter with lat/lon
//                LOGGER.warn("Spatial filtering for lat/lon is not yet implemented!");
//            }
//            
//        }
//    }
}

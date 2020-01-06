/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mockito;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.cache.ContentCachePersistenceStrategy;
import org.n52.iceland.cache.WritableContentCache;
import org.n52.iceland.cache.ctrl.CompleteCacheUpdateFactory;
import org.n52.iceland.cache.ctrl.ContentCacheFactory;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JTSHelperForTesting;
import org.n52.shetland.util.ReverseOf;
import org.n52.sos.cache.InMemoryCacheImpl;
import org.n52.sos.cache.ctrl.SosContentCacheControllerImpl;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.builder.SamplingFeatureBuilder;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class HibernateFeatureQueryHandlerTest extends HibernateTestCase {

    private GeometryHandler geometryHandler;

    private HibernateFeatureQueryHandler featureQueryHandler;

    private final InMemoryCacheImpl cache = new InMemoryCacheImpl();

    private final TestingSosContentCacheControllerImpl contentCacheController =
            new TestingSosContentCacheControllerImpl();

    private final I18NDAORepository i18NDAORepository = new I18NDAORepository();

    @Before
    public void setUp() throws ConfigurationError {
        geometryHandler = new GeometryHandler();
        geometryHandler.setStorageEpsg(4326);
        geometryHandler.setStorage3DEpsg(4979);
        geometryHandler.setEpsgCodesWithNorthingFirstAxisOrder(
                "2044-2045;2081-2083;2085-2086;2093;2096-2098;2105-2132;2169-2170;2176-2180;2193;2200;2206-2212;2319;"
                        + "2320-2462;2523-2549;2551-2735;2738-2758;2935-2941;2953;3006-3030;3034-3035;3058-3059;3068;"
                        + "3114-3118;3126-3138;3300-3301;3328-3335;3346;3350-3352;3366;3416;4001-4999;20004-20032;"
                        + "20064-20092;21413-21423;21473-21483;21896-21899;22171;22181-22187;22191-22197;25884;27205-27232;"
                        + "27391-27398;27492;28402-28432;28462-28492;30161-30179;30800;31251-31259;31275-31279;31281-31290;"
                        + "31466-31700");
        geometryHandler.setDatasourceNorthingFirst(false);

        contentCacheController.setPersistenceStrategy(Mockito.mock(ContentCachePersistenceStrategy.class));
        contentCacheController.setCacheFactory(Mockito.mock(ContentCacheFactory.class));
        contentCacheController.setCompleteCacheUpdateFactory(Mockito.mock(CompleteCacheUpdateFactory.class));
        contentCacheController.setCache(cache);

        featureQueryHandler = new HibernateFeatureQueryHandler();
        featureQueryHandler.setGeometryHandler(geometryHandler);
        featureQueryHandler.setContentCacheController(contentCacheController);
        featureQueryHandler.setI18NDAORepository(i18NDAORepository);
    }

    @Test
    public void shouldCreateValidModelDomainFeature() throws OwsExceptionReport {
        Session session = getSession();
        try {
            final String id = "id";
            final String type = SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT;
            FeatureEntity feature = create(1, id, null, "name", "url", createFeatureOfInterestType(1L, type));
            String version = Sos2Constants.SERVICEVERSION;
            AbstractFeature result = featureQueryHandler.createSosAbstractFeature(feature,
                    new FeatureQueryHandlerQueryObject(session).setVersion(version));
            final AbstractFeature expectedResult =
                    SamplingFeatureBuilder.aSamplingFeature().setFeatureType(type).setIdentifier(id).build();
            assertThat(expectedResult, is(result));
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he);
        } finally {
            returnSession(session);
        }
    }

    public FeatureEntity create(long id, String identifier, Geometry geom, String name, String url, FormatEntity type)
            throws CodedException {
        FeatureEntity featureOfInterest = new FeatureEntity();
        featureOfInterest.setIdentifier(identifier);
        featureOfInterest.setId(id);
        // featureOfInterest.setNames(name);
        featureOfInterest.setGeometry(geom);
        featureOfInterest.setUrl(url);
        featureOfInterest.setFeatureType(type);
        return featureOfInterest;
    }

    private FormatEntity createFeatureOfInterestType(Long id, String type) {
        FormatEntity featureOfInterestType = new FormatEntity();
        featureOfInterestType.setId(id);
        featureOfInterestType.setFormat(type);
        return featureOfInterestType;
    }

    @Test
    public void shouldSwitchCoordinatesForEpsg4326() throws OwsExceptionReport {
        GeometryFactory factory = JTSHelper.getGeometryFactoryForSRID(4326);
        Geometry geometry = factory.createPoint(JTSHelperForTesting.randomCoordinate());
        Geometry switched = geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(geometry);

        assertThat(geometryHandler.isNorthingFirstEpsgCode(4326), is(true));
        assertThat(switched, is(notNullValue()));
        assertThat(switched, is(instanceOf(geometry.getClass())));
        assertThat(switched, is(not(sameInstance(geometry))));
        assertThat(switched, is(ReverseOf.reverseOf(geometry)));
    }

    @Test
    public void shouldSwitchCoordinatesForSosAbstractFeature() throws OwsExceptionReport {
        Session session = getSession();
        try {
            GeometryFactory factory = JTSHelper.getGeometryFactoryForSRID(4326);
            Geometry geometry = factory.createPoint(JTSHelperForTesting.randomCoordinate());
            FeatureEntity feature = create(1, "id", geometry, "name", "url", createFeatureOfInterestType(1L, "type"));
            AbstractFeature sosFeature = featureQueryHandler.createSosAbstractFeature(feature,
                    new FeatureQueryHandlerQueryObject(session).setVersion(Sos2Constants.SERVICEVERSION));

            assertThat(geometryHandler.isNorthingFirstEpsgCode(4326), is(true));
            assertThat(sosFeature, is(notNullValue()));
            assertThat(sosFeature, is(instanceOf(SamplingFeature.class)));

            SamplingFeature ssf = (SamplingFeature) sosFeature;

            assertThat(ssf.getGeometry(), is(notNullValue()));
            assertThat(ssf.getGeometry(), is(instanceOf(geometry.getClass())));
            assertThat(ssf.getGeometry(), is(not(sameInstance(geometry))));
            assertThat(ssf.getGeometry(), is(ReverseOf.reverseOf(geometry)));
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void shouldNotSwitchCoordinatesForEpsg2094() throws OwsExceptionReport {
        GeometryFactory factory = JTSHelper.getGeometryFactoryForSRID(2094);
        Geometry geometry = factory.createPoint(JTSHelperForTesting.randomCoordinate());
        Geometry switched = geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(geometry);

        assertThat(geometryHandler.isNorthingFirstEpsgCode(2094), is(false));
        assertThat(switched, is(notNullValue()));
        assertThat(switched, is(instanceOf(geometry.getClass())));
        // assertThat(switched, is(sameInstance(geometry)));
        // assertThat(switched, is(not(reverseOf(geometry))));
    }

    @Test
    public void shouldNotSwitchCoordinatesForSosAbstractFeature() throws OwsExceptionReport {
        Session session = getSession();
        try {
            GeometryFactory factory = JTSHelper.getGeometryFactoryForSRID(2007);
            Geometry geometry = factory.createPoint(JTSHelperForTesting.randomCoordinate());

            assertThat(geometryHandler.isNorthingFirstEpsgCode(2007), is(false));

            FeatureEntity feature = create(1, "id", geometry, "name", "url", createFeatureOfInterestType(1L, "type"));
            AbstractFeature sosFeature = featureQueryHandler.createSosAbstractFeature(feature,
                    new FeatureQueryHandlerQueryObject(session).setVersion(Sos2Constants.SERVICEVERSION));

            assertThat(geometryHandler.isNorthingFirstEpsgCode(4326), is(true));
            assertThat(sosFeature, is(notNullValue()));
            assertThat(sosFeature, is(instanceOf(SamplingFeature.class)));

            SamplingFeature ssf = (SamplingFeature) sosFeature;

            assertThat(ssf.getGeometry(), is(notNullValue()));
            assertThat(ssf.getGeometry(), is(instanceOf(geometry.getClass())));
            assertThat(ssf.getGeometry(), is(sameInstance(geometry)));
            assertThat(ssf.getGeometry(), is(not(ReverseOf.reverseOf(geometry))));
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void shouldNotFailOnReferencedFeature() throws OwsExceptionReport {
        String url = "http://example.com/wfs?service=WFS&version=1.1.0&"
                + "request=GetFeature&typeName=waterdata:sampling&featureid=foi1";
        SamplingFeature ssf = new SamplingFeature(null);
        ssf.setUrl(url);
        // TODO check
        // assertThat(insertFeature(ssf, null), is(url));
    }

    private class TestingSosContentCacheControllerImpl extends SosContentCacheControllerImpl {

        @Override
        protected void setCache(WritableContentCache wcc) {
            super.setCache(wcc);
        }
    }
}

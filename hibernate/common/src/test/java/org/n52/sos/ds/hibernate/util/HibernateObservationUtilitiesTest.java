/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.observation.AdditionalObservationCreatorRepository;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.sos.util.SosHelper;

/**
 * The class <code>HibernateObservationUtilitiesTest</code> contains tests for
 * the class {@link HibernateObservationUtilities}
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike
 *         HinderkJ&uuml;rrens</a>
 *
 * @since 4.0.0
 *
 */
public class HibernateObservationUtilitiesTest extends HibernateTestCase {
    public static final String FEATURE_OF_INTEREST_TYPE = "junit_feature_of_interest_type";

    public static final String OFFERING = "junit_offering";

    public static final String CODESPACE = "junit_codespace";

    private static final String PROCEDURE = "junit_test_procedure_id";

    /*
     * Must be a valid feature identifier in the test data base
     */
    private static final String FEATURE = "1000";

    private static final String OBSERVABLE_PROPERTY = "http://sweet.jpl.nasa.gov/2.0/hydroSurface.owl#Discharge";

    private static final String PROCEDURE_DESCRIPTION_FORMAT = "junit_procedure_description_format";

    @AfterClass
    public static void cleanUp() {
        H2Configuration.recreate();
    }

    @Test
    public void returnEmptyCollectionIfCalledWithoutAnyParameters() throws OwsExceptionReport, ConverterException {
        ObservationStream resultList = HibernateObservationUtilities.createSosObservationFromObservationConstellation(
                null, null, null, null, null, new OmObservationCreatorContext(null, null, null, null, null, null, null,
                        null, null, null, null, null, null),
                null);
        assertThat("result is null", resultList, is(not(nullValue())));
        assertThat("elements in list", resultList.hasNext(), is(false));
    }

    @Test
    // FIXME this one fails: SWE Array is only returned if a result template is
    // present
    public void createSubObservationOfSweArrayObservationViaGetObservationById()
            throws OwsExceptionReport, ConnectionProviderException, ConverterException {
        // PREPARE
        Session session = getSession();

        try {
            Transaction transaction = session.beginTransaction();
            GetObservationByIdRequest request = new GetObservationByIdRequest();
            request.setVersion(Sos2Constants.SERVICEVERSION);

            FormatEntity hProcedureDescriptionFormat = new FormatEntity();
            FormatEntity hFeatureOfInterestType = new FormatEntity();
            FeatureEntity hFeatureOfInterest = new FeatureEntity();
            PhenomenonEntity hObservableProperty = new PhenomenonEntity();
            CategoryEntity hCategory = new CategoryEntity();
            PlatformEntity hPlatform = new PlatformEntity();
            FormatEntity hObservationType = new FormatEntity();
            OfferingEntity hOffering = new OfferingEntity();
            DatasetEntity hObservationConstellation = new DatasetEntity();
            CodespaceEntity hCodespace = new CodespaceEntity();
            ProcedureEntity hProcedure = new ProcedureEntity();
            QuantityDataEntity hObservation = new QuantityDataEntity();
            ResultTemplateEntity hResultTemplateEntity = new ResultTemplateEntity();

            hProcedureDescriptionFormat.setFormat(PROCEDURE_DESCRIPTION_FORMAT);
            hCodespace.setName(CODESPACE);
            hProcedure.setIdentifier(PROCEDURE);
            hProcedure.setFormat(hProcedureDescriptionFormat);
            hFeatureOfInterestType.setFormat(FEATURE_OF_INTEREST_TYPE);
            hFeatureOfInterest.setIdentifier(FEATURE);
            hFeatureOfInterest.setFeatureType(hFeatureOfInterestType);
            hFeatureOfInterest.setIdentifierCodespace(hCodespace);
            hPlatform.setIdentifier(FEATURE);
            hObservableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            hCategory.setIdentifier(OBSERVABLE_PROPERTY);
            hObservationType.setFormat(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            hOffering.setIdentifier(OFFERING);
            hObservationConstellation.setProcedure(hProcedure);
            hObservationConstellation.setOffering(hOffering);
            hObservationConstellation.setObservableProperty(hObservableProperty);
            hObservationConstellation.setOmObservationType(hObservationType);
            hObservationConstellation.setFeature(hFeatureOfInterest);
            hObservationConstellation.setDeleted(false);
            hObservationConstellation.setHidden(false);
            hObservationConstellation.setCategory(hCategory);
            hObservationConstellation.setPlatform(hPlatform);

            hResultTemplateEntity.setFeature(hFeatureOfInterest);
            hResultTemplateEntity.setOffering(hOffering);
            hResultTemplateEntity.setPhenomenon(hObservableProperty);
            hResultTemplateEntity.setProcedure(hProcedure);
            hResultTemplateEntity.setIdentifier("test-rt-1");
            hResultTemplateEntity.setEncoding(
                    "<swe:TextEncoding xmlns:swe=\"http://www.opengis.net/swe/2.0\" "
                    + "xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\" "
                    + "xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" "
                    + "xmlns:sf=\"http://www.opengis.net/sampling/2.0\" "
                    + "xmlns:swes=\"http://www.opengis.net/swes/2.0\" "
                    + "xmlns:sos=\"http://www.opengis.net/sos/2.0\" "
                    + "xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
                    + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                    + "xmlns:om=\"http://www.opengis.net/om/2.0\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                    + "tokenSeparator=\"#\" blockSeparator=\"@\"/>");
            hResultTemplateEntity.setStructure(
                    "\"<swe:DataRecord xmlns:swe=\"http://www.opengis.net/swe/2.0\" "
                    + "xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\" "
                    + "xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" "
                    + "xmlns:sf=\"http://www.opengis.net/sampling/2.0\" "
                    + "xmlns:swes=\"http://www.opengis.net/swes/2.0\" "
                    + "xmlns:sos=\"http://www.opengis.net/sos/2.0\" "
                    + "xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
                    + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                    + "xmlns:om=\"http://www.opengis.net/om/2.0\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n"
                    + "  <swe:field name=\"phenomenonTime\">\r\n"
                    + "    <swe:Time definition=\"http://www.opengis.net/def/property/OGC/0/PhenomenonTime\">\r\n"
                    + "      <swe:uom xlink:href=\"http://www.opengis.net/def/uom/ISO-8601/0/Gregorian\"/>\r\n"
                    + "    </swe:Time>\r\n" + "  </swe:field>\r\n" + "  <swe:field name=\"Discharge\">\r\n"
                    + "    <swe:Quantity definition=\"http://sweet.jpl.nasa.gov/2.0/hydroSurface.owl#Discharge\">\r\n"
                    + "      <swe:uom code=\"m3\"/>\r\n" + "    </swe:Quantity>\r\n" + "  </swe:field>\r\n"
                    + "</swe:DataRecord>\"");

            session.save(hProcedureDescriptionFormat);
            session.save(hProcedure);
            session.save(hCodespace);
            session.save(hOffering);
            session.save(hFeatureOfInterestType);
            session.save(hFeatureOfInterest);
            session.save(hPlatform);
            session.save(hObservableProperty);
            session.save(hCategory);
            session.save(hObservationType);
            session.save(hObservationConstellation);
            session.save(hResultTemplateEntity);

            session.flush();

            hObservation.setValue(BigDecimal.valueOf(1.0));
            hObservation.setSamplingTimeStart(DateTime.now().toDate());
            hObservation.setSamplingTimeEnd(hObservation.getSamplingTimeStart());
            hObservation.setResultTime(hObservation.getSamplingTimeStart());
            hObservation.setDataset(hObservationConstellation);
            hObservation.setStaIdentifier("123");
            hObservation.setDeleted(false);
            session.save(hObservation);
            transaction.commit();

            ArrayList<DataEntity<?>> observationsFromDataBase = new ArrayList<>();
            observationsFromDataBase.add(hObservation);
            SosHelper sosHelper = new SosHelper();
            sosHelper.setServiceURL(URI.create("http://test.org/"));
            DaoFactory daoFactory = new DaoFactory();
            daoFactory.setSosHelper(sosHelper);
            // CALL
            OmObservationCreatorContext ctx = new OmObservationCreatorContext(null, null, daoFactory,
                    new ProfileHanlderMock(), Mockito.mock(AdditionalObservationCreatorRepository.class), null,
                    new FeatureQueryHandlerMock(), null, null, null, null, null,
                    Mockito.mock(BindingRepository.class));
            ObservationStream resultList = HibernateObservationUtilities.createSosObservationsFromObservations(
                    observationsFromDataBase, request, Locale.ENGLISH, null, ctx, session);
            // TEST RESULTS
            assertThat(resultList, is(notNullValue()));
            assertThat(resultList.hasNext(), is(true));
            Object value = resultList.next().getValue().getValue();
            assertThat(resultList.hasNext(), is(false));
            assertThat(value, is(instanceOf(QuantityValue.class)));
            Double val = ((QuantityValue) value).getValue().doubleValue();
//            assertThat(value, is(instanceOf(SweDataArray.class)));
//            Double val = Double.parseDouble(((SweDataArray) value).getValues().get(0).get(1));
            assertThat(val, is(closeTo(1.0, 0.00001)));
        } finally {
            returnSession(session);
        }
    }
}

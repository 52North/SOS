/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import java.util.ArrayList;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.QuantityDatasetEntity;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.util.observation.AdditionalObservationCreatorRepository;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.sos.service.profile.DefaultProfileHandler;

/**
 * The class <code>HibernateObservationUtilitiesTest</code> contains tests for
 * the class {@link <code>HibernateObservationUtilities</code>}
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike
 *         HinderkJ&uuml;rrens</a>
 *
 * @since 4.0.0
 *
 */
public class HibernateObservationUtilitiesTest
        extends HibernateTestCase {
    private static final String PROCEDURE = "junit_test_procedure_id";

    /*
     * Must be a valid feature identifier in the test data base
     */
    private static final String FEATURE = "1000";

    private static final String OBSERVABLE_PROPERTY = "http://sweet.jpl.nasa.gov/2.0/hydroSurface.owl#Discharge";

    private static final String PROCEDURE_DESCRIPTION_FORMAT = "junit_procedure_description_format";

    public static final String FEATURE_OF_INTEREST_TYPE = "junit_feature_of_interest_type";

    public static final String OFFERING = "junit_offering";

    public static final String CODESPACE = "junit_codespace";

    @Test
    public void returnEmptyCollectionIfCalledWithoutAnyParameters()
            throws OwsExceptionReport, ConverterException {
        ObservationStream resultList = HibernateObservationUtilities
                .createSosObservationFromObservationConstellation(null, null, null, null, null, new OmObservationCreatorContext(null, null, null, null, null, null, null, null, null, null, null, null), null);
        assertThat("result is null", resultList, is(not(nullValue())));
        assertThat("elements in list", resultList.hasNext(), is(false));
    }

    @Test
    @Ignore
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
            FormatEntity hObservationType = new FormatEntity();
            OfferingEntity hOffering = new OfferingEntity();
            DatasetEntity hObservationConstellation = new QuantityDatasetEntity();
            CodespaceEntity hCodespace = new CodespaceEntity();
            ProcedureEntity hProcedure = new ProcedureEntity();
            QuantityDataEntity hObservation = new QuantityDataEntity();

            hProcedureDescriptionFormat.setFormat(PROCEDURE_DESCRIPTION_FORMAT);
            hCodespace.setName(CODESPACE);
            hProcedure.setIdentifier(PROCEDURE);
            hProcedure.setFormat(hProcedureDescriptionFormat);
            hFeatureOfInterestType.setFormat(FEATURE_OF_INTEREST_TYPE);
            hFeatureOfInterest.setIdentifier(FEATURE);
            hFeatureOfInterest.setFeatureType(hFeatureOfInterestType);
            hFeatureOfInterest.setIdentifierCodespace(hCodespace);
            hObservableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            hObservationType.setFormat(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            hOffering.setIdentifier(OFFERING);
            hObservationConstellation.setProcedure(hProcedure);
            hObservationConstellation.setOffering(hOffering);
            hObservationConstellation.setObservableProperty(hObservableProperty);
            hObservationConstellation.setObservationType(hObservationType);
            hObservationConstellation.setFeature(hFeatureOfInterest);
            hObservationConstellation.setDeleted(false);
            hObservationConstellation.setHiddenChild(false);

            session.save(hProcedureDescriptionFormat);
            session.save(hProcedure);
            session.save(hCodespace);
            session.save(hOffering);
            session.save(hFeatureOfInterestType);
            session.save(hFeatureOfInterest);
            session.save(hObservableProperty);
            session.save(hObservationType);
            session.save(hObservationConstellation);

            session.flush();

            hObservation.setValue(new BigDecimal(1.0));
            hObservation.setSamplingTimeStart(DateTime.now().toDate());
            hObservation.setSamplingTimeEnd(hObservation.getSamplingTimeStart());
            hObservation.setResultTime(hObservation.getSamplingTimeStart());
            hObservation.setDataset(hObservationConstellation);
            hObservation.setDeleted(false);
            session.save(hObservation);
            transaction.commit();

            ArrayList<DataEntity<?>> observationsFromDataBase = new ArrayList<>();
            observationsFromDataBase.add(hObservation);
            // CALL
            ObservationStream resultList = HibernateObservationUtilities.createSosObservationsFromObservations(
                    observationsFromDataBase, request, Locale.ENGLISH, null,
                    new OmObservationCreatorContext(null, null, null, new DefaultProfileHandler(),
                            Mockito.mock(AdditionalObservationCreatorRepository.class), null,
                            new FeatureQueryHandlerMock(), null, null, null, null, null),
                    session);
            // TEST RESULTS
            assertThat(resultList, is(notNullValue()));
            assertThat(resultList.hasNext(), is(true));
            Object value = resultList.next().getValue().getValue();
            assertThat(resultList.hasNext(), is(false));
            Double val = Double.parseDouble(((SweDataArray) value).getValues().get(0).get(1));
            assertThat(value, is(instanceOf(SweDataArray.class)));
            assertThat(val, is(closeTo(1.0, 0.00001)));
        } finally {
            returnSession(session);
        }
    }
}

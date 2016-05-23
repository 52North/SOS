/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.NumericObservation;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.request.GetObservationByIdRequest;

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
public class HibernateObservationUtilitiesTest extends HibernateTestCase {
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
    public void returnEmptyCollectionIfCalledWithoutAnyParameters() throws OwsExceptionReport, ConverterException {
        List<OmObservation> resultList =
                HibernateObservationUtilities.createSosObservationsFromObservations(null, null, null, null);
        assertThat("result is null", resultList, is(not(nullValue())));
        assertThat("elements in list", resultList.size(), is(0));
    }

    @Test
    @Ignore
    // FIXME this one fails: SWE Array is only returned if a result template is
    // present
    public void createSubObservationOfSweArrayObservationViaGetObservationById() throws OwsExceptionReport,
            ConnectionProviderException, ConverterException {
        // PREPARE
        Session session = getSession();
        try {
            GetObservationByIdRequest request = new GetObservationByIdRequest();
            request.setVersion(Sos2Constants.SERVICEVERSION);

            ProcedureDescriptionFormat hProcedureDescriptionFormat = new ProcedureDescriptionFormat();
            FeatureOfInterestType hFeatureOfInterestType = new FeatureOfInterestType();
            FeatureOfInterest hFeatureOfInterest = new FeatureOfInterest();
            ObservableProperty hObservableProperty = new ObservableProperty();
            ObservationType hObservationType = new ObservationType();
            Offering hOffering = new Offering();
            ObservationConstellation hObservationConstellation = new ObservationConstellation();
            Codespace hCodespace = new Codespace();
            Procedure hProcedure = new Procedure();
            NumericObservation hObservation = new NumericObservation();

            hProcedureDescriptionFormat.setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT);
            hCodespace.setCodespace(CODESPACE);
            hProcedure.setIdentifier(PROCEDURE);
            hProcedure.setProcedureDescriptionFormat(hProcedureDescriptionFormat);
            hFeatureOfInterestType.setFeatureOfInterestType(FEATURE_OF_INTEREST_TYPE);
            hFeatureOfInterest.setIdentifier(FEATURE);
            hFeatureOfInterest.setFeatureOfInterestType(hFeatureOfInterestType);
            hFeatureOfInterest.setCodespace(hCodespace);
            hObservableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            hObservationType.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            hOffering.setIdentifier(OFFERING);
            hObservationConstellation.setProcedure(hProcedure);
            hObservationConstellation.setOffering(hOffering);
            hObservationConstellation.setObservableProperty(hObservableProperty);
            hObservationConstellation.setObservationType(hObservationType);
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

            hObservation.setValue(Double.valueOf(1.0));
            hObservation.setProcedure(hProcedure);
            hObservation.setOfferings(Collections.singleton(hOffering));
            hObservation.setObservableProperty(hObservableProperty);
            hObservation.setFeatureOfInterest(hFeatureOfInterest);
            hObservation.setDeleted(false);

            List<AbstractObservation> observationsFromDataBase = new ArrayList<AbstractObservation>();
            observationsFromDataBase.add(hObservation);
            // CALL
            List<OmObservation> resultList =
                    HibernateObservationUtilities.createSosObservationsFromObservations(observationsFromDataBase,
                            request, null, session);
            // TEST RESULTS
            assertThat(resultList, is(notNullValue()));
            assertThat(resultList.size(), is(1));
            Object value = resultList.get(0).getValue().getValue();
            Double val = Double.parseDouble(((SweDataArray) value).getValues().get(0).get(1));
            assertThat(value, is(instanceOf(SweDataArray.class)));
            assertThat(val, is(closeTo(1.0, 0.00001)));
        } finally {
            returnSession(session);
        }
    }
}

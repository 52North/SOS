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
package org.n52.sos.ds.hibernate;


import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ds.AbstractInsertResultTemplateHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;

/**
 * Implementation of the abstract class AbstractInsertResultTemplateDAO
 *
 * @since 4.0.0
 *
 */
public class InsertResultTemplateDAO extends AbstractInsertResultTemplateHandler {

    private HibernateSessionHolder sessionHolder;
    private DaoFactory daoFactory;

    public InsertResultTemplateDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public InsertResultTemplateResponse insertResultTemplate(InsertResultTemplateRequest request)
            throws OwsExceptionReport {
        InsertResultTemplateResponse response = new InsertResultTemplateResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        response.setAcceptedTemplate(request.getIdentifier().getValue());
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            OmObservationConstellation sosObsConst = request.getObservationTemplate();
            ObservationConstellation obsConst = null;
            for (String offeringID : sosObsConst.getOfferings()) {
                obsConst = daoFactory.getObservationConstellationDAO().checkObservationConstellation(sosObsConst, offeringID,
                        session, Sos2Constants.InsertResultTemplateParams.proposedTemplate.name());
                if (obsConst != null) {
                    FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO(daoFactory);
                    FeatureOfInterest feature = featureOfInterestDAO
                            .checkOrInsert(sosObsConst.getFeatureOfInterest(), session);
                    featureOfInterestDAO.checkOrInsertRelatedFeatureRelation(feature,
                            obsConst.getOffering(), session);
                    // check if result structure elements are supported
                    checkResultStructure(request.getResultStructure(),
                            obsConst.getObservableProperty().getIdentifier());
                    new ResultTemplateDAO().checkOrInsertResultTemplate(request, obsConst, feature, session);
                } else {
                    // TODO make better exception.
                    throw new InvalidObservationTypeException(request.getObservationTemplate().getObservationType());
                }
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Insert result template into database failed!");
        } catch (OwsExceptionReport owse) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw owse;
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplate.class);
    }

    private void checkResultStructure(SosResultStructure resultStructure, String observedProperty)
            throws OwsExceptionReport {
        // TODO modify or remove if complex field elements are supported
        final SweDataRecord record = setRecordFrom(resultStructure.get().get());

        for (final SweField swefield : record.getFields()) {

            if (!((swefield.getElement() instanceof SweAbstractSimpleType<?>)
              || (swefield.getElement() instanceof SweDataRecord))) {
                throw new NoApplicableCodeException().withMessage(
                        "The swe:Field element of type %s is not yet supported!",
                    swefield.getElement().getClass().getName());
            }
        }
        if (ResultHandlingHelper.hasPhenomenonTime(record) == -1) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("Missing swe:Time or swe:TimeRange with definition %s", OmConstants.PHENOMENON_TIME);
        }
        if (ResultHandlingHelper.checkFields(record.getFields(), observedProperty) == -1) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("Missing swe:field content with element definition %s", observedProperty);
        }
        if ((ResultHandlingHelper.hasResultTime(record) > -1 && record.getFields().size() > 3)
        || (ResultHandlingHelper.hasResultTime(record) == -1 && record.getFields().size() > 2)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "Supported resultStructure is swe:field content swe:Time or swe:TimeRange with element definition %s, "
                                    + " optional swe:Time with element definition %s and swe:field content swe:AbstractSimpleComponent or swe:DataRecord "
                                    + "with element definition %s",
                        OmConstants.PHENOMENON_TIME, OmConstants.RESULT_TIME, observedProperty);
        }
    }

}

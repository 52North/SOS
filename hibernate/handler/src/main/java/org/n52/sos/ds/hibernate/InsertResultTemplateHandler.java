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

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.exception.CodedException;
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
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;
import org.n52.sos.service.SosSettings;

import com.google.common.annotations.VisibleForTesting;

/**
 * Implementation of the abstract class AbstractInsertResultTemplateDAO
 *
 * @since 4.0.0
 *
 */
public class InsertResultTemplateHandler extends AbstractInsertResultTemplateHandler implements Constructable {

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    private ResultHandlingHelper helper;

    private boolean allowTemplateWithoutProcedureAndFeature;

    public InsertResultTemplateHandler() {
        super(SosConstants.SOS);
    }

    @Setting(SosSettings.ALLOW_TEMPLATE_WITHOUT_PROCEDURE_FEATURE)
    public void setAllowTemplateWithoutProcedureAndFeature(boolean allowTemplateWithoutProcedureAndFeature) {
        this.allowTemplateWithoutProcedureAndFeature = allowTemplateWithoutProcedureAndFeature;
    }

    @Override
    public void init() {
        sessionHolder = new HibernateSessionHolder(connectionProvider);
        helper = new ResultHandlingHelper(getDaoFactory().getGeometryHandler(), getDaoFactory().getSweHelper(),
                getDaoFactory().getDecoderRepository());
    }

    @Override
    public synchronized InsertResultTemplateResponse insertResultTemplate(InsertResultTemplateRequest request)
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
            DatasetEntity obsConst = null;
            for (String offeringID : sosObsConst.getOfferings()) {
                obsConst = getDaoFactory().getSeriesDAO().checkSeries(sosObsConst, offeringID, session,
                        Sos2Constants.InsertResultTemplateParams.proposedTemplate.name());
                if (obsConst != null) {
                    // check if result structure elements are supported
                    checkResultStructure(request.getResultStructure(),
                            obsConst.getObservableProperty().getIdentifier(), sosObsConst);
                    ProcedureEntity procedure = null;
                    AbstractFeatureEntity<?> feature = null;
                    if (sosObsConst.isSetFeatureOfInterest()) {
                        FeatureOfInterestDAO featureOfInterestDAO = getDaoFactory().getFeatureOfInterestDAO();
                        feature = featureOfInterestDAO.checkOrInsert(sosObsConst.getFeatureOfInterest(), session);
                        featureOfInterestDAO.checkOrInsertRelatedFeatureRelation(feature, obsConst.getOffering(),
                                session);
                    }
                    if (sosObsConst.isSetProcedure()) {
                        procedure = obsConst.getProcedure();
                    }
                    checkOrInsertResultTemplate(request, obsConst, procedure, feature, session);
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
        return HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private void checkOrInsertResultTemplate(InsertResultTemplateRequest request, DatasetEntity obsConst,
            ProcedureEntity procedure, AbstractFeatureEntity<?> feature, Session session) throws OwsExceptionReport {
        getDaoFactory().getResultTemplateDAO()
                .checkOrInsertResultTemplate(request, obsConst, procedure, feature, session);
    }

    private void checkResultStructure(SosResultStructure resultStructure, String observedProperty,
            OmObservationConstellation sosObsConst) throws OwsExceptionReport {
        // TODO modify or remove if complex field elements are supported
        final SweDataRecord record = setRecordFrom(resultStructure.get().get());

        List<String> definitions = new LinkedList<>();
        for (final SweField swefield : record.getFields()) {
            checkDuplicateDefinitions(definitions, swefield);
            if (!((swefield.getElement() instanceof SweAbstractSimpleType<?>) || helper.isDataRecord(swefield)
                    || helper.isVector(swefield) || helper.isDataArray(swefield))) {
                throw new NoApplicableCodeException().withMessage(
                        "The swe:Field element of type %s is not yet supported!",
                        swefield.getElement().getClass().getName());
            }
            helper.checkDataRecordForObservedProperty(swefield, observedProperty);
            helper.checkVectorForSamplingGeometry(swefield);
        }
        if (helper.hasPhenomenonTime(record) == -1) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("Missing swe:Time or swe:TimeRange with definition %s", OmConstants.PHENOMENON_TIME);
        }
        if (helper.checkFields(record.getFields(), observedProperty) == -1) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("Missing swe:field content with element definition %s", observedProperty);
        }
        if (allowTemplateWithoutProcedureAndFeature) {
            if (sosObsConst.getNillableFeatureOfInterest().isNil()
                    && helper.checkFields(record.getFields(), ResultHandlingHelper.OM_FEATURE_OF_INTEREST) == -1) {
                throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                        .withMessage(
                                "Missing swe:field content with element definition '%s' because the "
                                        + "featureOfInterest is not defined in the observationTemplate!",
                                ResultHandlingHelper.OM_FEATURE_OF_INTEREST);
            }
            if (sosObsConst.getNillableProcedure().isNil()
                    && helper.checkFields(record.getFields(), ResultHandlingHelper.OM_PROCEDURE) == -1) {
                throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                        .withMessage(
                                "Missing swe:field content with element definition '%s' because the procdure "
                                        + "is not defined in the observationTemplate!",
                                ResultHandlingHelper.OM_PROCEDURE);
            }
        }
        if (record.getFields().size() > getAllowedSize(record)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "Supported resultStructure is swe:field content swe:Time or swe:TimeRange with element "
                                    + "definition '%s', optional swe:Time with element definition '%s' and swe:field "
                                    + "content swe:AbstractSimpleComponent or swe:DataRecord  with element definition"
                                    + " '%s' or swe:Vector with element defintion '%s' or swe:Text with element"
                                    + " definitions '%s' and '%s' and swe:DataRecord with element definition '%s'!",
                            OmConstants.PHENOMENON_TIME, OmConstants.RESULT_TIME, observedProperty,
                            OmConstants.PARAM_NAME_SAMPLING_GEOMETRY, ResultHandlingHelper.OM_FEATURE_OF_INTEREST,
                            ResultHandlingHelper.OM_PROCEDURE, OmConstants.OM_PARAMETER);
        }
    }

    private void checkDuplicateDefinitions(List<String> definitions, SweField swefield) throws CodedException {
        if (definitions.contains(swefield.getElement().getDefinition())) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("The definition '%s' is already defined! Please check your insert result template!",
                            swefield.getElement().getDefinition());
        } else {
            definitions.add(swefield.getElement().getDefinition());
        }
    }

    private int getAllowedSize(SweDataRecord record) throws CodedException {
        int allowedSize = 2;
        if (helper.hasResultTime(record) > -1) {
            allowedSize++;
        }
        int additionalValues = 0;
        for (final SweField swefield : record.getFields()) {
            if (helper.isVector(swefield) && helper.checkVectorForSamplingGeometry(swefield)) {
                additionalValues++;
            }
            if (allowTemplateWithoutProcedureAndFeature) {
                if (helper.isText(swefield)
                        && helper.checkDefinition(swefield, ResultHandlingHelper.OM_FEATURE_OF_INTEREST)) {
                    additionalValues++;
                }
                if (helper.isText(swefield) && helper.checkDefinition(swefield, ResultHandlingHelper.OM_PROCEDURE)) {
                    additionalValues++;
                }
            }
            if (helper.checkDataRecordForParameter(swefield)) {
                additionalValues++;
            }
        }
        return allowedSize + additionalValues;
    }

    @VisibleForTesting
    protected synchronized void initForTesting(DaoFactory daoFactory, ConnectionProvider connectionProvider) {
        this.daoFactory = daoFactory;
        this.connectionProvider = connectionProvider;
    }

}

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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.XmlNamespaceDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.NoDecoderForKeyException;
import org.n52.svalbard.decode.exception.XmlDecodingException;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.XmlBeansEncodingFlags;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Hibernate data access class for featureofInterest types
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ResultTemplateDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultTemplateDAO.class);
    private EncoderRepository encoderRepository;
    private DecoderRepository decoderRepository;
    private XmlOptionsHelper xmlOptionsHelper;

    public ResultTemplateDAO(EncoderRepository encoderRepository, XmlOptionsHelper xmlOptionsHelper, DecoderRepository decoderRepository) {
        this.encoderRepository = encoderRepository;
        this.xmlOptionsHelper = xmlOptionsHelper;
        this.decoderRepository = decoderRepository;
    }

    /**
     * Get result template object for result template identifier
     *
     * @param identifier
     *            Result template identifier
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    public ResultTemplateEntity getResultTemplateObject(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(ResultTemplateEntity.class)
                        .add(Restrictions.eq(ResultTemplateEntity.PROPERTY_IDENTIFIER, identifier))
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        LOGGER.debug("QUERY getResultTemplateObject(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (ResultTemplateEntity) criteria.uniqueResult();
    }

    /**
     * Get all result template objects
     *
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    @SuppressWarnings("unchecked")
    public List<ResultTemplateEntity> getResultTemplateObjects(final Session session) {
        return session.createCriteria(ResultTemplateEntity.class)
                .setFetchMode(ResultTemplateEntity.PROPERTY_OFFERING, FetchMode.JOIN)
                .setFetchMode(ResultTemplateEntity.PROPERTY_PHENOMENON, FetchMode.JOIN)
                .setFetchMode(ResultTemplateEntity.PROPERTY_FEATURE, FetchMode.JOIN)
                .list();
    }

    /**
     * Get result template object for observation constellation
     *
     * @param observationConstellation
     *            Observation constellation object
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    public ResultTemplateEntity getResultTemplateObjectsForObservationConstellation(
            final DatasetEntity observationConstellation, final Session session) {
        return getResultTemplateObject(observationConstellation.getOffering().getIdentifier(),
                observationConstellation.getObservableProperty().getIdentifier(), session);
    }

    /**
     * Get result template objects for observation constellation and
     * featureOfInterest
     *
     * @param observationConstellation
     *            Observation constellation object
     * @param sosAbstractFeature
     *            FeatureOfInterest
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    public List<ResultTemplateEntity> getResultTemplateObjectsForObservationConstellationAndFeature(
            final DatasetEntity observationConstellation, final AbstractFeature sosAbstractFeature,
            final Session session) {
        return getResultTemplateObject(observationConstellation.getOffering().getIdentifier(),
                observationConstellation.getObservableProperty().getIdentifier(),
                Lists.newArrayList(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue()), session);
    }

    /**
     * Get result template object for offering identifier and observable
     * property identifier
     *
     * @param offering
     *            Offering identifier
     * @param observedProperty
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    @SuppressWarnings("unchecked")
    public ResultTemplateEntity getResultTemplateObject(final String offering, final String observedProperty,
            final Session session) {
        final Criteria rtc = session.createCriteria(ResultTemplateEntity.class).setMaxResults(1);
        rtc.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
        rtc.createCriteria(DatasetEntity.PROPERTY_PHENOMENON).add(
                Restrictions.eq(PhenomenonEntity.IDENTIFIER, observedProperty));
        /* there can be multiple but equal result templates... */
        LOGGER.debug("QUERY getResultTemplateObject(offering, observedProperty): {}",
                HibernateHelper.getSqlString(rtc));
        final List<ResultTemplateEntity> templates = rtc.list();
        return templates.isEmpty() ? null : templates.iterator().next();
    }

    /**
     * Get result template objects for offering identifier, observable property
     * identifier and featureOfInterest identifier
     *
     * @param offering
     *            Offering identifier
     * @param observedProperty
     *            Observable property identifier
     * @param featureOfInterest
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    @SuppressWarnings("unchecked")
    public List<ResultTemplateEntity> getResultTemplateObject(final String offering, final String observedProperty,
            final Collection<String> featureOfInterest, final Session session) {
        final Criteria rtc =
                session.createCriteria(ResultTemplateEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        rtc.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
        rtc.createCriteria(DatasetEntity.PROPERTY_PHENOMENON).add(
                Restrictions.eq(PhenomenonEntity.IDENTIFIER, observedProperty));
        if (featureOfInterest != null && !featureOfInterest.isEmpty()) {
            rtc.createAlias(ResultTemplateEntity.PROPERTY_FEATURE, "foi", JoinType.LEFT_OUTER_JOIN);
            rtc.add(Restrictions.or(Restrictions.isNull(ResultTemplateEntity.PROPERTY_FEATURE),
                    Restrictions.in("foi." + AbstractFeatureEntity.IDENTIFIER, featureOfInterest)));
            // rtc.createCriteria(ResultTemplate.FEATURE_OF_INTEREST).add(
            // Restrictions.in(FeatureOfInterest.IDENTIFIER,
            // featureOfInterest));
        }
        LOGGER.debug("QUERY getResultTemplateObject(offering, observedProperty, featureOfInterest): {}",
                HibernateHelper.getSqlString(rtc));
        return rtc.list();
    }

    /**
     * Check or insert result template
     *
     * @param request
     *            Insert result template request
     * @param observationConstellation
     *            Observation constellation object
     * @param procedure
     * @param featureOfInterest
     *            FeatureOfInterest object
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If the requested structure/encoding is invalid
     */
    public void checkOrInsertResultTemplate(InsertResultTemplateRequest request,
            DatasetEntity observationConstellation,
            ProcedureEntity procedure,
            AbstractFeatureEntity featureOfInterest,
            Session session)
            throws OwsExceptionReport {
        try {
            String offering = observationConstellation.getOffering().getIdentifier();
            String observableProperty = observationConstellation.getObservableProperty().getIdentifier();

            List<ResultTemplateEntity> resultTemplates =
                    getResultTemplateObject(offering, observableProperty, null, session);
            if (CollectionHelper.isEmpty(resultTemplates)) {
                createAndSaveResultTemplate(request, observationConstellation, procedure, featureOfInterest, session);
            } else {
                List<String> storedIdentifiers = new ArrayList<>(0);

                for (ResultTemplateEntity storedResultTemplate : resultTemplates) {
                    storedIdentifiers.add(storedResultTemplate.getIdentifier());
                    SosResultStructure storedStructure =
                            createSosResultStructure(storedResultTemplate.getStructure());
                    SosResultEncoding storedEncoding =
                            createSosResultEncoding(storedResultTemplate.getEncoding());

                    if (!storedStructure.equals(request.getResultStructure())) {
                        throw new InvalidParameterValueException()
                                .at(Sos2Constants.InsertResultTemplateParams.proposedTemplate)
                                .withMessage(
                                        "The requested resultStructure is different from already inserted result template for procedure (%s) observedProperty (%s) and offering (%s)!",
                                        procedure.getIdentifier(), observableProperty, offering);
                    }

                    if (!storedEncoding.equals(request.getResultEncoding())) {
                        throw new InvalidParameterValueException()
                                .at(Sos2Constants.InsertResultTemplateParams.proposedTemplate)
                                .withMessage(
                                        "The requested resultEncoding is different from already inserted result template for procedure (%s) observedProperty (%s) and offering (%s)!",
                                        procedure.getIdentifier(), observableProperty, offering);
                    }
                }
                if (request.getIdentifier() != null && !storedIdentifiers.contains(request.getIdentifier())) {
                    /* save it only if the identifier is different */
                    createAndSaveResultTemplate(request, observationConstellation, procedure, featureOfInterest,
                            session);
                }

            }
        } catch (EncodingException | DecodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }
    }

    /**
     * Insert result template
     *
     * @param request
     *            Insert result template request
     * @param observationConstellation
     *            Observation constellation object
     * @param procedure
     * @param featureOfInterest
     *            FeatureOfInterest object
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    private void createAndSaveResultTemplate(final InsertResultTemplateRequest request,
            final DatasetEntity observationConstellation, ProcedureEntity procedure,
            final AbstractFeatureEntity featureOfInterest,
            final Session session)
            throws EncodingException {
        final ResultTemplateEntity resultTemplate = new ResultTemplateEntity();
        resultTemplate.setIdentifier(request.getIdentifier().getValue());
        resultTemplate.setPhenomenon(observationConstellation.getObservableProperty());
        resultTemplate.setOffering(observationConstellation.getOffering());
        if (procedure != null) {
            resultTemplate.setProcedure(procedure);
        }
        if (featureOfInterest != null) {
            resultTemplate.setFeature(featureOfInterest);
        }

        if (request.getResultEncoding().getXml().isPresent()) {
            resultTemplate.setEncoding(request.getResultEncoding().getXml().get());
        } else {
            resultTemplate.setEncoding(
                    encodeObjectToXmlText(SweConstants.NS_SWE_20, request.getResultEncoding().get().get()));
        }
        if (request.getResultStructure().getXml().isPresent()) {
            resultTemplate.setStructure(request.getResultStructure().getXml().get());
        } else {
            resultTemplate.setStructure(
                    encodeObjectToXmlText(SweConstants.NS_SWE_20, request.getResultStructure().get().get()));
        }

        session.save(resultTemplate);
        session.flush();
    }

    private SosResultEncoding createSosResultEncoding(String resultEncoding)
            throws DecodingException {
        return new SosResultEncoding((SweAbstractEncoding) decodeXmlObject(resultEncoding), resultEncoding);
    }

    private SosResultStructure createSosResultStructure(String resultStructure)
            throws DecodingException {
        return new SosResultStructure((SweAbstractDataComponent) decodeXmlObject(resultStructure), resultStructure);
    }

    private String encodeObjectToXmlText(String namespace, Object object)
            throws EncodingException {
        return encodeObjectToXml(namespace, object).xmlText(this.xmlOptionsHelper.getXmlOptions());
    }

    private XmlObject encodeObjectToXml(String namespace, Object object)
            throws EncodingException {
        return getEncoder(namespace, object).encode(object, EncodingContext.of(XmlBeansEncodingFlags.DOCUMENT, true));
    }

    private <T> Encoder<XmlObject, T> getEncoder(String namespace, T o)
            throws EncodingException {
        EncoderKey key = new XmlEncoderKey(namespace, o.getClass());
        Encoder<XmlObject, T> encoder = encoderRepository.getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    private <T> T decodeXmlObject(XmlObject xbObject)
            throws DecodingException {
        DecoderKey key = CodingHelper.getDecoderKey(xbObject);
        Decoder<T, XmlObject> decoder = decoderRepository.getDecoder(key);
        if (decoder == null) {
            DecoderKey schemaTypeKey = new XmlNamespaceDecoderKey(xbObject.schemaType().getName().getNamespaceURI(),
                    xbObject.getClass());
            decoder = decoderRepository.getDecoder(schemaTypeKey);
        }
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    private Object decodeXmlObject(String xmlString)
            throws DecodingException {
        try {
            return decodeXmlObject(XmlObject.Factory.parse(xmlString));
        } catch (final XmlException e) {
            throw new XmlDecodingException("XML string", xmlString, e);
        }
    }

}

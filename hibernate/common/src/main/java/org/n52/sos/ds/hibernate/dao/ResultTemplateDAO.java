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
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
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
    public ResultTemplate getResultTemplateObject(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(ResultTemplate.class)
                        .add(Restrictions.eq(ResultTemplate.IDENTIFIER, identifier))
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        LOGGER.debug("QUERY getResultTemplateObject(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (ResultTemplate) criteria.uniqueResult();
    }

    /**
     * Get all result template objects
     *
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    @SuppressWarnings("unchecked")
    public List<ResultTemplate> getResultTemplateObjects(final Session session) {
        return session.createCriteria(ResultTemplate.class)
                .setFetchMode(ResultTemplate.OFFERING, FetchMode.JOIN)
                .setFetchMode(ResultTemplate.OBSERVABLE_PROPERTY, FetchMode.JOIN)
                .setFetchMode(ResultTemplate.FEATURE_OF_INTEREST, FetchMode.JOIN)
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
    public ResultTemplate getResultTemplateObjectsForObservationConstellation(
            final ObservationConstellation observationConstellation, final Session session) {
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
    public List<ResultTemplate> getResultTemplateObjectsForObservationConstellationAndFeature(
            final ObservationConstellation observationConstellation, final AbstractFeature sosAbstractFeature,
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
    public ResultTemplate getResultTemplateObject(final String offering, final String observedProperty,
            final Session session) {
        final Criteria rtc = session.createCriteria(ResultTemplate.class).setMaxResults(1);
        rtc.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        rtc.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
        /* there can be multiple but equal result templates... */
        LOGGER.debug("QUERY getResultTemplateObject(offering, observedProperty): {}",
                HibernateHelper.getSqlString(rtc));
        final List<ResultTemplate> templates = rtc.list();
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
    public List<ResultTemplate> getResultTemplateObject(final String offering, final String observedProperty,
            final Collection<String> featureOfInterest, final Session session) {
        final Criteria rtc =
                session.createCriteria(ResultTemplate.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        rtc.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        rtc.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
        if (featureOfInterest != null && !featureOfInterest.isEmpty()) {
            rtc.createAlias(ResultTemplate.FEATURE_OF_INTEREST, "foi", JoinType.LEFT_OUTER_JOIN);
            rtc.add(Restrictions.or(Restrictions.isNull(ResultTemplate.FEATURE_OF_INTEREST),
                    Restrictions.in("foi." + FeatureOfInterest.IDENTIFIER, featureOfInterest)));
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
            ObservationConstellation observationConstellation,
            Procedure procedure,
            AbstractFeatureOfInterest featureOfInterest,
            Session session)
            throws OwsExceptionReport {
        try {
            String offering = observationConstellation.getOffering().getIdentifier();
            String observableProperty = observationConstellation.getObservableProperty().getIdentifier();

            List<ResultTemplate> resultTemplates =
                    getResultTemplateObject(offering, observableProperty, null, session);
            if (CollectionHelper.isEmpty(resultTemplates)) {
                createAndSaveResultTemplate(request, observationConstellation, procedure, featureOfInterest, session);
            } else {
                List<String> storedIdentifiers = new ArrayList<>(0);

                for (ResultTemplate storedResultTemplate : resultTemplates) {
                    storedIdentifiers.add(storedResultTemplate.getIdentifier());
                    SosResultStructure storedStructure =
                            createSosResultStructure(storedResultTemplate.getResultStructure());
                    SosResultEncoding storedEncoding =
                            createSosResultEncoding(storedResultTemplate.getResultEncoding());

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
            final ObservationConstellation observationConstellation, Procedure procedure,
            final AbstractFeatureOfInterest featureOfInterest,
            final Session session)
            throws EncodingException {
        final ResultTemplate resultTemplate = new ResultTemplate();
        resultTemplate.setIdentifier(request.getIdentifier().getValue());
        resultTemplate.setObservableProperty(observationConstellation.getObservableProperty());
        resultTemplate.setOffering(observationConstellation.getOffering());
        if (procedure != null) {
            resultTemplate.setProcedure(procedure);
        }
        if (featureOfInterest != null) {
            resultTemplate.setFeatureOfInterest(featureOfInterest);
        }

        if (request.getResultEncoding().getXml().isPresent()) {
            resultTemplate.setResultEncoding(request.getResultEncoding().getXml().get());
        } else {
            resultTemplate.setResultEncoding(
                    encodeObjectToXmlText(SweConstants.NS_SWE_20, request.getResultEncoding().get().get()));
        }
        if (request.getResultStructure().getXml().isPresent()) {
            resultTemplate.setResultStructure(request.getResultStructure().getXml().get());
        } else {
            resultTemplate.setResultStructure(
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

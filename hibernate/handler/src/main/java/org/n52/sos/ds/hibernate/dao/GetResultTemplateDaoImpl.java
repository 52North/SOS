/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ds.dao.GetResultTemplateDao;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class GetResultTemplateDaoImpl extends AbstractDaoImpl implements GetResultTemplateDao, Constructable {

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    private boolean supportsDatabaseEntities;

    private DecoderRepository decodingRepository;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decodingRepository) {
        this.decodingRepository = decodingRepository;
    }

    @Override
    public void init() {
        this.supportsDatabaseEntities = HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }

    private DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public GetResultTemplateResponse queryResultTemplate(GetResultTemplateRequest request,
            GetResultTemplateResponse response) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return queryResultTemplate(request, response, session);
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying result data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public GetResultTemplateResponse queryResultTemplate(GetResultTemplateRequest request,
            GetResultTemplateResponse response, Object connection) throws OwsExceptionReport {
        if (checkConnection(connection)) {
            return getResultTemplate(request, response, HibernateSessionHolder.getSession(connection));
        }
        return response;

    }

    private GetResultTemplateResponse getResultTemplate(GetResultTemplateRequest request,
            GetResultTemplateResponse response, Session session) throws CodedException {
        try {
            ResultTemplateEntity resultTemplate = supportsDatabaseEntities ? getDaoFactory().getResultTemplateDAO()
                    .getResultTemplateObjectForResponse(request.getOffering(), request.getObservedProperty(), session)
                    : null;
            if (resultTemplate != null && resultTemplate.isSetStructure() && resultTemplate.isSetEncoding()) {
                response.setResultEncoding(createSosResultEncoding(resultTemplate.getEncoding()));
                response.setResultStructure(createSosResultStructure(resultTemplate.getStructure()));
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data result template data!");
        }
    }

    protected SosResultStructure createSosResultStructure(String resultStructure) throws CodedException {
        SweAbstractDataComponent abstractDataComponent = (SweAbstractDataComponent) decode(resultStructure);
        if (abstractDataComponent != null) {
            return new SosResultStructure(abstractDataComponent, resultStructure);
        }
        return new SosResultStructure(resultStructure);
    }

    protected SosResultEncoding createSosResultEncoding(String resultEncoding) throws CodedException {
        SweAbstractEncoding abstractEncoding = (SweAbstractEncoding) decode(resultEncoding);
        if (abstractEncoding != null) {
            return new SosResultEncoding(abstractEncoding, resultEncoding);
        }
        return new SosResultEncoding(resultEncoding);
    }

    protected Object decode(String xml) throws CodedException {
        try {
            XmlObject xmlObject = XmlHelper.parseXmlString(xml);
            DecoderKey decoderKey = CodingHelper.getDecoderKey(xmlObject);
            Decoder<Object, Object> decoder = decodingRepository.getDecoder(decoderKey);
            if (decoder != null) {
                return decoder.decode(xmlObject);
            } else {
                throw new NoApplicableCodeException().withMessage("No decoder found for %s", xmlObject.getClass()
                        .getName());
            }
        } catch (DecodingException de) {
            throw new NoApplicableCodeException().causedBy(de);
        }
    }

}

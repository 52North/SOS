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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.metadata.I18NFeatureMetadata;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.IdentifierNameDescriptionEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.AbstractGML;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public class AbstractIdentifierNameDescriptionDAO extends TimeCreator {

    private final DaoFactory daoFactory;

    public AbstractIdentifierNameDescriptionDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public void addIdentifierNameDescription(AbstractGML abstractFeature,
            IdentifierNameDescriptionEntity entity,
                                             Session session) {
        addIdentifier(abstractFeature, entity, session);
        addName(abstractFeature, entity, session);
        addDescription(abstractFeature, entity);
    }

    public void addIdentifier(AbstractGML abstractFeature,
                    IdentifierNameDescriptionEntity entity,
                              Session session) {
        addIdentifier(entity, abstractFeature.getIdentifierCodeWithAuthority(), session);
    }

    public void addIdentifier(IdentifierNameDescriptionEntity entity,
                              CodeWithAuthority identifier, Session session) {
        String value = identifier != null && identifier.isSetValue()
                               ? identifier.getValue() : null;
        String codespace = identifier != null && identifier.isSetCodeSpace()
                                   ? identifier.getCodeSpace()
                           : OGCConstants.UNKNOWN;
        entity.setIdentifier(value);
        entity.setIdentifierCodespace(new CodespaceDAO()
                .getOrInsertCodespace(codespace, session));
    }

    public void addName(AbstractGML abstractFeature,
            IdentifierNameDescriptionEntity entity,
                        Session session) {
        addName(entity, abstractFeature.getFirstName(), session);
    }

    public void addName(IdentifierNameDescriptionEntity entity, CodeType name,
                        Session session) {
        String value = name != null && name.isSetValue()
                               ? name.getValue() : null;
        String codespace = name != null && name.isSetCodeSpace()
                                   ? name.getCodeSpace().toString() : OGCConstants.UNKNOWN;
        entity.setName(value);
        entity.setNameCodespace(new CodespaceDAO()
                .getOrInsertCodespace(codespace, session));
    }

    public void addDescription(AbstractGML abstractFeature,
            IdentifierNameDescriptionEntity entity) {
        addDescription(entity, abstractFeature.getDescription());
    }

    public void addDescription(IdentifierNameDescriptionEntity entity,
                               String description) {
        if (description != null && !description.isEmpty()) {
            entity.setDescription(description);
        }
    }

    public void getAndAddIdentifierNameDescription(AbstractGML abstractFeature,
            IdentifierNameDescriptionEntity entity) throws OwsExceptionReport {
        abstractFeature.setIdentifier(getIdentifier(entity));
        abstractFeature.addName(getName(entity));
        abstractFeature.setDescription(getDescription(entity));
    }

    public CodeWithAuthority getIdentifier(IdentifierNameDescriptionEntity entity) {
        CodeWithAuthority identifier = new CodeWithAuthority(entity.getIdentifier());
        if (entity.isSetIdentifierCodespace()) {
            identifier.setCodeSpace(entity.getIdentifierCodespace().getName());
        }
        return identifier;
    }

    public CodeType getName(IdentifierNameDescriptionEntity entity) throws OwsExceptionReport {
        if (entity.isSetName()) {
            CodeType name = new CodeType(entity.getName());
            if (entity.isSetNameCodespace()) {
                try {
                    name.setCodeSpace(new URI(entity.getNameCodespace().getName()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating URI from '{}'", entity.getNameCodespace().getName());
                }
            }
            return name;
        }
        return null;
    }

    public String getDescription(IdentifierNameDescriptionEntity entity) {
        if (entity.isSetDescription()) {
            return entity.getDescription();
        }
        return null;
    }

    public void insertNames(AbstractFeatureEntity feature, List<CodeType> name, I18NDAORepository i18nr, Session session) {
        CodespaceDAO codespaceDAO = new CodespaceDAO();
        I18NDAO<I18NFeatureMetadata> dao = i18nr.getDAO(I18NFeatureMetadata.class);
        for (CodeType codeType : name) {
            CodespaceEntity codespace = codespaceDAO.getOrInsertCodespace(codeType.getCodeSpace().toString(), session);
//            i18ndao.insertI18N(feature, new I18NInsertionObject(codespace, codeType.getValue()), session);
        }
    }

    public void insertNameAndDescription(IdentifierNameDescriptionEntity entity,
                                         AbstractFeature abstractFeature,
                                         Session session) {
        if (abstractFeature.isSetName()) {

        }
//        session.saveOrUpdate(
//
//        AbstractI18NDAO<?, ?> i18ndao = DaoFactory.getInstance().getI18NDAO(feature, session);
//        featureOfInterestDAO.addIdentifierNameDescription(samplingFeature, feature, session);
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }
}

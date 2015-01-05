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
package org.n52.sos.ds.hibernate.dao;

import java.util.List;

import org.hibernate.Session;

import org.n52.sos.ds.I18NDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.metadata.I18NFeatureMetadata;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractGML;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;

public class AbstractIdentifierNameDescriptionDAO extends TimeCreator {

    public void addIdentifierNameDescription(AbstractGML abstractFeature,
            AbstractIdentifierNameDescriptionEntity entity, Session session) {
        addIdentifier(abstractFeature, entity, session);
        addName(abstractFeature, entity, session);
        addDescription(abstractFeature, entity);
    }

    public void addIdentifier(AbstractGML abstractFeature, AbstractIdentifierNameDescriptionEntity entity,
            Session session) {
        if (abstractFeature.isSetIdentifier()) {
            entity.setIdentifier(abstractFeature.getIdentifierCodeWithAuthority().getValue());
            if (abstractFeature.getIdentifierCodeWithAuthority().isSetCodeSpace()) {
                entity.setCodespace(new CodespaceDAO().getOrInsertCodespace(abstractFeature.getIdentifierCodeWithAuthority()
                        .getCodeSpace(), session));
            }
        }
        if (!entity.isSetCodespace()) {
            entity.setCodespace(new CodespaceDAO().getOrInsertCodespace(OGCConstants.UNKNOWN, session));
        }
    }

    protected void addName(AbstractGML abstractFeature, AbstractIdentifierNameDescriptionEntity entity,
            Session session) {
        if (abstractFeature.isSetName()) {
            entity.setName(abstractFeature.getFirstName().getValue());
            if (abstractFeature.getFirstName().isSetCodeSpace()) {
                entity.setCodespaceName(new CodespaceDAO().getOrInsertCodespace(abstractFeature.getFirstName()
                        .getCodeSpace(), session));
            }
        }
        if (!entity.isSetCodespaceName()) {
            entity.setCodespaceName(new CodespaceDAO().getOrInsertCodespace(OGCConstants.UNKNOWN, session));
        }
    }

    protected void addDescription(AbstractGML abstractFeature, AbstractIdentifierNameDescriptionEntity entity) {
        if (abstractFeature.isSetDescription()) {
            entity.setDescription(abstractFeature.getDescription());
        }
    }

    public void getAndAddIdentifierNameDescription(AbstractGML abstractFeature,
            AbstractIdentifierNameDescriptionEntity entity) {
        abstractFeature.setIdentifier(getIdentifier(entity));
        abstractFeature.addName(getName(entity));
        abstractFeature.setDescription(getDescription(entity));
    }

    public CodeWithAuthority getIdentifier(AbstractIdentifierNameDescriptionEntity entity) {
        CodeWithAuthority identifier = new CodeWithAuthority(entity.getIdentifier());
        if (entity.isSetCodespace()) {
            identifier.setCodeSpace(entity.getCodespace().getCodespace());
        }
        return identifier;
    }

    public CodeType getName(AbstractIdentifierNameDescriptionEntity entity) {
        if (entity.isSetName()) {
            CodeType name = new CodeType(entity.getName());
            if (entity.isSetCodespaceName()) {
                name.setCodeSpace(entity.getCodespaceName().getCodespace());
            }
            return name;
        }
        return null;
    }

    public String getDescription(AbstractIdentifierNameDescriptionEntity entity) {
        if (entity.isSetDescription()) {
            return entity.getDescription();
        }
        return null;
    }

    public void insertNames(FeatureOfInterest feature, List<CodeType> name, Session session) {
        CodespaceDAO codespaceDAO = new CodespaceDAO();
        I18NDAO<I18NFeatureMetadata> dao
                = I18NDAORepository.getInstance().getDAO(I18NFeatureMetadata.class);
        for (CodeType codeType : name) {
            Codespace codespace = codespaceDAO.getOrInsertCodespace(codeType.getCodeSpace(), session);
//            i18ndao.insertI18N(feature, new I18NInsertionObject(codespace, codeType.getValue()), session);
        }
    }

    public void insertNameAndDescription(AbstractIdentifierNameDescriptionEntity entity, SamplingFeature samplingFeature, Session session) {
        if (samplingFeature.isSetName()) {


        }
//        session.saveOrUpdate(
//
//        AbstractI18NDAO<?, ?> i18ndao = DaoFactory.getInstance().getI18NDAO(feature, session);
//        featureOfInterestDAO.addIdentifierNameDescription(samplingFeature, feature, session);
    }
}

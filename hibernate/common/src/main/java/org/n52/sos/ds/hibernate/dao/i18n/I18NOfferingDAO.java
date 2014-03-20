/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.i18n;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ds.hibernate.entities.i18n.I18NInsertionObject;
import org.n52.sos.ds.hibernate.entities.i18n.I18NOffering;

/**
 * I18N DAO class for Offering values
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class I18NOfferingDAO extends AbstractFeatureI18NDAO {

    @Override
    public List<AbstractFeatureI18N> getObjects(Session session) {
        return getAbstractI18NObjects(I18NOffering.class, session);
    }

    @Override
    public List<AbstractFeatureI18N> getObjects(Codespace codespace, Session session) {
        return getAbstractI18NObjects(I18NOffering.class, codespace, session);
    }

    @Override
    public List<AbstractFeatureI18N> getObjectsForCodespaceString(String codespace, Session session) {
        return getAbstractI18NObjectsForCodespaceString(I18NOffering.class, codespace, session);
    }

    @Override
    public List<AbstractFeatureI18N> getObjects(long objectId, Session session) {
        return (List<AbstractFeatureI18N>) getAbstractI18NObjects(I18NOffering.class, objectId, session);
    }

    @Override
    public I18NOffering getObject(long objectId, Codespace codespace, Session session) {
        return (I18NOffering) getAbstractI18NObjects(I18NOffering.class, objectId, codespace, session);
    }

    @Override
    public I18NOffering getObject(long objectId, String codespace, Session session) {
        return (I18NOffering) getAbstractI18NObjects(I18NOffering.class, objectId, codespace, session);
    }
    
    @Override
    public List<AbstractFeatureI18N> getObjects(AbstractIdentifierNameDescriptionEntity object, Session session) {
        return (List<AbstractFeatureI18N>) getAbstractI18NObjects(I18NOffering.class, object, session);
    }

    @Override
    public AbstractFeatureI18N getObject(AbstractIdentifierNameDescriptionEntity object, Codespace codespace, Session session) {
        return getAbstractI18NObjects(AbstractIdentifierNameDescriptionEntity.class, object, codespace, session);
    }

    @Override
    public AbstractFeatureI18N getObject(AbstractIdentifierNameDescriptionEntity object, String codespace, Session session) {
        return getAbstractI18NObjects(I18NOffering.class, object, codespace, session);
    }

    @Override
    public List<AbstractFeatureI18N> getObjects(AbstractFeatureI18N abstractI18N, Session session) {
        return (List<AbstractFeatureI18N>) getAbstractI18NObjects(I18NOffering.class, abstractI18N, session);
    }

    @Override
    public I18NOffering getObject(AbstractFeatureI18N abstractI18N, Codespace codespace,
            Session session) {
        return (I18NOffering) getAbstractI18NObjects(I18NOffering.class, abstractI18N, codespace, session);
    }

    @Override
    public I18NOffering getObject(AbstractFeatureI18N abstractI18N, String codespace, Session session) {
        return (I18NOffering) getAbstractI18NObjects(I18NOffering.class, abstractI18N, codespace, session);
    }
    
    @Override
    public void insertI18N(AbstractIdentifierNameDescriptionEntity objectId, I18NInsertionObject insertionObject, Session session) {
       insertAbstractI18N(I18NOffering.class, objectId, insertionObject, session);
    }

    @Override
    public void insertI18N(AbstractIdentifierNameDescriptionEntity objectId, List<I18NInsertionObject> insertionObjects, Session session) {
        for (I18NInsertionObject i18nInsertionObject : insertionObjects) {
            insertAbstractI18N(I18NOffering.class, objectId, i18nInsertionObject, session);
        }
    }
    
    @Override
    protected void addSpecificValuesToObject(AbstractFeatureI18N abstractI18N, I18NInsertionObject insertionObject) {
        // currently no specific values supported
    }

    @Override
    protected I18NOffering getI18NImpl() {
        return new I18NOffering();
    }
    
    @Override
    public AbstractFeatureI18N getObject(String objectIdentifier, String codespace, Session session) {
        Offering offering = new OfferingDAO().getOfferingForIdentifier(objectIdentifier, session);
        if (offering != null) {
            return getObject(offering, codespace, session);
        }
        return null;
    }

    @Override
    public List<AbstractFeatureI18N> getObjectsForIdentifier(String objectIdentifier, Session session) {
        Offering offering = new OfferingDAO().getOfferingForIdentifier(objectIdentifier, session);
        if (offering != null) {
            return getObjects(offering, session);
        }
        return null;
    }
    
    @Override
    public List<Codespace> getCodespace(Session session) {
        return getAbstractI18NCodespace(I18NOffering.class, session);
    }

    @Override
    public Set<String> getCodespaceAsString(Session session) {
        return getAbstractI18NCodespaceString(I18NOffering.class, session);
    }

}

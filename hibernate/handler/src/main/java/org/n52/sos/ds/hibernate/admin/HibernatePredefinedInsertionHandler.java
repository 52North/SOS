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
package org.n52.sos.ds.hibernate.admin;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.series.db.beans.i18n.I18nPhenomenonEntity;
import org.n52.series.db.beans.i18n.I18nUnitEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.PredefinedInsertionHandler;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.TransactionHelper;
import org.n52.sos.predefined.Phenomenon;
import org.n52.sos.predefined.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernatePredefinedInsertionHandler
        implements PredefinedInsertionHandler, Constructable, TransactionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePredefinedInsertionHandler.class);

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    @Override
    public void init() {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public boolean insertPredefinedObservableProperties(Collection<Phenomenon> observableProperties)
            throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = getTransaction(session);
            for (Phenomenon phenomenon : observableProperties) {
                getDaoFactory().getObservablePropertyDAO().getOrInsertObservableProperty(convert(phenomenon), session);
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException pe) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error while insertin predefined observableProperties!", pe);
            checkExceptionAndThrow(pe);
            return false;
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        return true;
    }

    @Override
    public boolean insertPredefinedUnits(Collection<Unit> untis) throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = getTransaction(session);
            for (Unit unit : untis) {
                getDaoFactory().getUnitDAO().getOrInsertUnit(convert(unit), session);
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException pe) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error while inserting predefined units!", pe);
            checkExceptionAndThrow(pe);
            return false;
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        return true;
    }

    private void checkExceptionAndThrow(HibernateException pe) throws CodedException {
        if (pe instanceof SQLGrammarException && ((SQLGrammarException) pe).getSQLState().equals("42501")) {
            throw new NoApplicableCodeException()
                    .withMessage("The user does not have the privileges to write data into the database!");
        }
    }

    private PhenomenonEntity convert(Phenomenon phenomenon) {
        PhenomenonEntity entity = new PhenomenonEntity();
        entity.setIdentifier(phenomenon.getIdentifier(), daoFactory.isStaSupportsUrls());
        entity.setName(phenomenon.getName());
        entity.setDescription(phenomenon.getDescription());
        if (phenomenon.hasTranslations()) {
            Set<I18nEntity<? extends Describable>> trans = new LinkedHashSet<>();
            phenomenon.getTranslations().forEach(i -> {
                I18nPhenomenonEntity i18n = new I18nPhenomenonEntity();
                i18n.setLocale(i.getLocale());
                i18n.setName(i.getName());
                i18n.setDescription(i.getDescription());
                trans.add(i18n);
            });
            entity.setTranslations(trans);
        }
        return entity;
    }

    private UnitEntity convert(Unit unit) {
        UnitEntity entity = new UnitEntity();
        entity.setSymbol(unit.getSymbol(), getDaoFactory().isStaSupportsUrls());
        entity.setName(unit.getName());
        entity.setLink(unit.getLink());
        if (unit.hasTranslations()) {
            Set<I18nEntity<? extends Describable>> trans = new LinkedHashSet<>();
            unit.getTranslations().forEach(i -> {
                I18nUnitEntity i18n = new I18nUnitEntity();
                i18n.setLocale(i.getLocale());
                i18n.setName(i.getName());
                i18n.setDescription(i.getDescription());
                trans.add(i18n);
            });
            entity.setTranslations(trans);
        }
        return entity;
    }

    private synchronized DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

}

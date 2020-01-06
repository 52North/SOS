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
package org.n52.sos.ds.hibernate.create;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.util.GeometryHandler;

public class FeatureVisitorContext {
    private Session session;

    private int storageEPSG;

    private int storage3DEPSG;

    private GeometryHandler geometryHandler;

    private DaoFactory daoFactory;

    private Locale defaultLanguage;

    private String version;

    private Locale requestedLanguage;

    private boolean showAllLanguages;

    private boolean createFeatureGeometryFromSamplingGeometries;

    private boolean updateFeatureGeometry;

    private I18NDAORepository i18NDAORepository;

    private SosContentCache cache;

    private String serviceURL;

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * @param session
     *            the session to set
     */
    public FeatureVisitorContext setSession(Session session) {
        this.session = session;
        return this;
    }

    /**
     * @return the storageEPSG
     */
    public int getStorageEPSG() {
        return storageEPSG;
    }

    /**
     * @param storageEPSG
     *            the storageEPSG to set
     */
    public FeatureVisitorContext setStorageEPSG(int storageEPSG) {
        this.storageEPSG = storageEPSG;
        return this;
    }

    /**
     * @return the storage3DEPSG
     */
    public int getStorage3DEPSG() {
        return storage3DEPSG;
    }

    /**
     * @param storage3depsg
     *            the storage3DEPSG to set
     */
    public FeatureVisitorContext setStorage3DEPSG(int storage3depsg) {
        storage3DEPSG = storage3depsg;
        return this;
    }

    /**
     * @return the geometryHandler
     */
    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    /**
     * @param geometryHandler
     *            the geometryHandler to set
     */
    public FeatureVisitorContext setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
        return this;
    }

    /**
     * @return the daoFactory
     */
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * @param daoFactory
     *            the daoFactory to set
     */
    public FeatureVisitorContext setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
        return this;
    }

    /**
     * @return the defaultLanguage
     */
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * @param defaultLanguage
     *            the defaultLanguage to set
     */
    public FeatureVisitorContext setDefaultLanguage(Locale defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public FeatureVisitorContext setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * @return the requestedLanguage
     */
    public Locale getRequestedLanguage() {
        return requestedLanguage;
    }

    /**
     * @param requestedLanguage
     *            the requestedLanguage to set
     * @return
     */
    public FeatureVisitorContext setRequestedLanguage(Locale requestedLanguage) {
        this.requestedLanguage = requestedLanguage;
        return this;
    }

    /**
     * @return the showAllLanguages
     */
    public boolean isShowAllLanguages() {
        return showAllLanguages;
    }

    /**
     * @param showAllLanguages
     *            the showAllLanguages to set
     */
    public FeatureVisitorContext setShowAllLanguages(boolean showAllLanguages) {
        this.showAllLanguages = showAllLanguages;
        return this;
    }

    public FeatureVisitorContext setCreateFeatureGeometryFromSamplingGeometries(
            boolean createFeatureGeometryFromSamplingGeometries) {
        this.createFeatureGeometryFromSamplingGeometries = createFeatureGeometryFromSamplingGeometries;
        return this;
    }

    public boolean createFeatureGeometryFromSamplingGeometries() {
        return createFeatureGeometryFromSamplingGeometries && !updateFeatureGeometry;
    }

    public boolean isUpdateFeatureGeometry() {
        return updateFeatureGeometry;
    }

    public FeatureVisitorContext setUpdateFeatureGeometry(boolean updateFeatureGeometry) {
        this.updateFeatureGeometry = updateFeatureGeometry;
        return this;
    }

    /**
     * @return the i18NDAORepository
     */
    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

    /**
     * @param i18ndaoRepository
     *            the i18NDAORepository to set
     */
    public FeatureVisitorContext setI18NDAORepository(I18NDAORepository i18ndaoRepository) {
        i18NDAORepository = i18ndaoRepository;
        return this;
    }

    /**
     * @param cache
     *            the cache to set
     */
    public FeatureVisitorContext setCache(SosContentCache cache) {
        this.cache = cache;
        return this;
    }

    /**
     * @return the cache
     */
    public SosContentCache getCache() {
        return cache;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public FeatureVisitorContext setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
        return this;
    }

}

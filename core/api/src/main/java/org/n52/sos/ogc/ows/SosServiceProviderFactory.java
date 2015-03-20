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
package org.n52.sos.ogc.ows;

import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.ADDRESS;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.CITY;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.COUNTRY;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.EMAIL;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.FILE;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.INDIVIDUAL_NAME;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.NAME;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.PHONE;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.POSITION_NAME;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.POSTAL_CODE;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.SITE;
import static org.n52.sos.ogc.ows.SosServiceProviderFactorySettings.STATE;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.LazyThreadSafeProducer;
import org.n52.sos.util.XmlHelper;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
@Configurable
public class SosServiceProviderFactory extends LazyThreadSafeProducer<SosServiceProvider> {

    private File file;
    private String name;
    private URI site;
    private String individualName;
    private String positionName;
    private String phone;
    private String deliveryPoint;
    private String city;
    private String postalCode;
    private String country;
    private String mailAddress;
    private String administrativeArea;

    public SosServiceProviderFactory() throws ConfigurationException {
        SettingsManager.getInstance().configure(this);
    }

    @Setting(FILE)
    public void setFile(File file) {
        this.file = file;
        setRecreate();
    }

    @Setting(NAME)
    public void setName(String name) throws ConfigurationException {
        this.name = name;
        setRecreate();
    }

    @Setting(SITE)
    public void setSite(URI site) {
        this.site = site;
        setRecreate();
    }

    @Setting(INDIVIDUAL_NAME)
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
        setRecreate();
    }

    @Setting(POSITION_NAME)
    public void setPositionName(String positionName) {
        this.positionName = positionName;
        setRecreate();
    }

    @Setting(PHONE)
    public void setPhone(String phone) {
        this.phone = phone;
        setRecreate();
    }

    @Setting(ADDRESS)
    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
        setRecreate();
    }

    @Setting(CITY)
    public void setCity(String city) {
        this.city = city;
        setRecreate();
    }

    @Setting(POSTAL_CODE)
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setRecreate();
    }

    @Setting(COUNTRY)
    public void setCountry(String country) {
        this.country = country;
        setRecreate();
    }

    @Setting(EMAIL)
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
        setRecreate();
    }

    @Setting(STATE)
    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
        setRecreate();
    }

    @Override
    protected SosServiceProvider create(Locale language) throws ConfigurationException {
        SosServiceProvider serviceProvider = new SosServiceProvider();
        if (this.file != null) {
            try {
                serviceProvider.setServiceProvider(XmlHelper.loadXmlDocumentFromFile(this.file));
            } catch (OwsExceptionReport ex) {
                throw new ConfigurationException(ex);
            }
        } else {
            serviceProvider.setAdministrativeArea(this.administrativeArea);
            serviceProvider.setCity(this.city);
            serviceProvider.setCountry(this.country);
            serviceProvider.setDeliveryPoint(this.deliveryPoint);
            serviceProvider.setIndividualName(this.individualName);
            serviceProvider.setMailAddress(this.mailAddress);
            serviceProvider.setName(this.name);
            serviceProvider.setPhone(this.phone);
            serviceProvider.setPositionName(this.positionName);
            serviceProvider.setPostalCode(this.postalCode);
            serviceProvider.setSite(this.site == null ? null : this.site.toString());
        }
        return serviceProvider;
    }
}

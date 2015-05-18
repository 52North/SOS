/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;

import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.ADDRESS;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.CITY;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.COUNTRY;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.EMAIL;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.FILE;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.INDIVIDUAL_NAME;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.NAME;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.PHONE;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.POSITION_NAME;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.POSTAL_CODE;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.SITE;
import static org.n52.iceland.ogc.ows.ServiceProviderFactorySettings.STATE;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.LazyThreadSafeProducer;
import org.n52.iceland.util.XmlHelper;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
@Configurable
public class ServiceProviderFactory extends LazyThreadSafeProducer<OwsServiceProvider> {

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

    public ServiceProviderFactory() throws ConfigurationException {
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
    protected OwsServiceProvider create(Locale language) throws ConfigurationException {
        OwsServiceProvider serviceProvider = new OwsServiceProvider();
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

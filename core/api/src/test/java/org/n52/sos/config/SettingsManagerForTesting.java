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
package org.n52.sos.config;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.binding.BindingKey;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.sos.ogc.ows.StaticCapabilities;
import org.n52.sos.ogc.ows.StringBasedCapabilitiesExtension;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.request.operator.RequestOperatorKey;

/**
 * @since 4.0.0
 *
 */
public class SettingsManagerForTesting extends AbstractSettingsManager {

    public static final SettingValueFactory SETTING_FACTORY = new SettingFactoryForTesting();

    @Override
    protected Set<SettingValue<?>> getSettingValues() throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected SettingValue<?> getSettingValue(String key) throws ConnectionProviderException {
        return null;
    }

    @Override
    protected void deleteSettingValue(String key) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveSettingValue(SettingValue<?> setting) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setOperationStatus(RequestOperatorKey requestOperatorKeyType, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setResponseFormatStatus(ResponseFormatKey rfkt, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }


    @Override
    @Deprecated
    protected void setProcedureDescriptionFormatStatus(String pdf, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setBindingStatus(BindingKey bk, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setOfferingExtensionStatus(OfferingExtensionKey oek, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setOwsExtendedCapabilitiesStatus(OwsExtendedCapabilitiesKey oeck, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public SettingValueFactory getSettingFactory() {
        return SETTING_FACTORY;
    }

    @Override
    public Set<AdministratorUser> getAdminUsers() throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AdministratorUser getAdminUser(String username) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AdministratorUser createAdminUser(String username, String password) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveAdminUser(AdministratorUser user) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAdminUser(String username) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isActive(RequestOperatorKey rokt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(ResponseFormatKey rfkt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(BindingKey bk) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(OfferingExtensionKey oek) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(OwsExtendedCapabilitiesKey oeck) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    private static class SettingFactoryForTesting extends AbstractSettingValueFactory {

        @Override
        protected SettingValue<Boolean> newBooleanSettingValue() {
            return new BooleanSettingValueForTesting();
        }

        @Override
        protected SettingValue<Integer> newIntegerSettingValue() {
            return new IntegerSettingValueForTesting();
        }

        @Override
        protected SettingValue<String> newStringSettingValue() {
            return new StringSettingValueForTesting();
        }

        @Override
        protected SettingValue<File> newFileSettingValue() {
            return new FileSettingValueForTesting();
        }

        @Override
        protected SettingValue<URI> newUriSettingValue() {
            return new UriSettingValueForTesting();
        }

        @Override
        protected SettingValue<Double> newNumericSettingValue() {
            return new NumericSettingValueForTesting();
        }

        @Override
        protected SettingValue<TimeInstant> newTimeInstantSettingValue() {
            return new TimeInstantSettingValueForTesting();
        }

        @Override
        protected SettingValue<MultilingualString> newMultiLingualStringSettingValue() {
            return new MultilingualStringValueForTestin();
        }

        @Override
        protected SettingValue<String> newChoiceSettingValue() {
            return new ChoiceSettingValueForTesting();
        }
    }

    @Override
    protected void setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey pdfkt, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isActive(ProcedureDescriptionFormatKey pdfkt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public Map<String, List<OfferingExtension>> getOfferingExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<OfferingExtension>> getActiveOfferingExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOfferingExtension(String offering, String identifier,
			String value) throws NoSuchOfferingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableOfferingExtension(String offering, String identifier,
			boolean disabled) throws NoSuchExtensionException,
			NoSuchOfferingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteOfferingExtension(String offering, String identifier)
			throws NoSuchOfferingException, NoSuchExtensionException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCapabilitiesExtension(String identifier, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableCapabilitiesExtension(String identifier, boolean disabled)
			throws NoSuchExtensionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteCapabiltiesExtension(String identfier)
			throws NoSuchExtensionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActiveStaticCapabilities(String identifier)
			throws NoSuchExtensionException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getActiveStaticCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActiveStaticCapabilitiesDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStaticCapabilitiesActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, StaticCapabilities> getStaticCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticCapabilities getStaticCapabilities(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveStaticCapabilities(String identifier, String document) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStaticCapabilities(String identifier)
			throws NoSuchExtensionException {
		// TODO Auto-generated method stub

	}

}

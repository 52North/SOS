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
package org.n52.iceland.config;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.binding.BindingKey;
import org.n52.iceland.config.AbstractSettingValueFactory;
import org.n52.iceland.config.AbstractSettingsManager;
import org.n52.iceland.config.AdministratorUser;
import org.n52.iceland.config.SettingValue;
import org.n52.iceland.config.SettingValueFactory;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.encode.ProcedureDescriptionFormatKey;
import org.n52.iceland.encode.ResponseFormatKey;
import org.n52.iceland.exception.NoSuchExtensionException;
import org.n52.iceland.exception.NoSuchOfferingException;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.ows.OfferingExtension;
import org.n52.iceland.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.iceland.ogc.ows.StaticCapabilities;
import org.n52.iceland.ogc.ows.StringBasedCapabilitiesExtension;
import org.n52.iceland.ogc.swes.OfferingExtensionKey;
import org.n52.iceland.request.operator.RequestOperatorKey;

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

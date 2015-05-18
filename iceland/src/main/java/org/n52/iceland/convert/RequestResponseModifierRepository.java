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
package org.n52.iceland.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;

@SuppressWarnings("rawtypes")
public class RequestResponseModifierRepository extends
		AbstractConfiguringServiceLoaderRepository<RequestResponseModifier> {

	private static RequestResponseModifierRepository instance;

	private final Map<RequestResponseModifierKeyType, List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>> requestResponseModifier = new HashMap<RequestResponseModifierKeyType, List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>>(
			0);

	public static RequestResponseModifierRepository getInstance() {
		if (instance == null) {
			instance = new RequestResponseModifierRepository();
		}
		return instance;
	}

	public RequestResponseModifierRepository() {
		super(RequestResponseModifier.class, false);
		load(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processConfiguredImplementations(
			Set<RequestResponseModifier> requestResponseModifier)
			throws ConfigurationException {
		this.requestResponseModifier.clear();
		for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> aModifier : requestResponseModifier) {
			for (RequestResponseModifierKeyType modifierKeyType : aModifier
					.getRequestResponseModifierKeyTypes()) {
				List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> list = this.requestResponseModifier
						.get(modifierKeyType);
				if (list == null) {
					list = new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
					this.requestResponseModifier.put(modifierKeyType, list);
				}
				list.add(aModifier);
			}
		}
	}

	public List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> getRequestResponseModifier(
			AbstractServiceRequest request) {
		return getRequestResponseModifier(new RequestResponseModifierKeyType(
				request.getService(), request.getVersion(), request));
	}

	public List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> getRequestResponseModifier(
			AbstractServiceRequest request, AbstractServiceResponse response) {
		return getRequestResponseModifier(new RequestResponseModifierKeyType(
				response.getService(), response.getVersion(), request, response));
	}

	public <T, F> List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> getRequestResponseModifier(
			RequestResponseModifierKeyType key) {
		return requestResponseModifier.get(key);
	}

	public boolean hasRequestResponseModifier(AbstractServiceRequest request) {
		return hasRequestResponseModifier(new RequestResponseModifierKeyType(
				request.getService(), request.getVersion(), request));
	}

	public boolean hasRequestResponseModifier(AbstractServiceRequest request,
			AbstractServiceResponse response) {
		return hasRequestResponseModifier(new RequestResponseModifierKeyType(
				request.getService(), request.getVersion(), request, response))
				&& hasRequestResponseModifier(new RequestResponseModifierKeyType(
						response.getService(), response.getVersion(), request,
						response));
	}

	public boolean hasRequestResponseModifier(RequestResponseModifierKeyType key) {
		return requestResponseModifier.containsKey(key);
	}

}

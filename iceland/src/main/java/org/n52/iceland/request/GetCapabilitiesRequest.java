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
package org.n52.iceland.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.exception.ows.VersionNegotiationFailedException;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.response.GetCapabilitiesResponse;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.StringHelper;

/**
 * SOS GetCapabilities request
 * 
 * @since 4.0.0
 */
public class GetCapabilitiesRequest extends AbstractServiceRequest<GetCapabilitiesResponse> {
    private String updateSequence;

    private final List<String> acceptVersions = new LinkedList<String>();

    private final List<String> sections = new LinkedList<String>();

    private final List<String> acceptFormats = new LinkedList<String>();

    private List<ServiceOperatorKey> serviceOperatorKeyTypes;

    /**
     * Extensions list
     * 
     * FIXME change to a model object to remove dependency to XmlBeans in API
     * package
     */
    private List<XmlObject> extensionArray = new LinkedList<XmlObject>();

    private String capabilitiesId;

    public GetCapabilitiesRequest(String service) {
        setService(service);
    }

    @Override
    public String getOperationName() {
        return OWSConstants.Operations.GetCapabilities.name();
    }

    @Override
    public List<ServiceOperatorKey> getServiceOperatorKeyType() {
        if (serviceOperatorKeyTypes == null) {
            if (isSetAcceptVersions()) {
                serviceOperatorKeyTypes = new ArrayList<ServiceOperatorKey>(acceptVersions.size());
                for (String acceptVersion : acceptVersions) {
                    serviceOperatorKeyTypes.add(new ServiceOperatorKey(getService(), acceptVersion));
                }
            } else {
                Set<String> supportedVersions =
                        ServiceOperatorRepository.getInstance().getSupportedVersions(getService());
                if (CollectionHelper.isNotEmpty(supportedVersions)) {
                    setVersion(Collections.max(supportedVersions));
                }
                serviceOperatorKeyTypes =
                        Collections.singletonList(new ServiceOperatorKey(getService(), getVersion()));
            }
        }
        return Collections.unmodifiableList(serviceOperatorKeyTypes);
    }

    /**
     * Get accept Formats
     * 
     * @return accept Formats
     */
    public List<String> getAcceptFormats() {
        return acceptFormats == null ? null : Collections.unmodifiableList(acceptFormats);
    }

    /**
     * Set accept Formats
     * 
     * @param acceptFormats
     *            accept Formats
     */
    public void setAcceptFormats(List<String> acceptFormats) {
        this.acceptFormats.clear();
        if (acceptFormats != null) {
            this.acceptFormats.addAll(acceptFormats);
        }
    }

    /**
     * Get accept versions
     * 
     * @return accept versions
     */
    public List<String> getAcceptVersions() {
        return acceptVersions == null ? null : Collections.unmodifiableList(acceptVersions);
    }

    public void addAcceptVersion(String acceptVersion) {
        if (acceptVersion != null) {
            acceptVersions.add(acceptVersion);
        }
    }

    public void setAcceptVersions(List<String> acceptVersions) {
        this.acceptVersions.clear();
        if (acceptVersions != null) {
            this.acceptVersions.addAll(acceptVersions);
        }
    }

    /**
     * Get sections
     * 
     * @return sections
     */
    public List<String> getSections() {
        return sections == null ? null : Collections.unmodifiableList(sections);
    }

    /**
     * Set sections
     * 
     * @param sections
     *            sections
     */
    public void setSections(List<String> sections) {
        this.sections.clear();
        if (sections != null) {
            this.sections.addAll(sections);
        }
    }

    /**
     * Get update sequence
     * 
     * @return update sequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Set update sequence
     * 
     * @param updateSequence
     *            update sequence
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * Set extensions
     * 
     * @param extensionArray
     *            extensions
     */
    public void setExtensionArray(List<XmlObject> extensionArray) {
        this.extensionArray = extensionArray;
    }

    public String getCapabilitiesId() {
        return this.capabilitiesId;
    }

    public void setCapabilitiesId(String id) {
        this.capabilitiesId = id;
    }

    public boolean isCapabilitiesIdSet() {
        return getCapabilitiesId() != null && !getCapabilitiesId().isEmpty();
    }

    /**
     * Get extensions
     * 
     * @return extensions
     */
    public List<XmlObject> getExtensionArray() {
        return extensionArray;
    }

    public boolean isSetAcceptFormats() {
        return CollectionHelper.isNotEmpty(getAcceptFormats());
    }

    public boolean isSetAcceptVersions() {
        return CollectionHelper.isNotEmpty(getAcceptVersions());
    }

    public boolean isSetSections() {
        return CollectionHelper.isNotEmpty(getSections());
    }

    public boolean isSetUpdateSequence() {
        return StringHelper.isNotEmpty(getUpdateSequence());
    }

    @Override
    public GetCapabilitiesResponse getResponse() throws OwsExceptionReport {
        return (GetCapabilitiesResponse) new GetCapabilitiesResponse().set(this).setVersion(getVersionParameter());
    }

    /**
     * Get the response version from request, from set version, from
     * acceptVersions or from supported versions
     * 
     * @return the response version
     * @throws OwsExceptionReport
     *             If the requested version is not supported
     */
    private String getVersionParameter() throws OwsExceptionReport {
        if (!isSetVersion()) {
            if (isSetAcceptVersions()) {
                for (final String acceptedVersion : getAcceptVersions()) {
                    if (ServiceOperatorRepository.getInstance().isVersionSupported(getService(), acceptedVersion)) {
                        setVersion(acceptedVersion);
                        return acceptedVersion;
                    }
                }
            } else {
                for (final String supportedVersion : ServiceOperatorRepository.getInstance().getSupportedVersions(
                        getService())) {
                    setVersion(supportedVersion);
                    return supportedVersion;
                }
            }
        } else {
            return getVersion();
        }

        throw new VersionNegotiationFailedException().withMessage(
                "The requested '%s' values are not supported by this service!",
                OWSConstants.GetCapabilitiesParams.AcceptVersions);
    }

}

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

import java.util.List;
import java.util.Map;

import org.n52.iceland.exception.NoSuchExtensionException;
import org.n52.iceland.exception.NoSuchOfferingException;
import org.n52.iceland.ogc.ows.OfferingExtension;
import org.n52.iceland.ogc.ows.StaticCapabilities;
import org.n52.iceland.ogc.ows.StringBasedCapabilitiesExtension;

public interface CapabilitiesExtensionManager {

    Map<String, List<OfferingExtension>> getOfferingExtensions();
    Map<String, List<OfferingExtension>> getActiveOfferingExtensions();
    void saveOfferingExtension(String offering, String identifier, String value) throws NoSuchOfferingException;
    void disableOfferingExtension(String offering, String identifier, boolean disabled) throws NoSuchExtensionException,
                                                                                               NoSuchOfferingException;

    void deleteOfferingExtension(String offering, String identifier) throws NoSuchOfferingException,
                                                                            NoSuchExtensionException;
    Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions();
    Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions();
    void saveCapabilitiesExtension(String identifier, String value);
    void disableCapabilitiesExtension(String identifier, boolean disabled) throws NoSuchExtensionException;
    void deleteCapabiltiesExtension(String identfier) throws NoSuchExtensionException;
    void setActiveStaticCapabilities(String identifier) throws NoSuchExtensionException;

    /**
     * @return the identifier
     */
    String getActiveStaticCapabilities();

    /**
     * @return the document
     */
    String getActiveStaticCapabilitiesDocument();
    boolean isStaticCapabilitiesActive();
    Map<String, StaticCapabilities> getStaticCapabilities();
    StaticCapabilities getStaticCapabilities(String id);
    void saveStaticCapabilities(String identifier, String document);
    void deleteStaticCapabilities(String identifier) throws NoSuchExtensionException;
}

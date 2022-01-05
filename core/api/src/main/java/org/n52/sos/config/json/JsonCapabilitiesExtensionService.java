/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.json;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.janmayen.function.Consumers;
import org.n52.janmayen.function.Functions;
import org.n52.faroe.json.AbstractJsonDao;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.config.json.JsonConstants;
import org.n52.iceland.ogc.ows.extension.StaticCapabilities;
import org.n52.shetland.ogc.ows.extension.AbstractExtension;
import org.n52.shetland.ogc.ows.extension.DisableableExtension;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.StringBasedCapabilitiesExtension;
import org.n52.shetland.ogc.ows.extension.StringBasedExtension;
import org.n52.shetland.ogc.sos.extension.SosObservationOfferingExtension;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class JsonCapabilitiesExtensionService extends AbstractJsonDao implements CapabilitiesExtensionService {

    private static final String IDENTIFIER = "identifier";

    private ContentCacheController contentCacheController;

    @Inject
    public void setContentCacheController(ContentCacheController ctrl) {
        this.contentCacheController = ctrl;
    }

    @Override
    public Map<String, List<SosObservationOfferingExtension>> getOfferingExtensions() {
        readLock().lock();
        try {
            return offeringExtensionStream()
                    .collect(groupingBy(SosObservationOfferingExtension::getOfferingName, toList()));
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public Map<String, List<SosObservationOfferingExtension>> getActiveOfferingExtensions() {
        readLock().lock();
        try {
            return offeringExtensionStream()
                    .filter(ce -> !ce.isDisabled())
                    .collect(groupingBy(SosObservationOfferingExtension::getOfferingName, toList()));
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public void saveOfferingExtension(String offering, String identifier, String value)
            throws NoSuchOfferingException {
        writeLock().lock();
        try {
            checkOfferingName(offering);
            getConfiguration()
                    .with(JsonConstants.OFFERING_EXTENSIONS)
                    .with(offering)
                    .with(identifier)
                    .put(JsonConstants.EXTENSION, value);
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    protected void checkOfferingName(String offering)
            throws NoSuchOfferingException {
        if (!getCache().hasOffering(offering)) {
            throw new NoSuchOfferingException(offering);
        }
    }

    private SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

    @Override
    public void disableOfferingExtension(String offering, String identifier, boolean disabled)
            throws NoSuchExtensionException, NoSuchOfferingException {
        writeLock().lock();
        try {
            checkOfferingName(offering);
            ObjectNode extensions = getConfiguration()
                    .with(JsonConstants.OFFERING_EXTENSIONS)
                    .with(offering);

            if (!extensions.has(identifier)) {
                throw new NoSuchExtensionException(identifier);
            }

            extensions.with(identifier).put(JsonConstants.DISABLED, disabled);
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void deleteOfferingExtension(String offering, String identifier)
            throws NoSuchOfferingException, NoSuchExtensionException {
        writeLock().lock();
        try {
            checkOfferingName(offering);
            ObjectNode extensions = getConfiguration()
                    .with(JsonConstants.OFFERING_EXTENSIONS)
                    .with(offering);
            JsonNode remove = extensions.remove(identifier);
            if (remove == null) {
                throw new NoSuchExtensionException(identifier);
            }
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions() {
        readLock().lock();
        try {
            return capabilitiesExtensionStream().filter(ce -> !ce.isDisabled())
                    .collect(swesExtensionCollector());
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions() {
        readLock().lock();
        try {
            return capabilitiesExtensionStream().collect(swesExtensionCollector());
        } finally {
            readLock().unlock();
        }
    }

    private Stream<SosObservationOfferingExtension> offeringExtensionStream() {
        return createEntryStream(getConfiguration().with(JsonConstants.OFFERING_EXTENSIONS))
                .flatMap(entry -> createEntryStream(entry.getValue())
                        .map(this::decodeOfferingExtension)
                        .map(Functions.mutate(Consumers
                                .currySecond(SosObservationOfferingExtensionImpl::setOfferingName, entry.getKey()))));
    }

    private SosObservationOfferingExtensionImpl decodeOfferingExtension(Entry<String, JsonNode> entry) {
        String identifier = entry.getKey();
        JsonNode n = entry.getValue();
        SosObservationOfferingExtensionImpl oe = new SosObservationOfferingExtensionImpl();
        oe.setIdentifier(identifier);
        oe.setDefinition(n.path(JsonConstants.DEFINITION).textValue());
        oe.setDisabled(n.path(JsonConstants.DISABLED).booleanValue());
        oe.setExtension(n.path(JsonConstants.EXTENSION).textValue());
        oe.setNamespace(n.path(JsonConstants.NAMESPACE).textValue());
        return oe;
    }

    private Stream<StringBasedCapabilitiesExtension> capabilitiesExtensionStream() {
        return createEntryStream(getConfiguration().with(JsonConstants.CAPABILITIES_EXTENSIONS))
                .map(this::decodeCapabilitiesExtension);
    }

    private CapabilitiesExtensionImpl decodeCapabilitiesExtension(Entry<String, JsonNode> e) {
        CapabilitiesExtensionImpl ce = new CapabilitiesExtensionImpl();
        JsonNode n = e.getValue();
        ce.setIdentifier(e.getKey());
        ce.setDefinition(n.path(JsonConstants.DEFINITION).textValue());
        ce.setDisabled(n.path(JsonConstants.DISABLED).booleanValue());
        ce.setExtension(n.path(JsonConstants.EXTENSION).textValue());
        ce.setNamespace(n.path(JsonConstants.NAMESPACE).textValue());
        return ce;
    }

    @Override
    public void saveCapabilitiesExtension(String identifier, String value) {
        writeLock().lock();
        try {
            getConfiguration()
                    .with(JsonConstants.CAPABILITIES_EXTENSIONS)
                    .with(identifier)
                    .put(JsonConstants.EXTENSION, value);
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void disableCapabilitiesExtension(String identifier, boolean disabled) throws NoSuchExtensionException {
        writeLock().lock();
        try {
            ObjectNode node = getConfiguration().with(JsonConstants.CAPABILITIES_EXTENSIONS);
            if (!node.has(identifier)) {
                throw new NoSuchExtensionException(identifier);
            } else {
                node.with(identifier).put(JsonConstants.DISABLED, disabled);
            }
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void deleteCapabiltiesExtension(String identfier) throws NoSuchExtensionException {
        writeLock().lock();
        try {
            JsonNode removed = getConfiguration()
                    .with(JsonConstants.CAPABILITIES_EXTENSIONS)
                    .remove(identfier);

            if (removed == null) {
                throw new NoSuchExtensionException(identfier);
            }

        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void setActiveStaticCapabilities(String identifier)
            throws NoSuchExtensionException {
        writeLock().lock();
        try {
            if (identifier != null && !identifier.isEmpty()) {
                JsonNode node
                        = getStaticCapabilitiesJsonNode()
                                .path(identifier);
                if (node.isMissingNode() || node.isNull()) {
                    throw new NoSuchExtensionException(identifier);
                }

                getConfiguration().with(JsonConstants.STATIC_CAPABILITIES)
                        .put(JsonConstants.ACTIVE, identifier);
            } else {
                getConfiguration().with(JsonConstants.STATIC_CAPABILITIES).remove(JsonConstants.ACTIVE);
            }
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public String getActiveStaticCapabilities() {
        readLock().lock();
        try {
            return getConfiguration().path(JsonConstants.STATIC_CAPABILITIES)
                    .path(JsonConstants.ACTIVE).textValue();
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public String getActiveStaticCapabilitiesDocument() {
        readLock().lock();
        try {
            String id = getActiveStaticCapabilities();
            return id == null ? null : getStaticCapabilitiesJsonNode()
                    .path(id)
                    .textValue();
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public boolean isStaticCapabilitiesActive() {
        readLock().lock();
        try {
            return getConfiguration().path(JsonConstants.STATIC_CAPABILITIES)
                    .path(JsonConstants.ACTIVE).isTextual();
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public Map<String, StaticCapabilities> getStaticCapabilities() {
        readLock().lock();
        try {
            JsonNode node =
                    getStaticCapabilitiesJsonNode();
            return createEntryStream(node).collect(
                    toMap(Entry::getKey, e -> new StaticCapabilitiesImpl(e.getKey(), e.getValue().textValue())));
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public StaticCapabilities getStaticCapabilities(String id) {
        readLock().lock();
        try {
            String value = getStaticCapabilitiesJsonNode()
                    .path(id)
                    .textValue();
            return value == null ? null : new StaticCapabilitiesImpl(id, value);
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public void saveStaticCapabilities(String identifier, String document) {
        writeLock().lock();
        try {
            getStaticCapabilitiesObjectNode()
                .put(identifier, document);
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void deleteStaticCapabilities(String identifier) throws NoSuchExtensionException {
        writeLock().lock();
        try {
            JsonNode removed = getStaticCapabilitiesObjectNode()
                    .remove(identifier);
            if (removed == null) {
                throw new NoSuchExtensionException(identifier);
            }
        } finally {
            writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    private ObjectNode getStaticCapabilitiesObjectNode() {
        return  getConfiguration()
                .with(JsonConstants.STATIC_CAPABILITIES)
                .with(JsonConstants.CAPABILITIES);
    }

    private JsonNode getStaticCapabilitiesJsonNode() {
        return  getConfiguration()
                .path(JsonConstants.STATIC_CAPABILITIES)
                .path(JsonConstants.CAPABILITIES);
    }

    private static <T extends Extension<?>> Collector<T, ?, Map<String, T>> swesExtensionCollector() {
        return toMap(Extension::getIdentifier, Function.identity());
    }

    private static class StaticCapabilitiesImpl implements StaticCapabilities {
        private String document;
        private String identifier;

        StaticCapabilitiesImpl(String identifier, String document) {
            setIdentifier(identifier);
            setDocument(document);
        }

        @Override
        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return toStringHelper().toString();
        }

        protected ToStringHelper toStringHelper() {
            return MoreObjects.toStringHelper(this)
                    .add(IDENTIFIER, getIdentifier())
                    .add("document", getDocument());
        }

    }

    private abstract static class AbstractDisableableExtension extends AbstractExtension<String>
            implements DisableableExtension, StringBasedExtension {
        private boolean disabled;
        private String value;

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        public boolean isDisabled() {
            return this.disabled;
        }

        public AbstractDisableableExtension setExtension(String extension) {
            setValue(extension);
            return this;
        }

        @Override
        public String getExtension() {
            return getValue();
        }

        @Override
        public AbstractDisableableExtension setValue(String value) {
            this.value = value;
            return this;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return toStringHelper().toString();
        }

        protected ToStringHelper toStringHelper() {
            return MoreObjects.toStringHelper(this)
                    .add(IDENTIFIER, getIdentifier())
                    .add("namespace", getNamespace())
                    .add("definition", getDefinition())
                    .add("extension", getExtension())
                    .add("disabled", isDisabled());
        }
    }

    private static class SosObservationOfferingExtensionImpl extends AbstractDisableableExtension
            implements SosObservationOfferingExtension {

        private String offeringName;

        public void setOfferingName(String offeringName) {
            this.offeringName = offeringName;
        }

        @Override
        public String getOfferingName() {
            return this.offeringName;
        }

        @Override
        protected ToStringHelper toStringHelper() {
            return super.toStringHelper()
                    .add("offeringName", getOfferingName());
        }
    }

    private static class CapabilitiesExtensionImpl
            extends AbstractDisableableExtension
            implements StringBasedCapabilitiesExtension {

        @Override
        public String getSectionName() {
            return getIdentifier();
        }
    }

}

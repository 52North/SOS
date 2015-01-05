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
package org.n52.sos.coding;

import static org.n52.sos.util.MultiMaps.newSetMultiMap;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ProcedureEncoder;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.Activatable;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.CompositeSimilar;
import org.n52.sos.util.ProxySimilarityComparator;
import org.n52.sos.util.SetMultiMap;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CodingRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CodingRepository.class);

    private static class LazyHolder {
		private static final CodingRepository INSTANCE = new CodingRepository();
		
		private LazyHolder() {};
	}


    @SuppressWarnings("rawtypes")
    private final ServiceLoader<Decoder> serviceLoaderDecoder;

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<Encoder> serviceLoaderEncoder;

    private final Set<Decoder<?, ?>> decoders;

    private final Set<Encoder<?, ?>> encoders;

    private final SetMultiMap<DecoderKey, Decoder<?, ?>> decoderByKey = newSetMultiMap();

    private final SetMultiMap<EncoderKey, Encoder<?, ?>> encoderByKey = newSetMultiMap();

    private SetMultiMap<SupportedTypeKey, Activatable<String>> typeMap = newSetMultiMap(SupportedTypeKey.class);

    private final Set<ObservationEncoder<?, ?>> observationEncoders = Sets.newHashSet();

    private final Map<String, Map<String, Set<String>>> responseFormats = Maps.newHashMap();

    private final Map<ResponseFormatKey, Boolean> responseFormatStatus = Maps.newHashMap();

    private final Map<String, Set<SchemaLocation>> schemaLocations = Maps.newHashMap();

    private final Map<String, Map<String, Set<String>>> procedureDescriptionFormats = Maps.newHashMap();

    private final Map<ProcedureDescriptionFormatKey, Boolean> procedureDescriptionFormatsStatus = Maps.newHashMap();

    /**
     * @return Returns a singleton instance of the CodingRepository.
     */
    public static CodingRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * private constructor for singleton
     */
    private CodingRepository() {
        serviceLoaderDecoder = ServiceLoader.load(Decoder.class);
        serviceLoaderEncoder = ServiceLoader.load(Encoder.class);
        decoders = Sets.newHashSet(loadDecoders());
        encoders = Sets.newHashSet(loadEncoders());
        initDecoderMap();
        initEncoderMap();
        generateTypeMap();
        generateResponseFormatMaps();
        generateProcedureDescriptionFormatMaps();
        generateSchemaLocationMap();
    }

    @SuppressWarnings("unchecked")
    private <T> T unsafeCast(final Object o) {
        return (T) o;
    }

    private <F, T> Decoder<F, T> processDecoderMatches(final Set<Decoder<?, ?>> matches, final DecoderKey key) {
        if (matches == null || matches.isEmpty()) {
            LOG.debug("No Decoder implementation for {}", key);
            return null;
        } else if (matches.size() > 1) {
            final Decoder<?, ?> dec = Collections.min(matches, new DecoderComparator(key));
            LOG.debug("Requested ambiguous Decoder implementations for {}: Found {}; Choosing {}.", key, Joiner
                    .on(", ").join(matches), dec);
            return unsafeCast(dec);
        } else {
            return unsafeCast(matches.iterator().next());
        }
    }

    private <F, T> Encoder<F, T> processEncoderMatches(final Set<Encoder<?, ?>> matches, final EncoderKey key) {
        if (matches == null || matches.isEmpty()) {
            LOG.debug("No Encoder for {}", key);
            return null;
        } else if (matches.size() > 1) {
            final Encoder<?, ?> enc = Collections.min(matches, new EncoderComparator(key));
            LOG.debug("Requested ambiguous Encoder implementations for {}: Found {}; Choosing {}.", key, Joiner
                    .on(", ").join(matches), enc);
            return unsafeCast(enc);
        } else {
            return unsafeCast(matches.iterator().next());
        }
    }

    public void updateDecoders() {
        LOG.debug("Reloading Decoder implementations");
        decoders.clear();
        decoders.addAll(loadDecoders());
        initDecoderMap();
        generateTypeMap();
        LOG.debug("Reloaded Decoder implementations");
    }

    public void updateEncoders() {
        LOG.debug("Reloading Encoder implementations");
        encoders.clear();
        encoders.addAll(loadEncoders());
        initEncoderMap();
        generateTypeMap();
        generateResponseFormatMaps();
        generateProcedureDescriptionFormatMaps();
        generateSchemaLocationMap();
        LOG.debug("Reloaded Encoder implementations");
    }

    private void generateResponseFormatMaps() {
        responseFormatStatus.clear();
        responseFormats.clear();
        final Set<ServiceOperatorKey> serviceOperatorKeyTypes =
                ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes();
        for (final Encoder<?, ?> e : getEncoders()) {
            if (e instanceof ObservationEncoder) {
                final ObservationEncoder<?, ?> oe = (ObservationEncoder<?, ?>) e;
                for (final ServiceOperatorKey sokt : serviceOperatorKeyTypes) {
                    final Set<String> rfs = oe.getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
                    if (rfs != null) {
                        for (final String rf : rfs) {
                            addResponseFormat(new ResponseFormatKey(sokt, rf));
                        }
                    }
                }
            }
        }
    }

    private void generateProcedureDescriptionFormatMaps() {
        procedureDescriptionFormatsStatus.clear();
        procedureDescriptionFormats.clear();
        final Set<ServiceOperatorKey> serviceOperatorKeyTypes =
                ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes();
        for (final Encoder<?, ?> e : getEncoders()) {
            if (e instanceof ProcedureEncoder) {
                final ProcedureEncoder<?, ?> oe = (ProcedureEncoder<?, ?>) e;
                for (final ServiceOperatorKey sokt : serviceOperatorKeyTypes) {
                    final Set<String> rfs = oe.getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
                    if (rfs != null) {
                        for (final String rf : rfs) {
                            addProcedureDescriptionFormat(new ProcedureDescriptionFormatKey(sokt, rf));
                        }
                    }
                }
            }
        }
    }

    private void generateSchemaLocationMap() {
        schemaLocations.clear();
        for (final Encoder<?, ?> encoder : encoders) {
            if (CollectionHelper.isNotEmpty(encoder.getEncoderKeyType())) {
                for (final EncoderKey key : encoder.getEncoderKeyType()) {
                    if (key instanceof XmlEncoderKey && CollectionHelper.isNotEmpty(encoder.getSchemaLocations())) {
                        schemaLocations.put(((XmlEncoderKey) key).getNamespace(), encoder.getSchemaLocations());
                    }
                }

            }
        }
    }

    protected void addResponseFormat(final ResponseFormatKey rfkt) {
        try {
            responseFormatStatus.put(rfkt, SettingsManager.getInstance().isActive(rfkt));
        } catch (final ConnectionProviderException ex) {
            throw new ConfigurationException(ex);
        }
        Map<String, Set<String>> byService = responseFormats.get(rfkt.getService());
        if (byService == null) {
            byService = Maps.newHashMap();
            responseFormats.put(rfkt.getService(), byService);
        }
        Set<String> byVersion = byService.get(rfkt.getVersion());
        if (byVersion == null) {
            byVersion = Sets.newHashSet();
            byService.put(rfkt.getVersion(), byVersion);
        }
        byVersion.add(rfkt.getResponseFormat());
    }

    protected void addProcedureDescriptionFormat(final ProcedureDescriptionFormatKey pdfkt) {
        try {
            procedureDescriptionFormatsStatus.put(pdfkt, SettingsManager.getInstance().isActive(pdfkt));
        } catch (final ConnectionProviderException ex) {
            throw new ConfigurationException(ex);
        }
        Map<String, Set<String>> byService = procedureDescriptionFormats.get(pdfkt.getService());
        if (byService == null) {
            byService = Maps.newHashMap();
            procedureDescriptionFormats.put(pdfkt.getService(), byService);
        }
        Set<String> byVersion = byService.get(pdfkt.getVersion());
        if (byVersion == null) {
            byVersion = Sets.newHashSet();
            byService.put(pdfkt.getVersion(), byVersion);
        }
        byVersion.add(pdfkt.getProcedureDescriptionFormat());
    }

    private List<Decoder<?, ?>> loadDecoders() {
        final List<Decoder<?, ?>> loadedDecoders = new LinkedList<Decoder<?, ?>>();
        try {
            final SettingsManager sm = SettingsManager.getInstance();
            for (final Decoder<?, ?> decoder : serviceLoaderDecoder) {
                sm.configure(decoder);
                loadedDecoders.add(decoder);
            }
        } catch (final ServiceConfigurationError sce) {
            final String text = "An Decoder implementation could not be loaded!";
            LOG.warn(text, sce);
            throw new ConfigurationException(text, sce);
        }
        return loadedDecoders;
    }

    private List<Encoder<?, ?>> loadEncoders() {
        final List<Encoder<?, ?>> loadedEncoders = new LinkedList<Encoder<?, ?>>();
        try {
            final SettingsManager sm = SettingsManager.getInstance();
            for (final Encoder<?, ?> encoder : serviceLoaderEncoder) {
                sm.configure(encoder);
                loadedEncoders.add(encoder);
            }
        } catch (final ServiceConfigurationError sce) {
            final String text = "An Encoder implementation could not be loaded!";
            LOG.warn(text, sce);
            throw new ConfigurationException(text, sce);
        }
        return loadedEncoders;
    }

    public Set<Decoder<?, ?>> getDecoders() {
        return CollectionHelper.unmodifiableSet(decoders);
    }

    public Set<Encoder<?, ?>> getEncoders() {
        return CollectionHelper.unmodifiableSet(encoders);
    }

    public Map<DecoderKey, Set<Decoder<?, ?>>> getDecoderByKey() {
        return CollectionHelper.unmodifiableMap(decoderByKey);
    }

    public Map<EncoderKey, Set<Encoder<?, ?>>> getEncoderByKey() {
        return CollectionHelper.unmodifiableMap(encoderByKey);
    }

    public Set<String> getFeatureOfInterestTypes() {
        return typesFor(SupportedTypeKey.FeatureType);
    }

    public Set<String> getObservationTypes() {
        return typesFor(SupportedTypeKey.ObservationType);
    }

    private Set<String> typesFor(final SupportedTypeKey key) {
        if (typeMap == null || !typeMap.containsKey(key) || typeMap.get(key) == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(Activatable.filter(typeMap.get(key)));
    }

    private void generateTypeMap() {
        final List<Map<SupportedTypeKey, Set<String>>> list = new LinkedList<Map<SupportedTypeKey, Set<String>>>();
        for (final Decoder<?, ?> decoder : getDecoders()) {
            list.add(decoder.getSupportedTypes());
        }
        for (final Encoder<?, ?> encoder : getEncoders()) {
            list.add(encoder.getSupportedTypes());
        }

        final SetMultiMap<SupportedTypeKey, Activatable<String>> resultMap = newSetMultiMap(SupportedTypeKey.class);
        for (final Map<SupportedTypeKey, Set<String>> map : list) {
            if (map != null && !map.isEmpty()) {
                for (final SupportedTypeKey type : map.keySet()) {
                    if (map.get(type) != null && !map.get(type).isEmpty()) {
                        resultMap.addAll(type, Activatable.from(map.get(type)));
                    }
                }
            }
        }

        typeMap = resultMap;
    }

    private void initEncoderMap() {
        encoderByKey.clear();
        for (final Encoder<?, ?> encoder : getEncoders()) {
            for (final EncoderKey key : encoder.getEncoderKeyType()) {
                encoderByKey.add(key, encoder);
            }
            if (encoder instanceof ObservationEncoder) {
                observationEncoders.add((ObservationEncoder<?, ?>) encoder);
            }
        }
    }

    private void initDecoderMap() {
        decoderByKey.clear();
        for (final Decoder<?, ?> decoder : getDecoders()) {
            for (final DecoderKey key : decoder.getDecoderKeyTypes()) {
                decoderByKey.add(key, decoder);
            }
        }
    }

    public boolean hasDecoder(final DecoderKey key, final DecoderKey... keys) {
        return getDecoder(key, keys) != null;
    }

    public <F, T> Decoder<F, T> getDecoder(final DecoderKey key, final DecoderKey... keys) {
        if (keys.length == 0) {
            return getDecoderSingleKey(key);
        } else {
            return getDecoderCompositeKey(new CompositeDecoderKey(ImmutableList.<DecoderKey> builder().add(key)
                    .add(keys).build()));
        }
    }

    public boolean hasEncoder(final EncoderKey key, final EncoderKey... keys) {
        return getEncoder(key, keys) != null;
    }

    public <F, T> Encoder<F, T> getEncoder(final EncoderKey key, final EncoderKey... keys) {
        if (keys.length == 0) {
            return getEncoderSingleKey(key);
        } else {
            return getEncoderCompositeKey(new CompositeEncoderKey(ImmutableList.<EncoderKey> builder().add(key)
                    .add(keys).build()));
        }
    }

    public Set<SchemaLocation> getSchemaLocation(final String namespace) {
        if (schemaLocations.containsKey(namespace)) {
            return schemaLocations.get(namespace);
        }
        return Sets.newHashSet();
    }

    private <F, T> Decoder<F, T> getDecoderSingleKey(final DecoderKey key) {
        return processDecoderMatches(findDecodersForSingleKey(key), key);
    }

    private <F, T> Decoder<F, T> getDecoderCompositeKey(final CompositeDecoderKey key) {
        return processDecoderMatches(findDecodersForCompositeKey(key), key);
    }

    private <F, T> Encoder<F, T> getEncoderSingleKey(final EncoderKey key) {
        return processEncoderMatches(findEncodersForSingleKey(key), key);
    }

    private <F, T> Encoder<F, T> getEncoderCompositeKey(final CompositeEncoderKey key) {
        return processEncoderMatches(findEncodersForCompositeKey(key), key);
    }

    private Set<Encoder<?, ?>> findEncodersForSingleKey(final EncoderKey key) {
        if (!encoderByKey.containsKey(key)) {
            for (final Encoder<?, ?> encoder : getEncoders()) {
                for (final EncoderKey ek : encoder.getEncoderKeyType()) {
                    if (ek.getSimilarity(key) >= 0) {
                        encoderByKey.add(key, encoder);
                    }
                }
            }
        }
        return encoderByKey.get(key);
    }

    private Set<Decoder<?, ?>> findDecodersForSingleKey(final DecoderKey key) {
        if (!decoderByKey.containsKey(key)) {
            for (final Decoder<?, ?> decoder : getDecoders()) {
                for (final DecoderKey dk : decoder.getDecoderKeyTypes()) {
                    if (dk.getSimilarity(key) >= 0) {
                        decoderByKey.add(key, decoder);
                    }
                }
            }
        }
        return decoderByKey.get(key);
    }

    private Set<Encoder<?, ?>> findEncodersForCompositeKey(final CompositeEncoderKey ck) {
        if (!encoderByKey.containsKey(ck)) {
            // first request; search for matching encoders and save result for
            // later quries
            for (final Encoder<?, ?> encoder : encoders) {
                if (ck.matches(encoder.getEncoderKeyType())) {
                    encoderByKey.add(ck, encoder);
                }
            }
            LOG.debug("Found {} Encoders for CompositeKey: {}", encoderByKey.get(ck).size(),
                    Joiner.on(", ").join(encoderByKey.get(ck)));
        }
        return encoderByKey.get(ck);
    }

    private Set<Decoder<?, ?>> findDecodersForCompositeKey(final CompositeDecoderKey ck) {
        if (!decoderByKey.containsKey(ck)) {
            // first request; search for matching decoders and save result for
            // later queries
            for (final Decoder<?, ?> decoder : decoders) {
                if (ck.matches(decoder.getDecoderKeyTypes())) {
                    decoderByKey.add(ck, decoder);
                }
            }
            LOG.debug("Found {} Decoders for CompositeKey: {}", decoderByKey.get(ck).size(),
                    Joiner.on(", ").join(decoderByKey.get(ck)));
        }
        return decoderByKey.get(ck);
    }

    public Map<ServiceOperatorKey, Set<String>> getSupportedResponseFormats() {
        final Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (final ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getSupportedResponseFormats(sokt));
        }
        return map;
    }

    public Set<String> getSupportedResponseFormats(final ServiceOperatorKey sokt) {
        return getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedResponseFormats(final String service, final String version) {
        final Map<String, Set<String>> byService = responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        final Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }

        final ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        final Set<String> result = Sets.newHashSet();
        for (final String a : rfs) {
            final ResponseFormatKey rfkt = new ResponseFormatKey(sokt, a);
            final Boolean status = responseFormatStatus.get(rfkt);
            if (status != null && status.booleanValue()) {
                result.add(a);
            }
        }
        return result;
    }

    public Set<String> getAllSupportedResponseFormats(final String service, final String version) {
        final Map<String, Set<String>> byService = responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        final Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(rfs);
    }

    public Map<ServiceOperatorKey, Set<String>> getAllSupportedResponseFormats() {
        final Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (final ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getAllSupportedResponseFormats(sokt));
        }
        return map;
    }

    public Set<String> getAllSupportedResponseFormats(final ServiceOperatorKey sokt) {
        return getAllSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Map<ServiceOperatorKey, Set<String>> getSupportedProcedureDescriptionFormats() {
        final Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (final ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getSupportedProcedureDescriptionFormats(final ServiceOperatorKey sokt) {
        return getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        final Map<String, Set<String>> byService = procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        final Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }

        final ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        final Set<String> result = Sets.newHashSet();
        for (final String a : rfs) {
            final ProcedureDescriptionFormatKey pdfkt = new ProcedureDescriptionFormatKey(sokt, a);
            final Boolean status = procedureDescriptionFormatsStatus.get(pdfkt);
            if (status != null && status.booleanValue()) {
                result.add(a);
            }
        }
        return result;
    }

    public Map<ServiceOperatorKey, Set<String>> getAllProcedureDescriptionFormats() {
        final Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (final ServiceOperatorKey sokt : ServiceOperatorRepository.getInstance().getServiceOperatorKeyTypes()) {
            map.put(sokt, getAllSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(final String service, final String version) {
        final Map<String, Set<String>> byService = procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        final Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(rfs);
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(final ServiceOperatorKey sokt) {
        return getAllSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    public void setActive(final ResponseFormatKey rfkt, final boolean active) {
        if (responseFormatStatus.containsKey(rfkt)) {
            responseFormatStatus.put(rfkt, active);
        }
    }

    public void setActive(final ProcedureDescriptionFormatKey pdfk, final boolean active) {
        if (procedureDescriptionFormatsStatus.containsKey(pdfk)) {
            procedureDescriptionFormatsStatus.put(pdfk, active);
        }
    }
    
    public String getNamespaceFor(String prefix) {
        Map<String, String> prefixNamspaceMap = getPrefixNamspaceMap();
        for (String namespace : prefixNamspaceMap.keySet()) {
            if (prefix.equals(prefixNamspaceMap.get(prefix))) {
                return namespace; 
            }
        }
        return null;
    }
    
    public String getPrefixFor(String namespace) {
        return getPrefixNamspaceMap().get(namespace);
        
    }
    
    private Map<String, String> getPrefixNamspaceMap() {
        Map<String, String> prefixMap = Maps.newHashMap();
        for (final Encoder<?, ?> encoder : CodingRepository.getInstance().getEncoders()) {
            encoder.addNamespacePrefixToMap(prefixMap);
        }
        return prefixMap;
    }

    private class DecoderComparator extends ProxySimilarityComparator<Decoder<?, ?>, DecoderKey> {
        DecoderComparator(final DecoderKey key) {
            super(key);
        }

        @Override
        protected Collection<DecoderKey> getSimilars(final Decoder<?, ?> t) {
            return t.getDecoderKeyTypes();
        }
    }

    private class EncoderComparator extends ProxySimilarityComparator<Encoder<?, ?>, EncoderKey> {
        EncoderComparator(final EncoderKey key) {
            super(key);
        }

        @Override
        protected Collection<EncoderKey> getSimilars(final Encoder<?, ?> t) {
            return t.getEncoderKeyType();
        }
    }

    private class CompositeEncoderKey extends CompositeSimilar<EncoderKey> implements EncoderKey {
        CompositeEncoderKey(final Iterable<EncoderKey> keys) {
            super(keys);
        }
    }

    private class CompositeDecoderKey extends CompositeSimilar<DecoderKey> implements DecoderKey {
        CompositeDecoderKey(final Iterable<DecoderKey> keys) {
            super(keys);
        }
    }
}

/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.coding.encode;


import java.util.Set;

import org.n52.janmayen.Producer;
import org.n52.janmayen.Producers;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SosEncoderRepository extends EncoderRepository {
    private final Set<Producer<ObservationEncoder<?, ?>>> observationEncoders
            = Sets.newHashSet();

    @Override
    public void init() {
        super.init();
        this.observationEncoders.clear();
        getComponentProviders().forEach(producer -> {
            Encoder<?, ?> encoder = producer.get();
            if (encoder instanceof ObservationEncoder) {
                this.observationEncoders.add(asObservationEncoderProducer(producer));
            }
        });
    }

    public Set<ObservationEncoder<?, ?>> getObservationEncoders() {
        return Producers.produce(this.observationEncoders);
    }

    @SuppressWarnings("unchecked")
    private static Producer<ObservationEncoder<?, ?>> asObservationEncoderProducer(
            Producer<? extends Encoder<?, ?>> producer) {
        return (Producer<ObservationEncoder<?, ?>>) producer;
    }

}

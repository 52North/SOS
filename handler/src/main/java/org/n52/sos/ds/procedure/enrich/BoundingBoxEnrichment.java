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
package org.n52.sos.ds.procedure.enrich;

import java.util.Optional;
import java.util.function.Predicate;

import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.swe.DataRecord;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweEnvelope;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class BoundingBoxEnrichment
        extends
        SensorMLEnrichment {
    public static final Predicate<SmlCapabilities> BBOX_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);

    public BoundingBoxEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    @Override
    public void enrich(final AbstractSensorML description)
            throws OwsExceptionReport {
        final Optional<SmlCapabilities> existingCapabilities = description.findCapabilities(BBOX_PREDICATE);
        final Optional<SmlCapabilities> newCapabilities = createCapabilities(existingCapabilities);

        if (newCapabilities.isPresent()) {
            if (existingCapabilities.isPresent()) {
                description.getCapabilities().remove(existingCapabilities.get());
            }
            description.addCapabilities(newCapabilities.get());
        }
    }

    private Optional<SmlCapabilities> createCapabilities(final Optional<SmlCapabilities> existing)
            throws OwsExceptionReport {
        final ReferencedEnvelope sosEnv = createEnvelopeForOfferings();
        if (existing.isPresent()) {
            final DataRecord dataRecord = existing.get().getDataRecord();
            final int i = dataRecord.getFieldIndexByIdentifier(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);
            if (i >= 0) {
                final SweAbstractDataComponent e = dataRecord.getFields().get(i).getElement();
                if (e instanceof SweEnvelope) {
                    sosEnv.expandToInclude(((SweEnvelope) e).toReferencedEnvelope());
                }
            }
        }
        return createCapabilities(sosEnv);
    }

    private Optional<SmlCapabilities> createCapabilities(final ReferencedEnvelope bbox) throws CodedException {
        if (bbox.isSetEnvelope()) {
            // add merged bbox to capabilities as swe:envelope
            final SweEnvelope envelope = new SweEnvelope(bbox, procedureSettings().getLatLongUom(),
                    getProcedureCreationContext().getGeometryHandler().isNorthingFirstEpsgCode(bbox.getSrid()));
            envelope.setDefinition(SensorMLConstants.OBSERVED_BBOX_DEFINITION_URN);
            final SweField field = new SweField(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX, envelope);
            return Optional.of(new SmlCapabilities().setName(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX)
                    .setDataRecord(new SweDataRecord().addField(field)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Merge offering sosEnvelopes.
     *
     * @return merged sosEnvelope
     * @throws CodedException If an error occurs
     */
    public ReferencedEnvelope createEnvelopeForOfferings()
            throws CodedException {
        final ReferencedEnvelope mergedEnvelope = new ReferencedEnvelope();
        for (final SosOffering offering : getSosOfferings()) {
            mergedEnvelope.expandToInclude(getEnvelope(offering));
        }
        return mergedEnvelope.setSrid(getProcedureCreationContext().getGeometryHandler().getStorageEPSG());
    }

    /**
     * Get the sosEnvelope for the given offering.
     *
     * @param offering
     *            the offering
     *
     * @return the sosEnvelope (may be <code>null</code>)
     */
    private ReferencedEnvelope getEnvelope(final SosOffering offering) {
        return getCache().getEnvelopeForOffering(offering.getIdentifier());
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithDiscoveryInformation();
    }
}

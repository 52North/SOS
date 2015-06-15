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
package org.n52.sos.ds.hibernate.util.procedure.enrich;


import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class BoundingBoxEnrichment extends SensorMLEnrichment {
	public static final Predicate<SmlCapabilities> BBOX_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);

    @Override
    public void enrich(final AbstractSensorML description) throws OwsExceptionReport {
        final Optional<SmlCapabilities> existingCapabilities =
                description.findCapabilities(BBOX_PREDICATE);
        final Optional<SmlCapabilities> newCapabilities =
                createCapabilities(existingCapabilities);

        if (newCapabilities.isPresent()) {
            if (existingCapabilities.isPresent()) {
                description.getCapabilities().remove(existingCapabilities.get());
            }
            description.addCapabilities(newCapabilities.get());
        }
    }

    private Optional<SmlCapabilities> createCapabilities(final Optional<SmlCapabilities> existing) throws OwsExceptionReport {
        final SosEnvelope sosEnv = createEnvelopeForOfferings();
        if (existing.isPresent()) {
            final DataRecord dataRecord = existing.get().getDataRecord();
            final int i = dataRecord.getFieldIndexByIdentifier(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);
            if (i >= 0) {
                final SweAbstractDataComponent e = dataRecord.getFields().get(i).getElement();
                if (e instanceof SweEnvelope) {
                    sosEnv.expandToInclude(((SweEnvelope) e).toSosEnvelope());
                }
            }
        }
        return createCapabilities(sosEnv);
    }

    /**
     * Merge offering sosEnvelopes.
     *
     * @return merged sosEnvelope
     */
    protected SosEnvelope createEnvelopeForOfferings() {
        final SosEnvelope mergedEnvelope = new SosEnvelope();
        for (final SosOffering offering : getSosOfferings()) {
            mergedEnvelope.expandToInclude(getEnvelope(offering));
        }
        return mergedEnvelope.setSrid(GeometryHandler.getInstance().getStorageEPSG());
    }

    /**
     * Get the sosEnvelope for the given offering.
     *
     * @param offering the offering
     *
     * @return the sosEnvelope (may be <code>null</code>)
     */
    private SosEnvelope getEnvelope(final SosOffering offering) {
        return getCache().getEnvelopeForOffering(offering.getIdentifier());
    }

    private Optional<SmlCapabilities> createCapabilities(final SosEnvelope bbox) {
        if (bbox.isSetEnvelope()) {
            // add merged bbox to capabilities as swe:envelope
            final SweEnvelope envelope = new SweEnvelope(bbox, procedureSettings().getLatLongUom());
            envelope.setDefinition(SensorMLConstants.OBSERVED_BBOX_DEFINITION_URN);
            final SweField field = new SweField(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX, envelope);
            return Optional.of(new SmlCapabilities()
                    .setName(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX)
                    .setDataRecord(new SweDataRecord().addField(field)));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithDiscoveryInformation();
    }
}

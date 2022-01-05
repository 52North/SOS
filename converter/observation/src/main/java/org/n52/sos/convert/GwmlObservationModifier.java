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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.ogc.gwml.GWMLConstants;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GwmlObservationModifier extends AbstractRequestResponseModifier {
    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    private static Set<RequestResponseModifierKey> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<OwsServiceRequest, OwsServiceResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        Set<RequestResponseModifierKey> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (Entry<OwsServiceRequest, OwsServiceResponse> entry : requestResponseMap.entrySet()) {
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey()));
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey(), requestResponseMap
                            .get(entry.getKey())));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        if (response instanceof AbstractObservationResponse) {
            return checkGetObservationResponse((AbstractObservationResponse) response);
        }
        return super.modifyResponse(request, response);
    }

    private OwsServiceResponse checkGetObservationResponse(AbstractObservationResponse response)
            throws NoSuchElementException, OwsExceptionReport {
        List<OmObservation> observations = new LinkedList<OmObservation>();
        while (response.getObservationCollection().hasNext()) {
            OmObservation o = response.getObservationCollection().next();
            observations.add(o);
            if (o.getObservationConstellation().isSetObservationType()
                    && (GWMLConstants.OBS_TYPE_GEOLOGY_LOG.equals(o.getObservationConstellation().getObservationType())
                            || GWMLConstants.OBS_TYPE_GEOLOGY_LOG_COVERAGE
                                    .equals(o.getObservationConstellation().getObservationType())
                            || OmConstants.OBS_TYPE_PROFILE_OBSERVATION
                                    .equals(o.getObservationConstellation().getObservationType()))) {
                if (OmConstants.NS_OM_2.equals(response.getResponseFormat())
                        || GWMLConstants.NS_GWML_22.equals(response.getResponseFormat())
                        || GWMLConstants.NS_GWML_WELL_22.equals(response.getResponseFormat())) {
                    o.getObservationConstellation().setObservationType(GWMLConstants.OBS_TYPE_GEOLOGY_LOG);
                }
                if (o.isSetValue() && o.getValue() instanceof SingleObservationValue) {
                    if (o.getValue().getValue() instanceof BooleanValue
                            || o.getValue().getValue() instanceof CategoryValue
                            || o.getValue().getValue() instanceof CountValue
                            || o.getValue().getValue() instanceof QuantityValue
                            || o.getValue().getValue() instanceof TextValue) {
                        ProfileLevel pl = new ProfileLevel().addValue(o.getValue().getValue());
                        if (o.isSetParameter()) {
                            for (NamedValue<?> param : o.getParameter()) {
                                if (param.getName().isSetHref() && param.getValue() instanceof QuantityValue) {
                                    if (GWMLConstants.PARAM_FROM_DEPTH.equals(param.getName().getHref())) {
                                        pl.setLevelStart((QuantityValue) param.getValue());
                                    } else if (GWMLConstants.PARAM_TO_DEPTH.equals(param.getName().getHref())) {
                                        pl.setLevelEnd((QuantityValue) param.getValue());
                                    }
                                }
                            }
                        }
                        SingleObservationValue<List<ProfileLevel>> sov =
                                new SingleObservationValue<>(new ProfileValue("").addValue(pl));
                        sov.setPhenomenonTime(o.getValue().getPhenomenonTime());
                        o.setValue(sov);
                    }
                }
            }
        }
        return response.setObservationCollection(ObservationStream.of(observations));
    }

}

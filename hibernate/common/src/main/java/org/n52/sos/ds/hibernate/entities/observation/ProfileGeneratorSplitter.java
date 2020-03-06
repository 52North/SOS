/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation;

import java.util.List;
import java.util.Map;

import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.observation.valued.ProfileValuedObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.ValuedParameterVisitor;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProfileGeneratorSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileGeneratorSplitter.class);
    
    public static ProfileValue create(ProfileValuedObservation entity) throws OwsExceptionReport {
        ProfileValue profileValue = new ProfileValue();
        profileValue.setGmlId("pv" + entity.getObservationId());
        UoM uom = null;
        if (entity.isSetLevelUnit()) {
            Unit levelunit = entity.getLevelUnit();
            uom = new UoM(levelunit.getUnit());
            if (levelunit.isSetName()) {
                uom.setName(levelunit.getName());
            }
            if (levelunit.isSetLink()) {
                uom.setLink(levelunit.getLink());
            }
        }
        if (entity.isSetFromLevel()) {
            profileValue.setFromLevel(new QuantityValue(entity.getFromLevel(), uom));
        }
        if (entity.isSetToLevel()) {
            profileValue.setToLevel(new QuantityValue(entity.getToLevel(), uom));
        }
        profileValue.setValue(createProfileLevel(entity));
        return profileValue;
    }
    
    public static SweAbstractDataComponent createValue(ProfileValuedObservation entity) throws OwsExceptionReport {
        return create(entity).asDataRecord();
    }

    private static List<ProfileLevel> createProfileLevel(ProfileValuedObservation entity) throws OwsExceptionReport {
        Map<Double, ProfileLevel> map = Maps.newTreeMap();
        if (entity.isSetValue()) {
            for (Observation<?> observation : entity.getValue()) {
                if (observation.isSetValue()) {
                    QuantityValue levelStart = getLevelStart(observation);
                    QuantityValue levelEnd = getLevelEnd(observation);
                    Value<?> value = observation.accept(new ObservationValueCreator());
                    Double key = getKey(levelStart, levelEnd);
                    if (map.containsKey(key)) {
                        map.get(key).addValue(value);
                    } else {
                        ProfileLevel profileLevel = new ProfileLevel();
                        profileLevel.setLevelStart(levelStart);
                        profileLevel.setLevelEnd(levelEnd);
                        if (observation.hasSamplingGeometry()) {
                            profileLevel.setLocation(observation.getSamplingGeometry());
                        }
                        profileLevel.addValue(value);
                        map.put(key, profileLevel);
                    }
                }
                if (observation.hasParameters()) {
                    
                    
                }
            }
        }
        return (List<ProfileLevel>)Lists.newArrayList(map.values());
    }

    private static Double getKey(QuantityValue levelStart, QuantityValue levelEnd) {
        if (levelStart != null && levelStart.isSetValue()) {
            return levelStart.getValue();
        } else if (levelEnd != null && levelEnd.isSetValue()) {
            return levelEnd.getValue();
        }
        return Double.NaN;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static QuantityValue getLevelStart(Observation<?> observation) throws OwsExceptionReport {
        if (observation.hasVerticalFrom()) {
            QuantityValue value = new QuantityValue(observation.getVerticalFrom());
            String name = "from";
            if (observation.hasVerticalFromName()) {
                name = observation.getVerticalFromName();
            }
            value.setDefinition(name);
            value.setName(name);
            if (observation.hasVerticalUnit()) {
                value.setUnit(observation.getVerticalUnit().getUnit());
            }
            return value;
        } else if (observation.hasParameters()) {
            for (Parameter parameter : observation.getParameters()) {
                if (checkParameterForStartLevel(parameter.getName())) {
                    NamedValue namedValue = parameter.accept(new ValuedParameterVisitor());
                    if (namedValue.getValue() instanceof QuantityValue) {
                        QuantityValue value = (QuantityValue)namedValue.getValue();
                        value.setDefinition(parameter.getName());
                        value.setName(parameter.getName());
                        return value;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static QuantityValue getLevelEnd(Observation<?> observation) throws OwsExceptionReport {
        if (observation.hasVerticalTo()) {
            QuantityValue value = new QuantityValue(observation.getVerticalTo());
            String name = "to";
            if (observation.hasVerticalToName()) {
                name = observation.getVerticalToName();
            }
            value.setDefinition(name);
            value.setName(name);
            if (observation.hasVerticalUnit()) {
                value.setUnit(observation.getVerticalUnit().getUnit());
            }
            return value;
        } else if (observation.hasParameters()) {
            for (Parameter parameter : observation.getParameters()) {
                if (checkParameterForEndLevel(parameter.getName())) {
                    NamedValue namedValue = parameter.accept(new ValuedParameterVisitor());
                    if (namedValue.getValue() instanceof QuantityValue) {
                        QuantityValue value = (QuantityValue)namedValue.getValue();
                        value.setDefinition(parameter.getName());
                        value.setName(parameter.getName());
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private static boolean checkParameterForStartLevel(String name) {
        return OmConstants.PARAMETER_NAME_DEPTH_URL.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_DEPTH_URL.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_ELEVATION.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM_HEIGHT.equalsIgnoreCase(name);
    }

    private static boolean checkParameterForEndLevel(String name) {
        return OmConstants.PARAMETER_NAME_TO.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_TO_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_TO_HEIGHT.equalsIgnoreCase(name);
    }

    public static void split(ProfileValue coverage, ProfileValuedObservation entity) {
        LOG.warn("Inserting of GW_GeologyLogCoverages is not yet supported!");
    }

}

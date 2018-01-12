/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.Parameter;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.util.JTSConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProfileGeneratorSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileGeneratorSplitter.class);

    public static ProfileValue create(ProfileDataEntity entity) throws OwsExceptionReport {
        ProfileValue profileValue = new ProfileValue("");
        profileValue.setGmlId("pv" + entity.getId());
        UoM uom = null;
        if (entity.hasLevelUnit()) {
            UnitEntity levelunit = entity.getLevelUnit();
            uom = new UoM(levelunit.getUnit());
            if (levelunit.isSetName()) {
                uom.setName(levelunit.getName());
            }
            if (levelunit.isSetLink()) {
                uom.setLink(levelunit.getLink());
            }
        }
        if (entity.hasFromLevel()) {
            profileValue.setFromLevel(new QuantityValue(entity.getFromLevel(), uom));
        }
        if (entity.hasToLevel()) {
            profileValue.setToLevel(new QuantityValue(entity.getToLevel(), uom));
        }
        profileValue.setValue(createProfileLevel(entity));
        return profileValue;
    }

    public static SweAbstractDataComponent createValue(ProfileDataEntity entity) throws OwsExceptionReport {
        return create(entity).asDataRecord();
    }

    private static List<ProfileLevel> createProfileLevel(ProfileDataEntity entity) throws OwsExceptionReport {
        Map<Double, ProfileLevel> map = Maps.newTreeMap();
        if (entity.hasValue()) {
            for (DataEntity<?> observation : entity.getValue()) {
                if (observation.hasParameters() && observation.hasValue()) {
                    QuantityValue levelStart = getLevelStart(observation.getParameters());
                    QuantityValue levelEnd = getLevelEnd(observation.getParameters());
                    Double key = getKey(levelStart, levelEnd);
                    Value<?> value = new ObservationValueCreator().visit(observation);
                    if (map.containsKey(key)) {
                        map.get(key).addValue(value);
                    } else {
                        ProfileLevel profileLevel = new ProfileLevel();
                        profileLevel.setLevelStart(levelStart);
                        profileLevel.setLevelEnd(levelEnd);
                        if (observation.isSetGeometryEntity()) {
                            profileLevel.setLocation(JTSConverter.convert(observation.getGeometryEntity().getGeometry()));
                        }
                        profileLevel.addValue(value);
                        map.put(key, profileLevel);
                    }
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

    @SuppressWarnings("rawtypes")
    private static QuantityValue getLevelStart(Set<Parameter<?>> parameters) throws OwsExceptionReport {
        for (Parameter<?> parameter : parameters) {
            if (checkParameterForStartLevel(parameter.getName())) {
                NamedValue namedValue = new ParameterVisitor().visit(parameter);
                if (namedValue.getValue() instanceof QuantityValue) {
                    QuantityValue value = (QuantityValue)namedValue.getValue();
                    value.setDefinition(parameter.getName());
                    value.setName(parameter.getName());
                    return value;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static QuantityValue getLevelEnd(Set<Parameter<?>> parameters) throws OwsExceptionReport {
        for (Parameter<?> parameter : parameters) {
            if (checkParameterForEndLevel(parameter.getName())) {
                NamedValue namedValue = new ParameterVisitor().visit(parameter);
                if (namedValue.getValue() instanceof QuantityValue) {
                    QuantityValue value = (QuantityValue)namedValue.getValue();
                    value.setDefinition(parameter.getName());
                    value.setName(parameter.getName());
                    return value;
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

    public static void split(ProfileValue coverage, ProfileDataEntity entity) {
        LOG.warn("Inserting of GW_GeologyLogCoverages is not yet supported!");
    }

}

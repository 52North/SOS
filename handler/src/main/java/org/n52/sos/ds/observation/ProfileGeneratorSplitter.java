/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.observation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ds.parameter.ParameterVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class ProfileGeneratorSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileGeneratorSplitter.class);

    private AbstractObservationValueCreator creator;

    public ProfileGeneratorSplitter(AbstractObservationValueCreator creator) {
        this.creator = creator;
    }

    public ProfileValue create(ProfileDataEntity entity) throws OwsExceptionReport {
        ProfileValue profileValue = new ProfileValue("");
        profileValue.setGmlId("pv" + entity.getId());
        UoM uom = null;
        if (entity.getDataset().hasVerticalMetadata() && entity.getDataset().getVerticalMetadata().hasVerticalUnit()) {
            UnitEntity levelunit = entity.getDataset().getVerticalMetadata().getVerticalUnit();
            uom = new UoM(levelunit.getUnit());
            if (levelunit.isSetName()) {
                uom.setName(levelunit.getName());
            }
            if (levelunit.isSetLink()) {
                uom.setLink(levelunit.getLink());
            }
        }
        if (entity.hasVerticalFrom()) {
            profileValue.setFromLevel(new QuantityValue(entity.getVerticalFrom().doubleValue(), uom));
        }
        if (entity.hasVerticalTo()) {
            profileValue.setToLevel(new QuantityValue(entity.getVerticalTo().doubleValue(), uom));
        }
        profileValue.setValue(createProfileLevel(entity));
        return profileValue;
    }

    public SweAbstractDataComponent createValue(ProfileDataEntity entity) throws OwsExceptionReport {
        return create(entity).asDataRecord();
    }

    private List<ProfileLevel> createProfileLevel(ProfileDataEntity entity) throws OwsExceptionReport {
        Map<BigDecimal, ProfileLevel> map = Maps.newTreeMap();
        if (entity.hasValue()) {
            for (DataEntity<?> o : entity.getValue()) {
                DataEntity<?> observation  = Hibernate.unproxy(o, DataEntity.class);
                VerticalMetadataEntity verticalMetadata = entity.getDataset().getVerticalMetadata();
                QuantityValue levelStart = observation.hasVerticalFrom() && observation.hasVerticalInterval()
                        ? getLevelStart(observation.getVerticalFrom(), verticalMetadata.getVerticalFromName(),
                                verticalMetadata.getVerticalUnit())
                        : null;
                QuantityValue levelEnd =
                        observation.hasVerticalTo()
                                ? getLevelEnd(observation.getVerticalTo(), verticalMetadata.getVerticalToName(),
                                        verticalMetadata.getVerticalUnit())
                                : null;
                if (levelStart == null && levelEnd == null && observation.hasParameters()) {
                    levelStart = getLevelStart(observation.getParameters());
                    levelEnd = getLevelEnd(observation.getParameters());
                }
                if (levelStart != null || levelEnd != null) {
                    BigDecimal key = getKey(levelStart, levelEnd);
                    Value<?> value = creator.visit(observation);
                    if (map.containsKey(key)) {
                        map.get(key).addValue(value);
                    } else {
                        ProfileLevel profileLevel = new ProfileLevel();
                        profileLevel.setLevelStart(levelStart);
                        profileLevel.setLevelEnd(levelEnd);
                        if (observation.isSetGeometryEntity()) {
                            profileLevel.setLocation(observation.getGeometryEntity().getGeometry());
                        }
                        profileLevel.addValue(value);
                        map.put(key, profileLevel);
                    }
                }
            }
        }
        return (List<ProfileLevel>) Lists.newArrayList(map.values());
    }

    private BigDecimal getKey(QuantityValue levelStart, QuantityValue levelEnd) {
        if (levelStart != null && levelStart.isSetValue()) {
            return levelStart.getValue();
        } else if (levelEnd != null && levelEnd.isSetValue()) {
            return levelEnd.getValue();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private QuantityValue getLevelStart(Set<ParameterEntity<?>> parameters) throws OwsExceptionReport {
        for (ParameterEntity<?> parameter : parameters) {
            if (checkParameterForStartLevel(parameter.getName())) {
                NamedValue namedValue = new ParameterVisitor().visit(parameter);
                if (namedValue.getValue() instanceof QuantityValue) {
                    QuantityValue value = (QuantityValue) namedValue.getValue();
                    value.setDefinition(parameter.getName());
                    value.setName(parameter.getName());
                    return value;
                }
            }
        }
        return null;
    }

    private QuantityValue getLevelStart(BigDecimal fromLevel, String fromLevelName, UnitEntity levelUnit) {
        if (fromLevel != null) {
            return fromLevelName != null && !fromLevelName.isEmpty() ? getLevel(fromLevel, fromLevelName, levelUnit)
                    : getLevel(fromLevel, "from", levelUnit);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private QuantityValue getLevelEnd(Set<ParameterEntity<?>> parameters) throws OwsExceptionReport {
        for (ParameterEntity<?> parameter : parameters) {
            if (checkParameterForEndLevel(parameter.getName())) {
                NamedValue namedValue = new ParameterVisitor().visit(parameter);
                if (namedValue.getValue() instanceof QuantityValue) {
                    QuantityValue value = (QuantityValue) namedValue.getValue();
                    value.setDefinition(parameter.getName());
                    value.setName(parameter.getName());
                    return value;
                }
            }
        }
        return null;
    }

    private QuantityValue getLevelEnd(BigDecimal toLevel, String toLevelName, UnitEntity levelUnit) {
        if (toLevel != null) {
            return toLevelName != null && !toLevelName.isEmpty() ? getLevel(toLevel, toLevelName, levelUnit)
                    : getLevel(toLevel, "to", levelUnit);
        }
        return null;
    }

    private QuantityValue getLevel(BigDecimal v, String n, UnitEntity u) {
        QuantityValue value = new QuantityValue(v);
        value.setDefinition(n);
        value.setName(n);
        if (u != null && u.isSetIdentifier()) {
            UoM uom = new UoM(u.getIdentifier());
            if (u.isSetName()) {
                uom.setName(u.getName());
            }
            if (u.isSetLink()) {
                uom.setLink(u.getLink());
            }
            value.setUnit(uom);
        } else {
            value.setUnit(new UoM("m").setName("meter"));
        }
        return value;
    }

    private boolean checkParameterForStartLevel(String name) {
        return OmConstants.PARAMETER_NAME_DEPTH_URL.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_ELEVATION.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_FROM_HEIGHT.equalsIgnoreCase(name);
    }

    private boolean checkParameterForEndLevel(String name) {
        return OmConstants.PARAMETER_NAME_TO.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_TO_DEPTH.equalsIgnoreCase(name)
                || OmConstants.PARAMETER_NAME_TO_HEIGHT.equalsIgnoreCase(name);
    }

    public void split(ProfileValue coverage, ProfileDataEntity entity) {
        LOG.warn("Inserting of GW_GeologyLogCoverages is not yet supported!");
    }

}

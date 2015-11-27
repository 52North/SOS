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
package org.n52.svalbard.gml.v321.encode;

import java.util.List;
import java.util.Map;

import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;

import com.google.common.collect.Lists;

import net.opengis.gml.x32.CoordinatesType;
import net.opengis.gml.x32.DataBlockType;
import net.opengis.gml.x32.DiscreteCoverageType;
import net.opengis.gml.x32.DomainSetType;
import net.opengis.gml.x32.RangeSetType;
import net.opengis.gml.x32.ValueArrayType;
import net.opengis.gml.x33.ce.SimpleMultiPointType;

public abstract class AbstractRectifiedGridCoverageTypeEncoder<T> extends AbstractSpecificXmlEncoder<T, RectifiedGridCoverage> {

    protected DiscreteCoverageType encodeRectifiedGridCoverage(RectifiedGridCoverage rectifiedGridCoverage) {
        DiscreteCoverageType dct = DiscreteCoverageType.Factory.newInstance();
//        dct.setId(rectifiedGridCoverage.get);
        dct.setDomainSet(encodeDomainSet());
        dct.setRangeSet(encodeRangeSet());
        return dct;
    }
    
    private DomainSetType encodeDomainSet() {
        DomainSetType dst = DomainSetType.Factory.newInstance();
        SimpleMultiPointType smpt = SimpleMultiPointType.Factory.newInstance();
        dst.setAbstractGeometry(smpt);
        return dst;
    }

    private RangeSetType encodeRangeSet() {
        RangeSetType rst = RangeSetType.Factory.newInstance();
        DataBlockType dbt = rst.addNewDataBlock();
        dbt.setDoubleOrNilReasonTupleList(doubleOrNilReasonTupleList);
        CoordinatesType tupleList = dbt.getTupleList();
        
        ValueArrayType addNewValueArray1 = rst.addNewValueArray1();
        addNewValueArray1.setUom("uom");
        addNewValueArray1.addNewValueComponent().set
       .addNewValueComponent().setAbstractValue(abstractValue);
        
        List<?> list = Lists.newArrayList(45.2, 76.6, "unknown", 64);
        RangeSetType rst = RangeSetType.Factory.newInstance();
        DataBlockType dbt = rst.addNewDataBlock();
        dbt.setDoubleOrNilReasonTupleList(list);
        return rst;
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }

}

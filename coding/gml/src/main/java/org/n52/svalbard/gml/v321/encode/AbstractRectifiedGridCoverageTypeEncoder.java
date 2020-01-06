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
package org.n52.svalbard.gml.v321.encode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.values.QuantityValued;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.RangeValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.opengis.gml.x32.DirectPositionListType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.DiscreteCoverageType;
import net.opengis.gml.x32.LineStringDocument;
import net.opengis.gml.x32.LineStringType;
import net.opengis.gml.x33.ce.SimpleMultiPointDocument;
import net.opengis.gml.x33.ce.SimpleMultiPointType;

/**
 * Abstract {@link Encoder} implementation for {@link RectifiedGridCoverage}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 * @param <T>
 */
public abstract class AbstractRectifiedGridCoverageTypeEncoder<T>
        extends AbstractCoverageEncoder<T, RectifiedGridCoverage> {

    /**
     * Encodes the {@link RectifiedGridCoverage} to {@link DiscreteCoverageType}
     * 
     * @param rectifiedGridCoverage
     *            The {@link RectifiedGridCoverage}
     * @param additionalValues
     *            Helper values
     * @return Encoded {@link RectifiedGridCoverage}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected DiscreteCoverageType encodeRectifiedGridCoverage(RectifiedGridCoverage rectifiedGridCoverage,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        DiscreteCoverageType dct = DiscreteCoverageType.Factory.newInstance();
        dct.setId(rectifiedGridCoverage.getGmlId());
        XmlObject encodedGeometry = encodeDomainSet(rectifiedGridCoverage);
        dct.addNewDomainSet().set(encodedGeometry);
        dct.setRangeSet(encodeRangeSet(dct, rectifiedGridCoverage));
        return dct;
    }

    private XmlObject encodeDomainSet(RectifiedGridCoverage rectifiedGridCoverage) {
        List<QuantityValued<?, ?>> domainSet = rectifiedGridCoverage.getDomainSet();
        if (!checkForRange(domainSet)) {
            SimpleMultiPointDocument smpd = SimpleMultiPointDocument.Factory.newInstance();
            SimpleMultiPointType smpt = smpd.addNewSimpleMultiPoint();
            smpt.setId("smp_" + rectifiedGridCoverage.getGmlId());
            DirectPositionListType dplt = smpt.addNewPosList();
            List<String> uoms = getUoms(domainSet);
            if (!uoms.isEmpty()) {
                dplt.setUomLabels(Lists.newArrayList(uoms));
            }
            dplt.setListValue(getList(rectifiedGridCoverage.getDomainSet()));
            return smpd;
        } else {
            LineStringDocument lsd = LineStringDocument.Factory.newInstance();
            LineStringType lst = lsd.addNewLineString();
            lst.setId("ls_" + rectifiedGridCoverage.getGmlId());
            lst.setUomLabels(getUoms(domainSet));
            for (QuantityValued<?, ?> quantityValued : domainSet) {
                Object value = quantityValued.getValue();
                if (value instanceof Double) {
                    lst.addNewPos().setListValue(Lists.newArrayList((Double)value));
                } else if (value instanceof RangeValue) {
                    lst.addNewPos().setListValue(((RangeValue) value).getRangeAsList());
                }
            }
            return lsd;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List getList(List<QuantityValued<?, ?>> domainSet) {
        List list = new ArrayList<>();
        for (QuantityValued<?, ?> quantityValued : domainSet) {
            if (quantityValued.getValue() instanceof Double) {
                list.add(((Double)quantityValued.getValue()));
            }
        }
        return list;
    }

    private boolean checkForRange(List<QuantityValued<?, ?>> domainSet) {
        for (QuantityValued<?, ?> quantityValued : domainSet) {
            if (quantityValued.getValue() instanceof RangeValue) {
                return true;
            }
        }
        return false;
    }

    private List<String> getUoms(List<QuantityValued<?, ?>> domainSet) {
        SortedSet<String> uoms = Sets.newTreeSet();
        for (QuantityValued<?, ?> values : domainSet) {
            uoms.add(values.getUnit());
        }
        return Lists.newArrayList(uoms);
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }
    
}

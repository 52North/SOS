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
package org.n52.sos.ogc.om.values;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Multi value representing a SweDataArray for observations
 * 
 * @since 4.0.0
 * 
 */
public class SweDataArrayValue implements MultiValue<SweDataArray> {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SweDataArrayValue.class);

    /**
     * serial number
     */
    private static final long serialVersionUID = 3022136042762771037L;

    /**
     * Measurement values
     */
    private SweDataArray value;

    @Override
    public void setValue(final SweDataArray value) {
        this.value = value;
    }

    @Override
    public SweDataArray getValue() {
        return value;
    }

    @Override
    public void setUnit(final String unit) {
        // do nothing
    }

    @Override
    public String getUnit() {
        return null;
    }

    /**
     * Adds the given block - a {@link List}<{@link String}> - add the end of
     * the current list of blocks
     * 
     * @param blockOfTokensToAddAtTheEnd
     * @return <tt>true</tt> (as specified by {@link Collection#add}) <br />
     *         <tt>false</tt> if block could not be added
     */
    public boolean addBlock(final List<String> blockOfTokensToAddAtTheEnd) {
        if (value != null) {
            return value.add(blockOfTokensToAddAtTheEnd);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("SweDataArrayValue [value=%s, unit=null]", getValue());
    }

    @Override
    public Time getPhenomenonTime() {
        final TimePeriod timePeriod = new TimePeriod();
        Set<Integer> dateTokenIndizes = Sets.newHashSet();
        int dateTokenIndex = -1;
        if (getValue() != null && getValue().getElementType() != null && getValue().getEncoding() != null) {
            // get index of time token from elementtype
            if (getValue().getElementType() instanceof SweDataRecord) {
                final SweDataRecord elementType = (SweDataRecord) getValue().getElementType();
                final List<SweField> fields = elementType.getFields();
                for (int i = 0; i < fields.size(); i++) {
                    final SweField sweField = fields.get(i);
                    if (sweField.getElement() instanceof SweTime || sweField.getElement() instanceof SweTimeRange) {
                    	if (checkFieldNameAndElementDefinition(sweField)) {
                    		dateTokenIndizes.add(i);
                    	}
//                    	dateTokenIndex = i;
//                        break;
                    }
                }

            }
            if (CollectionHelper.isNotEmpty(dateTokenIndizes)) {
                for (final List<String> block : getValue().getValues()) {
                    // check for "/" to identify time periods (Is
                    // conform with ISO8601 (see WP))
                    // datetimehelper to DateTime from joda time
                    for (Integer index : dateTokenIndizes) {
                    	String token = null;
                    	try {
	                    	token = block.get(index);
	                    	final Time time = DateTimeHelper.parseIsoString2DateTime2Time(token);
	                        timePeriod.extendToContain(time);
	                    } catch (final DateTimeParseException dte) {
	                         LOGGER.error(String.format("Could not parse ISO8601 string \"%s\"", token), dte);
	                         // FIXME throw exception here?
	                         continue; // try next block;
	                     }
					}
                }
			} else {
				final String errorMsg = "PhenomenonTime field could not be found in ElementType";
				LOGGER.error(errorMsg);
			}
//                    if (dateTokenIndex > -1) {
//                        for (final List<String> block : getValue().getValues()) {
//                            // check for "/" to identify time periods (Is
//                            // conform with ISO8601 (see WP))
//                            // datetimehelper to DateTime from joda time
//                	final String token = block.get(dateTokenIndex);
//                    try {
//                        final Time time = DateTimeHelper.parseIsoString2DateTime2Time(token);
//                        timePeriod.extendToContain(time);
//                    } catch (final DateTimeParseException dte) {
//                        LOGGER.error(String.format("Could not parse ISO8601 string \"%s\"", token), dte);
//                        // FIXME throw exception here?
//                        continue; // try next block;
//                    }
//                   }
//            } else {
//                final String errorMsg = "PhenomenonTime field could not be found in ElementType";
//                LOGGER.error(errorMsg);
//            }
        } else {
            final String errorMsg =
                    String.format("Value of type \"%s\" not set correct.", SweDataArrayValue.class.getName());
            LOGGER.error(errorMsg);
        }
        return timePeriod;
    }

    private boolean checkFieldNameAndElementDefinition(SweField sweField) {
		return "StartTime".equals(sweField.getName().getValue()) || "EndTime".equals(sweField.getName().getValue())
				|| OmConstants.PHENOMENON_TIME.equals(sweField.getElement().getDefinition());
		
	}

	@Override
    public boolean isSetValue() {
        return getValue() != null && getValue().isEmpty();
    }

    @Override
    public boolean isSetUnit() {
        return false;
    }
}

/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.om.values;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.n52.iceland.exception.ows.concrete.DateTimeParseException;
import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.ogc.swe.SweDataArray;
import org.n52.iceland.ogc.swe.SweDataRecord;
import org.n52.iceland.ogc.swe.SweField;
import org.n52.iceland.ogc.swe.simpleType.SweTime;
import org.n52.iceland.ogc.swe.simpleType.SweTimeRange;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.DateTimeHelper;
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

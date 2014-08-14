/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.datasource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n52.sos.ogc.swe.SweDataArray;

import rasj.RasMArrayByte;

public class RasCsvToSweDataArrayConverter {
	
	public static List<List<String>> getLines(String csvRes) {
		Pattern p = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
		Matcher m = p.matcher(csvRes);
		List<List<String>> result = new ArrayList<>();
		
		while(m.find()) {
			List<String> auxList = new ArrayList<>();
			String auxString = m.group(1);
			String[] splitted = auxString.split(",");
			for (int i = 0; i < splitted.length; i++) {
				auxList.add(splitted[i]);
			}
			result.add(auxList);
		}
		
		return result;
		
	}
	
	public static SweDataArray csvToSweDataArray(String csvRes) {
		List<List<String>> arr = getLines(csvRes);
		SweDataArray sweArr = new SweDataArray();
		sweArr.setValues(arr);
		return sweArr;
	}
	
	public static SweDataArray rasByteArrayToSweDataArray(RasMArrayByte byteRes) {
		String csvString;
		try {
			csvString = new String(byteRes.getArray(), "UTF-8");
			List<List<String>> arr = getLines(csvString);
			SweDataArray sweArr = new SweDataArray();
			sweArr.setValues(arr);
			return sweArr;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

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

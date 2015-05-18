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
package org.n52.iceland.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Helper class for String objects. Contains methods to join Strings, convert
 * streams to Strings or to check for null and emptiness.
 * 
 * @since 4.0.0
 * 
 */
public final class StringHelper {
    /**
     * @param toNormalize
     *            the string to normalize
     * 
     * @return a normalized String for use in a file path, i.e. all
     *         [\,/,:,*,?,",<,>,;] characters are replaced by '_'.
     */
    public static String normalize(final String toNormalize) {
        // toNormalize = toNormalize.replaceAll("ä", "ae");
        // toNormalize = toNormalize.replaceAll("ö", "oe");
        // toNormalize = toNormalize.replaceAll("ü", "ue");
        // toNormalize = toNormalize.replaceAll("Ä", "AE");
        // toNormalize = toNormalize.replaceAll("Ö", "OE");
        // toNormalize = toNormalize.replaceAll("Ü", "UE");
        // toNormalize = toNormalize.replaceAll("ß", "ss");
        return toNormalize.replaceAll("[\\\\/:\\*?\"<>;,#%=@]", "_");
    }

    /**
     * Check if string is not null and not empty
     * 
     * @param string
     *            string to check
     * 
     * @return empty or not
     */
    public static boolean isNotEmpty(final String string) {
        return !Strings.isNullOrEmpty(string);
    }

    public static String convertStreamToString(InputStream is, String charset) throws OwsExceptionReport {
        try {
            Scanner scanner;
            if (isNotEmpty(charset)) {
                scanner = new Scanner(is, charset);
            } else {
                scanner = new Scanner(is);
            }
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (NoSuchElementException nsee) {
            throw new NoApplicableCodeException().causedBy(nsee).withMessage(
                    "Error while reading content of HTTP request: %s", nsee.getMessage());
        }
        return "";
    }

    public static String convertStreamToString(InputStream is) throws OwsExceptionReport {
        return convertStreamToString(is, null);
    }
    
    public static boolean checkIfCharacterOccursXTimesIgnoreCase(final String toCheck, final char character, int count) {
        String lowerCase = toCheck.toLowerCase();
        String upperCase = toCheck.toUpperCase();
        for (int i = 0; i < toCheck.length(); i++) {
            if (character == lowerCase.charAt(i) || character == upperCase.charAt(i)) {
                count--;
            }
        }
        return count == 0;
    }
    
    public static boolean checkIfCharacterOccursXTimes(final String toCheck, final char character, int count) {
        for (int i = 0; i < toCheck.length(); i++) {
            if (character == toCheck.charAt(i)) {
                count--;
            }
        }
        return count == 0;
    }

    private StringHelper() {
    }
    
    public static List<String> splitToList(String string) {
        return splitToList(string, Constants.COMMA_STRING);
    }

    public static List<String> splitToList(String string, String separator) {
        ArrayList<String> stringList = Lists.newArrayList();
        if (StringHelper.isNotEmpty(string)) {
            for (String s : string.split(separator)) {
                if (s != null && !s.trim().isEmpty()) {
                    stringList.add(s.trim());
                }
            }
        }
        return stringList;
    }
    
    public static Set<String> splitToSet(String stringToSplit, String separator) {
        return Sets.newTreeSet(splitToList(stringToSplit, separator));
    }
    
    public static Set<String> splitToSet(String stringToSplit) {
        return splitToSet(stringToSplit, Constants.COMMA_STRING);
    }

    public static String[] splitToArray(String stringToSplit, String separator) {
        List<String> splitToList = splitToList(stringToSplit, separator);
        return splitToList.toArray(new String[splitToList.size()]);
    }
    
    public static String[] splitToArray(String stringToSplit) {
        return splitToArray(stringToSplit, Constants.COMMA_STRING);
    }
}

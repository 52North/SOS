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
package org.n52.sos.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Joiner;
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
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer join(final CharSequence sep, final StringBuffer buff, final Iterable<?> src) {
        final Iterator<?> it = src.iterator();
        if (it.hasNext()) {
            buff.append(it.next());
        }
        while (it.hasNext()) {
            buff.append(sep).append(it.next());
        }
        return buff;
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String join(final CharSequence sep, final Iterable<?> src) {
        return join(sep, new StringBuffer(), src).toString();
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer join(final CharSequence sep, final StringBuffer buff, final Object... src) {
        return join(sep, buff, Arrays.asList(src));
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String join(final CharSequence sep, final Object... src) {
        return join(sep, Arrays.asList(src));
    }

    /**
     * Joins iterable content as given to passed StringBuffer
     * 
     * @param buff
     *            StringBuffer to add iterable content
     * @param src
     *            Iterable to join
     * 
     * @return StringBuffer with joined iterable content
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer concat(final StringBuffer buff, final Iterable<?> src) {

        final Iterator<?> it = src.iterator();
        while (it.hasNext()) {
            buff.append(it.next());
        }
        return buff;
    }

    /**
     * Joins iterable content as given to a single String
     * 
     * @param src
     *            Iterable to join
     * 
     * @return String with joined iterable content
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String concat(final Iterable<?> src) {
        return concat(new StringBuffer(), src).toString();
    }

    /**
     * Joins objects as given to passed StringBuffer
     * 
     * @param buff
     *            StringBuffer to add Objects
     * @param src
     *            Objects to join
     * 
     * @return StringBuffer with joined Objects
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer concat(final StringBuffer buff, final Object... src) {
        try {
            return Joiner.on("").appendTo(buff, src);
        } catch (IOException ex) {
        }
        return buff;
    }

    /**
     * Joins objects as given to a single String
     * 
     * @param src
     *            Objects to join
     * 
     * @return Joined String
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String concat(final Object... src) {
        return Joiner.on("").join(src);
    }

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

    /**
     * Check if string is null or empty
     * 
     * @param string
     *            string to check
     * @return <tt>true</tt>, if the string is null or empty
     * @deprecated use {@link Strings#isNullOrEmpty(java.lang.String) }
     */
    @Deprecated
    public static boolean isNullOrEmpty(final String string) {
        return Strings.isNullOrEmpty(string);
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

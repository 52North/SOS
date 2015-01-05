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
package org.n52.sos.inspire;

import java.util.Locale;

/**
 * Enum for European ISO6392B three character languages
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public enum InspireEuLanguageISO6392B {
    BUL("bul"), 
    CZE("cze"), 
    DAN("dan"), 
    DUT("dut"), 
    ENG("eng"), 
    EST("est"), 
    FIN("fin"), 
    FRE("fre"), 
    GER("ger"), 
    GRE("gre"), 
    HUN("hun"), 
    GLE("gle"), 
    ITA("ita"), 
    LAV("lav"), 
    LIT("lit"), 
    MLT("mlt"), 
    POL("pol"), 
    POR("por"), 
    RUM("rum"), 
    SLO("slo"), 
    SLV("slv"), 
    SPA("spa"), 
    SWE("swe");

    private final String value;

    /**
     * constructor
     * 
     * @param v the three character language string
     */
    InspireEuLanguageISO6392B(String v) {
        value = v;
    }

    /**
     * Get the value, three character language string
     * 
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Get {@link InspireEuLanguageISO6392B} for string value
     * 
     * @param v
     *            the string value to get {@link InspireEuLanguageISO6392B} for
     * @return {@link InspireEuLanguageISO6392B} of string value
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    public static InspireEuLanguageISO6392B fromValue(String v) {
        for (InspireEuLanguageISO6392B c : InspireEuLanguageISO6392B.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * Get {@link InspireEuLanguageISO6392B} for {@link InspireLanguageISO6392B}
     * 
     * @param v
     *            {@link InspireLanguageISO6392B} to get
     *            {@link InspireEuLanguageISO6392B} for
     * @return {@link InspireEuLanguageISO6392B} of
     *         {@link InspireLanguageISO6392B}
     * @throws IllegalArgumentException
     *             if the {@link InspireLanguageISO6392B} is invalid
     */
    public static InspireEuLanguageISO6392B fromValue(InspireLanguageISO6392B v) {
        for (InspireEuLanguageISO6392B c : InspireEuLanguageISO6392B.values()) {
            if (c.value.equals(v.value())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.value());
    }

    /**
     * Get {@link InspireEuLanguageISO6392B} for {@link Locale}
     * 
     * @param v
     *            {@link Locale} to get {@link InspireEuLanguageISO6392B} for
     * @return {@link InspireEuLanguageISO6392B} of {@link Locale}
     * @throws IllegalArgumentException
     *             if the {@link Locale} is invalid
     */
    public static InspireEuLanguageISO6392B fromValue(Locale v) {
        for (InspireEuLanguageISO6392B c : InspireEuLanguageISO6392B.values()) {
            if (c.value.equals(v.getISO3Country())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.getISO3Country());
    }
}

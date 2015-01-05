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

/**
 * Default constants interface with constants used by other constant interfaces
 * or classes
 * 
 * @since 4.0.0
 * 
 */
public interface Constants {

    int EPSG_WGS84_3D = 4979;

    int EPSG_WGS84 = 4326;
    
    String DEFAULT_ENCODING = "UTF-8";

    // String constants
    String EMPTY_STRING = "";
    
    String SPACE_STRING = " ";

    String BLANK_STRING = SPACE_STRING;
    
    String SEMICOLON_STRING = ";";

    String COLON_STRING = ":";
    
    String DOUBLE_COLON_STRING = COLON_STRING + COLON_STRING;

    String DOT_STRING = ".";

    String COMMA_STRING = ",";

    String COMMA_SPACE_STRING = ", ";

    String CSV_BLOCK_SEPARATOR = COMMA_STRING;

    String CSV_TOKEN_SEPARATOR = "@@";

    String AMPERSAND_STRING = "&";

    String EQUAL_SIGN_STRING = "=";

    String QUERSTIONMARK_STRING = "?";

    String SLASH_STRING = "/";

    String BACKSLASH_STRING = "\\";

    String LINE_SEPARATOR_STRING = "\n";

    String DASH_STRING = "-";

    String UNDERSCORE_STRING = "_";

    String NUMBER_SIGN_STRING = "#";

    String OPEN_BRACE_STRING = "(";

    String CLOSE_BRACE_STRING = ")";

    String PERCENT_STRING = "%";

    String DOLLAR_STRING = "$";

    String MINUS_STRING = "-";

    String PLUS_STRING = "+";
    
    String LESS_THAN_SIGN_STRING = "<";
    
    String GREATER_THAN_SIGN_STRING = ">";
    
    String INVERTED_COMMA_STRING = "'";

    // char constants
    char SPACE_CHAR = ' ';
    
    char BLANK_CHAR = SPACE_CHAR;

    char SEMICOLON_CHAR = ';';

    char COLON_CHAR = ':';

    char DOT_CHAR = '.';

    char COMMA_CHAR = ',';

    char AMPERSAND_CHAR = '&';

    char EQUAL_SIGN_CHAR = '=';

    char QUERSTIONMARK_CHAR = '?';

    char SLASH_CHAR = '/';

    char BACKSLASH_CHAR = '\\';

    char LINE_SEPARATOR_CHAR = '\n';

    char DASH_CHAR = '-';

    char UNDERSCORE_CHAR = '_';

    char NUMBER_SIGN_CHAR = '#';

    char OPEN_BRACE_CHAR = '(';

    char CLOSE_BRACE_CHAR = ')';

    char PERCENT_CHAR = '%';

    char DOLLAR_CHAR = '$';

    char MINUS_CHAR = '-';

    char PLUS_CHAR = '+';
    
    char INVERTED_COMMA_CHAR = '\'';

    int INT_0 = 0;

    int INT_1 = 1;

    int INT_2 = 2;

    int INT_3 = 3;

    int INT_4 = 4;

    String URN = "urn";

    String HTTP = "http";

    int HASH_CODE_3 = 3;

    int HASH_CODE_5 = 5;

    int HASH_CODE_7 = 7;

    int HASH_CODE_13 = 13;

    int HASH_CODE_17 = 17;

    int HASH_CODE_19 = 19;

    int HASH_CODE_23 = 23;

    int HASH_CODE_29 = 29;

    int HASH_CODE_31 = 31;

    int HASH_CODE_37 = 37;

    int HASH_CODE_41 = 41;

    int HASH_CODE_43 = 43;

    int HASH_CODE_47 = 47;

}

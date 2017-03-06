package org.n52.schetland.uvf;

import org.n52.sos.util.http.MediaType;

public interface UVFConstants {

    MediaType CONTENT_TYPE_UVF = new MediaType("application", "uvf");

    /**
     * Time format to be used in UVF encoded data: <code>yyMMddhhmm</code>,
     * e.g. <code>7001011230</code> is 01.01.1970 12:30 UTC
     */
    String TIME_FORMAT = "yyMMddhhmm";
    
    /**
     * The identifiers length is limited to 15 characters following UVF spec for lines 2, 3
     */
    int MAX_IDENTIFIER_LENGTH = 15;

    /**
     * The maximum length of a value string is limited to 10 characters. Hence, the values are shortened,
     * e.g. <code>52.1234567890</code> will be cut to <code>52.1234567</code>
     */
    int MAX_VALUE_LENGTH = 10;

    /**
     * No data values MUST be encoded with <code>-777</code> in the UVF format.
     */
    public String NO_DATA_STRING = "-777";

}
 

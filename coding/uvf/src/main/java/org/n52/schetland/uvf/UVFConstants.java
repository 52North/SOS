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

}
 

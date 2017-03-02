package org.n52.schetland.uvf;

import org.n52.sos.util.http.MediaType;

public interface UVFConstants {

    MediaType CONTENT_TYPE_UVF = new MediaType("application", "uvf");
    String TIME_FORMAT = "yyMMddhhmm";
    
    /**
     * The identifiers length is limited to 15 characters following UVF spec for lines 2, 3
     */
    int MAX_IDENTIFIER_LENGTH = 15;

}
 

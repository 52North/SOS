package org.n52.sos.util;

import java.net.URI;

import com.google.common.base.Function;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class Functions {

    private static final Function<String, URI> STRING_TO_URI
            = new Function<String, URI>() {

                @Override
                public URI apply(String input) {
                    return URI.create(input);
                }
            };

    public static Function<String, URI> stringToURI() {
        return STRING_TO_URI;
    }

}

package org.n52.sos.ds.hibernate.util;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class PropertyPath {
    private PropertyPath() {
    }

    public static String of(String first, String... path) {
        return Stream.concat(Stream.of(first), Arrays.stream(path))
                .filter(Objects::nonNull)
                .collect(joining("."));
    }

}

package org.n52.sos.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class CacheValidation {

    private CacheValidation() {
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a {@code IllegalArgumentException} if value is <= 0.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is <= 0
     */
    public static void greaterZero(String name, Integer value)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(value, name) <= 0) {
            throw new IllegalArgumentException(name + " may not less or equal 0!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a {@code IllegalArgumentException} if value is empty.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is empty
     */
    public static void notNullOrEmpty(String name, String value)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(value, name).isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value within is null.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException if value == null or value contains null
     */
    public static void noNullValues(String name, Collection<?> value)
            throws NullPointerException {
        if (Objects.requireNonNull(value, name).stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException(name + " may not contain null elements!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value within is null or empty.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value == null or value contains null
     * @throws IllegalArgumentException if any value is empty
     */
    public static void noNullOrEmptyValues(String name, Collection<String> value)
            throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(value, name).forEach(o -> {
            if (o == null) {
                throw new NullPointerException(name + " may not contain null elements!");
            }
            if (o.isEmpty()) {
                throw new IllegalArgumentException(name + " may not contain empty elements!");
            }
        });
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any key or value within is null.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException if value == null or value contains null values
     */
    public static void noNullValues(String name, Map<?, ?> value) throws NullPointerException {
        if (Objects.requireNonNull(value, name).entrySet().stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException(name + " may not contain null elements!");
        }
    }
}

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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Represents a tri-state object: can be nil (with an optional message), absent
 * (or null) or present. {@link #isNil()}, {@link #isAbsent()} and
 * {@link #isPresent()} are mutually exclusive {@code true}.
 *
 * @author Christian Autermann
 * @param <T> the instance type
 */
public abstract class Nillable<T> {

    public Nillable() {
    }

    /**
     * Checks if this {@code Nillable} is {@code nil}.
     *
     * @return whether or not this instance is {@code nil}
     *
     * @see #isPresent()
     * @see #isAbsent()
     *
     */
    public abstract boolean isNil();

    /**
     * Checks if this {@code Nillable} is present.
     *
     * @return whether or not this instance is present
     *
     * @see #isNil()
     * @see #isAbsent()
     */
    public abstract boolean isPresent();

    /**
     * Checks if this {@code Nillable} is {@code null}.
     *
     * @return whether or not this instance is {@code null}
     *
     * @see #isNil()
     * @see #isPresent()
     */
    public abstract boolean isAbsent();

    /**
     * Transforms this {@code Nillable} to the target type.
     *
     * @param <X> the target type
     * @param fun the transformation function
     *
     * @return the {@code Nillable}
     */
    public abstract <X> Nillable<X> transform(Function<? super T, X> fun);

    /**
     * Checks if this {@code Nillable} is {@code null}.
     *
     * @return whether or not this instance is {@code null}
     *
     * @see #isAbsent()
     */
    public boolean isNull() {
        return isAbsent();
    }

    /**
     * Checks if this this instance is not absent.
     *
     * @return whether this instance is present or nil
     */
    public boolean isPresentOrNil() {
        return !isAbsent();
    }

    /**
     * Returns the object if present.
     *
     * @return the object
     *
     * @throws UnsupportedOperationException if this instance is not present
     * @see #isPresent()
     */
    public abstract T get();

    /**
     * Checks if this {@code Nillable} is nil and has a reason.
     *
     * @return whether this instance has a nil reason or not
     */
    public boolean hasReason() {
        return isNil() && getNilReason().isPresent();
    }

    /**
     * Returns the optional reason for this object being nil.
     *
     * @return the nil reason
     *
     * @throws UnsupportedOperationException if this instance is not nil
     * @see #isNil()
     */
    public abstract Optional<String> getNilReason();

    /**
     * Creates a new {@code Nillable} from a present instance.
     *
     * @param <T> the type
     * @param t   the object
     *
     * @throws NullPointerException if {@code t} is {@code null}
     * @return the present {@code Nillable}
     */
    public static <T> Nillable<T> present(T t) {
        return new Present<>(t);
    }

    /**
     * Creates a {@code Nillable} for a absent instance.
     *
     * @param <T> the type
     *
     * @return the absent {@code Nillable}
     */
    public static <T> Nillable<T> absent() {
        return Absent.INSTANCE.cast();
    }

    /**
     * Creates a new {@code Nillable} that is {@code nil} because of optionally
     * supplied reason.
     *
     * @param <T>    the type
     * @param reason the reason (may be {@code null})
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> nil(String reason) {
        return new Nil(reason).cast();
    }

    /**
     * Creates a new {@code Nillable} that is {@code nil}.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> nil() {
        return nil(null);
    }

    /**
     * Creates a new {@code Nillable}, that is nil because it is inapplicable.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> inapplicable() {
        return Nil.INAPPLICABLE.cast();
    }

    /**
     * Creates a new {@code Nillable}, that is nil because it is missing.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> missing() {
        return Nil.MISSING.cast();
    }

    /**
     * Creates a new {@code Nillable}, that is nil because it is a template
     * value.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> template() {
        return Nil.TEMPLATE.cast();
    }

    /**
     * Creates a new {@code Nillable}, that is nil because it is unknown.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> unknown() {
        return Nil.UNKNOWN.cast();
    }

    /**
     * Creates a new {@code Nillable}, that is nil because it is withheld.
     *
     * @param <T> the type
     *
     * @return the nil {@code Nillable}
     */
    public static <T> Nillable<T> withheld() {
        return Nil.WITHHELD.cast();
    }

    /**
     * Creates a new {@code Nillable} that is either present (if {@code obj} is
     * not {@code null}), or absent.
     *
     * @param <T> the type
     * @param obj the object (may be {@code null})
     *
     * @return the {@code Nillable}
     */
    public static <T> Nillable<T> of(T obj) {
        return of(obj, null);
    }

    /**
     * Creates a new {@code Nillable} that is either present (if {@code obj} is
     * not {@code null}), nil (if {@code reason} is not {@code null}) or absent.
     *
     * @param <T>    the type
     * @param obj    the object (may be {@code null})
     * @param reason the nil reason (may be {@code null})
     *
     * @return the {@code Nillable}
     */
    public static <T> Nillable<T> of(T obj, String reason) {
        if (obj == null) {
            if (reason == null) {
                return absent();
            }
            return nil(reason);
        }
        return present(obj);
    }

    private static class Present<T> extends Nillable<T> {
        private final T obj;

        Present(T obj) {
            this.obj = checkNotNull(obj);
        }

        @Override
        public Optional<String> getNilReason() {
            throw new UnsupportedOperationException("instance is present");
        }

        @Override
        public boolean isNil() {
            return false;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public T get() {
            return obj;
        }

        @Override
        public <X> Nillable<X> transform(Function<? super T, X> fun) {
            return new Present<>(fun.apply(get()));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.get());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Present && Objects
                   .equal(get(), ((Present) obj).get());
        }

        @Override
        public String toString() {
            return get().toString();
        }
    }

    private static class Nil extends Nillable<Object> {
        private static final Nil INAPPLICABLE = new Nil("inapplicable");
        private static final Nil MISSING = new Nil("missing");
        private static final Nil TEMPLATE = new Nil("template");
        private static final Nil UNKNOWN = new Nil("unknown");
        private static final Nil WITHHELD = new Nil("withheld");
        private final Optional<String> reason;

        Nil(String reason) {
            this.reason = Optional.fromNullable(reason);
        }

        @Override
        public Optional<String> getNilReason() {
            return reason;
        }

        @Override
        public boolean isNil() {
            return true;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("instance is nil");
        }

        @Override
        public <X> Nillable<X> transform(Function<? super Object, X> fun) {
            return this.cast();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.getNilReason());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Nil && Objects
                   .equal(getNilReason(), ((Nil) obj).getNilReason());
        }

        @SuppressWarnings("unchecked")
        <T> Nillable<T> cast() {
            return (Nillable<T>) this;
        }

        @Override
        public String toString() {
            return "Nillable.nil(\"" + getNilReason().orNull() + "\")";
        }
    }

    private static class Absent extends Nillable<Object> {
        private static final Absent INSTANCE = new Absent();

        @Override
        public boolean hasReason() {
            return false;
        }

        @Override
        public Optional<String> getNilReason() {
            throw new UnsupportedOperationException("instance is absent");
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("instance is absent");
        }

        @Override
        public boolean isNil() {
            return false;
        }

        @Override
        public boolean isAbsent() {
            return true;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public <X> Nillable<X> transform(Function<? super Object, X> fun) {
            return this.cast();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Absent;
        }

        @SuppressWarnings("unchecked")
        <T> Nillable<T> cast() {
            return (Nillable<T>) this;
        }

        @Override
        public String toString() {
            return "Nillable.absent()";
        }
    }
}

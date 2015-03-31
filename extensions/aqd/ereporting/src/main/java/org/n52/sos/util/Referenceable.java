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

import java.net.URI;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public abstract class Referenceable<T> {

    public abstract Reference getReference();

    public abstract Nillable<T> getInstance();

    public abstract boolean isInstance();

    public abstract boolean isReference();

    public abstract boolean isAbsent();

    public abstract <X> Referenceable<X> transform(Function<T,X> fun);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public static <T> Referenceable<T> of(URI reference) {
        return new Ref(new Reference().setHref(reference)).cast();
    }

    public static <T> Referenceable<T> of(Reference reference) {
        return new Ref(reference).cast();
    }

    public static <T> Referenceable<T> of(T obj) {
        return of(Nillable.of(obj));
    }

    public static <T> Referenceable<T> of(Nillable<T> obj) {
        return new Instance<>(obj);
    }

    private static class Instance<T> extends Referenceable<T> {
        private final Nillable<T> obj;

        Instance(Nillable<T> obj) {
            this.obj = Preconditions.checkNotNull(obj);
        }

        @Override
        public Reference getReference() {
            throw new NullPointerException();
        }

        @Override
        public Nillable<T> getInstance() {
            return obj;
        }

        @Override
        public boolean isInstance() {
            return true;
        }

        @Override
        public boolean isReference() {
            return false;
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(getInstance());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Instance && Objects
                   .equal(this.getInstance(), ((Instance) obj).getInstance());
        }

        @Override
        public boolean isAbsent() {
            return getInstance().isAbsent();
        }

        @Override
        public String toString() {
            return getInstance().toString();
        }

        @Override
        public <X> Referenceable<X> transform(Function<T, X> fun) {
            return Referenceable.of(getInstance().transform(fun));
        }

    }

    private static class Ref extends Referenceable<Object> {
        private final Reference reference;

        Ref(Reference reference) {
            this.reference = reference;
        }

        @Override
        public Reference getReference() {
            return reference;
        }

        @Override
        public Nillable<Object> getInstance() {
            throw new NullPointerException();
        }

        @Override
        public boolean isInstance() {
            return false;
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getReference());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Ref && Objects
                   .equal(getReference(), ((Ref) obj).getReference());
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public String toString() {
            return getReference().toString();
        }

        @SuppressWarnings("unchecked")
        <T> Referenceable<T> cast() {
            return (Referenceable<T>) this;
        }

        @Override
        public <X> Referenceable<X> transform(Function<Object, X> fun) {
            return cast();
        }
    }
}

/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ScrollableIterable<T> implements Iterable<T>, Closeable {
    private final ScrollableResults results;

    private Iterator<T> iterator;

    public ScrollableIterable(ScrollableResults results) {
        this.results = results;
    }

    public static <T> ScrollableIterable<T> fromResults(ScrollableResults sr) {
        return new ScrollableIterable<>(sr);
    }

    public static <T> ScrollableIterable<T> fromCriteria(Criteria c) {
        return new ScrollableIterable<>(c.scroll());
    }

    @Override
    public Iterator<T> iterator() {
        if (iterator != null) {
            throw new IllegalStateException("this is a one time iterable");
        }
        iterator = new ScrollableIterator();
        return iterator;
    }

    @Override
    public void close() {
        results.close();
    }

    private class ScrollableIterator implements Iterator<T> {
        private Boolean hasNext;

        @Override
        public boolean hasNext() {
            if (hasNext == null) {
                // only proceed once
                hasNext = results.next();
            }
            return hasNext;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (hasNext != null && hasNext) {
                hasNext = null;
                return (T) results.get(0);
            } else {
                throw new NoSuchElementException();
            }

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.Iterator;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.google.common.collect.AbstractIterator;

/**
 * {@code Iterator}-Wrapper for {@link ScrollableResults}. This class implements {@link AutoCloseable} and will close
 * the {@code ScrollableResults} as well as the unterlying {@link Session}.
 *
 * @param <T> the iterators value type
 *
 * @author Christian Autermann
 */
public class ScrollableResultsIterator<T> extends AbstractIterator<T> implements Iterator<T>, AutoCloseable {
    private final ScrollableResults results;
    private final Session session;
    private final Function<ScrollableResults, T> extractor;

    /**
     * Create a new {@code ScrollableResultsIterator}.
     *
     * @param results   the results
     * @param session   the session
     * @param extractor the extractor
     */
    public ScrollableResultsIterator(ScrollableResults results, Session session,
                                     Function<ScrollableResults, T> extractor) {
        this.results = results;
        this.session = session;
        this.extractor = extractor;
    }

    @Override
    public void close() throws HibernateException {
        try {
            this.results.close();
        } finally {
            this.session.close();
        }
    }

    @Override
    protected T computeNext() {
        if (this.results.next()) {
            return this.extractor.apply(this.results);
        } else {
            return endOfData();
        }
    }

}

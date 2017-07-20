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

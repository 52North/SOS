package org.n52.sos.ds.hibernate.util;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import org.n52.janmayen.AbstractThrowingIterator;
import org.n52.janmayen.ThrowingIterator;
import org.n52.janmayen.function.ThrowingFunction;

/**
 * {@code ThrowingIterator}-Wrapper for {@link ScrollableResults}. This class implements {@link AutoCloseable} and will
 * close the {@code ScrollableResults} as well as the unterlying {@link Session}.
 *
 * @param <T> the iterators value type
 * @param <X> the exception type
 *
 * @author Christian Autermann
 *
 */
public class ThrowingScrollableResultsIterator<T, X extends Exception> extends AbstractThrowingIterator<T, X>
        implements ThrowingIterator<T, X>, AutoCloseable {
    
    private final ScrollableResults results;
    private final Session session;
    private final ThrowingFunction<ScrollableResults, T, X> extractor;

    /**
     * Create a new {@code ThrowingScrollableResultsIterator}.
     *
     * @param results   the results
     * @param session   the session
     * @param extractor the extractor
     */
    public ThrowingScrollableResultsIterator(ScrollableResults results, Session session,
                                             ThrowingFunction<ScrollableResults, T, X> extractor) {
        this.results = results;
        this.session = session;
        this.extractor = extractor;
    }

    @Override
    public void close() throws HibernateException {
        if (this.session.isOpen()) {
            this.session.close();
        }
    }

    @Override
    protected T computeNext() throws X {
        if (this.results.next()) {
            return this.extractor.apply(this.results);
        } else {
            close();
            return endOfData();
        }
    }

}
